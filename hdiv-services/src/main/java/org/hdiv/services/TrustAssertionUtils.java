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

import java.lang.annotation.Annotation;

import org.hdiv.services.SecureIdContainer.VoidSecureIdContainer;

public class TrustAssertionUtils {

	public static final TrustAssertion EMPTY = new TrustAssertion() {

		public Class<? extends Annotation> annotationType() {
			return TrustAssertion.class;
		}

		public Class<? extends SecureIdContainer> idFor() {
			return VoidSecureIdContainer.class;
		}

		public String plainIdFor() {
			return TrustAssertion.EMPTY;
		}

		public String originMask() {
			return TrustAssertion.EMPTY;
		}

		public TriState nid() {
			return TriState.UNDEFINED;
		}

		public int max() {
			// TODO Auto-generated method stub
			return 0;
		}

		public int maxLength() {
			// TODO Auto-generated method stub
			return 0;
		}

		public int min() {
			// TODO Auto-generated method stub
			return 0;
		}

		public int minLength() {
			// TODO Auto-generated method stub
			return 0;
		}

		public String pattern() {
			// TODO Auto-generated method stub
			return null;
		}

		public TriState readOnly() {
			// TODO Auto-generated method stub
			return TriState.UNDEFINED;
		}

		public TriState required() {
			// TODO Auto-generated method stub
			return TriState.UNDEFINED;
		}

		public int step() {
			// TODO Auto-generated method stub
			return 0;
		}

		public Type type() {
			// TODO Auto-generated method stub
			return null;
		}

		public String[] values() {
			// TODO Auto-generated method stub
			return null;
		}

		public Class<? extends Options<?>> options() {
			// TODO Auto-generated method stub
			return null;
		}

		public String[] args() {
			// TODO Auto-generated method stub
			return null;
		}

		public SuggestType suggestType() {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean wildcardCollection() {
			// TODO Auto-generated method stub
			return false;
		}

		public TriState recursiveNavigation() {
			// TODO Auto-generated method stub
			return TriState.UNDEFINED;
		}

		public boolean ignored() {
			// TODO Auto-generated method stub
			return false;
		}

	};

	public static boolean areValues(final TrustAssertion assertion) {
		return assertion != null && (assertion.values().length > 0 || assertion.options() != StringOptions.class);
	}

	public static boolean isEntity(final TrustAssertion assertion) {
		return assertion != null
				&& (assertion.idFor() != VoidSecureIdContainer.class || !assertion.plainIdFor().equals(TrustAssertion.EMPTY));
	}

	public static boolean isReadOnly(final TrustAssertion assertion) {
		return assertion != null ? checkTriState(assertion.readOnly(), TrustAssertionDefaults.get().readOnly)
				: TrustAssertionDefaults.get().readOnly;
	}

	public static boolean isRequired(final TrustAssertion assertion) {
		return assertion != null ? checkTriState(assertion.required(), TrustAssertionDefaults.get().required)
				: TrustAssertionDefaults.get().required;
	}

	public static boolean isNid(final TrustAssertion assertion) {
		return checkTriState(assertion.nid(), TrustAssertionDefaults.get().nid);
	}

	public static boolean isNotEmpty(final String value) {
		return value != null && !TrustAssertion.EMPTY.equals(value);
	}

	private static boolean checkTriState(final TriState state, final boolean defaultValue) {
		switch (state) {
		case TRUE:
			return true;
		case FALSE:
			return false;
		default:
			return defaultValue;
		}
	}

}
