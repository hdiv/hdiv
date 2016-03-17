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
package org.hdiv.state.scope;

import javax.servlet.http.HttpSession;

import org.hdiv.context.RequestContext;
import org.hdiv.session.ISession;

/**
 * <p>
 * {@link StateScope} that stores states at user level.
 * </p>
 * <p>
 * States scoped to 'user' are stored at {@link HttpSession} and are shared by all the pages of the same user.
 * </p>
 * 
 * @since 2.1.7
 */
public class UserSessionStateScope extends AbstractStateScope {

	private static final String USER_STATE_CACHE_ATTR = ScopedStateCache.class.getCanonicalName();

	protected StateScopeType scopeType = StateScopeType.USER_SESSION;

	protected ISession session;

	public String getScopeName() {
		return this.scopeType.getName();
	}

	public String getScopePrefix() {
		return this.scopeType.getPrefix();
	}

	public ScopedStateCache getStateCache(RequestContext context) {
		ScopedStateCache cache = this.session.getAttribute(context, USER_STATE_CACHE_ATTR, ScopedStateCache.class);
		return cache;
	}

	public void setStateCache(RequestContext context, ScopedStateCache cache) {
		this.session.setAttribute(context, USER_STATE_CACHE_ATTR, cache);
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(ISession session) {
		this.session = session;
	}

}
