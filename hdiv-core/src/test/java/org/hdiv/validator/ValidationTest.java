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
package org.hdiv.validator;

import org.hdiv.AbstractHDIVTestCase;

public class ValidationTest extends AbstractHDIVTestCase {

	protected void onSetUp() throws Exception {
	}

	public void testValidate() {

		Validation validation = new Validation();
		validation.setName("example");
		validation.setComponentType("text");
		validation.setAcceptedPattern("^[a-zA-Z0-9@.\\-_]*$");

		boolean result = validation.validate("param", new String[] { "safetext" }, "text");
		assertTrue(result);

		result = validation.validate("param", new String[] { "NOsafetext<<<" }, "text");
		assertFalse(result);

		result = validation.validate("param", new String[] { "safetext" }, "othertype");
		assertTrue(result);

	}

}