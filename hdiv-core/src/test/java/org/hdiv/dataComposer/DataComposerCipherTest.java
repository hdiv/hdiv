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
import org.hdiv.config.HDIVConfig;
import org.hdiv.config.Strategy;
import org.hdiv.state.IParameter;
import org.hdiv.state.IState;
import org.hdiv.state.StateUtil;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Unit tests for the {@link DataComposerCipher} class.
 * 
 * @author Gorka Vicente
 */
public class DataComposerCipherTest extends AbstractHDIVTestCase {

	private DataComposerFactory dataComposerFactory;

	private StateUtil stateUtil;

	protected void postCreateHdivConfig(HDIVConfig config) {
		config.setStrategy(Strategy.CIPHER);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void onSetUp() throws Exception {

		this.dataComposerFactory = this.getApplicationContext().getBean(DataComposerFactory.class);
		this.stateUtil = this.getApplicationContext().getBean(StateUtil.class);
	}

	/**
	 * @see DataComposerMamory#compose(String, String, String, boolean)
	 */
	public void testComposeSimple() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);
		assertTrue(dataComposer instanceof DataComposerCipher);

		dataComposer.startPage();
		dataComposer.beginRequest("GET", "test.do");

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

		String id = dataComposer.endRequest();
		assertNotNull(id);
	}

	public void testComposeAndRestore() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);

		dataComposer.startPage();
		dataComposer.beginRequest("GET", "test.do");
		dataComposer.compose("parameter1", "2", false);
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		assertNotNull(stateId);

		IState state = this.stateUtil.restoreState(stateId);

		assertEquals("test.do", state.getAction());
		List<String> values = state.getParameter("parameter1").getValues();
		assertEquals(1, values.size());
		assertEquals("2", values.get(0));
	}

	public void testComposeAndRestoreUrl() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);

		dataComposer.startPage();
		dataComposer.beginRequest("GET", "test.do");
		String params = "param1=val1&param2=val2";
		String processedParams = dataComposer.composeParams(params, "GET", Constants.ENCODING_UTF_8);
		assertEquals("param1=0&param2=0", processedParams);
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		assertNotNull(stateId);

		IState state = this.stateUtil.restoreState(stateId);

		assertEquals("test.do", state.getAction());
		String stateParams = state.getParams();
		assertEquals(params, stateParams);
	}

	public void testMemoryFallback() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);

		// Force change to memory strategy due to long encoded state
		((DataComposerCipher) dataComposer).setAllowedLength(5);

		dataComposer.startPage();
		dataComposer.beginRequest("GET", "test.do");
		dataComposer.compose("parameter1", "2", false);
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		assertTrue(memoryPattern.matcher(stateId).matches());
		assertNotNull(stateId);

		IState state = this.stateUtil.restoreState(stateId);

		assertEquals("test.do", state.getAction());
		List<String> values = state.getParameter("parameter1").getValues();
		assertEquals(1, values.size());
		assertEquals("2", values.get(0));
	}

	public void testAjax() {

		MockHttpServletRequest request = (MockHttpServletRequest) HDIVUtil.getHttpServletRequest();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		dataComposer.startPage();
		dataComposer.beginRequest("POST", "test.do");
		dataComposer.compose("parameter1", "1", false);
		String state1 = dataComposer.endRequest();
		dataComposer.endPage();

		assertNotNull(state1);

		// Ajax request to modify state

		request.addParameter("_MODIFY_HDIV_STATE_", state1);
		IDataComposer dataComposer2 = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer2, request);

		// Add new parameter
		dataComposer2.compose("parameter2", "2", false);
		String state2 = dataComposer2.endRequest();
		dataComposer2.endPage();

		// Restore state
		IState state = this.stateUtil.restoreState(state2);

		// State contains both parameters
		IParameter param = state.getParameter("parameter1");
		String val = param.getValues().get(0);
		assertEquals("1", val);

		param = state.getParameter("parameter2");
		val = param.getValues().get(0);
		assertEquals("2", val);
	}

	public void testInnerState() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		dataComposer.startPage();
		dataComposer.beginRequest("POST", "test.do");
		dataComposer.compose("parameter1", "2", false);

		// Start inner state
		dataComposer.beginRequest("GET", "testinner.do");
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
		dataComposer.beginRequest("POST", "test.do");
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
		dataComposer.beginRequest("POST", "test.do");
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

	public void testSaveStateInCreation() {

		// Test the validation of a state before processing all page

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		dataComposer.startPage();

		dataComposer.beginRequest("POST", "test.do");
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
		dataComposer.beginRequest("POST", "test test.do");
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		assertNotNull(stateId);

		IState state = this.stateUtil.restoreState(stateId);

		assertEquals("test test.do", state.getAction());

		// Encoded action url
		dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		dataComposer.startPage();
		dataComposer.beginRequest("POST", "test%20test.do");
		stateId = dataComposer.endRequest();
		dataComposer.endPage();

		assertNotNull(stateId);

		state = this.stateUtil.restoreState(stateId);

		// State action value is decoded because we store decoded values only
		assertEquals("test test.do", state.getAction());
	}

	public void testComposeSameTwice() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);
		assertTrue(dataComposer instanceof DataComposerCipher);

		dataComposer.startPage();
		dataComposer.beginRequest("POST", "test.do");
		dataComposer.composeFormField("parameter1", "2", false, null);
		String id = dataComposer.endRequest();
		assertNotNull(id);

		// Simulate other request creating a new DataComposer
		IDataComposer dataComposer2 = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer2, request);
		assertTrue(dataComposer2 instanceof DataComposerCipher);

		// Compose same data
		dataComposer2.startPage();
		dataComposer2.beginRequest("POST", "test.do");
		dataComposer2.composeFormField("parameter1", "2", false, null);
		String id2 = dataComposer2.endRequest();
		assertNotNull(id2);

		// Ciphered result is different
		assertFalse(id == id2);
	}

}
