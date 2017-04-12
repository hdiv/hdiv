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
package org.hdiv.logs;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface to get information about the user who made the request.
 * 
 * @author Roberto Velasco
 */
public interface IUserData {

	String ANONYMOUS = "anonymous";

	/**
	 * <p>
	 * Get application username to log attacks.
	 * </p>
	 * <p>
	 * If the user is anonymous, not logged in for example, return {@link IUserData#ANONYMOUS}.
	 * </p>
	 * 
	 * @param request request object
	 * @return application user name
	 */
	String getUsername(HttpServletRequest request);

	/**
	 * Get users local IP address, which can be different to the remote address if the user is behind a web proxy.
	 * @param request request object
	 * @return local IP
	 * @since 3.3.0
	 */
	String getLocalIp(HttpServletRequest request);

	/**
	 * Get users remote IP address.
	 * @param request request object
	 * @return remote IP
	 * @since 3.3.0
	 */
	String getRemoteIp(HttpServletRequest request);
}
