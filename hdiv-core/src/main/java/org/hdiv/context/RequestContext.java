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
import javax.servlet.http.HttpSession;

/**
 * Context holder for request-specific state. Contains request-specific data for validation and composition phases.
 *
 * @since 3.0.0
 */
public class RequestContext {

	private HttpServletRequest request;

	private HttpServletResponse response;

	private HttpSession session;

	public RequestContext(final HttpServletRequest request) {
		this(request, null);
	}

	public RequestContext(final HttpSession session) {
		this.session = session;
	}

	public RequestContext(final HttpServletRequest request, final HttpServletResponse response) {
		this.request = request;
		this.response = response;
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
	public HttpSession getSession() {
		if (session == null) {
			return request.getSession();
		}
		return session;
	}

}
