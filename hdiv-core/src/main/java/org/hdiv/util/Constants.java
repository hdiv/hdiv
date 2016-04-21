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

import org.hdiv.filter.ValidatorHelperResult;
import org.hdiv.idGenerator.PageIdGenerator;
import org.hdiv.session.IStateCache;

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
	 * The request attributes key under HDIV should store errors produced in the editable fields.
	 */
	public static final String EDITABLE_PARAMETER_ERROR = "org.hdiv.action.EDITABLE_PARAMETER_ERROR";

	/**
	 * The request attributes key under HDIV should store data about errors produced in the editable fields.
	 */
	public static final String EDITABLE_PARAMETER_ERROR_DATA = "org.hdiv.action.EDITABLE_PARAMETER_ERROR_DATA";

	/**
	 * Name of the attribute which is used for storing cookies in session.
	 */
	public static final String HDIV_COOKIES_KEY = "org.hdiv.HdivCookies";

	/**
	 * Request attribute name that contains validation result {@link ValidatorHelperResult}
	 * 
	 * @since 2.1.5
	 */
	public static final String VALIDATOR_HELPER_RESULT_NAME = "org.hdiv.ValidatorHelperResult";

	/**
	 * Session's cookie identifier
	 */
	public static final String JSESSIONID = "JSESSIONID";

	/**
	 * Session's cookie identifier in lower case.
	 */
	public static final String JSESSIONID_LC = JSESSIONID.toLowerCase();

	/**
	 * Session attribute name for {@link PageIdGenerator} instance
	 */
	public static final String PAGE_ID_GENERATOR_NAME = "org.hdiv.PageIdGenerator";

	/**
	 * Session attribute name for {@link IStateCache} instance
	 */
	public static final String STATE_CACHE_NAME = "org.hdiv.StateCache";

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

	/**
	 * Location of the internal resources files
	 */
	public static final String MESSAGE_SOURCE_PATH = "org.hdiv.msg.MessageResources";

	/**
	 * Request attribute name that contains if request is an ajax request
	 * 
	 * @since 2.1.13
	 */
	public static final String AJAX_REQUEST = "org.hdiv.ajaxrequest";

}