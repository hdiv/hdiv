/**
 * Copyright 2005-2016 hdiv.org
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
import org.hdiv.state.IPage;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;
import org.springframework.beans.factory.BeanFactory;

/**
 * Session cache handler
 * @author Ander Ruiz
 *
 */
public class HTTPSessionCache {

	/**
	 * Commons Logging instance.
	 */
	private static final Log log = LogFactory.getLog(SessionHDIV.class);

	private BeanFactory beanFactory;

	/**
	 * Prefix for the key of the pages stored in session.
	 */
	private static final String PAGE_ID_KEY_PREFIX = "hdiv-page-";

	public void insertPage(final SimpleCacheKey key, final IPage newPage) {
		HttpSession session = key.getRequestContext().getSession();
		int pageId = newPage.getId();
		HttpServletRequest request = key.getRequestContext().getRequest();

		boolean isAjaxRequest = false;

		IStateCache cache = getStateCache(session);

		// Get current request page identifier. Null if no state
		Integer currentPage = HDIVUtil.getCurrentPageId(request);

		Integer lastPageId = cache.getLastPageId();
		IPage lastPage = lastPageId == null ? null : findPage(new SimpleCacheKey(key.getRequestContext(), lastPageId));

		boolean isRefreshRequest = lastPage != null && newPage.getParentStateId() != null && lastPage.getParentStateId() != null
				&& newPage.getParentStateId().equals(lastPage.getParentStateId());

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
			deletePage(session, removedPageId);
		}

		// we update page identifier cache in session
		saveStateCache(session, cache);

		session.setAttribute(PAGE_ID_KEY_PREFIX + newPage.getId(), newPage);

		if (log.isDebugEnabled()) {
			log.debug("Added new page with id:" + newPage.getId());
		}

		// log cache content
		logCacheContent(key.getRequestContext(), cache);
	}

	public IPage findPage(final SimpleCacheKey key) {
		if (log.isDebugEnabled()) {
			log.debug("Getting page with id:" + key.getPageId());
		}

		HttpSession session = key.getRequestContext().getSession();
		return (IPage) session.getAttribute(PAGE_ID_KEY_PREFIX + key.getPageId());
	}

	private void deletePage(final HttpSession session, final int pageId) {
		session.removeAttribute(PAGE_ID_KEY_PREFIX + pageId);

		if (log.isDebugEnabled()) {
			log.debug("Deleted page with id:" + pageId);
		}
	}

	void removeEndedPages(final RequestContext context, final String conversationId) {

		HttpSession session = context.getRequest().getSession();

		IStateCache cache = getStateCache(session);
		if (log.isDebugEnabled()) {
			log.debug("Cache pages before finished pages are deleted:" + cache.toString());
		}

		List<Integer> pageIds = cache.getPageIds();

		for (int i = 0; i < pageIds.size(); i++) {

			Integer pageId = pageIds.get(i);
			IPage currentPage = findPage(new SimpleCacheKey(context, pageId));
			if (currentPage != null && conversationId.equalsIgnoreCase(currentPage.getFlowId())) {

				deletePage(context.getSession(), pageId);
				pageIds.remove(i);
				i--;
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("Cache pages after finished pages are deleted:" + cache);
		}
	}

	/**
	 * Log cache content in the logger.
	 * 
	 * @param context Context holder for request-specific state.
	 * @param cache cache object
	 */
	protected void logCacheContent(final RequestContext context, final IStateCache cache) {
		if (log.isTraceEnabled()) {
			synchronized (cache) {
				List<Integer> ids = cache.getPageIds();
				StringBuilder sb = new StringBuilder();
				for (final Integer id : ids) {
					IPage page = findPage(new SimpleCacheKey(context, id));
					String parentPage = null;
					if (page != null) {
						parentPage = page.getParentStateId();
					}
					if (parentPage != null) {
						parentPage = parentPage.substring(0, parentPage.indexOf('-'));
					}
					sb.append('[').append(id).append(" (").append(parentPage).append(")] ");
				}
				log.trace("Cache content [" + sb.toString() + "]");
			}
		}
	}

	/**
	 * Create new or obtain existing state Cache instance.
	 * 
	 * @param session {@link HttpSession} instance
	 * @return IStateCache instance
	 */
	public IStateCache getStateCache(final HttpSession session) {

		IStateCache cache = (IStateCache) session.getAttribute(Constants.STATE_CACHE_NAME);
		if (cache == null) {
			cache = createStateCacheInstance();
			session.setAttribute(Constants.STATE_CACHE_NAME, cache);
		}

		return cache;
	}

	/**
	 * Save state Cache instance.
	 * 
	 * @param session {@link HttpSession} instance
	 * @param stateCache {@link IStateCache} instance
	 * @since HDIV 2.1.6
	 */
	protected void saveStateCache(final HttpSession session, final IStateCache stateCache) {

		session.setAttribute(Constants.STATE_CACHE_NAME, stateCache);
	}

	/**
	 * Create new {@link IStateCache} instance.
	 * 
	 * @return {@link IStateCache} instance
	 * @since HDIV 2.1.6
	 */
	protected IStateCache createStateCacheInstance() {
		return beanFactory.getBean(IStateCache.class);
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
