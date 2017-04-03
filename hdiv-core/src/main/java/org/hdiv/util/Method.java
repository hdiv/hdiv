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
package org.hdiv.util;

/**
 * Class containing valid HTTP Method
 */
public enum Method {
	GET(false), HEAD(false), OPTIONS(false), POST(true), PATCH(true), PUT(true), DELETE(true), ANY(false);

	public final boolean isForm;

	Method(final boolean isForm) {
		this.isForm = isForm;
	}

	public static Method secureValueOf(final String value) {
		try {
			if (value == null) {
				return null;
			}
			return valueOf(value.toUpperCase());
		}
		catch (final IllegalArgumentException e) {
			return null;
		}
	}

}
