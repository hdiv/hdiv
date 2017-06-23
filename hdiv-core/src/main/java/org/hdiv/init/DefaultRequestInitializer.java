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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
public class DefaultRequestInitializer extends HdivParameterInitializer implements RequestInitializer {

	private static final Log log = LogFactory.getLog(DefaultRequestInitializer.class);

	/**
	 * Session object manager.
	 */
	protected ISession session;

	@SuppressWarnings("deprecation")
	public void initRequest(final HttpServletRequest request, final HttpServletResponse response) {

		RequestContext context = new RequestContext(request);

		HDIVUtil.setHdivStateParameterName(request, getDefault(context, Constants.HDIV_PARAMETER, getHdivParameter()));
		HDIVUtil.setModifyHdivStateParameterName(request,
				getDefault(context, Constants.MODIFY_STATE_HDIV_PARAMETER, getModifyHdivParameter()));

		// Store request original request uri
		HDIVUtil.setRequestURI(request.getRequestURI(), request);

	}

	private String getDefault(final RequestContext context, final String parameter, final String defaultValue) {
		String value = session.getAttribute(context, parameter);
		if (value == null) {
			log.error("HttpSession does not contain HDIV state name, this should never happen!!!");
			log.error("Restoring the value in the request, validation errors may appear");
			session.setAttribute(context, parameter, defaultValue);
			value = defaultValue;
		}
		return value;
	}

	public void endRequest(final HttpServletRequest request, final HttpServletResponse response) {
	}

	public RequestWrapper createRequestWrapper(final HttpServletRequest request, final HttpServletResponse response) {
		RequestWrapper requestWrapper = new RequestWrapper(request);
		requestWrapper.setConfidentiality(config.getConfidentiality());
		requestWrapper.setCookiesConfidentiality(config.isCookiesConfidentialityActivated());
		requestWrapper.setSession(session);

		return requestWrapper;
	}

	public ResponseWrapper createResponseWrapper(final HttpServletRequest request, final HttpServletResponse response) {
		ResponseWrapper responseWrapper = new ResponseWrapper(request, response);
		responseWrapper.setConfidentiality(config.getConfidentiality());
		responseWrapper.setAvoidCookiesConfidentiality(!config.isCookiesConfidentialityActivated());
		responseWrapper.setSession(session);

		return responseWrapper;
	}

	/**
	 * @param config the config to set
	 */
	public void setConfig(final HDIVConfig config) {
		this.config = config;
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(final ISession session) {
		this.session = session;
	}

}