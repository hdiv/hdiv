/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hdiv.dataComposer;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.config.HDIVConfig;
import org.hdiv.state.IState;
import org.hdiv.state.StateUtil;

/**
 * Unit tests for the <code>org.hdiv.composer.DataComposerMemory</code> class.
 * 
 * @author Gorka Vicente
 */
public class DataComposerHashTest extends AbstractHDIVTestCase {

	private DataComposerFactory dataComposerFactory;

	private StateUtil stateUtil;

	/*
	 * @see TestCase#setUp()
	 */
	protected void onSetUp() throws Exception {

		this.dataComposerFactory = (DataComposerFactory) this.getApplicationContext().getBean("dataComposerFactory");
		HDIVConfig config = this.getConfig();
		config.setStrategy("hash");
		this.dataComposerFactory.setHdivConfig(config);
		this.stateUtil = (StateUtil) this.getApplicationContext().getBean("stateUtil");
	}

	/**
	 * @see DataComposerMamory#compose(String, String, String, boolean)
	 */
	public void testComposeSimple() {

		IDataComposer dataComposer = this.dataComposerFactory.newInstance();

		dataComposer.startPage();
		dataComposer.beginRequest("test.do");

		boolean confidentiality = this.getConfig().getConfidentiality().booleanValue();

		// we add a multiple parameter that will be encoded as 0, 1, 2, ...
		String result = dataComposer.compose("test.do", "parameter1", "2", false);
		String value = (!confidentiality) ? "2" : "0";
		assertTrue(value.equals(result));

		result = dataComposer.compose("test.do", "parameter1", "2", false);
		value = (!confidentiality) ? "2" : "1";
		assertTrue(value.equals(result));

		result = dataComposer.compose("test.do", "parameter1", "2", false);
		assertTrue("2".equals(result));

		result = dataComposer.compose("test.do", "parameter2", "2", false);
		value = (!confidentiality) ? "2" : "0";
		assertTrue(value.equals(result));

		result = dataComposer.compose("test.do", "parameter2", "2", false);
		value = (!confidentiality) ? "2" : "1";
		assertTrue(value.equals(result));
	}

	public void testComposeAndRestore() {

		IDataComposer dataComposer = this.dataComposerFactory.newInstance();

		dataComposer.startPage();
		dataComposer.beginRequest("test.do");
		dataComposer.compose("parameter1", "2", false);
		String stateId = dataComposer.endRequest();
		dataComposer.endPage();

		assertNotNull(stateId);

		IState state = this.stateUtil.restoreState(stateId);

		assertEquals("test.do", state.getAction());
	}

}
