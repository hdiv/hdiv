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

import org.hdiv.regex.PatternMatcher;

/**
 * Contains the information of a start page.
 * 
 * @since 2.1.4
 * @author Gotzon Illarramendi
 */
public class StartPage {

	/**
	 * StartPage method. null or "" value is equivalent to 'any method'
	 */
	private String method;

	/**
	 * Url pattern
	 */
	private String pattern;

	/**
	 * Compiled pattern
	 */
	private PatternMatcher compiledPattern;

	public StartPage(String method, String pattern) {
		this.method = method;
		this.pattern = pattern;
	}

	public StartPage(String method, PatternMatcher compiledPattern) {
		this.method = method;
		this.compiledPattern = compiledPattern;
	}

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * @param compiledPattern
	 *            the compiledPattern to set
	 */
	public void setCompiledPattern(PatternMatcher compiledPattern) {
		this.compiledPattern = compiledPattern;
	}

	/**
	 * @return the compiledPattern
	 */
	public PatternMatcher getCompiledPattern() {
		return compiledPattern;
	}

	/**
	 * The method is "any method"?
	 * 
	 * @return is any method?
	 */
	public boolean isAnyMethod() {
		return method == null || method.length() == 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StartPage other = (StartPage) obj;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.equals(other.pattern))
			return false;
		return true;
	}

}