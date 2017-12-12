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

import org.hdiv.config.HDIVConfig;
import org.hdiv.context.RequestContext;
import org.hdiv.context.RequestContextHolder;
import org.hdiv.filter.AsyncRequestWrapper;
import org.hdiv.filter.RequestWrapper;
import org.hdiv.filter.ResponseWrapper;
import org.hdiv.session.ISession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link RequestInitializer} implementation with the default behavior.
 * 
 * @author Gotzon Illarramendi
 * @since 2.1.5
 */
public class DefaultRequestInitializer implements RequestInitializer {

	/**
	 * Session object manager.
	 */
	protected ISession session;

	protected HDIVConfig config;

	protected static final Logger log = LoggerFactory.getLogger(DefaultRequestInitializer.class);

	public void initRequest(final RequestContextHolder context) {
		RequestContext ctx = (RequestContext) context;
		// Store session scoped data into request
		ctx.setHdivParameterName(config.getStateParameterName());
		ctx.setHdivModifyParameterName(config.getModifyStateParameterName());
	}

	public void endRequest(final RequestContextHolder context) {
	}

	public RequestWrapper createRequestWrapper(final RequestContextHolder context) {
		return initializeRequestWrapper(new AsyncRequestWrapper(context));
	}

	protected RequestWrapper initializeRequestWrapper(final RequestWrapper requestWrapper) {
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