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
package org.hdiv.urlProcessor;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.util.HDIVUtil;

public class AvoidValidationInUrlsWithoutParamsTest extends AbstractHDIVTestCase {

	private LinkUrlProcessor linkUrlProcessor;

	private FormUrlProcessor formUrlProcessor;

	private DataComposerFactory dataComposerFactory;

	/*
	 * @see TestCase#setUp()
	 */
	protected void onSetUp() throws Exception {

		this.linkUrlProcessor = this.getApplicationContext().getBean(LinkUrlProcessor.class);
		this.formUrlProcessor = this.getApplicationContext().getBean(FormUrlProcessor.class);
		this.dataComposerFactory = this.getApplicationContext().getBean(DataComposerFactory.class);
	}

	/*
	 * Link processing
	 */

	public void testProcessAction() {

		this.getConfig().setAvoidValidationInUrlsWithoutParams(Boolean.FALSE);

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		String url = "/testAction.do";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.contains("_HDIV_STATE_"));
	}

	public void testProcessActionParams() {

		this.getConfig().setAvoidValidationInUrlsWithoutParams(Boolean.FALSE);

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		String url = "/testAction.do?param=1";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.contains("_HDIV_STATE_"));
	}

	public void testProcessActionAvoid() {

		this.getConfig().setAvoidValidationInUrlsWithoutParams(Boolean.TRUE);

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		String url = "/testAction.do";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertFalse(result.contains("_HDIV_STATE_"));
	}

	public void testProcessActionAvoidParams() {

		this.getConfig().setAvoidValidationInUrlsWithoutParams(Boolean.TRUE);

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		String url = "/testAction.do?param=1";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.contains("_HDIV_STATE_"));
	}

	/*
	 * Form processing. AvoidValidationInUrlsWithoutParams is ignored in forms
	 */

	public void testProcessFormAction() {

		this.getConfig().setAvoidValidationInUrlsWithoutParams(Boolean.FALSE);

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		String action = "/testAction.do";

		String result = this.formUrlProcessor.processUrl(request, action);

		// Post urls are not modified
		assertEquals(action, result);
	}

	public void testProcessFormParamAction() {

		this.getConfig().setAvoidValidationInUrlsWithoutParams(Boolean.FALSE);

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		String action = "/testAction.do?param=1";

		String result = this.formUrlProcessor.processUrl(request, action);

		// Confidenciality
		assertEquals("/testAction.do?param=0", result);
	}

	public void testProcessActionComplete() {

		this.getConfig().setAvoidValidationInUrlsWithoutParams(Boolean.TRUE);

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

}