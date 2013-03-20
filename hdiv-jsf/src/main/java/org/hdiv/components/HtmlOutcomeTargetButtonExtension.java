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
package org.hdiv.components;

import java.io.IOException;

import javax.faces.component.html.HtmlOutcomeTargetButton;
import javax.faces.context.FacesContext;

import org.hdiv.components.support.OutcomeTargetComponentProcessor;

/**
 * <p>
 * Extends HtmlOutcomeTargetButton in order to secure component
 * </p>
 * <p>
 * Only for JSF 2.0+
 * </p>
 * 
 * @author Gotzon Illarramendi
 */
public class HtmlOutcomeTargetButtonExtension extends HtmlOutcomeTargetButton {

	/**
	 * Component processor for HtmlOutcomeTarget components.
	 */
	private OutcomeTargetComponentProcessor componentProcessor = new OutcomeTargetComponentProcessor();

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.UIComponentBase#encodeBegin(javax.faces.context.FacesContext)
	 */
	public void encodeBegin(FacesContext context) throws IOException {

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