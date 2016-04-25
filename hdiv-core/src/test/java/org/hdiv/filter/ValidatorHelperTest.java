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
import javax.servlet.http.HttpServletResponse;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.init.RequestInitializer;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.util.HDIVUtil;
import org.hdiv.util.Method;
import org.springframework.mock.web.MockHttpServletRequest;
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

	private final String targetName = "/path/testAction.do";

	private RequestWrapper requestWrapper;

	private ResponseWrapper responseWrapper;

	@Override
	protected void onSetUp() throws Exception {

		hdivParameter = getConfig().getStateParameterName();
		helper = getApplicationContext().getBean(IValidationHelper.class);
		confidentiality = getConfig().getConfidentiality();

		DataComposerFactory dataComposerFactory = getApplicationContext().getBean(DataComposerFactory.class);
		HttpServletRequest request = getMockRequest();
		HttpServletResponse response = getMockResponse();
		dataComposer = dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);
		dataComposer.startPage();

		RequestInitializer requestInitializer = getApplicationContext().getBean(RequestInitializer.class);
		requestWrapper = requestInitializer.createRequestWrapper(request, response);
		responseWrapper = requestInitializer.createResponseWrapper(request, response);

	}

	/**
	 * Validation test with the HDIV parameter only. Validation should be correct.
	 */
	public void testValidateHasOnlyHDIVParameter() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);

		boolean result = helper.validate(requestWrapper).isValid();
		assertTrue(result);
	}

	/**
	 * Validation test for an start page.
	 */
	public void testValidateHasActionIsStartPage() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		request.setRequestURI("/testing.do");

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);

		ValidatorHelperResult result = helper.validate(requestWrapper);
		assertTrue(result.isValid());
		assertEquals(result, ValidatorHelperResult.VALIDATION_NOT_REQUIRED);
	}

	/**
	 * Validation test with an start parameter.
	 */
	public void testValidateHasOneStartParameter() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter("testingInitParameter", "0");
		request.addParameter(hdivParameter, pageState);

		ValidatorHelperResult result = helper.validate(requestWrapper);
		assertTrue(result.isValid());
		assertEquals(result, ValidatorHelperResult.VALID);
	}

	/**
	 * Validation test for a non-editable parameter with a correct value.
	 */
	public void testValidateHasOneNotEditableOneParameter() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.compose("param1", "value1", false);

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);

		String value = (confidentiality) ? "0" : "value1";
		request.addParameter("param1", value);

		assertTrue(helper.validate(requestWrapper).isValid());
	}

	/**
	 * Validation test with a non-editable multivalue parameter. The obtained values for the parameter must be 0 and 1
	 */
	public void testValidateHasOneNotEditableMultivalueParameter() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.compose("param1", "value1", false);
		dataComposer.compose("param1", "value2", false);

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);

		String value = (confidentiality) ? "0" : "value1";
		request.addParameter("param1", value);

		value = (confidentiality) ? "1" : "value2";
		request.addParameter("param1", value);

		assertTrue(helper.validate(requestWrapper).isValid());
	}

	/**
	 * Validation test with a non-editable multivalue parameter and another non-editable parameter with a simple value.
	 */
	public void testValidateHasMultiValue() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.compose("param1", "value1", false);
		dataComposer.compose("param1", "value2", false);
		dataComposer.compose("param2", "value3", false);

		String pageState = dataComposer.endRequest();

		request.addParameter(hdivParameter, pageState);

		String value = (confidentiality) ? "0" : "value1";
		request.addParameter("param1", value);

		value = (confidentiality) ? "1" : "value2";
		request.addParameter("param1", value);

		value = (confidentiality) ? "0" : "value3";
		request.addParameter("param2", value);

		dataComposer.endPage();
		assertTrue(helper.validate(requestWrapper).isValid());
	}

	/**
	 * Validation test with an init parameter and another non-editable parameter. Validation should be correct as the
	 * resulting values are correct.
	 */
	public void testValidateHasOneStartParameterOneNotEditableParameter() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.compose("param1", "value1", false);

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		String value = (confidentiality) ? "0" : "value1";
		request.addParameter("param1", value);

		request.addParameter("testingInitParameter", "0");
		request.addParameter(hdivParameter, pageState);

		assertTrue(helper.validate(requestWrapper).isValid());
	}

	/**
	 * Validation test for a non-editable multivalue parameter with modified values. Should not pass validation as the
	 * second value has been modified.
	 */
	public void testValidateHasOneParameterNotEditableMultivalueIndexOutOfBound() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);

		if (confidentiality) {

			dataComposer.compose("param1", "value1", false);
			dataComposer.compose("param1", "value2", false);

			String pageState = dataComposer.endRequest();
			dataComposer.endPage();

			request.addParameter(hdivParameter, pageState);
			request.addParameter("param1", "0");
			request.addParameter("param1", "2");

			assertTrue(!helper.validate(requestWrapper).isValid());
		}
		assertTrue(true);
	}

	/**
	 * Validation test with a modified non-editable parameter. More than expected parameters are received, so it should
	 * not pass validation.
	 */
	public void testValidateHasInvalidNumberOfParameters() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.compose("param1", "value1", false);

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		String value = (confidentiality) ? "0" : "value1";
		request.addParameter("param1", value);

		value = (confidentiality) ? "1" : "value2";
		request.addParameter("param1", value);

		request.addParameter(hdivParameter, pageState);

		assertTrue(!helper.validate(requestWrapper).isValid());
	}

	/**
	 * Validation test with a non-editable multivalue parameter. repeated values are received, so it should not pass
	 * validation.
	 */
	public void testValidateHasRepeatedValues() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.compose("param1", "value1", false);
		dataComposer.compose("param1", "value2", false);

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		String value = (confidentiality) ? "0" : "value1";
		request.addParameter("param1", value);

		value = (confidentiality) ? "0" : "value1";
		request.addParameter("param1", value);

		request.addParameter(hdivParameter, pageState);

		assertTrue(!helper.validate(requestWrapper).isValid());
	}

	/**
	 * Validation test with a non-editable parameter. Its value is modified so it should not pass validation.
	 */
	public void testValidateHasOnlyOneParameterNotEditableIndexOutOfBound() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);

		if (confidentiality) {

			dataComposer.compose("param1", "value1", false);

			String pageState = dataComposer.endRequest();
			dataComposer.endPage();

			request.addParameter(hdivParameter, pageState);
			request.addParameter("param1", "1");

			assertTrue(!helper.validate(requestWrapper).isValid());
		}
		assertTrue(true);
	}

	/**
	 * Validation test with a wrong page identifier. It should not pass validation as there isn't any state in memory
	 * which matches this identifier.
	 */
	public void testValidateHasMemoryWrongStateIndetifier() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.compose("param1", "value1", false);

		// page identifier is incorrect
		String pageState = "1-1";

		request.addParameter(hdivParameter, pageState);

		String value = (confidentiality) ? "0" : "value1";
		request.addParameter("param1", value);

		dataComposer.endPage();

		boolean result = true;
		try {
			result = helper.validate(requestWrapper).isValid();
			assertFalse(result);
		}
		catch (Exception e) {
			assertTrue(true);
		}
	}

	public void testEditableParameterValidation() {

		MockHttpServletRequest request = getMockRequest();
		request.setMethod("POST");

		dataComposer.beginRequest(Method.POST, targetName);
		dataComposer.compose("paramName", "", true, "text");

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);
		request.addParameter("paramName", "<script>storeCookie()</script>");

		ValidatorHelperResult result = helper.validate(requestWrapper);
		assertFalse(result.isValid());

		// Editable errors
		List<ValidatorError> errors = result.getErrors();
		assertEquals(1, errors.size());
		assertEquals(HDIVErrorCodes.EDITABLE_VALIDATION_ERROR, errors.get(0).getType());
	}

	public void testEditableParameterValidationRedirect() {

		getConfig().setShowErrorPageOnEditableValidation(true);

		MockHttpServletRequest request = getMockRequest();
		request.setMethod("POST");

		dataComposer.beginRequest(Method.POST, targetName);
		dataComposer.compose("paramName", "", true, "text");

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);
		request.addParameter("paramName", "<script>storeCookie()</script>");

		boolean result = helper.validate(requestWrapper).isValid();
		assertFalse(result);

	}

	/**
	 * Test for cookies integrity.
	 */
	public void testValidateCookiesIntegrity() {

		MockHttpServletRequest request = getMockRequest();

		responseWrapper.addCookie(new Cookie("name", "value"));

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.compose("param1", "value1", false);
		String pageState = dataComposer.endRequest();
		assertNotNull(pageState);
		request.addParameter(hdivParameter, pageState);

		dataComposer.endPage();

		// Modify cookie value on client
		request.setCookies(new Cookie[] { new Cookie("name", "changedValue") });

		boolean result = helper.validate(requestWrapper).isValid();
		assertFalse(result);
	}

	public void testValidateWhitespace() {

		MockHttpServletRequest request = getMockRequest();
		request.setMethod("POST");

		dataComposer.beginRequest(Method.POST, "/path/test Action.do");
		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.setRequestURI("/path/test%20Action.do");
		request.addParameter(hdivParameter, pageState);

		assertTrue(helper.validate(requestWrapper).isValid());
	}

	public void testValidateEncoded() {

		MockHttpServletRequest request = getMockRequest();
		request.setMethod("POST");

		dataComposer.beginRequest(Method.POST, "/path/test%20Action.do");
		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.setRequestURI("/path/test%20Action.do");
		request.addParameter(hdivParameter, pageState);

		assertTrue(helper.validate(requestWrapper).isValid());
	}

	public void testValidateLongConfidencialValue() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.compose("param", "value", false);
		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);
		request.addParameter("param", "99999999999999999999");

		boolean result = helper.validate(requestWrapper).isValid();
		assertFalse(result);
	}

	public void testParamWithAmpersand() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.composeParams("param1=111&amp;param2=Me+%26+You", Method.GET, "utf-8");
		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);
		request.addParameter("param1", "0");
		request.addParameter("param2", "0");

		boolean result = helper.validate(requestWrapper).isValid();
		assertTrue(result);

		String param1Value = requestWrapper.getParameter("param1");
		assertEquals("111", param1Value);

		String param2Value = requestWrapper.getParameter("param2");
		assertEquals("Me & You", param2Value);

	}

	public void testValidateLongLiving() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.startScope("app");
		dataComposer.beginRequest(Method.GET, targetName);
		String pageState = dataComposer.endRequest();
		dataComposer.endScope();
		dataComposer.endPage();

		assertTrue(pageState.startsWith("A-"));

		request.addParameter(hdivParameter, pageState);

		boolean result = helper.validate(requestWrapper).isValid();
		assertTrue(result);
	}

	public void testEncodeFormAction() throws UnsupportedEncodingException {

		MockHttpServletRequest request = getMockRequest();

		String url = "/sample/TESTÑ/edit";

		// Escaped value is passed by Spring MVC for example
		String escaped = HtmlUtils.htmlEscape(url);
		// Encoded value is what browser sends
		String encoded = URLEncoder.encode(url, "utf-8");

		dataComposer.startPage();
		dataComposer.beginRequest(Method.POST, escaped);
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		request.setRequestURI(encoded);

		assertNotNull(stateId);

		request.addParameter(hdivParameter, stateId);

		boolean result = helper.validate(requestWrapper).isValid();
		assertTrue(result);
	}

	public void testFormActionWithWhitespace() throws UnsupportedEncodingException {

		MockHttpServletRequest request = getMockRequest();

		String url = "/sample/TEST TEST/edit";
		String urlRequest = "/sample/TEST%20TEST/edit";

		dataComposer.startPage();
		dataComposer.beginRequest(Method.POST, url);
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		request.setRequestURI(urlRequest);

		assertNotNull(stateId);

		request.addParameter(hdivParameter, stateId);

		boolean result = helper.validate(requestWrapper).isValid();
		assertTrue(result);
	}
}