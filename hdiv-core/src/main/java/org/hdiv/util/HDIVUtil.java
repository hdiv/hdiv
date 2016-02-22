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
package org.hdiv.util;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.application.IApplication;
import org.hdiv.config.HDIVConfig;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.exception.HDIVException;
import org.hdiv.urlProcessor.FormUrlProcessor;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.springframework.context.MessageSource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

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
	private static final Log log = LogFactory.getLog(HDIVUtil.class);

	public static final String APPLICATION_SERVLETCONTEXT_KEY = "APPLICATION_SERVLETCONTEXT_KEY";

	public static final String MESSAGESOURCE_SERVLETCONTEXT_KEY = "MESSAGESOURCE_SERVLETCONTEXT_KEY";

	public static final String HDIVCONFIG_SERVLETCONTEXT_KEY = "HDIVCONFIG_SERVLETCONTEXT_KEY";

	public static final String DATACOMPOSER_REQUEST_KEY = "DATACOMPOSER_REQUEST_KEY";

	public static final String REQUESTURI_REQUEST_KEY = "REQUESTURI_REQUEST_KEY";

	public static final String BASEURL_REQUEST_KEY = "BASEURL_REQUEST_KEY";

	public static final String LINKURLPROCESSOR_SERVLETCONTEXT_KEY = "LINKURLPROCESSOR_SERVLETCONTEXT_KEY";

	public static final String FORMURLPROCESSOR_SERVLETCONTEXT_KEY = "FORMURLPROCESSOR_SERVLETCONTEXT_KEY";

	public static final String CURRENT_PAGE_KEY = "CURRENT_PAGE_KEY";

	public static final Pattern intPattern = Pattern.compile("[0-9]+");

	/* DataComposer */

	/**
	 * Returns data composer object from <code>HttpServletRequest</code>
	 * 
	 * @param request HttpServletRequest
	 * @return {@link IDataComposer} instance
	 */
	public static IDataComposer getDataComposer(HttpServletRequest request) {

		IDataComposer requestDataComposer = (IDataComposer) request.getAttribute(DATACOMPOSER_REQUEST_KEY);
		return requestDataComposer;
	}

	/**
	 * Returns true if a data composer object exist in <code>HttpServletRequest</code>
	 * 
	 * @param request HttpServletRequest
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

	/**
	 * Remove the <code>IDataComposer</code> from the request.
	 * 
	 * @param request {@link HttpServletRequest} instance
	 */
	public static void removeDataComposer(HttpServletRequest request) {

		request.removeAttribute(DATACOMPOSER_REQUEST_KEY);
	}

	/* RequestURI */

	/**
	 * Returns RequestURI value from <code>HttpServletRequest</code>
	 * 
	 * @param request HttpServletRequest
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
	 * @param requestURI RequestURI to set
	 * @param request {@link HttpServletRequest} object
	 */
	public static void setRequestURI(String requestURI, HttpServletRequest request) {

		request.setAttribute(REQUESTURI_REQUEST_KEY, requestURI);
	}

	/* BaseURL */

	/**
	 * Returns BaseURL value from <code>HttpServletRequest</code>
	 * 
	 * @param request HttpServletRequest
	 * @return String
	 */
	public static String getBaseURL(HttpServletRequest request) {

		String baseURL = (String) request.getAttribute(BASEURL_REQUEST_KEY);
		return baseURL;
	}

	/**
	 * Set the BaseURL
	 * 
	 * @param baseURL BaseURL to set
	 * @param request {@link HttpServletRequest} object
	 */
	public static void setBaseURL(String baseURL, HttpServletRequest request) {

		request.setAttribute(BASEURL_REQUEST_KEY, baseURL);
	}

	/* IApplication */

	/**
	 * Returns the servlet context wrapper object.
	 * 
	 * @param servletContext {@link ServletContext} instance
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
	 * @param newApplication new {@link IApplication} instance
	 * @param servletContext {@link ServletContext} instance
	 */
	public static void setApplication(IApplication newApplication, ServletContext servletContext) {
		servletContext.setAttribute(APPLICATION_SERVLETCONTEXT_KEY, newApplication);
	}

	/* HDIVConfig */

	/**
	 * Return the <code>HDIVConfig</code> object
	 * 
	 * @param servletContext {@link ServletContext} instance
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
	 * @param hdivConfig {@link HDIVConfig} instance
	 * @param servletContext {@link ServletContext} instance
	 */
	public static void setHDIVConfig(HDIVConfig hdivConfig, ServletContext servletContext) {
		servletContext.setAttribute(HDIVCONFIG_SERVLETCONTEXT_KEY, hdivConfig);
	}

	/* UrlProcessor */

	/**
	 * Return the <code>LinkUrlProcessor</code> instance.
	 * 
	 * @param servletContext {@link ServletContext} instance
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
	 * @param urlProcessor {@link LinkUrlProcessor} instance
	 * @param servletContext {@link ServletContext} instance
	 */
	public static void setLinkUrlProcessor(LinkUrlProcessor urlProcessor, ServletContext servletContext) {
		servletContext.setAttribute(LINKURLPROCESSOR_SERVLETCONTEXT_KEY, urlProcessor);
	}

	/**
	 * Return the <code>FormUrlProcessor</code> instance.
	 * 
	 * @param servletContext {@link ServletContext} instance
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
	 * @param urlProcessor {@link FormUrlProcessor} instance
	 * @param servletContext {@link ServletContext} instance
	 */
	public static void setFormUrlProcessor(FormUrlProcessor urlProcessor, ServletContext servletContext) {
		servletContext.setAttribute(FORMURLPROCESSOR_SERVLETCONTEXT_KEY, urlProcessor);
	}

	/* CurrentPageId */

	/**
	 * Returns CurrentPageId value from <code>HttpServletRequest</code>
	 * 
	 * @param request {@link HttpServletRequest} object
	 * @return pageId
	 */
	public static Integer getCurrentPageId(HttpServletRequest request) {
		return (Integer) request.getAttribute(CURRENT_PAGE_KEY);
	}

	/**
	 * Set the CurrentPageId
	 * 
	 * @param pageId Current page id
	 * @param request {@link HttpServletRequest} object
	 */
	public static void setCurrentPageId(Integer pageId, HttpServletRequest request) {

		request.setAttribute(CURRENT_PAGE_KEY, pageId);
	}

	/* MessageSource */

	/**
	 * Return the {@link MessageSource} instance.
	 * 
	 * @param request HttpServletRequest object
	 * @return {@link MessageSource} instance
	 */
	public static MessageSource getMessageSource(HttpServletRequest request) {

		ServletContext servletContext = request.getSession().getServletContext();
		return getMessageSource(servletContext);
	}

	/**
	 * Return the {@link MessageSource} instance.
	 * 
	 * @param servletContext {@link ServletContext} instance
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
	 * @param msgSource {@link MessageSource} instance
	 * @param servletContext {@link ServletContext} instance
	 */
	public static void setMessageSource(MessageSource msgSource, ServletContext servletContext) {
		servletContext.setAttribute(MESSAGESOURCE_SERVLETCONTEXT_KEY, msgSource);
	}

	/**
	 * Try to resolve the message. Treat as an error if the message can't be found.
	 * 
	 * @param request HttpServletRequest object
	 * @param key the code to lookup up, such as 'calculator.noRateSet'
	 * @return The resolved message
	 */
	public static String getMessage(HttpServletRequest request, String key) {
		return HDIVUtil.getMessage(request, key, null);
	}

	/**
	 * Try to resolve the message. Treat as an error if the message can't be found.
	 * 
	 * @param request HttpServletRequest object
	 * @param key the code to lookup up, such as 'calculator.noRateSet'
	 * @param o Array of arguments that will be filled in for params within the message (params look like "{0}",
	 * "{1,date}", "{2,time}" within a message), or null if none.
	 * @return The resolved message
	 */
	public static String getMessage(HttpServletRequest request, String key, String o) {
		return HDIVUtil.getMessage(request, key, o, Locale.getDefault());
	}

	/**
	 * Try to resolve the message. Treat as an error if the message can't be found.
	 * 
	 * @param request HttpServletRequest object
	 * @param key the code to lookup up, such as 'calculator.noRateSet'
	 * @param o Array of arguments that will be filled in for params within the message (params look like "{0}",
	 * "{1,date}", "{2,time}" within a message), or null if none.
	 * @param userLocale locale
	 * @return The resolved message
	 */
	public static String getMessage(HttpServletRequest request, String key, String o, Locale userLocale) {

		String resolvedMessage = HDIVUtil.getMessageSource(request).getMessage(key, new String[] { o }, userLocale);
		if (log.isDebugEnabled()) {
			log.debug(resolvedMessage);
		}
		return resolvedMessage;
	}

	/**
	 * Generates a random number between 0 (inclusive) and n (exclusive).
	 * 
	 * @param n the bound on the random number to be returned. Must be positive.
	 * @return Returns a pseudorandom, uniformly distributed int value between 0 (inclusive) and <code>n</code>
	 * (exclusive).
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

	/**
	 * Strips a servlet session ID from <tt>url</tt>. The session ID is encoded as a URL "path parameter" beginning with
	 * "jsessionid=". We thus remove anything we find between ";jsessionid=" (inclusive) and either EOS or a subsequent
	 * ';' (exclusive).
	 * 
	 * @param url url
	 * @return url without sessionId
	 */
	public static String stripSession(String url) {

		if (log.isDebugEnabled()) {
			log.debug("Stripping jsessionid from url " + url);
		}
		StringBuffer u = new StringBuffer(url);
		int sessionStart;

		while (((sessionStart = u.toString().indexOf(";jsessionid=")) != -1)
				|| ((sessionStart = u.toString().indexOf(";JSESSIONID=")) != -1)) {

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
	 * Return an appropriate request object of the specified type, if available, unwrapping the given request as far as
	 * necessary.
	 * 
	 * @param request the servlet request to introspect
	 * @param requiredType the desired type of request object
	 * @return the matching request object, or {@code null} if none of that type is available
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getNativeRequest(ServletRequest request, Class<T> requiredType) {
		if (requiredType != null) {
			if (requiredType.isInstance(request)) {
				return (T) request;
			}
			else if (request instanceof ServletRequestWrapper) {
				return getNativeRequest(((ServletRequestWrapper) request).getRequest(), requiredType);
			}
		}
		return null;
	}

	/**
	 * Find a unique {@code WebApplicationContext} for this web app: either the root web app context (preferred) or a
	 * unique {@code WebApplicationContext} among the registered {@code ServletContext} attributes (typically coming
	 * from a single {@code DispatcherServlet} in the current web application).
	 * <p>
	 * Note that {@code DispatcherServlet}'s exposure of its context can be controlled through its
	 * {@code publishContext} property, which is {@code true} by default but can be selectively switched to only publish
	 * a single context despite multiple {@code DispatcherServlet} registrations in the web app.
	 * 
	 * @param sc ServletContext to find the web application context for
	 * @return the desired WebApplicationContext for this web app, or {@code null} if none
	 * @since 4.2
	 * @see #getWebApplicationContext(ServletContext)
	 * @see ServletContext#getAttributeNames()
	 */
	public static WebApplicationContext findWebApplicationContext(ServletContext sc) {
		WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(sc);
		if (wac == null) {
			Enumeration<String> attrNames = sc.getAttributeNames();
			while (attrNames.hasMoreElements()) {
				String attrName = attrNames.nextElement();
				Object attrValue = sc.getAttribute(attrName);
				if (attrValue instanceof WebApplicationContext) {
					if (wac != null) {
						throw new IllegalStateException("No unique WebApplicationContext found: more than one "
								+ "DispatcherServlet registered with publishContext=true?");
					}
					wac = (WebApplicationContext) attrValue;
				}
			}
		}
		if (wac == null) {
			throw new IllegalStateException("No WebApplicationContext found: no ContextLoaderListener registered?");
		}
		return wac;
	}

}