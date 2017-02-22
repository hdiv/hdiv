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
import java.util.Map;

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.components.UIParameterExtension;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.validation.ValidationContext;

/**
 * ComponentValidator that validates parameters of a component of type UICommand,
 * 
 * @author Gotzon Illarramendi
 */
public class UICommandValidator extends AbstractComponentValidator {

	private static final Log log = LogFactory.getLog(UICommandValidator.class);

	public UICommandValidator() {
		super(UICommand.class);
	}

	public void validate(final ValidationContext validationContext, final UIComponent component) {

		UICommand command = (UICommand) component;

		if (!wasClicked(validationContext.getFacesContext(), command)) {
			// Only validate the executed command
			return;
		}

		validateUICommand(validationContext, command);
	}

	// TODO add myfaces support, the parameter is different
	protected boolean wasClicked(final FacesContext facesContext, final UICommand command) {

		String clientId = command.getClientId(facesContext);
		String value = facesContext.getExternalContext().getRequestParameterMap().get(clientId);
		if (value != null && (value.equals(clientId) || value.equals(command.getValue()))) {
			return true;
		}

		PartialViewContext partialContext = facesContext.getPartialViewContext();
		if (partialContext != null && partialContext.isPartialRequest()) {
			// Is an ajax call partially processing the component tree
			Collection<String> execIds = partialContext.getExecuteIds();
			return execIds.contains(clientId);
		}

		return false;
	}

	protected void validateUICommand(final ValidationContext validationContext, final UICommand command) {

		validationContext.acceptParameter(command.getClientId(validationContext.getFacesContext()), command.getValue());

		// Check CommandLink's parameters
		for (UIComponent childComp : command.getChildren()) {
			if (childComp instanceof UIParameter) {
				UIParameter param = (UIParameter) childComp;
				processParam(validationContext, param);
			}
		}
	}

	/**
	 * Validates a parameter of component UICommand
	 * 
	 * @param context Request context
	 * @param parameter UIParameter component to validate
	 * @return validation result
	 */
	private void processParam(final ValidationContext validationContext, final UIParameter parameter) {

		FacesContext context = validationContext.getFacesContext();

		UIParameterExtension param = (UIParameterExtension) parameter;

		UIComponent parent = parameter.getParent();
		String parentClientId = parent.getClientId(context);

		Map<String, String> requestMap = context.getExternalContext().getRequestParameterMap();
		String requestValue = requestMap.get(param.getName());

		String realValue = param.getValue(parentClientId).toString();

		if (log.isDebugEnabled()) {
			log.debug("UIParameter requestValue:" + requestValue);
			log.debug("UIParameter realValue:" + realValue);
		}

		if (requestValue != null && requestValue.equals(realValue)) {
			validationContext.acceptParameter(param.getName(), requestValue);
		}
		else if (requestValue == null) {

			if (log.isDebugEnabled()) {
				log.debug("Parameter '" + param.getName() + "' rejected in component '" + param.getClientId(context)
						+ "' in ComponentValidator '" + this.getClass() + "'");
			}
			validationContext.rejectParameter(param.getName(), requestValue, HDIVErrorCodes.REQUIRED_PARAMETERS);
		}
		else {

			if (log.isDebugEnabled()) {
				log.debug("Parameter '" + param.getName() + "' rejected in component '" + param.getClientId(context)
						+ "' in ComponentValidator '" + this.getClass() + "'");
			}
			validationContext.rejectParameter(param.getName(), requestValue, HDIVErrorCodes.PARAMETER_VALUE_INCORRECT,
					param.getClientId(context));
		}
	}
}