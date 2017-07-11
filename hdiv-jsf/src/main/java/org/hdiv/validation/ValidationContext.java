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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.hdiv.util.UtilsJsf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidationContext {

	private static final Logger log = LoggerFactory.getLogger(ValidationContext.class);

	protected final FacesContext facesContext;

	protected final Map<String, String> requestParameters;

	protected final Map<String, List<String>> paramsWithRowId = new HashMap<String, List<String>>();

	protected final Map<String, Set<Object>> validParameters;

	protected final List<FacesValidatorError> errors;

	public ValidationContext(final FacesContext facesContext) {
		this.facesContext = facesContext;
		requestParameters = facesContext.getExternalContext().getRequestParameterMap();
		validParameters = new HashMap<String, Set<Object>>();
		errors = new ArrayList<FacesValidatorError>();

		initParamsWithRowId(facesContext);
	}

	protected void initParamsWithRowId(final FacesContext facesContext) {

		Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
		for (String paramName : params.keySet()) {

			if (paramName.startsWith("javax.faces")) {
				continue;
			}

			String paramNameNoIndex = UtilsJsf.removeRowId(paramName);
			if (!paramName.equals(paramNameNoIndex)) {

				List<String> previous = paramsWithRowId.get(paramNameNoIndex);
				if (previous == null) {
					previous = new ArrayList<String>();
					paramsWithRowId.put(paramNameNoIndex, previous);
				}
				previous.add(paramName);
			}
		}
	}

	public Map<String, List<String>> getParamsWithRowId() {
		return paramsWithRowId;
	}

	public FacesContext getFacesContext() {
		return facesContext;
	}

	public Map<String, String> getRequestParameters() {
		return requestParameters;
	}

	public void acceptParameterValues(final String parameterName, final Collection<Object> parameterValues) {
		for (Object value : parameterValues) {
			acceptParameter(parameterName, value);
		}
	}

	public void acceptParameter(final String parameterName, final Object parameterValue) {

		if (parameterName == null || parameterValue == null) {
			return;
		}

		if (log.isDebugEnabled()) {
			log.debug("Accepted parameter: " + parameterName + ", " + parameterValue);
		}

		Set<Object> values = validParameters.get(parameterName);
		if (values == null) {
			values = new HashSet<Object>();
			validParameters.put(parameterName, values);
		}
		values.add(parameterValue.toString());
	}

	public void rejectParameter(final String paramName, final String paramErrorValue, final String errorKey,
			final String editableValidationRule, final UIComponent editableComponent) {
		errors.add(new FacesValidatorError(errorKey, null, paramName, paramErrorValue, null, editableValidationRule, editableComponent));
	}

	public void rejectParameter(final String paramName, final String paramErrorValue, final String errorKey) {
		rejectParameter(paramName, paramErrorValue, errorKey, null, null);
	}

	public Map<String, Set<Object>> getValidParameters() {
		return validParameters;
	}

	public List<FacesValidatorError> getErrors() {
		return errors;
	}

	public boolean hasErrors() {
		return !errors.isEmpty();
	}
}
