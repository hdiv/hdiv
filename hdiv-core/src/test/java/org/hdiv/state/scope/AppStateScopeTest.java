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

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.state.IParameter;
import org.hdiv.state.IState;
import org.hdiv.state.Parameter;
import org.hdiv.state.State;

public class AppStateScopeTest extends AbstractHDIVTestCase {

	private AppStateScope stateScope;

	protected void onSetUp() throws Exception {

		this.stateScope = this.getApplicationContext().getBean(AppStateScope.class);
	}

	public void testConf() {

		String scopeName = stateScope.getScopeName();
		assertEquals("app", scopeName);

		String scopePrefix = stateScope.getScopePrefix();
		assertEquals("A", scopePrefix);

		assertTrue(stateScope.isScopeState("A-111-11111"));
	}

	public void testAddState() {

		IState state = new State(0);
		state.setAction("/action");

		this.stateScope.addState(state, "token");

		IState state2 = this.stateScope.restoreState(0);

		assertEquals(state, state2);
	}

	public void testAddSameActionState() {

		IState state = new State(0);
		state.setAction("/action");
		IParameter param = new Parameter("uno", "value", false, null, false);
		state.addParameter(param);

		String id = this.stateScope.addState(state, "token");

		IState state2 = new State(1);
		state2.setAction("/action");
		IParameter param2 = new Parameter("uno", "value", false, null, false);
		state2.addParameter(param2);

		String id2 = this.stateScope.addState(state2, "token");

		assertEquals(id, id2);
	}
}
