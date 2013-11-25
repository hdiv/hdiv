/**
 * Copyright 2005-2013 hdiv.org
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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.exception.HDIVException;
import org.hdiv.util.HDIVUtil;

/**
 * The principal class related with cryptography. It has the responsibility to encrypt and decrypt data.
 * 
 * @author Roberto Velasco
 * @see javax.crypto.Cipher
 * @see javax.crypto.spec.IvParameterSpec
 */
public class CipherHTTP implements ICipherHTTP {

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that a loaded class corresponds exactly
	 * to a serialized object.
	 */
	private static final long serialVersionUID = -1731737465730669951L;

	/**
	 * Commons Logging instance.
	 */
	private Log log = LogFactory.getLog(CipherHTTP.class);

	/**
	 * Name of the default cipher algorithm
	 */
	private static final String DEFAULT_ALGORITHM = "AES/CBC/PKCS5Padding";

	/**
	 * Algorithm name of cipher object
	 */
	private String transformation = DEFAULT_ALGORITHM;

	/**
	 * Provider name
	 */
	private String provider;

	/**
	 * This object provides the functionality of a cryptographic cipher for encryption and decryption
	 */
	private Cipher cipher;

	private IvParameterSpec ivSpec;

	private boolean encryptMode;

	/**
	 * Generates a Cipher object that implements the specified transformation.
	 */
	public void init() {

		try {
			if (this.provider == null) {
				this.cipher = Cipher.getInstance(this.transformation);
			} else {
				this.cipher = Cipher.getInstance(this.transformation, this.provider);
			}

			if (log.isDebugEnabled()) {
				log.debug("New CipherHTTP instance [cipher = " + this.cipher + "]");
			}

		} catch (NoSuchProviderException e) {
			throw new HDIVException(e.getMessage());

		} catch (NoSuchAlgorithmException e) {
			throw new HDIVException(e.getMessage());

		} catch (NoSuchPaddingException e) {
			throw new HDIVException(e.getMessage());
		}
	}

	/**
	 * <p>
	 * Generates a Cipher object that implements the specified <code>transformation</code>, initializes cipher vector
	 * and initializes cipher to encryption mode with a key and a set of algorithm parameters.
	 * </p>
	 * <p>
	 * The name of the transformation, e.g., DES/CBC/PKCS5Padding. See Appendix A in the <a
	 * href="../../../guide/security/jce/JCERefGuide.html#AppA"> Java Cryptography Extension Reference Guide for
	 * information about standard transformation names.
	 * </p>
	 * 
	 * @param key
	 *            the encryption key
	 * @throws HDIVException
	 *             if there is an initialization error.
	 */
	public void initEncryptMode(Key key) {

		try {
			// vector initialization
			this.ivSpec = new IvParameterSpec(key.getInitVector());

			// Constant used to initialize cipher to encryption mode
			this.cipher.init(Cipher.ENCRYPT_MODE, key.getKey(), this.ivSpec);
			this.encryptMode = true;

		} catch (InvalidKeyException e) {
			String errorMessage = HDIVUtil.getMessage("cipher.init.encrypt", e.getMessage());
			throw new HDIVException(errorMessage, e);
		} catch (InvalidAlgorithmParameterException e) {
			String errorMessage = HDIVUtil.getMessage("cipher.init.encrypt", e.getMessage());
			throw new HDIVException(errorMessage, e);
		}
	}

	/**
	 * Generates a Cipher object that implements the specified <code>transformation</code>, initializes cipher vector
	 * and initializes cipher to decryption mode with a key and a set of algorithm parameters.
	 * 
	 * @param key
	 *            the encryption key
	 * @throws HDIVException
	 *             if there is an initialization error.
	 */
	public void initDecryptMode(Key key) {

		try {
			// vector initialization
			this.ivSpec = new IvParameterSpec(key.getInitVector());

			this.cipher.init(Cipher.DECRYPT_MODE, key.getKey(), this.ivSpec);
			this.encryptMode = false;

		} catch (InvalidKeyException e) {
			String errorMessage = HDIVUtil.getMessage("cipher.init.decrypt", e.getMessage());
			throw new HDIVException(errorMessage, e);
		} catch (InvalidAlgorithmParameterException e) {
			String errorMessage = HDIVUtil.getMessage("cipher.init.decrypt", e.getMessage());
			throw new HDIVException(errorMessage, e);
		}
	}

	/**
	 * Encrypts <code>data</code> in a single-part operation, or finishes a multiple-part operation. The data is
	 * encrypted depending on how this cipher was initialized.
	 * <p>
	 * The bytes in the input buffer, and any input bytes that may have been buffered during a previous update
	 * operation, are processed, with padding (if requested) being applied. The result is stored in a new buffer.
	 * </p>
	 * <p>
	 * if any exception is thrown, this cipher object may need to be reset before it can be used again.
	 * </p>
	 * 
	 * @param data
	 *            The input buffer to encrypt
	 * @return The new buffer with the result
	 * @throws HDIVException
	 *             if any exception is thrown in encryption process.
	 */
	public byte[] encrypt(byte[] data) {

		try {
			return cipher.doFinal(data);

		} catch (IllegalBlockSizeException e) {
			String errorMessage = HDIVUtil.getMessage("cipher.encrypt", e.getMessage());
			throw new HDIVException(errorMessage, e);
		} catch (BadPaddingException e) {
			String errorMessage = HDIVUtil.getMessage("cipher.encrypt", e.getMessage());
			throw new HDIVException(errorMessage, e);
		}

	}

	/**
	 * Decrypts <code>data</code> in a single-part operation, or finishes a multiple-part operation. The data is
	 * decrypted depending on how this cipher was initialized.
	 * <p>
	 * The bytes in the input buffer, and any input bytes that may have been buffered during a previous update
	 * operation, are processed, with padding (if requested) being applied. The result is stored in a new buffer.
	 * </p>
	 * <p>
	 * if any exception is thrown, this cipher object may need to be reset before it can be used again.
	 * </p>
	 * 
	 * @param data
	 *            The input buffer to decrypt
	 * @return The new buffer with the result
	 * @throws HDIVException
	 *             if any exception is thrown in decryption process.
	 */
	public byte[] decrypt(byte[] data) {

		try {
			return cipher.doFinal(data);

		} catch (IllegalBlockSizeException e) {
			String errorMessage = HDIVUtil.getMessage("cipher.decrypt", e.getMessage());
			throw new HDIVException(errorMessage, e);
		} catch (BadPaddingException e) {
			String errorMessage = HDIVUtil.getMessage("cipher.decrypt", e.getMessage());
			throw new HDIVException(errorMessage, e);
		}
	}

	/**
	 * @param transformation
	 *            The transformation to set for the cipher factory bean.
	 */
	public void setTransformation(String transformation) {
		this.transformation = transformation;
	}

	/**
	 * @param provider
	 *            The provider to set.
	 */
	public void setProvider(String provider) {
		this.provider = provider;
	}

	public boolean isEncryptMode() {
		return encryptMode;
	}

	public void setEncryptMode(boolean encryptMode) {
		this.encryptMode = encryptMode;
	}
}
