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

import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.hdiv.config.HDIVConfig;
import org.hdiv.exception.HDIVException;
import org.hdiv.state.DefaultStateManager;
import org.hdiv.state.StateManager;
import org.hdiv.util.ConstantsJsf;
import org.hdiv.util.HDIVUtil;
import org.hdiv.util.UtilsJsf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PhaseListener that takes care of HDIV configuration, mostly objects that are stored in application context.
 * 
 * @author Gotzon Illarramendi
 */
public class ConfigPhaseListener implements PhaseListener {

	private static final long serialVersionUID = -3803869221110488120L;

	private static final Logger log = LoggerFactory.getLogger(ConfigPhaseListener.class);

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
	public void beforePhase(final PhaseEvent event) {

		FacesContext context = event.getFacesContext();
		boolean reqInitialized = UtilsJsf.isRequestInitialized(context);
		if (!reqInitialized) {
			return;
		}

		if (event.getPhaseId().equals(PhaseId.RESTORE_VIEW)) {

			if (!initialized) {

				if (log.isDebugEnabled()) {
					log.debug("Initialize ConfigPhaseListener.");
				}

				// Check not supported features
				checkSupportedFeatures(context);

				initialized = true;
			}

			// Init state manager
			initializeStateManager(context);
		}

		if (event.getPhaseId().equals(PhaseId.RENDER_RESPONSE)) {

			// Add user's unique id to state
			addUserUniqueTokenToState(context);
			// Init state manager
			initializeStateManager(context);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.event.PhaseListener#afterPhase(javax.faces.event.PhaseEvent)
	 */
	public void afterPhase(final PhaseEvent event) {

	}

	/**
	 * Adds to the state a data unique for the user. This way state becomes something unique for each user. Session id is used as this
	 * unique data.
	 * 
	 * @param facesContext request context
	 */
	private void addUserUniqueTokenToState(final FacesContext facesContext) {

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
	 * @param context request context
	 */
	private void checkSupportedFeatures(final FacesContext context) {

		ExternalContext externalContext = context.getExternalContext();
		ServletContext servletContext = (ServletContext) externalContext.getContext();

		HDIVConfig config = HDIVUtil.getHDIVConfig(servletContext);

		if (Boolean.TRUE.equals(config.getConfidentiality())) {
			throw new HDIVException("Confidentiality is not implemented in HDIV for JSF, disable it in hdiv-config.xml");
		}

	}

	/**
	 * Initialize {@link StateManager} for the current request.
	 * @param context request context
	 */
	protected void initializeStateManager(final FacesContext context) {

		Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
		if (!requestMap.containsKey(ConstantsJsf.HDIV_STATE_MANAGER_ATTRIBUTE_KEY)) {

			StateManager stateManager = new DefaultStateManager(context);
			requestMap.put(ConstantsJsf.HDIV_STATE_MANAGER_ATTRIBUTE_KEY, stateManager);
		}
	}

}
