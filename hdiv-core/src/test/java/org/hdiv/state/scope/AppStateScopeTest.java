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

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.context.RequestContextHolder;
import org.hdiv.state.IParameter;
import org.hdiv.state.IState;
import org.hdiv.state.Parameter;
import org.hdiv.state.State;

public class AppStateScopeTest extends AbstractHDIVTestCase {

	private AppStateScope stateScope;

	@Override
	protected void onSetUp() throws Exception {

		stateScope = getApplicationContext().getBean(AppStateScope.class);
	}

	public void testConf() {

		String scopeName = stateScope.getScopeType().getName();
		assertEquals("app", scopeName);

		String scopePrefix = stateScope.getScopePrefix();
		assertEquals("A", scopePrefix);

		assertTrue(stateScope.isScopeState("A-111-11111"));
	}

	public void testAddState() {

		RequestContextHolder context = getRequestContext();

		IState state = new State(0);
		state.setAction("/action");

		stateScope.addState(context, state, "token");

		IState state2 = stateScope.restoreState(context, 0);

		assertEquals(state, state2);
	}

	public void testAddSameActionState() {

		RequestContextHolder context = getRequestContext();

		IState state = new State(0);
		state.setAction("/action");
		IParameter param = new Parameter("uno", "value", false, null, false);
		state.addParameter(param);

		String id = stateScope.addState(context, state, "token");

		IState state2 = new State(1);
		state2.setAction("/action");
		IParameter param2 = new Parameter("uno", "value", false, null, false);
		state2.addParameter(param2);

		String id2 = stateScope.addState(context, state2, "token");

		assertEquals(id, id2);
	}

	public void testInvalidStateId() {

		String scopeName = stateScope.getScopeType().getName();
		assertEquals("app", scopeName);

		String scopePrefix = stateScope.getScopePrefix();
		assertEquals("A", scopePrefix);

		assertFalse(stateScope.isScopeState("1"));
	}
}
