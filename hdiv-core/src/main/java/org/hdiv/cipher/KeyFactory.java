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

import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.hdiv.exception.HDIVException;
import org.hdiv.util.HDIVUtil;

/**
 * Key Factory generator.
 * 
 * @author Roberto Velasco
 */
public class KeyFactory implements IKeyFactory {
	
	/**
	 * Name of the default PRNG algorithm
	 */
	public static final String DEFAULT_ALGORITHM = "SHA1PRNG";
	
	/**
	 * Name of the default provider
	 */
	public static final String DEFAULT_PROVIDER = "SUN";

	/**
	 * Algorithm for the Key Generator
	 */
	private String algorithm;
	
	/**
	 * This is an algorithm-specific metric, specified in number of bits.
	 */
	private int keySize;
	
	/**
	 * Pseudo Random Number Generator algorithm
	 * 
	 * @see See Appendix A in the Java Cryptography Architecture API Specification &
	 *      Reference for information about standard PRNG algorithm names.
	 */
	private String prngAlgorithm = null;
	
	/**
	 * Provider name
	 */
	private String provider = null;
	
	
	/**
	 * This method is called whenever a key needs to be generated with Pseudo Random
	 * Number Generator algorithm and provider default values.
	 * 
	 * @return Key the encryption key
	 */
	public Key generateKeyWithDefaultValues() {
		
		this.prngAlgorithm = DEFAULT_ALGORITHM;
		this.provider = DEFAULT_PROVIDER;
		
		return generateKey();
	}
	
	/**
	 * This method is called whenever a key needs to be generated.
	 * @return Key the encryption key
	 */
	public Key generateKey() {
		
		try {									
			// Create a secure random number generator			
			SecureRandom random = SecureRandom.getInstance(this.prngAlgorithm, this.provider);
									
			byte[] iv = new byte[16];
			random.nextBytes(iv);

			// Get the key Generator 
			KeyGenerator kgen = KeyGenerator.getInstance(algorithm);
			kgen.init(keySize, random);

			// Generate the key specs 
			SecretKey skey = kgen.generateKey();
			byte[] raw = skey.getEncoded();
			SecretKeySpec skeySpec = new SecretKeySpec(raw, algorithm);
			Key key = new Key();
			key.setKey(skeySpec);
			key.setInitVector(iv);
			
			return key;

		} catch (Exception e) {
			String errorMessage = HDIVUtil.getMessage("key.factory.generate", e.getMessage());
			throw new HDIVException(errorMessage, e);
		}
	}
	
	/**
	 * @return Returns the algorithm for the Key Generator.
	 */
	public String getAlgorithm() {
		return algorithm;
	}
	
	/**
	 * @param algorithm The algorithm to set for the Key Generator.
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public int getKeySize() {
		return keySize;
	}

	public void setKeySize(int keySize) {
		this.keySize = keySize;
	}
	
	/**
	 * @return Returns the provider name.
	 */
	public String getProvider() {
		return provider;
	}
	
	/**
	 * @param provider The provider to set.
	 */
	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	/**
	 * @return Returns the prngAlgorithm.
	 */
	public String getPrngAlgorithm() {
		return prngAlgorithm;
	}
	
	/**
	 * @param prngAlgorithm The prngAlgorithm to set.
	 */
	public void setPrngAlgorithm(String prngAlgorithm) {
		this.prngAlgorithm = prngAlgorithm;
	}
}
