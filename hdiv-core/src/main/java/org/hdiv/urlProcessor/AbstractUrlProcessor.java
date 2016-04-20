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
package org.hdiv.urlProcessor;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.config.HDIVConfig;
import org.hdiv.regex.PatternMatcher;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;
import org.hdiv.util.Method;
import org.springframework.util.Assert;
import org.springframework.web.util.HtmlUtils;

/**
 * This class contains methods to process urls.
 *
 * @author Gotzon Illarramendi
 * @since HDIV 2.1.0
 */
public abstract class AbstractUrlProcessor {

	/**
	 * Hdiv configuration.
	 */
	protected HDIVConfig config;

	@Deprecated
	public final UrlData createUrlData(final String url, final String method, final HttpServletRequest request) {
		return createUrlData(url, Method.secureValueOf(method),
				(String) request.getSession().getAttribute(Constants.HDIV_PARAMETER), request);
	}

	protected final String processAnchorAndParameters(String url, final UrlData urlData, final String hdivParameter) {
		url = urlData.findAnchor(url);
		// Remove parameters
		final int paramInit = url.indexOf('?');
		if (paramInit > -1) {
			urlData.setUrlParams(removeStateParameter(hdivParameter, url.substring(paramInit + 1)));
			url = url.substring(0, paramInit);
		}
		return url;
	}

	/**
	 * Create a new instance of {@link UrlData}.
	 *
	 * @param url original url
	 * @param method Http method
	 * @param request {@link HttpServletRequest} object
	 * @return new instance of {@link UrlData}
	 */
	public UrlData createUrlData(String url, final Method method, final String hdivParameter,
			final HttpServletRequest request) {

		Assert.notNull(config);
		final String contextPath = request.getContextPath();
		final String serverName = request.getServerName();
		final String baseURL = getBaseURL(request);
		final UrlData urlData = new UrlData(url, method);

		// Remove URi template params
		if (urlData.hasUriTemplate()) {
			url = urlData.getUrlWithOutUriTemplate();
		}
		url = processAnchorAndParameters(url, urlData, hdivParameter);

		// Extract protocol, domain and server if exist
		final String serverUrl = getServerFromUrl(url);
		if (serverUrl != null && serverUrl.length() > 0) {
			urlData.setServer(serverUrl);

			// Remove server and port
			url = url.replaceFirst(serverUrl, "");
		}

		// Remove jsessionid
		url = stripSession(url, urlData);

		// Calculate contextPath beginning url
		final String contextPathRelativeUrl = getContextPathRelative(baseURL, url);
		urlData.setContextPathRelativeUrl(contextPathRelativeUrl);

		// Detect if the url points to current app
		final boolean internal = isInternalUrl(serverName, contextPath, contextPathRelativeUrl, urlData);
		urlData.setInternal(internal);

		// Calculate url without the context path for later processing
		if (internal) {
			// Remove contextPath
			final String urlWithoutContextPath = contextPathRelativeUrl.substring(contextPath.length());
			urlData.setUrlWithoutContextPath(urlWithoutContextPath);
		}

		return urlData;

	}

	protected final String getBaseURL(final HttpServletRequest request) {
		// Base url defined by <base> tag in some frameworks
		String baseUrl = HDIVUtil.getBaseURL(request);
		if (baseUrl != null) {
			// Remove server part from the url
			final String serverUrl = getServerFromUrl(baseUrl);
			if (serverUrl != null && serverUrl.length() > 0) {
				// Remove server and port
				baseUrl = baseUrl.replaceFirst(serverUrl, "");
			}
		}
		else {
			// Original RequestUri before Jsp processing
			baseUrl = HDIVUtil.getRequestURI(request);
		}
		return baseUrl;
	}

	/**
	 * Remove _HDIV_STATE_ parameter if it exist.
	 *
	 * @param request {@link HttpServletRequest} object
	 * @param params parameters string
	 * @return parameters string without state id
	 */
	protected final String removeStateParameter(final String hdivParameter, final String params) {

		if (params == null || !params.contains(hdivParameter)) {
			return params;
		}

		final int start = params.indexOf(hdivParameter);
		if (start > 0 && params.charAt(start - 1) != '?' && params.charAt(start - 1) != '&') {
			return params;
		}

		int end = params.indexOf("&", start);
		if (end < 0) {
			end = params.indexOf("#", start);
		}
		if (end < 0) {
			end = params.length();
		}

		String result = params.substring(0, start);
		result = result + params.substring(end, params.length());

		if (result.endsWith("&")) {
			result = result.substring(0, result.length() - 1);
		}

		return result;
	}

