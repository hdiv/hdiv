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
package org.hdiv.events;

import java.util.List;

import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.FacesListener;
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

/**
 * <p>
 * Listener that processes a HDIV event. This class validates the component tree searching for modifications in the values of the non
 * editable data.
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

	private static final Log log = LogFactory.getLog(HDIVFacesEventListener.class);

	/**
	 * Request attribute that has a true value only if the request was correctly validated.
	 */
	public static final String REQUEST_VALIDATED = HDIVFacesEventListener.class.getName() + ".REQUEST_VALIDATED";

	/**
	 * HDIV config
	 */
	protected HDIVConfig config;

	/**
	 * Hdiv attack logger
	 */
	protected Logger logger;

	/**
	 * Error handler.
	 */
	protected ValidatorErrorHandler validatorErrorHandler;

	/**
	 * Component tree validator.
	 */
	protected ComponentTreeValidator componentTreeValidator;

	private void init(final FacesContext context) {

		if (componentTreeValidator != null) {
			return;
		}

		// TODO inject the required dependencies
		componentTreeValidator = (ComponentTreeValidator) context.getExternalContext().getRequestMap().get("ComponentTreeValidator");
	}

	/**
	 * Process a HDIVFacesEvent event
	 * 
	 * @param facesEvent Event
	 */
	public void processListener(final HDIVFacesEvent facesEvent) {

		if (log.isDebugEnabled()) {
			log.debug("Processing HDIV event:" + facesEvent);
		}

		FacesContext context = FacesContext.getCurrentInstance();

		init(context);

		UIComponent eventComponent = facesEvent.getComponent();

		// Validate component tree
		// List<ValidatorError> errors = componentTreeValidator.validateComponentTree(context);
		//
		// log(context, errors);
		// if (mustStopRequest(errors)) {
		// forwardToErrorPage(context, errors);
		// }
		//
		// context.getExternalContext().getRequestMap().put(REQUEST_VALIDATED, true);
	}

	protected boolean mustStopRequest(final List<ValidatorError> errors) {

		// TODO check debug config

		if (errors != null && !errors.isEmpty()) {
			for (ValidatorError error : errors) {
				if (!error.getType().equals(HDIVErrorCodes.EDITABLE_VALIDATION_ERROR)) {
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
	protected void forwardToErrorPage(final FacesContext context, final List<ValidatorError> validatorErrors) {

		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

		validatorErrorHandler.handleValidatorError(request, response, validatorErrors);
	}

	/**
	 * Helper method to write an attack in the log
	 * 
	 * @param context Request context
	 * @param error validation result
	 */
	private void log(final FacesContext context, final List<ValidatorError> errors) {

		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

		for (ValidatorError error : errors) {
			error.setTarget(HDIVUtil.getRequestURI(request));
			logger.log(error);
		}
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
	public void setTransient(final boolean newTransientValue) {

	}

	/**
	 * As the listener is transient this method isn't called
	 */
	public Object saveState(final FacesContext context) {

		return null;
	}

	/**
	 * As the listener is transient this method isn't called
	 */
	public void restoreState(final FacesContext context, final Object state) {

	}

	/**
	 * @param config the config to set
	 */
	public void setConfig(final HDIVConfig config) {
		this.config = config;
	}

	/**
	 * @param logger the logger to set
	 */
	public void setLogger(final Logger logger) {
		this.logger = logger;
	}

	/**
	 * @param validatorErrorHandler the validatorErrorHandler to set
	 */
	public void setValidatorErrorHandler(final ValidatorErrorHandler validatorErrorHandler) {
		this.validatorErrorHandler = validatorErrorHandler;
	}

}