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
package org.hdiv.web.servlet.support;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.context.RequestContext;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.state.IParameter;
import org.hdiv.state.IState;
import org.hdiv.state.StateUtil;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;

public class ThymeleafHdivRequestDataValueProcessorTest extends AbstractHDIVTestCase {

	private ThymeleafHdivRequestDataValueProcessor dataValueProcessor;

	private DataComposerFactory dataComposerFactory;

	private StateUtil stateUtil;

	@Override
	protected void onSetUp() throws Exception {

		this.dataValueProcessor = this.getApplicationContext().getBean(ThymeleafHdivRequestDataValueProcessor.class);
		this.dataComposerFactory = this.getApplicationContext().getBean(DataComposerFactory.class);
		this.stateUtil = this.getApplicationContext().getBean(StateUtil.class);
	}

	public void testProcessUrl() {

		HttpServletRequest request = this.getMockRequest();
		String url = "/testAction.do";

		String result = this.dataValueProcessor.processUrl(request, url);
		assertTrue(result.contains("_HDIV_STATE_"));

	}

	public void testProcessUrlAvoid() {

		this.getConfig().setAvoidValidationInUrlsWithoutParams(true);

		HttpServletRequest request = this.getMockRequest();
		String url = "/testAction.do";

		String result = this.dataValueProcessor.processUrl(request, url);
		assertEquals(url, result);

	}

	public void testProcessAction() {

		HttpServletRequest request = this.getMockRequest();
		String action = "/testAction.do";

		String result = this.dataValueProcessor.processAction(request, action);
		// Post urls are not modified
		assertEquals(action, result);

		String val = this.dataValueProcessor.processFormFieldValue(request, "param", "value", "select");
		assertEquals("0", val);

		Map<String, String> extraParams = this.dataValueProcessor.getExtraHiddenFields(request);

		assertNotNull(extraParams);
		assertTrue(extraParams.size() > 0);
	}

	public void testProcessActionGetMethod() {

		HttpServletRequest request = this.getMockRequest();
		String action = "/onlyget.do"; // Is startPage only for get

		String result = this.dataValueProcessor.processAction(request, action, "GET");
		// Post urls are not modified
		assertEquals(action, result);

		String val = this.dataValueProcessor.processFormFieldValue(request, "param", "value", "select");
		assertEquals("value", val);

		Map<String, String> extraParams = this.dataValueProcessor.getExtraHiddenFields(request);

		assertNotNull(extraParams);
		assertTrue(extraParams.size() == 0);
	}

	public void testProcessActionAvoid() {

		this.getConfig().setAvoidValidationInUrlsWithoutParams(true);

		HttpServletRequest request = this.getMockRequest();
		String action = "/testAction.do";

		String result = this.dataValueProcessor.processAction(request, action);
		// Post urls are not modified
		assertEquals(action, result);

		String val = this.dataValueProcessor.processFormFieldValue(request, "param", "value", "select");
		assertEquals("0", val);

		Map<String, String> extraParams = this.dataValueProcessor.getExtraHiddenFields(request);

		assertNotNull(extraParams);
		assertTrue(extraParams.size() > 0);
	}

	public void testProcessFormThymeleafOrder() {

		HttpServletRequest request = this.getMockRequest();
		RequestContext context = this.getRequestContext();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		dataComposer.startPage();

		String action = "/testAction.do";

		// 1. the action url
		String result = this.dataValueProcessor.processAction(request, action);
		// Post urls are not modified
		assertEquals(action, result);

		// 2. Hidden field
		Map<String, String> extraParams = this.dataValueProcessor.getExtraHiddenFields(request);

		assertNotNull(extraParams);
		assertTrue(extraParams.size() == 1);
		String hdivStateParam = (String) request.getSession().getAttribute(Constants.HDIV_PARAMETER);
		String stateValue = extraParams.get(hdivStateParam);
		assertNotNull(stateValue);

		// 3. form parameters
		String val = this.dataValueProcessor.processFormFieldValue(request, "param", "value", "select");
		assertEquals("0", val);

		val = this.dataValueProcessor.processFormFieldValue(request, "param1", "value1", "text");
		assertEquals("value1", val);

		dataComposer.endPage();

		// Restore state
		IState state = stateUtil.restoreState(context, stateValue);
		assertNotNull(state);

		IParameter param = state.getParameter("param");
		List<String> values = param.getValues();
		assertTrue(values.size() == 1);

		String value = values.get(0);
		assertEquals("value", value);

	}
}
