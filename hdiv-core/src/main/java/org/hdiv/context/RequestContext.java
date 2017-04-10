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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.session.SessionModel;
import org.hdiv.util.Constants;

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

	@SuppressWarnings("deprecation")
	public RequestContext(final HttpServletRequest request, final HttpServletResponse response) {
		this.request = request;
		this.response = response;
		requestURI = request.getRequestURI();
		request.setAttribute(Constants.HDIV_REQUEST_CONTEXT, this);
		doCreateSession();
	}

	public RequestContext(final RequestContextHolder context) {
		request = context.getRequest();
		response = context.getResponse();
		modifyParameterName = context.getHdivModifyParameterName();
		hdivParameterName = context.getHdivParameterName();
		requestURI = context.getRequestURI();
		currentPageId = context.getCurrentPageId();
		doCreateSession();
	}

	protected void doCreateSession() {
		session = new HttpSessionModel(request.getSession());
	}

	public final void update(final HttpServletRequest request, final HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	/**
	 * @return the request
	 */
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
	 * @param request the HttpServletRequest
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
}
