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
package org.hdiv.state;

import java.io.Serializable;
import java.util.List;

public interface IParameter extends Serializable {

	/**
	 * Adds the value <code>value</code> to the parameter <code>this</code>.
	 *
	 * @param value New value
	 */
	void addValue(String value);

	/**
	 * Checks if parameter has <code>value</code>.
	 *
	 * @param value Value
	 *
	 * @return True if <code>value</code> exists in the array of values <code>values</code>. False otherwise.
	 */
	boolean existValue(String value);

	/**
	 * Checks if the position <code>position</code> exists in the array of values <code>values</code>.
	 *
	 * @param position Position
	 *
	 * @return True if <code>position</code> is valid position in the array of values <code>values</code>. False otherwise.
	 */
	boolean existPosition(int position);

	/**
	 * @param position Position index
	 * @return Obtains the value of the position <code>position</code> in the list of values of the parameter.
	 */
	String getValuePosition(int position);

	/**
	 * @return Returns the parameter name.
	 */
	String getName();

	/**
	 * @return Returns the values of parameter.
	 */
	List<String> getValues();

	/**
	 * @return Returns if parameter <code>this</code> is editable or not.
	 */
	boolean isEditable();

	/**
	 * @param editable Modify the editable value of the parameter
	 * @since 2.1.8
	 */
	void setEditable(boolean editable);

	/**
	 * @return Returns confidential value
	 */
	String getConfidentialValue();

	/**
	 * Indicates if the parameter has been added to the action attribute of a link or form.
	 *
	 * @return True if the parameter has been added to the action attribute of a link or form. False in otherwise.
	 */
	boolean isActionParam();

	/**
	 * Indicates if the parameter is required
	 *
	 * @return True if the parameter is required
	 */
	boolean isRequired();

	/**
	 * @return Returns the editable data type.
	 */
	String getEditableDataType();

}
