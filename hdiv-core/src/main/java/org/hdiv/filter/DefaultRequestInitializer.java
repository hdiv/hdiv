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
package org.hdiv.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hdiv.config.HDIVConfig;
import org.hdiv.util.HDIVUtil;

/**
 * {@link RequestInitializer} implementation with default behavior.
 * 
 * @author Gotzon Illarramendi
 * @since 2.1.5
 */
public class DefaultRequestInitializer implements RequestInitializer {

	/**
	 * HDIV configuration object
	 */
	protected HDIVConfig config;

	public void initRequest(HttpServletRequest request) {

		// Put the request in threadlocal
		HDIVUtil.setHttpServletRequest(request);

		// Store request original request uri
		HDIVUtil.setRequestURI(request.getRequestURI(), request);

	}

	public void endRequest(HttpServletRequest request) {

		// Erase request from threadlocal
		HDIVUtil.resetLocalData();
	}

	public RequestWrapper createRequestWrapper(HttpServletRequest request) {
		RequestWrapper requestWrapper = new RequestWrapper(request);
		requestWrapper.setConfidentiality(this.config.getConfidentiality());
		requestWrapper.setCookiesConfidentiality(this.config.isCookiesConfidentialityActivated());

		return requestWrapper;
	}

	public ResponseWrapper createResponseWrapper(HttpServletResponse response) {
		ResponseWrapper responseWrapper = new ResponseWrapper(response);
		responseWrapper.setConfidentiality(this.config.getConfidentiality());
		responseWrapper.setAvoidCookiesConfidentiality(!this.config.isCookiesConfidentialityActivated());

		return responseWrapper;
	}

	/**
	 * @param config
	 *            the config to set
	 */
	public void setConfig(HDIVConfig config) {
		this.config = config;
	}

}
