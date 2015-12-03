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
package org.hdiv.web.servlet.support;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.AbstractHDIVTestCase;

public class HdivRequestDataValueProcessorTest extends AbstractHDIVTestCase {

	private HdivRequestDataValueProcessor dataValueProcessor;

	@Override
	protected void onSetUp() throws Exception {
		this.dataValueProcessor = (HdivRequestDataValueProcessor) this.getApplicationContext().getBean(
				"requestDataValueProcessor");

	}

	public void testProcessUrl() {

		HttpServletRequest request = this.getMockRequest();
		String url = "/testAction.do";

		String result = this.dataValueProcessor.processUrl(request, url);
		assertTrue(result.contains("_HDIV_STATE_"));

	}

	public void testProcessUrlAvoid() {

		this.getConfig().setAvoidValidationInUrlsWithoutParams(true);

		HttpServletRequest request = this.getMockRequest();
		String url = "/testAction.do";

		String result = this.dataValueProcessor.processUrl(request, url);
		assertEquals(url, result);

	}

	public void testProcessAction() {

		HttpServletRequest request = this.getMockRequest();
		String action = "/testAction.do";

		String result = this.dataValueProcessor.processAction(request, action);
		// Post urls are not modified
		assertEquals(action, result);

		String val = this.dataValueProcessor.processFormFieldValue(request, "param", "value", "select");
		assertEquals("0", val);

		Map<String, String> extraParams = this.dataValueProcessor.getExtraHiddenFields(request);

		assertNotNull(extraParams);
		assertTrue(extraParams.size() > 0);
	}
	
	public void testProcessActionGetMethod() {

		HttpServletRequest request = this.getMockRequest();
		String action = "/onlyget.do"; // Is startPage only for get

		String result = this.dataValueProcessor.processAction(request, action, "GET");
		// Post urls are not modified
		assertEquals(action, result);

		String val = this.dataValueProcessor.processFormFieldValue(request, "param", "value", "select");
		assertEquals("value", val);

		Map<String, String> extraParams = this.dataValueProcessor.getExtraHiddenFields(request);

		assertNotNull(extraParams);
		assertTrue(extraParams.size() == 0);
	}

	public void testProcessActionAvoid() {

		this.getConfig().setAvoidValidationInUrlsWithoutParams(true);

		HttpServletRequest request = this.getMockRequest();
		String action = "/testAction.do";

		String result = this.dataValueProcessor.processAction(request, action);
		// Post urls are not modified
		assertEquals(action, result);

		String val = this.dataValueProcessor.processFormFieldValue(request, "param", "value", "select");
		assertEquals("0", val);

		Map<String, String> extraParams = this.dataValueProcessor.getExtraHiddenFields(request);

		assertNotNull(extraParams);
		assertTrue(extraParams.size() > 0);
	}

}
