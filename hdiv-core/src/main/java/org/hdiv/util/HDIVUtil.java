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
package org.hdiv.util;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.hdiv.application.IApplication;
import org.hdiv.config.HDIVConfig;
import org.hdiv.context.RequestContext;
import org.hdiv.context.RequestContextHolder;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.exception.HDIVException;
import org.hdiv.filter.RequestWrapper;
import org.hdiv.urlProcessor.FormUrlProcessor;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.urlProcessor.UrlData;
import org.hdiv.urlProcessor.UrlDataImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.HtmlUtils;

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
	private static final Logger log = LoggerFactory.getLogger(HDIVUtil.class);

	private static final String APPLICATION_SERVLETCONTEXT_KEY = "APPLICATION_SERVLETCONTEXT_KEY";

	private static final String HDIV_SERVLETCONTEXT_KEY = "HDIV_SERVLETCONTEXT_KEY";

	private static final String MESSAGESOURCE_SERVLETCONTEXT_KEY = "MESSAGESOURCE_SERVLETCONTEXT_KEY";

	private static final String HDIVCONFIG_SERVLETCONTEXT_KEY = "HDIVCONFIG_SERVLETCONTEXT_KEY";

	private static final String LINKURLPROCESSOR_SERVLETCONTEXT_KEY = "LINKURLPROCESSOR_SERVLETCONTEXT_KEY";

	private static final String FORMURLPROCESSOR_SERVLETCONTEXT_KEY = "FORMURLPROCESSOR_SERVLETCONTEXT_KEY";

	public static final Pattern intPattern = Pattern.compile("[0-9]+");

	private static final char[] jsessionLower = ";jsessionid=".toCharArray();

	private static final char[] jsessionUpper = ";JSESSIONID=".toCharArray();

	private static final char[] SEMICOLON = ";".toCharArray();

	private static final char[] QUESTION = "?".toCharArray();

	private static Random r = new Random();

	/**
	 * Part of HTTP content type header.
	 */
	private static final String MULTIPART = "multipart/";

	private static boolean SERVLET3 = false;

	static {
		Cookie cookie = new Cookie("foo", "var");
		try {
			cookie.isHttpOnly();
			SERVLET3 = true;
		}
		catch (Throwable e) {
			// TODO: handle exception
		}
	}

	private HDIVUtil() {

	}

	public static boolean isServlet3() {
		return SERVLET3;
	}

	/* IApplication */

	/**
	 * Returns the servlet context wrapper object.
	 *
	 * @param servletContext {@link ServletContext} instance
	 * @return IApplication object
	 */
	public static IApplication getApplication(final ServletContext servletContext) {
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
	public static void setApplication(final IApplication newApplication, final ServletContext servletContext) {
		servletContext.setAttribute(APPLICATION_SERVLETCONTEXT_KEY, newApplication);
	}

	/* HDIVConfig */

	/**
	 * Return the <code>HDIVConfig</code> object
	 *
	 * @param servletContext {@link ServletContext} instance
	 * @return {@link HDIVConfig} instance
	 */
	public static HDIVConfig getHDIVConfig(final ServletContext servletContext) {

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
	public static void setHDIVConfig(final HDIVConfig hdivConfig, final ServletContext servletContext) {
		servletContext.setAttribute(HDIVCONFIG_SERVLETCONTEXT_KEY, hdivConfig);
	}

	/* UrlProcessor */

	/**
	 * Return the <code>LinkUrlProcessor</code> instance.
	 *
	 * @param servletContext {@link ServletContext} instance
	 * @return {@link LinkUrlProcessor} instance
	 */
	public static LinkUrlProcessor getLinkUrlProcessor(final ServletContext servletContext) {
		LinkUrlProcessor urlProcessor = (LinkUrlProcessor) servletContext.getAttribute(LINKURLPROCESSOR_SERVLETCONTEXT_KEY);
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
	public static void setLinkUrlProcessor(final LinkUrlProcessor urlProcessor, final ServletContext servletContext) {
		servletContext.setAttribute(LINKURLPROCESSOR_SERVLETCONTEXT_KEY, urlProcessor);
	}

	/**
	 * Return the <code>FormUrlProcessor</code> instance.
	 *
	 * @param servletContext {@link ServletContext} instance
	 * @return {@link FormUrlProcessor} instance
	 */
	public static FormUrlProcessor getFormUrlProcessor(final ServletContext servletContext) {
		FormUrlProcessor urlProcessor = (FormUrlProcessor) servletContext.getAttribute(FORMURLPROCESSOR_SERVLETCONTEXT_KEY);
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
	public static void setFormUrlProcessor(final FormUrlProcessor urlProcessor, final ServletContext servletContext) {
		servletContext.setAttribute(FORMURLPROCESSOR_SERVLETCONTEXT_KEY, urlProcessor);
	}

	/* MessageSource */

	/**
	 * Return the {@link MessageSource} instance.
	 *
	 * @param request ServletRequest object
	 * @return {@link MessageSource} instance
	 */
	public static MessageSource getMessageSource(final HttpServletRequest request) {
		return getMessageSource(request.getSession().getServletContext());
	}

	/**
	 * Return the {@link MessageSource} instance.
	 *
	 * @param servletContext {@link ServletContext} instance
	 * @return {@link MessageSource} instance
	 */
	public static MessageSource getMessageSource(final ServletContext servletContext) {
		final MessageSource msgSource = (MessageSource) servletContext.getAttribute(MESSAGESOURCE_SERVLETCONTEXT_KEY);
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
	public static void setMessageSource(final MessageSource msgSource, final ServletContext servletContext) {
		servletContext.setAttribute(MESSAGESOURCE_SERVLETCONTEXT_KEY, msgSource);
	}

	/**
	 * Try to resolve the message. Treat as an error if the message can't be found.
	 *
	 * @param request ServletRequest object
	 * @param key the code to lookup up, such as 'calculator.noRateSet'
	 * @return The resolved message
	 */
	public static String getMessage(final HttpServletRequest request, final String key) {
		return HDIVUtil.getMessage(request, key, null);
	}

	/**
	 * Try to resolve the message. Treat as an error if the message can't be found.
	 *
	 * @param request ServletRequest object
	 * @param key the code to lookup up, such as 'calculator.noRateSet'
	 * @param o Array of arguments that will be filled in for params within the message (params look like "{0}", "{1,date}", "{2,time}"
	 * within a message), or null if none.
	 * @return The resolved message
	 */
	public static String getMessage(final HttpServletRequest request, final String key, final String o) {
		return HDIVUtil.getMessage(request, key, o, Locale.getDefault());
	}

	/**
	 * Try to resolve the message. Treat as an error if the message can't be found.
	 *
	 * @param request ServletRequest object
	 * @param key the code to lookup up, such as 'calculator.noRateSet'
	 * @param o Array of arguments that will be filled in for params within the message (params look like "{0}", "{1,date}", "{2,time}"
	 * within a message), or null if none.
	 * @param userLocale locale
	 * @return The resolved message
	 */
	public static String getMessage(final HttpServletRequest request, final String key, final String o, final Locale userLocale) {
		String resolvedMessage = HDIVUtil.getMessageSource(request).getMessage(key, new String[] { o }, userLocale);
		log.debug(resolvedMessage);
		return resolvedMessage;
	}

	@SuppressWarnings("deprecation")
	public static RequestContextHolder getRequestContext(final ServletRequest request) {
		return (RequestContextHolder) request.getAttribute(Constants.HDIV_REQUEST_CONTEXT);
	}

	public static HttpServletRequest getCurrentHttpRequest() {
		return ((ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes())
				.getRequest();
	}

	@Deprecated
	public static String getHdivState(final HttpServletRequest request) {
		return request.getParameter(getHdivStateParameterName(request));
	}

	@Deprecated
	public static void setHdivState(final HttpServletRequest request, final String value) {
		((RequestWrapper) request).addParameter(getHdivStateParameterName(request), new String[] { value });
	}

	/**
	 * Gets if target is an obfuscated URL
	 * @param target Target to check
	 * @return Parameter name
	 * @since 3.3.0
	 */
	public static boolean isObfuscatedTarget(final String target) {
		return target.indexOf(UrlData.OBFUSCATION_ROOT_PATH) != -1;
	}

	/**
	 * Generates a random number between 0 (inclusive) and n (exclusive).
	 *
	 * @param n the bound on the random number to be returned. Must be positive.
	 * @return Returns a pseudorandom, uniformly distributed int value between 0 (inclusive) and <code>n</code> (exclusive).
	 * @since HDIV 1.1
	 */
	public static String createRandomToken(final int n) {

		int i = r.nextInt(n);
		if (i == 0) {
			i = 1;
		}

		return String.valueOf(i);
	}

	/**
	 * Strips a servlet session ID from <tt>url</tt>. The session ID is encoded as a URL "path parameter" beginning with "jsessionid=". We
	 * thus remove anything we find between ";jsessionid=" (inclusive) and either EOS or a subsequent ';' (exclusive).
	 *
	 * @param url url
	 * @return url without sessionId
	 */
	public static String stripSession(final String url) {
		return stripAndFillSessionData(url, null);
	}

	public static String stripAndFillSessionData(final String url, final UrlDataImpl urldata) {
		final int pos = url.indexOf(';');
		if (pos != -1) {
			final char[] data = url.toCharArray();
			int sessionStart;
			if ((sessionStart = indexOf(data, jsessionLower, pos)) != -1 || (sessionStart = indexOf(data, jsessionUpper, pos)) != -1) {
				int sessionEnd = indexOf(data, SEMICOLON, sessionStart + 1);
				if (sessionEnd == -1) {
					sessionEnd = indexOf(data, QUESTION, sessionStart + 1);
				}
				if (sessionEnd == -1) { // still
					sessionEnd = data.length;
				}
				final int len = sessionEnd - sessionStart;
				if (len > 0) {
					if (urldata != null) {
						urldata.setjSessionId(new String(data, sessionStart + 1, len - 1));
					}
					System.arraycopy(data, sessionStart + len, data, sessionStart, data.length - sessionEnd);
					return new String(data, 0, data.length - len);
				}
			}

		}
		return url;
	}

	private static int indexOf(final char[] source, final char[] target, final int fromIndex) {

		char first = target[0];
		int max = source.length - target.length;

		for (int i = 0 + fromIndex; i <= max; i++) {
			/* Look for first character. */
			if (source[i] != first) {
				while (++i <= max && source[i] != first) {
				}
			}

			/* Found first character, now look at the rest of v2 */
			if (i <= max) {
				int j = i + 1;
				int end = j + target.length - 1;
				for (int k = 1; j < end && source[j] == target[k]; j++, k++) {
					;
				}

				if (j == end) {
					/* Found whole string. */
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Return an appropriate request object of the specified type, if available, unwrapping the given request as far as necessary.
	 *
	 * @param request the servlet request to introspect
	 * @param requiredType the desired type of request object
	 * @param <T> the type of the element
	 * @return the matching request object, or {@code null} if none of that type is available
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getNativeRequest(final ServletRequest request, final Class<T> requiredType) {
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
	 * Find a unique {@code WebApplicationContext} for this web app: either the root web app context (preferred) or a unique
	 * {@code WebApplicationContext} among the registered {@code ServletContext} attributes (typically coming from a single
	 * {@code DispatcherServlet} in the current web application).
	 * <p>
	 * Note that {@code DispatcherServlet}'s exposure of its context can be controlled through its {@code publishContext} property, which is
	 * {@code true} by default but can be selectively switched to only publish a single context despite multiple {@code DispatcherServlet}
	 * registrations in the web app.
	 *
	 * @param sc ServletContext to find the web application context for
	 * @return the desired WebApplicationContext for this web app, or {@code null} if none
	 * @see ServletContext#getAttributeNames()
	 */
	public static ApplicationContext findWebApplicationContext(final ServletContext sc) {
		WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(sc);
		ApplicationContext backupCandidate = null;
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
				else if (attrValue instanceof ApplicationContext) {
					if (attrName.equals(HDIV_SERVLETCONTEXT_KEY)) {
						backupCandidate = (ApplicationContext) attrValue;
					}
				}
			}
		}
		if (wac == null) {
			if (backupCandidate != null) {
				return backupCandidate;
			}
			throw new IllegalStateException("No WebApplicationContext found: no ContextLoaderListener registered?");
		}
		return wac;
	}

	public static void registerApplicationContext(final ApplicationContext context, final ServletContext scontext) {
		scontext.setAttribute(HDIV_SERVLETCONTEXT_KEY, context);
	}

	/**
	 * <p>
	 * Decoded <code>value</code> using input <code>charEncoding</code>.
	 * </p>
	 * <p>
	 * Removes Html Entity elements too. Like that:
	 * </p>
	 * <blockquote> &amp;#<i>Entity</i>; - <i>(Example: &amp;amp;) case sensitive</i> &amp;#<i>Decimal</i>; - <i>(Example: &amp;#68;)</i>
	 * <br>
	 * &amp;#x<i>Hex</i>; - <i>(Example: &amp;#xE5;) case insensitive</i><br>
	 * </blockquote>
	 * <p>
	 * Based on {@link HtmlUtils#htmlUnescape}.
	 * </p>
	 *
	 * @param sb builder
	 * @param value value to decode
	 * @param charEncoding character encoding
	 * @return value decoded
	 */
	public static String getDecodedValue(final StringBuilder sb, final String value, final String charEncoding) {

		if (value == null || value.length() == 0) {
			return "";
		}

		String decodedValue = null;
		try {
			decodedValue = decodeValue(sb, value, charEncoding);
		}
		catch (UnsupportedEncodingException e) {
			decodedValue = value;
		}
		catch (IllegalArgumentException e) {
			decodedValue = value;
		}

		// Remove escaped Html elements
		if (decodedValue.indexOf('&') != -1) {
			// Can contain escaped characters
			decodedValue = HtmlUtils.htmlUnescape(decodedValue);
		}

		return decodedValue;
	}

	/**
	 * Returns if <code>parameterValue</code> and <code>value</code> are the same encoded value.
	 * 
	 * @param parameterValue parameter value
	 * @param value value to compare
	 * @return <code>true</code> if they are the same value; <code>false</code> otherwise
	 * @since HDIV 3.3
	 */
	public static boolean isTheSameEncodedValue(final String parameterValue, final String value) {

		return parameterValue.replace(" ", "+").equalsIgnoreCase(value);
	}

	/**
	 * @param sb builder
	 * @param s value
	 * @param enc encoding
	 * @return decoded value
	 * @see URLDecoder#decode(String, String)
	 * @throws UnsupportedEncodingException
	 */
	public static String decodeValue(final StringBuilder sb, final String s, final String enc) throws UnsupportedEncodingException {
		sb.setLength(0);
		boolean needToChange = false;
		int numChars = s.length();
		int i = 0;

		if (enc.length() == 0) {
			throw new UnsupportedEncodingException("URLDecoder: empty string enc parameter");
		}

		char c;
		byte[] bytes = null;
		while (i < numChars) {
			c = s.charAt(i);
			switch (c) {
			case '+':
				sb.append(' ');
				i++;
				needToChange = true;
				break;
			case '%':
				/*
				 * Starting with this instance of %, process all consecutive substrings of the form %xy. Each substring %xy will yield a
				 * byte. Convert all consecutive bytes obtained this way to whatever character(s) they represent in the provided encoding.
				 */

				try {

					// (numChars-i)/3 is an upper bound for the number
					// of remaining bytes
					if (bytes == null) {
						bytes = new byte[(numChars - i) / 3];
					}
					int pos = 0;

					while (i + 2 < numChars && c == '%') {
						int v = Integer.parseInt(s.substring(i + 1, i + 3), 16);
						if (v < 0) {
							throw new IllegalArgumentException("URLDecoder: Illegal hex characters in escape (%) pattern - negative value");
						}
						bytes[pos++] = (byte) v;
						i += 3;
						if (i < numChars) {
							c = s.charAt(i);
						}
					}

					// A trailing, incomplete byte encoding such as
					// "%x" will cause an exception to be thrown

					if (i < numChars && c == '%') {
						throw new IllegalArgumentException("URLDecoder: Incomplete trailing escape (%) pattern");
					}

					sb.append(new String(bytes, 0, pos, enc));
				}
				catch (NumberFormatException e) {
					throw new IllegalArgumentException("URLDecoder: Illegal hex characters in escape (%) pattern - " + e.getMessage());
				}
				needToChange = true;
				break;
			default:
				sb.append(c);
				i++;
				break;
			}
		}

		return needToChange ? sb.toString() : s;
	}

	public static boolean isPathVariable(final String uriTemplate, final String variable) {
		return uriTemplate.indexOf('{' + variable + '}') != -1;
	}

	/**
	 * Utility method that determines whether the request contains multipart content.
	 *
	 * @param request the request
	 * @return <code>true</code> if the request is multipart. <code>false</code> otherwise.
	 */
	public static boolean isMultipartContent(final HttpServletRequest request) {
		if (!Method.POST.toString().equalsIgnoreCase(request.getMethod())) {
			return false;
		}
		String contentType = request.getContentType();
		if (contentType == null) {
			return false;
		}
		if (contentType.toLowerCase(Locale.ENGLISH).startsWith(MULTIPART)) {
			return true;
		}
		return false;
	}

	/* RequestURI */

	@Deprecated
	public static String getRequestURI(final HttpServletRequest request) {
		return getRequestContext(request).getRequestURI();
	}

	@Deprecated
	public static void setRequestURI(final String requestURI, final HttpServletRequest request) {
		getContext(request).setRequestURI(requestURI);
	}

	/* CurrentPageId */

	@Deprecated
	public static Integer getCurrentPageId(final ServletRequest request) {
		return (int) getRequestContext(request).getCurrentPageId().getLeastSignificantBits();
	}

	@Deprecated
	public static void setCurrentPageId(final Integer pageId, final ServletRequest request) {
		getContext(request).setCurrentPageId(new UUID(0, pageId));
	}

	@Deprecated
	public static String getModifyHdivStateParameterName(final HttpServletRequest request) {
		return getRequestContext(request).getHdivModifyParameterName();
	}

	@Deprecated
	public static void setModifyHdivStateParameterName(final HttpServletRequest request, final String parameterName) {
		getContext(request).setHdivModifyParameterName(parameterName);
	}

	@Deprecated
	private static RequestContext getContext(final ServletRequest request) {
		return (RequestContext) getRequestContext(request);
	}

	@Deprecated
	public static String getHdivStateParameterName(final HttpServletRequest request) {
		return getRequestContext(request).getHdivParameterName();
	}

	@Deprecated
	public static void setHdivStateParameterName(final HttpServletRequest request, final String parameterName) {
		getContext(request).setHdivParameterName(parameterName);
	}

	/* BaseURL */

	@Deprecated
	public static String getBaseURL(final ServletRequest request) {
		return getRequestContext(request).getBaseURL();
	}

	@Deprecated
	public static void setBaseURL(final String baseURL, final ServletRequest request) {
		getRequestContext(request).setBaseURL(baseURL);
	}

	@Deprecated
	public static IDataComposer getDataComposer(final ServletRequest request) {
		return getRequestContext(request).getDataComposer();
	}

	@Deprecated
	public static void setDataComposer(final IDataComposer newDataComposer, final HttpServletRequest request) {
		getRequestContext(request).setDataComposer(newDataComposer);
	}

	@Deprecated
	public static void removeDataComposer(final HttpServletRequest request) {
		setDataComposer(null, request);
	}

	/**
	 * <p>
	 * Replaces a String with another String inside a larger String, once.
	 * </p>
	 *
	 * <p>
	 * A {@code null} reference passed to this method is a no-op.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.replaceOnce(null, *, *)        = null
	 * StringUtils.replaceOnce("", *, *)          = ""
	 * StringUtils.replaceOnce("any", null, *)    = "any"
	 * StringUtils.replaceOnce("any", *, null)    = "any"
	 * StringUtils.replaceOnce("any", "", *)      = "any"
	 * StringUtils.replaceOnce("aba", "a", null)  = "aba"
	 * StringUtils.replaceOnce("aba", "a", "")    = "ba"
	 * StringUtils.replaceOnce("aba", "a", "z")   = "zba"
	 * </pre>
	 *
	 * @param text text to search and replace in, may be null
	 * @param searchString the String to search for, may be null
	 * @param replacement the String to replace with, may be null
	 * @return the text with any replacements processed, {@code null} if null String input
	 */
	public static String replaceOnce(final String text, final String searchString, final String replacement) {
		return replace(text, searchString, replacement, 1, false);
	}

	/**
	 * <p>
	 * Replaces a String with another String inside a larger String, for the first {@code max} values of the search String, case
	 * sensitively/insensisitively based on {@code ignoreCase} value.
	 * </p>
	 *
	 * <p>
	 * A {@code null} reference passed to this method is a no-op.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.replace(null, *, *, *, false)         = null
	 * StringUtils.replace("", *, *, *, false)           = ""
	 * StringUtils.replace("any", null, *, *, false)     = "any"
	 * StringUtils.replace("any", *, null, *, false)     = "any"
	 * StringUtils.replace("any", "", *, *, false)       = "any"
	 * StringUtils.replace("any", *, *, 0, false)        = "any"
	 * StringUtils.replace("abaa", "a", null, -1, false) = "abaa"
	 * StringUtils.replace("abaa", "a", "", -1, false)   = "b"
	 * StringUtils.replace("abaa", "a", "z", 0, false)   = "abaa"
	 * StringUtils.replace("abaa", "A", "z", 1, false)   = "abaa"
	 * StringUtils.replace("abaa", "A", "z", 1, true)   = "zbaa"
	 * StringUtils.replace("abAa", "a", "z", 2, true)   = "zbza"
	 * StringUtils.replace("abAa", "a", "z", -1, true)  = "zbzz"
	 * </pre>
	 *
	 * @param text text to search and replace in, may be null
	 * @param searchString the String to search for (case insensitive), may be null
	 * @param replacement the String to replace it with, may be null
	 * @param max maximum number of values to replace, or {@code -1} if no maximum
	 * @param ignoreCase if true replace is case insensitive, otherwise case sensitive
	 * @return the text with any replacements processed, {@code null} if null String input
	 */
	public static String replace(final String text, String searchString, final String replacement, int max, final boolean ignoreCase) {
		if (!StringUtils.hasLength(text) || !StringUtils.hasLength(searchString) || replacement == null || max == 0) {
			return text;
		}
		String searchText = text;
		if (ignoreCase) {
			searchText = text.toLowerCase();
			searchString = searchString.toLowerCase();
		}
		int start = 0;
		int end = searchText.indexOf(searchString, start);
		if (end == -1) {
			return text;
		}
		final int replLength = searchString.length();
		int increase = replacement.length() - replLength;
		increase = increase < 0 ? 0 : increase;
		increase *= max < 0 ? 16 : max > 64 ? 64 : max;
		final StringBuilder buf = new StringBuilder(text.length() + increase);
		while (end != -1) {
			buf.append(text.substring(start, end)).append(replacement);
			start = end + replLength;
			if (--max == 0) {
				break;
			}
			end = searchText.indexOf(searchString, start);
		}
		buf.append(text.substring(start));
		return buf.toString();
	}

	/**
	 * Gets custom client app image if it is present
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "deprecation", "restriction" })
	public static String getCustomImage(final HttpServletRequest request) {
		return "http://hdiv.org/images/" + URLEncoder.encode(new sun.misc.BASE64Encoder().encode(request.getServerName().getBytes())) + "/"
				+ URLEncoder.encode(new sun.misc.BASE64Encoder().encode(request.getContextPath().getBytes()));
	}

	public static boolean checkCustomImage(final HttpServletRequest request) {
		try {
			/**
			 * Check whether a custom error image is present for the particular client
			 */
			HttpURLConnection connection = (HttpURLConnection) new URL(getCustomImage(request)).openConnection();
			return connection.getResponseCode() == HttpStatus.ACCEPTED.value();
		}
		catch (RuntimeException e) {
			return false;
		}

		catch (Exception e) {
			// What to do? true for now
			return true;
		}
	}

	public static boolean isButtonType(final String type) {
		return type != null && ("submit".equalsIgnoreCase(type) || "button".equalsIgnoreCase(type) || "image".equalsIgnoreCase(type));
	}

}