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
package org.hdiv;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hdiv.cipher.CipherHttpTest;
import org.hdiv.dataComposer.DataComposerMemoryTest;
import org.hdiv.dataValidator.DataValidatorTest;
import org.hdiv.filter.ValidatorHelperTest;
import org.hdiv.session.StateCacheTest;
import org.hdiv.state.StateUtilTest;
import org.hdiv.urlProcessor.FormUrlProcessorTest;
import org.hdiv.urlProcessor.LinkUrlProcessorTest;
import org.hdiv.util.EncodingUtilTest;

public class AllCoreTests {

	public static Test suite() {
		
		TestSuite suite = new TestSuite("Test for org.hdiv");
		
		//$JUnit-BEGIN$
		suite.addTestSuite(CipherHttpTest.class);
		suite.addTestSuite(DataComposerMemoryTest.class);
		suite.addTestSuite(DataValidatorTest.class);
		suite.addTestSuite(ValidatorHelperTest.class);
		suite.addTestSuite(StateCacheTest.class);
		suite.addTestSuite(StateUtilTest.class);
		suite.addTestSuite(EncodingUtilTest.class);
		suite.addTestSuite(FormUrlProcessorTest.class);
		suite.addTestSuite(LinkUrlProcessorTest.class);
		
		return suite;
	}

}
