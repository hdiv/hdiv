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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hdiv.regex.PatternMatcher;
import org.hdiv.validator.IValidation;

/**
 * Editable field validations.
 * 
 * @author Gorka Vicente
 * @since HDIV 1.1
 */
public class HDIVValidations implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Map containing the urls and parameters to which the user wants to apply validation for the editable parameters.
	 */
	protected Map<ValidationTarget, List<IValidation>> validations;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return validations.toString();
	}

	/**
	 * @return Returns the validations.
	 */
	public Map<ValidationTarget, List<IValidation>> getValidations() {
		return validations;
	}

	/**
	 * @param urls
	 *            The validations to set.
	 */
	public void setValidations(Map<ValidationTarget, List<IValidation>> validations) {
		this.validations = validations;
	}

	/**
	 * Identifier for an unique editable validation target.
	 */
	public static class ValidationTarget implements Serializable {

		private static final long serialVersionUID = 9173925337196238781L;

		private PatternMatcher url;

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

}