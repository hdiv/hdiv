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
package org.hdiv.init;

import javax.servlet.http.HttpSession;

/**
 * Initializes and destroys {@link HttpSession} scoped attributes.
 * 
 * @since 2.1.10
 */
public interface SessionInitializer {

	/**
	 * Initialize {@link HttpSession} scoped attributes.
	 * 
	 * @param session {@link HttpSession} instance
	 */
	void initializeSession(HttpSession session);

	/**
	 * Clean {@link HttpSession} scoped attributes.
	 * 
	 * @param session {@link HttpSession} instance
	 */
	void destroySession(HttpSession session);
}
