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

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.util.Constants;

/**
 * A wrapper for HTTP servlet request.
 * 
 * @author Roberto Velasco
 * @author Gorka Vicente
 * @see javax.servlet.http.HttpServletRequestWrapper
 */
public class RequestWrapper extends HttpServletRequestWrapper {

	/**
	 * Commons Logging instance.
	 */
	private static final Log log = LogFactory.getLog(RequestWrapper.class);

	/**
	 * HTTP header to sent cookies.
	 */
	protected static final String COOKIE = "cookie";

	/**
	 * Set with editable parameters.
	 */
	protected Set<String> editableParameters = new HashSet<String>();

	/**
	 * Map with request parameters
	 */
	protected Map<String, String[]> parameters = new HashMap<String, String[]>();

	/**
	 * The file request parameters.
	 */
	protected Map<String, Object> elementsFile = new HashMap<String, Object>();

	/**
	 * The text request parameters.
	 */
	protected Map<String, Object> elementsText = new HashMap<String, Object>();

	/**
	 * Determines whether this request is multipart.
	 */
	protected boolean isMultipart = false;

	/**
	 * Confidentiality indicator to know if information is accessible only for those who are authorized.
	 */
	protected boolean confidentiality = true;

	/**
	 * Indicates if cookie confidentiality is applied or not. If the value is <code>true</code> cookie values must not
	 * be replaced by relative values. If it is <code>false</code> they must be replaced by relative values to provide
	 * confidentiality.
	 */
	protected boolean cookiesConfidentiality;

	/**
	 * True if this is an Async request.
	 */
	protected boolean isAsyncRequest = false;

	/**
	 * Constructs a request object wrapping the given request.
	 * 
	 * @param servletRequest request
	 */
	public RequestWrapper(HttpServletRequest servletRequest) {

		super(servletRequest);

		if (log.isDebugEnabled()) {
			log.debug("New RequestWrapper instance.");
		}
	}

	/**
	 * Returns an array of String objects containing all of the values the given request parameter has. If the parameter
	 * has a single value, the array has a length of 1.
	 * 
	 * @param parameter the name of the parameter whose value is requested
	 */
	@Override
	public String[] getParameterValues(String parameter) {

		// non validated parameters are obtained from the original request
		if (!this.parameters.containsKey(parameter)) {
			return super.getParameterValues(parameter);
		}

		Object data = this.parameters.get(parameter);

		if (data.getClass().isArray()) {
			return (String[]) data;
		}
		else {
			String[] array = this.parameters.get(parameter);
			return array;
		}
	}

	/**
	 * Returns the value of a request parameter as a String. Request parameters are extra information sent with the
	 * request. For HTTP servlets, parameters are contained in the query string or posted form data.
	 * 
	 * @param parameter name of the parameter
	 */
	@Override
	public String getParameter(String parameter) {

		// non validated parameters are obtained from the original request
		if (!this.parameters.containsKey(parameter)) {
			return super.getParameter(parameter);
		}

		Object data = this.parameters.get(parameter);

		if (data.getClass().isArray()) {
			String[] array = (String[]) data;
			return array[0];
		}
		else {
			String[] values = this.parameters.get(parameter);
			return values.length > 0 ? values[0] : null;
		}
	}

	/**
	 * Returns the names of the parameters for this request. The enumeration consists of the normal request parameter
	 * names plus the parameters read from the multipart request.
	 */
	@Override
	public Enumeration<String> getParameterNames() {

		Enumeration<String> baseParams = super.getParameterNames();

		if (!this.isMultipart) {
			return baseParams;
		}

		Vector<String> list = new Vector<String>();

		while (baseParams.hasMoreElements()) {
			list.add(baseParams.nextElement());
		}

		Collection<String> multipartParams = this.parameters.keySet();

		list.addAll(multipartParams);

		return Collections.enumeration(list);
	}

	/**
	 * Returns the value of the specified request header as a String.
	 * 
	 * @param name header name
	 * @return a String containing the value of the requested header, or null if the request does not have a header of
	 * that name
	 * @since HDIV 1.1.1
	 */
	@Override
	@SuppressWarnings("unchecked")
	public String getHeader(String name) {

		String cookieHeader = super.getHeader(name);
		if (name.equalsIgnoreCase(COOKIE) && this.confidentiality && this.cookiesConfidentiality) {

			Map<String, SavedCookie> sessionCookies = (Map<String, SavedCookie>) super.getSession().getAttribute(
					Constants.HDIV_COOKIES_KEY);

			if (sessionCookies != null) {
				return this.replaceCookieString(cookieHeader, sessionCookies);
			}
		}
		return cookieHeader;
	}

