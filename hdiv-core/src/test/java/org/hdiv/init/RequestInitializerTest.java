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
package org.hdiv.init;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.exception.HDIVException;
import org.hdiv.filter.RequestWrapper;
import org.hdiv.filter.ResponseWrapper;
import org.hdiv.util.HDIVUtil;
import org.springframework.mock.web.MockHttpServletResponse;

public class RequestInitializerTest extends AbstractHDIVTestCase {

	private RequestInitializer requestInitializer;

	protected void onSetUp() throws Exception {
		this.requestInitializer = this.getApplicationContext().getBean(RequestInitializer.class);
	}

	public void testCreateRequestWrapper() {

		HttpServletRequest request = this.getMockRequest();
		HttpServletResponse response = this.getMockResponse();

		RequestWrapper wrapper = this.requestInitializer.createRequestWrapper(request, response);

		assertNotNull(wrapper);
	}

	public void testCreateResponseWrapper() {

		HttpServletRequest request = this.getMockRequest();
		HttpServletResponse response = this.getMockResponse();

		ResponseWrapper wrapper = this.requestInitializer.createResponseWrapper(request, response);

		assertNotNull(wrapper);
	}

	public void testInitRequest() {

		HttpServletRequest request = this.getMockRequest();
		HttpServletResponse response = new MockHttpServletResponse();

		this.requestInitializer.initRequest(request, response);

		assertNotNull(HDIVUtil.getRequestURI(request));
	}

	public void testEndRequest() {

		HttpServletRequest request = this.getMockRequest();
		HttpServletResponse response = new MockHttpServletResponse();
		try {
			this.requestInitializer.endRequest(request, response);
		} catch (HDIVException e) {
			assertTrue(true);
		}

	}
}
