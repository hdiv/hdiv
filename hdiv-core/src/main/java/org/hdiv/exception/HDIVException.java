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
package org.hdiv.exception;

/**
 * Exception thrown when HDIV validation is not passed.
 * 
 * @author Roberto Velasco
 */
public class HDIVException extends RuntimeException {

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that
	 * a loaded class corresponds exactly to a serialized object.
	 */
	private static final long serialVersionUID = 4564720056088062302L;

	/**
	 * Create a new exception.
	 * 
	 * @see java.lang.RuntimeException
	 */
	public HDIVException() {
		super();
	}

    /**
	 * Constructs a new runtime exception with the specified cause and a detail
	 * message of <tt>(cause==null ? null : cause.toString())</tt> (which typically
	 * contains the class and detail message of <tt>cause</tt>). This constructor
	 * is useful for runtime exceptions that are little more than wrappers for other
	 * throwables.
	 * 
	 * @param cause the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A <tt>null</tt> value is permitted,
	 *            and indicates that the cause is nonexistent or unknown.)
	 * @see java.lang.RuntimeException#RuntimeException(java.lang.Throwable)
	 */
	public HDIVException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new runtime exception with the specified detail message. The
	 * cause is not initialized, and may subsequently be initialized by a call to
	 * {@link #initCause}.
	 * 
	 * @param message the detail message. The detail message is saved for later
	 *            retrieval by the {@link #getMessage()} method.
	 * @see java.lang.RuntimeException
	 */
	public HDIVException(String message) {

		super(message);
	}

	/**
	 * Constructs a new runtime exception with the specified detail message and
	 * cause.
	 * 
	 * @param message the detail message (which is saved for later retrieval by the
	 *            {@link #getMessage()} method).
	 * @param cause the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A <tt>null</tt> value is permitted,
	 *            and indicates that the cause is nonexistent or unknown.)
	 * @see java.lang.RuntimeException
	 */
	public HDIVException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Returns the cause of this throwable or <code>null</code> if the cause is
	 * nonexistent or unknown. (The cause is the throwable that caused this throwable
	 * to get thrown.)
	 * 
	 * @see java.lang.RuntimeException#getCause()
	 */
	public Throwable getCause() {
		return super.getCause();
	}

}
