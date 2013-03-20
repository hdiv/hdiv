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

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.util.Constants;
import org.springframework.web.multipart.MultipartFile;

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
	private static Log log = LogFactory.getLog(RequestWrapper.class);

	/**
	 * HTTP header to sent cookies
	 */
	private static final String COOKIE = "cookie";

	/**
	 * Map with request parameters
	 */
	private Hashtable parameters = new Hashtable();

	/**
	 * The file request parameters.
	 */
	private Hashtable elementsFile;

	/**
	 * The text request parameters.
	 */
	private Hashtable elementsText;

	/**
	 * Determines whether this request is multipart.
	 */
	private boolean isMultipart;

	/**
	 * Confidentiality indicator to know if information is accessible only for those who are authorized.
	 */
	private Boolean confidentiality;

	/**
	 * Indicates if cookie confidentiality is applied or not. If the value is <code>true</code> cookie values must not
	 * be replaced by relative values. If it is <code>false</code> they must be replaced by relative values to provide
	 * confidentiality.
	 */
	private boolean cookiesConfidentiality;

	/**
	 * Constructs a request object wrapping the given request.
	 * 
	 * @param servletRequest
	 *            request
	 */
	public RequestWrapper(HttpServletRequest servletRequest) {

		super(servletRequest);

		this.elementsText = new Hashtable();
		this.elementsFile = new Hashtable();
		this.isMultipart = false;

		if (log.isDebugEnabled()) {
			log.debug("New RequestWrapper instance.");
		}
	}

	/**
	 * Returns an array of String objects containing all of the values the given request parameter has. If the parameter
	 * has a single value, the array has a length of 1.
	 * 
	 * @param parameter
	 *            the name of the parameter whose value is requested
	 */
	public String[] getParameterValues(String parameter) {

		// non validated parameters are obtained from the original request
		if (!this.parameters.containsKey(parameter)) {
			return super.getParameterValues(parameter);
		}

		Object data = this.parameters.get(parameter);

		if (data.getClass().isArray()) {
			return (String[]) data;

		} else {
			String[] array = new String[1];
			array[0] = (String) this.parameters.get(parameter);
			return array;
		}
	}

	/**
	 * Returns the value of a request parameter as a String. Request parameters are extra information sent with the
	 * request. For HTTP servlets, parameters are contained in the query string or posted form data.
	 * 
	 * @param parameter
	 *            name of the parameter
	 */
	public String getParameter(String parameter) {

		// non validated parameters are obtained from the original request
		if (!this.parameters.containsKey(parameter)) {
			return super.getParameter(parameter);
		}

		Object data = this.parameters.get(parameter);

		if (data.getClass().isArray()) {
			String[] array = (String[]) data;
			return array[0];

		} else {
			return (String) this.parameters.get(parameter);
		}
	}

	/**
	 * Returns the names of the parameters for this request. The enumeration consists of the normal request parameter
	 * names plus the parameters read from the multipart request.
	 */
	public Enumeration getParameterNames() {

		Enumeration baseParams = super.getParameterNames();

		if (!this.isMultipart)
			return baseParams;

		Vector list = new Vector();

		while (baseParams.hasMoreElements()) {
			list.add(baseParams.nextElement());
		}

		Collection multipartParams = this.parameters.keySet();
		Iterator iterator = multipartParams.iterator();

		while (iterator.hasNext()) {
			list.add(iterator.next());
		}

		return Collections.enumeration(list);
	}

	/**
	 * Returns the value of the specified request header as a String.
	 * 
	 * @param name
	 *            header name
	 * @return a String containing the value of the requested header, or null if the request does not have a header of
	 *         that name
	 * @since HDIV 1.1.1
	 */
	public String getHeader(String name) {

		String cookieHeader = super.getHeader(name);
		if (name.equalsIgnoreCase(COOKIE) && Boolean.TRUE.equals(this.confidentiality) && this.cookiesConfidentiality) {

			Hashtable sessionCookies = (Hashtable) super.getSession().getAttribute(Constants.HDIV_COOKIES_KEY);

			if (sessionCookies != null) {
				return this.replaceCookieString(cookieHeader, sessionCookies);
			}
		}
		return cookieHeader;
	}

	/**
	 * Returns all the values of the specified request header as an Enumeration of String objects.
	 * 
	 * @param name
	 *            a String specifying the header name
	 * @return an Enumeration containing the values of the requested header. If the request does not have any headers of
	 *         that name return an empty enumeration. If the container does not allow access to header information,
	 *         return null.
	 * @since HDIV 1.1.1
	 */
	public Enumeration getHeaders(String name) {

		Enumeration headerValues = super.getHeaders(name);

		if (name.equalsIgnoreCase(COOKIE) && Boolean.TRUE.equals(this.confidentiality) && this.cookiesConfidentiality) {

			Vector values = new Vector();
			Hashtable sessionCookies = (Hashtable) super.getSession().getAttribute(Constants.HDIV_COOKIES_KEY);

			if (sessionCookies != null) {
				while (headerValues.hasMoreElements()) {
					String element = (String) headerValues.nextElement();
					String replaced = this.replaceCookieString(element, sessionCookies);
					values.add(replaced);
				}
			}
			return values.elements();
		}
		return headerValues;
	}

	/**
	 * Parses an http cookie request header and replace values if confidentiality is activated.
	 * 
	 * @param cookieHader
	 *            value assigned to cookie header
	 * @param sessionCookies
	 *            cookies stored in user session
	 * @return cookie request header with replaced values
	 * @since HDIV 1.1.1
	 */
	private String replaceCookieString(String cookieHeader, Hashtable sessionCookies) {

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
					SavedCookie savedCookie = (SavedCookie) sessionCookies.get(name);
					header = header.replaceFirst("=" + value, "=" + savedCookie.getValue());
				}
			}
		}
		return header;
	}

	/**
	 * Add a single value for the specified HTTP parameter <code>name</code>.
	 * 
	 * @param name
	 *            parameter name
	 * @param value
	 *            value
	 */
	public void addParameter(String name, Object value) {

		this.parameters.put(name, value);

		if (this.isMultipart) {
			this.addTextParameter(name, value);
		}
	}

	/**
	 * Combines the parameters stored here with those in the underlying request. If paramater values in the underlying
	 * request take precedence over those stored here.
	 * 
	 * @since HDIV 1.3
	 */
	public Map getParameterMap() {

		Map map = new HashMap(super.getRequest().getParameterMap());
		map.putAll(this.parameters);

		return map;
	}

	/**
	 * Returns a hash table containing the text (that is, non-file) request parameters.
	 * 
	 * @return The text request parameters.
	 */
	public Hashtable getTextElements() {
		return this.elementsText;
	}

	/**
	 * Returns a hash table containing the file (that is, non-text) request parameters.
	 * 
	 * @return The file request parameters.
	 */
	public Hashtable getFileElements() {
		return this.elementsFile;
	}

	/**
	 * Adds a regular text parameter to the set of text parameters for this request.
	 * 
	 * @param name
	 *            text parameter name
	 * @param value
	 *            text parameter value
	 */
	public void addTextParameter(String name, Object value) {
		this.elementsText.put(name, value);
	}

	/**
	 * Adds a file parameter to the set of file parameters for this request.
	 * 
	 * @param name
	 *            file name
	 * @param value
	 *            file value
	 */
	public void addFileItem(String name, MultipartFile value) {
		this.elementsFile.put(name, value);
	}

	/**
	 * Adds a file parameter to the set of file parameters for this request.
	 * 
	 * @param name
	 *            file name
	 * @param values
	 *            file values
	 */
	public void addFileItem(String name, List values) {
		this.elementsFile.put(name, values);
	}

	/**
	 * Determines whether this request is multipart.
	 * 
	 * @param isMultipart
	 */
	public void setMultipart(boolean isMultipart) {
		this.isMultipart = isMultipart;
	}

	/**
	 * @param cookiesConfidentiality
	 *            The cookiesConfidentiality to set.
	 */
	public void setCookiesConfidentiality(boolean cookiesConfidentiality) {
		this.cookiesConfidentiality = cookiesConfidentiality;
	}

	/**
	 * @param confidentiality
	 *            The confidentiality to set.
	 */
	public void setConfidentiality(Boolean confidentiality) {
		this.confidentiality = confidentiality;
	}

}