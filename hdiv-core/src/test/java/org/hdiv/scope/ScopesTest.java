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
package org.hdiv.scope;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.util.HDIVUtil;

public class ScopesTest extends AbstractHDIVTestCase {

	private LinkUrlProcessor linkUrlProcessor;

	protected void onSetUp() throws Exception {

		this.linkUrlProcessor = this.getApplicationContext().getBean(LinkUrlProcessor.class);
	}

	public void testScopeDifferent() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		String url = "/testAction.do";
		String url2 = "/otherAction.do";

		IDataComposer dataComposer = HDIVUtil.getDataComposer(request);
		dataComposer.startScope("app");

		String result1 = this.linkUrlProcessor.processUrl(request, url);

		String result2 = this.linkUrlProcessor.processUrl(request, url2);

		// States are different
		assertFalse(getState(result1).equals(getState(result2)));
	}

	public void testScopeSame() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		String url = "/testAction.do";

		IDataComposer dataComposer = HDIVUtil.getDataComposer(request);
		dataComposer.startScope("app");

		String result1 = this.linkUrlProcessor.processUrl(request, url);

		String result2 = this.linkUrlProcessor.processUrl(request, url);

		// States are equal
		assertTrue(getState(result1).equals(getState(result2)));
	}

	public void testScopeDifferentParams() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		String url = "/testAction.do?param=value";
		String url2 = "/testAction.do?other=value";

		IDataComposer dataComposer = HDIVUtil.getDataComposer(request);
		dataComposer.startScope("app");

		String result1 = this.linkUrlProcessor.processUrl(request, url);

		String result2 = this.linkUrlProcessor.processUrl(request, url2);

		// States are different
		assertFalse(getState(result1).equals(getState(result2)));
	}

	public void testScopeSameParams() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		String url = "/testAction.do?param=value";

		IDataComposer dataComposer = HDIVUtil.getDataComposer(request);
		dataComposer.startScope("app");

		String result1 = this.linkUrlProcessor.processUrl(request, url);

		String result2 = this.linkUrlProcessor.processUrl(request, url);

		// States are equal
		assertTrue(getState(result1).equals(getState(result2)));
	}

	private String getState(String url) {

		return url.substring(url.indexOf("_HDIV_STATE_") + 13);
	}

}
