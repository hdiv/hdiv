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
package org.hdiv.config;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.util.Method;
import org.hdiv.validator.EditableDataValidationProvider;
import org.hdiv.validator.EditableDataValidationResult;

public class HDIVConfigTest extends AbstractHDIVTestCase {

	@Override
	protected void onSetUp() throws Exception {
	}

	public void testIsStartPage() {

		HDIVConfig config = getConfig();

		// method not defined
		boolean result = config.isStartPage("/testing.do", (Method) null);
		assertEquals(true, result);

		result = config.isStartPage("/testing.do", Method.GET);
		assertEquals(true, result);

		result = config.isStartPage("/testing.do", Method.POST);
		assertEquals(true, result);

		// method = get
		result = config.isStartPage("/onlyget.do", (Method) null);
		assertEquals(false, result);

		result = config.isStartPage("/onlyget.do", Method.GET);
		assertEquals(true, result);

		result = config.isStartPage("/onlyget.do", Method.POST);
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
		EditableDataValidationProvider provider = config.getEditableDataValidationProvider();
		EditableDataValidationResult result = provider.validate("inicio.html", "one", new String[] { "noProblem" }, "text");
		assertTrue(result.isValid());

		result = provider.validate("inicio.html", "one", new String[] { "XSS <script>" }, "text");
		assertFalse(result.isValid());
	}

	public void testExcludedExtensions() {

		HDIVConfig config = getConfig();

		boolean result = config.hasExtensionToExclude("/assets/run.js");
		assertTrue(result);

		result = config.hasExtensionToExclude("/assets/image.jpg");
		assertFalse(result);
	}

	public void testIsLongLivingPages() {

		HDIVConfig config = getConfig();

		assertEquals("app", config.isLongLivingPages("/scopedPage/app.html"));
		assertEquals("app", config.isLongLivingPages("/scopedPage/appScoped/test"));
		assertEquals("user-session", config.isLongLivingPages("/scopedPage/user.html"));
		assertEquals("user-session", config.isLongLivingPages("/scopedPage/userScoped/test"));

		assertEquals(null, config.isLongLivingPages("/"));
		assertEquals(null, config.isLongLivingPages("/scopedPage/appScoped"));
		assertEquals(null, config.isLongLivingPages("/scopedPage/"));
	}

}
