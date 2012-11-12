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
package org.hdiv.util;

/**
 * <p>
 * Global constants.
 * </p>
 * 
 * @author Gorka Vicente
 * @since HDIV 1.1.1
 */
public class Constants {

	/**
	 * Name of the attribute which is used for storing cookies in session.
	 */
	public static final String HDIV_COOKIES_KEY = "hdivCookies";

	/**
	 * Name of the attribute which is used for storing the suffix added to the HDIV state.
	 */
	public static final String STATE_SUFFIX = "hdivStateSuffix";

	/**
	 * Session's cookie identifier
	 */
	public static final String JSESSIONID = "JSESSIONID";

	public static final String KEY_NAME = "key";

	public static final String CACHE_NAME = "cache";

	public static final String PAGE_ID_GENERATOR_NAME = "pageIdGenerator";

	public static final String HDIV_PARAMETER = "HDIVParameter";

	public static final String MODIFY_STATE_HDIV_PARAMETER = "modifyHDIVStateParameter";

	public static final String ENCODING_UTF_8 = "UTF-8";

	/**
	 * Properties key that contains the error message to be shown when the value of the editable parameter is not valid.
	 * Only used for Editable Validation errors.
	 */
	public static final String HDIV_EDITABLE_ERROR_KEY = "hdiv.editable.error";

	/**
	 * Properties key that contains the error message to be shown when the value of the editable password parameter is
	 * not valid. Only used for Editable Validation errors.
	 */
	public static final String HDIV_EDITABLE_PASSWORD_ERROR_KEY = "hdiv.editable.password.error";

}