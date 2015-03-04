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
package org.hdiv.context.jsf1;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;

/**
 * Wrapper of FacesContextFactory to create new instances of FacesContext. Returns a Hdiv's proprietary FacesContext
 * instance which is a wrapper of the original instance
 * 
 * @author Gotzon Illarramendi
 */
public class FacesContextFactoryWrapper extends FacesContextFactory {

	/**
	 * Original FacesContextFactory
	 */
	private FacesContextFactory wrapped;

	/**
	 * Default constructor
	 * 
	 * @param wrapped Wrapped {@link FacesContextFactory}
	 */
	public FacesContextFactoryWrapper(FacesContextFactory wrapped) {
		super();
		this.wrapped = wrapped;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.context.FacesContextFactory#getFacesContext(java.lang.Object, java.lang.Object,
	 * java.lang.Object, javax.faces.lifecycle.Lifecycle)
	 */
	public FacesContext getFacesContext(Object context, Object request, Object response, Lifecycle lifecycle)
			throws FacesException {

		FacesContext original = this.wrapped.getFacesContext(context, request, response, lifecycle);
		return new HDIVFacesContext(original);
	}

}
