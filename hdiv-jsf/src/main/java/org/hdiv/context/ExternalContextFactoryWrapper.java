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
package org.hdiv.context;

import javax.faces.context.ExternalContext;
import javax.faces.context.ExternalContextFactory;

/**
 * <p>
 * Factory to create HDIV's ExternalContext objects instead of JSF's base implementation
 * </p>
 * <p>
 * Only for JSF 2.0+
 * </p>
 * 
 * @author Gotzon Illarramendi
 */
public class ExternalContextFactoryWrapper extends ExternalContextFactory {

	/**
	 * Original ExternalContextFactory
	 */
	private final ExternalContextFactory original;

	/**
	 * Default constructor
	 * 
	 * @param original original ExternalContextFactory
	 */
	public ExternalContextFactoryWrapper(final ExternalContextFactory original) {
		super();
		this.original = original;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.context.ExternalContextFactory#getWrapped()
	 */
	@Override
	public ExternalContextFactory getWrapped() {

		return original;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.context.ExternalContextFactory#getExternalContext(java.lang .Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public ExternalContext getExternalContext(final Object context, final Object request, final Object response) {

		ExternalContext ec = original.getExternalContext(context, request, response);
		return new RedirectExternalContext(ec);
	}

}
