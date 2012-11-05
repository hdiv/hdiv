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
		this.formUrlProcessor = (FormUrlProcessor) this.getApplicationContext().getBean("formUrlProcessor");
		this.dataComposerFactory = (DataComposerFactory) this.getApplicationContext().getBean("dataComposerFactory");
	}

	public void testProcessAction() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		String action = "/testAction.do";

		String result = this.formUrlProcessor.processUrl(request, action);

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

		IDataComposer dataComposer = this.dataComposerFactory.newInstance();
		dataComposer.startPage();

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
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
}
