/**
 * Copyright 2005-2011 hdiv.org
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

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.cipher.ICipherHTTP;
import org.hdiv.cipher.Key;
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
	 * The keyName
	 */
	private String keyName = Constants.KEY_NAME;

	/**
	 * Obtains from the user session the page identifier where the current request or form is
	 * 
	 * @return Returns the pageId.
	 */
	public String getPageId() {

		HttpSession session = this.getHttpSession();

		PageIdGenerator pageIdGenerator = (PageIdGenerator) session.getAttribute(this.pageIdGeneratorName);
		if (pageIdGenerator == null) {
			throw new HDIVException("session.nopageidgenerator");
		}

		String id = pageIdGenerator.getNextPageId();
		session.setAttribute(this.pageIdGeneratorName, pageIdGenerator);

		return id;

	}

	/**
	 * Returns the page with id <code>pageId</code>.
	 * 
	 * @param pageId
	 *            page id
	 * @return Returns the page with id <code>pageId</code>.
	 * @since HDIV 2.0.4
	 */
	public IPage getPage(String pageId) {
		try {
			if (log.isDebugEnabled()) {
				log.debug("Getting page with id:" + pageId);
			}

			return (IPage) getHttpSession().getAttribute(pageId);

		} catch (IllegalStateException e) {
			throw new HDIVException(HDIVErrorCodes.PAGE_ID_INCORRECT, e);
		}
	}

	/**
	 * It adds a new page to the user session. To do this it adds a new page identifier to the cache and if it has
	 * reached the maximun size allowed, the oldest page is deleted from the session and from the cache itself.
	 * 
	 * @param pageId
	 *            Page identifier
	 * @param page
	 *            Page with all the information about states
	 */
	public void addPage(String pageId, IPage page) {

		HttpSession session = this.getHttpSession();

		IStateCache cache = this.getStateCache();

		page.setName(pageId);
		String removedPageId = cache.addPage(pageId);

		// if it returns a page identifier it is because the cache has reached
		// the maximun size and therefore we must delete the page which has been
		// stored for the longest time
		if (removedPageId != null) {
			session.removeAttribute(removedPageId);

			if (log.isDebugEnabled()) {
				log.debug("Deleted page with id:" + removedPageId);
			}
		}

		// we update page identifier cache in session
		session.setAttribute(this.cacheName, cache);

		// we add a new page in session
		session.setAttribute(page.getName(), page);
		if (log.isDebugEnabled()) {
			log.debug("Added page with id:" + pageId);
		}

	}

	/**
	 * Deletes from session the data related to the finished flows. This means a memory consumption optimization because
	 * useless objects of type <code>IPage</code> are deleted.
	 * 
	 * @param conversationId
	 *            finished flow identifier
	 * @since HDIV 2.0.3
	 */
	public void removeEndedPages(String conversationId) {

		HttpSession session = this.getHttpSession();

		IStateCache cache = this.getStateCache();
		if (log.isDebugEnabled()) {
			log.debug("Cache pages before finished pages are deleted:" + cache.toString());
		}

		List pageIds = cache.getPageIds();
		String pageId = null;
		IPage currentPage = null;

		for (int i = 0; i < pageIds.size(); i++) {

			pageId = (String) pageIds.get(i);
			currentPage = (IPage) session.getAttribute(pageId);
			if ((currentPage != null) && (currentPage.getFlowId() != null)) {

				String pageFlowId = currentPage.getFlowId();

				if (conversationId.equalsIgnoreCase(pageFlowId)) {

					session.removeAttribute(pageId);
					String removedId = (String) pageIds.remove(i);
					i--;

					if (removedId != null) {
						if (log.isDebugEnabled()) {
							log.debug("Page with id " + removedId + " have been removed");
						}
					}
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
	 * @return State identifier <code>stateId</code> throws HDIVException If the state doesn't exist a new HDIV
	 *         exception is thrown.
	 */
	public IState getState(String pageId, String stateId) {

		try {
			IPage currentPage = (IPage) getHttpSession().getAttribute(pageId);
			return currentPage.getState(stateId);

		} catch (Exception e) {
			throw new HDIVException(HDIVErrorCodes.PAGE_ID_INCORRECT, e);
		}
	}

	/**
	 * Obtains the hash of the state identifier <code>stateId</code> related to page identifier <code>pageId</code>.
	 * 
	 * @return Hash of the state identifier <code>stateId</code>
	 * @throws HDIVException
	 *             If the state doesn't exist a new HDIV exception is thrown.
	 */
	public String getStateHash(String pageId, String stateId) {

		try {
			IPage currentPage = (IPage) getHttpSession().getAttribute(pageId);
			return currentPage.getStateHash(stateId);

		} catch (Exception e) {
			throw new HDIVException(HDIVErrorCodes.PAGE_ID_INCORRECT, e);
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
	 * @return IStateCache instance
	 */
	protected IStateCache getStateCache() {

		HttpSession session = this.getHttpSession();
		IStateCache cache = (IStateCache) session.getAttribute(this.cacheName);
		if (cache == null) {
			cache = (IStateCache) this.beanFactory.getBean(IStateCache.class);
			session.setAttribute(this.cacheName, cache);
		}

		return cache;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.session.ISession#getEncryptCipher()
	 */
	public ICipherHTTP getEncryptCipher() {
		ICipherHTTP cipher = (ICipherHTTP) this.beanFactory.getBean(ICipherHTTP.class);
		if (cipher == null) {
			String errorMessage = HDIVUtil.getMessage("encrypt.message");
			throw new HDIVException(errorMessage);
		}
		return cipher;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.session.ISession#getDecryptCipher()
	 */
	public ICipherHTTP getDecryptCipher() {
		ICipherHTTP cipher = (ICipherHTTP) this.beanFactory.getBean(ICipherHTTP.class);
		if (cipher == null) {
			String errorMessage = HDIVUtil.getMessage("decrypt.message");
			throw new HDIVException(errorMessage);
		}
		return cipher;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.session.ISession#getCipherKey()
	 */
	public Key getCipherKey() {

		Key key = (Key) getHttpSession().getAttribute(this.keyName);
		if (key == null) {
			throw new HDIVException("Key not initialized on session");
		}
		return key;
	}

	/**
	 * Obtain {@link HttpSession} instance for ThreadLocal
	 * 
	 * @return HttpSession instance
	 */
	private HttpSession getHttpSession() {
		return HDIVUtil.getHttpSession();
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

	/**
	 * @param keyName
	 *            The keyName to set.
	 */
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

}