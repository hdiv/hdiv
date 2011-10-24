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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.application.IApplication;
import org.hdiv.config.HDIVConfig;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.exception.HDIVException;
import org.hdiv.session.ISession;
import org.springframework.context.MessageSource;

/**
 * Class containing utility methods for access HDIV components: IDataComposer,
 * IDataValidator, IApplication, ISession.
 * <p>
 * This class is initialized from a Listener and a Filter.
 * </p>
 * 
 * @author Roberto Velasco
 * @author Gorka Vicente
 * @author Gotzon Illarramendi
 */
public class HDIVUtil {

	/**
	 * Commons Logging instance.
	 */
	private static Log log = LogFactory.getLog(HDIVUtil.class);

	public static final String APPLICATION_SERVLETCONTEXT_KEY = "APPLICATION_SERVLETCONTEXT_KEY";
	public static final String MESSAGESOURCE_SERVLETCONTEXT_KEY = "MESSAGESOURCE_SERVLETCONTEXT_KEY";
	public static final String HDIVCONFIG_SERVLETCONTEXT_KEY = "HDIVCONFIG_SERVLETCONTEXT_KEY";
	public static final String DATACOMPOSER_REQUEST_KEY = "dataComposer";
	public static final String ISESSION_SERVLETCONTEXT_KEY = "ISESSION_SERVLETCONTEXT_KEY";
	
	public static Pattern intPattern = Pattern.compile("[0-9]+");
	
	/**
	 * HttpServletRequest thread local
	 */
	private static ThreadLocal httpRequest = new ThreadLocal();
	
	/**
	 * ThreadLocales is always guaranteed to be cleaned up when returning the
	 * thread to the server's pool.
	 */
	public static void resetLocalData() {

		httpRequest.set(null);
	}

	/* DataComposer*/
	
	/**
	 * Returns data composer object from <code>HttpServletRequest</code>
	 * 
	 * @param request HttpServletRequest
	 * @return IDataComposer
	 */
	public static IDataComposer getDataComposer(HttpServletRequest request) {

		IDataComposer requestDataComposer = (IDataComposer) request.getAttribute(DATACOMPOSER_REQUEST_KEY);
		if(requestDataComposer==null){
			throw new HDIVException("No se ha inicializado el objeto IDataComposer en request");
		}
		return requestDataComposer;
	}

	/**
	 * @return Returns data composer object
	 */
	public static IDataComposer getDataComposer() {

		HttpServletRequest request = getHttpServletRequest();
		IDataComposer newDataComposer= getDataComposer(request);
		return newDataComposer;
		
	}
	
	/**
	 * Set the <code>IDataComposer</code>
	 * 
	 * @param newDataComposer
	 * @param request
	 */
	public static void setDataComposer(IDataComposer newDataComposer, HttpServletRequest request) {

		request.setAttribute(DATACOMPOSER_REQUEST_KEY, newDataComposer);
		
	}
	
	/* IApplication*/
	
	/**
	 * @return Returns the servlet context wrapper object.
	 */
	public static IApplication getApplication() {

		ServletContext servletContext = getHttpServletRequest().getSession().getServletContext();
		return getApplication(servletContext);
		
	}
	
	/**
	 * Returns the servlet context wrapper object.
	 * 
	 * @param servletContext
	 * @return IApplication object
	 */
	public static IApplication getApplication(ServletContext servletContext) {
		IApplication app= (IApplication) servletContext.getAttribute(APPLICATION_SERVLETCONTEXT_KEY);
		if(app==null){
			throw new HDIVException("No se ha inicializado el objeto IApplication en servletContext");
		}
		return app;
	}

	/**
	 * Set the <code>IApplication</code> in <code>ServletContext</code>
	 * 
	 * @param newApplication
	 * @param servletContext
	 */
	public static void setApplication(IApplication newApplication, ServletContext servletContext) {
		servletContext.setAttribute(APPLICATION_SERVLETCONTEXT_KEY, newApplication);
	}
	
	/* HDIVConfig*/
	
	/**
	 * Return the <code>HDIVConfig</code> object
	 * 
	 * @return HDIVConfig
	 */
	public static HDIVConfig getHDIVConfig() {

		ServletContext servletContext = getHttpServletRequest().getSession().getServletContext();
		return getHDIVConfig(servletContext);
		
	}
	
	/**
	 * Return the <code>HDIVConfig</code> object
	 * 
	 * @param servletContext
	 * @return HDIVConfig
	 */
	public static HDIVConfig getHDIVConfig(ServletContext servletContext) {
		
		HDIVConfig hdivConfig = (HDIVConfig) servletContext.getAttribute(HDIVCONFIG_SERVLETCONTEXT_KEY);
		if (hdivConfig == null){
			throw new HDIVException("HDIVConfig has not been initialized in in servlet context");
		}
		
		return hdivConfig;
	}
	
