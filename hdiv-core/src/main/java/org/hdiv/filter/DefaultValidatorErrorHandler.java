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
package org.hdiv.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hdiv.config.HDIVConfig;
import org.hdiv.context.RequestContextHolder;
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
	 * @param ctx request context
	 * @param errors Validation errors
	 * @since 2.1.13
	 */
	public void handleValidatorError(final RequestContextHolder ctx, final List<ValidatorError> errors) {
		HttpServletResponse response = ctx.getResponse();
		if (isPageNotFoundError(errors)) {
			// Page not found in session

			@SuppressWarnings("deprecation")
			HttpSession session = ctx.getRequest().getSession(false);

			if (session == null || session.isNew()) {
				// New session, maybe expired session
				// Redirect to login page instead of error page
				redirectToLoginPage(ctx, response);
			}
			else {
				ValidatorError error = errors.get(0);
				String username = error.getUserName();
				if (username == null || username == IUserData.ANONYMOUS) {
					// Not logged, so send to login page
					redirectToLoginPage(ctx, response);
				}
				else {
					// Logged, send to home
					redirectToHomePage(ctx, response);
				}
			}

		}
		else {

			// Redirect to general error page
			redirectToErrorPage(ctx, response);
		}
	}

	/**
	 * Is the error type HDIVErrorCodes.PAGE_ID_INCORRECT?
	 * 
	 * @param errors Validation errors
	 * @return true if there is any PAGE_ID_INCORRECT error in the list
	 */
	protected boolean isPageNotFoundError(final List<ValidatorError> errors) {

		for (ValidatorError error : errors) {
			if (HDIVErrorCodes.INVALID_PAGE_ID.equals(error.getType())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Redirect to error page if it exists.
	 * 
	 * @param ctx request context
	 * @param response {@link HttpServletResponse} instance
	 */
	protected void redirectToErrorPage(final RequestContextHolder ctx, final HttpServletResponse response) {
		if (config.getErrorPage() != null) {
			redirect(response, ctx.getContextPath() + config.getErrorPage());
		}
		else {
			redirectToDefaultErrorPage(ctx, response);
		}
	}

	/**
	 * Redirect to login page if it exists.
	 * 
	 * @param ctx request context
	 * @param response {@link HttpServletResponse} instance
	 */
	protected void redirectToLoginPage(final RequestContextHolder ctx, final HttpServletResponse response) {
		if (config.getSessionExpiredLoginPage() != null) {
			redirect(response, ctx.getContextPath() + config.getSessionExpiredLoginPage());
		}
		else {
			redirectToErrorPage(ctx, response);
		}
	}

	/**
	 * Redirect to home page if it exists.
	 * 
	 * @param ctx request context
	 * @param response {@link HttpServletResponse} instance
	 */
	protected void redirectToHomePage(final RequestContextHolder ctx, final HttpServletResponse response) {
		if (config.getSessionExpiredHomePage() != null) {
			redirect(response, ctx.getContextPath() + config.getSessionExpiredHomePage());
		}
		else {
			redirectToErrorPage(ctx, response);
		}
	}

	/**
	 * Redirect to the given URL.
	 * 
	 * @param response {@link HttpServletResponse} instance
	 * @param url redirect to
	 */
	protected void redirect(final HttpServletResponse response, final String url) {

		try {
			response.sendRedirect(response.encodeRedirectURL(url));
		}
		catch (IOException e) {
			throw new RuntimeException("Cant redirect to: " + url, e);
		}

	}

	/**
	 * Redirect to the default error page.
	 * 
	 * @param ctx request context
	 * @param response {@link HttpServletResponse} instance
	 */
	@SuppressWarnings("unchecked")
	protected void redirectToDefaultErrorPage(final RequestContextHolder ctx, final HttpServletResponse response) {

		try {

			response.setContentType("text/html");
			PrintWriter out = response.getWriter();

			List<ValidatorError> editableErrors = (List<ValidatorError>) ctx.getSession().getAttribute(Constants.EDITABLE_PARAMETER_ERROR);
			ctx.getSession().removeAttribute(Constants.EDITABLE_PARAMETER_ERROR);

			errorPageWritter.writeErrorPage(ctx, out, editableErrors);
			out.flush();
		}
		catch (IOException e) {
			throw new RuntimeException("Cant redirect to the default error page", e);
		}

	}

	public void setConfig(final HDIVConfig config) {
		this.config = config;
	}

	public void handleValidatorException(final RequestContextHolder context, final Throwable e) {
		// Nothing to do by default
	}

}