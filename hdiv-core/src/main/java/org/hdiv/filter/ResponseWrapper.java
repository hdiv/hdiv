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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.context.RequestContextHolder;
import org.hdiv.session.ISession;
import org.hdiv.util.Constants;

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
	private static final Log log = LogFactory.getLog(ResponseWrapper.class);

	/**
	 * HTTP header to sent cookies
	 */
	protected static final String SET_COOKIE = "Set-Cookie";

	/**
	 * The set of Cookies associated with this Response.
	 */
	protected Map<String, SavedCookie> cookies = new HashMap<String, SavedCookie>();

	/**
	 * Confidentiality indicator to know if information is accessible only for those who are authorized.
	 */
	protected boolean confidentiality;

	/**
	 * Indicates if cookie confidentiality is applied or not. If the value is <code>true</code> cookie values must not be replaced by
	 * relative values. If it is <code>false</code> they must be replaced by relative values to provide confidentiality.
	 */
	protected boolean avoidCookiesConfidentiality;

	/**
	 * Request context data.
	 */
	protected RequestContextHolder requestContext;

	/**
	 * Session object wrapper.
	 */
	protected ISession session;

	/**
	 * Constructs a response object wrapping the given response.
	 *
	 * @param request HttpServletRequest instance
	 * @param originalResponse response
	 */
	public ResponseWrapper(final RequestContextHolder context) {
		this(context, context.getResponse());
	}

	/**
	 * Constructs a response object wrapping the given response.
	 *
	 * @param request HttpServletRequest instance
	 * @param originalResponse response
	 */
	protected ResponseWrapper(final RequestContextHolder context, final HttpServletResponse response) {
		super(response);
		requestContext = context;
		if (log.isDebugEnabled()) {
			log.debug("New ResponseWrapper instance.");
		}
	}

	/**
	 * The default behavior of this method is to return setHeader(String name, String value) on the wrapped response object.
	 *
	 * @param name the name of the header
	 * @param value the header value
	 * @see javax.servlet.http.HttpServletResponseWrapper#setHeader(java.lang.String, java.lang.String)
	 */
	@Override
	public void setHeader(final String name, final String value) {

		String confidentialValue = value;

		if (name.equalsIgnoreCase(SET_COOKIE)) {
			cookies.clear();
			removeCookiesFromSession();

			List<String> parseValues = parseCookieString(value);

			if (confidentiality && !avoidCookiesConfidentiality) {
				confidentialValue = replaceOriginalValues(parseValues, value);
			}
		}
		super.setHeader(name, confidentialValue);
	}

	/**
	 * The default behavior of this method is to return addHeader(String name, String value) on the wrapped response object.
	 *
	 * @param name the name of the header
	 * @param value the header value
	 * @see javax.servlet.http.HttpServletResponseWrapper#addHeader(java.lang.String, java.lang.String)
	 */
	@Override
	public void addHeader(final String name, final String value) {

		String confidentialValue = value;

		if (name.equalsIgnoreCase(SET_COOKIE)) {

			List<String> parseValues = parseCookieString(value);

			if (confidentiality && !avoidCookiesConfidentiality) {
				confidentialValue = replaceOriginalValues(parseValues, value);
			}
		}

		super.addHeader(name, confidentialValue);
	}

	/**
	 * Replaces cookies' original values by relative values in order to provide confidentiality.
	 *
	 * @param values List of the original values to be replaced
	 * @param value Original value of the cookie to be added
	 * @return Confidential values for the cookies
	 */
	protected String replaceOriginalValues(final List<String> values, String value) {

		for (String currentValue : values) {
			value = value.replaceFirst("=" + currentValue, "=0");
		}

		return value;
	}

	/**
	 * Resets the response.
	 */
	@Override
	public void reset() {

		super.reset();
		cookies.clear();
		removeCookiesFromSession();
	}

	/**
	 * Parses an http cookie request header and append a keyword/value pair to <code>cookies</code> map.
	 *
	 * @param cookieString value assigned to Set-Cookie attribute
	 * @return Cookie list
	 */
	protected List<String> parseCookieString(String cookieString) {

		final List<String> values = new ArrayList<String>();
		cookieString = cookieString.trim();

		// Cookie fields are separated by ';'
		StringTokenizer tokens = new StringTokenizer(cookieString, ";");

		while (tokens.hasMoreTokens()) {
			// field name is separated from value by '='
			StringTokenizer t = new StringTokenizer(tokens.nextToken(), "=");
			String name = t.nextToken().trim();
			if (t.hasMoreTokens()) {
				String value = t.nextToken().trim();
				cookies.put(name, new SavedCookie(name, value));
				values.add(value);
			}
		}
		updateSessionCookies();
		return values;
	}

	/**
	 * Adds the specified cookie to the response. It can be called multiple times to set more than one cookie.
	 *
	 * @param cookie The <code>Cookie</code> to return to the client
	 * @see javax.servlet.http.HttpServletResponse#addCookie
	 */
	@Override
	public void addCookie(final Cookie cookie) {
		cookies.put(cookie.getName(), new SavedCookie(cookie));
		updateSessionCookies();

		if (confidentiality && !avoidCookiesConfidentiality) {
			cookie.setValue("0");
		}

		super.addCookie(cookie);
	}

	/**
	 * It updates cookies stored in the user's session with the wrapper's cookies.
	 */
	@SuppressWarnings("unchecked")
	protected void updateSessionCookies() {

		Map<String, SavedCookie> sessionOriginalCookies = session.getAttribute(requestContext, Constants.HDIV_COOKIES_KEY, Map.class);

		if (sessionOriginalCookies != null && sessionOriginalCookies.size() > 0) {

			sessionOriginalCookies.putAll(cookies);
			session.setAttribute(requestContext, Constants.HDIV_COOKIES_KEY, sessionOriginalCookies);

		}
		else {
			session.setAttribute(requestContext, Constants.HDIV_COOKIES_KEY, cookies);
		}
	}

	/**
	 * Removes from user's session the cookies added by the application.
	 */
	protected void removeCookiesFromSession() {

		session.removeAttribute(requestContext, Constants.HDIV_COOKIES_KEY);
	}

	/**
	 * Obtains all the cookies added by the application.
	 *
	 * @return cookies added by the application
	 */
	public Map<String, SavedCookie> getCookies() {
		return cookies;
	}

	/**
	 * @param confidentiality the confidentiality to set
	 */
	public void setConfidentiality(final boolean confidentiality) {
		this.confidentiality = confidentiality;
	}

	/**
	 * @param avoidCookiesConfidentiality the avoidCookiesConfidentiality to set
	 */
	public void setAvoidCookiesConfidentiality(final boolean avoidCookiesConfidentiality) {
		this.avoidCookiesConfidentiality = avoidCookiesConfidentiality;
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(final ISession session) {
		this.session = session;
	}

}