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
package org.hdiv.services;

public class SuggestObjectWrapper {

	public static final String ID = "svalue";

	private final String svalue;

	public SuggestObjectWrapper(final String id) {
		svalue = id;
	}

	public String getSvalue() {
		return svalue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (svalue == null ? 0 : svalue.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SuggestObjectWrapper other = (SuggestObjectWrapper) obj;
		if (svalue == null) {
			if (other.svalue != null) {
				return false;
			}
		}
		else if (!svalue.equals(other.svalue)) {
			return false;
		}
		return true;
	}

}
