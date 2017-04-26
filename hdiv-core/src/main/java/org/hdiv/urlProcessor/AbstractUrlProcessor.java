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
package org.hdiv.urlProcessor;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.config.HDIVConfig;
import org.hdiv.context.RequestContextHolder;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;
import org.hdiv.util.Method;

/**
 * This class contains methods to process urls.
 *
 * @author Gotzon Illarramendi
 * @since HDIV 2.1.0
 */
public abstract class AbstractUrlProcessor {

	private static final String AMP = "&amp;";

	private static final int AMP_LENGTH = AMP.length();

	/**
	 * Hdiv configuration.
	 */
	protected HDIVConfig config;

	protected static final String processAnchorAndParameters(String url, final UrlDataImpl urlData, final String hdivParameter) {
		url = urlData.findAnchor(url);
		// Remove parameters
		final int paramInit = url.indexOf('?');
		if (paramInit > -1) {
			urlData.setUrlParams(removeStateParameter(hdivParameter, url.substring(paramInit + 1)));
			url = url.substring(0, paramInit);
		}
		return url;
	}

	protected String removeURITemplateParams(final UrlDataImpl data) {
		return data.getUrlWithOutUriTemplate();
	}

	@Deprecated
	public final UrlData createUrlData(final String url, final Method method, final String hdivParameter,
			final HttpServletRequest request) {
		return createUrlData(url, method, hdivParameter, HDIVUtil.getRequestContext(request));
	}

	/**
	 * Create a new instance of {@link UrlData}.
	 * @param url original url
	 * @param method Http method
	 * @param hdivParameter Parameter for HDIV State
	 * @param ctx {@link RequestContextHolder} object
	 * @return new instance of {@link UrlData}
	 */
	public UrlData createUrlData(String url, final Method method, final String hdivParameter, final RequestContextHolder ctx) {

		final String contextPath = ctx.getContextPath();
		final String serverName = ctx.getServerName();
		final String baseURL = getBaseURL(ctx);
		final UrlDataImpl urlData = new UrlDataImpl(url, method);
		url = removeURITemplateParams(urlData);

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
		String contextPathRelativeUrl = getContextPathRelative(baseURL, url);
		urlData.setContextPathRelativeUrl(contextPathRelativeUrl);

		// Detect if the url points to current app
		boolean internal = isInternalUrl(serverName, contextPath, contextPathRelativeUrl, urlData);
		urlData.setInternal(internal);

		// Calculate url without the context path for later processing
		if (internal) {
			// Remove contextPath
			String urlWithoutContextPath = contextPathRelativeUrl.substring(contextPath.length());
			urlData.setUrlWithoutContextPath(urlWithoutContextPath);
		}
		else {
			// If contextPath is not present, the relative url is out of application
			urlData.setInternal(false);
		}

		return urlData;

	}

	@Deprecated
	protected static final String getBaseURL(final HttpServletRequest request) {
		return getBaseURL(HDIVUtil.getRequestContext(request));
	}

