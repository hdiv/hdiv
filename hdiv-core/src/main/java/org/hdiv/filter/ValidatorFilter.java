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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.config.multipart.IMultipartConfig;
import org.hdiv.config.multipart.exception.HdivMultipartException;
import org.hdiv.exception.HDIVException;
import org.hdiv.init.RequestInitializer;
import org.hdiv.logs.IUserData;
import org.hdiv.logs.Logger;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.util.HDIVUtil;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * An unique filter exists within HDIV. This filter has two responsibilities: initialize and validate. In fact, the actual validation is not
 * implemented in this class, it is delegated to ValidatorHelper.
 *
 * @author Roberto Velasco
 * @author Gorka Vicente
 * @see org.hdiv.filter.ValidatorHelperRequest
 */
public class ValidatorFilter extends OncePerRequestFilter {

	/**
	 * Commons Logging instance.
	 */
	private static final Log log = LogFactory.getLog(ValidatorFilter.class);

	/**
	 * HDIV configuration object.
	 */
	protected HDIVConfig hdivConfig;

	/**
	 * IValidationHelper object.
	 */
	protected IValidationHelper validationHelper;

	/**
	 * The multipart configuration.
	 */
	protected IMultipartConfig multipartConfig;

	/**
	 * Logger to print the possible attacks detected by HDIV.
	 */
	protected Logger logger;

	/**
	 * Validation error handler.
	 */
	protected ValidatorErrorHandler errorHandler;

	/**
	 * Request data and wrappers initializer.
	 */
	protected RequestInitializer requestInitializer;

	/**
	 * Obtains user data from the request
	 */
	protected IUserData userData;

	/**
	 * Creates ValidationContext
	 */
	protected ValidationContextFactory validationContextFactory;

	/**
	 * Initialize required dependencies.
	 */
	protected void initDependencies() {
		if (validationContextFactory == null) {
			synchronized (this) {
				if (hdivConfig == null) {
					ServletContext servletContext = getServletContext();
					WebApplicationContext context = HDIVUtil.findWebApplicationContext(servletContext);

					hdivConfig = context.getBean(HDIVConfig.class);
					validationHelper = context.getBean(IValidationHelper.class);

					String[] names = context.getBeanNamesForType(IMultipartConfig.class);
					if (names.length > 1) {
						throw new HDIVException("More than one bean of type 'multipartConfig' is defined.");
					}
					if (names.length == 1) {
						multipartConfig = context.getBean(IMultipartConfig.class);
					}
					else {
						// For applications without Multipart requests
						multipartConfig = null;
					}

					userData = context.getBean(IUserData.class);
					logger = context.getBean(Logger.class);
					errorHandler = context.getBean(ValidatorErrorHandler.class);
					requestInitializer = context.getBean(RequestInitializer.class);
					validationContextFactory = context.getBean(ValidationContextFactory.class);
				}
			}
		}
	}