	/**
	 * Set the <code>HDIVConfig</code> object
	 * 
	 * @param hdivConfig
	 * @param servletContext
	 */
	public static void setHDIVConfig(HDIVConfig hdivConfig, ServletContext servletContext) {
		servletContext.setAttribute(HDIVCONFIG_SERVLETCONTEXT_KEY, hdivConfig);
	}

	/* ISession*/
	
	/**
	 * Return the <code>ISession</code> instance.
	 * 
	 * @return
	 */
	public static ISession getISession() {

		ServletContext servletContext = getHttpServletRequest().getSession().getServletContext();
		return getISession(servletContext);
		
	}
	
	/**
	 * Return the <code>ISession</code> instance.
	 * 
	 * @param servletContext
	 * @return
	 */
	public static ISession getISession(ServletContext servletContext) {
		ISession session= (ISession) servletContext.getAttribute(ISESSION_SERVLETCONTEXT_KEY);
		if(session==null){
			throw new HDIVException("No se ha inicializado el objeto ISession en servletContext");
		}
		return session;
	}
	
	/**
	 * Set the <code>ISession</code> instance.
	 * 
	 * @param session
	 * @param servletContext
	 */
	public static void setISession(ISession session, ServletContext servletContext) {
		servletContext.setAttribute(ISESSION_SERVLETCONTEXT_KEY, session);
	}	

	/* HttpSession*/
	
	/**
	 * Return the <code>HttpSession</code> object.
	 * @return
	 */
	public static HttpSession getHttpSession() {
		HttpServletRequest request = getHttpServletRequest();
		return request.getSession();
	}

	/* HttpServletRequest*/
	
	/**
	 * Return the <code>HttpServletRequest</code> object.
	 * @return
	 */
	public static HttpServletRequest getHttpServletRequest() {
		HttpServletRequest request = (HttpServletRequest) httpRequest.get();
		if(request == null){
			throw new HDIVException("No se ha inicializado el request en threadlocal");
		}
		return request;
	}

	/**
	 * Set the <code>HttpServletRequest</code> instance in {@link ThreadLocal}
	 * @param httpServletRequest
	 */
	public static void setHttpServletRequest(HttpServletRequest httpServletRequest) {
		httpRequest.set(httpServletRequest);
	}

	/* MessageSource*/
	
	/**
	 * Return the {@link MessageSource} instance.
	 * @return
	 */
	public static MessageSource getMessageSource() {
		
		ServletContext servletContext = getHttpServletRequest().getSession().getServletContext();
		return getMessageSource(servletContext);
	}
	
	/**
	 * Return the {@link MessageSource} instance.
	 * @param servletContext
	 * @return
	 */
	public static MessageSource getMessageSource(ServletContext servletContext) {
		MessageSource msgSource = (MessageSource) servletContext.getAttribute(MESSAGESOURCE_SERVLETCONTEXT_KEY);
		if(msgSource==null){
			throw new HDIVException("No se ha inicializado el MessageSource en servletContext");
		}
		return msgSource;
	}

	/**
	 * Set the {@link MessageSource} instance.
	 * @param msgSource
	 * @param servletContext
	 */
	public static void setMessageSource(MessageSource msgSource, ServletContext servletContext) {
		servletContext.setAttribute(MESSAGESOURCE_SERVLETCONTEXT_KEY, msgSource);
	}
	
	
	/**
	 * Try to resolve the message. Treat as an error if the message can't be
	 * found.
	 * 
	 * @param key the code to lookup up, such as 'calculator.noRateSet'
	 * @return The resolved message
	 */
	public static String getMessage(String key) {
		return HDIVUtil.getMessage(key, null);
	}

	/**
	 * Try to resolve the message. Treat as an error if the message can't be
	 * found.
	 * 
	 * @param key the code to lookup up, such as 'calculator.noRateSet'
	 * @param o Array of arguments that will be filled in for params within the
	 *            message (params look like "{0}", "{1,date}", "{2,time}" within
	 *            a message), or null if none.
	 * @return The resolved message
	 */
	public static String getMessage(String key, String o) {
		return HDIVUtil.getMessage(key, o, Locale.getDefault());
	}

	/**
	 * Try to resolve the message. Treat as an error if the message can't be
	 * found.
	 * 
	 * @param key the code to lookup up, such as 'calculator.noRateSet'
	 * @param o Array of arguments that will be filled in for params within the
	 *            message (params look like "{0}", "{1,date}", "{2,time}" within
	 *            a message), or null if none.
	 * @param userLocale locale
	 * @return The resolved message
	 */
	public static String getMessage(String key, String o, Locale userLocale) {

		String resolvedMessage = HDIVUtil.getMessageSource().getMessage(key, new String[] { o }, userLocale);
		if (log.isDebugEnabled()) {
			log.debug(resolvedMessage);
		}
		return resolvedMessage;
	}

