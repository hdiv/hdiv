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

public class EntityValidationInfo {
	private final Class<?> idFor;

	private String plainIdFor;

	private String originMask;

	private final boolean singleValueType;

	public EntityValidationInfo(final Class<?> idFor, final boolean singleValueType) {
		this.idFor = idFor;
		this.singleValueType = singleValueType;
	}

	public EntityValidationInfo(final TrustAssertion trustAssertion, final boolean singleValueType) {
		this(trustAssertion.idFor(), singleValueType);
		plainIdFor = trustAssertion.plainIdFor();
		originMask = trustAssertion.originMask();
	}

	public Class<?> getIdFor() {
		return idFor;
	}

	public String getPlainIdFor() {
		return plainIdFor;
	}

	public String getOriginMask() {
		return originMask;
	}

	public boolean isSingleValueType() {
		return singleValueType;
	}

}
