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
package org.hdiv.phaseListeners;

import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.events.HDIVFacesEventListener;
import org.hdiv.exception.HDIVException;
import org.hdiv.util.HDIVUtil;
import org.hdiv.util.HDIVUtilJsf;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

/**
 * PhaseListener that takes care of HDIV configuration, mostly objects that are stored in application context.
 * 
 * @author Gotzon Illarramendi
 */
public class ConfigPhaseListener implements PhaseListener {

	private static final long serialVersionUID = -3803869221110488120L;

	private static Log log = LogFactory.getLog(ConfigPhaseListener.class);

	/**
	 * Name of the attribute that contains the user token
	 */
	private static final String HDIV_USER_TOKEN_ATTR_NAME = "HDIV_USER_TOKEN";

	/**
	 * Is SevletContext object initialized?
	 */
	private boolean initialized = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.event.PhaseListener#getPhaseId()
	 */
	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.event.PhaseListener#beforePhase(javax.faces.event.PhaseEvent)
	 */
	public void beforePhase(PhaseEvent event) {

		if (event.getPhaseId().equals(PhaseId.RESTORE_VIEW)) {

			if (!this.initialized) {

				if (log.isDebugEnabled()) {
					log.debug("Initialize ConfigPhaseListener dependencies.");
				}

				FacesContext context = event.getFacesContext();
				// Check not supported features
				this.checkSupportedFeatures(context);

				// Get listener instances
				WebApplicationContext wac = FacesContextUtils.getRequiredWebApplicationContext(context);
				HDIVFacesEventListener facesEventListener = wac.getBean(HDIVFacesEventListener.class);

				// It is added to the servletContext to be able to consume it from components
				HDIVUtilJsf.setFacesEventListener(facesEventListener, context);
			}
		}

		if (event.getPhaseId().equals(PhaseId.RENDER_RESPONSE)) {

			FacesContext context = event.getFacesContext();
			// Add user's unique id to state
			this.addUserUniqueTokenToState(context);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.event.PhaseListener#afterPhase(javax.faces.event.PhaseEvent)
	 */
	public void afterPhase(PhaseEvent event) {

	}

	/**
	 * Adds to the state a data unique for the user. This way state becomes something unique for each user. Session id
	 * is used as this unique data.
	 * 
	 * @param facesContext
	 *            request context
	 */
	private void addUserUniqueTokenToState(FacesContext facesContext) {

		UIViewRoot viewRoot = facesContext.getViewRoot();
		if (viewRoot != null) {

			String userToken = (String) viewRoot.getAttributes().get(HDIV_USER_TOKEN_ATTR_NAME);
			if (userToken == null) {

				HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
				if (session != null) {
					viewRoot.getAttributes().put(HDIV_USER_TOKEN_ATTR_NAME, session.getId());
				}

			}
		}
	}

	/**
	 * Check {@link HDIVConfig} to ensure all enabled features are supported by Jsf module.
	 * 
	 * @param context
	 *            request context
	 */
	private void checkSupportedFeatures(FacesContext context) {

		ExternalContext externalContext = context.getExternalContext();
		HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
		ServletContext servletContext = request.getSession().getServletContext();

		HDIVConfig config = HDIVUtil.getHDIVConfig(servletContext);

		if (Boolean.TRUE.equals(config.getConfidentiality())) {
			throw new HDIVException("Confidentiality is not implemented in HDIV for JSF, disable it in hdiv-config.xml");
		}

	}

}
