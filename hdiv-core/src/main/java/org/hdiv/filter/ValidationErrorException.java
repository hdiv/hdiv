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
package org.hdiv.filter;

public class ValidationErrorException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final ValidatorHelperResult result;

	public ValidationErrorException() {
		this(null, null, new ValidatorHelperResult(false));
	}

	public ValidationErrorException(final String message) {
		this(message, null, new ValidatorHelperResult(new ValidatorError(message)));
	}

	public ValidationErrorException(final String message, final Throwable e) {
		this(message, e, new ValidatorHelperResult(new ValidatorError(message)));
		result.getErrors().get(0).setException(e);
	}

	public ValidationErrorException(final ValidatorHelperResult result) {
		this(null, null, result);
	}

	public ValidationErrorException(final String message, final Throwable e, final ValidatorHelperResult result) {
		super(message, e);
		this.result = result;
	}

	public ValidatorHelperResult getResult() {
		return result;
	}

}
