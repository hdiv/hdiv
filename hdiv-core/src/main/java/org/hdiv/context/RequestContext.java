/**
 * Copyright 2005-2015 hdiv.org
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
 * @since 2.2.0
 */
public class RequestContext {

	protected HttpServletRequest request;

	protected HttpServletResponse response;

	protected HttpSession session;

	public RequestContext(HttpServletRequest request) {
		this.request = request;
		this.session = request.getSession();
	}

	public RequestContext(HttpSession session) {
		this.session = session;
	}

	public RequestContext(HttpServletRequest request, HttpSession session) {
		this.request = request;
		this.session = session;
	}

	public RequestContext(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		this.session = request.getSession();
	}

	public RequestContext(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		this.request = request;
		this.response = response;
		this.session = session;
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
		return session;
	}

}
