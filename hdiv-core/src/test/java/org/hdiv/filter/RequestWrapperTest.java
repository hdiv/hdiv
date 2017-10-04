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
package org.hdiv.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hdiv.context.RequestContext;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class RequestWrapperTest {

	public RequestWrapperTest() {
	}

	@Test
	public void testRewrite() {
		Assert.assertEquals("?personId=1",
				RequestWrapper.updateQueryString("?personId=1-88B-DBEDAFDE-10-0-BEB37C78B859D3212B19066A8E2FACB0", null, "personId", "1"));
	}

	@Test
	public void testRewrite2() {
		Assert.assertEquals("?personId=1&value=2", RequestWrapper
				.updateQueryString("?personId=1-88B-DBEDAFDE-10-0-BEB37C78B859D3212B19066A8E2FACB0&value=2", null, "personId", "1"));
	}

	@Test
	@SuppressWarnings("deprecation")
	public void protectAttributes() {

		HttpServletRequest request = new MockHttpServletRequest("GET", "/path/testAction.do");
		HttpServletResponse response = new MockHttpServletResponse();

		request = new RequestWrapper(new RequestContext(request, response));

		RequestContext ctx1 = (RequestContext) HDIVUtil.getRequestContext(request);
		Assert.assertNotNull(ctx1);

		request.setAttribute(Constants.HDIV_REQUEST_CONTEXT, null);
		RequestContext ctx2 = (RequestContext) HDIVUtil.getRequestContext(request);
		Assert.assertNotNull(ctx2);

		request.setAttribute(Constants.HDIV_REQUEST_CONTEXT, new RequestContext(request, response));
		RequestContext ctx3 = (RequestContext) HDIVUtil.getRequestContext(request);
		Assert.assertNotNull(ctx3);
		Assert.assertEquals(ctx1, ctx3);

		request.removeAttribute(Constants.HDIV_REQUEST_CONTEXT);
		RequestContext ctx4 = (RequestContext) HDIVUtil.getRequestContext(request);
		Assert.assertNotNull(ctx4);
		Assert.assertEquals(ctx1, ctx4);
	}
}
