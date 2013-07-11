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
package org.hdiv.web.validator;

import java.util.Map;

import org.hdiv.util.Constants;
import org.springframework.validation.Errors;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Abstract class with common editable validation methods.
 * 
 * @author Gotzon Illarramendi
 * @since HDIV 2.1.4
 */
public abstract class AbstractEditableParameterValidator {

	/**
	 * Obtains the errors from request detected by HDIV during the validation process of the editable parameters.
	 * 
	 * @param errors
	 *            errors detected by HDIV during the validation process of the editable parameters.
	 */
	@SuppressWarnings("unchecked")
	protected void validateEditableParameters(Errors errors) {

		RequestAttributes attr = RequestContextHolder.getRequestAttributes();
		if (attr == null) {
			// This is not a web request
			return;
		}

		Map<String, String[]> parameters = (Map<String, String[]>) attr.getAttribute(
				Constants.EDITABLE_PARAMETER_ERROR, 0);
		if (parameters != null && parameters.size() > 0) {

			for (String param : parameters.keySet()) {

				String[] unauthorizedValues = parameters.get(param);

				this.rejectParamValues(param, unauthorizedValues, errors);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void validateEditableParameter(String param, Errors errors) {

		RequestAttributes attr = RequestContextHolder.getRequestAttributes();
		if (attr == null) {
			// This is not a web request
			return;
		}

		Map<String, String[]> parameters = (Map<String, String[]>) attr.getAttribute(
				Constants.EDITABLE_PARAMETER_ERROR, 0);
		if (parameters != null && parameters.size() > 0) {

			String[] unauthorizedValues = parameters.get(param);
			if (unauthorizedValues != null && unauthorizedValues.length > 0) {

				this.rejectParamValues(param, unauthorizedValues, errors);
			}
		}
	}

	protected void rejectParamValues(String param, String[] paramValues, Errors errors) {

		if ((paramValues.length == 1) && (paramValues[0].equals(Constants.HDIV_EDITABLE_PASSWORD_ERROR_KEY))) {

			errors.rejectValue(param, Constants.HDIV_EDITABLE_PASSWORD_ERROR_KEY);

		} else {
			String printedValue = this.createMessageError(paramValues);
			errors.rejectValue(param, Constants.HDIV_EDITABLE_ERROR_KEY, new String[] { printedValue }, printedValue
					+ " has not allowed characters");
		}
	}

	/**
	 * It creates the message error from the values <code>values</code>.
	 * 
	 * @param values
	 *            values with not allowed characters
	 * @return message error to show
	 */
	protected String createMessageError(String[] values) {

		StringBuffer printedValue = new StringBuffer();

		for (int i = 0; i < values.length; i++) {

			if (i > 0) {
				printedValue.append(", ");
			}
			if (values[i].length() > 20) {
				printedValue.append(values[i].substring(0, 20) + "...");
			} else {
				printedValue.append(values[i]);
			}
			if (printedValue.length() > 20) {
				break;
			}
		}
		return printedValue.toString();
	}

}