	/**
	 * Called by the container each time a request/response pair is passed through the chain due to a client request for a resource at the
	 * end of the chain.
	 *
	 * @param request request object
	 * @param response response object
	 * @param filterChain filter chain
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
			throws ServletException, IOException {
		// Initialize dependencies
		initDependencies();

		// Initialize request scoped data
		requestInitializer.initRequest(request, response);

		RequestWrapper requestWrapper = requestInitializer.createRequestWrapper(request, response);
		ResponseWrapper responseWrapper = requestInitializer.createResponseWrapper(request, response);

		HttpServletRequest multipartProcessedRequest = requestWrapper;

		boolean isMultipartProcessed = false;

		try {

			boolean legal = false;
			boolean isMultipartException = false;

			if (isMultipartContent(request.getContentType())) {

				requestWrapper.setMultipart(true);

				try {

					if (multipartConfig == null) {
						throw new RuntimeException("No 'multipartConfig' configured. It is required for multipart requests.");
					}

					multipartProcessedRequest = multipartConfig.handleMultipartRequest(requestWrapper, super.getServletContext());
					isMultipartProcessed = true;

				}
				catch (HdivMultipartException e) {
					request.setAttribute(IMultipartConfig.FILEUPLOAD_EXCEPTION, e);
					isMultipartException = true;
					legal = true;
				}
			}
			ValidationContext context = validationContextFactory.newInstance(multipartProcessedRequest, validationHelper,
					hdivConfig.isUrlObfuscation());
			List<ValidatorError> errors = null;
			try {
				ValidatorHelperResult result = null;
				if (!isMultipartException) {
					result = validationHelper.validate(context);
					legal = result.isValid();

					// Store validation result in request
					request.setAttribute(Constants.VALIDATOR_HELPER_RESULT_NAME, result);
				}

				// All errors, integrity and editable validation
				errors = result == null ? null : result.getErrors();
			}
			catch (ValidationErrorException e) {
				errors = e.getResult().getErrors();
			}

			boolean hasEditableError = false;
			if (errors != null && !errors.isEmpty()) {
				// Complete error data
				completeErrorData(multipartProcessedRequest, errors);

				// Log the errors
				logValidationErrors(errors);

				hasEditableError = processEditableValidationErrors(multipartProcessedRequest, errors);
			}

			if (legal || hdivConfig.isDebugMode() || hasEditableError && !hdivConfig.isShowErrorPageOnEditableValidation()) {

				processRequest(multipartProcessedRequest, responseWrapper, filterChain, context.getRedirect());
			}
			else {

				// Call to ValidatorErrorHandler
				errorHandler.handleValidatorError(multipartProcessedRequest, responseWrapper, errors);
			}

		}
		catch (Exception e) {

			Throwable hdivException = e;
			do {
				if (!(hdivException instanceof HDIVException)) {
					hdivException = hdivException.getCause();
				}
			} while (hdivException != null && !(hdivException instanceof HDIVException));
			if (hdivException instanceof HDIVException) {
				if (log.isErrorEnabled()) {
					log.error("Exception in request validation", hdivException);
				}
				// Show error page
				if (!hdivConfig.isDebugMode()) {
					List<ValidatorError> errors = Collections
							.singletonList(new ValidatorError(hdivException.getMessage(), request.getRequestURI()));
					errorHandler.handleValidatorError(multipartProcessedRequest, responseWrapper, errors);
				}
			}
			else {
				throw new RuntimeException(e);
			}
		}
		finally {

			if (isMultipartProcessed) {
				// Cleanup multipart
				multipartConfig.cleanupMultipart(multipartProcessedRequest);
			}

			// Destroy request scoped data
			requestInitializer.endRequest(multipartProcessedRequest, responseWrapper);
		}
	}

	/**
	 * Utility method that determines whether the request contains multipart content.
	 *
	 * @param contentType content type
	 * @return <code>true</code> if the request is multipart. <code>false</code> otherwise.
	 */
	protected boolean isMultipartContent(final String contentType) {
		return contentType != null && contentType.indexOf("multipart/form-data") != -1;
	}

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
	 *
	 * @param requestWrapper request wrapper
	 * @param responseWrapper response wrapper
	 * @param filterChain filter chain
	 * @throws IOException if there is an error in request process.
	 * @throws ServletException if there is an error in request process.
	 */
	protected void processRequest(final HttpServletRequest requestWrapper, final ResponseWrapper responseWrapper,
			final FilterChain filterChain, final String obfuscated) throws IOException, ServletException {
		validationHelper.startPage(requestWrapper);
		try {
			if (obfuscated != null) {
				requestWrapper.getRequestDispatcher(obfuscated).forward(requestWrapper, responseWrapper);
			}
			else {
				filterChain.doFilter(requestWrapper, responseWrapper);
			}
		}
		finally {
			validationHelper.endPage(requestWrapper);
		}
	}

	/**
	 * Complete {@link ValidatorError} containing data including user related info.
	 *
	 * @param request request object
	 * @param errors all validation errors
	 */
	protected void completeErrorData(final HttpServletRequest request, final List<ValidatorError> errors) {

		String localIp = getUserLocalIP(request);
		String remoteIp = request.getRemoteAddr();
		String userName = userData.getUsername(request);

		String contextPath = request.getContextPath();
		for (ValidatorError error : errors) {

			error.setLocalIp(localIp);
			error.setRemoteIp(remoteIp);
			error.setUserName(userName);

			// Include context path in the target
			String target = error.getTarget();
			if (!target.startsWith(contextPath)) {
				target = request.getContextPath() + target;
			}
			error.setTarget(target);
		}
	}

	/**
	 * Obtain user local IP.
	 *
	 * @param request the HttpServletRequest of the request
	 * @return Returns the remote user IP address if behind the proxy.
	 */
	protected String getUserLocalIP(final HttpServletRequest request) {
		if (request.getHeader("X-Forwarded-For") == null) {
			return request.getRemoteAddr();
		}
		else {
			return request.getHeader("X-Forwarded-For");
		}
	}

	/**
	 * Log validation errors
	 *
	 * @param request request object
	 * @param errors all validation errors
	 */
	protected void logValidationErrors(final List<ValidatorError> errors) {

		for (ValidatorError error : errors) {
			// Log the error
			logger.log(error);
			System.out.println(error);
		}

	}

	/**
	 * Process editable validation errors. Add them to the request scope to read later from the web framework.
	 *
	 * @param request request object
	 * @param errors all validation errors
	 * @return true if there is a editable validation error
	 */
	protected boolean processEditableValidationErrors(final HttpServletRequest request, final List<ValidatorError> errors) {

		List<ValidatorError> editableErrors = new ArrayList<ValidatorError>();
		for (ValidatorError error : errors) {
			if (HDIVErrorCodes.EDITABLE_VALIDATION_ERROR.equals(error.getType())) {
				editableErrors.add(error);
			}
		}
		if (!editableErrors.isEmpty() && !hdivConfig.isDebugMode()) {

			// Put the errors on request to be accessible from the Web framework
			request.setAttribute(Constants.EDITABLE_PARAMETER_ERROR, editableErrors);

			if (hdivConfig.isShowErrorPageOnEditableValidation()) {
				// Redirect to error page
				// Put errors in session to be accessible from error page
				request.getSession().setAttribute(Constants.EDITABLE_PARAMETER_ERROR, editableErrors);
			}
		}
		return !editableErrors.isEmpty();
	}

}