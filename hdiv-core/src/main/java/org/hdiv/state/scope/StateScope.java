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

/**
 * <p>
 * Scoped state manager of a specific type.
 * </p>
 * <p>
 * Manages state addition, restore and automatic deletion.
 * </p>
 * 
 * @since 2.1.7
 */
public interface StateScope {

	String addState(RequestContext context, IState state, String token);

	IState restoreState(RequestContext context, int stateId);

	String getStateToken(RequestContext context, int stateId);

	String getScopeName();

	boolean isScopeState(String stateId);
}
