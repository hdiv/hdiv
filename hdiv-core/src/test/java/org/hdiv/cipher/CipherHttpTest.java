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

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.util.EncodingUtil;

/**
 * Unit tests for the <code>org.hdiv.cipher.CipherHttp</code> class.
 * 
 * @author Gorka Vicente
 */
public class CipherHttpTest extends AbstractHDIVTestCase {

	private ICipherHTTP cipherHttp;
	private IKeyFactory keyFactory;
	private Key key;

	/*
	 * @see TestCase#setUp()
	 */
	protected void onSetUp() throws Exception {

		this.cipherHttp = this.getApplicationContext().getBean(ICipherHTTP.class);
		this.keyFactory = this.getApplicationContext().getBean(IKeyFactory.class);
		this.key = this.keyFactory.generateKey();
	}

	public void testEncrypt() throws Exception {

		String data = "Data to encrypt";
		this.cipherHttp.initEncryptMode(key);
		String encryptedData = new String(this.cipherHttp.encrypt(data.getBytes()), EncodingUtil.ZIP_CHARSET);

		this.cipherHttp.initDecryptMode(key);
		String clearData = new String(this.cipherHttp.decrypt(encryptedData.getBytes(EncodingUtil.ZIP_CHARSET)));

		assertTrue(clearData.equalsIgnoreCase(data));
	}

}
