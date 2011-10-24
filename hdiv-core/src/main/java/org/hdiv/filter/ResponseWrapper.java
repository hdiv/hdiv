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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;

/**
 * A wrapper for HTTP servlet response.
 * 
 * @author Gorka Vicente
 * @see javax.servlet.http.HttpServletResponseWrapper
 * @since HDIV 1.1
 */
public class ResponseWrapper extends HttpServletResponseWrapper {

	/**
	 * Commons Logging instance.
	 */
	private static Log log = LogFactory.getLog(ResponseWrapper.class);

	/**
	 * HTTP header to sent cookies
	 */
	private static final String SET_COOKIE = "Set-Cookie";

	/**
	 * The set of Cookies associated with this Response.
	 */
	private Hashtable cookies;

	/**
	 * Confidentiality indicator to know if information is accessible only for those
	 * who are authorized.
	 */
	private boolean confidentiality;

	/**
	 * Indicates if cookie confidentiality is applied or not. If the value is
	 * <code>true</code> cookie values must not be replaced by relative values. If
	 * it is <code>false</code> they must be replaced by relative values to provide
	 * confidentiality.
	 */
	private boolean avoidCookiesConfidentiality;


	/**
	 * Constructs a response object wrapping the given response.
	 * 
	 * @param originalResponse response
	 */
	public ResponseWrapper(HttpServletResponse originalResponse) {

		super(originalResponse);
		this.cookies = new Hashtable();
		HDIVConfig config = (HDIVConfig)HDIVUtil.getApplication().getBean("config");
		this.confidentiality = config.getConfidentiality().booleanValue();
		this.avoidCookiesConfidentiality = !config.isCookiesConfidentialityActivated();
	}

	/**
	 * The default behavior of this method is to return setHeader(String name, String
	 * value) on the wrapped response object.
	 * 
	 * @param name the name of the header
	 * @param value the header value
	 * @see javax.servlet.http.HttpServletResponseWrapper#setHeader(java.lang.String,
	 *      java.lang.String)
	 */
	public void setHeader(String name, String value) {

		String confidentialValue = value;

		if (name.equalsIgnoreCase(SET_COOKIE)) {
			this.cookies.clear();
			this.removeCookiesFromSession();

			List parseValues = this.parseCookieString(value);

			if (this.confidentiality && !this.avoidCookiesConfidentiality) {
				confidentialValue = this.replaceOriginalValues(parseValues, value);
			}
		}
		super.setHeader(name, confidentialValue);
	}

	/**
	 * The default behavior of this method is to return addHeader(String name, String
	 * value) on the wrapped response object.
	 * 
	 * @param name the name of the header
	 * @param value the header value
	 * @see javax.servlet.http.HttpServletResponseWrapper#addHeader(java.lang.String,
	 *      java.lang.String)
	 */
	public void addHeader(String name, String value) {

		String confidentialValue = value;

		if (name.equalsIgnoreCase(SET_COOKIE)) {

			List parseValues = this.parseCookieString(value);

			if (this.confidentiality && !this.avoidCookiesConfidentiality) {
				confidentialValue = this.replaceOriginalValues(parseValues, value);
			}
		}

		super.addHeader(name, confidentialValue);
	}

	/**
	 * Replaces cookies' original values by relative values in order to provide
	 * confidentiality.
	 * 
	 * @param values List of the original values to be replaced
	 * @param value Original value of the cookie to be added
	 * @return Confidential values for the cookies
	 */
	public String replaceOriginalValues(List values, String value) {

		String currentValue = null;
		for (int i = 0; i < values.size(); i++) {
			currentValue = (String) values.get(i);
			value = value.replaceFirst("=" + currentValue, "=0");
		}
		return value;
	}

	/**
	 * Resets the response.
	 */
	public void reset() {

		super.reset();
		this.cookies.clear();
		this.removeCookiesFromSession();
	}

	/**
	 * Parses an http cookie request header and append a keyword/value pair to
	 * <code>cookies</code> map.
	 * 
	 * @param cookieString value assigned to Set-Cookie attribute
	 */
	private List parseCookieString(String cookieString) {

		List values = new ArrayList();
		cookieString = cookieString.trim();

		// Cookie fields are separated by ';'
		StringTokenizer tokens = new StringTokenizer(cookieString, ";");

		while (tokens.hasMoreTokens()) {
			// field name is separated from value by '='
			StringTokenizer t = new StringTokenizer(tokens.nextToken(), "=");
			String name = t.nextToken().trim();
			if (t.hasMoreTokens()) {
				String value = t.nextToken().trim();
				this.cookies.put(name, new SavedCookie(name, value));
				values.add(value);
			}
		}
		this.updateSessionCookies();
		return values;
	}

	/**
	 * Adds the specified cookie to the response. It can be called multiple times to
	 * set more than one cookie.
	 * 
	 * @param The <code>Cookie</code> to return to the client
	 * @see javax.servlet.http.HttpServletResponse#addCookie
	 */
	public void addCookie(Cookie cookie) {

		SavedCookie savedCookie = new SavedCookie(cookie);

		this.cookies.put(savedCookie.getName(), savedCookie);
		this.updateSessionCookies();

		if (this.confidentiality && !this.avoidCookiesConfidentiality) {
			cookie.setValue("0");
		}

		super.addCookie(cookie);
	}

	/**
	 * It updates cookies stored in the user's session with the wrapper's cookies.
	 */
	private void updateSessionCookies() {

		if (HDIVUtil.getHttpSession() != null) {
		
			Hashtable sessionOriginalCookies = (Hashtable) HDIVUtil.getHttpSession()
					.getAttribute(Constants.HDIV_COOKIES_KEY);
	
			if ((sessionOriginalCookies != null) && (sessionOriginalCookies.size() > 0)) {
	
				sessionOriginalCookies.putAll(this.cookies);
				HDIVUtil.getHttpSession().setAttribute(Constants.HDIV_COOKIES_KEY,
																	sessionOriginalCookies);
	
			} else {
				HDIVUtil.getHttpSession().setAttribute(Constants.HDIV_COOKIES_KEY,
																	this.cookies);
			}
		}
	}

	/**
	 * Removes from user's session the cookies added by the application.
	 */
	public void removeCookiesFromSession() {
		
		if (HDIVUtil.getHttpSession() != null) {
			HDIVUtil.getHttpSession().removeAttribute(Constants.HDIV_COOKIES_KEY);
		}
	}

	/**
	 * Obtains all the cookies added by the application.
	 * 
	 * @return cookies added by the application
	 */
	public Hashtable getCookies() {
		return this.cookies;
	}

}