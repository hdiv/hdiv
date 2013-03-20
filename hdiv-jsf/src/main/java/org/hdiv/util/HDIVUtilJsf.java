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
package org.hdiv.util;

import javax.faces.context.FacesContext;

import org.hdiv.events.HDIVFacesEventListener;
import org.hdiv.exception.HDIVException;

/**
 * Utility class that stores in threadlocal/servletContext/request necessary objects for JSF version.
 * 
 * @author Gotzon Illarramendi
 */
public class HDIVUtilJsf {

	public static final String FACESEVENTLISTENER_SERVLETCONTEXT_KEY = "FACESEVENTLISTENER_SERVLETCONTEXT_KEY";
	public static final String TARGET_REQUEST_KEY = "TARGET_REQUEST_KEY";

	/* HDIVFacesEventListener */

	public static HDIVFacesEventListener getFacesEventListener(FacesContext facesContext) {

		HDIVFacesEventListener newFacesEventListener = (HDIVFacesEventListener) facesContext.getExternalContext()
				.getApplicationMap().get(FACESEVENTLISTENER_SERVLETCONTEXT_KEY);

		if (newFacesEventListener == null) {
			throw new HDIVException(
					"HDIVFacesEventListener object has not been initialized correctly in servletContext.");
		} else {
			return newFacesEventListener;
		}
	}

	public static void setFacesEventListener(HDIVFacesEventListener newFacesEventListener, FacesContext facesContext) {

		facesContext.getExternalContext().getApplicationMap()
				.put(FACESEVENTLISTENER_SERVLETCONTEXT_KEY, newFacesEventListener);

	}

}