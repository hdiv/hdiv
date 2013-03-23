/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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

		this.dataComposerFactory = (DataComposerFactory) this.getApplicationContext().getBean(DataComposerFactory.class);
		this.stateUtil = (StateUtil) this.getApplicationContext().getBean(StateUtil.class);
		this.session = (ISession) this.getApplicationContext().getBean(ISession.class);
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

		boolean confidentiality = this.getConfig().getConfidentiality().booleanValue();

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
		IPage page = this.session.getPage(state.getPageId());
		dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		dataComposer.startPage(page);
		dataComposer.beginRequest(state);
		dataComposer.compose("parameter1", "3", false);
		String stateId2 = dataComposer.endRequest();
		dataComposer.endPage();

		assertEquals(stateId, stateId2);
		IState state2 = this.stateUtil.restoreState(stateId2);
		assertEquals(state2.getParameter("parameter1").getCount(), 2);
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
		dataComposer.compose("parameter1", "è-test", true);// not escaped value
		dataComposer.compose("parameterEscaped", "&egrave;-test", true);// escaped value
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		assertNotNull(stateId);

		IState state = this.stateUtil.restoreState(stateId);

		assertEquals("test.do", state.getAction());

		IParameter param = state.getParameter("parameter1");
		List values = param.getValues();
		assertEquals(1, values.size());
		assertEquals("è-test", values.get(0));// escaped value is the same

		IParameter param2 = state.getParameter("parameterEscaped");
		List values2 = param2.getValues();
		assertEquals(1, values2.size());
		// State stored value is not escaped value, it is the unescaped value
		assertEquals("è-test", values2.get(0));
	}

}
