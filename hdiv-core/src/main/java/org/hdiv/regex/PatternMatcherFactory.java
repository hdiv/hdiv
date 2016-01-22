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

/**
 * Factory that creates {@link PatternMatcher} instances.
 * 
 * @since 2.1.6
 */
public class PatternMatcherFactory {

	/**
	 * Return {@link PatternMatcher} instance.
	 * 
	 * @param regex regular expression to execute.
	 * @return {@link PatternMatcher} instance.
	 */
	public PatternMatcher getPatternMatcher(String regex) {
		return new DefaultPatternMatcher(regex);
	}
}
