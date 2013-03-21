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
package org.hdiv.state;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.config.HDIVConfig;
import org.hdiv.util.EncodingUtil;

/**
 * Unit tests for the <code>org.hdiv.state.StateUtil</code> class.
 * 
 * @author Gorka Vicente
 */
public class StateUtilTest extends AbstractHDIVTestCase {

	private EncodingUtil encodingUtil;

	protected void postCreateHdivConfig(HDIVConfig config) {
		config.setStrategy("cipher");
	}

	protected void onSetUp() throws Exception {

		this.encodingUtil = (EncodingUtil) this.getApplicationContext().getBean(EncodingUtil.class);
	}

	/*
	 * Test method for 'org.hdiv.state.StateUtil.encode64Cipher(Object)'
	 */
	public void testEncode64() {

		IState state = new State();

		state.setAction("action1");
		Parameter parameter = new Parameter();
		parameter.setName("parameter1");
		parameter.addValue("value1");
		parameter.addValue("value2");

		state.addParameter("parameter1", parameter);
		state.addParameter("parameter12", parameter);
		state.addParameter("parameter12", parameter);

		String data = encodingUtil.encode64Cipher(state);
		State obj = (State) encodingUtil.decode64Cipher(data);

		assertEquals(obj.getAction(), state.getAction());
	}

}