	protected static final String getBaseURL(final RequestContextHolder context) {
		// Base url defined by <base> tag in some frameworks
		String baseUrl = context.getBaseURL();
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
			baseUrl = context.getRequestURI();
		}
		return baseUrl;
	}

	/**
	 * Remove _HDIV_STATE_ parameter if it exist.
	 *
	 * @param hdivParameter HDIV state parameter name
	 * @param params parameters string
	 * @return parameters string without state id
	 */
	protected static final String removeStateParameter(final String hdivParameter, String params) {
		int start;
		if (params == null || (start = params.indexOf(hdivParameter)) == -1) {
			return params;
		}

		if (start > AMP_LENGTH) {
			int amp = params.indexOf(AMP, start - AMP_LENGTH);
			if (amp != -1) {
				params = params.substring(0, amp + 1) + params.substring(amp + AMP_LENGTH);
				start = params.indexOf(hdivParameter);
			}
		}
		if (start > 0) {
			char first = params.charAt(start - 1);
			if (first != '?' && first != '&') {
				return params;
			}
		}

		int end = params.indexOf('&', start);
		if (end < 0) {
			end = params.indexOf('#', start);
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

	@Deprecated
	public final Map<String, String[]> getUrlParamsAsMap(final StringBuilder sb, final HttpServletRequest request, final String urlParams) {
		return getUrlParamsAsMap(HDIVUtil.getRequestContext(request).getHdivParameterName(), sb, urlParams);
	}

	/**
	 * Generates a Map with request parameter name and values.
	 * @param hdivParameter Hdiv parameter
	 * @param sb builder
	 * @param urlParams urls query string
	 * @return Parameters as a Map
	 */
	public Map<String, String[]> getUrlParamsAsMap(final String hdivParameter, final StringBuilder sb, final String urlParams) {

		Map<String, String[]> params = new LinkedHashMap<String, String[]>();

		if (urlParams == null) {
			return params;
		}

		String value = urlParams.replaceAll(AMP, "&");

		StringTokenizer st = new StringTokenizer(value, "&");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			int index = token.indexOf('=');
			String param;
			String val = "";
			if (index > -1) {
				param = token.substring(0, index);
				val = token.substring(index + 1);
			}
			else {
				param = token;
			}

			// Decode parameter value
			val = HDIVUtil.getDecodedValue(sb, val, Constants.ENCODING_UTF_8);

			// Ignore Hdiv state parameter
			if (!param.equals(hdivParameter)) {
				// Add value to array or create it
				String[] values = params.get(param);
				if (values == null) {
					values = new String[] { val };
				}
				else {
					int l = values.length;
					values = Arrays.copyOf(values, l + 1);
					values[l] = val;
				}
				params.put(param, values);
			}
		}

		return params;
	}

	/**
	 * Generate a url with all parameters and include hdiv state parameter.
	 *
	 * @param sb builder
	 * @param hdivParameter HDIV parameter name
	 * @param urlData url data object
	 * @param stateParam hdiv state parameter value
	 * @return complete url
	 */
	public String getProcessedUrlWithHdivState(final StringBuilder sb, final String hdivParameter, final UrlData urlData,
			final String stateParam) {
		return urlData.getProcessedUrlWithHdivState(sb, hdivParameter, stateParam);
	}

	/**
	 * Generate final url with all parameters and anchor.
	 *
	 * @param sb StringBuilder
	 * @param urlData url data object
	 * @return complete url
	 */
	public String getProcessedUrl(final StringBuilder sb, final UrlData urlData) {
		return urlData.getProcessedUrl(sb);
	}

	/**
	 * Detects if the url points to this application
	 *
	 * @param serverName Server name
	 * @param contextPath contextPath
	 * @param url request url
	 * @param urlData url data
	 * @return is internal?
	 */
	protected static final boolean isInternalUrl(final String serverName, final String contextPath, final String url,
			final UrlDataImpl urlData) {

		if (urlData.getServer() != null) {
			// URL is absolute: http://...

			if (!urlData.getServer().contains(serverName)) {
				// http://www.google.com
				return false;
			}

			if (url.startsWith(contextPath) && (url.length() == contextPath.length() || url.charAt(contextPath.length()) == '/')) {
				// http://localhost:8080/APP/... or
				// http://localhost:8080/APP
				return true;
			}
			// http://localhost:8080/anotherApplication... or
			return false;

		}
		else {
			if (url.startsWith(contextPath) && (url.length() == contextPath.length() || url.charAt(contextPath.length()) == '/')) {
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
	protected static String getServerFromUrl(String url) {

		final int pos = url.indexOf("://");
		if (pos > 0) {
			int posicion = url.indexOf('/', pos + 3);
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
	 * Composes the url starting with context path. Removes any relative url.
	 *
	 * @param baseUrl base URL
	 * @param url url
	 * @return url starting with context path
	 */
	protected static final String getContextPathRelative(final String baseUrl, final String url) {

		String returnValue;

		if ("".equals(url)) {
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
	protected static String removeRelativePaths(final String url, final String originalRequestUri) {

		String urlWithoutRelativePath = url;

		if (url.startsWith("..")) {
			Stack<String> stack = new Stack<String>();
			String localUri = originalRequestUri.substring(originalRequestUri.indexOf('/'), originalRequestUri.lastIndexOf('/'));
			StringTokenizer localUriParts = new StringTokenizer(localUri.replace('\\', '/'), "/");
			while (localUriParts.hasMoreTokens()) {
				String part = localUriParts.nextToken();
				stack.push(part);
			}

			StringTokenizer pathParts = new StringTokenizer(url.replace('\\', '/'), "/");
			while (pathParts.hasMoreTokens()) {
				String part = pathParts.nextToken();

				if (!".".equals(part)) {
					if ("..".equals(part)) {
						stack.pop();
					}
					else {
						stack.push(part);
					}
				}
			}

			StringBuilder flatPathBuffer = new StringBuilder();
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
	public static final String stripSession(final String url, final UrlDataImpl urlData) {
		return HDIVUtil.stripAndFillSessionData(url, urlData);
	}

	/**
	 * @param config the config to set
	 */
	public void setConfig(final HDIVConfig config) {
		this.config = config;
	}

}
