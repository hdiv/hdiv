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

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.state.IState;
import org.hdiv.state.State;

public class ScopedStateCacheTest extends AbstractHDIVTestCase {

	protected void onSetUp() throws Exception {
	}

	public void testSameState() {

		ScopedStateCache cache = new ScopedStateCache();

		State state = new State(0);
		state.setAction("/action");
		String token = "123456789";

		String stateId = cache.addState(state, token);
		assertNotNull(stateId);

		String id = stateId.substring(0, stateId.indexOf("-"));
		IState restored = cache.getState(Integer.parseInt(id));

		assertEquals(state, restored);

		// Restore non existent
		restored = cache.getState(99999);
		assertNull(restored);

	}

}
