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
package org.hdiv.components.support;

import javax.faces.FacesException;
import javax.faces.component.UIOutcomeTarget;
import javax.faces.component.UIParameter;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.urlProcessor.UrlData;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;
import org.hdiv.util.UtilsJsf;

public class OutcomeTargetComponentProcessor extends AbstractComponentProcessor {

	private static Log log = LogFactory.getLog(OutcomeTargetComponentProcessor.class);

	protected OutcomeTargetComponentHelper helper = new OutcomeTargetComponentHelper();

	public void processOutcomeTargetLinkComponent(FacesContext context, UIOutcomeTarget component) {

		try {
			ExternalContext externalContext = context.getExternalContext();
			HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();

			String url = this.helper.getUrl(context, component);

			UrlData urlData = this.linkUrlProcessor.createUrlData(url, "GET", request);
			if (this.linkUrlProcessor.isHdivStateNecessary(urlData)) {

				boolean hasUIParams = UtilsJsf.hasUIParameterChild(component);

				// if url hasn't got parameters, we do not have to include HDIV's state
				if (!this.config.isValidationInUrlsWithoutParamsActivated() && !urlData.containsParams()
						&& !hasUIParams) {

					// Do nothing
					return;
				}

				IDataComposer dataComposer = HDIVUtil.getDataComposer(request);
				dataComposer.beginRequest("GET", urlData.getUrlWithoutContextPath());

				String processedParams = dataComposer.composeParams(urlData.getUrlParams(), "GET", Constants.ENCODING_UTF_8);
				urlData.setUrlParams(processedParams);

				String stateParam = dataComposer.endRequest();

				String hdivParameter = (String) externalContext.getSessionMap().get(Constants.HDIV_PARAMETER);

				// Add a children UIParam component with HDIV state
				UIParameter paramComponent = (UIParameter) context.getApplication().createComponent(
						UIParameter.COMPONENT_TYPE);
				paramComponent.setName(hdivParameter);
				paramComponent.setValue(stateParam);
				component.getChildren().add(paramComponent);
			}
		} catch (FacesException e) {
			log.error("Error in OutcomeTargetComponentProcessor.processOutcomeTargetLinkComponent: " + e.getMessage());
			throw e;
		}
	}

}
