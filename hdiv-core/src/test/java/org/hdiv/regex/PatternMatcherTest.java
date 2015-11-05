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
package org.hdiv.regex;

import org.hdiv.AbstractHDIVTestCase;

public class PatternMatcherTest extends AbstractHDIVTestCase {

	private PatternMatcherFactory patternMatcherFactory;

	protected void onSetUp() throws Exception {

		this.patternMatcherFactory = this.getApplicationContext().getBean(PatternMatcherFactory.class);
	}

	public void testPatterns() {

		String pattern = "/home/.*/private/.*";
		PatternMatcher patternMatcher = this.patternMatcherFactory.getPatternMatcher(pattern);

		boolean result = patternMatcher.matches("/home/other/private/index.html");
		assertTrue(result);

		result = patternMatcher.matches("/home/private/index.html");
		assertFalse(result);

		// Other pattern
		pattern = "/home.html";
		patternMatcher = this.patternMatcherFactory.getPatternMatcher(pattern);

		result = patternMatcher.matches("/home.html");
		assertTrue(result);

		result = patternMatcher.matches("/homeNO.html");
		assertFalse(result);
	}

}
