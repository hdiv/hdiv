/**
 * Copyright 2005-2012 hdiv.org
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
package org.hdiv.validation;

/**
 * Object that encapsulates data about an attack detected in the validation
 * process
 * 
 * @author Gotzon Illarramendi
 */
public class ValidationError {

	/**
	 * Type of attack
	 */
	private String errorKey;

	/**
	 * Component where the attack was detected
	 */
	private String errorComponent;

	/**
	 * Request parameter that has been modified
	 */
	private String errorParam;

	/**
	 * Value of the modified parameter
	 */
	private String errorValue;

	public ValidationError() {
	}

	/**
	 * Constructor with default properties
	 * 
	 * @param errorKey
	 *            error key
	 * @param errorComponent
	 *            component that has generated the error
	 * @param errorParam
	 *            parameter that has generated the error
	 * @param errorValue
	 *            value of the parameter that has generated the error
	 */
	public ValidationError(String errorKey, String errorComponent,
			String errorParam, String errorValue) {
		super();
		this.errorKey = errorKey;
		this.errorComponent = errorComponent;
		this.errorParam = errorParam;
		this.errorValue = errorValue;
	}

	public String getErrorKey() {
		return errorKey;
	}

	public void setErrorKey(String errorKey) {
		this.errorKey = errorKey;
	}

	public String getErrorComponent() {
		return errorComponent;
	}

	public void setErrorComponent(String errorComponent) {
		this.errorComponent = errorComponent;
	}

	public String getErrorParam() {
		return errorParam;
	}

	public void setErrorParam(String errorParam) {
		this.errorParam = errorParam;
	}

	public String getErrorValue() {
		return errorValue;
	}

	public void setErrorValue(String errorValue) {
		this.errorValue = errorValue;
	}

}
