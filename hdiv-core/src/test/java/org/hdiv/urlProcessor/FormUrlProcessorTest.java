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

public class FormUrlProcessorTest extends AbstractHDIVTestCase {

	private FormUrlProcessor formUrlProcessor;

	protected void onSetUp() throws Exception {
		this.formUrlProcessor = (FormUrlProcessor) this.getApplicationContext().getBean("formUrlProcessor");

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
}
