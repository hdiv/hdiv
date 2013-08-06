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
package org.hdiv.validator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Editable data (text/textarea) validation definition.
 * 
 * @author Gorka Vicente
 * @since HDIV 1.1
 */
public class Validation implements IValidation {

	/**
	 * Regular expression that values received in the parameter must fit.
	 */
	protected Pattern acceptedPattern;

	/**
	 * Regular expression that values received in the parameter can't fit.
	 */
	protected Pattern rejectedPattern;

	/**
	 * Set with the parameters to be ignored in the validation process of the parameters.
	 */
	private Set<String> ignoreParameters;

	/**
	 * Component type to which apply the validation <code>this</code>.
	 */
	private String componentType;

	public String getComponentType() {
		return componentType;
	}

	/**
	 * Initialize the list of ignore parameters defined for a URL.
	 * 
	 * @param ignoreParameters
	 *            list of ignore parameters
	 */
	public void setIgnoreParameters(List<String> ignoreParameters) {

		this.ignoreParameters = new HashSet<String>(ignoreParameters);

	}

	/**
	 * Checks if there are editable parameters that must be ignored in the validation process.
	 * 
	 * @return True if there are editable parameters that must be ignored in the validation process. False otherwise.
	 */
	public boolean existIgnoreParameters() {

		return (this.ignoreParameters != null) && (this.ignoreParameters.size() > 0);
	}

	/**
	 * Checks if <code>parameter</code> is a parameter that must be ignored during the validation process of the
	 * editable parameters.
	 * 
	 * @param parameter
	 *            parameter name
	 * @return True if <code>parameter</code> doesn't need to be validated. False otherwise.
	 */
	public boolean isIgnoreParameter(String parameter) {

		return this.ignoreParameters.contains(parameter);
	}

	/**
	 * Checks if a component type has been defined to which apply the validation <code>this</code>.
	 * 
	 * @return True if the component type to which apply de validation has been defined. False otherwise.
	 */
	public boolean existComponentType() {

		return (this.componentType != null);
	}

	/**
	 * Checks if the type <code>parameterType</code> is the same as the one defined in the validation <code>this</code>.
	 * 
	 * @param parameterType
	 *            Component type
	 * @return True if the validation <code>this</code> is the same as <code>parameterType</code>.
	 */
	public boolean isTheSameComponentType(String parameterType) {

		if (parameterType.equals("password")) {
			return this.componentType.equalsIgnoreCase("text");
		}
		return this.componentType.equalsIgnoreCase(parameterType);
	}

	/**
	 * <p>
	 * Checks if the values <code>values</code> are valid for the editable parameter <code>parameter</code>.
	 * </p>
	 * <p>
	 * There are two types of validations:
	 * <li>accepted: the value is valid only if it passes the validation</li>
	 * <li>rejected: the value is rejected if doesn't pass the validation</li>
	 * </p>
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
	public boolean validate(String parameter, String[] values, String dataType) {

		Matcher m = null;

		// we check if the component type we apply the validation to is
		// the same as the parameter's component type.
		if (this.existComponentType() && (!this.isTheSameComponentType(dataType))) {
			return true;
		}

		// we check if parameter must be ignored
		if (this.existIgnoreParameters() && (this.isIgnoreParameter(parameter))) {
			return true;
		}

		// we validate all the values for the parameter
		for (int j = 0; j < values.length; j++) {

			if (this.acceptedPattern != null) {

				m = this.acceptedPattern.matcher(values[j]);

				if (!m.matches()) {
					return false;
				}
			}

			if (this.rejectedPattern != null) {

				m = this.rejectedPattern.matcher(values[j]);

				if (m.matches()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @param componentType
	 *            The comkponent type to set.
	 */
	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}

	/**
	 * @param acceptedPattern
	 *            The accepted pattern to set.
	 */
	public void setAcceptedPattern(String acceptedPattern) {
		this.acceptedPattern = Pattern.compile(acceptedPattern);
	}

	/**
	 * @param rejectedPattern
	 *            The rejected pattern to set.
	 */
	public void setRejectedPattern(String rejectedPattern) {
		this.rejectedPattern = Pattern.compile(rejectedPattern);
	}

	public String toString() {
		StringBuffer result = new StringBuffer().append("");
		result = result.append(" componentType=").append(this.getComponentType());
		result = result.append(" acceptedPattern=").append(
				this.acceptedPattern == null ? "" : this.acceptedPattern.toString());
		result = result.append(" rejectedPattern=").append(
				this.rejectedPattern == null ? "" : this.rejectedPattern.toString());
		return result.toString();

	}

}
