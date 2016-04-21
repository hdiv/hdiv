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

import org.hdiv.context.RequestContext;
import org.hdiv.state.IPage;
import org.hdiv.state.IState;

/**
 * A custom wrapper for Http session request that returns a wrapped Http session.
 *
 * @author Roberto Velasco
 */
public interface ISession {

	/**
	 * It adds a new page to the user session. To do this it adds a new page identifier to the cache and if it has
	 * reached the maximum size allowed, the oldest page is deleted from the session and from the cache itself.
	 *
	 * @param context Context holder for request-specific state.
	 * @param page Page with all the information about states
	 */
	void addPage(RequestContext context, IPage page);

	@Deprecated
	void addPage(RequestContext context, int pageId, IPage page);

	/**
	 * It adds a partial page to the user session.
	 *
	 * @param context Context holder for request-specific state.
	 * @param page Page with all the information about states
	 * @since HDIV 2.1.13
	 */
	void addPartialPage(RequestContext context, IPage page);

	@Deprecated
	void addPartialPage(RequestContext context, int pageId, IPage page);

	/**
	 * Deletes from session the data related to the finished flows. This means a memory consumption optimization because
	 * useless objects of type <code>IPage</code> are deleted.
	 *
	 * @param context Context holder for request-specific state.
	 * @param conversationId finished flow identifier
	 * @since HDIV 2.0.3
	 */
	void removeEndedPages(RequestContext context, String conversationId);

	/**
	 * Obtains the state identifier <code>stateId</code> related to the page identifier <code>pageId</code>.
	 *
	 * @param context Context holder for request-specific state.
	 * @param pageId Page identifier
	 * @param stateId State identifier
	 *
	 * @return State identifier <code>stateId</code> throws HDIVException If the state doesn't exist a new HDIV
	 * exception is thrown.
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

}