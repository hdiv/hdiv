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

import java.io.IOException;

import javax.faces.component.html.HtmlOutcomeTargetLink;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.hdiv.components.support.OutcomeTargetComponentProcessor;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * <p>
 * Extends HtmlOutcomeTargetLink in order to secure component
 * </p>
 * <p>
 * Only for JSF 2.0+
 * </p>
 * 
 * @author Gotzon Illarramendi
 */
public class HtmlOutcomeTargetLinkExtension extends HtmlOutcomeTargetLink {

	/**
	 * Component processor for HtmlOutcomeTarget components.
	 */
	private OutcomeTargetComponentProcessor componentProcessor;

	protected void init(FacesContext context) {

		if (this.componentProcessor == null) {
			ExternalContext externalContext = context.getExternalContext();
			ServletContext servletContext = (ServletContext) externalContext.getContext();
			WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

			this.componentProcessor = wac.getBean(OutcomeTargetComponentProcessor.class);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.UIComponentBase#encodeBegin(javax.faces.context.FacesContext)
	 */
	public void encodeBegin(FacesContext context) throws IOException {

		// Init dependencies
		this.init(context);

		this.componentProcessor.processOutcomeTargetLinkComponent(context, this);

		super.encodeBegin(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.UIComponentBase#encodeEnd(javax.faces.context.FacesContext)
	 */
	public void encodeEnd(FacesContext context) throws IOException {

		super.encodeEnd(context);

		this.componentProcessor.removeHdivStateUIParameter(context, this);
	}

}