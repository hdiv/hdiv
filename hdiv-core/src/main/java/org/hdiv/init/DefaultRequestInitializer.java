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
package org.hdiv.init;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hdiv.config.HDIVConfig;
import org.hdiv.context.RequestContext;
import org.hdiv.filter.RequestWrapper;
import org.hdiv.filter.ResponseWrapper;
import org.hdiv.session.ISession;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;

/**
 * {@link RequestInitializer} implementation with the default behavior.
 * 
 * @author Gotzon Illarramendi
 * @since 2.1.5
 */
public class DefaultRequestInitializer implements RequestInitializer {

	/**
	 * HDIV configuration object
	 */
	protected HDIVConfig config;

	/**
	 * Session object manager.
	 */
	protected ISession session;

	public void initRequest(HttpServletRequest request, HttpServletResponse response) {

		RequestContext context = new RequestContext(request);

		// Store session scoped data into request
		String stateParameterName = this.session.getAttribute(context, Constants.HDIV_PARAMETER);
		String modifyStateParameterName = this.session.getAttribute(context, Constants.MODIFY_STATE_HDIV_PARAMETER);

		HDIVUtil.setHdivStateParameterName(request, stateParameterName);
		HDIVUtil.setModifyHdivStateParameterName(request, modifyStateParameterName);

		// Store request original request uri
		HDIVUtil.setRequestURI(request.getRequestURI(), request);
	}

	public void endRequest(HttpServletRequest request, HttpServletResponse response) {
	}

	public RequestWrapper createRequestWrapper(HttpServletRequest request, HttpServletResponse response) {
		RequestWrapper requestWrapper = new RequestWrapper(request);
		requestWrapper.setConfidentiality(this.config.getConfidentiality());
		requestWrapper.setCookiesConfidentiality(this.config.isCookiesConfidentialityActivated());
		requestWrapper.setSession(this.session);

		return requestWrapper;
	}

	public ResponseWrapper createResponseWrapper(HttpServletRequest request, HttpServletResponse response) {
		ResponseWrapper responseWrapper = new ResponseWrapper(request, response);
		responseWrapper.setConfidentiality(this.config.getConfidentiality());
		responseWrapper.setAvoidCookiesConfidentiality(!this.config.isCookiesConfidentialityActivated());
		responseWrapper.setSession(this.session);

		return responseWrapper;
	}

	/**
	 * @param config the config to set
	 */
	public void setConfig(HDIVConfig config) {
		this.config = config;
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(ISession session) {
		this.session = session;
	}

}