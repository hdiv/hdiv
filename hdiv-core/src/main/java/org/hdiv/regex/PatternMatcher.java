package org.hdiv.regex;

/**
 * <p>
 * Abstraction for Java Regular Expression execution.
 * </p>
 * <p>
 * The implementation must contain a constructor receiving the regular expression as unique parameter:
 * </p>
 * <code>public PatternMatcher(String regex);</code>
 * 
 * @since 2.1.6
 */
public interface PatternMatcher {

	/**
	 * Executes the regular expression over the input String.
	 * 
	 * @param input
	 *            text to match over the regular expression
	 * @return true if regular expression matches
	 */
	boolean matches(String input);

}
