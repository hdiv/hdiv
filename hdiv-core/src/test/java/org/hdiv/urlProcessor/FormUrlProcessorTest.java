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
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.util.HDIVUtil;
import org.hdiv.util.Method;

public class FormUrlProcessorTest extends AbstractHDIVTestCase {

	private FormUrlProcessor formUrlProcessor;

	private DataComposerFactory dataComposerFactory;

	@Override
	protected void onSetUp() throws Exception {
		formUrlProcessor = getApplicationContext().getBean(FormUrlProcessor.class);
		dataComposerFactory = getApplicationContext().getBean(DataComposerFactory.class);
	}

	public void testProcessAction() {

		final HttpServletRequest request = getMockRequest();
		final String action = "/testAction.do";

		final String result = formUrlProcessor.processUrl(request, action);

		// Post urls are not modified
		assertEquals(action, result);
	}

	public void testProcessActionGetMethod() {

		final HttpServletRequest request = getMockRequest();
		final String action = "/testAction.do";

		final String result = formUrlProcessor.processUrl(request, action, Method.GET);

		// Post urls are not modified
		assertEquals(action, result);
	}

	public void testProcessActionWithParam() {

		final HttpServletRequest request = getMockRequest();
		final String action = "/testAction.do?params=value";

		final String result = formUrlProcessor.processUrl(request, action);

		assertEquals("/testAction.do?params=0", result);
	}

	public void testProcessActionParamWithoutValue() {

		final HttpServletRequest request = getMockRequest();
		final String action = "/testAction.do?params";

		final String result = formUrlProcessor.processUrl(request, action);

		assertEquals("/testAction.do?params=0", result);
	}

	public void testProcessActionComplete() {

		final HttpServletRequest request = getMockRequest();
		final IDataComposer dataComposer = dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);
		dataComposer.startPage();

		final String action = "/testAction.do";

		final String result = formUrlProcessor.processUrl(request, action);

		// Post urls are not modified
		assertEquals(action, result);

		final String val = dataComposer.compose("param", "value", false);
		assertEquals("0", val);

		final String requestId = dataComposer.endRequest();

		assertNotNull(requestId);
		assertTrue(requestId.length() > 0);
	}

	public void testProcessActionStartPage() {

		final HttpServletRequest request = getMockRequest();

		String action = "/testing.do?params=value";// is a startPage
		String result = formUrlProcessor.processUrl(request, action);
		assertEquals(action, result);

		action = "/onlyget.do?params=value"; // is a startPage only in Get requests
		result = formUrlProcessor.processUrl(request, action);
		assertEquals("/onlyget.do?params=0", result);

		action = "/onlypost.do?params=value"; // is a startPage only in POST requests
		result = formUrlProcessor.processUrl(request, action);
		assertEquals(action, result);
	}

	public void testProcessMultiValueParam() {

		final HttpServletRequest request = getMockRequest();
		final String url = "/testAction.do?name=X&name=Y&name=Z";

		final String result = formUrlProcessor.processUrl(request, url);

		assertTrue(result.startsWith("/testAction.do?name=0&name=1&name=2"));

	}

	public void testProcessMultiValueParamConfidentialityFalse() {

		final HttpServletRequest request = getMockRequest();
		final boolean conf = getConfig().getConfidentiality();
		getConfig().setConfidentiality(false);
		final String url = "/testAction.do?name=X&name=Y&name=Z";

		final String result = formUrlProcessor.processUrl(request, url);

		assertTrue(result.startsWith("/testAction.do?name=X&name=Y&name=Z"));

		getConfig().setConfidentiality(conf);
	}

	public void testProcessActionJsessionId() {

		final HttpServletRequest request = getMockRequest();
		final String url = "/testAction.do;jsessionid=67CFB560B6EC2677D51814A2A2B16B24";

		final String result = formUrlProcessor.processUrl(request, url);

		assertEquals(result, url);
	}

	public void testProcessActionJsessionIdParam() {

		final HttpServletRequest request = getMockRequest();
		final String url = "/testAction.do;jsessionid=67CFB560B6EC2677D51814A2A2B16B24?params=0";

		final String result = formUrlProcessor.processUrl(request, url);

		assertEquals(result, url);
	}

	public void testProcessActionJsessionStartPage() {

		final HttpServletRequest request = getMockRequest();

		final String url = "/testing.do;jsessionid=67CFB560B6EC2677D51814A2A2B16B24"; // is a startPage
		final String result = formUrlProcessor.processUrl(request, url);
		assertEquals(url, result);
	}

	public void testProcessActionWithStateId() {

		final HttpServletRequest request = getMockRequest();

		String url = "/formAction.do?_HDIV_STATE_=11-11-1234567890";
		String result = formUrlProcessor.processUrl(request, url);
		assertEquals("/formAction.do", result);

		url = "/formAction.do?aaaa=bbbb&_HDIV_STATE_=11-11-1234567890";
		result = formUrlProcessor.processUrl(request, url);
		assertEquals("/formAction.do?aaaa=0", result);

		url = "/formAction.do?aaaa=bbbb&_HDIV_STATE_=11-11-1234567890#hash";
		result = formUrlProcessor.processUrl(request, url);
		assertEquals("/formAction.do?aaaa=0#hash", result);
	}
}
