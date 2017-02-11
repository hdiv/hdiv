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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;

import org.hdiv.filter.ValidatorError;

public class ValidationContext {

	private final FacesContext facesContext;

	private final Map<String, String> requestParameters;

	private final Map<String, Set<Object>> validParameters;

	private final List<ValidatorError> errors;

	public ValidationContext(final FacesContext facesContext) {
		this.facesContext = facesContext;
		errors = new ArrayList<ValidatorError>();
		requestParameters = facesContext.getExternalContext().getRequestParameterMap();
		validParameters = new HashMap<String, Set<Object>>();
	}

	public FacesContext getFacesContext() {
		return facesContext;
	}

	public Map<String, String> getRequestParameters() {
		return requestParameters;
	}

	public void acceptParameter(final String parameterName, final Object parameterValue) {

		Set<Object> values = validParameters.get(parameterName);
		if (values == null) {
			values = new HashSet<Object>();
			values.add(parameterValue);
			validParameters.put(parameterName, values);
		}
		else {
			values.add(parameterValue);
		}
	}

	public void rejectParameter(final String paramName, final String paramErrorValue, final String errorKey,
			final String editableValidationRule) {
		errors.add(new ValidatorError(errorKey, null, paramName, paramErrorValue, null, editableValidationRule));
	}

	public void rejectParameter(final String paramName, final String paramErrorValue, final String errorKey) {
		rejectParameter(paramName, paramErrorValue, errorKey, null);
	}

	public Map<String, Set<Object>> getValidParameters() {
		return validParameters;
	}

	public List<ValidatorError> getErrors() {
		return errors;
	}

	public boolean hasErrors() {
		return !errors.isEmpty();
	}
}
