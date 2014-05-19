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
package org.hdiv.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Methods for request scope data and wrapper initialization. Used in {@link ValidatorFilter}.
 * 
 * @author Gotzon Illarramendi
 * @since 2.1.5
 */
public interface RequestInitializer {

	/**
	 * Initialize request scoped data
	 * 
	 * @param request
	 *            request object
	 */
	void initRequest(HttpServletRequest request);

	/**
	 * Destroy request scoped data
	 * 
	 * @param request
	 *            request object
	 */
	void endRequest(HttpServletRequest request);

	/**
	 * Create request wrapper.
	 * 
	 * @param request
	 *            HTTP request
	 * @return the request wrapper
	 */
	RequestWrapper createRequestWrapper(HttpServletRequest request);

	/**
	 * Create response wrapper.
	 * 
	 * @param response
	 *            HTTP response
	 * @return the response wrapper
	 */
	ResponseWrapper createResponseWrapper(HttpServletResponse response);
}
