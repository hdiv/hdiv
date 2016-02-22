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
package org.hdiv.config.multipart;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.multipart.exception.HdivMultipartException;
import org.hdiv.filter.RequestWrapper;
import org.hdiv.web.multipart.HdivCommonsMultipartResolver;
import org.hdiv.web.multipart.HdivStandardServletMultipartResolver;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FrameworkServlet;
import org.springframework.web.util.WebUtils;

/**
 * Class containing multipart request configuration for Spring MVC.
 * 
 * @author Gorka Vicente
 * @author Gotzon Illarramendi
 * @since HDIV 2.1.0
 */
public class SpringMVCMultipartConfig implements IMultipartConfig {

	private static final Log log = LogFactory.getLog(SpringMVCMultipartConfig.class);

	private static final String MULTIPART_RESOLVER_BEAN_NAME = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME;

	private WebApplicationContext webApplicationContext;

	/**
	 * Parses the input stream and partitions the parsed items into a set of form fields and a set of file items.
	 * 
	 * @param request The multipart request wrapper.
	 * @param servletContext Our ServletContext object
	 * @throws HdivMultipartException if an unrecoverable error occurs.
	 */
	public HttpServletRequest handleMultipartRequest(RequestWrapper request, ServletContext servletContext)
			throws HdivMultipartException {

		MultipartResolver multipartResolver = this.lookupMultipartResolver(servletContext);

		if (multipartResolver == null) {
			return request;
		}
		if (!(multipartResolver instanceof HdivCommonsMultipartResolver || multipartResolver instanceof HdivStandardServletMultipartResolver)) {
			throw new IllegalStateException(
					"In order to enable HDIV multipart processing, MultipartResolver must be of "
							+ HdivCommonsMultipartResolver.class.getName() + " or "
							+ HdivStandardServletMultipartResolver.class.getName() + " type.");
		}

		MultipartHttpServletRequest processedRequest = null;
		try {
			// Resolve multipart with the original request
			processedRequest = multipartResolver.resolveMultipart((HttpServletRequest) request.getRequest());

		}
		catch (MultipartException e) {

			throw new HdivMultipartException(e);
		}

		// Set MultipartHttpServletRequest as the child request of RequestWrapper
		request.setRequest(processedRequest);
		return request;
	}

	/**
	 * Cleanup any resources used for the multipart handling, like a storage for the uploaded files.
	 * 
	 * @param request the request to cleanup resources for
	 * @since HDIV 2.1.0
	 */
	public void cleanupMultipart(HttpServletRequest request) {
		MultipartResolver multipartResolver = lookupMultipartResolver(request.getSession().getServletContext());

		if (multipartResolver != null) {
			MultipartHttpServletRequest req = WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class);
			if (req != null) {
				multipartResolver.cleanupMultipart(req);
			}
		}

	}

	/**
	 * Obtain MultipartResolver instance for this application.
	 * 
	 * @param servletContext app ServletContext
	 * @return MultipartResolver instance
	 */
	@SuppressWarnings("rawtypes")
	protected MultipartResolver lookupMultipartResolver(ServletContext servletContext) {

		MultipartResolver resolver = null;

		if (this.webApplicationContext == null) {
			this.webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		}

		try {
			resolver = this.webApplicationContext.getBean(MULTIPART_RESOLVER_BEAN_NAME, MultipartResolver.class);
			if (resolver != null) {
				return resolver;
			}
		}
		catch (NoSuchBeanDefinitionException ex) {
			// No MultipartResolver in this context.
		}

		if (resolver == null) {
			Enumeration e = servletContext.getAttributeNames();
			while (e.hasMoreElements()) {
				String name = (String) e.nextElement();
				if (name.startsWith(FrameworkServlet.SERVLET_CONTEXT_PREFIX)) {
					this.webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext,
							name);
					break;
				}
			}
		}

		if (this.webApplicationContext != null) {
			try {
				resolver = this.webApplicationContext.getBean(MULTIPART_RESOLVER_BEAN_NAME, MultipartResolver.class);
			}
			catch (NoSuchBeanDefinitionException ex) {
				// No MultipartResolver in this context.
			}
		}

		if (log.isDebugEnabled() && resolver == null) {
			log.debug("Cant find MultipartResolver on any context.");
		}

		return resolver;
	}

}