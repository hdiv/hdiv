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

import org.hdiv.util.HDIVUtil;

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

	public String getScopeName() {
		return this.scopeType.getName();
	}

	public String getScopePrefix() {
		return this.scopeType.getPrefix();
	}

	public ScopedStateCache getStateCache() {
		ScopedStateCache cache = (ScopedStateCache) this.getHttpSession().getAttribute(USER_STATE_CACHE_ATTR);
		return cache;
	}

	public void setStateCache(ScopedStateCache cache) {
		this.getHttpSession().setAttribute(USER_STATE_CACHE_ATTR, cache);
	}

	/**
	 * Obtain {@link HttpSession} instance for ThreadLocal
	 * 
	 * @return HttpSession instance
	 */
	protected HttpSession getHttpSession() {
		return HDIVUtil.getHttpSession();
	}

}
