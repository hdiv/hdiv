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
package org.hdiv.config;

import java.util.List;
import java.util.Map;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.config.HDIVValidations.ValidationTarget;
import org.hdiv.regex.DefaultPatternMatcher;
import org.hdiv.validator.IValidation;

public class EditableParameterValidationTest extends AbstractHDIVTestCase {

	private HDIVValidations editableValidations;

	protected void onSetUp() throws Exception {

		this.editableValidations = this.getApplicationContext().getBean(HDIVValidations.class);
	}

	public void testEditableParamValidator() {

		boolean exist = getConfig().existValidations();
		assertTrue(exist);

		String url = "/home";
		String parameter = "param";
		String[] values = { "<script>" };
		String dataType = "text";
		boolean result = getConfig().areEditableParameterValuesValid(url, parameter, values, dataType);

		assertFalse(result);

		dataType = "textarea";
		result = getConfig().areEditableParameterValuesValid(url, parameter, values, dataType);

		assertFalse(result);
	}

	public void testEditableParamValidatorOrder() {

		Map<ValidationTarget, List<IValidation>> validations = this.editableValidations.getValidations();
		assertEquals(3, validations.size());

		Object[] ptrs = validations.keySet().toArray();

		ValidationTarget vt0 = (ValidationTarget) ptrs[0];
		ValidationTarget vt1 = (ValidationTarget) ptrs[1];
		ValidationTarget vt2 = (ValidationTarget) ptrs[2];

		assertEquals(new DefaultPatternMatcher("/insecureParams/.*"), vt0.getUrl());
		assertEquals(new DefaultPatternMatcher("/insecure/.*"), vt1.getUrl());
		assertEquals(new DefaultPatternMatcher(".*"), vt2.getUrl());
	}

	public void testEditableParamValidatorPatternOrder() {

		boolean exist = getConfig().existValidations();
		assertTrue(exist);

		String url = "/insecure/action";
		String parameter = "param";
		String[] values = { "<script>" };
		String dataType = "text";
		boolean result = getConfig().areEditableParameterValuesValid(url, parameter, values, dataType);

		assertTrue(result);
	}

	public void testEditableParamValidatorPatternParams() {

		boolean exist = getConfig().existValidations();
		assertTrue(exist);

		// param1
		String url = "/insecureParams/action";
		String parameter = "param1";
		String[] values = { "<script>" };
		String dataType = "text";
		boolean result = getConfig().areEditableParameterValuesValid(url, parameter, values, dataType);

		assertTrue(result);

		// param2
		parameter = "param2";
		result = getConfig().areEditableParameterValuesValid(url, parameter, values, dataType);

		assertTrue(result);

		// otherParam
		parameter = "otherParam";
		result = getConfig().areEditableParameterValuesValid(url, parameter, values, dataType);

		assertFalse(result);
	}

	public void testEditableParamValidatorPatternParams2() {

		boolean exist = getConfig().existValidations();
		assertTrue(exist);

		// param1
		String url = "/secureParams/action";
		String parameter = "param1";
		String[] values = { "<script>" };
		String dataType = "text";
		boolean result = getConfig().areEditableParameterValuesValid(url, parameter, values, dataType);

		assertFalse(result);

		// param2
		parameter = "param2";
		result = getConfig().areEditableParameterValuesValid(url, parameter, values, dataType);

		assertFalse(result);

		// otherParam
		parameter = "otherParam";
		result = getConfig().areEditableParameterValuesValid(url, parameter, values, dataType);

		assertFalse(result);
	}

}
