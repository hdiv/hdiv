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
package org.hdiv.urlProcessor;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;

/**
 * UrlProcessor for form action urls.
 * 
 * @author Gotzon Illarramendi
 */
public class FormUrlProcessor extends AbstractUrlProcessor {

	private static final String FORM_STATE_ID = "hdivFormStateId";

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

		UrlData urlData = super.createUrlData(url, true, request);
		if (super.isHdivStateNecessary(urlData)) {
			// the url needs protection
			IDataComposer dataComposer = HDIVUtil.getDataComposer(request);
			String stateId = dataComposer.beginRequest(urlData.getContextPathRelativeUrl());

			// Publish the state in request to make it accessible on jsp
			request.setAttribute(FORM_STATE_ID, stateId);

			// Process url params
			Map params = urlData.getOriginalUrlParams();
			if (params != null) {
				Iterator it = params.keySet().iterator();
				while (it.hasNext()) {
					String key = (String) it.next();
					String[] values = (String[]) params.get(key);

					for (int i = 0; i < values.length; i++) {
						String value = values[i];
						String composedParam = dataComposer.compose(key, value, false, true, Constants.ENCODING_UTF_8);
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
