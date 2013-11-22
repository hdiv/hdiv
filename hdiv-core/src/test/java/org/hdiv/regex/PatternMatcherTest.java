package org.hdiv.regex;

import org.hdiv.AbstractHDIVTestCase;

public class PatternMatcherTest extends AbstractHDIVTestCase {

	private PatternMatcherFactory patternMatcherFactory = new PatternMatcherFactory();

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
