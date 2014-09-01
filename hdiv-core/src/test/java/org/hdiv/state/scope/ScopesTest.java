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
package org.hdiv.state.scope;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataComposer.DataComposerMemory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.state.IState;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.util.HDIVUtil;
import org.springframework.mock.web.MockHttpServletRequest;

public class ScopesTest extends AbstractHDIVTestCase {

	private LinkUrlProcessor linkUrlProcessor;

	private DataComposerFactory dataComposerFactory;

	private StateScopeManager stateScopeManager;

	protected void onSetUp() throws Exception {

		this.linkUrlProcessor = this.getApplicationContext().getBean(LinkUrlProcessor.class);
		this.dataComposerFactory = this.getApplicationContext().getBean(DataComposerFactory.class);
		this.stateScopeManager = this.getApplicationContext().getBean(StateScopeManager.class);
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

	public void testScopedPage() {

		MockHttpServletRequest request = (MockHttpServletRequest) HDIVUtil.getHttpServletRequest();
		// Put a uri that is configured as a scoped page
		request.setRequestURI("/scopedPage/user.html");
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);
		assertTrue(dataComposer instanceof DataComposerMemory);

		dataComposer.startPage();
		dataComposer.beginRequest("POST", "test.do");
		dataComposer.compose("test.do", "parameter1", "2", false);
		dataComposer.compose("test.do", "parameter1", "2", false);
		String stateId = dataComposer.endRequest();

		assertTrue(stateId.startsWith("U-"));

		StateScope scope = this.stateScopeManager.getStateScope(stateId);
		assertEquals("user-session", scope.getScopeName());
		int id = Integer.parseInt(stateId.substring(stateId.indexOf("-") + 1, stateId.indexOf("-") + 2));
		IState state = scope.restoreState(id);
		assertEquals("test.do", state.getAction());

	}

	private String getState(String url) {

		return url.substring(url.indexOf("_HDIV_STATE_") + 13);
	}

}
