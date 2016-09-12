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

import javax.servlet.http.HttpSession;

import org.hdiv.context.RequestContext;
import org.hdiv.state.IPage;
import org.hdiv.state.IState;

/**
 * Facade to access to attributes in {@link HttpSession}.
 * 
 * @author Roberto Velasco
 */
public interface ISession {

	/**
	 * It adds a new page to the user session. To do this it adds a new page identifier to the cache and if it has reached the maximum size
	 * allowed, the oldest page is deleted from the session and from the cache itself.
	 *
	 * @param context Context holder for request-specific state.
	 * @param page Page with all the information about states
	 */
	void addPage(RequestContext context, IPage page);

	/**
	 * It adds a partial page to the user session.
	 *
	 * @param context Context holder for request-specific state.
	 * @param page Page with all the information about states
	 * @since HDIV 2.1.13
	 */
	void addPartialPage(RequestContext context, IPage page);

	/**
	 * Obtains the state identifier <code>stateId</code> related to the page identifier <code>pageId</code>.
	 *
	 * @param context Context holder for request-specific state.
	 * @param pageId Page identifier
	 * @param stateId State identifier
	 *
	 * @return State identifier <code>stateId</code> throws HDIVException If the state doesn't exist a new HDIV exception is thrown.
	 */
	IState getState(RequestContext context, int pageId, int stateId);

	/**
	 * Obtains from the user session the page identifier for the current request.
	 *
	 * @param context Context holder for request-specific state.
	 * @return Returns the pageId.
	 */
	int getPageId(RequestContext context);

	/**
	 * Returns the page with id <code>pageId</code>.
	 *
	 * @param context Context holder for request-specific state.
	 * @param pageId page id
	 * @return Returns the page with id <code>pageId</code>.
	 * @since HDIV 2.0.4
	 */
	IPage getPage(RequestContext context, int pageId);

	/**
	 * Get an attribute from session.
	 * @param context Context holder for request-specific state.
	 * @param name Attribute name.
	 * @return Attribute value or null if the attribute doesn't exist.
	 * @since HDIV 3.0.1
	 */
	String getAttribute(RequestContext context, String name);

	/**
	 * Get an attribute from session.
	 * @param context Context holder for request-specific state.
	 * @param name Attribute name.
	 * @param requiredType Type of the attribute.
	 * @param <T> Return type
	 * @return Attribute value or null if the attribute doesn't exist.
	 * @since HDIV 3.0.1
	 */
	<T> T getAttribute(RequestContext context, String name, Class<T> requiredType);

	/**
	 * Set an attribute value in session.
	 * @param context Context holder for request-specific state.
	 * @param name Attribute name.
	 * @param value Attribute value.
	 * @since HDIV 3.0.1
	 */
	void setAttribute(RequestContext context, String name, Object value);

	/**
	 * Remove an attribute from session.
	 * @param context Context holder for request-specific state.
	 * @param name Attribute name.
	 * @since HDIV 3.0.1
	 */
	void removeAttribute(RequestContext context, String name);
}