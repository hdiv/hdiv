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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.context.RequestContextHolder;
import org.hdiv.state.IState;
import org.hdiv.util.HDIVUtil;

/**
 * UrlProcessor implementation for {@link IState} restore and URL validation purpose method.
 *
 * @since 2.1.7
 */
public class BasicUrlProcessor extends AbstractUrlProcessor {

	@Deprecated
	public final BasicUrlData createBasicUrlData(final String url, final HttpServletRequest request) {
		return createBasicUrlData(url, HDIVUtil.getRequestContext(request));
	}

	/**
	 * Create {@link UrlData} instance only with the ContextPath relative url and parameters in a Map.
	 *
	 * @param url original url, must be context relative
	 * @param request {@link HttpServletRequest} object
	 * @return new instance of {@link BasicUrlData}
	 */
	public BasicUrlData createBasicUrlData(String url, final RequestContextHolder request) {

		BasicUrlData urlData = new BasicUrlData();

		// Remove parameters
		int paramInit = url.indexOf('?');
		if (paramInit > -1) {
			String urlParams = url.substring(paramInit + 1);
			Map<String, String[]> ulrParamsMap = getUrlParamsAsMap(request.getHdivParameterName(), new StringBuilder(128), urlParams);
			urlData.setUrlParams(ulrParamsMap);
			url = url.substring(0, paramInit);
		}

		urlData.setContextPathRelativeUrl(url);

		return urlData;
	}

	@Deprecated
	public final BasicUrlData processUrl(final HttpServletRequest request, final String url) {
		return createBasicUrlData(url, request);
	}

	/**
	 * Creates {@link BasicUrlData} instance with contextPath relative URL and parameters processed.
	 *
	 * @param request {@link HttpServletRequest} object
	 * @param url URL to process
	 * @return {@link BasicUrlData} instance
	 */
	public BasicUrlData processUrl(final RequestContextHolder request, final String url) {
		return createBasicUrlData(url, request);
	}

}
