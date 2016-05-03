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
package org.hdiv.state.scope;

import org.hdiv.context.RequestContext;
import org.hdiv.state.IState;
import org.hdiv.util.Constants;

/**
 * Common code for {@link StateScope} implementation.
 *
 * @since 2.1.7
 */
public abstract class AbstractStateScope implements StateScope {

	private final String preffix;

	private final StateScopeType type;

	protected AbstractStateScope(final StateScopeType type) {
		preffix = type.getPrefix();
		this.type = type;
	}

	public String addState(final RequestContext context, final IState state, final String token) {

		ScopedStateCache cache = getStateCache(context);
		if (cache == null) {
			cache = new ScopedStateCache();
		}

		String stateId = cache.addState(state, token);

		setStateCache(context, cache);

		return new StringBuilder().append(preffix).append(Constants.STATE_ID_SEPARATOR).append(stateId).toString();
	}

	public IState restoreState(final RequestContext context, final int stateId) {

		ScopedStateCache cache = getStateCache(context);
		return cache == null ? null : cache.getState(stateId);
	}

	public String getStateToken(final RequestContext context, final int stateId) {

		ScopedStateCache cache = getStateCache(context);
		return cache == null ? null : cache.getStateToken(stateId);
	}

	public boolean isScopeState(final String stateId) {
		return stateId.charAt(preffix.length()) == Constants.STATE_ID_SEPARATOR && stateId.startsWith(preffix);
	}

	public final String getScopePrefix() {
		return preffix;
	}

	public StateScopeType getScopeType() {
		return type;
	}

	protected abstract ScopedStateCache getStateCache(RequestContext context);

	protected abstract void setStateCache(RequestContext context, ScopedStateCache cache);

}
