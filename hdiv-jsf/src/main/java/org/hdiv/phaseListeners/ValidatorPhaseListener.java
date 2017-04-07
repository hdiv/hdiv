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
package org.hdiv.phaseListeners;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.filter.ValidatorError;
import org.hdiv.filter.ValidatorErrorHandler;
import org.hdiv.logs.Logger;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.util.HDIVUtil;
import org.hdiv.validation.ComponentTreeValidator;
import org.hdiv.validation.FacesValidatorError;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

/**
 * {@link PhaseListener} that verifies that Hdiv validation is executed and is valid. Otherwise, the request will be stopped.
 */
public class ValidatorPhaseListener implements PhaseListener {

	private static final long serialVersionUID = -5951308353665763734L;

	private static final Log log = LogFactory.getLog(ValidatorPhaseListener.class);

	private static final String VALIDATION_ERRORS_ATTR_NAME = "VALIDATION_ERRORS_ATTR_NAME";

	private ComponentTreeValidator componentTreeValidator;

	private HDIVConfig config;

	private Logger logger;

	private ValidatorErrorHandler validatorErrorHandler;

	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

	public void beforePhase(final PhaseEvent event) {

		if (logger == null) {

			if (log.isDebugEnabled()) {
				log.debug("Initialize ValidationStatusPhaseListener dependencies.");
			}

			WebApplicationContext wac = FacesContextUtils.getRequiredWebApplicationContext(event.getFacesContext());
			validatorErrorHandler = wac.getBean(ValidatorErrorHandler.class);
			componentTreeValidator = wac.getBean(ComponentTreeValidator.class);
			config = wac.getBean(HDIVConfig.class);
			logger = wac.getBean(Logger.class);
		}

		if (event.getPhaseId().equals(PhaseId.PROCESS_VALIDATIONS)) {

			@SuppressWarnings("unchecked")
			List<FacesValidatorError> errors = (List<FacesValidatorError>) event.getFacesContext().getAttributes()
					.get(VALIDATION_ERRORS_ATTR_NAME);
			if (errors != null) {
				for (FacesValidatorError error : errors) {
					if (error.getType().equals(HDIVErrorCodes.INVALID_EDITABLE_VALUE)) {
						UIComponent comp = error.getEditableValidationComponent();
						if (comp instanceof UIInput) {
							((UIInput) comp).setValid(false);
						}
						else {
							log.info("Can't set validity to false on component: " + comp.getClientId(event.getFacesContext()));
						}
					}
				}
			}
		}
	}

	public void afterPhase(final PhaseEvent event) {

		if (event.getPhaseId().equals(PhaseId.RESTORE_VIEW)) {

			FacesContext context = event.getFacesContext();

			if (!context.isPostback()) {
				// Don't validate a request if it is not a postback
				return;
			}

			List<FacesValidatorError> errors = null;
			try {
				errors = componentTreeValidator.validateComponentTree(context);
			}
			catch (Exception e) {
				if (log.isErrorEnabled()) {
					log.error("Error in component tree validation.", e);
				}
			}

			context.getAttributes().put(VALIDATION_ERRORS_ATTR_NAME, errors);

			log(context, errors);
			if (mustStopRequest(errors)) {
				forwardToErrorPage(context, errors);
			}
		}
	}

	protected boolean mustStopRequest(final List<FacesValidatorError> errors) {

		if (config.isDebugMode()) {
			return false;
		}

		if (errors != null && !errors.isEmpty()) {
			for (ValidatorError error : errors) {
				if (!error.getType().equals(HDIVErrorCodes.INVALID_EDITABLE_VALUE)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Redirects the execution to the HDIV error page
	 * 
	 * @param context Request context
	 * @param validatorErrors validation error data
	 */
	protected void forwardToErrorPage(final FacesContext context, final List<FacesValidatorError> validatorErrors) {

		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

		List<ValidatorError> errors = new ArrayList<ValidatorError>();
		if (validatorErrors != null) {
			for (FacesValidatorError error : validatorErrors) {
				errors.add(error);
			}
		}
		validatorErrorHandler.handleValidatorError(request, response, errors);
	}

	/**
	 * Helper method to write an attack in the log
	 * 
	 * @param context Request context
	 * @param error validation result
	 */
	private void log(final FacesContext context, final List<FacesValidatorError> errors) {

		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

		if (errors != null) {
			for (ValidatorError error : errors) {
				error.setTarget(HDIVUtil.getRequestURI(request));
				logger.log(error);
			}
		}
	}

}
