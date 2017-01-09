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
package org.hdiv.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hdiv.session.SessionModel;

/**
 * Context holder for request-specific state. Contains request-specific data for validation and composition phases.
 *
 * @since 3.0.0
 */
public class RequestContext {

	private final HttpServletRequest request;

	private final HttpServletResponse response;

	private final SessionModel session;

	public RequestContext(final HttpServletRequest request) {
		this(request, null);
	}

	public RequestContext(final HttpServletRequest request, final HttpServletResponse response) {
		this.request = request;
		this.response = response;
		session = new HttpSessionModel(request.getSession());
	}

	/**
	 * @return the request
	 */
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * @return the response
	 */
	public HttpServletResponse getResponse() {
		return response;
	}

	/**
	 * @return the session
	 */
	public SessionModel getSession() {
		return session;
	}

}
