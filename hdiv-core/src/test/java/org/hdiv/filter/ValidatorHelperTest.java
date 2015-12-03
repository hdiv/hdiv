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
package org.hdiv.filter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.util.HDIVUtil;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.HtmlUtils;

/**
 * Unit tests for the <code>org.hdiv.filter.ValidatorHelper</code> class.
 * 
 * @author Gorka Vicente
 * @author Gotzon Illarramendi
 */
public class ValidatorHelperTest extends AbstractHDIVTestCase {

	private IValidationHelper helper;

	private IDataComposer dataComposer;

	private String hdivParameter;

	private boolean confidentiality;

	private String targetName = "/path/testAction.do";

	protected void onSetUp() throws Exception {

		this.hdivParameter = this.getConfig().getStateParameterName();
		this.helper = this.getApplicationContext().getBean(IValidationHelper.class);
		this.confidentiality = this.getConfig().getConfidentiality();

		DataComposerFactory dataComposerFactory = this.getApplicationContext().getBean(DataComposerFactory.class);
		HttpServletRequest request = this.getMockRequest();
		this.dataComposer = dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);
		this.dataComposer.startPage();
	}

	/**
	 * Validation test with the HDIV parameter only. Validation should be correct.
	 */
	public void testValidateHasOnlyHDIVParameter() {

		MockHttpServletRequest request = this.getMockRequest();

		this.dataComposer.beginRequest("GET", this.targetName);

		String pageState = this.dataComposer.endRequest();
		this.dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);

		RequestWrapper requestWrapper = new RequestWrapper(request);
		boolean result = helper.validate(requestWrapper).isValid();
		assertTrue(result);
	}

	/**
	 * Validation test for an start page.
	 */
	public void testValidateHasActionIsStartPage() {

		MockHttpServletRequest request = this.getMockRequest();

		this.dataComposer.beginRequest("GET", this.targetName);
		request.setRequestURI("/testing.do");

		String pageState = this.dataComposer.endRequest();
		this.dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);

		RequestWrapper requestWrapper = new RequestWrapper(request);
		ValidatorHelperResult result = helper.validate(requestWrapper);
		assertTrue(result.isValid());
		assertEquals(result, ValidatorHelperResult.VALIDATION_NOT_REQUIRED);
	}

	/**
	 * Validation test with an start parameter.
	 */
	public void testValidateHasOneStartParameter() {

		MockHttpServletRequest request = this.getMockRequest();

		this.dataComposer.beginRequest("GET", this.targetName);
		String pageState = this.dataComposer.endRequest();
		this.dataComposer.endPage();

		request.addParameter("testingInitParameter", "0");
		request.addParameter(hdivParameter, pageState);

		RequestWrapper requestWrapper = new RequestWrapper(request);
		ValidatorHelperResult result = helper.validate(requestWrapper);
		assertTrue(result.isValid());
		assertEquals(result, ValidatorHelperResult.VALID);
	}

	/**
	 * Validation test for a non-editable parameter with a correct value.
	 */
	public void testValidateHasOneNotEditableOneParameter() {

		MockHttpServletRequest request = this.getMockRequest();

		this.dataComposer.beginRequest("GET", this.targetName);
		this.dataComposer.compose("param1", "value1", false);

		String pageState = this.dataComposer.endRequest();
		this.dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);

		String value = (this.confidentiality) ? "0" : "value1";
		request.addParameter("param1", value);

		RequestWrapper requestWrapper = new RequestWrapper(request);
		assertTrue(helper.validate(requestWrapper).isValid());
	}

	/**
	 * Validation test with a non-editable multivalue parameter. The obtained values for the parameter must be 0 and 1
	 */
	public void testValidateHasOneNotEditableMultivalueParameter() {

		MockHttpServletRequest request = this.getMockRequest();

		this.dataComposer.beginRequest("GET", this.targetName);
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
		assertTrue(helper.validate(requestWrapper).isValid());
	}

	/**
	 * Validation test with a non-editable multivalue parameter and another non-editable parameter with a simple value.
	 */
	public void testValidateHasMultiValue() {

		MockHttpServletRequest request = this.getMockRequest();

		this.dataComposer.beginRequest("GET", this.targetName);
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
		assertTrue(helper.validate(requestWrapper).isValid());
	}

	/**
	 * Validation test with an init parameter and another non-editable parameter. Validation should be correct as the
	 * resulting values are correct.
	 */
	public void testValidateHasOneStartParameterOneNotEditableParameter() {

		MockHttpServletRequest request = this.getMockRequest();

		this.dataComposer.beginRequest("GET", this.targetName);
		this.dataComposer.compose("param1", "value1", false);

		String pageState = this.dataComposer.endRequest();
		this.dataComposer.endPage();

		String value = (this.confidentiality) ? "0" : "value1";
		request.addParameter("param1", value);

		request.addParameter("testingInitParameter", "0");
		request.addParameter(hdivParameter, pageState);

		RequestWrapper requestWrapper = new RequestWrapper(request);
		assertTrue(helper.validate(requestWrapper).isValid());
	}

	/**
	 * Validation test for a non-editable multivalue parameter with modified values. Should not pass validation as the
	 * second value has been modified.
	 */
	public void testValidateHasOneParameterNotEditableMultivalueIndexOutOfBound() {

		MockHttpServletRequest request = this.getMockRequest();

		this.dataComposer.beginRequest("GET", this.targetName);

		if (this.confidentiality) {

			this.dataComposer.compose("param1", "value1", false);
			this.dataComposer.compose("param1", "value2", false);

			String pageState = this.dataComposer.endRequest();
			this.dataComposer.endPage();

			request.addParameter(hdivParameter, pageState);
			request.addParameter("param1", "0");
			request.addParameter("param1", "2");

			RequestWrapper requestWrapper = new RequestWrapper(request);
			assertTrue(!helper.validate(requestWrapper).isValid());
		}
		assertTrue(true);
	}

	/**
	 * Validation test with a modified non-editable parameter. More than expected parameters are received, so it should
	 * not pass validation.
	 */
	public void testValidateHasInvalidNumberOfParameters() {

		MockHttpServletRequest request = this.getMockRequest();

		this.dataComposer.beginRequest("GET", this.targetName);
		this.dataComposer.compose("param1", "value1", false);

		String pageState = this.dataComposer.endRequest();
		this.dataComposer.endPage();

		String value = (this.confidentiality) ? "0" : "value1";
		request.addParameter("param1", value);

		value = (this.confidentiality) ? "1" : "value2";
		request.addParameter("param1", value);

		request.addParameter(hdivParameter, pageState);

		RequestWrapper requestWrapper = new RequestWrapper(request);
		assertTrue(!helper.validate(requestWrapper).isValid());
	}

	/**
	 * Validation test with a non-editable multivalue parameter. repeated values are received, so it should not pass
	 * validation.
	 */
	public void testValidateHasRepeatedValues() {

		MockHttpServletRequest request = this.getMockRequest();

		this.dataComposer.beginRequest("GET", this.targetName);
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
		assertTrue(!helper.validate(requestWrapper).isValid());
	}

	/**
	 * Validation test with a non-editable parameter. Its value is modified so it should not pass validation.
	 */
	public void testValidateHasOnlyOneParameterNotEditableIndexOutOfBound() {

		MockHttpServletRequest request = this.getMockRequest();

		this.dataComposer.beginRequest("GET", this.targetName);

		if (this.confidentiality) {

			this.dataComposer.compose("param1", "value1", false);

			String pageState = this.dataComposer.endRequest();
			this.dataComposer.endPage();

			request.addParameter(hdivParameter, pageState);
			request.addParameter("param1", "1");

			RequestWrapper requestWrapper = new RequestWrapper(request);
			assertTrue(!helper.validate(requestWrapper).isValid());
		}
		assertTrue(true);
	}

	/**
	 * Validation test with a wrong page identifier. It should not pass validation as there isn't any state in memory
	 * which matches this identifier.
	 */
	public void testValidateHasMemoryWrongStateIndetifier() {

		MockHttpServletRequest request = this.getMockRequest();

		this.dataComposer.beginRequest("GET", this.targetName);
		this.dataComposer.compose("param1", "value1", false);

		// page identifier is incorrect
		String pageState = "1-1";

		request.addParameter(hdivParameter, pageState);

		String value = (this.confidentiality) ? "0" : "value1";
		request.addParameter("param1", value);

		this.dataComposer.endPage();

		boolean result = true;
		try {
			RequestWrapper requestWrapper = new RequestWrapper(request);
			result = helper.validate(requestWrapper).isValid();
			assertFalse(result);
		} catch (Exception e) {
			assertTrue(true);
		}
	}

	public void testEditableParameterValidation() {

		MockHttpServletRequest request = this.getMockRequest();
		request.setMethod("POST");

		this.dataComposer.beginRequest("POST", this.targetName);
		this.dataComposer.compose("paramName", "", true, "text");

		String pageState = this.dataComposer.endRequest();
		this.dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);
		request.addParameter("paramName", "<script>storeCookie()</script>");

		RequestWrapper requestWrapper = new RequestWrapper(request);
		ValidatorHelperResult result = helper.validate(requestWrapper);
		assertFalse(result.isValid());

		// Editable errors
		List<ValidatorError> errors = result.getErrors();
		assertEquals(1, errors.size());
		assertEquals(HDIVErrorCodes.EDITABLE_VALIDATION_ERROR, errors.get(0).getType());
	}

	public void testEditableParameterValidationRedirect() {

		getConfig().setShowErrorPageOnEditableValidation(true);

		MockHttpServletRequest request = this.getMockRequest();
		request.setMethod("POST");

		this.dataComposer.beginRequest("POST", this.targetName);
		this.dataComposer.compose("paramName", "", true, "text");

		String pageState = this.dataComposer.endRequest();
		this.dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);
		request.addParameter("paramName", "<script>storeCookie()</script>");

		RequestWrapper requestWrapper = new RequestWrapper(request);
		boolean result = helper.validate(requestWrapper).isValid();
		assertFalse(result);

	}

	/**
	 * Test for cookies integrity.
	 */
	public void testValidateCookiesIntegrity() {

		MockHttpServletRequest request = this.getMockRequest();
		RequestWrapper requestWrapper = new RequestWrapper(request);

		MockHttpServletResponse response = new MockHttpServletResponse();
		ResponseWrapper responseWrapper = new ResponseWrapper(request, response);

		responseWrapper.addCookie(new Cookie("name", "value"));

		this.dataComposer.beginRequest("GET", this.targetName);
		this.dataComposer.compose("param1", "value1", false);
		String pageState = this.dataComposer.endRequest();
		assertNotNull(pageState);
		request.addParameter(hdivParameter, pageState);

		this.dataComposer.endPage();

		// Modify cookie value on client
		request.setCookies(new Cookie[] { new Cookie("name", "changedValue") });

		requestWrapper = new RequestWrapper(request);
		boolean result = helper.validate(requestWrapper).isValid();
		assertFalse(result);
	}

	public void testValidateWhitespace() {

		MockHttpServletRequest request = this.getMockRequest();
		request.setMethod("POST");

		this.dataComposer.beginRequest("POST", "/path/test Action.do");
		String pageState = this.dataComposer.endRequest();
		this.dataComposer.endPage();

		request.setRequestURI("/path/test%20Action.do");
		request.addParameter(hdivParameter, pageState);

		RequestWrapper requestWrapper = new RequestWrapper(request);
		assertTrue(helper.validate(requestWrapper).isValid());
	}

	public void testValidateEncoded() {

		MockHttpServletRequest request = this.getMockRequest();
		request.setMethod("POST");

		this.dataComposer.beginRequest("POST", "/path/test%20Action.do");
		String pageState = this.dataComposer.endRequest();
		this.dataComposer.endPage();

		request.setRequestURI("/path/test%20Action.do");
		request.addParameter(hdivParameter, pageState);

		RequestWrapper requestWrapper = new RequestWrapper(request);
		assertTrue(helper.validate(requestWrapper).isValid());
	}

	public void testValidateLongConfidencialValue() {

		MockHttpServletRequest request = this.getMockRequest();

		this.dataComposer.beginRequest("GET", this.targetName);
		this.dataComposer.compose("param", "value", false);
		String pageState = this.dataComposer.endRequest();
		this.dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);
		request.addParameter("param", "99999999999999999999");

		RequestWrapper requestWrapper = new RequestWrapper(request);
		boolean result = helper.validate(requestWrapper).isValid();
		assertFalse(result);
	}

	public void testParamWithAmpersand() {

		MockHttpServletRequest request = this.getMockRequest();

		this.dataComposer.beginRequest("GET", this.targetName);
		this.dataComposer.composeParams("param1=111&amp;param2=Me+%26+You", "GET", "utf-8");
		String pageState = this.dataComposer.endRequest();
		this.dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);
		request.addParameter("param1", "0");
		request.addParameter("param2", "0");

		RequestWrapper requestWrapper = new RequestWrapper(request);
		boolean result = helper.validate(requestWrapper).isValid();
		assertTrue(result);

		String param1Value = requestWrapper.getParameter("param1");
		assertEquals("111", param1Value);

		String param2Value = requestWrapper.getParameter("param2");
		assertEquals("Me & You", param2Value);

	}

	public void testValidateLongLiving() {

		MockHttpServletRequest request = this.getMockRequest();

		this.dataComposer.startScope("app");
		this.dataComposer.beginRequest("GET", this.targetName);
		String pageState = this.dataComposer.endRequest();
		this.dataComposer.endScope();
		this.dataComposer.endPage();

		assertTrue(pageState.startsWith("A-"));

		request.addParameter(hdivParameter, pageState);

		RequestWrapper requestWrapper = new RequestWrapper(request);
		boolean result = helper.validate(requestWrapper).isValid();
		assertTrue(result);
	}

	public void testEncodeFormAction() throws UnsupportedEncodingException {

		MockHttpServletRequest request = this.getMockRequest();

		String url = "/sample/TESTÑ/edit";

		// Escaped value is passed by Spring MVC for example
		String escaped = HtmlUtils.htmlEscape(url);
		// Encoded value is what browser sends
		String encoded = URLEncoder.encode(url, "utf-8");

		dataComposer.startPage();
		dataComposer.beginRequest("POST", escaped);
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		request.setRequestURI(encoded);

		assertNotNull(stateId);

		request.addParameter(hdivParameter, stateId);

		RequestWrapper requestWrapper = new RequestWrapper(request);
		boolean result = helper.validate(requestWrapper).isValid();
		assertTrue(result);
	}

	public void testFormActionWithWhitespace() throws UnsupportedEncodingException {

		MockHttpServletRequest request = this.getMockRequest();

		String url = "/sample/TEST TEST/edit";
		String urlRequest = "/sample/TEST%20TEST/edit";

		dataComposer.startPage();
		dataComposer.beginRequest("POST", url);
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		request.setRequestURI(urlRequest);

		assertNotNull(stateId);

		request.addParameter(hdivParameter, stateId);

		RequestWrapper requestWrapper = new RequestWrapper(request);
		boolean result = helper.validate(requestWrapper).isValid();
		assertTrue(result);
	}
}