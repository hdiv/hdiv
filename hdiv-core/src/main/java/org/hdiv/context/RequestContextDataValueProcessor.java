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
package org.hdiv.context;

import java.util.Map;

public interface RequestContextDataValueProcessor {

	/**
	 * Invoked when a new form action is rendered.
	 * @param request the current request
	 * @param action the form action
	 * @param httpMethod the form HTTP method
	 * @return the action to use, possibly modified
	 */
	String processAction(RequestContextHolder request, String action, String httpMethod);

	/**
	 * Invoked when a form field value is rendered.
	 * @param request the current request
	 * @param name the form field name
	 * @param value the form field value
	 * @param type the form field type ("text", "hidden", etc.)
	 * @return the form field value to use, possibly modified
	 */
	String processFormFieldValue(RequestContextHolder request, String name, String value, String type);

	/**
	 * Invoked after all form fields have been rendered.
	 * @param request the current request
	 * @return additional hidden form fields to be added, or {@code null}
	 */
	Map<String, String> getExtraHiddenFields(RequestContextHolder request);

	/**
	 * Invoked when a URL is about to be rendered or redirected to.
	 * @param request the current request
	 * @param url the URL value
	 * @return the URL to use, possibly modified
	 */
	String processUrl(RequestContextHolder request, String url);

}
