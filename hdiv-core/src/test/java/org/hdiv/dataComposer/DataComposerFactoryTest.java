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
package org.hdiv.dataComposer;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.util.Method;
import org.springframework.mock.web.MockHttpServletRequest;

public class DataComposerFactoryTest extends AbstractHDIVTestCase {

	private DataComposerFactory dataComposerFactory;

	@Override
	protected void onSetUp() throws Exception {

		dataComposerFactory = getApplicationContext().getBean(DataComposerFactory.class);
	}

	public void testNewInstance() {

		HttpServletRequest request = getMockRequest();
		IDataComposer dataComposer = dataComposerFactory.newInstance(request);

		assertTrue(dataComposer instanceof DataComposerMemory);
	}

	public void testNewInstanceAjax() {

		MockHttpServletRequest request = getMockRequest();

		IDataComposer dataComposer = dataComposerFactory.newInstance(request);

		dataComposer.beginRequest(Method.GET, "/ajax");
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		// Create other instance
		request.addParameter("_HDIV_STATE_", stateId);
		request.addHeader("x-requested-with", "XMLHttpRequest");
		getConfig().setReuseExistingPageInAjaxRequest(true);

		dataComposer = dataComposerFactory.newInstance(request);

		dataComposer.beginRequest(Method.GET, "/ajax");
		String stateId2 = dataComposer.endRequest();
		dataComposer.endPage();

		assertEquals(getPageId(stateId), getPageId(stateId2));
	}

	public void testNewInstanceAjaxNoParameter() {

		MockHttpServletRequest request = getMockRequest();

		IDataComposer dataComposer = dataComposerFactory.newInstance(request);

		dataComposer.beginRequest(Method.GET, "/ajax");
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		// Create other instance
		request.addHeader("x-requested-with", "XMLHttpRequest");
		getConfig().setReuseExistingPageInAjaxRequest(true);

		dataComposer = dataComposerFactory.newInstance(request);

		dataComposer.beginRequest(Method.GET, "/ajax");
		String stateId2 = dataComposer.endRequest();
		dataComposer.endPage();

		assertFalse(getPageId(stateId) == getPageId(stateId2));
	}

	public void testNewInstancePjax() {

		MockHttpServletRequest request = getMockRequest();

		IDataComposer dataComposer = dataComposerFactory.newInstance(request);

		dataComposer.beginRequest(Method.GET, "/ajax");
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		// Create other instance
		request.addParameter("_HDIV_STATE_", stateId);
		request.addHeader("x-requested-with", "XMLHttpRequest");
		request.addHeader("X-PJAX", "");
		getConfig().setReuseExistingPageInAjaxRequest(true);

		dataComposer = dataComposerFactory.newInstance(request);

		dataComposer.beginRequest(Method.GET, "/ajax");
		String stateId2 = dataComposer.endRequest();
		dataComposer.endPage();

		// Next page id is expected
		assertEquals(getPageId(stateId), Integer.parseInt(getPageId(stateId2)) - 1 + "");
	}

	protected String getPageId(String stateId) {

		return stateId.substring(0, stateId.indexOf("-"));
	}

}
