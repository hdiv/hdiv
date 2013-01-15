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
package org.hdiv.filter;

import org.hdiv.util.HDIVErrorCodes;

/**
 * Result of the invocation of the validation of a request with {@link IValidationHelper}
 * 
 * @author Gotzon Illarramendi
 * @since 2.1.4
 */
public class ValidatorHelperResult {

	/**
	 * Constant valid result.
	 */
	public static final ValidatorHelperResult VALID = new ValidatorHelperResult(true);

	/**
	 * Validation is valid or not
	 */
	private boolean valid;

	/**
	 * Validation method result value
	 */
	private Object value;

	/**
	 * Validation error code from {@link HDIVErrorCodes}
	 */
	private String errorCode;

	public ValidatorHelperResult(boolean valid) {
		this.valid = valid;
	}

	public ValidatorHelperResult(String errorCode) {
		this.valid = false;
		this.errorCode = errorCode;
	}

	public ValidatorHelperResult(boolean valid, Object value) {
		this.valid = valid;
		this.value = value;
	}

	public ValidatorHelperResult(boolean valid, String errorCode) {
		this.valid = valid;
		this.errorCode = errorCode;
	}

	public boolean isValid() {
		return valid;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public Object getValue() {
		return value;
	}

	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("Valid: ").append(this.valid).append(" Errorcode: ").append(this.errorCode).append(" Value:")
				.append(this.value);
		return b.toString();
	}

}
