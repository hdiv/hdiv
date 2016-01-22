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
package org.hdiv.validator;

/**
 * Executes validations against editable fields (text and textarea).
 * 
 * @since HDIV 2.1.10
 */
public interface EditableDataValidationProvider {

	/**
	 * <p>
	 * Checks if the values <code>values</code> are valid for the editable parameter <code>parameter</code>.
	 * </p>
	 * 
	 * @param url request url
	 * @param parameter parameter name
	 * @param values parameter's values
	 * @param dataType editable data type
	 * @return True if the values <code>values</code> are valid for the parameter <code>parameter</code>.
	 */
	EditableDataValidationResult validate(String url, String parameter, String[] values, String dataType);
}
