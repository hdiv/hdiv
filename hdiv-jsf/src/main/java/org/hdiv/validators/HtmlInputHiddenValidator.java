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

import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.components.HtmlInputHiddenExtension;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.util.UtilsJsf;
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

		UIData uiDataComp = UtilsJsf.findParentUIData(inputHidden);

		int rowIndex = 0;
		if (uiDataComp != null) {
			rowIndex = uiDataComp.getRowIndex();
		}
		Object hiddenValue;
		Object hiddenRealValue;

		Map<String, String> parameters = context.getExternalContext().getRequestParameterMap();
		if (rowIndex >= 0) {
			// If rowIndex >= 0, current position is a table and hidden's component
			// clientId is correct

			hiddenValue = parameters.get(inputHidden.getClientId(context));
			hiddenRealValue = inputHidden.getRealValue(inputHidden.getClientId(context));

			if (log.isDebugEnabled()) {
				log.debug("Hidden's value received:" + hiddenValue);
				log.debug("Hidden's value sent to the client:" + hiddenRealValue);
			}

			if (hiddenValue == null) {

				if (log.isDebugEnabled()) {
					log.debug("Parameter '" + inputHidden.getId() + "' rejected in component '" + inputHidden.getId()
							+ "' in ComponentValidator '" + this.getClass() + "'");
				}
				validationContext.rejectParameter(inputHidden.getId(), null, HDIVErrorCodes.REQUIRED_PARAMETERS);
			}

			boolean correct = hasEqualValue(hiddenValue, hiddenRealValue);
			if (!correct) {

				if (log.isDebugEnabled()) {
					log.debug("Parameter '" + inputHidden.getId() + "' rejected in component '" + inputHidden.getId()
							+ "' in ComponentValidator '" + this.getClass() + "'");
				}
				validationContext.rejectParameter(inputHidden.getId(), hiddenRealValue.toString(),
						HDIVErrorCodes.PARAMETER_VALUE_INCORRECT);
			}
			else {
				validationContext.acceptParameter(inputHidden.getId(), hiddenRealValue.toString());
			}
		}
		else {
			// else, current position isn't a table, but hidden is in a table
			// and its clientId is incorrect
			List<String> clientIds = inputHidden.getClientIds();
			for (int i = 0; i < clientIds.size(); i++) {
				String clientId = clientIds.get(i);
				hiddenValue = parameters.get(clientId);
				hiddenRealValue = inputHidden.getRealValue(clientId);
				if (log.isDebugEnabled()) {
					log.debug("Hidden's value received:" + hiddenValue);
					log.debug("Hidden's value sent to the client:" + hiddenRealValue);
				}

				if (hiddenValue == null) {

					if (log.isDebugEnabled()) {
						log.debug("Parameter '" + inputHidden.getId() + "' rejected in component '" + inputHidden.getId()
								+ "' in ComponentValidator '" + this.getClass() + "'");
					}
					validationContext.rejectParameter(inputHidden.getId(), null, HDIVErrorCodes.REQUIRED_PARAMETERS);
				}

				boolean correct = hiddenValue.equals(hiddenRealValue);
				if (!correct) {

					if (log.isDebugEnabled()) {
						log.debug("Parameter '" + inputHidden.getId() + "' rejected in component '" + inputHidden.getId()
								+ "' in ComponentValidator '" + this.getClass() + "'");
					}
					validationContext.rejectParameter(inputHidden.getId(), hiddenRealValue.toString(),
							HDIVErrorCodes.PARAMETER_VALUE_INCORRECT);
				}
				else {
					validationContext.acceptParameter(inputHidden.getId(), hiddenRealValue.toString());
				}
			}

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
