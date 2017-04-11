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
package org.hdiv.util;

import junit.framework.TestCase;

public class UtilsJsfTest extends TestCase {

	public void testRemoveRowId() {

		String clientId = "aaaa:1:bbbb";
		String ci = UtilsJsf.removeRowId(clientId);
		assertEquals("aaaa:bbbb", ci);
		assertFalse(ci.matches(":\\d*:"));
	}

	public void testRemoveRowIdEnd() {

		String clientId = "aaaa:1";
		String ci = UtilsJsf.removeRowId(clientId);
		assertEquals("aaaa", ci);
		assertFalse(ci.matches(":\\d*:"));
	}

	public void testHasRowId() {

		String clientId = "aaaa:1:bbbb";
		assertTrue(UtilsJsf.hasRowId(clientId));

		clientId = "aaaa:bbbb";
		assertFalse(UtilsJsf.hasRowId(clientId));
	}

	public void testHasRowIdEnd() {

		String clientId = "aaaa:1";
		assertTrue(UtilsJsf.hasRowId(clientId));

		clientId = "aaaa:bbbb";
		assertFalse(UtilsJsf.hasRowId(clientId));
	}

}
