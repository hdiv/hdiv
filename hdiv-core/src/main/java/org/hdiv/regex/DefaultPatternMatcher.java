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
package org.hdiv.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link PatternMatcher} implementation based on java {@link Pattern}.
 *
 * @since 2.1.6
 */
public class DefaultPatternMatcher implements PatternMatcher {

	private static final long serialVersionUID = 1L;

	/**
	 * Original regular expression
	 */
	protected final String regex;

	/**
	 * Compiled {@link Pattern}
	 */
	protected final Pattern pattern;

	/**
	 * Constructor that compiles the regular expression.
	 *
	 * @param regex java regular expression
	 */
	public DefaultPatternMatcher(final String regex) {
		this.regex = regex;
		pattern = Pattern.compile(regex);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.hdiv.regex.PatternMatcher#matches(java.lang.String)
	 */
	public boolean matches(final String input) {

		return execPattern(input);
	}

	protected boolean execPattern(final String input) {

		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	public String getPattern() {
		return regex;
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
	public boolean equals(final Object obj) {
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
		}
		else if (!regex.equals(other.regex)) {
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
