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
package org.hdiv.filter;

import java.util.ArrayList;
import java.util.List;

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
	 * Constant valid result for requests that do not require validation.
	 */
	public static final ValidatorHelperResult VALIDATION_NOT_REQUIRED = new ValidatorHelperResult(true);

	/**
	 * Validation is valid or not
	 */
	private boolean valid;

	/**
	 * Validation method result value
	 */
	private Object value;

	/**
	 * Validation error data
	 */
	private List<ValidatorError> errors;

	public ValidatorHelperResult(boolean valid) {
		this.valid = valid;
	}

	public ValidatorHelperResult(ValidatorError error) {
		this(false, error);
	}

	public ValidatorHelperResult(List<ValidatorError> errors) {
		this.valid = false;
		this.errors = errors;
	}

	public ValidatorHelperResult(boolean valid, Object value) {
		this.valid = valid;
		this.value = value;
	}

	public ValidatorHelperResult(boolean valid, ValidatorError error) {
		this.valid = valid;
		this.errors = new ArrayList<ValidatorError>();
		this.errors.add(error);
	}

	public boolean isValid() {
		return valid;
	}

	public List<ValidatorError> getErrors() {
		return errors;
	}

	public Object getValue() {
		return value;
	}

	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("Valid: ").append(this.valid).append(", ");
		if (this.errors != null) {
			for (ValidatorError error : errors) {
				b.append(" Errorcode: ").append(error.toString());
			}
		}
		if (this.value != null) {
			b.append(" Value:").append(this.value).append(", ");
		}
		if (this.equals(VALIDATION_NOT_REQUIRED)) {
			b.append(" Type: VALIDATION_NOT_REQUIRED");
		}

		return b.toString();
	}

}
