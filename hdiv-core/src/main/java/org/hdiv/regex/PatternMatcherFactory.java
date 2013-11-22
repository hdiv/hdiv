package org.hdiv.regex;

/**
 * Factory that creates {@link PatternMatcher} instances.
 * 
 * @since 2.1.6
 */
public class PatternMatcherFactory {

	/**
	 * Return {@link PatternMatcher} instance.
	 * 
	 * @param regex
	 *            regular expression to execute.
	 * @return {@link PatternMatcher} instance.
	 */
	public PatternMatcher getPatternMatcher(String regex) {
		return new DefaultPatternMatcher(regex);
	}
}
