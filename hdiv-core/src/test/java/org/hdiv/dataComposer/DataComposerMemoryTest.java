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
package org.hdiv.dataComposer;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.session.ISession;
import org.hdiv.state.IPage;
import org.hdiv.state.IParameter;
import org.hdiv.state.IState;
import org.hdiv.state.StateUtil;
import org.hdiv.util.HDIVUtil;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Unit tests for the <code>org.hdiv.composer.DataComposerMemory</code> class.
 * 
 * @author Gorka Vicente
 */
public class DataComposerMemoryTest extends AbstractHDIVTestCase {

	private DataComposerFactory dataComposerFactory;

	private StateUtil stateUtil;

	private ISession session;

	/*
	 * @see TestCase#setUp()
	 */
	protected void onSetUp() throws Exception {

		this.dataComposerFactory = this.getApplicationContext().getBean(DataComposerFactory.class);
		this.stateUtil = this.getApplicationContext().getBean(StateUtil.class);
		this.session = this.getApplicationContext().getBean(ISession.class);
	}

	/**
	 * @see DataComposerMamory#compose(String, String, String, boolean)
	 */
	public void testComposeSimple() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);
		assertTrue(dataComposer instanceof DataComposerMemory);

		dataComposer.startPage();
		dataComposer.beginRequest("test.do");

		boolean confidentiality = this.getConfig().getConfidentiality();

		// we add a multiple parameter that will be encoded as 0, 1, 2, ...
		String result = dataComposer.compose("test.do", "parameter1", "2", false);
		String value = (!confidentiality) ? "2" : "0";
		assertTrue(value.equals(result));

		result = dataComposer.compose("test.do", "parameter1", "2", false);
		value = (!confidentiality) ? "2" : "1";
		assertTrue(value.equals(result));

		result = dataComposer.compose("test.do", "parameter1", "2", false);
		assertTrue("2".equals(result));

		result = dataComposer.compose("test.do", "parameter2", "2", false);
		value = (!confidentiality) ? "2" : "0";
		assertTrue(value.equals(result));

		result = dataComposer.compose("test.do", "parameter2", "2", false);
		value = (!confidentiality) ? "2" : "1";
		assertTrue(value.equals(result));
	}

	public void testComposeAndRestore() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		dataComposer.startPage();
		dataComposer.beginRequest("test.do");
		dataComposer.compose("parameter1", "2", false);
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		assertNotNull(stateId);

		IState state = this.stateUtil.restoreState(stateId);

		assertEquals("test.do", state.getAction());
	}

	public void testComposeExistingState() {
		MockHttpServletRequest request = (MockHttpServletRequest) HDIVUtil.getHttpServletRequest();

		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		dataComposer.startPage();
		dataComposer.beginRequest("test.do");
		dataComposer.compose("parameter1", "2", false);
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		assertNotNull(stateId);
		request.addParameter("_PREVIOUS_HDIV_STATE_", stateId);

		// New request
		IState state = this.stateUtil.restoreState(stateId);
		IPage page = this.session.getPage(state.getPageId() + "");
		dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		dataComposer.startPage(page);
		dataComposer.beginRequest(state);
		dataComposer.compose("parameter1", "3", false);
		String stateId2 = dataComposer.endRequest();
		dataComposer.endPage();

		assertEquals(stateId, stateId2);
		IState state2 = this.stateUtil.restoreState(stateId2);
		assertEquals(state2.getParameter("parameter1").getConfidentialValue(), "1");
		assertTrue(state2.getParameter("parameter1").existValue("3"));
	}

	public void testInnerState() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		dataComposer.startPage();
		dataComposer.beginRequest("test.do");
		dataComposer.compose("parameter1", "2", false);

		// Start inner state
		dataComposer.beginRequest("testinner.do");
		dataComposer.compose("parameter1", "3", false);
		String stateIdInner = dataComposer.endRequest();

		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		assertNotNull(stateId);
		assertNotNull(stateIdInner);
		assertNotSame(stateId, stateIdInner);

		IState state = this.stateUtil.restoreState(stateId);
		IState stateInner = this.stateUtil.restoreState(stateIdInner);
		String action = state.getAction();
		String actionInner = stateInner.getAction();
		assertEquals("test.do", action);
		assertEquals("testinner.do", actionInner);
	}

	public void testEscapeHtml() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		dataComposer.startPage();
		dataComposer.beginRequest("test.do");
		dataComposer.compose("parameter1", "è-test", false);// not escaped value
		dataComposer.compose("parameterEscaped", "&egrave;-test", false);// escaped value
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		assertNotNull(stateId);

		IState state = this.stateUtil.restoreState(stateId);

		assertEquals("test.do", state.getAction());

		IParameter param = state.getParameter("parameter1");
		List<String> values = param.getValues();
		assertEquals(1, values.size());
		assertEquals("è-test", values.get(0));// escaped value is the same

		IParameter param2 = state.getParameter("parameterEscaped");
		List<String> values2 = param2.getValues();
		assertEquals(1, values2.size());
		// State stored value is not escaped value, it is the unescaped value
		assertEquals("è-test", values2.get(0));
	}
	
	public void testEditableNullValue() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		dataComposer.startPage();
		dataComposer.beginRequest("test.do");
		dataComposer.compose("parameter1", "test", true);
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		assertNotNull(stateId);

		IState state = this.stateUtil.restoreState(stateId);

		assertEquals("test.do", state.getAction());

		IParameter param = state.getParameter("parameter1");
		List<String> values = param.getValues();
		assertEquals(0, values.size());
	}

	public void testAjax() {

		MockHttpServletRequest request = (MockHttpServletRequest) HDIVUtil.getHttpServletRequest();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		dataComposer.startPage();
		dataComposer.beginRequest("test.do");
		dataComposer.compose("parameter1", "1", false);
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		assertNotNull(stateId);

		// Ajax request to modify state

		request.addParameter("_MODIFY_HDIV_STATE_", stateId);
		IDataComposer dataComposer2 = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer2, request);

		// Add new parameter
		dataComposer2.compose("parameter2", "2", false);
		String stateId2 = dataComposer2.endRequest();
		dataComposer2.endPage();

		assertEquals(stateId, stateId2);

		// Restore state
		IState state = this.stateUtil.restoreState(stateId);

		// State contains both parameters
		IParameter param = state.getParameter("parameter1");
		String val = param.getValues().get(0);
		assertEquals("1", val);

		param = state.getParameter("parameter2");
		val = param.getValues().get(0);
		assertEquals("2", val);
	}
	
	public void testAjaxWithHeaderEnabledAjaxSupport() {
		this.getConfig().setCreateNewPageInAjaxRequest(false);
		
		MockHttpServletRequest request = (MockHttpServletRequest) HDIVUtil.getHttpServletRequest();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		dataComposer.startPage();
		dataComposer.beginRequest("test.do");
		dataComposer.compose("parameter1", "1", false);
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		assertNotNull(stateId);

		// Ajax
		MockHttpServletRequest ajaxRequest = (MockHttpServletRequest) HDIVUtil.getHttpServletRequest();
		ajaxRequest.addHeader("x-requested-with", "XMLHttpRequest");
		ajaxRequest.addParameter("_HDIV_STATE_", stateId);
		IDataComposer ajaxDataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(ajaxDataComposer, ajaxRequest);
		
		// Ajax request to add states
		ajaxDataComposer.beginRequest("/test/1");
		String ajaxStateId = ajaxDataComposer.endRequest();
		
		// Restore states
		IState state = this.stateUtil.restoreState(stateId);
		IState ajaxState = this.stateUtil.restoreState(ajaxStateId);
		
		assertEquals(state.getPageId(), ajaxState.getPageId());
		assertEquals(state.getId() + 1, ajaxState.getId());
	}
	
	public void testAjaxWithHeaderDisabledAjaxSupport() {
		MockHttpServletRequest request = (MockHttpServletRequest) HDIVUtil.getHttpServletRequest();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		dataComposer.startPage();
		dataComposer.beginRequest("test.do");
		dataComposer.compose("parameter1", "1", false);
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		assertNotNull(stateId);

		// Ajax
		MockHttpServletRequest ajaxRequest = (MockHttpServletRequest) HDIVUtil.getHttpServletRequest();
		ajaxRequest.addHeader("x-requested-with", "XMLHttpRequest");
		ajaxRequest.addParameter("_HDIV_STATE_", stateId);
		IDataComposer ajaxDataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(ajaxDataComposer, ajaxRequest);
		
		// Ajax request to add states
		ajaxDataComposer.beginRequest("/test/1");
		String ajaxStateId = ajaxDataComposer.endRequest();
		
		// Restore states
		int pageId = this.stateUtil.restoreState(stateId).getPageId();
		int ajaxPageId = this.stateUtil.restoreState(ajaxStateId).getPageId();
		
		assertEquals(pageId + 1, ajaxPageId);
	}

	public void testSaveStateInCreation() {

		// Test the validation of a state before processing all page

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		dataComposer.startPage();

		dataComposer.beginRequest("test.do");
		String result = dataComposer.compose("test.do", "parameter1", "2", false);
		assertEquals("0", result);
		String stateId = dataComposer.endRequest();

		IState state = this.stateUtil.restoreState(stateId);
		assertNotNull(state);
		assertEquals("test.do", state.getAction());

		dataComposer.endPage();
	}

	public void testEncodeFormAction() {

		// No encoded url
		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		dataComposer.startPage();
		dataComposer.beginRequest("test test.do");
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		assertNotNull(stateId);

		IState state = this.stateUtil.restoreState(stateId);

		assertEquals("test test.do", state.getAction());

		// Encoded action url
		dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		dataComposer.startPage();
		dataComposer.beginRequest("test%20test.do");
		stateId = dataComposer.endRequest();
		dataComposer.endPage();

		assertNotNull(stateId);

		state = this.stateUtil.restoreState(stateId);

		// State action value is decoded because we store decoded values only
		assertEquals("test test.do", state.getAction());
	}
}
