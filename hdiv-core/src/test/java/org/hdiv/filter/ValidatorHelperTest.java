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
package org.hdiv.filter;

import java.util.Hashtable;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.util.HDIVUtil;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Unit tests for the <code>org.hdiv.filter.ValidatorHelper</code> class.
 * 
 * @author Gorka Vicente
 */
public class ValidatorHelperTest extends AbstractHDIVTestCase {

	private IValidationHelper helper;

	private IDataComposer dataComposer;

	private String hdivParameter;

	private boolean confidentiality;

	private String targetName = "/path/testAction.do";;

	protected void onSetUp() throws Exception {

		this.hdivParameter = (String) this.getApplicationContext().getBean("hdivParameter");
		this.helper = (IValidationHelper) this.getApplicationContext().getBean("validatorHelper");
		this.confidentiality = this.getConfig().getConfidentiality().booleanValue();

		DataComposerFactory dataComposerFactory = (DataComposerFactory) this.getApplicationContext().getBean(
				"dataComposerFactory");
		this.dataComposer = dataComposerFactory.newInstance();
		this.dataComposer.startPage();
	}

	/**
	 * Validation test with the HDIV parameter only. Validation should be correct.
	 */
	public void testValidateHashOnlyHDIVParameter() {

		MockHttpServletRequest request = (MockHttpServletRequest) HDIVUtil.getHttpServletRequest();

		this.dataComposer.beginRequest(this.targetName);

		String pageState = this.dataComposer.endRequest();
		this.dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);

