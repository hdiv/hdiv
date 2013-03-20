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
package org.hdiv.logs;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ClassUtils;

/**
 * Default implementation of {@link IUserData} interface
 * 
 * @author Roberto Velasco
 * @author Gotzon Illarramendi
 */
public class UserData implements IUserData {

	private final boolean springSecurityPresent = ClassUtils.isPresent(
			"org.springframework.security.core.context.SecurityContextHolder", UserData.class.getClassLoader());

	public String getUsername(HttpServletRequest request) {

		// Find username in JEE standard security
		Principal principal = request.getUserPrincipal();
		if (principal != null && principal.getName() != null) {
			return principal.getName();
		}

		// Find username in Spring Security
		if (springSecurityPresent) {
			SecurityContext securityContext = SecurityContextHolder.getContext();
			if (securityContext != null && securityContext.getAuthentication() != null) {
				return securityContext.getAuthentication().getName();
			}
		}

		// Return anonymous
		return IUserData.ANONYMOUS;
	}

}
