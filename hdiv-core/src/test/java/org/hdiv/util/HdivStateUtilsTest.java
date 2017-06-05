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
package org.hdiv.util;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

public class HdivStateUtilsTest {

	public HdivStateUtilsTest() {
		// TODO Auto-generated constructor stub
	}

	@Test
	public void testEncodeDecodeUUID() {
		UUID id = new UUID(0, 1);
		String result = HDIVStateUtils.uuidToString(id);
		Assert.assertEquals("1", result);
		Assert.assertEquals(id, HDIVStateUtils.parsePageId(result));

		id = new UUID(1, 1);
		result = HDIVStateUtils.uuidToString(id);
		Assert.assertEquals("U00000000000000010000000000000001", result);
		Assert.assertEquals(id, HDIVStateUtils.parsePageId(result));
	}

}
