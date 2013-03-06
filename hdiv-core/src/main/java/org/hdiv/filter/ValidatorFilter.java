/**
 * Copyright 2005-2011 hdiv.org
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
import org.hdiv.util.HDIVUtil;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * An unique filter exists within HDIV. This filter has two responsibilities: initialize and validate. In fact, the
 * actual validation is not implemented in this class, it is delegated to ValidatorHelper.
 * 
 * @author Roberto Velasco
 * @author Gorka Vicente
 * @see org.hdiv.filter.ValidatorHelperRequest
 */
public class ValidatorFilter extends OncePerRequestFilter {

	/**
	 * Commons Logging instance.
	 */
	private static Log log = LogFactory.getLog(ValidatorFilter.class);

	/**
	 * HDIV configuration object
	 */
	private HDIVConfig hdivConfig;

	/**
	 * IValidationHelper object
	 */
	private IValidationHelper validationHelper;

	/**
	 * The multipart config
	 */
	private IMultipartConfig multipartConfig;

	/**
	 * Validation error handler
	 */
	private ValidatorErrorHandler errorHandler;

	/**
	 * Creates a new ValidatorFilter object.
	 */
	public ValidatorFilter() {

	}

	/**
	 * Init required dependencies
	 */
	protected void initDependencies() {

		if (this.hdivConfig == null) {
			ServletContext servletContext = getServletContext();
			WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

			this.hdivConfig = (HDIVConfig) context.getBean("config");
			this.validationHelper = (IValidationHelper) context.getBean("validatorHelper");
			if (context.containsBean("multipartConfig")) {
				// For applications without Multipart requests
				this.multipartConfig = (IMultipartConfig) context.getBean("multipartConfig");
			}

			this.errorHandler = (ValidatorErrorHandler) context.getBean("validatorErrorHandler");
		}

	}

	/**
	 * Init request scoped data
	 * 
	 * @param request
	 *            request object
	 */
	protected void initRequestData(HttpServletRequest request) {

		// Put the request in threadlocal
		HDIVUtil.setHttpServletRequest(request);

		// Store request original request uri
		HDIVUtil.setRequestURI(request.getRequestURI(), request);

	}

	/**
	 * Destroy request scoped data
	 * 
	 * @param request
	 *            request object
	 */
	protected void destroyRequestData(HttpServletRequest request) {

		// Erase request from threadlocal
		HDIVUtil.resetLocalData();

	}

	/**
	 * Called by the container each time a request/response pair is passed through the chain due to a client request for
	 * a resource at the end of the chain.
	 * 
	 * @param request
	 *            request object
	 * @param response
	 *            response object
	 * @param filterChain
	 *            filter chain
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse,
	 *      javax.servlet.FilterChain)
	 */
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// Initialize dependencies
		this.initDependencies();

		// Initialize request scoped data
		this.initRequestData(request);

		ResponseWrapper responseWrapper = this.getResponseWrapper(response);
		RequestWrapper requestWrapper = this.getRequestWrapper(request);

		HttpServletRequest multipartProcessedRequest = requestWrapper;

		try {

			boolean legal = false;
			boolean isMultipartException = false;

			if (this.isMultipartContent(request.getContentType())) {

				requestWrapper.setMultipart(true);

				try {

					if (this.multipartConfig == null) {
						throw new RuntimeException(
								"No 'multipartConfig' configured. It is required to multipart requests.");
					}

					multipartProcessedRequest = this.multipartConfig.handleMultipartRequest(requestWrapper,
							super.getServletContext());

				} catch (HdivMultipartException e) {
					request.setAttribute(IMultipartConfig.FILEUPLOAD_EXCEPTION, e);
					isMultipartException = true;
					legal = true;
				}
			}

			ValidatorHelperResult result = null;
			if (!isMultipartException) {
				result = this.validationHelper.validate(multipartProcessedRequest);
				legal = result.isValid();
			}

			if (legal || this.hdivConfig.isDebugMode()) {
				processRequest(multipartProcessedRequest, responseWrapper, filterChain);
			} else {

				// Call to ValidatorErrorHandler
				this.errorHandler.handleValidatorError(multipartProcessedRequest, response, result.getErrorCode());
			}

		} catch (IOException e) {
			// Internal framework exception, rethrow exception
			throw e;
		} catch (ServletException e) {
			// Internal framework exception, rethrow exception
			throw e;
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Exception in request validation:");
				log.error("Message: " + e.getMessage());

				StringBuffer buffer = new StringBuffer();
				StackTraceElement[] trace = e.getStackTrace();
				for (int i = 0; i < trace.length; i++) {
					buffer.append("\tat " + trace[i] + System.getProperty("line.separator"));
				}
				log.error("StackTrace: " + buffer.toString());
				log.error("Cause: " + e.getCause());
				log.error("Exception: " + e.toString());
			}
			// Redirect to error page
			if (!this.hdivConfig.isDebugMode()) {
				response.sendRedirect(response.encodeRedirectURL(request.getContextPath()
						+ this.hdivConfig.getErrorPage()));
			}
		} finally {

			// Destroy request scoped data
			this.destroyRequestData(request);
		}
	}

	/**
	 * Utility method that determines whether the request contains multipart content.
	 * 
	 * @param contentType
	 *            content type
	 * @return <code>true</code> if the request is multipart. <code>false</code> otherwise.
	 */
	public boolean isMultipartContent(String contentType) {
		return ((contentType != null) && (contentType.indexOf("multipart/form-data") != -1));
	}

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
	 * 
	 * @param requestWrapper
	 *            request wrapper
	 * @param responseWrapper
	 *            response wrapper
	 * @param filterChain
	 *            filter chain
	 * @throws Exception
	 *             if there is an error in request process.
	 */
	protected void processRequest(HttpServletRequest requestWrapper, ResponseWrapper responseWrapper,
			FilterChain filterChain) throws IOException, ServletException {

		this.validationHelper.startPage(requestWrapper);
		filterChain.doFilter(requestWrapper, responseWrapper);
		this.validationHelper.endPage(requestWrapper);
	}

	/**
	 * Create request wrapper.
	 * 
	 * @param request
	 *            HTTP request
	 * @return the request wrapper
	 */
	protected RequestWrapper getRequestWrapper(HttpServletRequest request) {

		RequestWrapper requestWrapper = new RequestWrapper(request);
		requestWrapper.setConfidentiality(this.hdivConfig.getConfidentiality());
		requestWrapper.setCookiesConfidentiality(this.hdivConfig.isCookiesConfidentialityActivated());

		return requestWrapper;
	}

	/**
	 * Create response wrapper.
	 * 
	 * @param response
	 *            HTTP response
	 * @return the response wrapper
	 */
	protected ResponseWrapper getResponseWrapper(HttpServletResponse response) {

		ResponseWrapper responseWrapper = new ResponseWrapper(response);
		responseWrapper.setConfidentiality(this.hdivConfig.getConfidentiality().booleanValue());
		responseWrapper.setAvoidCookiesConfidentiality(!this.hdivConfig.isCookiesConfidentialityActivated());

		return responseWrapper;
	}

}
