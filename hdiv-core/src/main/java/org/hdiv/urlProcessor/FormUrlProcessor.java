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
package org.hdiv.urlProcessor;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.context.RequestContextHolder;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;
import org.hdiv.util.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UrlProcessor for form action urls.
 *
 * @author Gotzon Illarramendi
 */
public class FormUrlProcessor extends AbstractUrlProcessor {

	/**
	 * Commons Logging instance.
	 */
	private static final Logger log = LoggerFactory.getLogger(FormUrlProcessor.class);

	@Deprecated
	public final String processUrl(final HttpServletRequest request, final String url) {
		return processUrl(HDIVUtil.getRequestContext(request), url, Method.POST);
	}

	@Deprecated
	public final String processUrl(final HttpServletRequest request, final String url, final Method method) {
		return processUrl(HDIVUtil.getRequestContext(request), url, method);
	}

	/**
	 * Process form action url to add hdiv state if it is necessary.
	 *
	 * @param request {@link RequestContextHolder} object
	 * @param url url to process
	 * @return processed url
	 */
	public String processUrl(final RequestContextHolder request, final String url) {
		return processUrl(request, url, Method.POST);
	}

	/**
	 * Process form action url to add hdiv state if it is necessary.
	 *
	 * @param request {@link RequestContextHolder} object
	 * @param url url to process
	 * @param method form submit method
	 * @return processed url
	 */
	public String processUrl(final RequestContextHolder request, String url, Method method) {

		if (method == null) {
			method = Method.POST;
		}

		IDataComposer dataComposer = request.getDataComposer();
		if (dataComposer == null) {
			// IDataComposer not initialized on request, request is out of filter
			if (log.isDebugEnabled()) {
				log.debug("IDataComposer not initialized on request. Request doesn't pass through ValidatorFilter, review it's mapping");
			}
			return url;
		}
		String hdivParameter = dataComposer.getHdivParameterName();
		UrlData urlData = createUrlData(url, method, hdivParameter, request);
		if (urlData.isHdivStateNecessary(config)) {
			// the url needs protection
			String stateId = dataComposer.beginRequest(method, urlData.getUrlWithoutContextPath());

			// Publish the state in request to make it accessible on jsp
			request.setFormStateId(stateId);

			// Process url params
			String processedParams = dataComposer.composeParams(urlData.getUrlParams(), method, Constants.ENCODING_UTF_8);
			urlData.setComposedUrlParams(processedParams);

			// Action url with confidential values
			url = getProcessedUrl(dataComposer.getBuilder(), urlData);

		}
		else {
			// Reset state info
			request.setFormStateId(null);
		}

		return url;

	}
}
