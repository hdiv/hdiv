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

import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.component.html.HtmlInputSecret;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.util.HDIVUtil;
import org.hdiv.util.MessageFactory;
import org.hdiv.util.UtilsJsf;
import org.hdiv.validation.ValidationContext;
import org.hdiv.validator.EditableDataValidationResult;

/**
 * Responsible for validating that the parameters coming from an editable component (InputText, Textarea, Secret) are logical
 * 
 * @author Ugaitz Urien
 */
public class EditableValidator implements ComponentValidator {

	private static final Log log = LogFactory.getLog(EditableValidator.class);

	/**
	 * HDIV config
	 */
	private HDIVConfig hdivConfig;

	public boolean supports(final UIComponent component) {

		return UIInput.class.isAssignableFrom(component.getClass()) && component.getFamily().equals("javax.faces.Input");
	}

	public void validate(final ValidationContext context, final UIComponent component) {
		validateInput(context, (UIInput) component);
	}

	/**
	 * Configures variables to call validateContent
	 * 
	 * @param validationContext Validation context
	 * @param inputComponent {@link UIInput} to validate
	 */
	protected void validateInput(final ValidationContext validationContext, final UIInput inputComponent) {

		FacesContext context = validationContext.getFacesContext();

		String clientId = inputComponent.getClientId(context);
		Object value = context.getExternalContext().getRequestParameterMap().get(clientId);
		String contentType = null;
		if (inputComponent instanceof HtmlInputHidden) {
			contentType = "hidden";
		}
		else if (inputComponent instanceof HtmlInputTextarea) {
			contentType = "textarea";
		}
		else if (inputComponent instanceof HtmlInputText) {
			contentType = "text";
		}
		else if (inputComponent instanceof HtmlInputSecret) {
			contentType = "password";
		}

		// Parameter value is always accepted, because the component has an editable value
		Converter conv = inputComponent.getConverter();
		String convertedValue = value == null ? "" : value.toString();
		if (conv != null) {
			convertedValue = conv.getAsString(context, inputComponent, value);
		}
		validationContext.acceptParameter(clientId, convertedValue);

		EditableDataValidationResult result = validateContent(context, clientId, value, contentType);
		if (!result.isValid()) {

			// Add message
			FacesMessage msg = createFacesMessage(context, inputComponent);
			context.addMessage(clientId, msg);

			// We can't do this in RestoreState phase. Store the component and do it later.
			// inputComponent.setValid(false);

			if (log.isDebugEnabled()) {
				log.debug("Parameter '" + clientId + "' rejected in component '" + clientId + "' in ComponentValidator '" + this.getClass()
						+ "'");
			}

			validationContext.rejectParameter(clientId, value.toString(), HDIVErrorCodes.EDITABLE_VALIDATION_ERROR,
					result.getValidationId(), inputComponent);
		}
	}

	/**
	 * Uses HdivConfig to validate editable field content
	 * 
	 * @param context Request context
	 * @param clientId clientId value of the component
	 * @param contentObj value of the component
	 * @param contentType type of content
	 * @return is the content valid?
	 */
	protected EditableDataValidationResult validateContent(final FacesContext context, final String clientId, final Object contentObj,
			final String contentType) {
		if (!(contentObj instanceof String)) {
			return EditableDataValidationResult.VALIDATION_NOT_REQUIRED;
		}
		String target = UtilsJsf.getTargetUrl(context);

		String[] content = { (String) contentObj };
		EditableDataValidationResult result = hdivConfig.getEditableDataValidationProvider().validate(target, clientId, content,
				contentType);
		return result;
	}

	/**
	 * Create {@link FacesMessage} for error
	 * 
	 * @param context Request context
	 * @param inputComponent {@link UIInput} to validate
	 * @return FacesMessage
	 */
	protected FacesMessage createFacesMessage(final FacesContext context, final UIInput inputComponent) {

		String clientId = inputComponent.getClientId();

		String label = null;

		if (inputComponent instanceof HtmlInputTextarea) {
			label = ((HtmlInputTextarea) inputComponent).getLabel();
		}
		else if (inputComponent instanceof HtmlInputText) {
			label = ((HtmlInputText) inputComponent).getLabel();
		}
		else if (inputComponent instanceof HtmlInputSecret) {
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
			HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
			msg = HDIVUtil.getMessage(request, Constants.HDIV_EDITABLE_ERROR_KEY, label, locale);
		}

		FacesMessage facesMessage = new FacesMessage(msg);
		facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
		return facesMessage;
	}

	public void setHdivConfig(final HDIVConfig hdivConfig) {
		this.hdivConfig = hdivConfig;
	}
}
