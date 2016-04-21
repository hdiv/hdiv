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
package org.hdiv.session;

import java.util.List;

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

	private static final Log log = LogFactory.getLog(StateCacheTest.class);

	@Override
	protected void onSetUp() throws Exception {
	}

	public void testAddPage() {

		// cache's maximum size is defined using the Spring factory.
		IStateCache cache = this.getApplicationContext().getBean(IStateCache.class);

		IPage page1 = new Page(1);
		IPage page2 = new Page(2);
		IPage page3 = new Page(3);

		IState state1 = new State(0);
		IState state2 = new State(0);
		IState state3 = new State(0);

		int currentPageid = -1;

		page1.addState(state1);
		cache.addPage(1, currentPageid, false, false);

		page2.addState(state2);
		cache.addPage(2, currentPageid, false, false);

		page3.addState(state3);
		cache.addPage(3, currentPageid, false, false);

		log.info("cache:" + cache.toString());

		List<Integer> ids = cache.getPageIds();
		assertEquals(3, ids.size());
		assertEquals((Integer) 1, ids.get(0));
		assertEquals((Integer) 2, ids.get(1));
		assertEquals((Integer) 3, ids.get(2));
	}

	public void testPageReflesh() {

		// cache's maximum size is defined using the Spring factory.
		IStateCache cache = this.getApplicationContext().getBean(IStateCache.class);

		IPage page1 = new Page(1);
		IPage page2 = new Page(2);
		IPage page3 = new Page(3);

		IState state1 = new State(0);
		IState state2 = new State(0);
		IState state3 = new State(0);

		int currentPageid = -1;

		page1.addState(state1);
		cache.addPage(1, currentPageid, false, false);
		assertEquals(1, cache.getPageIds().size());

		page2.addState(state2);
		cache.addPage(2, currentPageid, false, false);
		assertEquals(2, cache.getPageIds().size());

		// Simulate a page refresh or F5
		currentPageid = 1;

		page3.addState(state3);
		cache.addPage(3, currentPageid, true, false);
		assertEquals(2, cache.getPageIds().size());// Size is 2 instead of 3

		List<Integer> ids = cache.getPageIds();
		assertEquals((Integer) 1, ids.get(0));
		assertEquals((Integer) 3, ids.get(1));
	}

}
