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
package org.hdiv.filter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.Cookie;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.context.RequestContext;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.state.scope.StateScopeType;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVErrorCodes;
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

	private ValidationContextImpl context;

	private ResponseWrapper responseWrapper;

	@Override
	protected void onSetUp() throws Exception {

		hdivParameter = getConfig().getStateParameterName();
		helper = getApplicationContext().getBean(IValidationHelper.class);
		confidentiality = getConfig().getConfidentiality();

		DataComposerFactory dataComposerFactory = getApplicationContext().getBean(DataComposerFactory.class);
		dataComposer = dataComposerFactory.newInstance(getRequestContext());
		getRequestContext().setDataComposer(dataComposer);
		dataComposer.startPage();

		((RequestContext) getRequestContext()).setHdivParameterName(hdivParameter);
		responseWrapper = (ResponseWrapper) getRequestContext().getResponse();
		context = new ValidationContextImpl(getRequestContext(), helper, false);

	}

	/**
	 * Validation test with the HDIV parameter only. Validation should be correct.
	 */
	public void testValidateHasOnlyHDIVParameter() {

		dataComposer.beginRequest(Method.GET, targetName);

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		addParameter(pageState);

		boolean result = helper.validate(context).isValid();
		assertTrue(result);
	}

	/**
	 * Validation test for an start page.
	 */
	public void testValidateHasActionIsStartPage() {

		dataComposer.beginRequest(Method.GET, targetName);
		setRequestURI("/testing.do");

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		addParameter(pageState);

		ValidatorHelperResult result = helper.validate(context);
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
		addParameter(pageState);

		ValidatorHelperResult result = helper.validate(context);
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

		addParameter(pageState);

		String value = confidentiality ? "0" : "value1";
		request.addParameter("param1", value);

		assertTrue(helper.validate(context).isValid());
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

		addParameter(pageState);

		String value = confidentiality ? "0" : "value1";
		request.addParameter("param1", value);

		value = confidentiality ? "1" : "value2";
		request.addParameter("param1", value);

		assertTrue(helper.validate(context).isValid());
	}

	/**
	 * Validation test with a non-editable multivalue parameter. The obtained values for the parameter must be 0 and 1
	 */
	public void testValidateRequiredHiddenParameter() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.compose("hiddenValue", "value1", false, true);

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		addParameter(pageState);

		assertTrue(!helper.validate(context).isValid());

		request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		String value = dataComposer.compose("hiddenValue", "value1", false, true);

		pageState = dataComposer.endRequest();
		dataComposer.endPage();

		addParameter(pageState);

		request.addParameter("hiddenValue", value);

		assertTrue(helper.validate(context).isValid());
	}

	/**
	 * Validation test with a non-editable multivalue parameter. The obtained values for the parameter must be 0 and 1
	 */
	public void testValidateRequiredTextParameter() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.compose("text", "value1", true, "text");

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		addParameter(pageState);

		assertTrue(helper.validate(context).isValid());

		request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.compose("text", "value1", true, "text");

		pageState = dataComposer.endRequest();
		dataComposer.endPage();

		addParameter(pageState);

		request.addParameter("text", "value2");

		assertTrue(helper.validate(context).isValid());
	}

	public void testValidateEditableFieldsRequiredByDefault() {

		getConfig().setEditableFieldsRequiredByDefault(true);

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.compose("text", "value1", true, "text");

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		addParameter(pageState);

		ValidatorHelperResult result = helper.validate(context);
		assertFalse(result.isValid());
		assertEquals(HDIVErrorCodes.NOT_RECEIVED_ALL_REQUIRED_PARAMETERS, result.getErrors().get(0).getType());

		getConfig().setEditableFieldsRequiredByDefault(false);
	}

	/**
	 * Validation test with a non-editable multivalue parameter. The obtained values for the parameter must be 0 and 1
	 */
	public void testValidateNotRequiredButtonParameter() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.compose("button", "value1", true, "button");

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		addParameter(pageState);

		assertTrue(helper.validate(context).isValid());

		request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.compose("button", "value1", true, "button");

		pageState = dataComposer.endRequest();
		dataComposer.endPage();

		addParameter(pageState);

		request.addParameter("button", "value1");

		assertTrue(helper.validate(context).isValid());
	}

	public void testValidateNotRequiredButtonAndHiddenParameter() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		String buttonValue = dataComposer.compose("button", "buttonValue", false, "button");
		String hiddenValue = dataComposer.compose("hidden", "value1", false, "hidden", true, Method.POST, "UTF-8");

		System.out.println("HIDDENVALUE:" + hiddenValue + " BUTTON:" + buttonValue);

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		addParameter(pageState);

		assertTrue(!helper.validate(context).isValid());
	}

	public void testValidateNotRequiredButtonAndHiddenParameterNotConfidential() {
		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		String buttonValue = dataComposer.compose("button", "buttonValue", false, "button");
		String hiddenValue = dataComposer.compose("hidden", "value1", false, "hidden", true, Method.POST, "UTF-8");

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		addParameter(pageState);

		request.addParameter("hidden", "value1");
		assertTrue(!helper.validate(context).isValid());

	}

	public void testValidateNotRequiredButtonAndHiddenParameterConfidential() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		String buttonValue = dataComposer.compose("button", "buttonValue", false, "button");
		String hiddenValue = dataComposer.compose("hidden", "value1", false, "hidden", true, Method.POST, "UTF-8");

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		addParameter(pageState);

		request.addParameter("hidden", hiddenValue);
		ValidatorHelperResult result = helper.validate(context);
		assertTrue(result.isValid());
	}

	public void testValidateNotRequiredButtonAndHiddenParameterConfidentialAndButton() {
		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		String buttonValue = dataComposer.compose("button", "buttonValue", false, "button");
		String hiddenValue = dataComposer.compose("hidden", "value1", false, "hidden");

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		addParameter(pageState);

		request.addParameter("hidden", hiddenValue);
		request.addParameter("button", "buttonValue");
		assertTrue(helper.validate(context).isValid());
	}

	public void testValidateNotRequiredResetButton() {
		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		String buttonValue = dataComposer.compose("reset", "buttonValue", true, "reset");
		String hiddenValue = dataComposer.compose("hidden", "value1", false, "hidden");

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		addParameter(pageState);

		request.addParameter("hidden", hiddenValue);
		assertTrue(helper.validate(context).isValid());
	}

	private void addParameter(final String value) {
		getMockRequest().addParameter(hdivParameter, value);
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

		addParameter(pageState);

		String value = confidentiality ? "0" : "value1";
		request.addParameter("param1", value);

		value = confidentiality ? "1" : "value2";
		request.addParameter("param1", value);

		value = confidentiality ? "0" : "value3";
		request.addParameter("param2", value);

		dataComposer.endPage();
		assertTrue(helper.validate(context).isValid());
	}

	/**
	 * Validation test with an init parameter and another non-editable parameter. Validation should be correct as the resulting values are
	 * correct.
	 */
	public void testValidateHasOneStartParameterOneNotEditableParameter() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.compose("param1", "value1", false);

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		String value = confidentiality ? "0" : "value1";
		request.addParameter("param1", value);

		request.addParameter("testingInitParameter", "0");
		addParameter(pageState);

		assertTrue(helper.validate(context).isValid());
	}

	/**
	 * Validation test for a non-editable multivalue parameter with modified values. Should not pass validation as the second value has been
	 * modified.
	 */
	public void testValidateHasOneParameterNotEditableMultivalueIndexOutOfBound() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);

		if (confidentiality) {

			dataComposer.compose("param1", "value1", false);
			dataComposer.compose("param1", "value2", false);

			String pageState = dataComposer.endRequest();
			dataComposer.endPage();

			addParameter(pageState);
			request.addParameter("param1", "0");
			request.addParameter("param1", "2");

			assertTrue(!helper.validate(context).isValid());
		}
		assertTrue(true);
	}

	/**
	 * Validation test with a modified non-editable parameter. More than expected parameters are received, so it should not pass validation.
	 */
	public void testValidateHasInvalidNumberOfParameters() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.compose("param1", "value1", false);

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		String value = confidentiality ? "0" : "value1";
		request.addParameter("param1", value);

		value = confidentiality ? "1" : "value2";
		request.addParameter("param1", value);

		addParameter(pageState);

		assertTrue(!helper.validate(context).isValid());
	}

	/**
	 * Validation test with a non-editable multivalue parameter. repeated values are received, so it should not pass validation.
	 */
	public void testValidateHasRepeatedValues() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.compose("param1", "value1", false);
		dataComposer.compose("param1", "value2", false);

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		String value = confidentiality ? "0" : "value1";
		request.addParameter("param1", value);

		value = confidentiality ? "0" : "value1";
		request.addParameter("param1", value);

		addParameter(pageState);

		assertTrue(!helper.validate(context).isValid());
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

			addParameter(pageState);
			request.addParameter("param1", "1");

			assertTrue(!helper.validate(context).isValid());
		}
		assertTrue(true);
	}

	/**
	 * Validation test with a wrong page identifier. It should not pass validation as there isn't any state in memory which matches this
	 * identifier.
	 */
	public void testValidateHasMemoryWrongStateIndetifier() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.compose("param1", "value1", false);

		// page identifier is incorrect
		String pageState = "1-1";

		addParameter(pageState);

		String value = confidentiality ? "0" : "value1";
		request.addParameter("param1", value);

		dataComposer.endPage();

		boolean result = true;
		try {
			result = helper.validate(context).isValid();
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

		addParameter(pageState);
		request.addParameter("paramName", "<script>storeCookie()</script>");

		ValidatorHelperResult result = helper.validate(context);
		assertFalse(result.isValid());

		// Editable errors
		List<ValidatorError> errors = result.getErrors();
		assertEquals(1, errors.size());
		assertEquals(HDIVErrorCodes.INVALID_EDITABLE_VALUE, errors.get(0).getType());
	}

	public void testEditableParameterValidationRedirect() {

		getConfig().setShowErrorPageOnEditableValidation(true);

		MockHttpServletRequest request = getMockRequest();
		request.setMethod("POST");

		dataComposer.beginRequest(Method.POST, targetName);
		dataComposer.compose("paramName", "", true, "text");

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		addParameter(pageState);
		request.addParameter("paramName", "<script>storeCookie()</script>");

		boolean result = helper.validate(context).isValid();
		assertFalse(result);

	}

	/**
	 * Test for cookies integrity.
	 */
	public void testValidateCookiesIntegrityIncorrect() {

		MockHttpServletRequest request = getMockRequest();

		responseWrapper.addCookie(new Cookie("name", "value"));

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.compose("param1", "value1", false);
		String pageState = dataComposer.endRequest();
		assertNotNull(pageState);
		addParameter(pageState);

		dataComposer.endPage();

		// Modify cookie value on client
		request.setCookies(new Cookie[] { new Cookie("name", "changedValue") });

		boolean result = helper.validate(context).isValid();
		assertFalse(result);
	}

	/**
	 * Test for cookies integrity.
	 */
	public void testValidateCookiesIntegrityCorrect() {

		MockHttpServletRequest request = getMockRequest();

		responseWrapper.addCookie(new Cookie("name", "value"));

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.compose("param1", "value1", false);
		String pageState = dataComposer.endRequest();
		assertNotNull(pageState);
		addParameter(pageState);

		dataComposer.endPage();

		// Modify cookie value on client
		request.setCookies(new Cookie[] { new Cookie("name", "0") });

		boolean result = helper.validate(context).isValid();
		assertTrue(result);
	}

	/**
	 * Test for cookies integrity.
	 */
	public void testValidateCookiesIntegrityCorrectWithDomain() {

		MockHttpServletRequest request = getMockRequest();

		Cookie localCookie = new Cookie("name", "value");
		localCookie.setDomain("localhost");

		responseWrapper.addCookie(localCookie);

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.compose("param1", "value1", false);
		String pageState = dataComposer.endRequest();
		assertNotNull(pageState);
		request.addParameter(hdivParameter, pageState);

		dataComposer.endPage();

		// Modify cookie value on client
		request.setCookies(new Cookie[] { new Cookie("name", "0") });

		boolean result = helper.validate(context).isValid();
		assertTrue(result);
	}

	public void testValidateWhitespace() {

		MockHttpServletRequest request = getMockRequest();
		request.setMethod("POST");

		dataComposer.beginRequest(Method.POST, "/path/test Action.do");
		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		setRequestURI("/path/test%20Action.do");
		addParameter(pageState);

		assertTrue(helper.validate(context).isValid());
	}

	public void testValidateEncoded() {

		MockHttpServletRequest request = getMockRequest();
		request.setMethod("POST");

		dataComposer.beginRequest(Method.POST, "/path/test%20Action.do");
		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		setRequestURI("/path/test%20Action.do");
		addParameter(pageState);

		assertTrue(helper.validate(context).isValid());
	}

	public void testValidateLongConfidencialValue() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.compose("param", "value", false);
		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		addParameter(pageState);
		request.addParameter("param", "99999999999999999999");

		boolean result = helper.validate(context).isValid();
		assertFalse(result);
	}

	public void testParamWithAmpersand() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.composeParams("param1=111&amp;param2=Me+%26+You", Method.GET, Constants.ENCODING_UTF_8);
		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		addParameter(pageState);
		request.addParameter("param1", "0");
		request.addParameter("param2", "0");

		boolean result = helper.validate(context).isValid();
		assertTrue(result);

		String param1Value = context.getRequestContext().getParameter("param1");
		assertEquals("111", param1Value);

		String param2Value = context.getRequestContext().getParameter("param2");
		assertEquals("Me & You", param2Value);

	}

	public void testValidateLongLiving() {

		dataComposer.startScope(StateScopeType.APP);
		dataComposer.beginRequest(Method.GET, targetName);
		String pageState = dataComposer.endRequest();
		dataComposer.endScope();
		dataComposer.endPage();

		assertTrue(pageState.startsWith("A-"));

		addParameter(pageState);

		boolean result = helper.validate(context).isValid();
		assertTrue(result);
	}

	public void testEncodeFormAction() throws UnsupportedEncodingException {

		String url = "/sample/TESTÑ/edit";

		// Escaped value is passed by Spring MVC for example
		String escaped = HtmlUtils.htmlEscape(url);
		// Encoded value is what browser sends
		String encoded = URLEncoder.encode(url, Constants.ENCODING_UTF_8);

		dataComposer.startPage();
		dataComposer.beginRequest(Method.POST, escaped);
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		setRequestURI(encoded);

		assertNotNull(stateId);

		addParameter(stateId);

		boolean result = helper.validate(context).isValid();
		assertTrue(result);
	}

	public void testFormActionWithWhitespace() {

		String url = "/sample/TEST TEST/edit";
		String urlRequest = "/sample/TEST%20TEST/edit";

		dataComposer.startPage();
		dataComposer.beginRequest(Method.POST, url);
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		setRequestURI(urlRequest);

		assertNotNull(stateId);

		addParameter(stateId);

		boolean result = helper.validate(context).isValid();
		assertTrue(result);
	}

	/**
	 * Test validation with a link without parameters
	 */
	public void testIfAllParametersAreReceivedLinkWithoutParameters() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);

		boolean result = helper.validate(context).isValid();
		assertTrue(result);
	}

	/**
	 * Test validation with a link without parameters and adding a parameter
	 */
	public void testIfAllParametersAreReceivedLinkWithoutParametersAndAddParameter() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);

		// Add parameter
		request.addParameter("param1", "0");

		boolean result = helper.validate(context).isValid();
		assertFalse(result);
	}

	/**
	 * Test validation with a link with parameter
	 */
	public void testIfAllParametersAreReceivedLinkWithParameter() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.composeParams("param1=111", Method.GET, Constants.ENCODING_UTF_8);
		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);
		request.addParameter("param1", "0");

		boolean result = helper.validate(context).isValid();
		assertTrue(result);
	}

	/**
	 * Test validation if a parameter from a link is removed
	 */
	public void testIfAllParametersAreReceivedRemoveParameterFromLink() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.composeParams("param1=111", Method.GET, Constants.ENCODING_UTF_8);
		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);

		// Do not add parameter request (remove parameter)
		// request.addParameter("param1", "0");

		boolean result = helper.validate(context).isValid();
		assertFalse(result);
	}

	/**
	 * Test validation if a new parameter is added to a link
	 */
	public void testIfAllParametersAreReceivedAddParameterToLink() {

		MockHttpServletRequest request = getMockRequest();

		dataComposer.beginRequest(Method.GET, targetName);
		dataComposer.composeParams("param1=111", Method.GET, Constants.ENCODING_UTF_8);
		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);

		// Add new parameter request
		request.addParameter("param4", "111");

		boolean result = helper.validate(context).isValid();
		assertFalse(result);
	}

	/**
	 * Test validation if a form has parameters
	 */
	public void testIfAllParametersAreReceivedFormWithParameters() {

		MockHttpServletRequest request = getMockRequest();
		request.setMethod("POST");

		dataComposer.beginRequest(Method.POST, targetName);

		// Form parameters
		dataComposer.compose("param", "value", false);

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);
		request.addParameter("param", "0");

		ValidatorHelperResult result = helper.validate(context);
		assertTrue(result.isValid());

	}

	/**
	 * Test validation if a form has parameters and a new parameter is added
	 */
	public void testIfAllParametersAreReceivedFormWithParametersAndAddParameter() {

		MockHttpServletRequest request = getMockRequest();
		request.setMethod("POST");

		dataComposer.beginRequest(Method.POST, targetName);

		// Form parameters
		dataComposer.compose("param", "value", false);

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);
		request.addParameter("param", "0");
		request.addParameter("newParam", "0");

		ValidatorHelperResult result = helper.validate(context);
		assertFalse(result.isValid());
	}

	/**
	 * Test validation if a form has parameters and its action has parameters too
	 */
	public void testIfAllParametersAreReceivedFormWithParametersAndActionWithParameters() {

		MockHttpServletRequest request = getMockRequest();
		request.setMethod("POST");

		dataComposer.beginRequest(Method.POST, targetName);

		// Action parameters
		dataComposer.composeParams("paramAction=111", Method.POST, Constants.ENCODING_UTF_8);

		// Form parameters
		dataComposer.compose("param", "value", false);

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);
		request.addParameter("paramAction", "0");
		request.addParameter("param", "0");

		ValidatorHelperResult result = helper.validate(context);
		assertTrue(result.isValid());

	}

	/**
	 * Test validation if a form has parameters and its action has parameters too. Remove a parameter from action
	 */
	public void testIfAllParametersAreReceivedFormWithParametersAndRemoveParamFromAction() {

		MockHttpServletRequest request = getMockRequest();
		request.setMethod("POST");

		dataComposer.beginRequest(Method.POST, targetName);

		// Action parameters
		dataComposer.composeParams("paramAction=111", Method.POST, Constants.ENCODING_UTF_8);

		// Form parameters
		dataComposer.compose("param", "value", false);

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);

		// Remove parameter from action
		// request.addParameter("paramAction", "0");

		request.addParameter("param", "0");

		ValidatorHelperResult result = helper.validate(context);
		assertFalse(result.isValid());

	}

	/**
	 * Test validation if a form has parameters and its action has parameters too. Add a parameter to action
	 */
	public void testIfAllParametersAreReceivedFormWithParametersAndAddParamToAction() {

		MockHttpServletRequest request = getMockRequest();
		request.setMethod("POST");

		dataComposer.beginRequest(Method.POST, targetName);

		// Action parameters
		dataComposer.composeParams("paramAction=111", Method.POST, Constants.ENCODING_UTF_8);

		// Form parameters
		dataComposer.compose("param", "value", false);

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);
		request.addParameter("paramAction", "0");
		request.addParameter("param", "0");

		// Added parameter
		request.addParameter("paramAction2", "0");

		ValidatorHelperResult result = helper.validate(context);
		assertFalse(result.isValid());

	}

	/**
	 * Test validation if a form has NOT parameters and its action has parameters.
	 */
	public void testIfAllParametersAreReceivedFormWithoutParamsAndParamsInAction() {

		MockHttpServletRequest request = getMockRequest();
		request.setMethod("POST");

		dataComposer.beginRequest(Method.POST, targetName);

		// Action parameters
		dataComposer.composeParams("paramAction=111", Method.POST, Constants.ENCODING_UTF_8);

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);
		request.addParameter("paramAction", "0");

		ValidatorHelperResult result = helper.validate(context);
		assertTrue(result.isValid());
	}

	/**
	 * Test validation if a form has NOT any parameters and its action has parameters. Remove action param.
	 */
	public void testIfAllParametersAreReceivedFormWithoutParamsAndRemovingParamsInAction() {

		MockHttpServletRequest request = getMockRequest();
		request.setMethod("POST");

		dataComposer.beginRequest(Method.POST, targetName);

		// Action parameters
		dataComposer.composeParams("paramAction=111", Method.POST, Constants.ENCODING_UTF_8);

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);
		// request.addParameter("paramAction", "0");

		ValidatorHelperResult result = helper.validate(context);
		assertFalse(result.isValid());

	}

	/**
	 * Test validation if a form has NOT any parameters and its action has parameters. Add action param.
	 */
	public void testIfAllParametersAreReceivedFormWithoutParamsAndAddingParamsInAction() {

		MockHttpServletRequest request = getMockRequest();
		request.setMethod("POST");

		dataComposer.beginRequest(Method.POST, targetName);

		// Action parameters
		dataComposer.composeParams("paramAction=111", Method.POST, Constants.ENCODING_UTF_8);

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);
		request.addParameter("paramAction", "0");
		request.addParameter("paramAction2", "0");

		ValidatorHelperResult result = helper.validate(context);
		assertFalse(result.isValid());

	}

	/**
	 * Test validation if a form has NOT any parameters. Add form param.
	 */
	public void testIfAllParametersAreReceivedFormWithoutParamsAndAddingFormParam() {

		MockHttpServletRequest request = getMockRequest();
		request.setMethod("POST");

		dataComposer.beginRequest(Method.POST, targetName);

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);
		request.addParameter("newParam", "0");

		ValidatorHelperResult result = helper.validate(context);
		assertFalse(result.isValid());

	}

	private ValidationContextImpl build() {
		return new ValidationContextImpl(getRequestContext(), helper, false);
	}

	/**
	 * Test validation if a form has a parameter with special characters
	 */
	public void testFormWithParameterWithSpecialCharacterDifferentValue() {

		getConfig().setConfidentiality(false);
		MockHttpServletRequest request = getMockRequest();
		request.setMethod("POST");

		dataComposer.beginRequest(Method.POST, targetName);

		// Form parameters
		dataComposer.compose("param", "valu+e", false);

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);
		request.addParameter("param", "valu e");

		ValidatorHelperResult result = helper.validate(context);
		assertTrue(result.isValid());

	}

	/**
	 * Test validation if a form has a parameter with special characters
	 */
	public void testFormWithParameterWithSpecialCharacterSameValue() {

		getConfig().setConfidentiality(false);
		MockHttpServletRequest request = getMockRequest();
		request.setMethod("POST");

		dataComposer.beginRequest(Method.POST, targetName);

		// Form parameters
		dataComposer.compose("param", "valu+e", false);

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);
		request.addParameter("param", "valu+e");

		ValidatorHelperResult result = helper.validate(context);
		assertTrue(result.isValid());

	}

	public void testFormWithParameterWithSpecialCharacterEscaped() {

		getConfig().setConfidentiality(false);
		MockHttpServletRequest request = getMockRequest();
		request.setMethod("POST");

		dataComposer.beginRequest(Method.POST, targetName);

		// Form parameters
		dataComposer.composeFormField("field", "user&Atilde;&plusmn;Id", false, "hidden");// 'userñId' escaped by Spring hidden tag

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);
		request.addParameter("field", "userñId");

		ValidatorHelperResult result = helper.validate(context);
		assertTrue(result.isValid());
	}

	public void testFormWithParameterWithSpecialCharacterWrongEncoding() {

		getConfig().setConfidentiality(false);
		MockHttpServletRequest request = getMockRequest();
		request.setMethod("POST");

		dataComposer.beginRequest(Method.POST, targetName);

		// Form parameters
		dataComposer.composeFormField("field", "valueññ", false, "submit");

		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);
		String wrongEncoding = "valueÃ±Ã±";// Encoded using UTF-8 and decoded using ISO-8859-1
		request.addParameter("field", wrongEncoding);

		ValidatorHelperResult result = helper.validate(context);
		assertTrue(result.isValid());
	}

	public void testWrongEncodingValue() throws UnsupportedEncodingException {

		String value = "valueññ";

		String encoded = URLEncoder.encode(value, Constants.ENCODING_UTF_8);
		// encoded: value%C3%B1%C3%B1

		String decoded = URLDecoder.decode(encoded, "ISO-8859-1");
		// decoded = valueÃ±Ã±

		// Wrong decode encoding used
		// try to fix it

		String fix = URLEncoder.encode(decoded, "ISO-8859-1");
		// fix = value%C3%B1%C3%B1

		fix = URLDecoder.decode(fix, Constants.ENCODING_UTF_8);
		// fix = valueññ

		assertEquals(value, fix);
	}
}
