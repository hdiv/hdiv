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
package org.hdiv.application;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.config.HDIVConfig;

public class IApplicationTest extends AbstractHDIVTestCase {

	private IApplication application;

	protected void onSetUp() throws Exception {

		this.application = this.getApplicationContext().getBean(IApplication.class);
	}

	public void testGetBean() throws Exception {

		HDIVConfig value = (HDIVConfig) this.application.getBean("config");
		assertNotNull(value);
	}

}
