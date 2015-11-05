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
package org.hdiv.session;

import java.util.List;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.state.IPage;
import org.hdiv.state.IParameter;
import org.hdiv.state.IState;
import org.hdiv.state.Page;
import org.hdiv.state.Parameter;
import org.hdiv.state.State;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;

public class SessionTest extends AbstractHDIVTestCase {

	private ISession session;

	protected void onSetUp() throws Exception {

		this.session = this.getApplicationContext().getBean(ISession.class);
	}

	public void testGetPageId() {

		int pageId = session.getPageId();

		assertTrue(pageId > 0);
	}

	public void testAddPage() {

		IPage page = new Page();
		page.setId(20);

		IState state = new State(0);
		state.setAction("/action");
		IParameter param = new Parameter("name", "value", false, null, true);
		state.addParameter(param);
		page.addState(state);

		session.addPage(20, page);

	}

	public void testGetState() {

		IPage page = new Page();
		page.setId(20);

		IState state = new State(0);
		state.setAction("/action");
		IParameter param = new Parameter("name", "value", false, null, true);
		state.addParameter(param);
		page.addState(state);

		session.addPage(20, page);

		// Restore state
		IState restored = session.getState(20, 0);

		assertNotNull(restored);
		assertEquals(state, restored);
	}

	public void testGetPage() {

		IPage page = new Page();
		page.setId(20);

		IState state = new State(0);
		state.setAction("/action");
		IParameter param = new Parameter("name", "value", false, null, true);
		state.addParameter(param);
		page.addState(state);

		session.addPage(20, page);

		// Restore page
		IPage restored = session.getPage(20);

		assertNotNull(restored);
		assertEquals(page, restored);
	}

	public void testPageRefresh() {

		// First page
		IPage page = new Page();
		page.setId(20);

		IState state = new State(0);
		state.setAction("/action");
		page.addState(state);

		session.addPage(20, page);

		IStateCache cache = (IStateCache) HDIVUtil.getHttpServletRequest().getSession()
				.getAttribute(Constants.STATE_CACHE_NAME);
		List<Integer> ids = cache.getPageIds();
		assertEquals(1, ids.size());

		// Second page
		page = new Page();
		page.setId(21);

		state = new State(0);
		state.setAction("/action");
		page.addState(state);
		page.setParentStateId("14-0-E3E5BA9F9AC0DEA35BBE14189510600E"); 

		session.addPage(21, page);

		cache = (IStateCache) HDIVUtil.getHttpServletRequest().getSession().getAttribute(Constants.STATE_CACHE_NAME);
		ids = cache.getPageIds();
		assertEquals(2, ids.size());

		// Simulate Page refresh
		HDIVUtil.setCurrentPageId(20, HDIVUtil.getHttpServletRequest());

		// Third page
		page = new Page();
		page.setId(22);
		
		// Same parent state id because a refresh has been performed
		page.setParentStateId("14-0-E3E5BA9F9AC0DEA35BBE14189510600E"); 

		state = new State(0);
		state.setAction("/action");
		page.addState(state);

		session.addPage(22, page);

		cache = (IStateCache) HDIVUtil.getHttpServletRequest().getSession().getAttribute(Constants.STATE_CACHE_NAME);
		ids = cache.getPageIds();
		assertEquals(2, ids.size());

	}

}
