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
package org.hdiv.web.validator;

import java.util.List;

import org.hdiv.filter.ValidatorError;
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
	 * @param errors errors detected by HDIV during the validation process of the editable parameters.
	 */
	@SuppressWarnings("unchecked")
	protected void validateEditableParameters(Errors errors) {

		RequestAttributes attr = RequestContextHolder.getRequestAttributes();
		if (attr == null) {
			// This is not a web request
			return;
		}

		List<ValidatorError> validationErrors = (List<ValidatorError>) attr.getAttribute(
				Constants.EDITABLE_PARAMETER_ERROR, 0);
		if (validationErrors != null && validationErrors.size() > 0) {

			for (ValidatorError error : validationErrors) {

				this.rejectParamValues(error.getParameterName(), error.getParameterValue(), errors);
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

		List<ValidatorError> validationErrors = (List<ValidatorError>) attr.getAttribute(
				Constants.EDITABLE_PARAMETER_ERROR, 0);
		if (validationErrors != null && validationErrors.size() > 0) {

			ValidatorError paramError = null;
			for (ValidatorError error : validationErrors) {
				if (error.getParameterName().equals(param)) {
					paramError = error;
				}
			}
			if (paramError != null) {

				this.rejectParamValues(paramError.getParameterName(), paramError.getParameterValue(), errors);
			}
		}
	}

	protected void rejectParamValues(String param, String paramValues, Errors errors) {

		if ((paramValues.contains(Constants.HDIV_EDITABLE_PASSWORD_ERROR_KEY))) {

			errors.rejectValue(param, Constants.HDIV_EDITABLE_PASSWORD_ERROR_KEY);

		}
		else {
			String printedValue = this.createMessageError(paramValues);
			errors.rejectValue(param, Constants.HDIV_EDITABLE_ERROR_KEY, new String[] { printedValue }, printedValue
					+ " has not allowed characters");
		}
	}

	/**
	 * It creates the message error from the values <code>values</code>.
	 * 
	 * @param paramValues values with not allowed characters
	 * @return message error to show
	 */
	protected String createMessageError(String paramValues) {

		String[] values = paramValues.split(",");
		StringBuilder printedValue = new StringBuilder();

		for (int i = 0; i < values.length; i++) {

			if (i > 0) {
				printedValue.append(", ");
			}
			if (values[i].length() > 20) {
				printedValue.append(values[i].substring(0, 20) + "...");
			}
			else {
				printedValue.append(values[i]);
			}
			if (printedValue.length() > 20) {
				break;
			}
		}
		return printedValue.toString();
	}

}