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
package org.hdiv.logs;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.util.HDIVUtil;

public class UserDataTest extends AbstractHDIVTestCase {

	private IUserData userData;

	protected void onSetUp() throws Exception {
		this.userData = super.getApplicationContext().getBean(IUserData.class);
	}

	public void testDefault() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();

		String username = userData.getUsername(request);

		assertNotNull(username);
		// default result expected
		assertEquals("anonymous", username);

	}

	public void testPrincipal() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		HttpServletRequest mockResquest = new HttpServletRequestWrapper(request) {
			@Override
			public Principal getUserPrincipal() {
				Principal principal = new Principal() {

					public String getName() {
						return "test-user";
					}
				};
				return principal;
			}
		};

		String username = userData.getUsername(mockResquest);

		assertNotNull(username);
		// default result expected
		assertEquals("test-user", username);

	}

}
