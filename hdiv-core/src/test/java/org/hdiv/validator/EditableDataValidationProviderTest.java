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
package org.hdiv.validator;

import java.util.List;
import java.util.Map;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.regex.DefaultPatternMatcher;
import org.hdiv.validator.DefaultEditableDataValidationProvider.ValidationTarget;

public class EditableDataValidationProviderTest extends AbstractHDIVTestCase {

	private EditableDataValidationProvider validationProvider;

	protected void onSetUp() throws Exception {

		this.validationProvider = this.getApplicationContext().getBean(EditableDataValidationProvider.class);
	}

	public void testEditableParamValidator() {

		String url = "/home";
		String parameter = "param";
		String[] values = { "<script>" };
		String dataType = "text";
		EditableDataValidationResult result = this.validationProvider.validate(url, parameter, values, dataType);

		assertFalse(result.isValid());

		dataType = "textarea";
		result = this.validationProvider.validate(url, parameter, values, dataType);

		assertFalse(result.isValid());
	}

	public void testEditableParamValidatorOrder() {

		Map<ValidationTarget, List<IValidation>> validations = ((DefaultEditableDataValidationProvider) this.validationProvider)
				.getValidations();
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

		String url = "/insecure/action";
		String parameter = "param";
		String[] values = { "<script>" };
		String dataType = "text";
		EditableDataValidationResult result = this.validationProvider.validate(url, parameter, values, dataType);

		assertTrue(result.isValid());
	}

	public void testEditableParamValidatorPatternParams() {

		// param1
		String url = "/insecureParams/action";
		String parameter = "param1";
		String[] values = { "<script>" };
		String dataType = "text";
		EditableDataValidationResult result = this.validationProvider.validate(url, parameter, values, dataType);

		assertTrue(result.isValid());

		// param2
		parameter = "param2";
		result = this.validationProvider.validate(url, parameter, values, dataType);

		assertTrue(result.isValid());

		// otherParam
		parameter = "otherParam";
		result = this.validationProvider.validate(url, parameter, values, dataType);

		assertFalse(result.isValid());
	}

	public void testEditableParamValidatorPatternParams2() {

		// param1
		String url = "/secureParams/action";
		String parameter = "param1";
		String[] values = { "<script>" };
		String dataType = "text";
		EditableDataValidationResult result = this.validationProvider.validate(url, parameter, values, dataType);

		assertFalse(result.isValid());

		// param2
		parameter = "param2";
		result = this.validationProvider.validate(url, parameter, values, dataType);

		assertFalse(result.isValid());

		// otherParam
		parameter = "otherParam";
		result = this.validationProvider.validate(url, parameter, values, dataType);

		assertFalse(result.isValid());
	}

}
