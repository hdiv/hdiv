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

import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.hdiv.context.RequestContextHolder;
import org.hdiv.exception.HDIVException;
import org.hdiv.idGenerator.PageIdGenerator;
import org.hdiv.state.IPage;
import org.hdiv.state.IState;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVErrorCodes;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.util.Assert;

/**
 * Facade to access to attributes in {@link HttpSession}.
 * 
 * @author Roberto Velasco
 */
public class SessionHDIV implements ISession, BeanFactoryAware {

	/**
	 * The root interface for accessing a Spring bean container.
	 * 
	 * @see org.springframework.beans.factory.BeanFactory
	 */
	private BeanFactory beanFactory;

	/**
	 * The pageIdGeneratorName
	 */
	private String pageIdGeneratorName = Constants.PAGE_ID_GENERATOR_NAME;

	protected final HTTPSessionCache cache = new HTTPSessionCache();

	/**
	 * Obtains from the user session the page identifier for the current request.
	 * 
	 * @param context Context holder for request-specific state.
	 * @return Returns the pageId.
	 */
	public final UUID getPageId(final RequestContextHolder context) {

		SessionModel session = context.getSession();

		PageIdGenerator pageIdGenerator = (PageIdGenerator) session.getAttribute(pageIdGeneratorName);
		if (pageIdGenerator == null) {
			pageIdGenerator = beanFactory.getBean(PageIdGenerator.class);
		}
		if (pageIdGenerator == null) {
			throw new HDIVException("session.nopageidgenerator");
		}

		UUID id = pageIdGenerator.getNextPageId();

		// PageId must be greater than 0
		if (id == null) {
			throw new HDIVException("Incorrect PageId generated [" + id + "].");
		}

		session.setAttribute(pageIdGeneratorName, pageIdGenerator);

		return id;

	}

	/**
	 * Returns the page with id <code>pageId</code>.
	 * 
	 * @param context Context holder for request-specific state.
	 * @param pageId page id
	 * @return Returns the page with id <code>pageId</code>.
	 * @since HDIV 2.0.4
	 */
	public IPage getPage(final RequestContextHolder context, final UUID pageId) {
		try {
			return cache.findPage(new SimpleCacheKey(context, pageId));
		}
		catch (final IllegalStateException e) {
			throw new HDIVException(HDIVErrorCodes.INVALID_PAGE_ID, e);
		}
	}

	/**
	 * It adds a new page to the user session.
	 * 
	 * @param context Context holder for request-specific state.
	 * @param page Page with all the information about states
	 */
	public void addPage(final RequestContextHolder context, final IPage page) {
		addPageToSession(context, page, false);
	}

	/**
	 * It adds a partial page to the user session.
	 * 
	 * @param context Context holder for request-specific state.
	 * @param page Page with all the information about states
	 */
	public void addPartialPage(final RequestContextHolder context, final IPage page) {
		addPageToSession(context, page, true);
	}

	/**
	 * Obtains the state identifier <code>stateId</code> related to the page identifier <code>pageId</code>.
	 * 
	 * @param context Context holder for request-specific state.
	 * @return State identifier <code>stateId</code> throws HDIVException If the state doesn't exist a new HDIV exception is thrown.
	 */
	public IState getState(final RequestContextHolder context, final UUID pageId, final int stateId) {
		try {
			return getPage(context, pageId).getState(stateId);
		}
		catch (final Exception e) {
			throw new HDIVException(HDIVErrorCodes.INVALID_PAGE_ID, e);
		}
	}

	/**
	 * Internal method to add a new IPage instance to {@link HttpSession}
	 * 
	 * @param context {@link RequestContextHolder} instance
	 * @param page IPage instance
	 * @param isPartial If is partial page
	 * 
	 * @since HDIV 2.1.5
	 */
	protected void addPageToSession(final RequestContextHolder context, final IPage page, final boolean isPartial) {
		cache.insertPage(new SimpleCacheKey(context, page.getId()), page);
	}

	public boolean removePage(final RequestContextHolder context, final UUID pageId) {
		Assert.notNull(context);

		return cache.removePage(new SimpleCacheKey(context, pageId));
	}

	/**
	 * Callback that supplies the owning factory to a bean instance. Invoked after population of normal bean properties but before an init
	 * callback like InitializingBean's afterPropertiesSet or a custom init-method.
	 * 
	 * @param beanFactory owning BeanFactory (may not be null). The bean can immediately call methods on the factory.
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		cache.setBeanFactory(beanFactory);
	}

	public String getAttribute(final RequestContextHolder context, final String name) {
		Assert.notNull(context);
		return getAttribute(context.getSession(), name);
	}

	public String getAttribute(final SessionModel context, final String name) {
		Assert.notNull(name);
		return (String) context.getAttribute(name);
	}

	@SuppressWarnings("unchecked")
	public <T> T getAttribute(final RequestContextHolder context, final String name, final Class<T> requiredType) {
		Assert.notNull(context);
		Assert.notNull(name);
		Assert.notNull(requiredType);

		Object result = context.getSession().getAttribute(name);
		if (result == null) {
			return null;
		}
		else if (requiredType.isInstance(result)) {
			return (T) result;
		}
		else {
			throw new IllegalArgumentException(
					"Attibute with name '" + name + "' is not of required type " + requiredType.getCanonicalName());
		}
	}

	public void setAttribute(final RequestContextHolder context, final String name, final Object value) {
		Assert.notNull(context);
		Assert.notNull(name);

		context.getSession().setAttribute(name, value);
	}

	public void removeAttribute(final RequestContextHolder context, final String name) {
		Assert.notNull(context);
		Assert.notNull(name);

		context.getSession().removeAttribute(name);
	}

	/**
	 * @param pageIdGeneratorName The pageIdGeneratorName to set.
	 */
	public void setPageIdGeneratorName(final String pageIdGeneratorName) {
		this.pageIdGeneratorName = pageIdGeneratorName;
	}

	public void removeEndedPages(final RequestContextHolder context, final String conversationId) {
		cache.removeEndedPages(context, conversationId);
	}
}