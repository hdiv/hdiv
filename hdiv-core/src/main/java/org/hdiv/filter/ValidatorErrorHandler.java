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

import org.hdiv.util.HDIVErrorCodes;

/**
 * Process a request with validation errors.
 * 
 * @author Gotzon Illarramendi
 * @since 2.1.4
 */
public interface ValidatorErrorHandler {

	/**
	 * Process a request with validation errors.
	 * 
	 * @param request
	 *            {@link HttpServletRequest} instance
	 * @param response
	 *            {@link HttpServletResponse} instance
	 * @param errorCode
	 *            Error code from {@link HDIVErrorCodes}
	 */
	void handleValidatorError(HttpServletRequest request, HttpServletResponse response, String errorCode);
}