	/**
	 * Returns all the values of the specified request header as an Enumeration of String objects.
	 * 
	 * @param name a String specifying the header name
	 * @return an Enumeration containing the values of the requested header. If the request does not have any headers of
	 * that name return an empty enumeration. If the container does not allow access to header information, return null.
	 * @since HDIV 1.1.1
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Enumeration<String> getHeaders(String name) {

		Enumeration<String> headerValues = super.getHeaders(name);

		if (name.equalsIgnoreCase(COOKIE) && this.confidentiality && this.cookiesConfidentiality) {

			Vector<String> values = new Vector<String>();
			Map<String, SavedCookie> sessionCookies = (Map<String, SavedCookie>) super.getSession().getAttribute(
					Constants.HDIV_COOKIES_KEY);

			if (sessionCookies != null) {
				while (headerValues.hasMoreElements()) {
					String element = (String) headerValues.nextElement();
					String replaced = this.replaceCookieString(element, sessionCookies);
					values.add(replaced);
				}
			}
			else {
				return headerValues;
			}
			return values.elements();
		}
		return headerValues;
	}

	/**
	 * Parses an http cookie request header and replace values if confidentiality is activated.
	 * 
	 * @param cookieHeader value assigned to cookie header
	 * @param sessionCookies cookies stored in user session
	 * @return cookie request header with replaced values
	 * @since HDIV 1.1.1
	 */
	protected String replaceCookieString(String cookieHeader, Map<String, SavedCookie> sessionCookies) {

		String header = cookieHeader.trim();

		// Cookie fields are separated by ';'
		StringTokenizer tokens = new StringTokenizer(cookieHeader, ";");

		while (tokens.hasMoreTokens()) {
			// field name is separated from value by '='
			StringTokenizer t = new StringTokenizer(tokens.nextToken(), "=");
			String name = t.nextToken().trim();

			if (name.equals(Constants.JSESSIONID)) {
				continue;
			}

			if (sessionCookies.containsKey(name)) {
				if (t.hasMoreTokens()) {
					String value = t.nextToken().trim();
					SavedCookie savedCookie = sessionCookies.get(name);
					header = header.replaceFirst("=" + value, "=" + savedCookie.getValue());
				}
			}
		}
		return header;
	}

	/**
	 * Add a single value for the specified HTTP parameter <code>name</code>.
	 * 
	 * @param name parameter name
	 * @param value value
	 */
	public void addParameter(String name, String[] value) {

		this.parameters.put(name, value);

		if (this.isMultipart) {
			this.addTextParameter(name, value);
		}
	}

	/**
	 * Combines the parameters stored here with those in the underlying request. If parameter values in the underlying
	 * request take precedence over those stored here.
	 * 
	 * @since HDIV 1.3
	 */
	@Override
	public Map<String, String[]> getParameterMap() {

		Map<String, String[]> map = new HashMap<String, String[]>(super.getRequest().getParameterMap());
		map.putAll(this.parameters);

		return map;
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		this.isAsyncRequest = true;
		return super.startAsync();
	}

	@Override
	public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
			throws IllegalStateException {
		this.isAsyncRequest = true;
		return super.startAsync(servletRequest, servletResponse);
	}

	/**
	 * Returns a map containing the text (that is, non-file) request parameters.
	 * 
	 * @return The text request parameters.
	 */
	public Map<String, Object> getTextElements() {
		return this.elementsText;
	}

	/**
	 * Returns a map containing the file (that is, non-text) request parameters.
	 * 
	 * @return The file request parameters.
	 */
	public Map<String, Object> getFileElements() {
		return this.elementsFile;
	}

	/**
	 * Adds a regular text parameter to the set of text parameters for this request.
	 * 
	 * @param name text parameter name
	 * @param value text parameter value
	 */
	public void addTextParameter(String name, Object value) {
		this.elementsText.put(name, value);
	}

	/**
	 * Adds a file parameter to the set of file parameters for this request.
	 * 
	 * @param name file name
	 * @param values file values
	 */
	public void addFileItem(String name, Object values) {
		this.elementsFile.put(name, values);
	}

	/**
	 * Add editable parameter.
	 * 
	 * @param parameter new parameter name
	 */
	public void addEditableParameter(String parameter) {
		this.editableParameters.add(parameter);
	}

	/**
	 * Return true if parameter is editable.
	 * 
	 * @param parameter parameter name
	 * @return boolean
	 */
	public boolean isEditableParameter(String parameter) {
		return this.editableParameters.contains(parameter);
	}

	/**
	 * @return the isAsyncRequest
	 */
	public boolean isAsyncRequest() {
		return isAsyncRequest;
	}

	/**
	 * Determines whether this request is multipart.
	 * 
	 * @param isMultipart true if it is multipart
	 */
	public void setMultipart(boolean isMultipart) {
		this.isMultipart = isMultipart;
	}

	/**
	 * @param cookiesConfidentiality The cookiesConfidentiality to set.
	 */
	public void setCookiesConfidentiality(boolean cookiesConfidentiality) {
		this.cookiesConfidentiality = cookiesConfidentiality;
	}

	/**
	 * @param confidentiality The confidentiality to set.
	 */
	public void setConfidentiality(boolean confidentiality) {
		this.confidentiality = confidentiality;
	}

}