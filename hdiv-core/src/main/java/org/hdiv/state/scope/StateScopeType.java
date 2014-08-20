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
package org.hdiv.state.scope;

/**
 * Different type of {@link StateScope} and their names.
 * 
 * @since 2.1.7
 */
public enum StateScopeType {

	/**
	 * <p>
	 * States stored in this scope are removed when user session is finished.
	 * </p>
	 * <p>
	 * States stored in this scope are unique per user.
	 * </p>
	 */
	USER_SESSION("user-session", "U"),

	/**
	 * <p>
	 * States stored in this scope are removed when the server is stopped.
	 * </p>
	 * <p>
	 * States stored in this scope are shared by all the users of the application.
	 * </p>
	 */
	APP("app", "A");

	/**
	 * Scope name used for the configuration.
	 */
	private String name;

	/**
	 * Prefix code used at state id creation.
	 */
	private String prefix;

	StateScopeType(String name, String prefix) {
		this.name = name;
		this.prefix = prefix;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

}
