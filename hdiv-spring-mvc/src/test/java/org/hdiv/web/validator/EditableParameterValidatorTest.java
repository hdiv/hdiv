/*
 * Copyright 2004-2012 The Apache Software Foundation.
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
package org.hdiv.web.validator;

import java.util.HashMap;
import java.util.Hashtable;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.filter.IValidationHelper;
import org.hdiv.filter.RequestWrapper;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class EditableParameterValidatorTest extends AbstractHDIVTestCase {

	private IValidationHelper helper;

	private IDataComposer dataComposer;

	private String hdivParameter;

	private String targetName = "/path/testAction.do";;

	protected void onSetUp() throws Exception {

		this.hdivParameter = (String) this.getApplicationContext().getBean("hdivParameter");
		this.helper = (IValidationHelper) this.getApplicationContext().getBean("validatorHelper");

		DataComposerFactory dataComposerFactory = (DataComposerFactory) this.getApplicationContext().getBean(
				"dataComposerFactory");
		this.dataComposer = dataComposerFactory.newInstance();
		this.dataComposer.startPage();
	}

	@SuppressWarnings("unchecked")
	public void testEditableValidator() {

		MockHttpServletRequest request = (MockHttpServletRequest) HDIVUtil.getHttpServletRequest();

		this.dataComposer.beginRequest(this.targetName);
		this.dataComposer.compose("paramName", "", true, "text");

		String pageState = this.dataComposer.endRequest();
		this.dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);
		request.addParameter("paramName", "<script>storeCookie()</script>");

		RequestWrapper requestWrapper = new RequestWrapper(request);
		boolean result = helper.validate(requestWrapper).isValid();
		assertTrue(result);

		// Editable errors in request?
		Hashtable<String, String[]> parameters = (Hashtable<String, String[]>) requestWrapper
				.getAttribute(Constants.EDITABLE_PARAMETER_ERROR);
		assertEquals(1, parameters.size());

		// Set request attributes on threadlocal
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(requestWrapper));

		// New Editable instance
		EditableParameterValidator validator = new EditableParameterValidator();
		Errors errors = new MapBindingResult(new HashMap<String, String>(), "");
		assertFalse(errors.hasErrors());

		// move errors to Errors instance
		validator.validate("anyObject", errors);
		assertTrue(errors.hasErrors());

	}

}
