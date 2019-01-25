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
package org.hdiv.validators;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;

import org.hdiv.components.HtmlInputHiddenExtension;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.validation.ValidationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validates component of type HtmlInputHiddenExtension.
 * 
 * @author Gotzon Illarramendi
 */
public class HtmlInputHiddenValidator extends AbstractComponentValidator {

	private static final Logger log = LoggerFactory.getLogger(HtmlInputHiddenValidator.class);

	public HtmlInputHiddenValidator() {
		super(HtmlInputHidden.class);
	}

	public void validate(final ValidationContext context, final UIComponent component) {

		HtmlInputHiddenExtension inputHidden = (HtmlInputHiddenExtension) component;
		validateHiddenComponent(context, inputHidden);
	}

	/**
	 * Validates Hidden component received as input
	 * 
	 * @param validationContext Validation context
	 * @param inputHidden component to validate
	 */
	protected void validateHiddenComponent(final ValidationContext validationContext, final HtmlInputHiddenExtension inputHidden) {

		FacesContext context = validationContext.getFacesContext();

		Map<String, String> parameters = context.getExternalContext().getRequestParameterMap();
		String clientId = inputHidden.getClientId(context);
		String requestValue = parameters.get(clientId);
		Collection<Object> stateValues = inputHidden.getStateValue(context, clientId);

		if (log.isDebugEnabled()) {
			log.debug("Hidden's request value:" + requestValue);
			log.debug("Hidden's state values:" + stateValues);
		}

		if (requestValue != null && isCorrectValue(requestValue, stateValues)) {
			validationContext.acceptParameter(clientId, requestValue);
		}
		else if (requestValue == null) {

			if (log.isDebugEnabled()) {
				log.debug("Parameter '" + clientId + "' rejected in component '" + inputHidden.getId() + "' in ComponentValidator '"
						+ this.getClass() + "'");
			}
			validationContext.rejectParameter(clientId, null, HDIVErrorCodes.NOT_RECEIVED_ALL_REQUIRED_PARAMETERS);
		}
		else {

			if (log.isDebugEnabled()) {
				log.debug("Parameter '" + clientId + "' rejected in component '" + inputHidden.getId() + "' in ComponentValidator '"
						+ this.getClass() + "'");
			}
			validationContext.rejectParameter(clientId, requestValue.toString(), HDIVErrorCodes.INVALID_PARAMETER_VALUE);
		}

	}

	protected boolean isCorrectValue(final String requestValue, final Collection<Object> stateValues) {

		Iterator<Object> it = stateValues.iterator();
		while (it.hasNext()) {
			Object value = it.next();

			if (value == null) {
				// If the hidden field has not a defined value, a null value is stored.
				// For request validation purpose, it equivalent to empty String
				value = "";
			}

			if (value.toString().equals(requestValue)) {
				return true;
			}
		}
		return false;
	}

}
