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
package org.hdiv.idGenerator;

import java.io.Serializable;

/**
 * A key generator that uses the RandomGuid support class. The default implementation used by HDIV to generate random tokens to avoid CSRF
 * attacks.
 * 
 * @author Gorka Vicente
 * @since HDIV 2.0.4
 */
public class RandomGuidUidGenerator implements UidGenerator, Serializable {

	private static final long serialVersionUID = 5187183004631843583L;

	/**
	 * Should the random GUID generated be secure?
	 */
	private boolean secure;

	/**
	 * Returns whether or not the generated random numbers are <i>secure</i>, meaning cryptographically strong.
	 * 
	 * @return true if the random is cryptographically strong
	 */
	public boolean isSecure() {
		return secure;
	}

	/**
	 * Sets whether or not the generated random numbers should be <i>secure</i>. If set to true, generated GUIDs are cryptographically
	 * strong.
	 * @param secure boolean
	 */
	public void setSecure(final boolean secure) {
		this.secure = secure;
	}

	public Serializable generateUid() {
		if (!secure) {
			return FastUUID.get();
		}
		return RandomGuid.getRandomGuid(secure);
	}

	public Serializable parseUid(final String encodedUid) {
		return encodedUid;
	}
}