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
package org.hdiv.util;

/**
 * Code Errors used by HDIV validation process.
 * 
 * @author Gorka Vicente
 */
public class HDIVErrorCodes {

	/**
	 * The action or url received in the request does not match the one expected.
	 */
	public static final String ACTION_ERROR = "INVALID_ACTION";

	/**
	 * The parameter received in the request does not exist in the request state.
	 */
	public static final String PARAMETER_NOT_EXISTS = "INVALID_PARAMETER_NAME";

	/**
	 * For a certain parameter, the expected number of values have not been received.
	 */
	public static final String REQUIRED_PARAMETERS = "NOT_RECEIVED_ALL_REQUIRED_PARAMETERS";

	/**
	 * Incorrect parameter value.
	 */
	public static final String PARAMETER_VALUE_INCORRECT = "INVALID_PARAMETER_VALUE";

	/**
	 * For a certain parameter not the expected number of values has been received.
	 */
	public static final String VALUE_LENGTH_INCORRECT = "NOT_RECEIVED_ALL_PARAMETER_VALUES";

	/**
	 * Repeated values have been received for the same parameter.
	 */
	public static final String REPEATED_VALUES = "REPEATED_VALUES_FOR_PARAMETER";

	/**
	 * Incorrect parameter value received if confidentiality is activated.
	 */
	public static final String CONFIDENTIAL_VALUE_INCORRECT = "INVALID_CONFIDENTIAL_VALUE";

	/**
	 * The HDIV parameter has not been received in the request.
	 */
	public static final String HDIV_PARAMETER_DOES_NOT_EXIST = "HDIV_PARAMETER_DOES_NOT_EXIST";

	/**
	 * The HDIV parameter has an incorrect value.
	 */
	public static final String HDIV_PARAMETER_INCORRECT_VALUE = "INVALID_HDIV_PARAMETER_VALUE";

	/**
	 * The HDIV parameter has an incorrect page identifier.
	 */
	public static final String PAGE_ID_INCORRECT = "INVALID_PAGE_ID";

	/**
	 * Error in the editable parameter validation.
	 * 
	 * @since HDIV 1.1
	 */
	public static final String EDITABLE_VALIDATION_ERROR = "INVALID_EDITABLE_VALUE";

	/**
	 * The cookie received in the request has an incorrect value.
	 * 
	 * @since HDIV 1.1
	 */
	public static final String COOKIE_INCORRECT = "INVALID_COOKIE";

	/**
	 * Internal error created in HDIV during request processing.
	 * 
	 * @since HDIV 2.1.8
	 */
	public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
}