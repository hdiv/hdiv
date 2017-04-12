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

import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.components.HtmlInputHiddenExtension;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.validation.ValidationContext;

/**
 * Validates component of type HtmlInputHiddenExtension.
 * 
 * @author Gotzon Illarramendi
 */
public class HtmlInputHiddenValidator extends AbstractComponentValidator {

	private static final Log log = LogFactory.getLog(HtmlInputHiddenValidator.class);

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

		Object hiddenValue = parameters.get(inputHidden.getClientId(context));
		Object hiddenStateValue = inputHidden.getStateValue(context, inputHidden.getClientId(context));

		if (log.isDebugEnabled()) {
			log.debug("Hidden's received value:" + hiddenValue);
			log.debug("Hidden's stored value:" + hiddenStateValue);
		}

		if (hiddenStateValue == null) {
			// If the hidden field has not a defined value, a null value is stored.
			// For request validation purpose, it equivalent to empty String
			hiddenStateValue = "";
		}

		if (hiddenValue == null) {

			if (log.isDebugEnabled()) {
				log.debug("Parameter '" + inputHidden.getId() + "' rejected in component '" + inputHidden.getId()
						+ "' in ComponentValidator '" + this.getClass() + "'");
			}
			validationContext.rejectParameter(inputHidden.getId(), null, HDIVErrorCodes.NOT_RECEIVED_ALL_REQUIRED_PARAMETERS);
		}

		boolean correct = hasEqualValue(hiddenValue, hiddenStateValue);
		if (!correct) {

			if (log.isDebugEnabled()) {
				log.debug("Parameter '" + inputHidden.getId() + "' rejected in component '" + inputHidden.getId()
						+ "' in ComponentValidator '" + this.getClass() + "'");
			}
			validationContext.rejectParameter(inputHidden.getId(), hiddenStateValue.toString(), HDIVErrorCodes.INVALID_PARAMETER_VALUE);
		}
		else {
			validationContext.acceptParameter(inputHidden.getId(), hiddenStateValue.toString());
		}

	}

	/**
	 * Return true only if the two objects have the same value.
	 * 
	 * @param hiddenValue component value
	 * @param realValue request value
	 * @return result
	 */
	protected boolean hasEqualValue(Object hiddenValue, Object realValue) {

		if (!(hiddenValue instanceof String)) {
			hiddenValue = hiddenValue.toString();
		}
		if (!(realValue instanceof String)) {
			realValue = realValue.toString();
		}

		return hiddenValue.equals(realValue);
	}

}
