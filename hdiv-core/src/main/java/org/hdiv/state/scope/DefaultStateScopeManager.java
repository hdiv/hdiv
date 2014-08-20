/**
 * Copyright 2005-2013 hdiv.org
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
	private List<StateScope> stateScopes;

	public DefaultStateScopeManager(List<StateScope> stateScopes) {
		this.stateScopes = stateScopes;
	}

	public StateScope getStateScope(String stateId) {

		for (StateScope stateScope : stateScopes) {
			if (stateScope.isScopeState(stateId)) {
				return stateScope;
			}
		}
		return null;
	}

	public StateScope getStateScopeByName(String scopeName) {

		for (StateScope stateScope : stateScopes) {
			if (stateScope.getScopeName().equals(scopeName)) {
				return stateScope;
			}
		}
		return null;
	}
}