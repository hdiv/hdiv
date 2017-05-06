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

public class TrustAssertionUtils {

	public static final TrustAssertion EMPTY = new TrustAssertion() {

		public Class<? extends Annotation> annotationType() {
			return TrustAssertion.class;
		}

		public Class<?> idFor() {
			return Void.class;
		}

		public String plainIdFor() {
			return TrustAssertion.EMPTY;
		}

		public String originMask() {
			return TrustAssertion.EMPTY;
		}

		public boolean nid() {
			return true;
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

		public boolean readOnly() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean required() {
			// TODO Auto-generated method stub
			return false;
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

		public boolean recursiveNavigation() {
			// TODO Auto-generated method stub
			return false;
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
		return assertion != null && (assertion.idFor() != Void.class || !assertion.plainIdFor().equals(TrustAssertion.EMPTY));
	}

}
