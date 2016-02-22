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
package org.hdiv.exception;

/**
 * Exception thrown when an attack is detected in an <b>immediate</b> component. This exception is thrown in order to
 * stop JSF's default life cycle and show HDIV's error page.
 * 
 * @author Gotzon Illarramendi
 */
public class StateValidationException extends HDIVException {

	private static final long serialVersionUID = 373542288299714280L;

	public StateValidationException() {
	}

	public StateValidationException(Throwable cause) {
		super(cause);
	}

}
