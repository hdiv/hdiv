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
package org.hdiv.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.util.HDIVUtil;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletResponse;

public class ValidatorFilterTest extends AbstractHDIVTestCase {

	protected void onSetUp() throws Exception {

	}

	public void testFilterCreation() {
		ValidatorFilter filter = new ValidatorFilter();

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterConfig filterConfig = new MockFilterConfig(request.getSession().getServletContext(), "hdivFilter");
		FilterChain filterChain = new MockFilterChain();

		try {
			filter.init(filterConfig);

			filter.doFilter(request, response, filterChain);

			// Validation error because is not start page
			String redirectUrl = response.getRedirectedUrl();
			assertEquals(getConfig().getErrorPage(), redirectUrl);

		} catch (ServletException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

}
