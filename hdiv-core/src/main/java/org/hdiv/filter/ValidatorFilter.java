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

import org.hdiv.config.HDIVConfig;
import org.hdiv.config.multipart.IMultipartConfig;
import org.hdiv.config.multipart.exception.HdivMultipartException;
import org.hdiv.context.RequestContextFactory;
import org.hdiv.context.RequestContextHolder;
import org.hdiv.exception.HDIVException;
import org.hdiv.exception.SharedHdivException;
import org.hdiv.init.RequestInitializer;
import org.hdiv.logs.IUserData;
import org.hdiv.logs.Logger;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.util.HDIVUtil;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
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
	private static final org.slf4j.Logger log = LoggerFactory.getLogger(ValidatorFilter.class);

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

	private RequestContextFactory requestContextFactory;

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
	private void initDependencies(final HttpServletRequest request) {
		if (validationContextFactory == null) {
			synchronized (this) {
				if (hdivConfig == null) {
					ServletContext servletContext = getServletContext();
					ApplicationContext context = HDIVUtil.findWebApplicationContext(servletContext);

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
					requestContextFactory = context.getBean(RequestContextFactory.class);
					userData = context.getBean(IUserData.class);
					logger = context.getBean(Logger.class);
					errorHandler = context.getBean(ValidatorErrorHandler.class);
					requestInitializer = context.getBean(RequestInitializer.class);
					validationContextFactory = context.getBean(ValidationContextFactory.class);
					HDIVUtil.checkCustomImage(request);
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
		initDependencies(request);

		if (validationHelper.isInternal(request, response)) {
			return;
		}

		RequestContextHolder ctx = requestContextFactory.create(requestInitializer, request, response);
		// Initialize request scoped data
		requestInitializer.initRequest(ctx);

		@SuppressWarnings("deprecation")
		RequestWrapper requestWrapper = (RequestWrapper) ctx.getRequest();
		ResponseWrapper responseWrapper = (ResponseWrapper) ctx.getResponse();

		HttpServletRequest multipartProcessedRequest = requestWrapper;

		boolean isMultipartProcessed = false;

		try {

			boolean legal = false;
			boolean isMultipartException = false;

			if (isMultipartContent(request)) {

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
			ValidationContext context = validationContextFactory.newInstance(ctx, validationHelper, hdivConfig.isUrlObfuscation());
			ctx.setValidationContext(context);
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
				if (e.getResult() == ValidatorHelperResult.PEN_TESTING) {
					return;
				}
				errors = e.getResult().getErrors();
			}
			catch (Exception e) {
				errors = findErrors(e, context.getRequestedTarget(), false);
				if (errors == null) {
					/**
					 * It is not a HdivException... but it was launched in our code...
					 */
					if (log.isErrorEnabled()) {
						log.error("Exception in request validation in target:" + context.getRequestedTarget(), e);
					}
					legal = true;
					errorHandler.handleValidatorException(ctx, e);
				}
			}

			boolean hasEditableError = false;
			if (errors != null && !errors.isEmpty()) {
				// Complete error data
				completeErrorData(multipartProcessedRequest, errors);

				// Log the errors
				logValidationErrors(errors);

				hasEditableError = processEditableValidationErrors(ctx, errors);
			}

			if (legal || hdivConfig.isDebugMode() || hasEditableError && !hdivConfig.isShowErrorPageOnEditableValidation()) {

				processRequest(ctx, multipartProcessedRequest, responseWrapper, filterChain, context.getRedirect());
			}
			else {

				// Call to ValidatorErrorHandler
				errorHandler.handleValidatorError(ctx, errors);
			}

		}
		catch (Exception e) {
			List<ValidatorError> errors = findErrors(e, request.getRequestURI(), true);
			if (errors != null) {
				// Show error page
				if (!hdivConfig.isDebugMode()) {
					errorHandler.handleValidatorError(ctx, errors);
				}
			}
			else {
				/**
				 * Try to rethrow the same exception if posible
				 */

				if (e instanceof RuntimeException) {
					throw (RuntimeException) e;
				}
				if (e instanceof ServletException) {
					throw (ServletException) e;
				}
				if (e instanceof IOException) {
					throw (IOException) e;
				}
				throw new RuntimeException(e);
			}
		}
		finally {

			if (isMultipartProcessed) {
				// Cleanup multipart
				multipartConfig.cleanupMultipart(multipartProcessedRequest);
			}

			// Destroy request scoped data
			requestInitializer.endRequest(ctx);
		}
	}

	private List<ValidatorError> findErrors(final Throwable e, final String target, final boolean allowUncontrolledOrigin) {
		Throwable current = e;
		do {
			if (!(current instanceof SharedHdivException)) {
				current = current.getCause();
			}
		} while (current != null && !(current instanceof SharedHdivException));
		if (current instanceof SharedHdivException) {
			if (log.isErrorEnabled()) {
				log.error("Exception in request validation", current);
			}
			if (!allowUncontrolledOrigin) {
				// Check uncontrolledOrigin
				Throwable invalid;
				while ((invalid = current.getCause()) != null) {
					if (invalid instanceof NullPointerException || invalid instanceof IndexOutOfBoundsException
							|| invalid instanceof OutOfMemoryError || invalid instanceof ClassNotFoundException
							|| invalid instanceof StackOverflowError || invalid instanceof ClassCastException) {
						return null;
					}
				}

			}
			return Collections.singletonList(new ValidatorError(current, target));
		}
		return null;
	}

	/**
	 * Utility method that determines whether the request contains multipart content.
	 *
	 * @param request the request
	 * @return <code>true</code> if the request is multipart. <code>false</code> otherwise.
	 */
	protected boolean isMultipartContent(final HttpServletRequest request) {
		return HDIVUtil.isMultipartContent(request);
	}

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
	 *
	 * @param ctx request context
	 * @param requestWrapper request wrapper
	 * @param responseWrapper response wrapper
	 * @param filterChain filter chain
	 * @param obfuscated obfuscated
	 * @throws IOException if there is an error in request process.
	 * @throws ServletException if there is an error in request process.
	 */
	protected final void processRequest(final RequestContextHolder ctx, final HttpServletRequest requestWrapper,
			final ResponseWrapper responseWrapper, final FilterChain filterChain, final String obfuscated)
			throws IOException, ServletException {
		SharedHdivException ex = null;
		try {
			validationHelper.startPage(ctx);
		}
		catch (SharedHdivException e) {
			if (hdivConfig.isDebugMode()) {
				ex = e;
			}
			else {
				throw e;
			}
		}
		try {
			if (obfuscated != null) {
				requestWrapper.getRequestDispatcher(obfuscated).forward(requestWrapper, responseWrapper);
			}
			else {
				filterChain.doFilter(requestWrapper, responseWrapper);
			}

		}
		finally {
			validationHelper.endPage(ctx);
		}
		if (ex != null) {
			throw new HDIVException("Wrapped exception on debug", ex);
		}
	}

	/**
	 * Complete {@link ValidatorError} containing data including user related info.
	 *
	 * @param request request object
	 * @param errors all validation errors
	 */
	protected void completeErrorData(final HttpServletRequest request, final List<ValidatorError> errors) {

		String localIp = userData.getLocalIp(request);
		String remoteIp = userData.getRemoteIp(request);
		String userName = userData.getUsername(request);

		String contextPath = request.getContextPath();
		for (ValidatorError error : errors) {

			error.setLocalIp(localIp);
			error.setRemoteIp(remoteIp);
			error.setUserName(userName);

			// Include context path in the target
			String target = error.getTarget();
			if (target != null && !target.startsWith(contextPath)) {
				target = request.getContextPath() + target;
			}
			else if (target == null) {
				target = request.getRequestURI();
			}
			error.setTarget(target);
		}
	}

	/**
	 * Log validation errors
	 *
	 * @param errors all validation errors
	 */
	protected void logValidationErrors(final List<ValidatorError> errors) {

		for (ValidatorError error : errors) {
			// Log the error
			logger.log(error);
		}
	}

	/**
	 * Process editable validation errors. Add them to the request scope to read later from the web framework.
	 *
	 * @param request request object
	 * @param errors all validation errors
	 * @return true if there is a editable validation error
	 */
	protected boolean processEditableValidationErrors(final RequestContextHolder request, final List<ValidatorError> errors) {

		List<ValidatorError> editableErrors = new ArrayList<ValidatorError>();
		for (ValidatorError error : errors) {
			if (HDIVErrorCodes.INVALID_EDITABLE_VALUE.equals(error.getType())) {
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