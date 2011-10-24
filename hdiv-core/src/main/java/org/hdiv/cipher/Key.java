/**
 * Copyright 2005-2011 hdiv.org
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
package org.hdiv.cipher;

import java.io.Serializable;

import javax.crypto.spec.SecretKeySpec;

/**
 * Stored Session related data in the cipher section
 * 
 * @author Roberto Velasco
 */
public class Key implements Serializable {

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that
	 * a loaded class corresponds exactly to a serialized object.
	 */
	private static final long serialVersionUID = 3354458880328517060L;

	/**
	 * Secret key specification
	 */
	SecretKeySpec key;

	byte[] initVector;


	public byte[] getInitVector() {
		return initVector;
	}

	public void setInitVector(byte[] initVector) {
		this.initVector = initVector;
	}

	public SecretKeySpec getKey() {
		return key;
	}

	public void setKey(SecretKeySpec key) {
		this.key = key;
	}
}
