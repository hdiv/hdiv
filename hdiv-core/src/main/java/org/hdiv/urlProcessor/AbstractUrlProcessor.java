/**
 * Copyright 2005-2012 hdiv.org
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
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.regex.PatternMatcher;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;
import org.springframework.util.Assert;

/**
 * This class contains methods to process urls.
 * 
 * @author Gotzon Illarramendi
 * @since HDIV 2.1.0
 */
public abstract class AbstractUrlProcessor {

	/**
	 * Commons Logging instance.
	 */
	private static Log log = LogFactory.getLog(AbstractUrlProcessor.class);

	/**
	 * Hdiv configuration.
	 */
	protected HDIVConfig config;

	/**
	 * Create a new instance of {@link UrlData}.
	 * 
	 * @param url
	 *            original url
	 * @param method
	 *            Http method
	 * @param request
	 *            {@link HttpServletRequest} object
	 * @return new instance of {@link UrlData}
	 */
	public UrlData createUrlData(String url, String method, HttpServletRequest request) {

		Assert.notNull(this.config);

		UrlData urlData = new UrlData(url, method);

		// Extract the anchor
		if (url.indexOf('#') >= 0) {
			String anchor = url.substring(url.indexOf('#') + 1);
			urlData.setAnchor(anchor);

			url = url.substring(0, url.indexOf('#'));
		}

		// Remove parameters
		if (url.indexOf("?") > 0) {
			String urlParams = url.substring(url.indexOf("?") + 1);
			Map<String, String[]> ulrParamsMap = this.getUrlParamsAsMap(request, urlParams);
			urlData.setOriginalUrlParams(ulrParamsMap);
			url = url.substring(0, url.indexOf("?"));

		}

		// Extract protocol, domain and server if exist
		String serverUrl = this.getServerFromUrl(url);
		if (serverUrl != null && serverUrl.length() > 0) {
			urlData.setServer(serverUrl);

			// Remove server and port
			url = url.replaceFirst(serverUrl, "");
		}

		// Detect if the url points to actual app
		boolean internal = this.isInternalUrl(request, url, urlData);
		urlData.setInternal(internal);

		// Remove jsessionid
		url = this.stripSession(url, urlData);

		// Calculate contextPath beginning url
		String contextPathRelativeUrl = this.getContextPathRelative(request, url);
		urlData.setContextPathRelativeUrl(contextPathRelativeUrl);

		// Calculate url without the context path for later processing
		if (contextPathRelativeUrl.startsWith(request.getContextPath())) {
			String urlWithoutContextPath = contextPathRelativeUrl.substring(request.getContextPath().length());
			urlData.setUrlWithoutContextPath(urlWithoutContextPath);
		} else {
			// If contextPath is not present, the relative url is out of application
			urlData.setInternal(false);
		}

		return urlData;

	}

