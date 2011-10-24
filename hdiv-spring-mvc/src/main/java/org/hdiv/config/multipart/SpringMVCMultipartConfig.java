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
package org.hdiv.config.multipart;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.multipart.exception.HdivMultipartException;
import org.hdiv.filter.RequestWrapper;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Class containing multipart request configuration.
 * 
 * @author Gorka Vicente
 * @author Gotzon Illarramendi
 * @since HDIV 2.1.0
 */
public class SpringMVCMultipartConfig implements IMultipartConfig {

	private static Log log = LogFactory.getLog(SpringMVCMultipartConfig.class);

	private static final String MULTIPART_RESOLVER_BEAN_NAME = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME;

	/**
	 * Parses the input stream and partitions the parsed items into a set of
	 * form fields and a set of file items.
	 * 
	 * @param request
	 *            The multipart request wrapper.
	 * @param servletContext
	 *            Our ServletContext object
	 * @throws HdivMultipartException
	 *             if an unrecoverable error occurs.
	 */
	public HttpServletRequest handleMultipartRequest(RequestWrapper request, ServletContext servletContext)
			throws HdivMultipartException {

		MultipartResolver multipartResolver = lookupMultipartResolver(servletContext);

		if (multipartResolver == null) {
			return request;
		}

		MultipartHttpServletRequest processedRequest = null;
		try {
			processedRequest = multipartResolver.resolveMultipart((HttpServletRequest) request.getRequest());

		} catch (MultipartException e) {

			HdivMultipartException exc = new HdivMultipartException(e);
			throw exc;
		}

		request.setRequest(processedRequest);

		return processedRequest;

	}

	/**
	 * Cleanup any resources used for the multipart handling, like a storage for
	 * the uploaded files.
	 * 
	 * @param request
	 *            the request to cleanup resources for
	 * @since HDIV 2.1.0
	 */
	public void cleanupMultipart(HttpServletRequest request) {
		MultipartResolver multipartResolver = lookupMultipartResolver(request.getSession().getServletContext());

		if (multipartResolver != null) {
			if (request instanceof MultipartHttpServletRequest) {
				MultipartHttpServletRequest multientidadRequest = (MultipartHttpServletRequest) request;
				multipartResolver.cleanupMultipart(multientidadRequest);
			}

		}

	}

	/**
	 * Obtain MultipartResolver instance for this application.
	 * 
	 * @param servletContext
	 * @return MultipartResolver instance
	 */
	protected MultipartResolver lookupMultipartResolver(ServletContext servletContext) {

		WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		try {
			return wac.getBean(MULTIPART_RESOLVER_BEAN_NAME, MultipartResolver.class);
		} catch (NoSuchBeanDefinitionException ex) {
			// Default is no multipart resolver.
			return null;
		}
	}

}