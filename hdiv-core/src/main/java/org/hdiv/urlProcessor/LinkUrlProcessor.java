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
 * UrlProcessor for link and redirect urls.
 * 
 * @author Gotzon Illarramendi
 */
public class LinkUrlProcessor extends AbstractUrlProcessor {

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

		UrlData urlData = super.createUrlData(url, false, request);
		if (super.isHdivStateNecessary(urlData)) {
			// the url needs protection
			IDataComposer dataComposer = HDIVUtil.getDataComposer(request);
			dataComposer.beginRequest(urlData.getContextPathRelativeUrl());

			Map params = urlData.getOriginalUrlParams();
			if (params != null) {
				Iterator it = params.keySet().iterator();
				while (it.hasNext()) {
					String key = (String) it.next();
					String value = (String) params.get(key);

					String composedParam = dataComposer.compose(key, value, false, false, encoding);

					params.put(key, composedParam);
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
