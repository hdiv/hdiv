/**
 * Copyright 2005-2010 hdiv.org
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

package org.hdiv.web.servlet.view;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.util.HDIVUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.view.RedirectView;

/**
 * <p>View that redirects to an absolute, context relative, or current request
 * relative URL. By default all primitive model attributes (or collections
 * thereof) are exposed as HTTP query parameters, but this behavior can be changed
 * by overriding the {@link #isEligibleProperty(String, Object)} method.
 * 
 * <p>A URL for this view is supposed to be a HTTP redirect URL, i.e.
 * suitable for HttpServletResponse's <code>sendRedirect</code> method, which
 * is what actually does the redirect if the HTTP 1.0 flag is on, or via sending
 * back an HTTP 303 code - if the HTTP 1.0 compatibility flag is off.
 *
 * <p>Note that while the default value for the "contextRelative" flag is off,
 * you will probably want to almost always set it to true. With the flag off,
 * URLs starting with "/" are considered relative to the web server root, while
 * with the flag on, they are considered relative to the web application root.
 * Since most web applications will never know or care what their context path
 * actually is, they are much better off setting this flag to true, and submitting
 * paths which are to be considered relative to the web application root.
 *
 * <p><b>NOTE when using this redirect view in a Portlet environment:</b> Make sure
 * that your controller respects the Portlet <code>sendRedirect</code> constraints.
 * When e.g. using {@link org.springframework.web.portlet.mvc.SimpleFormController},
 * make sure to set your controller's
 * {@link org.springframework.web.portlet.mvc.AbstractFormController#setRedirectAction "redirectAction"}
 * property to "true", in order to make the controller base class behave accordingly.
 * 
 * @author Gorka Vicente
 * @since HDIV 2.0.6
 * @see #setHttp10Compatible
 * @see javax.servlet.http.HttpServletResponse#sendRedirect
 */
public class RedirectViewHDIV extends RedirectView {

	private HttpStatus statusCode;
	
	/**
	 * Constructor for use as a bean.
	 */
	public RedirectViewHDIV() {
		super();
	}

	/**
	 * Create a new RedirectView with the given URL.
	 * <p>
	 * The given URL will be considered as relative to the web server, not as
	 * relative to the current ServletContext.
	 * 
	 * @param url the URL to redirect to
	 * @see #RedirectView(String, boolean)
	 */
	public RedirectViewHDIV(String url) {
		super(url);
	}

	/**
	 * Create a new RedirectView with the given URL.
	 * 
	 * @param url the URL to redirect to
	 * @param contextRelative whether to interpret the given URL as relative to
	 *            the current ServletContext
	 */
	public RedirectViewHDIV(String url, boolean contextRelative) {
		super(url, contextRelative);
	}

	/**
	 * Create a new RedirectView with the given URL.
	 * 
	 * @param url the URL to redirect to
	 * @param contextRelative whether to interpret the given URL as relative to
	 *            the current ServletContext
	 * @param http10Compatible whether to stay compatible with HTTP 1.0 clients
	 */
	public RedirectViewHDIV(String url, boolean contextRelative, boolean http10Compatible) {

		super(url, contextRelative, http10Compatible);
	}

	/**
	 * Create a new RedirectView with the given URL.
	 * @param url the URL to redirect to
	 * @param contextRelative whether to interpret the given URL as
	 * relative to the current ServletContext
	 * @param http10Compatible whether to stay compatible with HTTP 1.0 clients
	 * @param exposeModelAttributes whether or not model attributes should be
	 * exposed as query parameters
	 */
	public RedirectViewHDIV(String url, boolean contextRelative, boolean http10Compatible, boolean exposeModelAttributes) {
		super(url, contextRelative, http10Compatible, exposeModelAttributes);
	}

	/**
	 * Send a redirect back to the HTTP client and adds HDIV state as a
	 * parameter if <code>targetUrl</code> references our application.
	 * 
	 * @param request current HTTP request (allows for reacting to request
	 *            method)
	 * @param response current HTTP response (for sending response headers)
	 * @param targetUrl the target URL to redirect to
	 * @param http10Compatible whether to stay compatible with HTTP 1.0 clients
	 * @throws IOException if thrown by response methods
	 */
	@Override
	protected void sendRedirect(HttpServletRequest request, HttpServletResponse response, String targetUrl,
			boolean http10Compatible) throws IOException {

		if (request.getSession(false) != null) {
			LinkUrlProcessor linkUrlProcessor = HDIVUtil.getLinkUrlProcessor(request.getSession().getServletContext());
			targetUrl = linkUrlProcessor.processUrl(request, targetUrl);
		}

		// simply use super to retain the latest of whatever spring version is used (hence the removal of the commented getHttp11StatusCode which is only in 3.x)
		// this was a 'sendRedirect' method as per 2.5.x branch - https://src.springframework.org/svn/spring-maintenance/trunk/src/org/springframework/web/servlet/view/RedirectView.java
		// where the version of that repo is indicated by https://src.springframework.org/svn/spring-maintenance/trunk/changelog.txt
		super.sendRedirect(request, response, targetUrl, http10Compatible);
	}

}
