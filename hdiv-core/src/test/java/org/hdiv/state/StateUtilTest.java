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
package org.hdiv.state;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.context.RequestContext;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.exception.HDIVException;
import org.hdiv.util.HDIVUtil;

/**
 * Unit tests for the <code>org.hdiv.state.StateUtil</code> class.
 * 
 */
public class StateUtilTest extends AbstractHDIVTestCase {

	private DataComposerFactory dataComposerFactory;

	private StateUtil stateUtil;

	protected void onSetUp() throws Exception {

		this.dataComposerFactory = this.getApplicationContext().getBean(DataComposerFactory.class);
		this.stateUtil = this.getApplicationContext().getBean(StateUtil.class);
	}

	public void testRestore() {

		HttpServletRequest request = this.getMockRequest();
		RequestContext context = this.getRequestContext();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		dataComposer.startPage();
		dataComposer.beginRequest("GET", "test.do");
		dataComposer.compose("parameter1", "2", false);
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		assertNotNull(stateId);

		IState restored = this.stateUtil.restoreState(context, stateId);

		assertNotNull(restored);
		assertEquals(restored.getAction(), "test.do");
		assertEquals(restored.getParameter("parameter1").getValues().get(0), "2");
	}

	public void testRestoreIncorrectStateId() {

		HttpServletRequest request = this.getMockRequest();
		RequestContext context = this.getRequestContext();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		try {
			IState restored = this.stateUtil.restoreState(context, "1111-");
			assertNull(restored);
			fail();
		}
		catch (HDIVException e) {
			assertTrue(true);

		}
	}

	public void testIsMemoryStrategy() {

		HttpServletRequest request = this.getMockRequest();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);

		HDIVUtil.setDataComposer(dataComposer, request);

		// memory strategy in conf and bad formatted stateId
		boolean result = this.stateUtil.isMemoryStrategy("1111");
		assertTrue(result);
	}

	public void testLongLivingApp() {

		HttpServletRequest request = this.getMockRequest();
		RequestContext context = this.getRequestContext();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		dataComposer.startPage();
		dataComposer.startScope("app");
		dataComposer.beginRequest("GET", "test.do");
		dataComposer.compose("parameter1", "2", false);
		String stateId = dataComposer.endRequest();
		dataComposer.endScope();
		dataComposer.endPage();

		assertNotNull(stateId);
		assertTrue(stateId.startsWith("A-"));

		IState restored = this.stateUtil.restoreState(context, stateId);

		assertNotNull(restored);
		assertEquals(restored.getAction(), "test.do");
		assertEquals(restored.getParameter("parameter1").getValues().get(0), "2");
	}

	public void testLongLivingUser() {

		HttpServletRequest request = this.getMockRequest();
		RequestContext context = this.getRequestContext();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		dataComposer.startPage();
		dataComposer.startScope("user-session");
		dataComposer.beginRequest("GET", "test.do");
		dataComposer.compose("parameter1", "2", false);
		String stateId = dataComposer.endRequest();
		dataComposer.endScope();
		dataComposer.endPage();

		assertNotNull(stateId);
		assertTrue(stateId.startsWith("U-"));

		IState restored = this.stateUtil.restoreState(context, stateId);

		assertNotNull(restored);
		assertEquals(restored.getAction(), "test.do");
		assertEquals(restored.getParameter("parameter1").getValues().get(0), "2");
	}
}