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

/**
 * Main interface for scoped states management.
 * 
 * @since 2.1.7
 */
public interface StateScopeManager {

	/**
	 * Obtain the correct {@link StateScope} based on a state identifier.
	 * 
	 * @param stateId State identifier
	 * @return the corresponding {@link StateScope} or null
	 */
	StateScope getStateScope(String stateId);

	/**
	 * Obtain the correct {@link StateScope} based on a scope name.
	 * 
	 * @param scopeName The name of the scope
	 * @return the corresponding {@link StateScope} or null
	 */
	StateScope getStateScope(StateScopeType scopeName);
}
