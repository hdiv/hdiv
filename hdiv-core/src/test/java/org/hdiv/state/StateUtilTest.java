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
package org.hdiv.state;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.context.RequestContextHolder;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.exception.HDIVException;
import org.hdiv.state.scope.StateScopeType;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.util.Method;

/**
 * Unit tests for the <code>org.hdiv.state.StateUtil</code> class.
 * 
 */
public class StateUtilTest extends AbstractHDIVTestCase {

	private DataComposerFactory dataComposerFactory;

	private StateUtil stateUtil;

	@Override
	protected void onSetUp() throws Exception {

		dataComposerFactory = getApplicationContext().getBean(DataComposerFactory.class);
		stateUtil = getApplicationContext().getBean(StateUtil.class);
	}

	public void testRestore() {

		RequestContextHolder context = getRequestContext();
		IDataComposer dataComposer = dataComposerFactory.newInstance(context);
		context.setDataComposer(dataComposer);

		dataComposer.startPage();
		dataComposer.beginRequest(Method.GET, "test.do");
		dataComposer.compose("parameter1", "2", false);
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		assertNotNull(stateId);

		IState restored = stateUtil.restoreState(context, stateId);

		assertNotNull(restored);
		assertEquals(restored.getAction(), "test.do");
		assertEquals(restored.getParameter("parameter1").getValues().get(0), "2");
	}

	public void testRestoreIncorrectStateId() {

		RequestContextHolder context = getRequestContext();
		IDataComposer dataComposer = dataComposerFactory.newInstance(context);
		context.setDataComposer(dataComposer);

		try {
			IState restored = stateUtil.restoreState(context, "1111-");
			assertNull(restored);
			fail();
		}
		catch (HDIVException e) {
			assertTrue(true);

		}
	}

	public void testLongLivingApp() {

		RequestContextHolder context = getRequestContext();
		IDataComposer dataComposer = dataComposerFactory.newInstance(context);
		context.setDataComposer(dataComposer);

		dataComposer.startPage();
		dataComposer.startScope(StateScopeType.APP);
		dataComposer.beginRequest(Method.GET, "test.do");
		dataComposer.compose("parameter1", "2", false);
		String stateId = dataComposer.endRequest();
		dataComposer.endScope();
		dataComposer.endPage();

		assertNotNull(stateId);
		assertTrue(stateId.startsWith("A-"));

		IState restored = stateUtil.restoreState(context, stateId);

		assertNotNull(restored);
		assertEquals(restored.getAction(), "test.do");
		assertEquals(restored.getParameter("parameter1").getValues().get(0), "2");
	}

	public void testLongLivingUser() {

		RequestContextHolder context = getRequestContext();
		IDataComposer dataComposer = dataComposerFactory.newInstance(context);
		context.setDataComposer(dataComposer);

		dataComposer.startPage();
		dataComposer.startScope(StateScopeType.USER_SESSION);
		dataComposer.beginRequest(Method.GET, "test.do");
		dataComposer.compose("parameter1", "2", false);
		String stateId = dataComposer.endRequest();
		dataComposer.endScope();
		dataComposer.endPage();

		assertNotNull(stateId);
		assertTrue(stateId.startsWith("U-"));

		IState restored = stateUtil.restoreState(context, stateId);

		assertNotNull(restored);
		assertEquals(restored.getAction(), "test.do");
		assertEquals(restored.getParameter("parameter1").getValues().get(0), "2");
	}

	public void testInvalidateSession() {

		RequestContextHolder context = getRequestContext();
		IDataComposer dataComposer = dataComposerFactory.newInstance(context);
		context.setDataComposer(dataComposer);

		dataComposer.startPage();
		dataComposer.beginRequest(Method.GET, "test.do");
		String params = "param1=val1&param2=val2";
		String processedParams = dataComposer.composeParams(params, Method.GET, Constants.ENCODING_UTF_8);
		assertEquals("param1=0&param2=0", processedParams);
		String stateId = dataComposer.endRequest();

		// Invalidate the session in the middle of the request
		context.getRequest().getSession().invalidate();

		dataComposer.endPage();
		assertNotNull(stateId);

		try {
			stateUtil.restoreState(context, stateId);
		}
		catch (HDIVException e) {
			assertEquals(HDIVErrorCodes.INVALID_PAGE_ID, e.getMessage());
			return;
		}

		fail();
	}

}