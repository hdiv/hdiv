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
package org.hdiv.validator;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hdiv.regex.PatternMatcher;

/**
 * Default {@link EditableDataValidationProvider} implementation based on validations defined in hdiv-config.xml file.
 * 
 * @since HDIV 2.1.10
 */
public class DefaultEditableDataValidationProvider implements EditableDataValidationProvider, Serializable {

	private static final long serialVersionUID = 2276666823731793620L;

	/**
	 * Map containing the urls and parameters to which the user wants to apply validation for the editable parameters.
	 */
	protected Map<ValidationTarget, List<IValidation>> validations;

	/**
	 * <p>
	 * Checks if the values <code>values</code> are valid for the editable parameter <code>parameter</code>, using the
	 * validations defined in the hdiv-config.xml configuration file of Spring.
	 * </p>
	 * There are two types of validations:
	 * <ul>
	 * <li>accepted: the value is valid only if it passes the validation</li>
	 * <li>rejected: the value is rejected if doesn't pass the validation</li>
	 * </ul>
	 * 
	 * @param url
	 *            target url
	 * @param parameter
	 *            parameter name
	 * @param values
	 *            parameter's values
	 * @param dataType
	 *            editable data type
	 * @return True if the values <code>values</code> are valid for the parameter <code>parameter</code>.
	 */
	public EditableDataValidationResult validate(String url, String parameter, String[] values, String dataType) {

		for (ValidationTarget target : this.validations.keySet()) {

			PatternMatcher urlMatcher = target.getUrl();

			if (urlMatcher.matches(url)) {

				List<PatternMatcher> paramMatchers = target.getParams();
				boolean paramMatch = false;

				if (paramMatchers != null && paramMatchers.size() > 0) {
					for (PatternMatcher paramMatcher : paramMatchers) {
						if (paramMatcher.matches(parameter)) {
							paramMatch = true;
							break;
						}
					}
				} else {
					paramMatch = true;
				}

				if (paramMatch) {

					List<IValidation> userDefinedValidations = this.validations.get(target);
					for (IValidation currentValidation : userDefinedValidations) {

						if (!currentValidation.validate(parameter, values, dataType)) {

							EditableDataValidationResult result = new EditableDataValidationResult(false,
									currentValidation.getName());
							return result;
						}
					}
					return EditableDataValidationResult.VALID;
				}
			}
		}

		return EditableDataValidationResult.VALID;
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

	/**
	 * @param validations
	 *            the validations to set
	 */
	public void setValidations(Map<ValidationTarget, List<IValidation>> validations) {
		this.validations = validations;
	}

	/**
	 * @return the validations
	 */
	public Map<ValidationTarget, List<IValidation>> getValidations() {
		return validations;
	}

}
