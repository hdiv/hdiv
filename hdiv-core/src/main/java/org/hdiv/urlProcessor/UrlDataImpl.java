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

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hdiv.config.HDIVConfig;
import org.hdiv.regex.PatternMatcher;
import org.hdiv.util.Method;

/**
 * Contains the data of an url.
 *
 * @author Gotzon Illarramendi
 */
public class UrlDataImpl implements UrlData {

	private static final Pattern NAMES_PATTERN = Pattern.compile("\\{([^/]+?)\\}");

	/**
	 * Original url, previous to any change.
	 */
	private final String originalUrl;

	/**
	 * Original urls anchor
	 */
	String anchor;

	/**
	 * JSessionId value
	 */
	String jSessionId;

	/**
	 * Url that starts with contextPath
	 */
	String contextPathRelativeUrl;

	/**
	 * The same as contextPathRelativeUrl with contextPath erased
	 */
	private String urlWithoutContextPath;

	/**
	 * URL parameters in query string format. For example: param1=val1&param2=val2
	 *
	 * @since 2.1.7
	 */
	private String urlParams;

	/**
	 * Map with original url parameter name and values
	 */
	private Map<String, String[]> originalUrlParams;

	/**
	 * Map with processed url parameter name and values
	 */
	private Map<String, String[]> processedUrlParams;

	/**
	 * True if the url points to this app
	 */
	private boolean internal = true;

	/**
	 * Protocol, server and port of the url
	 */
	String server;

	/**
	 * Http method.
	 */
	private Method method;

	/**
	 * UriTemplate https://tools.ietf.org/html/rfc6570
	 *
	 * @since 3.0.0
	 */
	String uriTemplate;

	String composedParams;

	/**
	 * Constructor
	 *
	 * @param url Original url
	 * @param method Http method.
	 */
	public UrlDataImpl(final String url, final Method method) {
		originalUrl = url;
		this.method = method;
		if (url.indexOf('{') != -1) {
			parser(url);
		}
	}

	/**
	 * Is url method GET?
	 *
	 * @return true is it is GET
	 */
	public boolean isGetMethod() {
		return Method.GET == method;
	}

	/**
	 * Determines if url contains parameters
	 *
	 * @return has parameters?
	 */
	public boolean containsParams() {
		return (originalUrlParams != null && originalUrlParams.size() > 0) || (urlParams != null && urlParams.length() > 0);
	}

	/**
	 * @return the anchor
	 */
	public String getAnchor() {
		return anchor;
	}

	public String findAnchor(final String url) {
		int pos = url.indexOf('#');
		if (pos >= 0) {
			setAnchor(url.substring(pos + 1));

			return url.substring(0, pos);
		}
		return url;
	}

	/**
	 * @param anchor the anchor to set
	 */
	private void setAnchor(final String anchor) {
		this.anchor = anchor;
	}

	/**
	 * @return the contextPathRelativeUrl
	 */
	public String getContextPathRelativeUrl() {
		return contextPathRelativeUrl;
	}

	/**
	 * @param contextPathRelativeUrl the contextPathRelativeUrl to set
	 */
	public void setContextPathRelativeUrl(final String contextPathRelativeUrl) {
		this.contextPathRelativeUrl = contextPathRelativeUrl;
	}

	/**
	 * @return the urlWithoutContextPath
	 */
	public String getUrlWithoutContextPath() {
		return urlWithoutContextPath;
	}

	/**
	 * @param urlWithoutContextPath the urlWithoutContextPath to set
	 */
	public void setUrlWithoutContextPath(final String urlWithoutContextPath) {
		this.urlWithoutContextPath = urlWithoutContextPath;
	}

	/**
	 * @return the originalUrlParams
	 */
	public Map<String, String[]> getOriginalUrlParams() {
		return originalUrlParams;
	}

	/**
	 * @param originalUrlParams the originalUrlParams to set
	 */
	public void setOriginalUrlParams(final Map<String, String[]> originalUrlParams) {
		this.originalUrlParams = originalUrlParams;
	}

	/**
	 * @return the processedUrlParams
	 */
	public Map<String, String[]> getProcessedUrlParams() {
		return processedUrlParams;
	}

	/**
	 * @param processedUrlParams the processedUrlParams to set
	 */
	public void setProcessedUrlParams(final Map<String, String[]> processedUrlParams) {
		this.processedUrlParams = processedUrlParams;
	}

	/**
	 * @return the internal
	 */
	public boolean isInternal() {
		return internal;
	}

	/**
	 * @param internal the internal to set
	 */
	public void setInternal(final boolean internal) {
		this.internal = internal;
	}

	/**
	 * @return the server
	 */
	public String getServer() {
		return server;
	}

	/**
	 * @param server the server to set
	 */
	public void setServer(final String server) {
		this.server = server;
	}

	/**
	 * @return the method
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(final Method method) {
		this.method = method;
	}

	/**
	 * @return the jSessionId
	 */
	public String getjSessionId() {
		return jSessionId;
	}

