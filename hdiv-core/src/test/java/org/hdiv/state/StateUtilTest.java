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
package org.hdiv.state;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.util.HDIVUtil;

/**
 * Unit tests for the <code>org.hdiv.state.StateUtil</code> class.
 * 
 */
public class StateUtilTest extends AbstractHDIVTestCase {

	private DataComposerFactory dataComposerFactory;

	private StateUtil stateUtil;

	protected void onSetUp() throws Exception {

		this.dataComposerFactory = this.getApplicationContext().getBean(DataComposerFactory.class);
		this.stateUtil = this.getApplicationContext().getBean(StateUtil.class);
	}

	public void testRestore() {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();
		IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		dataComposer.startPage();
		dataComposer.beginRequest("test.do");
		dataComposer.compose("parameter1", "2", false);
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		assertNotNull(stateId);

		IState restored = this.stateUtil.restoreState(stateId);

		assertNotNull(restored);
		assertEquals(restored.getAction(), "test.do");
		assertEquals(restored.getParameter("parameter1").getValues().get(0), "2");
	}
}
