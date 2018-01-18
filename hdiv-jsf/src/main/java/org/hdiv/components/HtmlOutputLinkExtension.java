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
package org.hdiv.components;

import java.io.IOException;

import javax.faces.component.html.HtmlOutputLink;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.hdiv.components.support.OutputLinkComponentProcessor;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * HtmlOutputLink component extension
 * 
 * @author Gotzon Illarramendi
 */
public class HtmlOutputLinkExtension extends HtmlOutputLink {

	private OutputLinkComponentProcessor componentProcessor;

	protected void init(final FacesContext context) {

		if (componentProcessor == null) {
			ExternalContext externalContext = context.getExternalContext();
			ServletContext servletContext = (ServletContext) externalContext.getContext();
			WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

			componentProcessor = wac.getBean(OutputLinkComponentProcessor.class);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.UIComponent#encodeBegin(javax.faces.context. FacesContext)
	 */
	@Override
	public void encodeBegin(final FacesContext context) throws IOException {

		// Init dependencies
		init(context);

		componentProcessor.processOutputLink(context, this);

		super.encodeBegin(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.UIComponentBase#encodeEnd(javax.faces.context.FacesContext)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void encodeEnd(final FacesContext context) throws IOException {

		super.encodeEnd(context);

		componentProcessor.removeHdivStateUIParameter(context, this);

		// Deprecated method in 1.2, but necessary to work in 1.1
		if (getValueBinding("value") != null) {
			setValue(null);
		}
	}

}