	/**
	 * Generates a Map with request parameter name and values.
	 * 
	 * @param request
	 *            {@link HttpServletRequest} object
	 * @param urlParams
	 *            urls query string
	 * @return Map
	 */
	protected Map<String, String[]> getUrlParamsAsMap(HttpServletRequest request, String urlParams) {

		Map<String, String[]> params = new LinkedHashMap<String, String[]>();

		if (urlParams == null) {
			return params;
		}

		String value = urlParams.replaceAll("&amp;", "&");

		String hdivParameter = (String) request.getSession().getAttribute(Constants.HDIV_PARAMETER);

		StringTokenizer st = new StringTokenizer(value, "&");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			int index = token.indexOf("=");
			String param = "";
			String val = "";
			if (index > -1) {
				param = token.substring(0, index);
				val = token.substring(index + 1);
			} else {
				param = token;
			}

			// Ignore Hdiv state parameter
			if (!param.equals(hdivParameter)) {
				// Add value to array or create it
				String[] values = params.get(param);
				if (values == null) {
					values = new String[] { val };
				} else {
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
	 * Determines if the url is a startPage
	 * 
	 * @return boolean is startPage?
	 */
	protected boolean isStartPage(UrlData urlData) {

		// If this is a start page, don't compose
		if (this.config.isStartPage(urlData.getContextPathRelativeUrl(), urlData.getMethod())) {
			return true;
		}

		// If the url contains the context path and is a start page, don't
		// compose
		if (this.config.isStartPage(urlData.getUrlWithoutContextPath(), urlData.getMethod())) {
			return true;
		}

		return false;
	}

	/**
	 * Generate a url with all parameters and include hdiv state parameter.
	 * 
	 * @param request
	 *            {@link HttpServletRequest} object
	 * @param urlData
	 *            url data object
	 * @param stateParam
	 *            hdiv state parameter value
	 * @return complete url
	 */
	public String getProcessedUrlWithHdivState(HttpServletRequest request, UrlData urlData, String stateParam) {

		// obtain url with parameters
		String url = this.getParamProcessedUrl(urlData);

		if (stateParam == null || stateParam.length() <= 0) {
			return url;
		}

		String separator = (urlData.containsParams()) ? "&" : "?";
		String hdivParameter = (String) request.getSession().getAttribute(Constants.HDIV_PARAMETER);

		StringBuffer sb = new StringBuffer();
		sb.append(url).append(separator).append(hdivParameter).append("=").append(stateParam);

		url = appendAnchor(sb.toString(), urlData.getAnchor());

		return url;
	}

	/**
	 * Generate a url with all parameters.
	 * 
	 * @param urlData
	 *            url data object
	 * @return complete url
	 */
	public String getParamProcessedUrl(UrlData urlData) {

		Map<String, String[]> params = null;
		if (urlData.getProcessedUrlParams() != null) {
			params = urlData.getProcessedUrlParams();
		} else {
			params = urlData.getOriginalUrlParams();
		}

		StringBuffer sb = new StringBuffer();
		if (urlData.getServer() != null) {
			sb.append(urlData.getServer());
		}
		sb.append(urlData.getContextPathRelativeUrl());

		// Add jSessionId
		if (urlData.getjSessionId() != null) {
			sb.append(";");
			sb.append(urlData.getjSessionId());
		}

		if (params == null || params.size() == 0) {
			return sb.toString();
		}

		String separator = "?";

		for (String key : params.keySet()) {
			String[] values = (String[]) params.get(key);

			for (int i = 0; i < values.length; i++) {
				String value = values[i];
				sb.append(separator).append(key).append("=").append(value);
				if (separator.equals("?")) {
					separator = "&";
				}
			}
		}

		return sb.toString();
	}

	/**
	 * Generate final url with all parameters and anchor.
	 * 
	 * @param urlData
	 *            url data object
	 * @return complete url
	 */
	public String getProcessedUrl(UrlData urlData) {

		String url = this.getParamProcessedUrl(urlData);

		url = appendAnchor(url, urlData.getAnchor());
		return url;
	}

	/**
	 * Append anchor to url if constains any.
	 * 
	 * @param url
	 *            url
	 * @param anchor
	 *            anchor
	 * @return url with the anchor
	 */
	protected String appendAnchor(String url, String anchor) {
		if (anchor != null) {
			// it could be ""
			StringBuffer sb = new StringBuffer(url);
			sb.append("#").append(anchor);
			url = sb.toString();
		}
		return url;
	}

	/**
	 * Determines if Hdiv state is necessary for the url.
	 * 
	 * @param urlData
	 *            url data object
	 * @return is necessary?
	 */
	public boolean isHdivStateNecessary(UrlData urlData) {

		if (urlData.getOriginalUrl().startsWith("javascript:")) {
			return false;
		}

		if (!urlData.isInternal()) {
			return false;
		}

		boolean startPage = this.isStartPage(urlData);
		if (startPage) {
			return false;
		}

		if (this.hasExtensionToExclude(urlData)) {
			return false;
		}

		boolean validateParamLessUrls = this.config.isValidationInUrlsWithoutParamsActivated();
		// if url is a link (or a GET method form) and has not got parameters, we do not have to include HDIV's state
		if (urlData.isGetMethod() && !validateParamLessUrls && !urlData.containsParams()) {
			return false;
		}

		return true;

	}

	/**
	 * Detects if the url points to this application
	 * 
	 * @param request
	 *            {@link HttpServletRequest} object
	 * @param url
	 *            request url
	 * @param urlData
	 *            url data
	 * @return is internal?
	 */
	protected boolean isInternalUrl(HttpServletRequest request, String url, UrlData urlData) {

		if (urlData.getServer() != null) {
			// URL is absolute: http://...

			String serverName = request.getServerName();
			if (!urlData.getServer().contains(serverName)) {
				// http://www.google.com
				return false;
			}

			String contextPath = request.getContextPath();

			if (url.startsWith(contextPath + "/") || url.equals(contextPath)) {
				// http://localhost:8080/APP/... or
				// http://localhost:8080/APP
				return true;
			}
			// http://localhost:8080/anotherApplication... or
			return false;

		} else {

			String contextPath = request.getContextPath();

			if (url.startsWith(contextPath + "/") || url.equals(contextPath)) {
				// url of type /APP/... or /APP
				return true;
			} else if (url.startsWith("/")) {
				// url of type /anotherApplication/...
				return false;
			} else {
				// url of type section/action...
				return true;
			}
		}
	}

	/**
	 * Returns from url the part related with the server side in an absolute url.
	 * 
	 * @param url
	 *            absolute url
	 * @return url protocol, domain and port
	 */
	protected String getServerFromUrl(String url) {

		int pos = url.indexOf("://");
		if (pos > 0) {
			int posicion = url.indexOf("/", pos + 3);
			if (posicion > 0) {
				url = url.substring(0, posicion);
				return url;
			} else {
				return url;
			}
		}
		return null;
	}

	/**
	 * Determines if the url contains a extension to exclude for Hdiv state inclusion.
	 * 
	 * @param urlData
	 *            url data object
	 * @return is excluded or not
	 */
	protected boolean hasExtensionToExclude(UrlData urlData) {
		String contextPathRelativeUrl = urlData.getContextPathRelativeUrl();
		if (contextPathRelativeUrl.charAt(contextPathRelativeUrl.length() - 1) == '/') {
			return false;
		}

		List<String> excludedExtensions = this.config.getExcludedURLExtensions();

		if (excludedExtensions != null) {

			for (String extension : excludedExtensions) {
				if (contextPathRelativeUrl.endsWith(extension)) {
					return true;
				}
			}
		}

		List<PatternMatcher> protectedExtension = this.config.getProtectedURLPatterns();

		// jsp is always protected
		if (contextPathRelativeUrl.endsWith(".jsp")) {
			return false;
		}

		if (protectedExtension != null) {
			for (PatternMatcher extensionPattern : protectedExtension) {

				if (extensionPattern.matches(contextPathRelativeUrl)) {
					return false;
				}
			}
		}

		return (!contextPathRelativeUrl.startsWith("/")) && (contextPathRelativeUrl.indexOf(".") == -1);
	}

	/**
	 * Composes the url starting with context path. Removes any relative url.
	 * 
	 * @param request
	 *            {@link HttpServletRequest} object
	 * @param url
	 *            url
	 * @return url starting with context path
	 */
	protected String getContextPathRelative(HttpServletRequest request, String url) {

		String returnValue = null;

		// Base url defined by <base> tag in some frameworks
		String baseUrl = HDIVUtil.getBaseURL(request);
		if (baseUrl != null) {
			// Remove server part from the url
			String serverUrl = this.getServerFromUrl(baseUrl);
			if (serverUrl != null && serverUrl.length() > 0) {
				// Remove server and port
				baseUrl = baseUrl.replaceFirst(serverUrl, "");
			}
		} else {
			// Original RequestUri before Jsp processing
			baseUrl = HDIVUtil.getRequestURI(request);
		}

		if (url.equals("")) {
			return baseUrl;
		} else if (url.startsWith("/")) {
			returnValue = url;
		} else if (url.startsWith("..")) {
			returnValue = url;
		} else {
			// relative path
			String uri = baseUrl;
			uri = uri.substring(uri.indexOf("/"), uri.lastIndexOf("/"));
			returnValue = uri + "/" + url;
		}

		return removeRelativePaths(returnValue, baseUrl);
	}

	/**
	 * Removes from <code>url<code> references to relative paths.
	 * 
	 * @param url
	 *            url
	 * @param originalRequestUri
	 *            originalRequestUri
	 * @return returns <code>url</code> without relative paths.
	 */
	protected String removeRelativePaths(String url, String originalRequestUri) {

		String urlWithoutRelativePath = url;

		if (url.startsWith("..")) {
			Stack<String> stack = new Stack<String>();
			String localUri = originalRequestUri.substring(originalRequestUri.indexOf("/"),
					originalRequestUri.lastIndexOf("/"));
			StringTokenizer localUriParts = new StringTokenizer(localUri.replace('\\', '/'), "/");
			while (localUriParts.hasMoreTokens()) {
				String part = localUriParts.nextToken();
				stack.push(part);
			}

			StringTokenizer pathParts = new StringTokenizer(url.replace('\\', '/'), "/");
			while (pathParts.hasMoreTokens()) {
				String part = pathParts.nextToken();

				if (!part.equals(".")) {
					if (part.equals("..")) {
						stack.pop();
					} else {
						stack.push(part);
					}
				}
			}

			StringBuffer flatPathBuffer = new StringBuffer();
			for (int i = 0; i < stack.size(); i++) {
				flatPathBuffer.append("/").append(stack.elementAt(i));
			}

			urlWithoutRelativePath = flatPathBuffer.toString();
		}

		return urlWithoutRelativePath;
	}

	/**
	 * Strips a servlet session ID from <tt>url</tt>.
	 * 
	 * @param url
	 *            url
	 * @param urlData
	 *            actual url data
	 * @return url without sessionId
	 */
	protected String stripSession(String url, UrlData urlData) {

		if (url.contains(Constants.JSESSIONID) || url.contains(Constants.JSESSIONID_LC)) {

			int last = url.length();
			if (url.contains("?")) {
				last = url.indexOf("?");
			}
			String jSessionId = url.substring(url.indexOf(";") + 1, last);
			urlData.setjSessionId(jSessionId);

			if (log.isDebugEnabled()) {
				log.debug("jSessionId value: " + jSessionId);
			}

			return HDIVUtil.stripSession(url);
		} else {
			return url;
		}

	}

	/**
	 * @param config
	 *            the config to set
	 */
	public void setConfig(HDIVConfig config) {
		this.config = config;
	}

}
