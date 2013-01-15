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

import javax.servlet.http.HttpServletRequest;

import org.hdiv.exception.HDIVException;

/**
 * Interface to validate a client request.
 * 
 * @author Roberto Velasco
 * @author Gorka Vicente
 */
public interface IValidationHelper {

	/**
	 * Initialization of the objects needed for the validation process.
	 * 
	 * @throws HDIVException
	 *             if there is an initialization error.
	 */
	public void init();

	/**
	 * Checks if the values of the parameters received in the request <code>request</code> are valid. These values are
	 * valid if and only if the noneditable parameters haven�t been modified.<br>
	 * 
	 * @param request
	 *            HTTP servlet request
	 * @return {@link ValidatorHelperResult} with true value If all the parameter values of the request
	 *         <code>request</code> pass the the HDIV validation. False, otherwise.
	 * @throws HDIVException
	 *             If the request doesn't pass the HDIV validation an exception is thrown explaining the cause of the
	 *             error.
	 */
	public ValidatorHelperResult validate(HttpServletRequest request);

	/**
	 * It is called in the pre-processing stage of each user request.
	 * 
	 * @deprecated DataComposer startPage moved to InitListener
	 */
	public void startPage(HttpServletRequest request);

	/**
	 * It is called in the post-processing stage of each user request.
	 * 
	 * @deprecated DataComposer endPage moved to InitListener
	 */
	public void endPage(HttpServletRequest request);

}
