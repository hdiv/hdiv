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
package org.hdiv.session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.state.IPage;
import org.hdiv.state.IState;
import org.hdiv.state.Page;
import org.hdiv.state.State;

/**
 * Unit tests for the <code>org.hdiv.session.StateCache</code> class.
 * 
 * @author Gorka Vicente
 */
public class StateCacheTest extends AbstractHDIVTestCase {

	private static Log log = LogFactory.getLog(StateCacheTest.class);

	protected void onSetUp() throws Exception {
	}

	public void testAddPage() {

		// cache's maximum size is defined using the Spring factory.
		IStateCache cache = this.getApplicationContext().getBean(IStateCache.class);

		IPage page1 = new Page();
		IPage page2 = new Page();
		IPage page3 = new Page();

		IState state1 = new State(0);
		IState state2 = new State(1);
		IState state3 = new State(2);

		page1.addState(state1);
		page1.setName("page1");
		cache.addPage("1");

		page2.addState(state2);
		page2.setName("page2");
		cache.addPage("2");

		page3.addState(state3);
		page3.setName("page3");
		cache.addPage("3");

		log.info("cache:" + cache.toString());
	}

}
