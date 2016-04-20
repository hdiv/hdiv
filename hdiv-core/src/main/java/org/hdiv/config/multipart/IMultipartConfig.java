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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.hdiv.config.multipart.exception.HdivMultipartException;
import org.hdiv.filter.RequestWrapper;

/**
 * Class containing multipart request configuration and methods initialized from Spring Factory.
 *
 * @author Gorka Vicente
 */
public interface IMultipartConfig {

	/**
	 * This is the ServletRequest attribute that should be set when a multipart request is being read and failed. It's
	 * the job of the implementation to put this attribute in the request if multipart process failed; in the
	 * handleRequest(HttpServletRequest) method.
	 *
	 * @since HDIV 2.0.1
	 */
	String FILEUPLOAD_EXCEPTION = "org.hdiv.exception.HDIVMultipartException";

	/**
	 * Parses the input stream and partitions the parsed items into a set of form fields and a set of file items.
	 *
	 * @param request The multipart request wrapper.
	 * @param servletContext Our ServletContext object
	 * @return multipart processed request
	 * @throws HdivMultipartException if an unrecoverable error occurs.
	 */
	HttpServletRequest handleMultipartRequest(RequestWrapper request, ServletContext servletContext) throws HdivMultipartException;

	/**
	 * Cleanup any resources used for the multipart handling, like a storage for the uploaded files.
	 *
	 * @param request the request to cleanup resources for
	 * @since HDIV 2.1.0
	 */
	void cleanupMultipart(HttpServletRequest request);
}
