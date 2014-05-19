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
package org.hdiv.filter;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.exception.HDIVException;
import org.hdiv.util.HDIVUtil;
import org.springframework.mock.web.MockHttpServletResponse;

public class RequestInitializerTest extends AbstractHDIVTestCase {

	private RequestInitializer requestInitializer;

	protected void onSetUp() throws Exception {
		this.requestInitializer = this.getApplicationContext().getBean(RequestInitializer.class);
	}

	public void testCreateRequestWrapper() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();

		RequestWrapper wrapper = this.requestInitializer.createRequestWrapper(request);

		assertNotNull(wrapper);
	}

	public void testCreateResponseWrapper() {

		MockHttpServletResponse response = new MockHttpServletResponse();

		ResponseWrapper wrapper = this.requestInitializer.createResponseWrapper(response);

		assertNotNull(wrapper);
	}

	public void testInitRequest() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();

		this.requestInitializer.initRequest(request);

		assertNotNull(HDIVUtil.getHttpServletRequest());
	}

	public void testEndRequest() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		try {
			this.requestInitializer.endRequest(request);
		} catch (HDIVException e) {
			assertTrue(true);
		}

	}
}
