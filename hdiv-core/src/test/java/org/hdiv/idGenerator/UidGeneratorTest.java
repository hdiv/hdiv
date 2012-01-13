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
package org.hdiv.idGenerator;

import java.io.Serializable;

import org.hdiv.AbstractHDIVTestCase;

public class UidGeneratorTest extends AbstractHDIVTestCase {

	private UidGenerator uidGenerator;

	protected void onSetUp() throws Exception {
		this.uidGenerator = (UidGenerator) super.getApplicationContext().getBean(UidGenerator.class);
	}

	public void testUidGeneration() {
		Serializable id = this.uidGenerator.generateUid();
		assertNotNull(id);

	}

}
