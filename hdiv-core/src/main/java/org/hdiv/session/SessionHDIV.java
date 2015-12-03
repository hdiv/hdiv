/**
 * Copyright 2005-2015 hdiv.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hdiv.session;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.context.RequestContext;
import org.hdiv.exception.HDIVException;
import org.hdiv.idGenerator.PageIdGenerator;
import org.hdiv.state.IPage;
import org.hdiv.state.IState;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.util.HDIVUtil;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * A custom wrapper for http session request that returns a wrapped http session.
 * 
 * @author Roberto Velasco
 */
public class SessionHDIV implements ISession, BeanFactoryAware {

	/**
	 * Commons Logging instance.
	 */
	private static final Log log = LogFactory.getLog(SessionHDIV.class);

	/**
	 * The root interface for accessing a Spring bean container.
	 * 
	 * @see org.springframework.beans.factory.BeanFactory
	 */
	private BeanFactory beanFactory;

	/**
	 * The cacheName
	 */
	private String cacheName = Constants.STATE_CACHE_NAME;

	/**
	 * The pageIdGeneratorName
	 */
	private String pageIdGeneratorName = Constants.PAGE_ID_GENERATOR_NAME;

	/**
	 * Obtains from the user session the page identifier for the current request.
	 * 
	 * @param context
	 *            Context holder for request-specific state.
	 * @return Returns the pageId.
	 */
	public int getPageId(RequestContext context) {

		HttpSession session = context.getRequest().getSession();

		PageIdGenerator pageIdGenerator = (PageIdGenerator) session.getAttribute(this.pageIdGeneratorName);
		if (pageIdGenerator == null) {
			pageIdGenerator = this.beanFactory.getBean(PageIdGenerator.class);
		}
		if (pageIdGenerator == null) {
			throw new HDIVException("session.nopageidgenerator");
		}

		int id = pageIdGenerator.getNextPageId();

		// PageId must be greater than 0
		if (id <= 0) {
			throw new HDIVException("Incorrect PageId generated [" + id + "]. PageId must be greater than 0.");
		}

		session.setAttribute(this.pageIdGeneratorName, pageIdGenerator);

		return id;

	}

	/**
	 * Returns the page with id <code>pageId</code>.
	 * 
	 * @param context
	 *            Context holder for request-specific state.
	 * @param pageId
	 *            page id
	 * @return Returns the page with id <code>pageId</code>.
	 * @since HDIV 2.0.4
	 */
	public IPage getPage(RequestContext context, int pageId) {
		try {
			return this.getPageFromSession(context, pageId);

		} catch (IllegalStateException e) {
			throw new HDIVException(HDIVErrorCodes.PAGE_ID_INCORRECT, e);
		}
	}

	/**
	 * It adds a new page to the user session. To do this it adds a new page identifier to the cache and if it has
	 * reached the maximum size allowed, the oldest page is deleted from the session and from the cache itself.
	 * 
	 * @param context
	 *            Context holder for request-specific state.
	 * @param pageId
	 *            Page identifier
	 * @param newPage
	 *            Page with all the information about states
	 * @param isPartial
	 *            If is a partial page
	 */
	protected void addPage(RequestContext context, int pageId, IPage newPage, boolean isPartial) {

		HttpServletRequest request = context.getRequest();
		HttpSession session = request.getSession();

		boolean isRefreshRequest = false;
		boolean isAjaxRequest = false;

		IStateCache cache = this.getStateCache(session);

		// Get current request page identifier. Null if no state
		Integer currentPage = HDIVUtil.getCurrentPageId(request);

		Integer lastPageId = cache.getLastPageId();
		IPage lastPage = lastPageId == null ? null : this.getPage(context, lastPageId);

		// Check if is an refresh request. For example, to check if an user has performed a F5 request
		if (newPage != null && lastPage != null && newPage.getParentStateId() != null
				&& lastPage.getParentStateId() != null
				&& newPage.getParentStateId().equals(lastPage.getParentStateId())) {
			isRefreshRequest = true;
		}

		// Check if is an Ajax request.
		Object isAjaxRequestObject = request.getAttribute(Constants.AJAX_REQUEST);

		if (isAjaxRequestObject != null) {
			isAjaxRequest = (Boolean) isAjaxRequestObject;
		}

		Integer removedPageId = cache.addPage(pageId, currentPage, isRefreshRequest, isAjaxRequest);

		// if it returns a page identifier it is because the cache has reached
		// the maximum size and therefore we must delete the page which has been
		// stored for the longest time
		if (removedPageId != null) {

			this.removePageFromSession(context, removedPageId);
		}

		// we update page identifier cache in session
		this.saveStateCache(session, cache);

		// we add a new page in session
		this.addPageToSession(context, newPage, isPartial);

		// log cache content
		this.logCacheContent(context, cache);
	}

