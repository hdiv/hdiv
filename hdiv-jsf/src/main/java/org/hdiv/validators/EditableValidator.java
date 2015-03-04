/**
 * Copyright 2005-2015 hdiv.org
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

import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.component.html.HtmlInputSecret;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.hdiv.config.HDIVConfig;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.util.HDIVUtil;
import org.hdiv.util.MessageFactory;
import org.hdiv.validation.ValidationError;
import org.hdiv.validator.EditableDataValidationResult;

/**
 * Responsible for validating that the parameters coming from an editable component (InputText, Textarea, Secret) are
 * logical
 * 
 * @author Ugaitz Urien
 */
public class EditableValidator implements ComponentValidator {

	/**
	 * HDIV config
	 */
	private HDIVConfig hdivConfig;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.validators.ComponentValidator#validate(javax.faces.context.FacesContext,
	 * javax.faces.component.UIComponent)
	 */
	public ValidationError validate(FacesContext context, UIComponent component) {

		UIForm form = (UIForm) component;
		ValidationError error = this.validateEditablesForm(context, form);
		return error;
	}

	/**
	 * Validates all the editable components of the form
	 * 
	 * @param context
	 *            Request context
	 * @param formComponent
	 *            UIForm component to validate
	 * @return result
	 */
	protected ValidationError validateEditablesForm(FacesContext context, UIForm formComponent) {
		ValidationError error = null;

		for (UIComponent component : formComponent.getChildren()) {
			ValidationError tempError = this.validateComponent(context, component);
			if (tempError != null) {
				error = tempError;
			}
		}
		return error;
	}

	/**
	 * Recursive method. When a component is non editable, verifies its children.
	 * 
	 * @param context
	 *            Request context
	 * @param uiComponent
	 *            UIComponent to validate
	 * @return result
	 */
	protected ValidationError validateComponent(FacesContext context, UIComponent uiComponent) {
		if ((uiComponent instanceof HtmlInputText) || (uiComponent instanceof HtmlInputTextarea)
				|| (uiComponent instanceof HtmlInputSecret) || (uiComponent instanceof HtmlInputHidden)) {
			UIInput inputComponent = (UIInput) uiComponent;
			return validateInput(context, inputComponent);
		} else {
			ValidationError error = null;
			for (UIComponent child : uiComponent.getChildren()) {
				ValidationError tempError = validateComponent(context, child);
				if (tempError != null) {
					error = tempError;
				}
			}
			return error;
		}
	}

	/**
	 * Configures variables to call validateContent
	 * 
	 * @param context
	 *            Request context
	 * @param inputComponent
	 *            {@link UIInput} to validate
	 * @return result
	 */
	protected ValidationError validateInput(FacesContext context, UIInput inputComponent) {

		Object value = inputComponent.getValue();
		String clientId = inputComponent.getClientId(context);
		String contentType = null;
		if (inputComponent instanceof HtmlInputHidden) {
			contentType = "hidden";
		} else if (inputComponent instanceof HtmlInputTextarea) {
			contentType = "textarea";
		} else if (inputComponent instanceof HtmlInputText) {
			contentType = "text";
		} else if (inputComponent instanceof HtmlInputSecret) {
			contentType = "password";
		}

		if (!this.validateContent(context, clientId, value, contentType)) {

			// Add message
			FacesMessage msg = this.createFacesMessage(context, inputComponent);
			context.addMessage(clientId, msg);

			inputComponent.setValid(false);

			return new ValidationError(HDIVErrorCodes.EDITABLE_VALIDATION_ERROR, null, clientId, value.toString());
		}
		return null;
	}

	/**
	 * Uses HdivConfig to validate editable field content
	 * 
	 * @param context
	 *            Request context
	 * @param clientId
	 *            clientId value of the component
	 * @param contentObj
	 *            value of the component
	 * @param contentType
	 *            type of content
	 * @return is the content valid?
	 */
	protected boolean validateContent(FacesContext context, String clientId, Object contentObj, String contentType) {
		if (!(contentObj instanceof String)) {
			return true;
		}
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		String target = HDIVUtil.getRequestURI(request);
		String targetWithoutContextPath = getTargetWithoutContextPath(request, target);

		String[] content = { (String) contentObj };
		EditableDataValidationResult result = this.hdivConfig.areEditableParameterValuesValid(targetWithoutContextPath,
				clientId, content, contentType);
		return result.isValid();
	}

	/**
	 * Removes the target's ContextPath part
	 * 
	 * @param request
	 *            HttpServletRequest to validate
	 * @param target
	 *            target to strip the ContextPath
	 * @return target without the ContextPath
	 */
	protected String getTargetWithoutContextPath(HttpServletRequest request, String target) {
		String targetWithoutContextPath = target.substring(request.getContextPath().length());
		return targetWithoutContextPath;
	}

	/**
	 * Create {@link FacesMessage} for error
	 * 
	 * @param context
	 *            Request context
	 * @param inputComponent
	 *            {@link UIInput} to validate
	 * @return FacesMessage
	 */
	protected FacesMessage createFacesMessage(FacesContext context, UIInput inputComponent) {

		String clientId = inputComponent.getClientId();

		String label = null;

		if (inputComponent instanceof HtmlInputTextarea) {
			label = ((HtmlInputTextarea) inputComponent).getLabel();
		} else if (inputComponent instanceof HtmlInputText) {
			label = ((HtmlInputText) inputComponent).getLabel();
		} else if (inputComponent instanceof HtmlInputSecret) {
			label = ((HtmlInputSecret) inputComponent).getLabel();
		}

		label = label != null ? label : clientId;

		// First, use component own message
		String msg = inputComponent.getValidatorMessage();

		if (msg == null) {

			// Search in JSF resource bundle
			Object[] params = { label };

			FacesMessage facesMessage = MessageFactory.getMessage(Constants.HDIV_EDITABLE_ERROR_KEY, params);
			if (facesMessage != null) {
				return facesMessage;
			}
		}

		if (msg == null) {

			// Use Hdiv core message
			Locale locale = context.getViewRoot().getLocale();
			msg = HDIVUtil.getMessage(Constants.HDIV_EDITABLE_ERROR_KEY, label, locale);
		}

		FacesMessage facesMessage = new FacesMessage(msg);
		facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
		return facesMessage;
	}

	public void setHdivConfig(HDIVConfig hdivConfig) {
		this.hdivConfig = hdivConfig;
	}
}
