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
