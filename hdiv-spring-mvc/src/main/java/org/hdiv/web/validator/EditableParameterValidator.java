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
package org.hdiv.web.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;

/**
 * Validation to visualize the errors produced in the editable fields detected by HDIV.
 * 
 * @author Gorka Vicente
 * @author Gotzon Illarramendi
 * @since HDIV 2.0.6
 */
public class EditableParameterValidator extends AbstractEditableParameterValidator implements SmartValidator {

	/**
	 * Wrapped validator.
	 */
	private Validator innerValidator;

	public boolean supports(Class<?> clazz) {
		return true;
	}

	public void validate(Object obj, Errors errors) {
		super.validateEditableParameters(errors);

		// If Hdiv validation OK, delegate to inner validation
		if (!errors.hasErrors() && this.innerValidator != null) {
			this.innerValidator.validate(obj, errors);
		}
	}
	
	public void validate(Object obj, Errors errors, Object... hints) {
		validate(obj, errors);
	}

	/**
	 * @param innerValidator
	 *            the innerValidator to set
	 */
	public void setInnerValidator(Validator innerValidator) {
		this.innerValidator = innerValidator;
	}

}