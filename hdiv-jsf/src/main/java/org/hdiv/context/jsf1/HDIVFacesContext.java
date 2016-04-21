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
package org.hdiv.context.jsf1;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.hdiv.context.jsf1.support.FacesContextWrapper;

/**
 * Wrapper of FacesContext.
 * 
 * Returns an ExternalContext wrapper instead of the original.
 * 
 * @author Gotzon Illarramendi
 */
public class HDIVFacesContext extends FacesContextWrapper {

	/**
	 * Original FacesContext
	 */
	private FacesContext wrapped;

	/**
	 * Original ExternalContext
	 */
	private ExternalContext eContext;

	/**
	 * Default constructor
	 * 
	 * @param wrapped original FacesContext
	 */
	public HDIVFacesContext(FacesContext wrapped) {
		super();
		this.wrapped = wrapped;
		setCurrentInstance(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.context.FacesContextWrapper#getWrapped()
	 */
	public FacesContext getWrapped() {
		return this.wrapped;
	}

	/**
	 * Returns ExternalContext wrapper instead of the original Caches ExternalContext instance.
	 */
	public ExternalContext getExternalContext() {

		if (this.eContext == null) {
			ExternalContext original = this.wrapped.getExternalContext();
			this.eContext = new RedirectExternalContext(original);
		}

		return this.eContext;
	}
}
