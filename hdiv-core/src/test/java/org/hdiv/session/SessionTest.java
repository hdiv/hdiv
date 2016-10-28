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

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.context.RequestContext;
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

	@Override
	protected void onSetUp() throws Exception {

		session = getApplicationContext().getBean(ISession.class);
	}

	public void testGetPageId() {

		RequestContext context = getRequestContext();

		int pageId = session.getPageId(context);

		assertTrue(pageId > 0);
	}

	public void testAddPage() {

		RequestContext context = getRequestContext();

		IPage page = new Page(20);

		IState state = new State(0);
		state.setAction("/action");
		IParameter param = new Parameter("name", "value", false, null, true);
		state.addParameter(param);
		page.addState(state);

		session.addPage(context, page);

	}

	public void testGetState() {

		RequestContext context = getRequestContext();

		IPage page = new Page(20);

		IState state = new State(0);
		state.setAction("/action");
		IParameter param = new Parameter("name", "value", false, null, true);
		state.addParameter(param);
		page.addState(state);

		session.addPage(context, page);

		// Restore state
		IState restored = session.getState(context, 20, 0);

		assertNotNull(restored);
		assertEquals(state, restored);
	}

	public void testGetPage() {

		RequestContext context = getRequestContext();

		IPage page = new Page(20);

		IState state = new State(0);
		state.setAction("/action");
		IParameter param = new Parameter("name", "value", false, null, true);
		state.addParameter(param);
		page.addState(state);

		session.addPage(context, page);

		// Restore page
		IPage restored = session.getPage(context, 20);

		assertNotNull(restored);
		assertEquals(page, restored);
	}

	public void testPageRefresh() {

		RequestContext context = getRequestContext();

		// First page
		IPage page = new Page(20);

		IState state = new State(0);
		state.setAction("/action");
		page.addState(state);

		session.addPage(context, page);

		IStateCache cache = (IStateCache) getMockRequest().getSession().getAttribute(Constants.STATE_CACHE_NAME);
		List<Integer> ids = cache.getPageIds();
		assertEquals(1, ids.size());

		// Second page
		page = new Page(21);

		state = new State(0);
		state.setAction("/action");
		page.addState(state);
		page.setParentStateId("14-0-E3E5BA9F9AC0DEA35BBE14189510600E");

		session.addPage(context, page);

		cache = (IStateCache) getMockRequest().getSession().getAttribute(Constants.STATE_CACHE_NAME);
		ids = cache.getPageIds();
		assertEquals(2, ids.size());

		// Simulate Page refresh
		HDIVUtil.setCurrentPageId(20, getMockRequest());

		// Third page
		page = new Page(22);

		// Same parent state id because a refresh has been performed
		page.setParentStateId("14-0-E3E5BA9F9AC0DEA35BBE14189510600E");

		state = new State(0);
		state.setAction("/action");
		page.addState(state);

		session.addPage(context, page);

		cache = (IStateCache) getMockRequest().getSession().getAttribute(Constants.STATE_CACHE_NAME);
		ids = cache.getPageIds();
		assertEquals(2, ids.size());

	}

	public void testAttributes() {

		RequestContext context = super.getRequestContext();
		String name = "attr";
		String value = "value";

		String result = session.getAttribute(context, name);
		assertNull(result);

		session.setAttribute(context, name, value);
		result = session.getAttribute(context, name);
		assertNotNull(result);
		assertEquals(value, result);

		session.removeAttribute(context, name);
		result = session.getAttribute(context, name);
		assertNull(result);

	}

	public void testTypedAttributes() {

		RequestContext context = super.getRequestContext();
		String name = "attr";

		Test1Bean result = session.getAttribute(context, name, Test1Bean.class);
		assertNull(result);

		Test1Bean bean = new Test1Bean();
		session.setAttribute(context, name, bean);
		Test1Bean res = session.getAttribute(context, name, Test1Bean.class);
		assertNotNull(res);
		assertEquals(bean, res);

		try {
			session.getAttribute(context, name, Test2Bean.class);
			fail();
		}
		catch (IllegalArgumentException e) {
		}

	}

	public void testRemovePage() {

		RequestContext context = getRequestContext();

		IPage page = new Page(20);

		IState state = new State(0);
		state.setAction("/action");
		IParameter param = new Parameter("name", "value", false, null, true);
		state.addParameter(param);
		page.addState(state);

		session.addPage(context, page);

		boolean result = session.removePage(context, 0);
		assertEquals(false, result);

		result = session.removePage(context, 20);
		assertEquals(true, result);
	}

	class Test1Bean {
	}

	class Test2Bean {
	}

}
