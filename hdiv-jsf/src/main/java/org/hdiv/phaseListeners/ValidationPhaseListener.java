/**
 * Copyright 2005-2012 hdiv.org
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

import java.io.IOException;

import javax.faces.application.NavigationHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.exception.HDIVException;
import org.hdiv.exception.StateValidationException;
import org.hdiv.filter.IValidationHelper;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

/**
 * PhaseListener that executes request validation. It uses a ValidationHelper for that.
 * 
 * @author Gotzon Illarramendi
 */
public class ValidationPhaseListener implements PhaseListener {

	private static final long serialVersionUID = 4071021880986895296L;

	private static Log log = LogFactory.getLog(ValidationPhaseListener.class);

	/**
	 * HDIV configuration
	 */
	private HDIVConfig hdivConfig;

	/**
	 * HDIV core request validation helper
	 */
	private IValidationHelper validationHelper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.event.PhaseListener#getPhaseId()
	 */
	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.event.PhaseListener#beforePhase(javax.faces.event.PhaseEvent)
	 */
	public void beforePhase(PhaseEvent event) {

		if (this.hdivConfig == null) {
			WebApplicationContext context = FacesContextUtils.getRequiredWebApplicationContext(event.getFacesContext());
			this.hdivConfig = (HDIVConfig) context.getBean("config");
			this.validationHelper = (IValidationHelper) context.getBean("jsfValidatorHelper");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.event.PhaseListener#afterPhase(javax.faces.event.PhaseEvent)
	 */
	public void afterPhase(PhaseEvent event) {

		ExternalContext exContext = event.getFacesContext().getExternalContext();
		HttpServletRequest request = (HttpServletRequest) exContext.getRequest();

		try {
			boolean legal = validationHelper.validate(request);

			if (!legal) {
				try {
					FacesContext fContext = FacesContext.getCurrentInstance();
					String contextPath = fContext.getExternalContext().getRequestContextPath();
					fContext.getExternalContext().redirect(contextPath + this.hdivConfig.getErrorPage());
				} catch (IOException e) {
					throw new StateValidationException();
				}
			}

		} catch (HDIVException e) {
			log.error("Error in the request validation. Msg:" + e.getMessage());
			try {
				FacesContext fc = FacesContext.getCurrentInstance();
				NavigationHandler nav = fc.getApplication().getNavigationHandler();
				nav.handleNavigation(fc, null, this.hdivConfig.getErrorPage());
				fc.renderResponse();
			} catch (Exception ex) {
				log.error("Error in the request validation. Msg:" + ex.getMessage());
				// Request must be stopped.
				// Throw exception to be catched by JSF
				throw new RuntimeException(e);
			}
		}

	}
}