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
package org.hdiv.dataValidator;

/**
 * Validation Result class.
 * 
 * @author Roberto Velasco
 * @author Oscar Ocariz
 */
public class ValidationResult implements IValidationResult {

	/**
	 * Indicates if the user request passes HDIV validation.
	 */
	private boolean legal;

	private Object result;

	/**
	 * Expected value for the HDIV validation process
	 */
	private String expectedValue;

	/**
	 * @return Returns the legal.
	 */
	public boolean getLegal() {
		return legal;
	}

	/**
	 * @param legal The legal to set.
	 */
	public void setLegal(boolean legal) {
		this.legal = legal;
	}

	/**
	 * @return Returns the result.
	 */
	public Object getResult() {
		return result;
	}

	/**
	 * @param result The result to set.
	 */
	public void setResult(Object result) {
		this.result = result;
	}

	/**
	 * @return Returns the expectedValue.
	 */
	public String getExpectedValue() {
		return expectedValue;
	}

	/**
	 * @param expectedValue The expectedValue to set.
	 */
	public void setExpectedValue(String expectedValue) {
		this.expectedValue = expectedValue;
	}
}
