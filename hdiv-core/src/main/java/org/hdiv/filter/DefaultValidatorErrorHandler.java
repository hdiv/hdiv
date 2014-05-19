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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hdiv.config.HDIVConfig;
import org.hdiv.logs.IUserData;
import org.hdiv.util.HDIVErrorCodes;

/**
 * Default implementation of {@link ValidatorErrorHandler}.
 * 
 * @author Gotzon Illarramendi
 * @since 2.1.4
 */
public class DefaultValidatorErrorHandler implements ValidatorErrorHandler {

	/**
	 * Hdiv general configuration
	 */
	private HDIVConfig config;

	/**
	 * Application user data
	 */
	private IUserData userData;

	/**
	 * Process a request with validation errors.
	 * 
	 * @param request
	 *            {@link HttpServletRequest} instance
	 * @param response
	 *            {@link HttpServletResponse} instance
	 * @param errorCode
	 *            Error code from {@link HDIVErrorCodes}
	 */
	public void handleValidatorError(HttpServletRequest request, HttpServletResponse response, String errorCode) {

		HttpSession session = request.getSession(false);

		if (HDIVErrorCodes.PAGE_ID_INCORRECT.equals(errorCode)) {
			// Page not found in session

			if (session == null || session.isNew()) {
				// New session, maybe expired session
				// Redirect to login page instead of error page
				this.redirectToLoginPage(request, response);
			} else {
				String username = this.userData.getUsername(request);
				if (username == null || username == IUserData.ANONYMOUS) {
					// Not logged, so send to login page
					this.redirectToLoginPage(request, response);
				} else {
					// Logged, send to home
					this.redirectToHomePage(request, response);
				}
			}

		} else {

			// Redirect to general error page
			redirect(response, request.getContextPath() + this.config.getErrorPage());
		}
	}

	/**
	 * Redirect to login page if it exist
	 * 
	 * @param request
	 *            {@link HttpServletRequest} instance
	 * @param response
	 *            {@link HttpServletResponse} instance
	 */
	private void redirectToLoginPage(HttpServletRequest request, HttpServletResponse response) {
		if (this.config.getSessionExpiredLoginPage() != null) {
			redirect(response, request.getContextPath() + this.config.getSessionExpiredLoginPage());
		} else {
			redirect(response, request.getContextPath() + this.config.getErrorPage());
		}
	}

	/**
	 * Redirect to home page if it exist
	 * 
	 * @param request
	 *            {@link HttpServletRequest} instance
	 * @param response
	 *            {@link HttpServletResponse} instance
	 */
	private void redirectToHomePage(HttpServletRequest request, HttpServletResponse response) {
		if (this.config.getSessionExpiredHomePage() != null) {
			redirect(response, request.getContextPath() + this.config.getSessionExpiredHomePage());
		} else {
			redirect(response, request.getContextPath() + this.config.getErrorPage());
		}
	}

	/**
	 * Redirect to the given url
	 * 
	 * @param response
	 *            {@link HttpServletResponse} instance
	 * @param url
	 *            redirect to
	 */
	protected void redirect(HttpServletResponse response, String url) {

		try {
			response.sendRedirect(response.encodeRedirectURL(url));
		} catch (IOException e) {
			throw new RuntimeException("Cant redirect to: " + url, e);
		}

	}

	public void setConfig(HDIVConfig config) {
		this.config = config;
	}

	public void setUserData(IUserData userData) {
		this.userData = userData;
	}

}
