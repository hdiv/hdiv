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
import javax.faces.component.UIData;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.components.UIParameterExtension;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.util.UtilsJsf;
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

		Clicked clicked = wasClicked(validationContext.getFacesContext(), command);
		if (!clicked.isClicked()) {
			// Only validate the executed command
			return;
		}

		validateUICommand(validationContext, command, clicked);
	}

	// TODO add myfaces support, the parameter is different
	protected Clicked wasClicked(final FacesContext facesContext, final UICommand command) {

		String clientId = command.getClientId(facesContext);
		String value = facesContext.getExternalContext().getRequestParameterMap().get(clientId);
		if (value != null && (value.equals(clientId) || value.equals(command.getValue()))) {
			return new Clicked(true, clientId);
		}

		Clicked clicked = wasComponentWithRowIdClicked(facesContext, command, clientId);
		if (clicked.isClicked()) {
			return clicked;
		}

		PartialViewContext partialContext = facesContext.getPartialViewContext();
		if (partialContext != null && partialContext.isPartialRequest()) {
			// Is an ajax call partially processing the component tree
			Collection<String> execIds = partialContext.getExecuteIds();
			return new Clicked(execIds.contains(clientId));
		}

		return new Clicked(false);
	}

	/**
	 * If the UICommand component is inside a UIData component can have an index in the name. <br>
	 * For example: form:pets:1:button
	 */
	protected Clicked wasComponentWithRowIdClicked(final FacesContext facesContext, final UICommand command, final String clientId) {

		UIData uiData = UtilsJsf.findParentUIData(command);
		if (uiData == null) {
			return new Clicked(false);
		}

		Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
		for (String paramName : params.keySet()) {

			if (paramName.startsWith("javax.faces")) {
				continue;
			}

			String paramNameNoIndex = UtilsJsf.removeRowId(paramName);
			if (clientId.equals(paramNameNoIndex) && paramName.equals(params.get(paramName))) {
				return new Clicked(true, paramName);
			}
		}
		return new Clicked(false);
	}

	protected void validateUICommand(final ValidationContext validationContext, final UICommand command, final Clicked clicked) {

		validationContext.acceptParameter(command.getClientId(validationContext.getFacesContext()), command.getValue());

		if (clicked.getParamName() != null) {
			validationContext.acceptParameter(clicked.getParamName(),
					validationContext.getFacesContext().getExternalContext().getRequestParameterMap().get(clicked.getParamName()));
		}

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
			validationContext.rejectParameter(param.getName(), requestValue, HDIVErrorCodes.NOT_RECEIVED_ALL_REQUIRED_PARAMETERS);
		}
		else {

			if (log.isDebugEnabled()) {
				log.debug("Parameter '" + param.getName() + "' rejected in component '" + param.getClientId(context)
						+ "' in ComponentValidator '" + this.getClass() + "'");
			}
			validationContext.rejectParameter(param.getName(), requestValue, HDIVErrorCodes.INVALID_PARAMETER_VALUE);
		}
	}

	public static class Clicked {

		private final boolean clicked;

		private final String paramName;

		public Clicked(final boolean clicked) {
			this.clicked = clicked;
			paramName = null;
		}

		public Clicked(final boolean clicked, final String paramName) {
			this.clicked = clicked;
			this.paramName = paramName;
		}

		public boolean isClicked() {
			return clicked;
		}

		public String getParamName() {
			return paramName;
		}

	}
}