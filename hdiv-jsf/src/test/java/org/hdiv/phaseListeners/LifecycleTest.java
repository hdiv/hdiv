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
package org.hdiv.phaseListeners;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.servlet.http.HttpServletRequest;

import org.hdiv.AbstractJsfHDIVTestCase;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.util.HDIVUtil;
import org.hdiv.util.Method;
import org.springframework.mock.web.MockHttpServletRequest;

public class LifecycleTest extends AbstractJsfHDIVTestCase {

	private IDataComposer dataComposer;

	private String hdivParameter;

	private final String targetName = "/path/testAction.do";

	@Override
	protected void innerSetUp() throws Exception {

		hdivParameter = getConfig().getStateParameterName();

		DataComposerFactory dataComposerFactory = getApplicationContext().getBean(DataComposerFactory.class);
		HttpServletRequest request = getMockRequest();
		dataComposer = dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

	}

	public void testRequestWithoutState() {

		try {
			// There is not Hdiv State, so a redirect to errorPage is executed.
			// MockExternalContext throws an UnsupportedOperationException on redirect

			// Run PhaseaListeners
			runLifecycle();

			assertFalse(false);
		}
		catch (UnsupportedOperationException e) {
			assertTrue(true);
		}

	}

	public void testCorrectRequest() {

		// Create state
		MockHttpServletRequest request = getMockRequest();

		dataComposer.startPage();

		dataComposer.beginRequest(Method.GET, targetName);
		String pageState = dataComposer.endRequest();
		dataComposer.endPage();

		request.addParameter(hdivParameter, pageState);

		// Run PhaseaListeners
		runLifecycle();

		assertTrue(true);
	}

	private void runLifecycle() {

		// RESTORE_VIEW phase
		PhaseEvent event = new PhaseEvent(shaleMockObjects.getFacesContext(), PhaseId.RESTORE_VIEW,
				shaleMockObjects.getLifecycle());

		ConfigPhaseListener conf = new ConfigPhaseListener();
		conf.beforePhase(event);

		// PROCESS_VALIDATIONS phase
		ComponentMessagesPhaseListener msg = new ComponentMessagesPhaseListener();
		event = new PhaseEvent(shaleMockObjects.getFacesContext(), PhaseId.PROCESS_VALIDATIONS,
				shaleMockObjects.getLifecycle());
		msg.beforePhase(event);
		msg.afterPhase(event);

		// RENDER_RESPONSE phase
		event = new PhaseEvent(shaleMockObjects.getFacesContext(), PhaseId.RENDER_RESPONSE,
				shaleMockObjects.getLifecycle());
		conf.afterPhase(event);
	}

}
