/**
 * Copyright 2005-2010 hdiv.org
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

package org.hdiv.webflow.validator;

import java.util.Hashtable;
import java.util.Iterator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.context.ExternalContextHolder;

/**
 * Validation to visualize the errors produced in the editable fields detected
 * by HDIV.
 * 
 * @author Gorka Vicente
 * @since HDIV 2.0.3
 */
public class EditableParameterValidator implements Validator {

	/**
	 * The request attributes key under HDIV should store errors produced in the
	 * editable fields.
	 */
	private static final String EDITABLE_PARAMETER_ERROR = "org.hdiv.action.EDITABLE_PARAMETER_ERROR";

	/**
	 * Property that contains the error message to be shown when the value 
	 * of the editable parameter is not valid.
	 */
	private static final String HDIV_EDITABLE_ERROR = "hdiv.editable.error";

	/**
	 * Property that contains the error message to be shown when the value 
	 * of the editable password parameter is not valid.
	 */
	private static final String HDIV_EDITABLE_PASSWORD_ERROR = "hdiv.editable.password.error";

	public boolean supports(Class arg0) {
		return true;
	}

	public void validate(Object obj, Errors errors) {
		validateEditableParameters(obj, errors);
	}

	/**
	 * Obtains the errors from request detected by HDIV during the validation
	 * process of the editable parameters.
	 * 
	 * @param formObject form object
	 * @param errors errors detected by HDIV during the validation process of
	 *            the editable parameters.
	 */
	public void validateEditableParameters(Object formObject, Errors errors) {

		ExternalContext externalContext = ExternalContextHolder.getExternalContext();
		Hashtable editableParameters = 
			(Hashtable) externalContext.getRequestMap().get(EDITABLE_PARAMETER_ERROR);

		if ((editableParameters != null) && (editableParameters.size() > 0)) {

			for (Iterator it = editableParameters.keySet().iterator(); it.hasNext();) {

				String currentParameter = (String) it.next();
				String[] currentUnauthorizedValues = (String[]) editableParameters.get(currentParameter);

				if ((currentUnauthorizedValues.length == 1)
						&& (currentUnauthorizedValues[0].equals(HDIV_EDITABLE_PASSWORD_ERROR))) {

					errors.rejectValue(currentParameter, HDIV_EDITABLE_PASSWORD_ERROR);

				} else {
					String printedValue = this.createMessageError(currentUnauthorizedValues);
					errors.rejectValue(currentParameter, HDIV_EDITABLE_ERROR, new String[] { printedValue },
							printedValue + " has not allowed characters");
				}
			}
		}
	}

	/**
	 * It creates the message error from the values <code>values</code>.
	 * 
	 * @param values values with not allowed characters
	 * @return message error to show
	 */
	public String createMessageError(String[] values) {

		StringBuffer printedValue = new StringBuffer();

		for (int i = 0; i < values.length; i++) {

			if (i > 0)
				printedValue.append(", ");
			if (values[i].length() > 20)
				printedValue.append(values[i].substring(0, 20) + "...");
			else
				printedValue.append(values[i]);

			if (printedValue.length() > 20)
				break;
		}
		return printedValue.toString();
	}

}