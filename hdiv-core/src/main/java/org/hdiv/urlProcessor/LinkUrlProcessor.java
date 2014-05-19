/**
 * Copyright 2005-2013 hdiv.org
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
package org.hdiv.urlProcessor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;

/**
 * UrlProcessor for link and redirect urls.
 * 
 * @author Gotzon Illarramendi
 */
public class LinkUrlProcessor extends AbstractUrlProcessor {

	/**
	 * Commons Logging instance.
	 */
	private static Log log = LogFactory.getLog(LinkUrlProcessor.class);

	/**
	 * Process the url to add hdiv state if it is necessary.
	 * 
	 * @param request
	 *            {@link HttpServletRequest} object
	 * @param url
	 *            url to process
	 * @return processed url
	 */
	public String processUrl(HttpServletRequest request, String url) {
		// Default encoding UTF-8
		return this.processUrl(request, url, Constants.ENCODING_UTF_8);
	}

	/**
	 * Process the url to add hdiv state if it is necessary.
	 * 
	 * @param request
	 *            {@link HttpServletRequest} object
	 * @param url
	 *            url to process
	 * @param encoding
	 *            char encoding
	 * @return processed url
	 */
	public String processUrl(HttpServletRequest request, String url, String encoding) {

		IDataComposer dataComposer = HDIVUtil.getDataComposer(request);
		if (dataComposer == null) {
			// IDataComposer not initialized on request, request is out of filter
			if (log.isDebugEnabled()) {
				log.debug("IDataComposer not initialized on request, request is out of filter");
			}
			return url;
		}

		UrlData urlData = super.createUrlData(url, "GET", request);
		if (super.isHdivStateNecessary(urlData)) {
			// the url needs protection
			dataComposer.beginRequest(urlData.getUrlWithoutContextPath());

			Map<String, String[]> params = urlData.getOriginalUrlParams();
			if (params != null) {
				for (String key : params.keySet()) {
					String[] values = (String[]) params.get(key);

					for (int i = 0; i < values.length; i++) {
						String value = values[i];
						String composedParam = dataComposer.compose(key, value, false, false, encoding);
						values[i] = composedParam;
					}
					params.put(key, values);
				}
				urlData.setProcessedUrlParams(params);
			}

			// Hdiv state param value
			String stateParam = dataComposer.endRequest();
			// Url with confidential values and hdiv state param
			url = super.getProcessedUrlWithHdivState(request, urlData, stateParam);
		}

		return url;
	}

}