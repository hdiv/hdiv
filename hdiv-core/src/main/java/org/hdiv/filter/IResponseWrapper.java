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
package org.hdiv.filter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Interface to provide response information for HTTP servlets.
 * 
 * @author Gorka Vicente
 * @see javax.servlet.http.HttpServletResponse
 * @since HDIV 1.1
 */
public interface IResponseWrapper extends HttpServletResponse {

	/**
	* Adds the specified cookie to the response. It can be called multiple
	* times to set more than one cookie.
	*
	* @param cookie The <CODE>Cookie</CODE> to return to the client
	* @see javax.servlet.http.HttpServletResponse#addCookie
	*/
	public void addCookie(Cookie cookie);

}
