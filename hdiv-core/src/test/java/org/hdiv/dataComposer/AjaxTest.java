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
package org.hdiv.dataComposer;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.context.RequestContext;
import org.hdiv.state.IParameter;
import org.hdiv.state.IState;
import org.hdiv.state.StateUtil;
import org.hdiv.util.HDIVStateUtils;
import org.hdiv.util.HDIVUtil;
import org.hdiv.util.Method;
import org.springframework.mock.web.MockHttpServletRequest;

public class AjaxTest extends AbstractHDIVTestCase {

	private DataComposerFactory dataComposerFactory;

	private StateUtil stateUtil;

	@Override
	protected void onSetUp() throws Exception {

		dataComposerFactory = getApplicationContext().getBean(DataComposerFactory.class);
		stateUtil = getApplicationContext().getBean(StateUtil.class);
	}

	public void testAjaxWithoutReusingExistingPage() {

		getConfig().setReuseExistingPageInAjaxRequest(false);

		MockHttpServletRequest request = getMockRequest();
		RequestContext context = getRequestContext();
		IDataComposer dataComposer = dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		dataComposer.startPage();
		dataComposer.beginRequest(Method.GET, "test.do");
		dataComposer.compose("parameter1", "1", false);
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		assertNotNull(stateId);
		request.addParameter(getConfig().getStateParameterName(), stateId);

		request.addHeader("x-requested-with", "XMLHttpRequest");

		// DataComposer1
		IDataComposer dataComposer1 = dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer1, request);

		dataComposer1.beginRequest(Method.GET, "test.do");
		// Add new parameter
		dataComposer1.compose("parameter2", "2", false);
		String stateId1 = dataComposer1.endRequest();
		dataComposer1.endPage();

		assertEquals(getPageId(stateId), getPageId(stateId1) - 1);

		// DataComposer2
		IDataComposer dataComposer2 = dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer2, request);

		dataComposer2.beginRequest(Method.GET, "test.do");
		// Add new parameter
		dataComposer2.compose("parameter3", "3", false);
		String stateId2 = dataComposer2.endRequest();
		dataComposer2.endPage();

		assertEquals(getPageId(stateId), getPageId(stateId2) - 2);

		int sId1 = getStateId(stateId1);
		int sId2 = getStateId(stateId2);

		assertEquals(sId1, sId2);
		// Restore state
		IState state = stateUtil.restoreState(context, stateId);
		IParameter param = state.getParameter("parameter1");
		String val = param.getValues().get(0);
		assertEquals("1", val);

		state = stateUtil.restoreState(context, stateId1);
		param = state.getParameter("parameter2");
		val = param.getValues().get(0);
		assertEquals("2", val);

		state = stateUtil.restoreState(context, stateId2);
		param = state.getParameter("parameter3");
		val = param.getValues().get(0);
		assertEquals("3", val);
	}

	public void testAjax() {

		getConfig().setReuseExistingPageInAjaxRequest(true);

		MockHttpServletRequest request = getMockRequest();
		RequestContext context = getRequestContext();
		IDataComposer dataComposer = dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		dataComposer.startPage();
		dataComposer.beginRequest(Method.GET, "test.do");
		dataComposer.compose("parameter1", "1", false);
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		assertNotNull(stateId);
		request.addParameter(getConfig().getStateParameterName(), stateId);

		request.addHeader("x-requested-with", "XMLHttpRequest");

		// DataComposer1
		IDataComposer dataComposer1 = dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer1, request);

		dataComposer1.beginRequest(Method.GET, "test.do");
		// Add new parameter
		dataComposer1.compose("parameter2", "2", false);
		String stateId1 = dataComposer1.endRequest();
		dataComposer1.endPage();

		assertEquals(getPageId(stateId), getPageId(stateId1));

		// DataComposer2
		IDataComposer dataComposer2 = dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer2, request);

		dataComposer2.beginRequest(Method.GET, "test.do");
		// Add new parameter
		dataComposer2.compose("parameter3", "3", false);
		String stateId2 = dataComposer2.endRequest();
		dataComposer2.endPage();

		assertEquals(getPageId(stateId), getPageId(stateId2));

		int sId1 = getStateId(stateId1);
		int sId2 = getStateId(stateId2);

		assertEquals(sId1 + 1, sId2);
		// Restore state
		IState state = stateUtil.restoreState(context, stateId);
		IParameter param = state.getParameter("parameter1");
		String val = param.getValues().get(0);
		assertEquals("1", val);

		state = stateUtil.restoreState(context, stateId1);
		param = state.getParameter("parameter2");
		val = param.getValues().get(0);
		assertEquals("2", val);

		state = stateUtil.restoreState(context, stateId2);
		param = state.getParameter("parameter3");
		val = param.getValues().get(0);
		assertEquals("3", val);
	}

	public void testConcurrentAjax() {

		getConfig().setReuseExistingPageInAjaxRequest(true);

		MockHttpServletRequest request = getMockRequest();
		RequestContext context = getRequestContext();
		IDataComposer dataComposer = dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		dataComposer.startPage();
		dataComposer.beginRequest(Method.POST, "test.do");
		dataComposer.compose("parameter1", "1", false);
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		assertNotNull(stateId);
		request.addParameter(getConfig().getStateParameterName(), stateId);

		request.addHeader("x-requested-with", "XMLHttpRequest");

		// Create two dataComposers concurrently
		IDataComposer dataComposer1 = dataComposerFactory.newInstance(request);
		IDataComposer dataComposer2 = dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer1, request);

		// DataComposer1
		dataComposer1.beginRequest(Method.GET, "test.do");
		// Add new parameter
		dataComposer1.compose("parameter2", "2", false);
		String stateId1 = dataComposer1.endRequest();
		dataComposer1.endPage();

		assertEquals(getPageId(stateId), getPageId(stateId1));

		// DataComposer2
		HDIVUtil.setDataComposer(dataComposer2, request);

		dataComposer2.beginRequest(Method.GET, "test.do");
		// Add new parameter
		dataComposer2.compose("parameter3", "3", false);
		String stateId2 = dataComposer2.endRequest();
		dataComposer2.endPage();

		assertEquals(getPageId(stateId), getPageId(stateId2));

		int sId1 = getStateId(stateId1);
		int sId2 = getStateId(stateId2);

		assertEquals(sId1 + 1, sId2);
		// Restore state
		IState state = stateUtil.restoreState(context, stateId);
		IParameter param = state.getParameter("parameter1");
		String val = param.getValues().get(0);
		assertEquals("1", val);

		state = stateUtil.restoreState(context, stateId1);
		param = state.getParameter("parameter2");
		val = param.getValues().get(0);
		assertEquals("2", val);

		state = stateUtil.restoreState(context, stateId2);
		param = state.getParameter("parameter3");
		val = param.getValues().get(0);
		assertEquals("3", val);
	}

	public int getPageId(final String stateId) {
		return HDIVStateUtils.getPageId(stateId);
	}

	public int getStateId(final String stateId) {
		return HDIVStateUtils.getStateId(stateId);
	}
}
