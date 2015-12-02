/**
 * Copyright 2005-2015 hdiv.org
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
package org.hdiv.validator;

import java.util.List;

import org.hdiv.AbstractHDIVTestCase;

public class ValidationRepositoryTest extends AbstractHDIVTestCase {

	private ValidationRepository validationRepository;

	protected void onSetUp() throws Exception {

		this.validationRepository = this.getApplicationContext().getBean(ValidationRepository.class);
	}

	public void testFind() {

		String url = "/home";
		String parameter = "param";
		List<IValidation> vals = this.validationRepository.findValidations(url, parameter);

		assertEquals(8, vals.size());

		IValidation validation = vals.get(0);
		String name = validation.getName();

		assertEquals("safeText", name);
	}

	public void testDefaultValidations() {

		List<IValidation> vals = this.validationRepository.findDefaultValidations();

		assertEquals(8, vals.size());

		IValidation validation = vals.get(0);
		String name = validation.getName();

		assertEquals("safeText", name);
	}

}
