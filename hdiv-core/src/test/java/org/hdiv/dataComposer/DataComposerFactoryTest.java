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
package org.hdiv.dataComposer;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.context.RequestContextHolder;
import org.hdiv.util.HDIVStateUtils;
import org.hdiv.util.Method;
import org.springframework.mock.web.MockHttpServletRequest;

public class DataComposerFactoryTest extends AbstractHDIVTestCase {

	private DataComposerFactory dataComposerFactory;

	@Override
	protected void onSetUp() throws Exception {
		dataComposerFactory = getApplicationContext().getBean(DataComposerFactory.class);
	}

	public void testNewInstance() {

		RequestContextHolder context = getRequestContext();
		IDataComposer dataComposer = dataComposerFactory.newInstance(context);

		assertTrue(dataComposer instanceof DataComposerMemory);
	}

	public void testNewInstanceAjax() {

		MockHttpServletRequest request = getMockRequest();
		RequestContextHolder context = getRequestContext();
		IDataComposer dataComposer = dataComposerFactory.newInstance(context);

		dataComposer.beginRequest(Method.GET, "/ajax");
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		// Create other instance
		request.addParameter("_HDIV_STATE_", stateId);
		request.addHeader("x-requested-with", "XMLHttpRequest");
		clearAjax();
		getConfig().setReuseExistingPageInAjaxRequest(true);

		dataComposer = dataComposerFactory.newInstance(context);

		dataComposer.beginRequest(Method.GET, "/ajax");
		String stateId2 = dataComposer.endRequest();
		dataComposer.endPage();

		assertEquals(HDIVStateUtils.getPageId(stateId), HDIVStateUtils.getPageId(stateId2));
	}

	public void testNewInstanceAjaxNoParameter() {

		MockHttpServletRequest request = getMockRequest();

		RequestContextHolder context = getRequestContext();
		IDataComposer dataComposer = dataComposerFactory.newInstance(context);

		dataComposer.beginRequest(Method.GET, "/ajax");
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		// Create other instance
		request.addHeader("x-requested-with", "XMLHttpRequest");
		clearAjax();
		getConfig().setReuseExistingPageInAjaxRequest(true);

		dataComposer = dataComposerFactory.newInstance(context);

		dataComposer.beginRequest(Method.GET, "/ajax");
		String stateId2 = dataComposer.endRequest();
		dataComposer.endPage();

		assertFalse(HDIVStateUtils.getPageId(stateId) == HDIVStateUtils.getPageId(stateId2));
	}

	public void testNewInstancePjax() {

		MockHttpServletRequest request = getMockRequest();

		RequestContextHolder context = getRequestContext();
		IDataComposer dataComposer = dataComposerFactory.newInstance(context);

		dataComposer.beginRequest(Method.GET, "/ajax");
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		// Create other instance
		request.addParameter("_HDIV_STATE_", stateId);
		request.addHeader("x-requested-with", "XMLHttpRequest");
		request.addHeader("X-PJAX", "");
		clearAjax();
		getConfig().setReuseExistingPageInAjaxRequest(true);

		dataComposer = dataComposerFactory.newInstance(context);

		dataComposer.beginRequest(Method.GET, "/ajax");
		String stateId2 = dataComposer.endRequest();
		dataComposer.endPage();

		// Next page id is expected
		assertEquals(HDIVStateUtils.getPageId(stateId).getLeastSignificantBits(),
				HDIVStateUtils.getPageId(stateId2).getLeastSignificantBits() - 1);
	}

}
