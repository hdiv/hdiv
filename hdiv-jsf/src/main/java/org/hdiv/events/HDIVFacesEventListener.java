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
package org.hdiv.events;

import java.io.IOException;

import javax.faces.component.StateHolder;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;
import javax.faces.event.FacesListener;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.exception.StateValidationException;
import org.hdiv.filter.ValidatorError;
import org.hdiv.logs.Logger;
import org.hdiv.util.HDIVUtil;
import org.hdiv.validation.ValidationError;
import org.hdiv.validators.ComponentValidator;
import org.hdiv.validators.EditableValidator;

/**
 * <p>
 * Listener that processes a HDIV event. This class validates the component tree searching for modifications in the
 * values of the non editable data.
 * </p>
 * <p>
 * Validation logic for each type of component is stored in a separate class that implements ComponentValidator.
 * </p>
 * <p>
 * Implements StateHolder interface to set it as transient and don't store it in HDIV state.
 * </p>
 * 
 * @author Gotzon Illarramendi
 */
public class HDIVFacesEventListener implements FacesListener, StateHolder {

	private static Log log = LogFactory.getLog(HDIVFacesEventListener.class);

	/**
	 * Parameter validator
	 */
	private ComponentValidator requestParamValidator;

	/**
	 * UICommand components validator
	 */
	private ComponentValidator uiCommandValidator;

	/**
	 * HtmlInputHidden components validator
	 */
	private ComponentValidator htmlInputHiddenValidator;

	/**
	 * Editable data validator
	 */
	private EditableValidator editableValidator;

	/**
	 * HDIV config
	 */
	private HDIVConfig config;

	/**
	 * Hdiv attack logger
	 */
	private Logger logger;

	/**
	 * Process a HDIVFacesEvent event
	 * 
	 * @param facesEvent
	 *            Evento de HDIV
	 */
	public void processListener(HDIVFacesEvent facesEvent) {

		if (log.isDebugEnabled()) {
			log.debug("Processing HDIV event:" + facesEvent);
		}

		FacesContext context = FacesContext.getCurrentInstance();

		UICommand eventComp = (UICommand) facesEvent.getComponent();

		// Search form component
		UIForm form = this.findParentForm(eventComp);

		// Validate request parameters
		ValidationError error = this.requestParamValidator.validate(context, form);
		if (error != null) {
			this.log(context, error);
			this.forwardToErrorPage(context, eventComp);
		}

		// Validate component parameters
		error = this.uiCommandValidator.validate(context, eventComp);
		if (error != null) {
			this.log(context, error);
			this.forwardToErrorPage(context, eventComp);
		}

		// Validate all the hidden components in the form
		error = this.validateHiddens(context, form);
		if (error != null) {
			this.log(context, error);
			this.forwardToErrorPage(context, eventComp);
		}

		error = this.editableValidator.validate(context, form);
		if (error != null) {
			this.log(context, error);
		}
	}

	/**
	 * Searches the form inside the component. Input component must be UICommand type and must be inside a form.
	 * 
	 * @param comp
	 *            Base component
	 * @return UIForm component
	 */

	private UIForm findParentForm(UIComponent comp) {

		UIComponent parent = comp.getParent();
		while (!(parent instanceof UIForm)) {
			parent = parent.getParent();
		}
		return (UIForm) parent;

	}

	/**
	 * Validates HtmlInputHidden components inside the form
	 * 
	 * @param context
	 *            Request context
	 * @param component
	 *            UIForm component
	 * @return validation result
	 */
	private ValidationError validateHiddens(FacesContext context, UIComponent component) {

		for (UIComponent uicomponent : component.getChildren()) {
			if (uicomponent instanceof HtmlInputHidden) {

				HtmlInputHidden hidden = (HtmlInputHidden) uicomponent;
				ValidationError error = this.htmlInputHiddenValidator.validate(context, hidden);
				if (error != null) {
					return error;
				}
			} else {
				ValidationError error = validateHiddens(context, uicomponent);
				if (error != null) {
					return error;
				}
			}
		}
		return null;
	}

	/**
	 * Redirects the execution to the HDIV error page
	 * 
	 * @param context
	 *            Request context
	 * @param comp
	 *            component which throws the event
	 */
	private void forwardToErrorPage(FacesContext context, UICommand comp) {
		if (!comp.isImmediate()) {
			// Redirect to Hdiv errors page
			try {
				String contextPath = context.getExternalContext().getRequestContextPath();
				context.getExternalContext().redirect(contextPath + this.config.getErrorPage());
			} catch (IOException e) {
				throw new StateValidationException();
			}
		} else {
			// Previous strategy doesn't work with immediate components because
			// the execution of business logic continues running-
			// An exception is thrown to be catched by the ExceptionHandler
			// (JSF2)
			throw new StateValidationException();
		}

	}

	/**
	 * Helper method to write an attack in the log
	 * 
	 * @param context
	 *            Request context
	 * @param error
	 *            validation result
	 */
	private void log(FacesContext context, ValidationError error) {

		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

		ValidatorError errorData = new ValidatorError(error.getErrorKey(), HDIVUtil.getRequestURI(request),
				error.getErrorParam(), error.getErrorValue());
		this.logger.log(errorData);
	}

	/**
	 * It is set as transient to avoid storing in the JSF state
	 */
	public boolean isTransient() {

		return true;
	}

	/**
	 * As the listener is transient this method isn't called
	 */
	public void setTransient(boolean newTransientValue) {

	}

	/**
	 * As the listener is transient this method isn't called
	 */
	public Object saveState(FacesContext context) {

		return null;
	}

	/**
	 * As the listener is transient this method isn't called
	 */
	public void restoreState(FacesContext context, Object state) {

	}

	/**
	 * @param requestParamValidator
	 *            the requestParamValidator to set
	 */
	public void setRequestParamValidator(ComponentValidator requestParamValidator) {
		this.requestParamValidator = requestParamValidator;
	}

	/**
	 * @param uiCommandValidator
	 *            the uiCommandValidator to set
	 */
	public void setUiCommandValidator(ComponentValidator uiCommandValidator) {
		this.uiCommandValidator = uiCommandValidator;
	}

	/**
	 * @param htmlInputHiddenValidator
	 *            the htmlInputHiddenValidator to set
	 */
	public void setHtmlInputHiddenValidator(ComponentValidator htmlInputHiddenValidator) {
		this.htmlInputHiddenValidator = htmlInputHiddenValidator;
	}

	/**
	 * @param editableValidator
	 *            the editableValidator to set
	 */
	public void setEditableValidator(EditableValidator editableValidator) {
		this.editableValidator = editableValidator;
	}

	/**
	 * @param config
	 *            the config to set
	 */
	public void setConfig(HDIVConfig config) {
		this.config = config;
	}

	/**
	 * @param logger
	 *            the logger to set
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

}