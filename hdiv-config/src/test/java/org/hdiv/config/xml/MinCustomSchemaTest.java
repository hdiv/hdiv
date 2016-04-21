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
package org.hdiv.config.xml;

import junit.framework.TestCase;

import org.hdiv.config.HDIVConfig;
import org.hdiv.validator.EditableDataValidationProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MinCustomSchemaTest extends TestCase {

	private ApplicationContext context;

	@Override
	protected void setUp() throws Exception {

		this.context = new ClassPathXmlApplicationContext("org/hdiv/config/xml/hdiv-config-test-schema-min.xml");
	}

	public void testSchema() {

		HDIVConfig hdivConfig = this.context.getBean(HDIVConfig.class);
		assertNotNull(hdivConfig);
		assertFalse(hdivConfig.isDebugMode());

		EditableDataValidationProvider provider = this.context.getBean(EditableDataValidationProvider.class);
		assertNotNull(provider);
		System.out.println(provider.toString());
	}

}
