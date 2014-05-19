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
 * UrlProcessor for form action urls.
 * 
 * @author Gotzon Illarramendi
 */
public class FormUrlProcessor extends AbstractUrlProcessor {

	public static final String FORM_STATE_ID = "hdivFormStateId";

	/**
	 * Commons Logging instance.
	 */
	private static Log log = LogFactory.getLog(FormUrlProcessor.class);

	/**
	 * Process form action url to add hdiv state if it is necessary.
	 * 
	 * @param request
	 *            {@link HttpServletRequest} object
	 * @param url
	 *            url to process
	 * @return processed url
	 */
	public String processUrl(HttpServletRequest request, String url) {

		return this.processUrl(request, url, "POST");
	}

	/**
	 * Process form action url to add hdiv state if it is necessary.
	 * 
	 * @param request
	 *            {@link HttpServletRequest} object
	 * @param url
	 *            url to process
	 * @param method
	 *            form submit method
	 * @return processed url
	 */
	public String processUrl(HttpServletRequest request, String url, String method) {

		if (method == null) {
			method = "POST";
		}

		IDataComposer dataComposer = HDIVUtil.getDataComposer(request);
		if (dataComposer == null) {
			// IDataComposer not initialized on request, request is out of filter
			if (log.isDebugEnabled()) {
				log.debug("IDataComposer not initialized on request, request is out of filter");
			}
			return url;
		}

		UrlData urlData = super.createUrlData(url, method, request);
		if (super.isHdivStateNecessary(urlData)) {
			// the url needs protection
			String stateId = dataComposer.beginRequest(urlData.getUrlWithoutContextPath());

			// Publish the state in request to make it accessible on jsp
			request.setAttribute(FORM_STATE_ID, stateId);

			// Process url params
			Map<String, String[]> params = urlData.getOriginalUrlParams();
			if (params != null) {
				for (String key : params.keySet()) {
					String[] values = (String[]) params.get(key);

					for (int i = 0; i < values.length; i++) {
						String value = values[i];
						String composedParam = dataComposer.compose(key, value, false, null, true, method,
								Constants.ENCODING_UTF_8);
						values[i] = composedParam;
					}
					params.put(key, values);
				}
				urlData.setProcessedUrlParams(params);
			}

			// Action url with confidential values
			url = this.getProcessedUrl(urlData);
		}

		return url;

	}
}
