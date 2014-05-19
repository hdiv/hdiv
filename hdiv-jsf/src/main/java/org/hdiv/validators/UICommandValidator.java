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
package org.hdiv.validators;

import java.util.Map;

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.components.UIParameterExtension;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.util.UtilsJsf;
import org.hdiv.validation.ValidationError;

/**
 * ComponentValidator that validates parameters of a component of type UICommand,
 * 
 * @author Gotzon Illarramendi
 */
public class UICommandValidator implements ComponentValidator {

	private static Log log = LogFactory.getLog(UICommandValidator.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.validators.ComponentValidator#validate(javax.faces.context.FacesContext,
	 * javax.faces.component.UIComponent)
	 */
	public ValidationError validate(FacesContext context, UIComponent component) {

		UICommand command = (UICommand) component;

		// Search parent components of type UIData
		UIData uiDataComp = UtilsJsf.findParentUIData(command);

		int rowIndex = 0;
		if (uiDataComp != null) {
			rowIndex = uiDataComp.getRowIndex();
		}

		// Check CommandLink's parameters
		for (UIComponent childComp : component.getChildren()) {
			if (childComp instanceof UIParameter) {
				UIParameter param = (UIParameter) childComp;
				ValidationError error = this.processParam(context, param, rowIndex);
				if (error != null) {
					return error;
				}
			}
		}
		return null;

	}

	/**
	 * Validates a parameter of component UICommand
	 * 
	 * @param context
	 *            Request context
	 * @param parameter
	 *            UIParameter component to validate
	 * @param rowIndex
	 *            index that shows where it is the UICommand component inside a UIData
	 * @return validation result
	 */
	private ValidationError processParam(FacesContext context, UIParameter parameter, int rowIndex) {

		UIParameterExtension param = (UIParameterExtension) parameter;

		UIComponent parent = parameter.getParent();
		String parentClientId = parent.getClientId(context);

		Map<String, String> requestMap = context.getExternalContext().getRequestParameterMap();
		String requestValue = requestMap.get(param.getName());

		String realValue;

		if (rowIndex < 0) {

			// May be -1 when commandLink finds a facet in a table,
			// in the footer for example
			// In these cases value has been stored in the 0 position
			rowIndex = 0;
		}
		realValue = param.getValue(parentClientId).toString();

		if (log.isDebugEnabled()) {
			log.debug("requestValue:" + requestValue);
			log.debug("realValue:" + realValue);
		}

		if (requestValue == null) {
			ValidationError error = new ValidationError();
			error.setErrorKey(HDIVErrorCodes.REQUIRED_PARAMETERS);
			error.setErrorParam(param.getId());
			error.setErrorValue(requestValue);
			error.setErrorComponent(param.getClientId(context));
			return error;

		}
		if (!requestValue.equals(realValue)) {
			ValidationError error = new ValidationError();
			error.setErrorKey(HDIVErrorCodes.PARAMETER_VALUE_INCORRECT);
			error.setErrorParam(param.getId());
			error.setErrorValue(requestValue);
			error.setErrorComponent(param.getClientId(context));
			return error;
		}
		return null;
	}

}
