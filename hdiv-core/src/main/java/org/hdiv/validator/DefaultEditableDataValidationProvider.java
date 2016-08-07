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
package org.hdiv.validator;

import java.util.List;

/**
 * Default {@link EditableDataValidationProvider} implementation based on validations defined in hdiv-config.xml file.
 * 
 * @since HDIV 2.1.10
 */
public class DefaultEditableDataValidationProvider implements EditableDataValidationProvider {

	private static final long serialVersionUID = 2276666823731793620L;

	protected ValidationRepository validationRepository;

	/**
	 * <p>
	 * Checks if the values <code>values</code> are valid for the editable parameter <code>parameter</code>, using the validations defined
	 * in the hdiv-config.xml configuration file of Spring.
	 * </p>
	 * 
	 * @param url target url
	 * @param parameter parameter name
	 * @param values parameter's values
	 * @param dataType editable data type
	 * @return True if the values <code>values</code> are valid for the parameter <code>parameter</code>.
	 */
	public EditableDataValidationResult validate(final String url, final String parameter, final String[] values, final String dataType) {

		if (validationRepository == null) {
			return EditableDataValidationResult.VALIDATION_NOT_REQUIRED;
		}

		List<IValidation> validations = validationRepository.findValidations(url, parameter);

		if (validations.isEmpty()) {
			return EditableDataValidationResult.VALIDATION_NOT_REQUIRED;
		}

		for (IValidation currentValidation : validations) {

			if (!currentValidation.validate(parameter, values, dataType)) {

				EditableDataValidationResult result = new EditableDataValidationResult(false, currentValidation.getName());
				return result;
			}
		}
		return EditableDataValidationResult.VALID;
	}

	/**
	 * @param validationRepository the validationRepository to set
	 */
	public void setValidationRepository(final ValidationRepository validationRepository) {
		this.validationRepository = validationRepository;
	}

	/**
	 * @return the validationRepository
	 */
	public ValidationRepository getValidationRepository() {
		return validationRepository;
	}

}
