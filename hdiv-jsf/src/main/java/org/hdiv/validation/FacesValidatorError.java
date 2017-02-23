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
package org.hdiv.validation;

import javax.faces.component.UIComponent;

import org.hdiv.filter.ValidatorError;

public class FacesValidatorError extends ValidatorError {

	private UIComponent editableValidationComponent;

	public FacesValidatorError(final String type) {
		super(type);
	}

	public FacesValidatorError(final String type, final String target, final String parameterName, final String parameterValue) {
		super(type, target, parameterName, parameterValue);
	}

	public FacesValidatorError(final String type, final String target, final String parameterName, final String parameterValue,
			final String originalParameterValue, final String validationRuleName) {
		super(type, target, parameterName, parameterValue, originalParameterValue, null, null, null, validationRuleName);
	}

	public FacesValidatorError(final String type, final String target, final String parameterName, final String parameterValue,
			final String originalParameterValue, final String validationRuleName, final UIComponent editableValidationComponent) {
		this(type, target, parameterName, parameterValue, originalParameterValue, validationRuleName);
		this.editableValidationComponent = editableValidationComponent;
	}

	public UIComponent getEditableValidationComponent() {
		return editableValidationComponent;
	}

}
