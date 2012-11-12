/**
 * Copyright 2005-2012 hdiv.org
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

import java.util.regex.Pattern;

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
	private Pattern compiledPattern;

	public StartPage(String method, String pattern) {
		this.method = method;
		this.pattern = pattern;
		this.compiledPattern = Pattern.compile(pattern);
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
	 * @return the compiledPattern
	 */
	public Pattern getCompiledPattern() {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("StartPage[");
		sb.append("method = " + this.method);
		sb.append(", ");
		sb.append("pattern = " + this.pattern);
		sb.append("]");
		return sb.toString();
	}

}