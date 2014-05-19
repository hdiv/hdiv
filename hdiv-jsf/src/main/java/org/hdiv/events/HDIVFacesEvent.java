/**
 * Copyright 2005-2013 hdiv.org
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

import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;

/**
 * HDIV event thrown by the executed UICommand component. This event is processed by HDIVfacesEventListener.
 * 
 * @author Gotzon Illarramendi
 * 
 */
public class HDIVFacesEvent extends FacesEvent {

	private static final long serialVersionUID = -5168833814150575067L;

	/**
	 * Default constructor
	 * 
	 * @param uiComponent
	 */
	public HDIVFacesEvent(UIComponent uiComponent) {
		super(uiComponent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.event.FacesEvent#isAppropriateListener(javax.faces.event.FacesListener)
	 */
	public boolean isAppropriateListener(FacesListener faceslistener) {

		return faceslistener instanceof HDIVFacesEventListener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.event.FacesEvent#processListener(javax.faces.event.FacesListener)
	 */
	public void processListener(FacesListener faceslistener) {

		HDIVFacesEventListener listener = (HDIVFacesEventListener) faceslistener;
		listener.processListener(this);

	}

}