	/**
	 * Return the form action converted into an action mapping path. The value
	 * of the <code>action</code> property is manipulated as follows in
	 * computing the name of the requested mapping:
	 * <ul>
	 * <li>Any filename extension is removed (on the theory that extension
	 * mapping is being used to select the controller servlet).</li>
	 * <li>If the resulting value does not start with a slash, then a slash is
	 * prepended.</li>
	 * </ul>
	 * 
	 * @param url URL representing the current request
	 * @return the form action converted into an action mapping path.
	 */
	public static String getActionMappingName(String url) {

		String value = url;
		int question = url.indexOf("?");
		if (question >= 0) {
			value = value.substring(0, question);
		}

		int pound = value.indexOf("#");
		if (pound >= 0) {
			value = value.substring(0, pound);
		}

		// strip a servlet session ID from
		value = stripSession(value);

		int slash = value.lastIndexOf("/");
		int period = value.lastIndexOf(".");

		// struts-examples/dir/action.do
		if ((period >= 0) && (period > slash)) {
			value = value.substring(0, value.length());
		}

		return value.startsWith("/") ? value : ("/" + value);
	}

	/**
	 * Strips a servlet session ID from <tt>url</tt>. The session ID is
	 * encoded as a URL "path parameter" beginning with "jsessionid=". We thus
	 * remove anything we find between ";jsessionid=" (inclusive) and either EOS
	 * or a subsequent ';' (exclusive).
	 */
	public static String stripSession(String url) {
 
		if (log.isDebugEnabled()) {
			log.debug("Stripping jsessionid from url " + url);
		}
		StringBuffer u = new StringBuffer(url);
		int sessionStart;

		while ( ((sessionStart = u.toString().indexOf(";jsessionid=")) != -1) 
				|| ((sessionStart = u.toString().indexOf(";JSESSIONID=")) != -1) ) {
 
			int sessionEnd = u.toString().indexOf(";", sessionStart + 1);
			if (sessionEnd == -1) {
				sessionEnd = u.toString().indexOf("?", sessionStart + 1);
			}
			if (sessionEnd == -1) { // still
				sessionEnd = u.length();
			}
			u.delete(sessionStart, sessionEnd);
		}
		return u.toString();
	}

	/**
	 * Return the URL representing the current request.
	 * 
	 * @param request The servlet request we are processing
	 * @return URL representing the current request
	 * @exception Exception if a URL cannot be created
	 */
	public static String actionName(HttpServletRequest request) throws Exception {

		return requestURL(request).getFile();
	}

	/**
	 * Return the URL representing the current request. This is equivalent to
	 * <code>HttpServletRequest.getRequestURL()</code> in Servlet 2.3.
	 * 
	 * @param request The servlet request we are processing
	 * @return URL representing the current request
	 * @exception MalformedURLException if a URL cannot be created
	 */
	public static URL requestURL(HttpServletRequest request) throws MalformedURLException {
		//TODO Is it necessary to do this to create a URL if then only used in actionName?

		StringBuffer url = new StringBuffer();
		String scheme = request.getScheme();
		int port = request.getServerPort();
		if (port < 0) {
			port = 80; // Work around java.net.URL bug
		}
		url.append(scheme);
		url.append("://");
		url.append(request.getServerName());
		if ((scheme.equals("http") && (port != 80)) || (scheme.equals("https") && (port != 443))) {
			url.append(':');
			url.append(port);
		}
		url.append(request.getRequestURI());
		return (new URL(HDIVUtil.stripSession(url.toString())));
	}

	/**
	 * Function to protect meaningful characters of regular expressions
	 * (+,*,...)
	 * 
	 * @param par Parameter to encode
	 * 
	 * @return Returns par with protected characters
	 */
	public static String protectCharacters(String par) {

		par = par.replaceAll("\\+", "\\\\+");
		par = par.replaceAll("\\*", "\\\\*");
		par = par.replaceAll("\\?", "\\\\?");
		par = par.replaceAll("\\$", "\\\\\\$");
		par = par.replaceAll("\\^", "\\\\^");
		par = par.replaceAll("\\[", "\\\\[");
		par = par.replaceAll("\\(", "\\\\(");
		par = par.replaceAll("\\)", "\\\\)");
		par = par.replaceAll("\\|", "\\\\|");
		return par;
	}
	
	/**
	 * Generates a random number between 0 (inclusive) and n (exclusive).
	 * 
	 * @param n the bound on the random number to be returned. Must be positive.
	 * @return Returns a pseudorandom, uniformly distributed int value between 0
	 *         (inclusive) and <code>n</code> (exclusive).
	 * @since HDIV 1.1
	 */
	public static String createRandomToken(int n) {

		Random r = new Random();
		int i = r.nextInt(n);
		if (i == 0) {
			i = 1;
		}

		return String.valueOf(i);
	}

}