	/**
	 * It adds a new page to the user session.
	 * 
	 * @param context
	 *            Context holder for request-specific state.
	 * @param pageId
	 *            Page identifier
	 * @param page
	 *            Page with all the information about states
	 */
	public void addPage(RequestContext context, int pageId, IPage page) {
		this.addPage(context, pageId, page, false);
	}

	/**
	 * It adds a partial page to the user session.
	 * 
	 * @param context
	 *            Context holder for request-specific state.
	 * @param pageId
	 *            Page identifier
	 * @param page
	 *            Page with all the information about states
	 */
	public void addPartialPage(RequestContext context, int pageId, IPage page) {
		this.addPage(context, pageId, page, true);
	}

	/**
	 * Deletes from session the data related to the finished flows. This means a memory consumption optimization because
	 * useless objects of type <code>IPage</code> are deleted.
	 * 
	 * @param context
	 *            Context holder for request-specific state.
	 * @param conversationId
	 *            finished flow identifier
	 * @since HDIV 2.0.3
	 */
	public void removeEndedPages(RequestContext context, String conversationId) {

		HttpSession session = context.getRequest().getSession();

		IStateCache cache = this.getStateCache(session);
		if (log.isDebugEnabled()) {
			log.debug("Cache pages before finished pages are deleted:" + cache.toString());
		}

		List<Integer> pageIds = cache.getPageIds();

		for (int i = 0; i < pageIds.size(); i++) {

			Integer pageId = pageIds.get(i);
			IPage currentPage = this.getPageFromSession(context, pageId);
			if ((currentPage != null) && (currentPage.getFlowId() != null)) {

				String pageFlowId = currentPage.getFlowId();

				if (conversationId.equalsIgnoreCase(pageFlowId)) {

					this.removePageFromSession(context, pageId);
					pageIds.remove(i);
					i--;
				}
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("Cache pages after finished pages are deleted:" + cache.toString());
		}
	}

	/**
	 * Obtains the state identifier <code>stateId</code> related to the page identifier <code>pageId</code>.
	 * 
	 * @param context
	 *            Context holder for request-specific state.
	 * @return State identifier <code>stateId</code> throws HDIVException If the state doesn't exist a new HDIV
	 *         exception is thrown.
	 */
	public IState getState(RequestContext context, int pageId, int stateId) {

		try {
			IPage currentPage = this.getPage(context, pageId);
			return currentPage.getState(stateId);

		} catch (Exception e) {
			throw new HDIVException(HDIVErrorCodes.PAGE_ID_INCORRECT, e);
		}
	}

	/**
	 * Internal method to retrieve a IPage instance from {@link HttpSession}
	 * 
	 * @param context
	 *            {@link RequestContext} instance
	 * @param pageId
	 *            page id to retrieve from session
	 * @return IPage instance
	 * @since HDIV 2.1.5
	 */
	protected IPage getPageFromSession(RequestContext context, int pageId) {

		if (log.isDebugEnabled()) {
			log.debug("Getting page with id:" + pageId);
		}

		HttpSession session = context.getSession();
		return (IPage) session.getAttribute(pageId + "");
	}

	/**
	 * Internal method to add a new IPage instance to {@link HttpSession}
	 * 
	 * @param context
	 *            {@link RequestContext} instance
	 * @param page
	 *            IPage instance
	 * @param isPartial
	 *            If is partial page
	 * 
	 * @since HDIV 2.1.5
	 */
	protected void addPageToSession(RequestContext context, IPage page, boolean isPartial) {

		HttpSession session = context.getSession();
		session.setAttribute(page.getName(), page);

		if (log.isDebugEnabled()) {
			log.debug("Added new page with id:" + page.getId());
		}
	}

	/**
	 * Internal method to remove a IPage instance from {@link HttpSession}
	 * 
	 * @param context
	 *            {@link RequestContext} instance
	 * @param pageId
	 *            page id to remove from session
	 * @since HDIV 2.1.5
	 */
	protected void removePageFromSession(RequestContext context, int pageId) {

		HttpSession session = context.getSession();
		session.removeAttribute(pageId + "");

		if (log.isDebugEnabled()) {
			log.debug("Deleted page with id:" + pageId);
		}
	}

	/**
	 * Callback that supplies the owning factory to a bean instance. Invoked after population of normal bean properties
	 * but before an init callback like InitializingBean's afterPropertiesSet or a custom init-method.
	 * 
	 * @param beanFactory
	 *            owning BeanFactory (may not be null). The bean can immediately call methods on the factory.
	 */
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Create new or obtain existing state Cache instance.
	 * 
	 * @param session
	 *            {@link HttpSession} instance
	 * @return IStateCache instance
	 */
	protected IStateCache getStateCache(HttpSession session) {

		IStateCache cache = (IStateCache) session.getAttribute(this.cacheName);
		if (cache == null) {
			cache = this.createStateCacheInstance();
			session.setAttribute(this.cacheName, cache);
		}

		return cache;
	}

	/**
	 * Save state Cache instance.
	 * 
	 * @param session
	 *            {@link HttpSession} instance
	 * @param stateCache
	 *            {@link IStateCache} instance
	 * @since HDIV 2.1.6
	 */
	protected void saveStateCache(HttpSession session, IStateCache stateCache) {

		session.setAttribute(this.cacheName, stateCache);
	}

	/**
	 * Create new {@link IStateCache} instance.
	 * 
	 * @return {@link IStateCache} instance
	 * @since HDIV 2.1.6
	 */
	protected IStateCache createStateCacheInstance() {

		IStateCache cache = this.beanFactory.getBean(IStateCache.class);
		return cache;
	}

	/**
	 * Log cache content in the logger.
	 * 
	 * @param context
	 *            Context holder for request-specific state.
	 * @param cache
	 *            cache object
	 */
	protected void logCacheContent(RequestContext context, IStateCache cache) {
		if (log.isTraceEnabled()) {
			synchronized (cache) {
				List<Integer> ids = cache.getPageIds();
				StringBuffer sb = new StringBuffer();
				for (Integer id : ids) {
					IPage page = this.getPage(context, id);
					String parentPage = null;
					if (page != null) {
						parentPage = page.getParentStateId();
					}
					if (parentPage != null) {
						parentPage = parentPage.substring(0, parentPage.indexOf("-"));
					}
					sb.append("[").append(id).append(" (").append(parentPage).append(")] ");
				}
				log.trace("Cache content [" + sb.toString() + "]");
			}
		}
	}

	/**
	 * @param cacheName
	 *            The cacheName to set.
	 */
	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	/**
	 * @param pageIdGeneratorName
	 *            The pageIdGeneratorName to set.
	 */
	public void setPageIdGeneratorName(String pageIdGeneratorName) {
		this.pageIdGeneratorName = pageIdGeneratorName;
	}

}