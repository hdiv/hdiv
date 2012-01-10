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
package org.hdiv.util;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.dataComposer.IDataComposer;

public class HDIVRequestUtils {

	/**
	 * Commons Logging instance.
	 */
	private static Log log = LogFactory.getLog(HDIVRequestUtils.class);
	
    /** <p>Valid characters in a scheme.</p>
     *  <p>RFC 1738 says the following:</p>
     *  <blockquote>
     *   Scheme names consist of a sequence of characters. The lower
     *   case letters "a"--"z", digits, and the characters plus ("+"),
     *   period ("."), and hyphen ("-") are allowed. For resiliency,
     *   programs interpreting URLs should treat upper case letters as
     *   equivalent to lower case in scheme names (e.g., allow "HTTP" as
     *   well as "http").
     *  </blockquote>
     * <p>We treat as absolute any URL that begins with such a scheme name,
     * followed by a colon.</p>
     */
    public static final String VALID_SCHEME_CHARS =
	"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789+.-";
	
	/**
	 * <p>
	 * It generates a new encoded values for the <code>url</code> parameters.
	 * </p>
	 * <p>
	 * The returned values guarantees the confidentiality in the encoded and
	 * memory strategies if confidentiality indicator defined by user is true.
	 * </p>
	 * 
	 * @param url request url
	 * @param questionIndex index of the first question occurrence in
	 *            <code>url</code> string
	 * @param charEncoding character encoding
	 * @return url with encoded values
	 */
	public static String composeAction(String url, int questionIndex, String charEncoding) {

		IDataComposer dataComposer = HDIVUtil.getDataComposer();
		String composed = HDIVRequestUtils.composeAction(url, questionIndex, charEncoding, dataComposer);

		return composed;
	}

	/**
	 * <p>
	 * It generates a new encoded values for the <code>url</code> parameters.
	 * </p>
	 * <p>
	 * The returned values guarantees the confidentiality in the encoded and
	 * memory strategies if confidentiality indicator defined by user is true.
	 * </p>
	 * 
	 * @param url request url
	 * @param questionIndex index of the first question occurrence in
	 *            <code>url</code> string
	 * @param charEncoding character encoding
	 * @param dataComposer the dataComposer
	 * @return url with encoded values
	 */
	public static String composeAction(String url, int questionIndex, String charEncoding, IDataComposer dataComposer) {
		
		String value = url;

		value = value.substring(questionIndex + 1);
		value = value.replaceAll("&amp;", "&");

		String token = null;
		String urlAction = HDIVUtil.getActionMappingName(url);

		StringTokenizer st = new StringTokenizer(value, "&");
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			String param = token.substring(0, token.indexOf("="));
			String val = token.substring(token.indexOf("=") + 1);

			String encodedValue = dataComposer.compose(urlAction, param, val, false, true, charEncoding);
			value = value.replaceFirst(HDIVUtil.protectCharacters(token), param + "=" + encodedValue);
		}

