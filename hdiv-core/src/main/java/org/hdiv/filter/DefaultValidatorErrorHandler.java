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
package org.hdiv.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hdiv.config.HDIVConfig;
import org.hdiv.logs.IUserData;
import org.hdiv.util.Constants;
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
	protected HDIVConfig config;

	/**
	 * Helper class to create default error page HTML.
	 */
	protected DefaultErrorPageWritter errorPageWritter = new DefaultErrorPageWritter();

	/**
	 * Process a request with validation errors.
	 * 
	 * @param request
	 *            {@link HttpServletRequest} instance
	 * @param response
	 *            {@link HttpServletResponse} instance
	 * @param errors
	 *            Validation errors
	 * @since 2.1.13
	 */
	public void handleValidatorError(HttpServletRequest request, HttpServletResponse response,
			List<ValidatorError> errors) {

		HttpSession session = request.getSession(false);

		if (this.isPageNotFoundError(errors)) {
			// Page not found in session

			if (session == null || session.isNew()) {
				// New session, maybe expired session
				// Redirect to login page instead of error page
				this.redirectToLoginPage(request, response);
			} else {
				ValidatorError error = errors.get(0);
				String username = error.getUserName();
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
			redirectToErrorPage(request, response);
		}
	}

	/**
	 * Is the error type HDIVErrorCodes.PAGE_ID_INCORRECT?
	 * 
	 * @param errors
	 *            Validation errors
	 * @return true if there is any PAGE_ID_INCORRECT error in the list
	 */
	protected boolean isPageNotFoundError(List<ValidatorError> errors) {

		for (ValidatorError error : errors) {
			if (HDIVErrorCodes.PAGE_ID_INCORRECT.equals(error.getType())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Redirect to error page if it exists.
	 * 
	 * @param request
	 *            {@link HttpServletRequest} instance
	 * @param response
	 *            {@link HttpServletResponse} instance
	 */
	protected void redirectToErrorPage(HttpServletRequest request, HttpServletResponse response) {
		if (this.config.getErrorPage() != null) {
			redirect(response, request.getContextPath() + this.config.getErrorPage());
		} else {
			redirectToDefaultErrorPage(request, response);
		}
	}

	/**
	 * Redirect to login page if it exists.
	 * 
	 * @param request
	 *            {@link HttpServletRequest} instance
	 * @param response
	 *            {@link HttpServletResponse} instance
	 */
	protected void redirectToLoginPage(HttpServletRequest request, HttpServletResponse response) {
		if (this.config.getSessionExpiredLoginPage() != null) {
			redirect(response, request.getContextPath() + this.config.getSessionExpiredLoginPage());
		} else {
			redirectToErrorPage(request, response);
		}
	}

	/**
	 * Redirect to home page if it exists.
	 * 
	 * @param request
	 *            {@link HttpServletRequest} instance
	 * @param response
	 *            {@link HttpServletResponse} instance
	 */
	protected void redirectToHomePage(HttpServletRequest request, HttpServletResponse response) {
		if (this.config.getSessionExpiredHomePage() != null) {
			redirect(response, request.getContextPath() + this.config.getSessionExpiredHomePage());
		} else {
			redirectToErrorPage(request, response);
		}
	}

	/**
	 * Redirect to the given URL.
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

	/**
	 * Redirect to the default error page.
	 * 
	 * @param request
	 *            {@link HttpServletRequest} instance
	 * @param response
	 *            {@link HttpServletResponse} instance
	 */
	@SuppressWarnings("unchecked")
	protected void redirectToDefaultErrorPage(HttpServletRequest request, HttpServletResponse response) {

		try {

			response.setContentType("text/html");
			PrintWriter out = response.getWriter();

			Map<String, String[]> editableErrors = (Map<String, String[]>) request.getSession().getAttribute(
					Constants.EDITABLE_PARAMETER_ERROR);
			request.getSession().removeAttribute(Constants.EDITABLE_PARAMETER_ERROR);

			this.errorPageWritter.writetErrorPage(out, editableErrors);
			out.flush();
		} catch (IOException e) {
			throw new RuntimeException("Cant redirect to the default error page", e);
		}

	}

	public void setConfig(HDIVConfig config) {
		this.config = config;
	}

}