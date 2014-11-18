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
import org.hdiv.regex.DefaultPatternMatcher;
import org.hdiv.regex.PatternMatcher;
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

		Map<PatternMatcher, List<IValidation>> urls = this.editableValidations.getUrls();
		assertEquals(2, urls.size());

		Object[] ptrs = urls.keySet().toArray();

		assertEquals(new DefaultPatternMatcher("/insecure/.*"), ptrs[0]);
		assertEquals(new DefaultPatternMatcher(".*"), ptrs[1]);
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

}