		return url.substring(0, questionIndex + 1) + value;
	}
	
	/**
	 * It creates a new state to store all the parameters and values of the
	 * <code>request</code> and it generates a new encoded values for the
	 * <code>request</code> parameters and adds the HDIV parameter.
	 * 
	 * @param request HTTP request
	 * @param finalLocation the location to redirect to
	 * @return URL with encoded parameters and HDIV parameter
	 */
	private static String composeURL(HttpServletRequest request, String finalLocation) {

		String encodedURL = finalLocation;

		IDataComposer dataComposer = HDIVUtil.getDataComposer(request);//TODO Can you pass as a parameter?
		
		dataComposer.beginRequest(HDIVUtil.getActionMappingName(finalLocation));

		int question = finalLocation.indexOf("?");
		if (question > 0) {

			// generate a new encoded values for the url parameters
			encodedURL = HDIVRequestUtils.composeAction(finalLocation, question, "UTF-8", dataComposer);
		}

		return HDIVRequestUtils.addExtraParameters(request, dataComposer, encodedURL);
	}

	/**
	 * Adds the HDIV parameter, depending on the strategy defined by the user,
	 * to validate the request <code>encodedURL</code>.
	 * 
	 * @param request HTTP request
	 * @param dataComposer HDIV's data composer
	 * @param encodedURL URL encoded
	 * @return <code>url</code> with the HDIV state added as a new parameters.
	 * @see org.hdiv.composer.IDataComposer
	 * @since HDIV 2.0.3
	 */
	public static String addExtraParameters(HttpServletRequest request, IDataComposer dataComposer, String encodedURL) {

		String hdivParameter = (String) request.getSession().getAttribute("HDIVParameter");
		String requestId = dataComposer.endRequest();
		
		return addHDIVState(hdivParameter, requestId, encodedURL);
	}

	/**
	 * Adds the HDIV parameter, depending on the strategy defined by the user,
	 * to validate the request <code>encodedURL</code>.
	 * 
	 * @param hdivParameter HDIV's parameter name
	 * @param requestId HDIV's parameter value
	 * @param encodedURL URL encoded
	 * @return <code>url</code> with the HDIV state added as a new parameter
	 * @see org.hdiv.composer.IDataComposer
	 */
	public static String addHDIVState(String hdivParameter, String requestId, String encodedURL) {

		String separator = "";

		if ((requestId.length() <= 0) || (encodedURL.startsWith("javascript:"))) {
			return encodedURL;
		}

		// we check if the url contains parameters
		separator = (encodedURL.indexOf("?") > 0) ? "&" : "?";

		StringBuffer sb = new StringBuffer();
		sb.append(encodedURL)
			.append(separator)
			.append(hdivParameter)
			.append("=")
			.append(requestId);
		
		return sb.toString();
	}
	
	/**
	 * Adds HDIV state as a parameters if
	 * <code>url</code> references our application.
	 * 
	 * @param request HTTP request
	 * @param url URL
	 * @param validationInUrlsWithoutParamsActivated
	 * @return <code>url</code> with the HDIV state added as a new parameters
	 */
	public static String addHDIVParameterIfNecessary(HttpServletRequest request, 
												 	 String url, 
												 	 boolean validationInUrlsWithoutParamsActivated) {
		
		// if url has not got parameters, we do not have to include HDIV's state
		if (!validationInUrlsWithoutParamsActivated && !(url.indexOf("?")>0)) {
			return url;
		}
		
		if (!HDIVRequestUtils.isAbsoluteUrl(url)) {

			url = HDIVRequestUtils.getContextRelativePath(request, url);
			if (url.indexOf(request.getContextPath()) != -1) {
				url = HDIVRequestUtils.composeURL(request, url);
			}

		} else { // URL is absolute			
			if (url.indexOf(request.getContextPath()) != -1) {

				url = url.substring(url.indexOf(request.getContextPath()));
				url = HDIVRequestUtils.composeURL(request, url);
			}
		}
		return url;
	}
	
    /**
     * Returns <tt>true</tt> if our current URL is absolute,
     * <tt>false</tt> otherwise.
     */
    public static boolean isAbsoluteUrl(String url) {
		// a null URL is not absolute, by our definition
		if (url == null)
		    return false;
	
		// do a fast, simple check first
		int colonPos;
		if ((colonPos = url.indexOf(":")) == -1)
		    return false;
	
		// if we DO have a colon, make sure that every character
		// leading up to it is a valid scheme character
		for (int i = 0; i < colonPos; i++)
		    if (VALID_SCHEME_CHARS.indexOf(url.charAt(i)) == -1)
			return false;
	
		// if so, we've got an absolute url
		return true;
    }
    
	public static String getContextRelativePath(HttpServletRequest request, String relativePath) {

		String returnValue = null;
		
		String originalRequestUri = (String)request.getAttribute(Constants.REQUEST_URI_KEY);

		if (relativePath.startsWith("/")) {
			returnValue = relativePath;
		} else if (relativePath.startsWith("..")) {
			returnValue = relativePath;
		} else {
			// relative path
			String uri = originalRequestUri;
			uri = uri.substring(uri.indexOf("/"), uri.lastIndexOf("/"));
			returnValue = uri + "/" + relativePath;
		}

		return removeRelativePaths(returnValue, originalRequestUri);
	}
	
	/**
	 * Removes from <code>url<code> references to relative paths.
	 * 
	 * @param url url
	 * @param originalRequestUri originalRequestUri
	 * @return returns <code>url</code> without relative paths.
	 * @since HDIV 2.0.3
	 */
	public static String removeRelativePaths(String url, String originalRequestUri) {

		String urlWithoutRelativePath = url;

		if (url.startsWith("..")) {
			Stack stack = new Stack();
			String localUri = originalRequestUri.substring(originalRequestUri.indexOf("/"), originalRequestUri.lastIndexOf("/"));
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
	 * Checks if <code>path</code> has an action extension or a jsp page
	 * extension.
	 * 
	 * @param path path
	 * @param extensions extensions table
	 * @return True if <code>path</code> is an action or references a jsp
	 *         page.
	 */
	public static boolean hasActionOrServletExtension(String path, Hashtable extensions) {

		if (path.indexOf("?") > 0) {
			path = path.substring(0, path.indexOf("?"));
		}

		if (path.charAt(path.length() - 1) == '/') {
			return true;
		}

		int pound = path.indexOf("#");
		if (pound >= 0) {
			path = path.substring(0, pound);
		}

		// strip a servlet session ID from
		path = HDIVUtil.stripSession(path);

		if (path.endsWith(".jsp")) {
			return true;
		}

		if (extensions != null) {

			for (Enumeration extensionsIds = extensions.elements(); extensionsIds.hasMoreElements();) {
				
				Pattern extensionPattern = (Pattern) extensionsIds.nextElement();
				Matcher m = extensionPattern.matcher(path);
					
				if (m.matches()) {
					return true;
				}
			}
		}

		return (!path.startsWith("/")) && (path.indexOf(".") == -1);
	}
	
	public static boolean hasExtensionToExclude(String path, List extensions)
	{
		if (path.indexOf("?") > 0) {
			path = path.substring(0, path.indexOf("?"));
		}

		if (path.charAt(path.length() - 1) == '/') {
			return false;
		}
		
		int pound = path.indexOf("#");
		if (pound >= 0) {
			path = path.substring(0, pound);
		}

		// strip a servlet session ID from
		path = HDIVUtil.stripSession(path);
		
		if (extensions != null) {

			for (Iterator iter = extensions.iterator(); iter.hasNext();) {
				
				String extension = (String) iter.next();
				if (path.endsWith(extension)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static String composeLinkUrl(String url, HttpServletRequest request){
		
		ServletContext servletContext = request.getSession().getServletContext();
		
		HDIVConfig hdivConfig = HDIVUtil.getHDIVConfig(servletContext);
		
		//Remove the anchor from the url
		String anchor = HDIVRequestUtils.getAnchorFromUrl(url);
		String urlWithoutAnchor = HDIVRequestUtils.removeAnchorFromUrl(url);
		
		boolean startPage = isUrlStartPage(urlWithoutAnchor, request, hdivConfig);
		if(startPage){
			return url;
		}
		
		if (!HDIVRequestUtils.hasExtensionToExclude(urlWithoutAnchor, hdivConfig.getExcludedURLExtensions()))
		{
			if (HDIVRequestUtils.hasActionOrServletExtension(urlWithoutAnchor, hdivConfig.getProtectedURLPatterns())) {
				urlWithoutAnchor = HDIVRequestUtils.addHDIVParameterIfNecessary(request, 
						urlWithoutAnchor, hdivConfig.isValidationInUrlsWithoutParamsActivated());
			}
		}
		
		return HDIVRequestUtils.appendAnchorToUrl(urlWithoutAnchor, anchor);
	}
	
	/**
	 * Determines if the url is a startPage
	 * @param url
	 * @param request
	 * @param hdivConfig
	 * @return boolean
	 */
	public static boolean isUrlStartPage(String url, HttpServletRequest request, HDIVConfig hdivConfig){
		
		//Obtain complete url (relative urls are completed)
		String contextPathUrl = HDIVRequestUtils.getContextRelativePath(request, url);
		
		//If this is a start page, don't compose
		if(hdivConfig.isStartPage(contextPathUrl)){
			return true;
		}
		
		//If the url contains the context path and is a start page, don't compose
		if(contextPathUrl.startsWith(request.getContextPath())){
			String urlWithoutContextPath = contextPathUrl.substring(request.getContextPath().length());
			if(hdivConfig.isStartPage(urlWithoutContextPath)){
				return true;
			}
		}
		
		return false;
	}
	
	public static String appendAnchorToUrl(String url, String anchor){
		
		if(anchor != null){
			return url + "#" + anchor;
		}
		return url;
	}
	
	public static String getAnchorFromUrl(String url){
		String anchor = null;
		if(url.indexOf('#') >= 0){
			anchor = url.substring(url.indexOf('#')+1);
		}
		return anchor;
	}
	
	public static String removeAnchorFromUrl(String url){

		if(url.indexOf('#') >= 0){
			return url.substring(0, url.indexOf('#'));
		}else{
			return url;
		}
	}
	
}
