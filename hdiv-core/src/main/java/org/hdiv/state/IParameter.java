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
package org.hdiv.state;

import java.util.List;

public interface IParameter {

	/**
	 * Adds the value <code>value</code> to the parameter <code>this</code>.
	 * 
	 * @param value
	 *            New value
	 */
	public void addValue(String value);

	/**
	 * Checks if parameter has <code>value</code>.
	 * 
	 * @param value
	 *            Value
	 * 
	 * @return True if <code>value</code> exists in the array of values <code>values</code>. False otherwise.
	 */
	public boolean existValue(String value);

	/**
	 * Checks if the position <code>position</code> exists in the array of values <code>values</code>.
	 * 
	 * @param position
	 *            Position
	 * 
	 * @return True if <code>position</code> is valid position in the array of values <code>values</code>. False
	 *         otherwise.
	 */
	public boolean existPosition(int position);

	/**
	 * @param position
	 *            Position index
	 * @return Obtains the value of the position <code>position</code> in the list of values of the parameter.
	 */
	public String getValuePosition(int position);

	/**
	 * @return Returns the parameter name.
	 */
	public String getName();

	/**
	 * @return Returns the values of parameter.
	 */
	public List<String> getValues();

	/**
	 * @return Returns if parameter <code>this</code> is editable or not.
	 */
	public boolean isEditable();

	/**
	 * @param editable
	 *            Modify the editable value of the parameter
	 * @since 2.1.8
	 */
	public void setEditable(boolean editable);

	/**
	 * @return Returns confidential value
	 */
	public String getConfidentialValue();

	/**
	 * Indicates if the parameter has been added to the action attribute of a link or form.
	 * 
	 * @return True if the parameter has been added to the action attribute of a link or form. False in otherwise.
	 */
	public boolean isActionParam();

	/**
	 * @return Returns the editable data type.
	 */
	public String getEditableDataType();

}
