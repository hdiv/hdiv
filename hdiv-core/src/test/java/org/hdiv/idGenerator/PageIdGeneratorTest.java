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
package org.hdiv.idGenerator;

import org.hdiv.AbstractHDIVTestCase;

public class PageIdGeneratorTest extends AbstractHDIVTestCase {

	private PageIdGenerator pageIdGenerator;

	protected void onSetUp() throws Exception {
		this.pageIdGenerator = super.getApplicationContext().getBean(PageIdGenerator.class);
	}

	public void testPageIdGenerator() {
		int id = this.pageIdGenerator.getNextPageId();
		assertNotNull(id);
		assertTrue(id > 0);

		int id2 = this.pageIdGenerator.getNextPageId();
		assertNotNull(id2);
		assertTrue(id2 > 0);

		assertFalse(id == id2);

	}

}
