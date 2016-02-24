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

import java.io.StringWriter;
import java.util.Map;

import org.springframework.web.util.UriTemplate;

/**
 * Contains the data of an url.
 * 
 * @author Gotzon Illarramendi
 */
public class UrlData {

	/**
	 * Original url, previous to any change.
	 */
	private String originalUrl;

	/**
	 * Original urls anchor
	 */
	private String anchor;

	/**
	 * JSessionId value
	 */
	private String jSessionId;

	/**
	 * Url that starts with contextPath
	 */
	private String contextPathRelativeUrl;

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
	private String server;

	/**
	 * Http method.
	 */
	private String method;

	/**
	 * UriTemplate https://tools.ietf.org/html/rfc6570
	 * 
	 * @since 3.0.0
	 */
	private UriTemplate uriTemplate;

	/**
	 * Constructor
	 * 
	 * @param url Original url
	 * @param method Http method.
	 */
	public UrlData(String url, String method) {
		this.originalUrl = url;
		this.method = method;
		if (!"".equals(url)) {
			this.uriTemplate = new UriTemplate(url);
		}
	}

	/**
	 * Is url method GET?
	 * 
	 * @return true is it is GET
	 */
	public boolean isGetMethod() {

		return this.method != null && this.method.equalsIgnoreCase("GET");
	}

	/**
	 * Determines if url contains parameters
	 * 
	 * @return has parameters?
	 */
	public boolean containsParams() {
		return (originalUrlParams != null && originalUrlParams.size() > 0) || urlParams != null;
	}

	/**
	 * @return the originalUrl
	 */
	public String getOriginalUrl() {
		return originalUrl;
	}

	/**
	 * @param originalUrl the originalUrl to set
	 */
	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}

	/**
	 * @return the anchor
	 */
	public String getAnchor() {
		return anchor;
	}

	/**
	 * @param anchor the anchor to set
	 */
	public void setAnchor(String anchor) {
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
	public void setContextPathRelativeUrl(String contextPathRelativeUrl) {
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
	public void setUrlWithoutContextPath(String urlWithoutContextPath) {
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
	public void setOriginalUrlParams(Map<String, String[]> originalUrlParams) {
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
	public void setProcessedUrlParams(Map<String, String[]> processedUrlParams) {
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
	public void setInternal(boolean internal) {
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
	public void setServer(String server) {
		this.server = server;
	}

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
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
	public void setjSessionId(String jSessionId) {
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
	public void setUrlParams(String urlParams) {
		this.urlParams = urlParams;
	}

	public boolean hasUriTemplate() {
		return uriTemplate != null && uriTemplate.getVariableNames().size() > 0;
	}

	public String getUrlWithOutUriTemplate() {
		return this.originalUrl.replace(getUriTemplate(), "");
	}

	public String getUriTemplate() {
		if (!hasUriTemplate()) {
			return "";
		}
		StringWriter sw = new StringWriter();
		sw.append("{");
		for (String variable : uriTemplate.getVariableNames()) {
			sw.append(variable);
		}
		sw.append("}");

		return sw.toString();
	}

}
