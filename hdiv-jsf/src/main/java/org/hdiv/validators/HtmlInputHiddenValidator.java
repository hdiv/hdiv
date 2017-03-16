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

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.components.HtmlInputHiddenExtension;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.util.UtilsJsf;
import org.hdiv.validation.ValidationError;

/**
 * Validates component of type HtmlInputHiddenExtension.
 * 
 * @author Gotzon Illarramendi
 */
public class HtmlInputHiddenValidator implements ComponentValidator {

	private static final Log log = LogFactory.getLog(HtmlInputHiddenValidator.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.validators.ComponentValidator#validate(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
	 */
	public ValidationError validate(final FacesContext context, final UIComponent component) {

		HtmlInputHiddenExtension inputHidden = (HtmlInputHiddenExtension) component;
		return validateHiddenComponent(context, inputHidden);
	}

	/**
	 * Validates Hidden component received as input
	 * 
	 * @param context Request context
	 * @param inputHidden component to validate
	 * @return validation result
	 */
	protected ValidationError validateHiddenComponent(final FacesContext context, final HtmlInputHiddenExtension inputHidden) {

		UIData uiDataComp = UtilsJsf.findParentUIData(inputHidden);

		int rowIndex = 0;
		if (uiDataComp != null) {
			rowIndex = uiDataComp.getRowIndex();
		}
		Object hiddenValue;
		Object hiddenRealValue;

		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		if (rowIndex >= 0) {
			// If rowIndex >= 0, current position is a table and hidden's component
			// clientId is correct

			hiddenValue = request.getParameter(inputHidden.getClientId(context));
			hiddenRealValue = inputHidden.getRealValue(inputHidden.getClientId(context));

			if (log.isDebugEnabled()) {
				log.debug("Hidden's value received:" + hiddenValue);
				log.debug("Hidden's value sent to the client:" + hiddenRealValue);
			}

			if (hiddenValue == null) {
				ValidationError error = new ValidationError();
				error.setErrorKey(HDIVErrorCodes.NOT_RECEIVED_ALL_REQUIRED_PARAMETERS);
				error.setErrorParam(inputHidden.getId());
				error.setErrorValue("null");
				error.setErrorComponent(inputHidden.getClientId(context));
				return error;
			}

			boolean correcto = hasEqualValue(hiddenValue, hiddenRealValue);
			if (!correcto) {
				ValidationError error = new ValidationError();
				error.setErrorKey(HDIVErrorCodes.INVALID_PARAMETER_VALUE);
				error.setErrorParam(inputHidden.getId());
				error.setErrorValue(hiddenRealValue.toString());
				error.setErrorComponent(inputHidden.getClientId(context));
				return error;
			}
		}
		else {
			// else, current position isn't a table, but hidden is in a table
			// and its clientId is incorrect
			List<String> clientIds = inputHidden.getClientIds();
			for (int i = 0; i < clientIds.size(); i++) {
				String clientId = clientIds.get(i);
				hiddenValue = request.getParameter(clientId);
				hiddenRealValue = inputHidden.getRealValue(clientId);
				if (log.isDebugEnabled()) {
					log.debug("Hidden's value received:" + hiddenValue);
					log.debug("Hidden's value sent to the client:" + hiddenRealValue);
				}

				if (hiddenValue == null) {
					ValidationError error = new ValidationError();
					error.setErrorKey(HDIVErrorCodes.NOT_RECEIVED_ALL_REQUIRED_PARAMETERS);
					error.setErrorParam(inputHidden.getId());
					error.setErrorValue("null");
					error.setErrorComponent(inputHidden.getClientId(context));
					return error;
				}

				boolean correcto = hiddenValue.equals(hiddenRealValue);
				if (!correcto) {
					ValidationError error = new ValidationError();
					error.setErrorKey(HDIVErrorCodes.INVALID_PARAMETER_VALUE);
					error.setErrorParam(inputHidden.getId());
					error.setErrorValue(hiddenRealValue.toString());
					error.setErrorComponent(inputHidden.getClientId(context));
					return error;
				}
			}

		}

		return null;
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
