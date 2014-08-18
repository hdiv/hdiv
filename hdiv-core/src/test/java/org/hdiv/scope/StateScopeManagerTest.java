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

import org.hdiv.AbstractHDIVTestCase;

public class StateScopeManagerTest extends AbstractHDIVTestCase {

	private StateScopeManager stateScopeManager;

	protected void onSetUp() throws Exception {

		this.stateScopeManager = this.getApplicationContext().getBean(StateScopeManager.class);
	}

	public void testScope() {

		StateScope scope = this.stateScopeManager.getStateScopeByName("app");
		assertEquals("app", scope.getScopeName());

		scope = this.stateScopeManager.getStateScopeByName("user");
		assertEquals("user", scope.getScopeName());

		scope = this.stateScopeManager.getStateScopeByName("");
		assertNull(scope);
	}

}
