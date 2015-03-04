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
package org.hdiv.validator;

/**
 * Interface for editable data validations.
 * 
 * @author Gorka Vicente
 * @since HDIV 1.1
 */
public interface IValidation {

	/**
	 * Returns the name of the validation rule.
	 * 
	 * @return Rules name.
	 * @since HDIV 2.1.10
	 */
	String getName();

	/**
	 * <p>
	 * Checks if the values <code>values</code> are valid for the editable parameter <code>parameter</code>.
	 * </p>
	 * There are two types of validations:
	 * <ul>
	 * <li>accepted: the value is valid only if it passes the validation</li>
	 * <li>rejected: the value is rejected if doesn't pass the validation</li>
	 * </ul>
	 * 
	 * @param parameter
	 *            parameter name
	 * @param values
	 *            parameter's values
	 * @param dataType
	 *            editable data type
	 * @return True if the values <code>values</code> are valid for the parameter <code>parameter</code>.
	 * @since HDIV 1.1.1
	 */
	boolean validate(String parameter, String[] values, String dataType);
}
