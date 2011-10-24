/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hdiv.util;

import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.cipher.IKeyFactory;
import org.hdiv.cipher.Key;

/**
 * Unit tests for the <code>org.hdiv.util.EncodingUtil</code> class.
 * 
 * @author Gorka Vicente
 */
public class EncodingUtilTest extends AbstractHDIVTestCase {

	private static Log log = LogFactory.getLog(EncodingUtilTest.class);
	
	private EncodingUtil encodingUtil;

	protected void onSetUp() throws Exception {
		
		IKeyFactory keyFactory = (IKeyFactory) this.getApplicationContext().getBean("keyFactory");
		Key key = keyFactory.generateKey();
		HttpSession httpSession = HDIVUtil.getHttpSession();
		httpSession.setAttribute("key", key);

		this.encodingUtil = (EncodingUtil) this.getApplicationContext().getBean("encoding");
	}

	public void testEncodeAndDecode64Cipher() {

		String clearData = "clearDa+ta";
		String encodedData = encodingUtil.encode64Cipher(clearData);

		String decodedData = (String) encodingUtil.decode64Cipher(encodedData);

		log.debug("decodedData:" + decodedData);
		Assert.assertEquals(clearData, decodedData);
	}

	/**
	 * Encrypted data is modified and it causes and error.
	 */
	public void testDecode64Cipher() {

		try {			
			String clearData = "clearDa+tadsfasdfsdfsd";
			String encodedData = encodingUtil.encode64Cipher(clearData);
		
			String result = (String) encodingUtil.decode64Cipher("head" + encodedData + "tail");
			assertFalse(true);
			
		} catch (Exception e) {
			assertTrue(true);
		}
	}

	public void testEncodeAndDecode64() {

		String clearData = "clearDa+ta";
		String encodedData = encodingUtil.encode64(clearData);

		String decodedData = (String) encodingUtil.decode64(encodedData);

		log.debug("decodedData:" + decodedData);
		Assert.assertEquals(clearData, decodedData);
	}	

}
