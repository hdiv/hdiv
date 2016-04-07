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
package org.hdiv.web.validator;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.filter.IValidationHelper;
import org.hdiv.filter.RequestWrapper;
import org.hdiv.filter.ValidatorError;
import org.hdiv.filter.ValidatorHelperResult;
import org.hdiv.util.Constants;
import org.hdiv.util.Method;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class EditableParameterValidatorTest extends AbstractHDIVTestCase {

	private IValidationHelper helper;

	private IDataComposer dataComposer;

	private String hdivParameter;

	private final String targetName = "/path/testAction.do";;

	@Override
	protected void onSetUp() throws Exception {

		hdivParameter = getConfig().getStateParameterName();
		helper = getApplicationContext().getBean(IValidationHelper.class);

		final DataComposerFactory dataComposerFactory = (DataComposerFactory) getApplicationContext().getBean("dataComposerFactory");
		final HttpServletRequest request = getMockRequest();
		dataComposer = dataComposerFactory.newInstance(request);
		dataComposer.startPage();
	}

	public void testEditableValidator() {

		final MockHttpServletRequest request = getMockRequest();
		request.setMethod("POST");

		dataComposer.beginRequest(Method.POST, targetName);
		dataComposer.compose("paramName", "", true, "text");

		final String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);
		request.addParameter("paramName", "<script>storeCookie()</script>");

		final RequestWrapper requestWrapper = new RequestWrapper(request);
		final ValidatorHelperResult result = helper.validate(requestWrapper);
		assertFalse(result.isValid());

		// Editable errors in request?
		final List<ValidatorError> validationErrors = result.getErrors();
		requestWrapper.setAttribute(Constants.EDITABLE_PARAMETER_ERROR, validationErrors);
		assertEquals(1, validationErrors.size());

		// Set request attributes on threadlocal
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(requestWrapper));

		// New Editable instance
		final EditableParameterValidator validator = new EditableParameterValidator();
		final Errors errors = new MapBindingResult(new HashMap<String, String>(), "");
		assertFalse(errors.hasErrors());

		// move errors to Errors instance
		validator.validate("anyObject", errors);
		assertTrue(errors.hasErrors());
	}

}