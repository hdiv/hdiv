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

public class TrustAssertionDefaults {

	boolean required = false;

	boolean nid = true;

	boolean readOnly = false;

	private static final TrustAssertionDefaults defaults = new TrustAssertionDefaults();

	private TrustAssertionDefaults() {
	}

	public static TrustAssertionDefaults get() {
		return defaults;
	}

	public TrustAssertionDefaults required(final boolean required) {
		this.required = required;
		return this;
	}

	public TrustAssertionDefaults nid(final boolean nid) {
		this.nid = nid;
		return this;
	}

	public TrustAssertionDefaults readOnly(final boolean readOnly) {
		this.readOnly = readOnly;
		return this;
	}

	public boolean isRequired() {
		return required;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

}