	/**
	 * Generates a Map with request parameter name and values.
	 *
	 * @param request {@link HttpServletRequest} object
	 * @param urlParams urls query string
	 * @return Map
	 */
	public Map<String, String[]> getUrlParamsAsMap(final HttpServletRequest request, final String urlParams) {

		final Map<String, String[]> params = new LinkedHashMap<String, String[]>();

		if (urlParams == null) {
			return params;
		}

		final String value = urlParams.replaceAll("&amp;", "&");

		final String hdivParameter = HDIVUtil.getHDIVParameter(request);

		final StringTokenizer st = new StringTokenizer(value, "&");
		while (st.hasMoreTokens()) {
			final String token = st.nextToken();
			final int index = token.indexOf('=');
			String param = "";
			String val = "";
			if (index > -1) {
				param = token.substring(0, index);
				val = token.substring(index + 1);
			}
			else {
				param = token;
			}

			// Decode parameter value
			val = getDecodedValue(val, Constants.ENCODING_UTF_8);

			// Ignore Hdiv state parameter
			if (!param.equals(hdivParameter)) {
				// Add value to array or create it
				String[] values = params.get(param);
				if (values == null) {
					values = new String[] { val };
				}
				else {
					final int l = values.length;
					values = Arrays.copyOf(values, l + 1);
					values[l] = val;
				}
				params.put(param, values);
			}
		}

		return params;
	}

	/**
	 * <p>
	 * Decoded <code>value</code> using input <code>charEncoding</code>.
	 * </p>
	 *
	 * @param value value to decode
	 * @param charEncoding character encoding
	 * @return value decoded
	 */
	protected String getDecodedValue(final String value, final String charEncoding) {

		if (value == null || value.length() == 0) {
			return "";
		}

		String decodedValue;
		try {
			decodedValue = URLDecoder.decode(value, charEncoding);
		}
		catch (final UnsupportedEncodingException e) {
			decodedValue = value;
		}
		catch (final IllegalArgumentException e) {
			decodedValue = value;
		}

		// Remove escaped Html elements
		if (decodedValue.contains("&")) {
			// Can contain escaped characters
			decodedValue = HtmlUtils.htmlUnescape(decodedValue);
		}

		return (decodedValue == null) ? "" : decodedValue;
	}

	/**
	 * Determines if the url is a startPage
	 *
	 * @param urlData {@link UrlData} object with url info.
	 *
	 * @return boolean is startPage?
	 */
	protected boolean isStartPage(final UrlData urlData) {

		// If this is a start page, don't compose
		return config.isStartPage(urlData.getUrlWithoutContextPath(), urlData.getMethod());
	}

	/**
	 * Generate a url with all parameters and include hdiv state parameter.
	 *
	 * @param request {@link HttpServletRequest} object
	 * @param urlData url data object
	 * @param stateParam hdiv state parameter value
	 * @return complete url
	 */
	public String getProcessedUrlWithHdivState(final String hdivParameter, final UrlData urlData,
			final String stateParam) {

		// obtain url with parameters
		final StringBuilder sb = urlData.getParamProcessedUrl();

		if (stateParam == null || stateParam.length() <= 0) {
			return sb.toString();
		}

		final char separator = (urlData.containsParams()) ? '&' : '?';

		sb.append(separator).append(hdivParameter).append('=').append(stateParam);
		sb.append(urlData.getUriTemplate().replace('?', '&'));

		appendAnchor(sb, urlData.getAnchor());
		return sb.toString();
	}

	/**
	 * Generate final url with all parameters and anchor.
	 *
	 * @param urlData url data object
	 * @return complete url
	 */
	public String getProcessedUrl(final UrlData urlData) {

		final StringBuilder url = urlData.getParamProcessedUrl();

		appendAnchor(url, urlData.getAnchor());
		return url.toString();
	}

	/**
	 * Append anchor to url if constains any.
	 *
	 * @param url url
	 * @param anchor anchor
	 * @return url with the anchor
	 */
	protected void appendAnchor(final StringBuilder url, final String anchor) {
		if (anchor != null) {
			// it could be ""
			url.append('#').append(anchor);
		}
	}

	/**
	 * Determines if Hdiv state is necessary for the url.
	 *
	 * @param urlData url data object
	 * @return is necessary?
	 */
	public boolean isHdivStateNecessary(final UrlData urlData) {

		if (urlData.isJS()) {
			return false;
		}

		if (!urlData.isInternal()) {
			return false;
		}

		final boolean startPage = isStartPage(urlData);
		if (startPage) {
			return false;
		}

		if (hasExtensionToExclude(urlData)) {
			return false;
		}

		final boolean validateParamLessUrls = config.isValidationInUrlsWithoutParamsActivated();
		// if url is a link (or a GET method form) and has not got parameters, we do not have to include HDIV's state
		if (urlData.isGetMethod() && !validateParamLessUrls && !urlData.containsParams()) {
			return false;
		}

		return true;

	}

