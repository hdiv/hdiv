/*
 * Copyright 2004-2012 The Apache Software Foundation.
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
package org.hdiv.urlProcessor;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.util.HDIVUtil;

public class FormUrlProcessorTest extends AbstractHDIVTestCase {

	private FormUrlProcessor formUrlProcessor;

	private DataComposerFactory dataComposerFactory;

	protected void onSetUp() throws Exception {
		this.formUrlProcessor = this.getApplicationContext().getBean(FormUrlProcessor.class);
		this.dataComposerFactory = this.getApplicationContext().getBean(DataComposerFactory.class);
	}

	public void testProcessAction() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		String action = "/testAction.do";

		String result = this.formUrlProcessor.processUrl(request, action);

		// Post urls are not modified
		assertEquals(action, result);
	}

	public void testProcessActionGetMethod() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		String action = "/testAction.do";

		String result = this.formUrlProcessor.processUrl(request, action, "GET");

		// Post urls are not modified
		assertEquals(action, result);
	}

	public void testProcessActionWithParam() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		String action = "/testAction.do?params=value";

		String result = this.formUrlProcessor.processUrl(request, action);

		assertEquals("/testAction.do?params=0", result);
	}

	public void testProcessActionParamWithoutValue() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		String action = "/testAction.do?params";

		String result = this.formUrlProcessor.processUrl(request, action);

		assertEquals("/testAction.do?params=0", result);
	}

	public void testProcessActionComplete() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);
		dataComposer.startPage();

		String action = "/testAction.do";

		String result = this.formUrlProcessor.processUrl(request, action);

		// Post urls are not modified
		assertEquals(action, result);

		String val = dataComposer.compose("param", "value", false);
		assertEquals("0", val);

		String requestId = dataComposer.endRequest();

		assertNotNull(requestId);
		assertTrue(requestId.length() > 0);
	}

	public void testProcessActionStartPage() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();

		String action = "/testing.do?params=value";// is a startPage
		String result = this.formUrlProcessor.processUrl(request, action);
		assertEquals(action, result);

		action = "/onlyget.do?params=value"; // is a startPage only in Get requests
		result = this.formUrlProcessor.processUrl(request, action);
		assertEquals("/onlyget.do?params=0", result);

		action = "/onlypost.do?params=value"; // is a startPage only in POST requests
		result = this.formUrlProcessor.processUrl(request, action);
		assertEquals(action, result);
	}

	public void testProcessMultiValueParam() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		String url = "/testAction.do?name=X&name=Y&name=Z";

		String result = this.formUrlProcessor.processUrl(request, url);

		assertTrue(result.startsWith("/testAction.do?name=0&name=1&name=2"));

	}

	public void testProcessMultiValueParamConfidentialityFalse() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		Boolean conf = this.getConfig().getConfidentiality();
		this.getConfig().setConfidentiality(Boolean.FALSE);
		String url = "/testAction.do?name=X&name=Y&name=Z";

		String result = this.formUrlProcessor.processUrl(request, url);

		assertTrue(result.startsWith("/testAction.do?name=X&name=Y&name=Z"));

		this.getConfig().setConfidentiality(conf);
	}

	public void testProcessActionJsessionId() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		String url = "/testAction.do;jsessionid=67CFB560B6EC2677D51814A2A2B16B24";

		String result = this.formUrlProcessor.processUrl(request, url);

		assertEquals(result, url);
	}

	public void testProcessActionJsessionIdParam() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		String url = "/testAction.do;jsessionid=67CFB560B6EC2677D51814A2A2B16B24?params=0";

		String result = this.formUrlProcessor.processUrl(request, url);

		assertEquals(result, url);
	}

	public void testProcessActionJsessionStartPage() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();

		String url = "/testing.do;jsessionid=67CFB560B6EC2677D51814A2A2B16B24"; // is a startPage
		String result = this.formUrlProcessor.processUrl(request, url);
		assertEquals(url, result);

	}
}
