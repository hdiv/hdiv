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
package org.hdiv.dataComposer;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.util.HDIVUtil;
import org.springframework.mock.web.MockHttpServletRequest;

public class DataComposerFactoryTest extends AbstractHDIVTestCase {

	private DataComposerFactory dataComposerFactory;

	protected void onSetUp() throws Exception {

		this.dataComposerFactory = this.getApplicationContext().getBean(DataComposerFactory.class);
	}

	public void testNewInstance() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);

		assertTrue(dataComposer instanceof DataComposerMemory);
	}

	public void testNewInstanceAjax() {

		MockHttpServletRequest request = (MockHttpServletRequest) HDIVUtil.getHttpServletRequest();

		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);

		dataComposer.beginRequest("GET", "/ajax");
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		// Create other instance
		request.addParameter("_HDIV_STATE_", stateId);
		request.addHeader("x-requested-with", "XMLHttpRequest");
		this.getConfig().setReuseExistingPageInAjaxRequest(true);

		dataComposer = this.dataComposerFactory.newInstance(request);

		dataComposer.beginRequest("GET", "/ajax");
		String stateId2 = dataComposer.endRequest();
		dataComposer.endPage();

		assertEquals(getPageId(stateId), getPageId(stateId2));
	}

	public void testNewInstanceAjaxNoParameter() {

		MockHttpServletRequest request = (MockHttpServletRequest) HDIVUtil.getHttpServletRequest();

		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);

		dataComposer.beginRequest("GET", "/ajax");
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		// Create other instance
		request.addHeader("x-requested-with", "XMLHttpRequest");
		this.getConfig().setReuseExistingPageInAjaxRequest(true);

		dataComposer = this.dataComposerFactory.newInstance(request);

		dataComposer.beginRequest("GET", "/ajax");
		String stateId2 = dataComposer.endRequest();
		dataComposer.endPage();

		assertFalse(getPageId(stateId) == getPageId(stateId2));
	}

	protected String getPageId(String stateId) {

		return stateId.substring(0, stateId.indexOf("-"));
	}

}
