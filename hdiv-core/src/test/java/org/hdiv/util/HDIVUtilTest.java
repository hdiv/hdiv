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
package org.hdiv.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.hdiv.exception.HDIVException;
import org.hdiv.urlProcessor.UrlData;
import org.hdiv.urlProcessor.UrlDataImpl;
import org.junit.Assert;
import org.junit.Test;

public class HDIVUtilTest {

	@Test
	public void testObfuscationUtils() {
		Assert.assertTrue(HDIVUtil.isObfuscatedTarget(UrlData.OBFUSCATION_ROOT_PATH));
		Assert.assertTrue(HDIVUtil.isObfuscatedTarget(UrlData.OBFUSCATION_ROOT_PATH + ";jsessionid=67CFB560B6EC2677D51814A2A2B16B24"));
		Assert.assertTrue(HDIVUtil.isObfuscatedTarget(UrlData.OBFUSCATION_ROOT_PATH + "?_HDIV_STATE=aaaa"));
		Assert.assertTrue(HDIVUtil.isObfuscatedTarget("http://localhost:8080" + UrlData.OBFUSCATION_ROOT_PATH + "?_HDIV_STATE=aaaa"));

	}

	@Test
	public void testProcessActionJsessionId() {

		final String url = "/testAction.do;jsessionid=67CFB560B6EC2677D51814A2A2B16B24";
		final UrlDataImpl data = new UrlDataImpl(url, Method.GET);
		Assert.assertEquals("/testAction.do", HDIVUtil.stripAndFillSessionData(url, data));
		Assert.assertEquals("jsessionid=67CFB560B6EC2677D51814A2A2B16B24", data.getjSessionId());
	}

	@Test
	public void testProcessActionJsessionIdParam() {
		final String url = "/testAction.do;jsessionid=67CFB560B6EC2677D51814A2A2B16B24?params=1";
		final UrlDataImpl data = new UrlDataImpl(url, Method.GET);
		Assert.assertEquals("/testAction.do?params=1", HDIVUtil.stripAndFillSessionData(url, data));
		Assert.assertEquals("jsessionid=67CFB560B6EC2677D51814A2A2B16B24", data.getjSessionId());

	}

	@Test
	public void testProcessActionJsessionStartPage() {

		final String url = "/testing.do;jsessionid=67CFB560B6EC2677D51814A2A2B16B24"; // is a startPage
		final UrlDataImpl data = new UrlDataImpl(url, Method.GET);
		Assert.assertEquals("/testing.do", HDIVUtil.stripAndFillSessionData(url, data));
		Assert.assertEquals("jsessionid=67CFB560B6EC2677D51814A2A2B16B24", data.getjSessionId());
	}

	@Test
	public void testDecoding() {
		final String url = "/testing.do;jsessionid=67CFB560B6EC2677D51814A2A2B16B24"; // is a startPage
		long time = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 1000000; i++) {
			try {
				HDIVUtil.decodeValue(sb, url, "UTF-8");
			}
			catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Time:" + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			try {
				URLDecoder.decode(url, "UTF-8");
			}
			catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Time:" + (System.currentTimeMillis() - time));
	}

	@Test(expected = HDIVException.class)
	public void testInvalidPageId() {
		HDIVStateUtils.getPageId("example-1-FEE0710648A1BE0BAEF05904B586A89B");
	}

	@Test(expected = HDIVException.class)
	public void testInvalidStateId() {
		HDIVStateUtils.getStateId("1-example-FEE0710648A1BE0BAEF05904B586A89B");
	}
}
