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
package org.hdiv.components.support;

import java.util.Map;

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

		// Init dependencies
		super.init(context);

		try {
			ExternalContext externalContext = context.getExternalContext();
			HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();

			String url = this.helper.getUrl(context, component);

			UrlData urlData = this.urlProcessor.createUrlData(url, "GET", request);
			if (this.urlProcessor.isHdivStateNecessary(urlData)) {

				boolean hasUIParams = UtilsJsf.hasUIParameterChild(component);

				// if url hasn't got parameters, we do not have to include HDIV's state
				if (!this.hdivConfig.isValidationInUrlsWithoutParamsActivated() && !urlData.containsParams()
						&& !hasUIParams) {

					// Do nothing
					return;
				}

				IDataComposer dataComposer = HDIVUtil.getDataComposer(request);
				dataComposer.beginRequest(urlData.getContextPathRelativeUrl());

				Map<String, String[]> params = urlData.getOriginalUrlParams();
				if (params != null) {
					// Process url params
					for (String key : params.keySet()) {
						String[] values = params.get(key);

						for (int i = 0; i < values.length; i++) {
							String value = values[i];
							String composedParam = dataComposer.compose(key, value, false, true,
									Constants.ENCODING_UTF_8);
							values[i] = composedParam;
						}
						params.put(key, values);
					}
					urlData.setProcessedUrlParams(params);
				}

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
			log.error("Error in JsfLinkUrlProcessor.processOutcomeTargetLinkComponent: " + e.getMessage());
			throw e;
		}
	}

}