		RequestWrapper requestWrapper = new RequestWrapper(request);
		boolean result = helper.validate(requestWrapper);
		assertTrue(result);
	}

	/**
	 * Validation test for an init action.
	 */
	public void testValidateHashActionIsStartPage() {

		MockHttpServletRequest request = (MockHttpServletRequest) HDIVUtil.getHttpServletRequest();

		this.dataComposer.beginRequest(this.targetName);
		request.setRequestURI("/testing.do");

		String pageState = this.dataComposer.endRequest();
		this.dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);

		RequestWrapper requestWrapper = new RequestWrapper(request);
		assertTrue(helper.validate(requestWrapper));
	}

	/**
	 * Validation test with an init parameter.
	 */
	public void testValidateHashOneStartParameter() {

		MockHttpServletRequest request = (MockHttpServletRequest) HDIVUtil.getHttpServletRequest();

		this.dataComposer.beginRequest(this.targetName);
		String pageState = this.dataComposer.endRequest();
		this.dataComposer.endPage();

		request.addParameter("testingInitParameter", "0");
		request.addParameter(hdivParameter, pageState);

		RequestWrapper requestWrapper = new RequestWrapper(request);
		assertTrue(helper.validate(requestWrapper));
	}

	/**
	 * Validation test for a non-editable parameter with a correct value.
	 */
	public void testValidateHashOneNotEditableOneParameter() {

		MockHttpServletRequest request = (MockHttpServletRequest) HDIVUtil.getHttpServletRequest();

		this.dataComposer.beginRequest(this.targetName);
		this.dataComposer.compose("param1", "value1", false);

		String pageState = this.dataComposer.endRequest();
		this.dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);

		String value = (this.confidentiality) ? "0" : "value1";
		request.addParameter("param1", value);

		RequestWrapper requestWrapper = new RequestWrapper(request);
		assertTrue(helper.validate(requestWrapper));
	}

	/**
	 * Validation test with a non-editable multivalue parameter. The obtained values for the parameter must be 0 and 1
	 */
	public void testValidateHashOneNotEditableMultivalueParameter() {

		MockHttpServletRequest request = (MockHttpServletRequest) HDIVUtil.getHttpServletRequest();

		this.dataComposer.beginRequest(this.targetName);
		this.dataComposer.compose("param1", "value1", false);
		this.dataComposer.compose("param1", "value2", false);

		String pageState = this.dataComposer.endRequest();
		this.dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);

		String value = (this.confidentiality) ? "0" : "value1";
		request.addParameter("param1", value);

		value = (this.confidentiality) ? "1" : "value2";
		request.addParameter("param1", value);

		RequestWrapper requestWrapper = new RequestWrapper(request);
		assertTrue(helper.validate(requestWrapper));
	}

	/**
	 * Validation test with a non-editable multivalue parameter and another non-editable parameter with a simple value.
	 */
	public void testValidateHashMultiValue() {

		MockHttpServletRequest request = (MockHttpServletRequest) HDIVUtil.getHttpServletRequest();

		this.dataComposer.beginRequest(this.targetName);
		this.dataComposer.compose("param1", "value1", false);
		this.dataComposer.compose("param1", "value2", false);
		this.dataComposer.compose("param2", "value3", false);

		String pageState = this.dataComposer.endRequest();

		request.addParameter(hdivParameter, pageState);

		String value = (this.confidentiality) ? "0" : "value1";
		request.addParameter("param1", value);

		value = (this.confidentiality) ? "1" : "value2";
		request.addParameter("param1", value);

		value = (this.confidentiality) ? "0" : "value3";
		request.addParameter("param2", value);

		this.dataComposer.endPage();
		RequestWrapper requestWrapper = new RequestWrapper(request);
		assertTrue(helper.validate(requestWrapper));
	}

	/**
	 * Validation test with an init parameter and another non-editable parameter. Validation should be correct as the
	 * resulting values are correct.
	 */
	public void testValidateHashOneStartParameterOneNotEditableParameter() {

		MockHttpServletRequest request = (MockHttpServletRequest) HDIVUtil.getHttpServletRequest();

		this.dataComposer.beginRequest(this.targetName);
		this.dataComposer.compose("param1", "value1", false);

		String pageState = this.dataComposer.endRequest();
		this.dataComposer.endPage();

		String value = (this.confidentiality) ? "0" : "value1";
		request.addParameter("param1", value);

		request.addParameter("testingInitParameter", "0");
		request.addParameter(hdivParameter, pageState);

		RequestWrapper requestWrapper = new RequestWrapper(request);
		assertTrue(helper.validate(requestWrapper));
	}

	/**
	 * Validation test for a non-editable multivalue parameter with modified values. Should not pass validation as the
	 * second value has been modified.
	 */
	public void testValidateHashOneParameterNotEditableMultivalueIndexOutOfBound() {

		MockHttpServletRequest request = (MockHttpServletRequest) HDIVUtil.getHttpServletRequest();

		this.dataComposer.beginRequest(this.targetName);

		if (this.confidentiality) {

			this.dataComposer.compose("param1", "value1", false);
			this.dataComposer.compose("param1", "value2", false);

			String pageState = this.dataComposer.endRequest();
			this.dataComposer.endPage();

			request.addParameter(hdivParameter, pageState);
			request.addParameter("param1", "0");
			request.addParameter("param1", "2");

			RequestWrapper requestWrapper = new RequestWrapper(request);
			assertTrue(!helper.validate(requestWrapper));
		}
		assertTrue(true);
	}

	/**
	 * Validation test with a modified non-editable parameter. More than expected parameters are received, so it should
	 * not pass validation.
	 */
	public void testValidateHashInvalidNumberOfParameters() {

		MockHttpServletRequest request = (MockHttpServletRequest) HDIVUtil.getHttpServletRequest();

		this.dataComposer.beginRequest(this.targetName);
		this.dataComposer.compose("param1", "value1", false);

		String pageState = this.dataComposer.endRequest();
		this.dataComposer.endPage();

		String value = (this.confidentiality) ? "0" : "value1";
		request.addParameter("param1", value);

		value = (this.confidentiality) ? "1" : "value2";
		request.addParameter("param1", value);

		request.addParameter(hdivParameter, pageState);

		RequestWrapper requestWrapper = new RequestWrapper(request);
		assertTrue(!helper.validate(requestWrapper));
	}

	/**
	 * Validation test with a non-editable multivalue parameter. repeated values are received, so it should not pass
	 * validation.
	 */
	public void testValidateHashRepeatedValues() {

		MockHttpServletRequest request = (MockHttpServletRequest) HDIVUtil.getHttpServletRequest();

		this.dataComposer.beginRequest(this.targetName);
		this.dataComposer.compose("param1", "value1", false);
		this.dataComposer.compose("param1", "value2", false);

		String pageState = this.dataComposer.endRequest();
		this.dataComposer.endPage();

		String value = (this.confidentiality) ? "0" : "value1";
		request.addParameter("param1", value);

		value = (this.confidentiality) ? "0" : "value1";
		request.addParameter("param1", value);

		request.addParameter(hdivParameter, pageState);

		RequestWrapper requestWrapper = new RequestWrapper(request);
		assertTrue(!helper.validate(requestWrapper));
	}

	/**
	 * Validation test with a non-editable parameter. Its value is modified so it should not pass validation.
	 */
	public void testValidateHashOnlyOneParameterNotEditableIndexOutOfBound() {

		MockHttpServletRequest request = (MockHttpServletRequest) HDIVUtil.getHttpServletRequest();

		this.dataComposer.beginRequest(this.targetName);

		if (this.confidentiality) {

			this.dataComposer.compose("param1", "value1", false);

			String pageState = this.dataComposer.endRequest();
			this.dataComposer.endPage();

			request.addParameter(hdivParameter, pageState);
			request.addParameter("param1", "1");

			RequestWrapper requestWrapper = new RequestWrapper(request);
			assertTrue(!helper.validate(requestWrapper));
		}
		assertTrue(true);
	}

	/**
	 * Validation test with a wrong page identifier. It should not pass validation as there isn't any state in memory
	 * which matches this identifier.
	 */
	public void testValidateHashMemoryWrongStateIndetifier() {

		MockHttpServletRequest request = (MockHttpServletRequest) HDIVUtil.getHttpServletRequest();

		this.dataComposer.beginRequest(this.targetName);
		this.dataComposer.compose("param1", "value1", false);

		// page indentifier is incorrect
		String pageState = "1-1";

		request.addParameter(hdivParameter, pageState);

		String value = (this.confidentiality) ? "0" : "value1";
		request.addParameter("param1", value);

		this.dataComposer.endPage();

		boolean result = true;
		try {
			RequestWrapper requestWrapper = new RequestWrapper(request);
			result = helper.validate(requestWrapper);
			assertFalse(result);
		} catch (Exception e) {
			assertTrue(true);
		}
	}

	public void testEditableParameterValidation() {

		MockHttpServletRequest request = (MockHttpServletRequest) HDIVUtil
				.getHttpServletRequest();

		this.dataComposer.beginRequest(this.targetName);
		this.dataComposer.compose("paramName", "", true, "text");

		String pageState = this.dataComposer.endRequest();
		this.dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);
		request.addParameter("paramName", "<script>storeCookie()</script>");

		RequestWrapper requestWrapper = new RequestWrapper(request);
		boolean result = helper.validate(requestWrapper);
		assertTrue(result);

		// Editable errors in request?
		Hashtable parameters = (Hashtable) requestWrapper
				.getAttribute(HDIVErrorCodes.EDITABLE_PARAMETER_ERROR);
		assertEquals(1, parameters.size());

	}
}
