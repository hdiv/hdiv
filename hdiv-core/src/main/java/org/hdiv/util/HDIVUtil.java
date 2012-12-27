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
import org.hdiv.urlProcessor.FormUrlProcessor;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.springframework.context.MessageSource;

/**
 * Class containing utility methods for access HDIV components: IDataComposer, IDataValidator, IApplication, ISession.
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
	public static final String DATACOMPOSER_REQUEST_KEY = "DATACOMPOSER_REQUEST_KEY";
	public static final String REQUESTURI_REQUEST_KEY = "REQUESTURI_REQUEST_KEY";
	public static final String BASEURL_REQUEST_KEY = "BASEURL_REQUEST_KEY";
	public static final String ISESSION_SERVLETCONTEXT_KEY = "ISESSION_SERVLETCONTEXT_KEY";
	public static final String LINKURLPROCESSOR_SERVLETCONTEXT_KEY = "LINKURLPROCESSOR_SERVLETCONTEXT_KEY";
	public static final String FORMURLPROCESSOR_SERVLETCONTEXT_KEY = "FORMURLPROCESSOR_SERVLETCONTEXT_KEY";

	public static Pattern intPattern = Pattern.compile("[0-9]+");

	/**
	 * HttpServletRequest thread local
	 */
	private static ThreadLocal httpRequest = new ThreadLocal();

	/**
	 * ThreadLocales is always guaranteed to be cleaned up when returning the thread to the server's pool.
	 */
	public static void resetLocalData() {

		httpRequest.set(null);
	}

	/* DataComposer */

	/**
	 * Returns data composer object from <code>HttpServletRequest</code>
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return {@link IDataComposer} instance
	 */
	public static IDataComposer getDataComposer(HttpServletRequest request) {

		IDataComposer requestDataComposer = (IDataComposer) request.getAttribute(DATACOMPOSER_REQUEST_KEY);
		if (requestDataComposer == null) {
			throw new HDIVException("IDataComposer has not been initialized in request");
		}
		return requestDataComposer;
	}

	/**
	 * Returns {@link IDataComposer} instance for this request.
	 * 
	 * @return {@link IDataComposer} instance
	 */
	public static IDataComposer getDataComposer() {

		HttpServletRequest request = getHttpServletRequest();
		IDataComposer newDataComposer = getDataComposer(request);
		return newDataComposer;

	}
	
	/**
	 * Returns true if a data composer object exist in <code>HttpServletRequest</code>
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return boolean
	 */
	public static boolean isDataComposer(HttpServletRequest request) {

		IDataComposer requestDataComposer = (IDataComposer) request.getAttribute(DATACOMPOSER_REQUEST_KEY);
		return requestDataComposer != null;
	}

	/**
	 * Set the <code>IDataComposer</code>
	 * 
	 * @param newDataComposer new {@link IDataComposer}
	 * @param request {@link HttpServletRequest} instance
	 */
	public static void setDataComposer(IDataComposer newDataComposer, HttpServletRequest request) {

		request.setAttribute(DATACOMPOSER_REQUEST_KEY, newDataComposer);

	}

	/* RequestURI */

	/**
	 * Returns RequestURI value from <code>HttpServletRequest</code>
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	public static String getRequestURI(HttpServletRequest request) {

		String requestURI = (String) request.getAttribute(REQUESTURI_REQUEST_KEY);
		if (requestURI == null) {
			throw new HDIVException("RequestURI has not been initialized in request.");
		}
		return requestURI;
	}

	/**
	 * Set the RequestURI
	 * 
	 * @param requestURI
	 *            RequestURI to set
	 * @param request
	 *            {@link HttpServletRequest} object
	 */
	public static void setRequestURI(String requestURI, HttpServletRequest request) {

		request.setAttribute(REQUESTURI_REQUEST_KEY, requestURI);

	}
	
	/* BaseURL */

	/**
	 * Returns BaseURL value from <code>HttpServletRequest</code>
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	public static String getBaseURL(HttpServletRequest request) {

		String baseURL = (String) request.getAttribute(BASEURL_REQUEST_KEY);
		return baseURL;
	}

	/**
	 * Set the BaseURL
	 * 
	 * @param baseURL
	 *            BaseURL to set
	 * @param request
	 *            {@link HttpServletRequest} object
	 */
	public static void setBaseURL(String baseURL, HttpServletRequest request) {

		request.setAttribute(BASEURL_REQUEST_KEY, baseURL);

	}

	/* IApplication */

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
		IApplication app = (IApplication) servletContext.getAttribute(APPLICATION_SERVLETCONTEXT_KEY);
		if (app == null) {
			throw new HDIVException("IApplication has not been initialized in servlet context");
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

	/* HDIVConfig */

	/**
	 * Return the <code>HDIVConfig</code> object
	 * 
	 * @return {@link HDIVConfig} instance
	 */
	public static HDIVConfig getHDIVConfig() {

		ServletContext servletContext = getHttpServletRequest().getSession().getServletContext();
		return getHDIVConfig(servletContext);

	}

	/**
	 * Return the <code>HDIVConfig</code> object
	 * 
	 * @param servletContext
	 * @return {@link HDIVConfig} instance
	 */
	public static HDIVConfig getHDIVConfig(ServletContext servletContext) {

		HDIVConfig hdivConfig = (HDIVConfig) servletContext.getAttribute(HDIVCONFIG_SERVLETCONTEXT_KEY);
		if (hdivConfig == null) {
			throw new HDIVException("HDIVConfig has not been initialized in servlet context");
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

	/* ISession */

	/**
	 * Return the <code>ISession</code> instance.
	 * 
	 * @return {@link ISession} instance
	 */
	public static ISession getISession() {

		ServletContext servletContext = getHttpServletRequest().getSession().getServletContext();
		return getISession(servletContext);

	}

	/**
	 * Return the <code>ISession</code> instance.
	 * 
	 * @param servletContext
	 * @return {@link ISession} instance
	 */
	public static ISession getISession(ServletContext servletContext) {
		ISession session = (ISession) servletContext.getAttribute(ISESSION_SERVLETCONTEXT_KEY);
		if (session == null) {
			throw new HDIVException("ISession has not been initialized in servlet context");
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

	/* UrlProcessor */

	/**
	 * Return the <code>LinkUrlProcessor</code> instance.
	 * 
	 * @param servletContext
	 *            {@link ServletContext} instance
	 * @return {@link LinkUrlProcessor} instance
	 */
	public static LinkUrlProcessor getLinkUrlProcessor(ServletContext servletContext) {
		LinkUrlProcessor urlProcessor = (LinkUrlProcessor) servletContext
				.getAttribute(LINKURLPROCESSOR_SERVLETCONTEXT_KEY);
		if (urlProcessor == null) {
			throw new HDIVException("LinkUrlProcessor has not been initialized in servlet context");
		}
		return urlProcessor;
	}

	/**
	 * Set the <code>LinkUrlProcessor</code> instance.
	 * 
	 * @param urlProcessor
	 *            {@link LinkUrlProcessor} instance
	 * @param servletContext
	 *            {@link ServletContext} instance
	 */
	public static void setLinkUrlProcessor(LinkUrlProcessor urlProcessor, ServletContext servletContext) {
		servletContext.setAttribute(LINKURLPROCESSOR_SERVLETCONTEXT_KEY, urlProcessor);
	}

	/**
	 * Return the <code>FormUrlProcessor</code> instance.
	 * 
	 * @param servletContext
	 *            {@link ServletContext} instance
	 * @return {@link FormUrlProcessor} instance
	 */
	public static FormUrlProcessor getFormUrlProcessor(ServletContext servletContext) {
		FormUrlProcessor urlProcessor = (FormUrlProcessor) servletContext
				.getAttribute(FORMURLPROCESSOR_SERVLETCONTEXT_KEY);
		if (urlProcessor == null) {
			throw new HDIVException("FormUrlProcessor has not been initialized in servlet context");
		}
		return urlProcessor;
	}

	/**
	 * Set the <code>FormUrlProcessor</code> instance.
	 * 
	 * @param urlProcessor
	 *            {@link FormUrlProcessor} instance
	 * @param servletContext
	 *            {@link ServletContext} instance
	 */
	public static void setFormUrlProcessor(FormUrlProcessor urlProcessor, ServletContext servletContext) {
		servletContext.setAttribute(FORMURLPROCESSOR_SERVLETCONTEXT_KEY, urlProcessor);
	}

	/* HttpSession */

	/**
	 * Return the <code>HttpSession</code> object.
	 * 
	 * @return {@link HttpSession} instance
	 */
	public static HttpSession getHttpSession() {
		HttpServletRequest request = getHttpServletRequest();
		return request.getSession();
	}

	/* HttpServletRequest */

	/**
	 * Return the <code>HttpServletRequest</code> object.
	 * 
	 * @return {@link HttpServletRequest} instance
	 */
	public static HttpServletRequest getHttpServletRequest() {
		HttpServletRequest request = (HttpServletRequest) httpRequest.get();
		if (request == null) {
			throw new HDIVException("Request has not been initialized in threadlocal");
		}
		return request;
	}

	/**
	 * Set the <code>HttpServletRequest</code> instance in {@link ThreadLocal}
	 * 
	 * @param httpServletRequest
	 */
	public static void setHttpServletRequest(HttpServletRequest httpServletRequest) {
		httpRequest.set(httpServletRequest);
	}

	/* MessageSource */

	/**
	 * Return the {@link MessageSource} instance.
	 * 
	 * @return {@link MessageSource} instance
	 */
	public static MessageSource getMessageSource() {

		ServletContext servletContext = getHttpServletRequest().getSession().getServletContext();
		return getMessageSource(servletContext);
	}

	/**
	 * Return the {@link MessageSource} instance.
	 * 
	 * @param servletContext
	 * @return {@link MessageSource} instance
	 */
	public static MessageSource getMessageSource(ServletContext servletContext) {
		MessageSource msgSource = (MessageSource) servletContext.getAttribute(MESSAGESOURCE_SERVLETCONTEXT_KEY);
		if (msgSource == null) {
			throw new HDIVException("MessageSource has not been initialized in servlet context");
		}
		return msgSource;
	}

	/**
	 * Set the {@link MessageSource} instance.
	 * 
	 * @param msgSource
	 * @param servletContext
	 */
	public static void setMessageSource(MessageSource msgSource, ServletContext servletContext) {
		servletContext.setAttribute(MESSAGESOURCE_SERVLETCONTEXT_KEY, msgSource);
	}

	/**
	 * Try to resolve the message. Treat as an error if the message can't be found.
	 * 
	 * @param key
	 *            the code to lookup up, such as 'calculator.noRateSet'
	 * @return The resolved message
	 */
	public static String getMessage(String key) {
		return HDIVUtil.getMessage(key, null);
	}

	/**
	 * Try to resolve the message. Treat as an error if the message can't be found.
	 * 
	 * @param key
	 *            the code to lookup up, such as 'calculator.noRateSet'
	 * @param o
	 *            Array of arguments that will be filled in for params within the message (params look like "{0}",
	 *            "{1,date}", "{2,time}" within a message), or null if none.
	 * @return The resolved message
	 */
	public static String getMessage(String key, String o) {
		return HDIVUtil.getMessage(key, o, Locale.getDefault());
	}

	/**
	 * Try to resolve the message. Treat as an error if the message can't be found.
	 * 
	 * @param key
	 *            the code to lookup up, such as 'calculator.noRateSet'
	 * @param o
	 *            Array of arguments that will be filled in for params within the message (params look like "{0}",
	 *            "{1,date}", "{2,time}" within a message), or null if none.
	 * @param userLocale
	 *            locale
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
	 * Generates a random number between 0 (inclusive) and n (exclusive).
	 * 
	 * @param n
	 *            the bound on the random number to be returned. Must be positive.
	 * @return Returns a pseudorandom, uniformly distributed int value between 0 (inclusive) and <code>n</code>
	 *         (exclusive).
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