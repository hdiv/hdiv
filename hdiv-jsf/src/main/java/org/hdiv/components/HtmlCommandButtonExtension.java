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
package org.hdiv.components;

import javax.faces.component.html.HtmlCommandButton;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.event.PhaseId;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.events.HDIVFacesEvent;
import org.hdiv.events.HDIVFacesEventListener;
import org.hdiv.util.HDIVUtilJsf;

/**
 * Extension of HtmlCommandButton. Method queueEvent is overwritten and executes 3 steps:
 * <ol>
 * <li>Creates HDIV event and adds it to the event queue</li>
 * <li>Adds the original event to the queue</li>
 * <li>Adds hdiv's event listener to the component</li>
 * </ol>
 * 
 * @author Gotzon Illarramendi
 */
public class HtmlCommandButtonExtension extends HtmlCommandButton {

	private static Log log = LogFactory.getLog(HtmlCommandButtonExtension.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.UICommand#queueEvent(javax.faces.event.FacesEvent)
	 */
	public void queueEvent(FacesEvent event) {

		// If it doesn't exist, add listener that handles HDIV's event
		this.addListener(this.getFacesContext());

		// If the event is of type ActionEvent, throw a HDIV event to handle it later
		// This verification is needed because JSF 2.0 can generate other type
		// (AjaxBehaviorEvent) of events due to the use of Ajax and we don't want
		// to generate two HDIV events.
		if (event instanceof ActionEvent) {
			HDIVFacesEvent hdivevent = new HDIVFacesEvent(this);
			if (this.isImmediate()) {
				hdivevent.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
			}
			else {
				hdivevent.setPhaseId(PhaseId.PROCESS_VALIDATIONS);
			}
			super.queueEvent(hdivevent);
		}

		// Throw ActionEvent to handle it in a standar way
		super.queueEvent(event);

	}

	@Override
	public Object saveState(FacesContext context) {
		// Remove existing listener
		this.removeListeners();

		return super.saveState(context);
	}

	/**
	 * Remove existing HDIVFacesEventListener
	 */
	private void removeListeners() {

		FacesListener[] listeners = null;
		try {
			listeners = this.getFacesListeners(HDIVFacesEventListener.class);
		}
		catch (NullPointerException e) {
			// Sun RI 1.2 versions throw a NullPointerException when calling
			// to this method because HDIVFacesListener is transient
			// and it isn't stored in the state.
			// When this error happens, there is no listener of type
			// HDIVFacesListener so we continue with the execution
			if (log.isDebugEnabled()) {
				log.debug("Catched Exception when calling UIComponent.getFacesListeners(Class)");
			}
		}

		// If there is a listener remove it
		if (listeners != null && listeners.length > 0) {
			for (int i = 0; i < listeners.length; i++) {
				this.removeFacesListener(listeners[i]);
			}
		}
	}

	/**
	 * Adds HDIV listener to the component if needed
	 * 
	 * @param context request context
	 */
	private void addListener(FacesContext context) {

		// Remove existing listener
		this.removeListeners();

		// Add the listener
		HDIVFacesEventListener eventListener = HDIVUtilJsf.getFacesEventListener(context);
		this.addFacesListener(eventListener);
	}

}