	/**
	 * Detects if the url points to this application
	 *
	 * @param request {@link HttpServletRequest} object
	 * @param url request url
	 * @param urlData url data
	 * @return is internal?
	 */
	protected boolean isInternalUrl(final String serverName, final String contextPath, final String url,
			final UrlData urlData) {

		if (urlData.getServer() != null) {
			// URL is absolute: http://...

			if (!urlData.getServer().contains(serverName)) {
				// http://www.google.com
				return false;
			}

			if (url.startsWith(contextPath)
					&& (url.length() == contextPath.length() || url.charAt(contextPath.length()) == '/')) {
				// http://localhost:8080/APP/... or
				// http://localhost:8080/APP
				return true;
			}
			// http://localhost:8080/anotherApplication... or
			return false;

		}
		else {

			if (url.startsWith(contextPath)
					&& (url.length() == contextPath.length() || url.charAt(contextPath.length()) == '/')) {
				// url of type /APP/... or /APP
				return true;
			}
			else if (url.charAt(0) == '/') {
				// url of type /anotherApplication/...
				return false;
			}
			else {
				// url of type section/action...
				return true;
			}
		}
	}

	/**
	 * Returns from url the part related with the server side in an absolute url.
	 *
	 * @param url absolute url
	 * @return url protocol, domain and port
	 */
	protected String getServerFromUrl(String url) {

		final int pos = url.indexOf("://");
		if (pos > 0) {
			final int posicion = url.indexOf("/", pos + 3);
			if (posicion > 0) {
				url = url.substring(0, posicion);
				return url;
			}
			else {
				return url;
			}
		}
		return null;
	}

	/**
	 * Determines if the url contains a extension to exclude for Hdiv state inclusion.
	 *
	 * @param urlData url data object
	 * @return is excluded or not
	 */
	protected boolean hasExtensionToExclude(final UrlData urlData) {
		final String contextPathRelativeUrl = urlData.getContextPathRelativeUrl();
		if (contextPathRelativeUrl.charAt(contextPathRelativeUrl.length() - 1) == '/') {
			return false;
		}

		final List<String> excludedExtensions = config.getExcludedURLExtensions();

		if (excludedExtensions != null) {
			for (int i = 0; i < excludedExtensions.size(); i++) {
				if (contextPathRelativeUrl.endsWith(excludedExtensions.get(i))) {
					return true;
				}
			}
		}

		// jsp is always protected
		if (contextPathRelativeUrl.endsWith(".jsp")) {
			return false;
		}
		final List<PatternMatcher> protectedExtension = config.getProtectedURLPatterns();
		for (int i = 0; protectedExtension != null && i < protectedExtension.size(); i++) {
			final PatternMatcher extensionPattern = protectedExtension.get(i);
			if (extensionPattern.matches(contextPathRelativeUrl)) {
				return false;
			}
		}

		return (contextPathRelativeUrl.charAt(0) != '/') && (contextPathRelativeUrl.indexOf('.') == -1);
	}

	/**
	 * Composes the url starting with context path. Removes any relative url.
	 *
	 * @param request {@link HttpServletRequest} object
	 * @param url url
	 * @return url starting with context path
	 */
	protected final String getContextPathRelative(final String baseUrl, final String url) {

		String returnValue;

		if (url.equals("")) {
			return baseUrl;
		}
		else if (url.charAt(0) == '/') {
			returnValue = url;
		}
		else if (url.startsWith("..")) {
			returnValue = url;
		}
		else {
			// relative path
			String uri = baseUrl;
			uri = uri.substring(uri.indexOf('/'), uri.lastIndexOf('/'));
			returnValue = uri + "/" + url;
		}

		return removeRelativePaths(returnValue, baseUrl);
	}

	/**
	 * Removes references to relative paths from the URL.
	 *
	 * @param url URL value
	 * @param originalRequestUri originalRequestUri
	 * @return returns URL without relative paths.
	 */
	protected String removeRelativePaths(final String url, final String originalRequestUri) {

		String urlWithoutRelativePath = url;

		if (url.startsWith("..")) {
			final Stack<String> stack = new Stack<String>();
			final String localUri = originalRequestUri.substring(originalRequestUri.indexOf('/'),
					originalRequestUri.lastIndexOf('/'));
			final StringTokenizer localUriParts = new StringTokenizer(localUri.replace('\\', '/'), "/");
			while (localUriParts.hasMoreTokens()) {
				final String part = localUriParts.nextToken();
				stack.push(part);
			}

			final StringTokenizer pathParts = new StringTokenizer(url.replace('\\', '/'), "/");
			while (pathParts.hasMoreTokens()) {
				final String part = pathParts.nextToken();

				if (!part.equals(".")) {
					if (part.equals("..")) {
						stack.pop();
					}
					else {
						stack.push(part);
					}
				}
			}

			final StringBuilder flatPathBuffer = new StringBuilder();
			for (int i = 0; i < stack.size(); i++) {
				flatPathBuffer.append('/').append(stack.elementAt(i));
			}

			urlWithoutRelativePath = flatPathBuffer.toString();
		}

		return urlWithoutRelativePath;
	}

	/**
	 * Strips a servlet session ID from <tt>url</tt>.
	 *
	 * @param url url
	 * @param urlData current url data
	 * @return url without sessionId
	 */
	public static String stripSession(final String url, final UrlData urlData) {
		return HDIVUtil.stripAndFillSessionData(url, urlData);
	}

	/**
	 * @param config the config to set
	 */
	public void setConfig(final HDIVConfig config) {
		this.config = config;
	}

}
