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
import org.hdiv.util.HDIVUtil;

public class LinkUrlProcessorTest extends AbstractHDIVTestCase {

	private LinkUrlProcessor linkUrlProcessor;

	protected void onSetUp() throws Exception {
		this.linkUrlProcessor = (LinkUrlProcessor) this.getApplicationContext().getBean("linkUrlProcessor");

	}

	public void testProcessAction() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		String url = "/testAction.do";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.startsWith("/testAction.do?_HDIV_STATE_="));
	}

	public void testProcessActionWithAnchor() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		String url = "/testAction.do#anchor";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.startsWith("/testAction.do?_HDIV_STATE_="));
		assertTrue(result.endsWith("#anchor"));
	}

	public void testProcessActionWithParams() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		String url = "/testAction.do?params=value";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.startsWith("/testAction.do?params=0&_HDIV_STATE_"));
	}

	public void testProcessActionParamWithoutValue() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		String url = "/testAction.do?params";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.startsWith("/testAction.do?params=0&_HDIV_STATE_"));
	}

	public void testProcessActionRelative() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		String url = "testAction.do";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.startsWith("/path/testAction.do?_HDIV_STATE_="));
	}

	public void testProcessActionRelative2() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		String url = "../testAction.do";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.startsWith("/testAction.do?_HDIV_STATE_="));
	}

	public void testStripSession() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		String url = "/app/list.do;jsessionid=AAAAAA?_HDIV_STATE_=14-2-8AB072360ABD8A2B2FBC484B0BC61BA4";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.indexOf("jsessionid") < 0);
	}

}
