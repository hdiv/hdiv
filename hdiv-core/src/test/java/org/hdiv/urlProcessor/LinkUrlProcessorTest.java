/**
 * Copyright 2005-2015 hdiv.org
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
package org.hdiv.urlProcessor;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.util.HDIVUtil;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.StringUtils;

public class LinkUrlProcessorTest extends AbstractHDIVTestCase {

	private LinkUrlProcessor linkUrlProcessor;

	protected void onSetUp() throws Exception {
		this.linkUrlProcessor = this.getApplicationContext().getBean(LinkUrlProcessor.class);
	}

	public void testProcessAction() {

		HttpServletRequest request = this.getMockRequest();
		String url = "/testAction.do";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.startsWith("/testAction.do?_HDIV_STATE_="));
	}

	public void testProcessActionWithContextPath() {

		MockHttpServletRequest request = this.getMockRequest();
		request.setContextPath("/path");
		String url = "/path/testAction.do";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.startsWith("/path/testAction.do?_HDIV_STATE_="));
	}

	public void testProcessActionWithAnchor() {

		HttpServletRequest request = this.getMockRequest();
		String url = "/testAction.do#anchor";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.startsWith("/testAction.do?_HDIV_STATE_="));
		assertTrue(result.endsWith("#anchor"));
	}

	public void testProcessActionWithParams() {

		HttpServletRequest request = this.getMockRequest();
		String url = "/testAction.do?params=value";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.startsWith("/testAction.do?params=0&_HDIV_STATE_"));
	}

	public void testProcessActionParamWithoutValue() {

		HttpServletRequest request = this.getMockRequest();
		String url = "/testAction.do?params";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.startsWith("/testAction.do?params=0&_HDIV_STATE_"));
	}

	public void testProcessActionRelative() {

		HttpServletRequest request = this.getMockRequest();
		String url = "testAction.do";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.startsWith("/path/testAction.do?_HDIV_STATE_="));
	}

	public void testProcessActionRelative2() {

		HttpServletRequest request = this.getMockRequest();
		String url = "../testAction.do";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.startsWith("/testAction.do?_HDIV_STATE_="));
	}

	public void testProcessActionRelative3() {

		MockHttpServletRequest request = this.getMockRequest();
		request.setContextPath("/path");

		String url = "../testAction.do";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.equals("../testAction.do"));
	}

	public void testProcessAbsoluteExternalUrlToAnotherApp() {

		MockHttpServletRequest request = this.getMockRequest();
		request.setContextPath("/path");

		String url = "/path-app/index.html";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertEquals(url, result);
	}

	public void testProcessAbsoluteExternalUrlWithContextPath() {

		MockHttpServletRequest request = this.getMockRequest();
		request.setContextPath("/path");

		String url = "http://www.google.com";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertEquals(url, result);
	}

	public void testProcessAbsoluteExternalUrl() {

		MockHttpServletRequest request = this.getMockRequest();

		String url = "http://www.google.com";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertEquals(url, result);
	}

	public void testProcessAbsoluteInternalUrlWithContextPath() {

		MockHttpServletRequest request = this.getMockRequest();
		request.setContextPath("/path");

		String url = "http://localhost:8080/path/sample.do";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.startsWith("http://localhost:8080/path/sample.do?_HDIV_STATE_="));
	}

	public void testProcessAbsoluteInternalUrlWithContextPath2() {

		MockHttpServletRequest request = this.getMockRequest();
		request.setContextPath("/diferentPath");

		String url = "http://localhost:8080/path/sample.do";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.startsWith("http://localhost:8080/path/sample.do"));
	}

	public void testProcessAbsoluteInternalUrl() {

		MockHttpServletRequest request = this.getMockRequest();

		String url = "http://localhost:8080/path/sample.do";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.startsWith("http://localhost:8080/path/sample.do?_HDIV_STATE_="));
	}

	public void testProcessActionStartPage() {

		HttpServletRequest request = this.getMockRequest();

		String url = "/testing.do"; // is a startPage
		String result = this.linkUrlProcessor.processUrl(request, url);
		assertEquals(url, result);

		url = "/onlyget.do"; // is a startPage only in Get requests
		result = this.linkUrlProcessor.processUrl(request, url);
		assertEquals(url, result);

		url = "/onlypost.do"; // is a startPage only in POST requests
		result = this.linkUrlProcessor.processUrl(request, url);
		assertTrue(result.startsWith("/onlypost.do?_HDIV_STATE_="));
	}

	public void testProcessActionStartPageWithParams() {

		HttpServletRequest request = this.getMockRequest();

		String url = "/testing.do?param=value"; // is a startPage
		String result = this.linkUrlProcessor.processUrl(request, url);
		assertEquals(url, result);

		url = "/onlyget.do?param=value"; // is a startPage only in Get requests
		result = this.linkUrlProcessor.processUrl(request, url);
		assertEquals(url, result);

		url = "/onlypost.do?param=value"; // is a startPage only in POST requests
		result = this.linkUrlProcessor.processUrl(request, url);
		assertTrue(result.startsWith("/onlypost.do?param=0&_HDIV_STATE_="));
	}

	public void testProcessWithBaseUrl() {

		MockHttpServletRequest request = this.getMockRequest();

		HDIVUtil.setBaseURL("/path/extra/plus/more", request);

		String url = "../testing.do";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.startsWith("/path/extra/testing.do?_HDIV_STATE_="));
	}

	public void testProcessMultiValueParam() {

		HttpServletRequest request = this.getMockRequest();
		String url = "/testAction.do?name=X&name=Y&name=Z";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.startsWith("/testAction.do?name=0&name=1&name=2&_HDIV_STATE_="));

	}

	public void testProcessMultiValueParamConfidentialityFalse() {

		HttpServletRequest request = this.getMockRequest();
		boolean conf = this.getConfig().getConfidentiality();
		this.getConfig().setConfidentiality(false);
		String url = "/testAction.do?name=X&name=Y&name=Z";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.startsWith("/testAction.do?name=X&name=Y&name=Z&_HDIV_STATE_="));

		this.getConfig().setConfidentiality(conf);
	}

	public void testProcessActionJsessionId() {

		HttpServletRequest request = this.getMockRequest();
		String url = "/testAction.do;jsessionid=67CFB560B6EC2677D51814A2A2B16B24";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.startsWith("/testAction.do;jsessionid=67CFB560B6EC2677D51814A2A2B16B24?_HDIV_STATE_"));
	}

	public void testProcessActionJsessionIdParam() {

		HttpServletRequest request = this.getMockRequest();
		String url = "/testAction.do;jsessionid=67CFB560B6EC2677D51814A2A2B16B24?params=1";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.startsWith("/testAction.do;jsessionid=67CFB560B6EC2677D51814A2A2B16B24?params=0&_HDIV_STATE_"));
	}

	public void testProcessActionJsessionStartPage() {

		HttpServletRequest request = this.getMockRequest();

		String url = "/testing.do;jsessionid=67CFB560B6EC2677D51814A2A2B16B24"; // is a startPage
		String result = this.linkUrlProcessor.processUrl(request, url);
		assertEquals(url, result);

	}

	public void testProcessActionWhitespace() {

		HttpServletRequest request = this.getMockRequest();

		String url = "/probando.do?param=text for testing";
		String result = this.linkUrlProcessor.processUrl(request, url);
		assertTrue(result.startsWith("/probando.do?param=0&_HDIV_STATE_"));
	}

	public void testProcessActionWhitespaceCharac() {

		HttpServletRequest request = this.getMockRequest();

		String url = "/probando.do?param=text+for+testing";
		String result = this.linkUrlProcessor.processUrl(request, url);
		assertTrue(result.startsWith("/probando.do?param=0&_HDIV_STATE_"));
	}

	public void testProcessActionAmpersand() {

		HttpServletRequest request = this.getMockRequest();

		String url = "/probando.do?stringArray=Value+1&amp;stringArray=Value+2&amp;stringArray=Value+3&amp;floatProperty=444.0&amp;intProperty=555";
		String result = this.linkUrlProcessor.processUrl(request, url);
		assertTrue(result.startsWith("/probando.do?stringArray=0&stringArray=1&stringArray=2&floatProperty=0&intProperty=0&_HDIV_STATE_"));
	}

	public void testJavaScriptLinks() {

		HttpServletRequest request = this.getMockRequest();
		String url = "javascript:performAction(this);";
		String result = this.linkUrlProcessor.processUrl(request, url);
		assertEquals(url, result);

		url = "JavaScript:performAction(this);";
		result = this.linkUrlProcessor.processUrl(request, url);
		assertEquals(url, result);

		url = "javaScript:performAction(this);";
		result = this.linkUrlProcessor.processUrl(request, url);
		assertEquals(url, result);
	}

	public void testProcessUrlWithStateId() {

		HttpServletRequest request = this.getMockRequest();

		String url = "/link.do?_HDIV_STATE_=11-11-1234567890";
		String result = this.linkUrlProcessor.processUrl(request, url);
		assertEquals(1, StringUtils.countOccurrencesOf(result, "_HDIV_STATE_"));
		assertFalse(result.contains("11-11-1234567890"));
		assertTrue(!result.equals(url));

		url = "/link.do?aaaa=bbbb&_HDIV_STATE_=11-11-1234567890";
		result = this.linkUrlProcessor.processUrl(request, url);
		assertEquals(1, StringUtils.countOccurrencesOf(result, "_HDIV_STATE_"));
		assertFalse(result.contains("11-11-1234567890"));
		assertTrue(!result.equals(url));

		url = "/link.do?aaaa=bbbb&_HDIV_STATE_=11-11-1234567890#hash";
		result = this.linkUrlProcessor.processUrl(request, url);
		assertEquals(1, StringUtils.countOccurrencesOf(result, "_HDIV_STATE_"));
		assertFalse(result.contains("11-11-1234567890"));
		assertTrue(!result.equals(url));

		url = "/link.do?aaaa=bbbb&_MODIFY_HDIV_STATE_=11-11-1234567890";
		result = this.linkUrlProcessor.processUrl(request, url);
		assertEquals(1, StringUtils.countOccurrencesOf(result, "&_HDIV_STATE_"));
		assertEquals(1, StringUtils.countOccurrencesOf(result, "&_MODIFY_HDIV_STATE_"));
		assertTrue(result.contains("11-11-1234567890"));
		assertTrue(!result.equals(url));
	}

}
