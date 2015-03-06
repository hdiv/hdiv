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
package org.hdiv.validator;

import java.io.Serializable;
import java.util.List;

import org.hdiv.regex.PatternMatcher;

/**
 * Identifier for an unique editable validation target.
 * 
 * @since HDIV 2.1.10
 */
public class ValidationTarget implements Serializable {

	private static final long serialVersionUID = 9173925337196238781L;

	/**
	 * Target url.
	 */
	private PatternMatcher url;

	/**
	 * Target parameter names.
	 */
	private List<PatternMatcher> params;

	public ValidationTarget() {
	}

	public PatternMatcher getUrl() {
		return url;
	}

	public void setUrl(PatternMatcher url) {
		this.url = url;
	}

	public List<PatternMatcher> getParams() {
		return params;
	}

	public void setParams(List<PatternMatcher> params) {
		this.params = params;
	}

}
