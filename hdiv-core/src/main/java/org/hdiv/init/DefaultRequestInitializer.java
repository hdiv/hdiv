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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.context.RequestContext;
import org.hdiv.context.RequestContextHolder;
import org.hdiv.filter.AsyncRequestWrapper;
import org.hdiv.filter.RequestWrapper;
import org.hdiv.filter.ResponseWrapper;
import org.hdiv.session.ISession;

/**
 * {@link RequestInitializer} implementation with the default behavior.
 * 
 * @author Gotzon Illarramendi
 * @since 2.1.5
 */
public class DefaultRequestInitializer extends HdivParameterInitializer implements RequestInitializer {

	/**
	 * Session object manager.
	 */
	protected ISession session;

	private static final Log log = LogFactory.getLog(DefaultRequestInitializer.class);

	public void initRequest(final RequestContextHolder context) {
		RequestContext ctx = (RequestContext) context;
		// Store session scoped data into request
		ctx.setHdivParameterName(getValue(context, DefaultSessionInitializer.HDIV_PARAMETER));
		ctx.setHdivModifyParameterName(getValue(context, DefaultSessionInitializer.MODIFY_STATE_HDIV_PARAMETER));
	}

	private String getValue(final RequestContextHolder context, final String attr) {
		String value = session.getAttribute(context, attr);
		if (value == null) {
			log.error("HttpSession does not contain HDIV state name, this should never happen!!!");
			log.error("Restoring the value in the request, validation errors may appear");

			String defaultValue = null;
			if (DefaultSessionInitializer.HDIV_PARAMETER.equals(attr)) {
				defaultValue = getHdivParameter();
			}
			else if (DefaultSessionInitializer.MODIFY_STATE_HDIV_PARAMETER.equals(attr)) {
				defaultValue = getModifyHdivParameter();
			}
			session.setAttribute(context, attr, defaultValue);
			value = defaultValue;
		}
		return value;
	}

	public void endRequest(final RequestContextHolder context) {
	}

	public RequestWrapper createRequestWrapper(final RequestContextHolder context) {
		RequestWrapper requestWrapper = new AsyncRequestWrapper(context);
		requestWrapper.setConfidentiality(config.getConfidentiality());
		requestWrapper.setCookiesConfidentiality(config.isCookiesConfidentialityActivated());
		requestWrapper.setSession(session);

		return requestWrapper;
	}

	public ResponseWrapper createResponseWrapper(final RequestContextHolder context) {
		ResponseWrapper responseWrapper = new ResponseWrapper(context);
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