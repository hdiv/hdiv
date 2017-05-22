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
package org.hdiv.filter;

import org.junit.Assert;
import org.junit.Test;

public class RequestWrapperTest {

	public RequestWrapperTest() {
		// TODO Auto-generated constructor stub
	}

	@Test
	public void testRewrite() {
		Assert.assertEquals("?personId=1",
				RequestWrapper.updateQueryString("?personId=1-88B-DBEDAFDE-10-0-BEB37C78B859D3212B19066A8E2FACB0", null, "personId", "1"));
	}

	@Test
	public void testRewrite2() {
		Assert.assertEquals("?personId=1&value=2", RequestWrapper
				.updateQueryString("?personId=1-88B-DBEDAFDE-10-0-BEB37C78B859D3212B19066A8E2FACB0&value=2", null, "personId", "1"));
	}

}
