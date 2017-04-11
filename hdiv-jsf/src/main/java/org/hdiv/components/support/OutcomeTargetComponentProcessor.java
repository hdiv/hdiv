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
import org.hdiv.util.Method;
import org.hdiv.util.UtilsJsf;

public class OutcomeTargetComponentProcessor extends AbstractComponentProcessor {

	private static final Log log = LogFactory.getLog(OutcomeTargetComponentProcessor.class);

	protected OutcomeTargetComponentHelper helper = new OutcomeTargetComponentHelper();

	public void processOutcomeTargetLinkComponent(final FacesContext context, final UIOutcomeTarget component) {

		try {
			ExternalContext externalContext = context.getExternalContext();
			HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();

			String url = helper.getUrl(context, component);
			String hdivParameter = HDIVUtil.getRequestContext(request).getHdivParameterName();
			UrlData urlData = linkUrlProcessor.createUrlData(url, Method.GET, hdivParameter, request);
			if (urlData.isHdivStateNecessary(config)) {

				boolean hasUIParams = UtilsJsf.hasUIParameterChild(component);

				// if url hasn't got parameters, we do not have to include HDIV's state
				if (!config.isValidationInUrlsWithoutParamsActivated() && !urlData.containsParams() && !hasUIParams) {

					// Do nothing
					return;
				}

				IDataComposer dataComposer = HDIVUtil.getRequestContext(request).getDataComposer();
				dataComposer.beginRequest(Method.GET, urlData.getUrlWithoutContextPath());

				urlData.setComposedUrlParams(dataComposer.composeParams(urlData.getUrlParams(), Method.GET, Constants.ENCODING_UTF_8));

				String stateParam = dataComposer.endRequest();

				// Add a children UIParam component with HDIV state
				UIParameter paramComponent = (UIParameter) context.getApplication().createComponent(UIParameter.COMPONENT_TYPE);
				paramComponent.setName(hdivParameter);
				paramComponent.setValue(stateParam);
				component.getChildren().add(paramComponent);
			}
		}
		catch (FacesException e) {
			log.error("Error in OutcomeTargetComponentProcessor.processOutcomeTargetLinkComponent: " + e.getMessage());
			throw e;
		}
	}

}
