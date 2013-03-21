package org.hdiv.urlProcessor;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.config.HDIVConfig;
import org.hdiv.util.HDIVUtil;

public class RandomNameTest extends AbstractHDIVTestCase {

	private LinkUrlProcessor linkUrlProcessor;

	protected void postCreateHdivConfig(HDIVConfig config) {
		config.setRandomName(true);
	}

	protected void onSetUp() throws Exception {

		this.linkUrlProcessor = (LinkUrlProcessor) this.getApplicationContext().getBean(LinkUrlProcessor.class);
	}

	public void testProcessAction() {

		this.getConfig().setAvoidValidationInUrlsWithoutParams(Boolean.FALSE);

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		String url = "/testAction.do";

		String result = this.linkUrlProcessor.processUrl(request, url);

		assertTrue(result.startsWith(url));
		assertTrue(!result.contains("_HDIV_STATE_"));
	}

}
