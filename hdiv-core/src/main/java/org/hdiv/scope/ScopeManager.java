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
package org.hdiv.scope;

/**
 * Main class for scoped states management.
 * 
 * @since 2.1.7
 */
public class ScopeManager {

	/**
	 * 'user' scoped state manager.
	 */
	private StateScope userStateScope;

	/**
	 * 'app' scoped state manager.
	 */
	private StateScope appStateScope;

	public StateScope getStateScope(String stateId) {

		if (this.userStateScope.isScopeState(stateId)) {
			return this.userStateScope;
		} else if (this.appStateScope.isScopeState(stateId)) {
			return this.appStateScope;
		} else {
			return null;
		}
	}

	public StateScope getStateScopeByName(String scopeName) {

		if (this.userStateScope.getScopeName().equals(scopeName)) {
			return this.userStateScope;
		} else if (this.appStateScope.getScopeName().equals(scopeName)) {
			return this.appStateScope;
		} else {
			return null;
		}
	}

	/**
	 * @param userStateScope
	 *            the userStateScope to set
	 */
	public void setUserStateScope(StateScope userStateScope) {
		this.userStateScope = userStateScope;
	}

	/**
	 * @param appStateScope
	 *            the appStateScope to set
	 */
	public void setAppStateScope(StateScope appStateScope) {
		this.appStateScope = appStateScope;
	}

}
