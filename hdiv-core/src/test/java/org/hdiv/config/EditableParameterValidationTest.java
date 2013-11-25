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

import org.hdiv.AbstractHDIVTestCase;

public class EditableParameterValidationTest extends AbstractHDIVTestCase {

	protected void onSetUp() throws Exception {
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

}
