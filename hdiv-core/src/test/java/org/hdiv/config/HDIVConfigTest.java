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

public class HDIVConfigTest extends AbstractHDIVTestCase {

	protected void onSetUp() throws Exception {
	}

	public void testIsStartPage() {

		HDIVConfig config = getConfig();

		// method not defined
		boolean result = config.isStartPage("/testing.do", null);
		assertEquals(true, result);

		result = config.isStartPage("/testing.do", "get");
		assertEquals(true, result);

		result = config.isStartPage("/testing.do", "post");
		assertEquals(true, result);

		// method = get
		result = config.isStartPage("/onlyget.do", null);
		assertEquals(false, result);

		result = config.isStartPage("/onlyget.do", "get");
		assertEquals(true, result);

		result = config.isStartPage("/onlyget.do", "post");
		assertEquals(false, result);

	}

	public void testIsParameterWithoutValidation() {

		HDIVConfig config = getConfig();

		boolean result = config.isParameterWithoutValidation("/path/testAction.do", "testingInitParameter");
		assertTrue(result);

		result = config.isParameterWithoutValidation("/path/testAction.do", "testingNOInitParameter");
		assertFalse(result);
	}

	public void testAreEditableParameterValuesValid() {

		HDIVConfig config = getConfig();
		boolean result = config.areEditableParameterValuesValid("inicio.html", "one", new String[] { "noProblem" },
				"text");
		assertTrue(result);

		result = config.areEditableParameterValuesValid("inicio.html", "one", new String[] { "XSS <script>" }, "text");
		assertFalse(result);
	}

	public void testExcludedExtensions() {

		HDIVConfig config = getConfig();

		boolean result = config.hasExtensionToExclude("/assets/run.js");
		assertTrue(result);

		result = config.hasExtensionToExclude("/assets/image.jpg");
		assertFalse(result);
	}

}
