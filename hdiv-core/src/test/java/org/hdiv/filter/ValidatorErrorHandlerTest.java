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

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.context.RequestContextHolder;
import org.hdiv.util.HDIVErrorCodes;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

public class ValidatorErrorHandlerTest extends AbstractHDIVTestCase {

	private ValidatorErrorHandler validatorErrorHandler;

	@Override
	protected void onSetUp() throws Exception {
		validatorErrorHandler = getApplicationContext().getBean(ValidatorErrorHandler.class);
	}

	public void testPageIncorrect() {

		RequestContextHolder ctx = getRequestContext();

		ValidatorError error = new ValidatorError(HDIVErrorCodes.INVALID_PAGE_ID);
		List<ValidatorError> errors = Collections.singletonList(error);
		validatorErrorHandler.handleValidatorError(ctx, errors);

		String redirectUrl = getMockResponse().getRedirectedUrl();

		assertEquals(getConfig().getSessionExpiredLoginPage(), redirectUrl);
	}

	public void testHandleValidatorError() {

		HttpServletRequest request = getMockRequest();
		RequestContextHolder ctx = getRequestContext();
		MockHttpSession session = (MockHttpSession) request.getSession();
		session.setNew(false); // mark as not new sesssion
		clearSession();
		MockHttpServletResponse response = getMockResponse();

		ValidatorError error = new ValidatorError(HDIVErrorCodes.NOT_RECEIVED_ALL_REQUIRED_PARAMETERS);
		List<ValidatorError> errors = Collections.singletonList(error);
		validatorErrorHandler.handleValidatorError(ctx, errors);

		String redirectUrl = response.getRedirectedUrl();

		assertEquals(getConfig().getErrorPage(), redirectUrl);
	}

	public void testDefaultErrorPage() {

		// Remove default errorPage
		getConfig().setErrorPage(null);

		RequestContextHolder ctx = getRequestContext();
		MockHttpServletResponse response = getMockResponse();

		ValidatorError error = new ValidatorError(HDIVErrorCodes.NOT_RECEIVED_ALL_REQUIRED_PARAMETERS);
		List<ValidatorError> errors = Collections.singletonList(error);
		validatorErrorHandler.handleValidatorError(ctx, errors);

		// Default Error page is generated, so no redirect URL exist
		assertNull(response.getRedirectedUrl());

		assertTrue(response.getBufferSize() > 0);

		String responseContent = null;
		try {
			responseContent = response.getContentAsString();
		}
		catch (UnsupportedEncodingException e) {
			responseContent = null;
		}
		assertNotNull(responseContent);
		assertTrue(responseContent.contains("Unauthorized access"));

	}

}
