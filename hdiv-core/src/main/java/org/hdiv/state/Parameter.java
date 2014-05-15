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
package org.hdiv.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data structure to store all the values of a parameter
 * 
 * @author Roberto Velasco
 */
public class Parameter implements IParameter, Serializable {

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that a loaded class corresponds exactly
	 * to a serialized object.
	 */
	private static final long serialVersionUID = 1390866699507616631L;

	private static final int VALUES_LIST_SIZE = 3;

	public Parameter(String name, String[] values, boolean editable, String editableDataType, boolean actionParam) {

		this(name, values[0], editable, editableDataType, actionParam);

		if (values.length > 1) {
			for (int i = 1; i < values.length; i++) {
				this.addValue(values[i]);
			}
		}
	}

	public Parameter(String name, String value, boolean editable, String editableDataType, boolean actionParam) {
		this.name = name;
		this.value = value;
		this.editable = editable;
		this.editableDataType = editableDataType;
		this.actionParam = actionParam;
	}

	/**
	 * parameter name
	 */
	private String name;

	private String value;

	/**
	 * List of values for parameter <code>this</code>
	 */
	private List<String> values;

	/**
	 * Indicates if the parameter <code>this</code> is editable or not.
	 * <p>
	 * A parameter is editable when the user can modify the value or values returned by the server, and it is
	 * noneditable when the data returned by the server cannot be modified by the user under no circumstance.
	 * </p>
	 */
	private boolean editable;

	/**
	 * Parameter type. Only for editable parameters.
	 */
	private String editableDataType;

	/**
	 * Indicates if <code>this</code> is a parameter added in the action atribute of a link or form. If it is
	 * <code>actionParam</code> ALL the values of this parameter must arrived within the request. If not, it means that
	 * the user has modified the request on purpose.
	 */
	private boolean actionParam;

	/**
	 * Adds the value <code>value</code> to the parameter <code>this</code>.
	 */
	public void addValue(String value) {
		if (this.values == null) {
			this.values = new ArrayList<String>(VALUES_LIST_SIZE);
			this.values.add(this.value);
		}

		this.values.add(value);
	}

	/**
	 * Checks if parameter has <code>value</code>.
	 * 
	 * @return True if <code>value</code> exists in the array of values <code>values</code>. False otherwise.
	 */
	public boolean existValue(String value) {
		if (this.values == null) {
			return this.value.equalsIgnoreCase(value);
		}

		for (int i = 0; i < this.values.size(); i++) {
			String tempValue = values.get(i);
			if (tempValue.equalsIgnoreCase(value)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Checks if the position <code>position</code> exists in the array of values <code>values</code>.
	 * 
	 * @return True if <code>position</code> is valid position in the array of values <code>values</code>. False
	 *         otherwise.
	 */
	public boolean existPosition(int position) {
		return (position == 0) || (values != null && position < values.size());
	}

	/**
	 * @return Obtains the value of the position <code>position</code> in the list of values of the parameter.
	 */
	public String getValuePosition(int position) {
		return (position == 0 ? this.value : this.values.get(position));
	}

	/**
	 * @return Returns the parameter name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Returns the values of parameter <code>this</code>.
	 */
	public List<String> getValues() {
		return (this.values == null ? Collections.singletonList(value) : this.values);
	}

	/**
	 * @return Returns if parameter <code>this</code> is editbale or not.
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * @return Returns confidential value.
	 */
	public String getConfidentialValue() {
		if (this.values == null) {
			return "0";
		}

		return Integer.toString(this.values.size() - 1);
	}

	/**
	 * Indicates if the parameter has been added to the action attribute of a link or form.
	 * 
	 * @return True if the parameter has been added to the action attribute of a link or form. False in otherwise.
	 */
	public boolean isActionParam() {
		return actionParam;
	}

	/**
	 * @param actionParam
	 *            The actionParam to set.
	 */
	public void setActionParam(boolean actionParam) {
		this.actionParam = actionParam;
	}

	/**
	 * @return Returns the editable data type.
	 */
	public String getEditableDataType() {
		return editableDataType;
	}

	public String toString() {

		StringBuffer result = new StringBuffer();
		result.append(" Parameter:" + this.getName() + " Values:");

		if (values == null) {
			result.append(this.value);
		} else {
			for (int i = 0; i < this.values.size(); i++) {
				String value = this.values.get(i);
				result.append(value);
				if (!(i + 1 == this.values.size())) {
					result.append(",");
				}
			}
		}

		return result.toString();
	}

}
