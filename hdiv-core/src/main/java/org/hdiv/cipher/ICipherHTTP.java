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

import org.hdiv.exception.HDIVException;

/**
 * The main interface for cryptography functions
 * 
 * @author Roberto Velasco
 */
public interface ICipherHTTP extends Serializable {

	/**
	 * Generates a Cipher object that implements the specified
	 * <code>transformation</code>, initializes cipher vector and initializes
	 * cipher to encryption mode with a key and a set of algorithm parameters.
	 * 
	 * @param key the encryption key
	 * @throws HDIVException if there is an initialization error.
	 */
	public void initEncryptMode(Key key);

	/**
	 * Generates a Cipher object that implements the specified
	 * <code>transformation</code>, initializes cipher vector and initializes
	 * cipher to decryption mode with a key and a set of algorithm parameters.
	 * 
	 * @param key the encryption key
	 * @throws HDIVException if there is an initialization error.
	 */
	public void initDecryptMode(Key key);

	/**
	 * Encrypts <code>data</code> in a single-part operation, or finishes a
	 * multiple-part operation. The data is encrypted depending on how this cipher
	 * was initialized.
	 * <p>
	 * The bytes in the input buffer, and any input bytes that may have been buffered
	 * during a previous update operation, are processed, with padding (if requested)
	 * being applied. The result is stored in a new buffer.
	 * </p>
	 * <p>
	 * if any exception is thrown, this cipher object may need to be reset before it
	 * can be used again.
	 * </p>
	 * @param data The input buffer to encrypt
	 * @return The new buffer with the result
	 */	
	public byte[] encrypt(byte[] data);

	/**
	 * Decrypts <code>data</code> in a single-part operation, or finishes a
	 * multiple-part operation. The data is decrypted depending on how this cipher
	 * was initialized.
	 * <p>
	 * The bytes in the input buffer, and any input bytes that may have been buffered
	 * during a previous update operation, are processed, with padding (if requested)
	 * being applied. The result is stored in a new buffer.
	 * </p>
	 * <p>
	 * if any exception is thrown, this cipher object may need to be reset before it
	 * can be used again.
	 * </p>
	 * @param data The input buffer to decrypt
	 * @return The new buffer with the result
	 */		
	public byte[] decrypt(byte[] data);

}
