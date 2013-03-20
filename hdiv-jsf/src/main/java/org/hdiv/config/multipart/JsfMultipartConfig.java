/**
 * Copyright 2005-2013 hdiv.org
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

import org.hdiv.config.multipart.exception.HdivMultipartException;
import org.hdiv.filter.RequestWrapper;

/**
 * Class containing multipart request configuration for JSF. Do nothing.
 * 
 * @author Gotzon Illarramendi
 * @since HDIV 2.1.4
 */
public class JsfMultipartConfig implements IMultipartConfig {

	/**
	 * Parses the input stream and partitions the parsed items into a set of form fields and a set of file items.
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

		return request;
	}

	/**
	 * Cleanup any resources used for the multipart handling, like a storage for the uploaded files.
	 * 
	 * @param request
	 *            the request to cleanup resources for
	 */
	public void cleanupMultipart(HttpServletRequest request) {

	}

}