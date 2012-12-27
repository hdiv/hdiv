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
package org.hdiv.context;

import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * <p>
 * Wrapper of ExternalContext.
 * </p>
 * <p>
 * Generates HDIV's state for redirects and adds it to the url as a parameter
 * </p>
 * <p>
 * Only for JSF 2.0+
 * </p>
 * 
 * @author Gotzon Illarramendi
 */
public class RedirectExternalContext extends javax.faces.context.ExternalContextWrapper {

	private static Log log = LogFactory.getLog(RedirectExternalContext.class);

	/**
	 * Class for helping with the redirect logic
	 */
	private RedirectHelper redirectHelper;

	/**
	 * Original ExternalContext
	 */
	private ExternalContext wrapped;

	/**
	 * ExternalContext constructor
	 * 
	 * @param wrapped
	 *            original ExternalContext
	 */
	public RedirectExternalContext(ExternalContext wrapped) {

		ServletContext servletContext = (ServletContext) wrapped.getContext();
		this.redirectHelper = (RedirectHelper) WebApplicationContextUtils.getRequiredWebApplicationContext(
				servletContext).getBean("redirectHelper");

		Assert.notNull(this.redirectHelper);

		this.wrapped = wrapped;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.context.ExternalContextWrapper#getWrapped()
	 */
	public ExternalContext getWrapped() {

		return this.wrapped;
	}

	/**
	 * If it is an internal redirect (to the application itself) generates the state, stores it in session and adds
	 * corresponding parameter to url.
	 */
	public void redirect(String url) throws IOException {

		// Add state to url
		String finalUrl = this.redirectHelper.addHDIVStateToURL(url);
		if (log.isDebugEnabled()) {
			log.debug("Redirecting to:" + finalUrl);
		}

		this.wrapped.redirect(finalUrl);

	}

}
