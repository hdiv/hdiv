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

import java.io.Serializable;

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
public interface PatternMatcher extends Serializable {

	/**
	 * Executes the regular expression over the input String.
	 * 
	 * @param input text to match over the regular expression
	 * @return true if regular expression matches
	 */
	boolean matches(String input);

	/**
	 * Obtain the pattern used to find
	 * @return pattern to find
	 */
	String getPattern();

}
