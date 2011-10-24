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

/**
 * Mock implementation of ICipherHTTP only to tests.
 * 
 * @author Roberto Velasco
 */
public class MockCipherHTTP implements ICipherHTTP {


	private static final long serialVersionUID = 1406354467403622899L;

	private String algorithm;


	public String decrypt(String data) {
		return data.substring(1);
	}

	public String encrypt(String data) {
		return "_" + data;
	} 

	public byte[] decrypt(byte[] data) {
		return data;
	}

	public byte[] encrypt(byte[] data) {
		return data;
	}

	public void initEncryptMode(Key key) {
	}

	public void initDecryptMode(Key key) {
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
}
