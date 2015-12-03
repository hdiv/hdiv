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
package org.hdiv.urlProcessor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.util.Constants;

public class BasicUrlProcessorTest extends AbstractHDIVTestCase {

	private BasicUrlProcessor urlProcessor;

	protected void onSetUp() throws Exception {
		this.urlProcessor = this.getApplicationContext().getBean(BasicUrlProcessor.class);
	}

	public void testProcessAction() {

		HttpServletRequest request = this.getMockRequest();
		String url = "/testAction.do?par1=val1&par2=val2";

		BasicUrlData result = this.urlProcessor.processUrl(request, url);

		assertEquals(result.getContextPathRelativeUrl(), "/testAction.do");
		assertEquals(2, result.getUrlParams().size());
		assertEquals("val1", result.getUrlParams().get("par1")[0]);
		assertEquals("val2", result.getUrlParams().get("par2")[0]);
	}

	public void testOnlyParams() {

		HttpServletRequest request = this.getMockRequest();
		String url = "?par1=val1&par2=val2";

		BasicUrlData result = this.urlProcessor.processUrl(request, url);

		assertEquals(result.getContextPathRelativeUrl(), "");
		assertEquals(2, result.getUrlParams().size());
		assertEquals("val1", result.getUrlParams().get("par1")[0]);
		assertEquals("val2", result.getUrlParams().get("par2")[0]);
	}

	public void testParamValues() throws UnsupportedEncodingException {

		String par1 = URLEncoder.encode("1111", Constants.ENCODING_UTF_8);
		String par2 = URLEncoder.encode("You & Me", Constants.ENCODING_UTF_8);

		HttpServletRequest request = this.getMockRequest();
		String url = "?par1=" + par1 + "&amp;par2=" + par2;

		BasicUrlData result = this.urlProcessor.processUrl(request, url);

		assertEquals(result.getContextPathRelativeUrl(), "");
		assertEquals(2, result.getUrlParams().size());
		assertEquals("1111", result.getUrlParams().get("par1")[0]);
		assertEquals("You & Me", result.getUrlParams().get("par2")[0]);

	}
}
