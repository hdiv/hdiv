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

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.exception.HDIVException;
import org.hdiv.filter.AsyncRequestWrapper;
import org.hdiv.filter.RequestWrapper;
import org.hdiv.filter.ValidationContext;
import org.hdiv.session.SessionModel;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;

/**
 * Context holder for request-specific state. Contains request-specific data for validation and composition phases.
 *
 * @since 3.0.0
 */
public class RequestContext implements RequestContextHolder {

	protected HttpServletRequest request;

	protected HttpServletResponse response;

	protected SessionModel session;

	protected String modifyParameterName;

	protected String hdivParameterName;

	private String requestURI;

	private String baseURL;

	private int currentPageId;

	private Boolean isAjaxRequest;

	private IDataComposer dataComposer;

	private long renderTime;

	private final Log log = LogFactory.getLog(RequestContextHolder.class);

	private String formStateId;

	private String redirect;

	private ValidationContext validationContext;

	@SuppressWarnings("deprecation")
	public RequestContext(final HttpServletRequest request, final HttpServletResponse response) {
		this.request = request;
		this.response = response;
		requestURI = request.getRequestURI();
		request.setAttribute(Constants.HDIV_REQUEST_CONTEXT, this);
		doCreateSession();
	}

	@Deprecated
	public void doCreateSession() {
		session = new HttpSessionModel(request.getSession());
	}

	public final void update(final HttpServletRequest request, final HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public String getParameter(final String name) {
		return request.getParameter(name);
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

	public String getHdivParameterName() {
		return hdivParameterName;
	}

	public void setHdivParameterName(final String name) {
		hdivParameterName = name;
	}

	public String getHdivModifyParameterName() {
		return modifyParameterName;
	}

	public void setHdivModifyParameterName(final String name) {
		modifyParameterName = name;
	}

	public String getHdivState() {
		return request.getParameter(getHdivParameterName());
	}

	public String getRequestURI() {
		return requestURI;
	}

	public void setRequestURI(final String requestURI) {
		this.requestURI = requestURI;
	}

	public int getCurrentPageId() {
		return currentPageId;
	}

	public void setCurrentPageId(final int currentPageId) {
		this.currentPageId = currentPageId;
	}

	public String getBaseURL() {
		return baseURL;
	}

	public void setBaseURL(final String baseURL) {
		this.baseURL = baseURL;
	}

	/**
	 * Checks if request is an ajax request and store the result in a request's attribute
	 *
	 * @return isAjaxRquest
	 */
	public final boolean isAjax() {
		if (isAjaxRequest == null) {
			String xRequestedWithValue = request.getHeader("x-requested-with");
			isAjaxRequest = xRequestedWithValue != null ? "XMLHttpRequest".equalsIgnoreCase(xRequestedWithValue) : false;
		}
		return isAjaxRequest;
	}

	public void clearAjax() {
		// Only for testing
		isAjaxRequest = null;
	}

	public String getUrlWithoutContextPath() {
		return requestURI.substring(request.getContextPath().length());
	}

	public IDataComposer getDataComposer() {
		return dataComposer;
	}

	public void setDataComposer(final IDataComposer dataComposer) {
		this.dataComposer = dataComposer;
	}

	public void addRenderTime(long time) {
		time = System.nanoTime() - time;
		if (log.isDebugEnabled()) {
			log.debug("render-time-processUrl (ms): " + time / 1000000.0);
		}
		renderTime += time;
	}

	public long getRenderTime() {
		return renderTime;
	}

	public String getMethod() {
		return request.getMethod();
	}

	public String getContextPath() {
		return request.getContextPath();
	}

	public String getServerName() {
		return request.getServerName();
	}

	public Object getAttribute(final String attributeName) {
		return request.getAttribute(attributeName);
	}

	public void setAttribute(final String attributeName, final Object value) {
		request.setAttribute(attributeName, value);
	}

	public boolean isAsync() {
		RequestWrapper wrapper = HDIVUtil.getNativeRequest(request, RequestWrapper.class);
		if (wrapper != null && wrapper instanceof AsyncRequestWrapper) {
			AsyncRequestWrapper asyncWrapper = (AsyncRequestWrapper) wrapper;
			return asyncWrapper.isAsyncRequest();
		}
		return false;
	}

	public Map<String, String[]> getParameterMap() {
		return request.getParameterMap();
	}

	public String getFormStateId() {
		return formStateId;
	}

	public void setFormStateId(final String formStateId) {
		this.formStateId = formStateId;
	}

	public Enumeration<String> getParameterNames() {
		return request.getParameterNames();
	}

	/**
	 * Mark parameter as editable.
	 *
	 * @param name parameter name
	 */
	public void addEditableParameter(final String name) {

		if (request instanceof RequestWrapper) {
			if (log.isDebugEnabled()) {
				log.debug("Editable parameter [" + name + "] added.");
			}
			RequestWrapper wrapper = (RequestWrapper) request;
			wrapper.addEditableParameter(name);
		}
	}

	/**
	 * Try to resolve the message. Treat as an error if the message can't be found.
	 *
	 * @param key the code to lookup up, such as 'calculator.noRateSet'
	 * @param o Array of arguments that will be filled in for params within the message (params look like "{0}", "{1,date}", "{2,time}"
	 * within a message), or null if none.
	 * @return The resolved message
	 */
	public String getMessage(final String key, final String o) {
		return HDIVUtil.getMessage(request, key, o, Locale.getDefault());
	}

	protected ServletRequest getNativeRequest(final ServletRequest request, final Class<?> requiredType) {
		if (requiredType != null) {
			if (requiredType.isInstance(request)) {
				return request;
			}
			else if (request instanceof ServletRequestWrapper) {
				return getNativeRequest(((ServletRequestWrapper) request).getRequest(), requiredType);
			}
		}
		return null;
	}

	/**
	 * Adds one parameter to the request. Since the HttpServletRequest object's parameters are unchanged according to the Servlet
	 * specification, the instance of request should be passed as a parameter of type RequestWrapper.
	 *
	 * @param name new parameter name
	 * @param value new parameter value
	 * @throws HDIVException if the request object is not of type RequestWrapper
	 */
	public void addParameterToRequest(final String name, final String[] value) {

		RequestWrapper wrapper;

		if (request instanceof RequestWrapper) {
			wrapper = (RequestWrapper) request;
		}
		else {
			wrapper = (RequestWrapper) getNativeRequest(request, RequestWrapper.class);
		}

		if (wrapper != null) {
			wrapper.addParameter(name, value);
		}
		else {
			String errorMessage = HDIVUtil.getMessage(request, "helper.notwrapper");
			throw new HDIVException(errorMessage);
		}

	}

	public String[] getParameterValues(final String name) {
		return request.getParameterValues(name);
	}

	public Cookie[] getCookies() {
		return request.getCookies();
	}

	public String getQueryString() {
		return request.getQueryString();
	}

	public String getContentType() {
		return request.getContentType();
	}

	public String getServletPath() {
		return request.getServletPath();
	}

	public void setRedirectAction(final String redirect) {
		this.redirect = redirect;
	}

	public String getRedirectAction() {
		return redirect;
	}

	@SuppressWarnings("deprecation")
	public void setHdivState(final String hdivState) {
		HDIVUtil.setHdivState(request, hdivState);
	}

	public InputStream getInputStream() throws IOException {
		return request.getInputStream();
	}

	public String getHeader(final String header) {
		return request.getHeader(header);
	}

	public <T extends ValidationContext> T getValidationContext() {
		return (T) validationContext;
	}

	public void setValidationContext(final ValidationContext validationContext) {
		this.validationContext = validationContext;
	}
}