	/**
	 * @param jSessionId the jSessionId to set
	 */
	public void setjSessionId(final String jSessionId) {
		this.jSessionId = jSessionId;
	}

	/**
	 * @return the urlParams
	 */
	public String getUrlParams() {
		return urlParams;
	}

	/**
	 * @param urlParams the urlParams to set
	 */
	public void setUrlParams(final String urlParams) {
		this.urlParams = urlParams;
	}

	public void setComposedUrlParams(final String composedParams) {
		this.composedParams = composedParams;
	}

	public boolean hasUriTemplate() {
		return uriTemplate != null;
	}

	public String getUrlWithOutUriTemplate() {
		if (hasUriTemplate()) {
			return originalUrl.replace(getUriTemplate(), "");
		}
		return originalUrl;
	}

	public String getUriTemplate() {
		return uriTemplate != null ? uriTemplate : "";
	}

	/**
	 * Generate a url with all parameters.
	 *
	 * @param urlData url data object
	 * @return complete url
	 */
	void getParamProcessedUrl(final StringBuilder sb) {
		sb.setLength(0);
		if (server != null) {
			sb.append(server);
		}
		sb.append(contextPathRelativeUrl);

		// Add jSessionId
		if (jSessionId != null) {
			sb.append(';').append(jSessionId);
		}

		if (composedParams != null) {
			sb.append('?').append(composedParams);
		}
		else if (urlParams != null && urlParams.length() != 0) {
			sb.append('?').append(urlParams);
		}
	}

	public String getProcessedUrlWithHdivState(final StringBuilder sb, final String hdivParameter, final String stateParam) {

		if (stateParam == null || stateParam.length() <= 0) {
			getParamProcessedUrl(sb);
			return sb.toString();
		}
		getParamProcessedUrl(sb);
		char separator = containsParams() ? '&' : '?';

		sb.append(separator).append(hdivParameter).append('=').append(stateParam);
		if (uriTemplate != null) {
			sb.append(uriTemplate.replace('?', '&'));
		}
		if (anchor != null) {
			// it could be ""
			sb.append('#').append(anchor);
		}
		return sb.toString();

	}

	/**
	 * Generate final url with all parameters and anchor.
	 *
	 * @param url Generating url
	 * @return complete url
	 */
	public String getProcessedUrl(final StringBuilder url) {

		getParamProcessedUrl(url);

		if (anchor != null) {
			// it could be ""
			url.append('#').append(anchor);
		}
		return url.toString();
	}

	private void parser(final String uriTemplate) {
		int startVars = uriTemplate.lastIndexOf('/');
		final Matcher matcher = NAMES_PATTERN.matcher(startVars == -1 ? uriTemplate : uriTemplate.substring(startVars));
		StringBuilder sb = null;

		boolean variable = false;
		while (matcher.find()) {
			final String match = matcher.group(1);
			final int colonIdx = match.indexOf(':');
			if (colonIdx == -1) {
				variable = true;
				if (sb == null) {
					sb = new StringBuilder();
					sb.append('{');
				}
				sb.append(match);
			}
			else {
				if (colonIdx + 1 == match.length()) {
					throw new IllegalArgumentException("No custom regular expression specified after ':' in \"" + match + "\"");
				}
				if (sb == null) {
					sb = new StringBuilder();
					sb.append('{');
				}
				sb.append(match.substring(0, colonIdx));
			}
		}
		if (variable) {
			sb.append('}');
			this.uriTemplate = sb.toString();
		}
	}

	public boolean isJS() {
		return originalUrl.length() > 10 && originalUrl.charAt(10) == ':' && originalUrl.toLowerCase().startsWith("javascript:");
	}

	public boolean isHdivStateNecessary(final HDIVConfig config) {

		if (isJS() || !internal || config.isStartPage(urlWithoutContextPath, method) || hasExtensionToExclude(config)) {
			return false;
		}

		boolean validateParamLessUrls = config.isValidationInUrlsWithoutParamsActivated();
		// if url is a link (or a GET method form) and has not got parameters, we do not have to include HDIV's state
		if (isGetMethod() && !validateParamLessUrls && !containsParams()) {
			return false;
		}
		return true;
	}

	/**
	 * Determines if the url contains a extension to exclude for Hdiv state inclusion.
	 *
	 * @param urlData url data object
	 * @return is excluded or not
	 */
	private boolean hasExtensionToExclude(final HDIVConfig config) {
		if (contextPathRelativeUrl.charAt(contextPathRelativeUrl.length() - 1) == '/') {
			return false;
		}

		List<String> excludedExtensions = config.getExcludedURLExtensions();

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
		List<PatternMatcher> protectedExtension = config.getProtectedURLPatterns();
		for (int i = 0; protectedExtension != null && i < protectedExtension.size(); i++) {
			PatternMatcher extensionPattern = protectedExtension.get(i);
			if (extensionPattern.matches(contextPathRelativeUrl)) {
				return false;
			}
		}

		return contextPathRelativeUrl.charAt(0) != '/' && contextPathRelativeUrl.indexOf('.') == -1;
	}

}
