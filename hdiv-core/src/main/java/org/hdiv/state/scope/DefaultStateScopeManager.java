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

import java.util.List;

/**
 * Default implementation of {@link StateScopeManager}.
 *
 * @since 2.1.7
 */
public class DefaultStateScopeManager implements StateScopeManager {

	/**
	 * Available {@link StateScope} implementations.
	 */
	private final StateScope[] stateScopes;

	public DefaultStateScopeManager(final List<StateScope> stateScopes) {
		this.stateScopes = stateScopes.toArray(new StateScope[stateScopes.size()]);
	}

	public StateScope getStateScope(final String stateId) {
		for (int i = 0; i < stateScopes.length; i++) {
			if (stateScopes[i].isScopeState(stateId)) {
				return stateScopes[i];
			}
		}
		return null;
	}

	public StateScope getStateScopeByName(final String scopeName) {
		for (int i = 0; i < stateScopes.length; i++) {
			if (stateScopes[i].getScopeName().equals(scopeName)) {
				return stateScopes[i];
			}
		}
		return null;
	}
}