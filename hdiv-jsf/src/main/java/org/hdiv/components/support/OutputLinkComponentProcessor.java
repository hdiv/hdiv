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
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.component.html.HtmlOutputLink;
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

public class OutputLinkComponentProcessor extends AbstractComponentProcessor {

	private static final Log log = LogFactory.getLog(OutputLinkComponentProcessor.class);

	public void processOutputLink(final FacesContext context, final HtmlOutputLink component) {

		try {
			final ExternalContext externalContext = context.getExternalContext();
			final HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();

			String url = component.getValue().toString();

			final UrlData urlData = linkUrlProcessor.createUrlData(url, Method.GET, request);
			if (linkUrlProcessor.isHdivStateNecessary(urlData)) {

				final boolean hasUIParams = UtilsJsf.hasUIParameterChild(component);

				// if url hasn't got parameters, we do not have to include HDIV's state
				if (!config.isValidationInUrlsWithoutParamsActivated() && !urlData.containsParams() && !hasUIParams) {

					// Do nothing
					return;
				}

				final IDataComposer dataComposer = HDIVUtil.getDataComposer(request);
				dataComposer.beginRequest(Method.GET, urlData.getUrlWithoutContextPath());

				final String processedParams = dataComposer.composeParams(urlData.getUrlParams(), Method.GET,
						Constants.ENCODING_UTF_8);
				urlData.setUrlParams(processedParams);

				if (hasUIParams) {

					for (final UIComponent comp : component.getChildren()) {
						if (comp instanceof UIParameter) {
							final UIParameter param = (UIParameter) comp;
							final String name = param.getName();
							final String value = param.getValue().toString();

							dataComposer.compose(name, value, false);
						}
					}

					final String stateParam = dataComposer.endRequest();

					url = linkUrlProcessor.getProcessedUrl(urlData);

					component.setValue(url);

					// Add a children UIParam component with Hdiv's state
					final UIParameter paramComponent = (UIParameter) context.getApplication()
							.createComponent(UIParameter.COMPONENT_TYPE);

					final String hdivParameter = (String) externalContext.getSessionMap().get(Constants.HDIV_PARAMETER);

					paramComponent.setName(hdivParameter);
					paramComponent.setValue(stateParam);
					component.getChildren().add(paramComponent);
				}
				else {

					final String stateParam = dataComposer.endRequest();

					// Add state directly in the outputLink's value
					url = linkUrlProcessor.getProcessedUrlWithHdivState(request, urlData, stateParam);
					component.setValue(url);
				}
			}
		}
		catch (final FacesException e) {
			log.error("Error in OutputLinkComponentProcessor.processOutputLink: " + e.getMessage());
			throw e;
		}

	}
}
