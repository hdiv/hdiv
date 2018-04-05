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
package org.hdiv.init;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.context.RequestContext;
import org.hdiv.context.RequestContextFactory;
import org.hdiv.exception.HDIVException;
import org.hdiv.filter.RequestWrapper;
import org.hdiv.filter.ResponseWrapper;
import org.hdiv.util.HDIVUtil;
import org.springframework.mock.web.MockHttpServletResponse;

public class RequestInitializerTest extends AbstractHDIVTestCase {

	private RequestInitializer requestInitializer;

	private RequestContextFactory contextFactory;

	@Override
	protected void onSetUp() throws Exception {
		requestInitializer = getApplicationContext().getBean(RequestInitializer.class);
	}

	public void testCreateRequestWrapper() {

		HttpServletRequest request = getMockRequest();
		HttpServletResponse response = getMockResponse();
		HttpSession httpSession = request.getSession();
		ServletContext servletContext = httpSession.getServletContext();

		RequestWrapper wrapper = requestInitializer.createRequestWrapper(new RequestContext(request, response, servletContext));

		assertNotNull(wrapper);
	}

	public void testCreateResponseWrapper() {

		HttpServletRequest request = getMockRequest();
		HttpServletResponse response = getMockResponse();
		HttpSession httpSession = request.getSession();
		ServletContext servletContext = httpSession.getServletContext();

		ResponseWrapper wrapper = requestInitializer.createResponseWrapper(new RequestContext(request, response, servletContext));

		assertNotNull(wrapper);
	}

	public void testInitRequest() {

		HttpServletRequest request = getMockRequest();
		HttpServletResponse response = new MockHttpServletResponse();
		HttpSession httpSession = request.getSession();
		ServletContext servletContext = httpSession.getServletContext();

		requestInitializer.initRequest(new RequestContext(request, response, servletContext));

		assertNotNull(HDIVUtil.getRequestURI(request));
	}

	public void testEndRequest() {

		HttpServletRequest request = getMockRequest();
		HttpServletResponse response = new MockHttpServletResponse();
		HttpSession httpSession = request.getSession();
		ServletContext servletContext = httpSession.getServletContext();
		try {
			requestInitializer.endRequest(new RequestContext(request, response, servletContext));
		}
		catch (HDIVException e) {
			assertTrue(true);
		}

	}
}
