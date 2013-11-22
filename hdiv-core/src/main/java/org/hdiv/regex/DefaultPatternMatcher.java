package org.hdiv.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link PatternMatcher} implementation based on java {@link Pattern}.
 * 
 * @since 2.1.6
 */
public class DefaultPatternMatcher implements PatternMatcher {

	/**
	 * Original regular expression
	 */
	protected String regex;

	/**
	 * Compiled {@link Pattern}
	 */
	protected Pattern pattern;

	/**
	 * Constructor that compiles the regular expression.
	 * 
	 * @param regex
	 *            java regular expression
	 */
	public DefaultPatternMatcher(String regex) {
		this.regex = regex;
		this.compilePattern(regex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.regex.PatternMatcher#matches(java.lang.String)
	 */
	public boolean matches(String input) {

		return this.execPattern(input);
	}

	protected void compilePattern(String regex) {

		this.pattern = Pattern.compile(regex);
	}

	protected boolean execPattern(String input) {

		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((regex == null) ? 0 : regex.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DefaultPatternMatcher other = (DefaultPatternMatcher) obj;
		if (regex == null) {
			if (other.regex != null) {
				return false;
			}
		} else if (!regex.equals(other.regex)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DefaultPatternMatcher [regex=" + regex + "]";
	}

}
