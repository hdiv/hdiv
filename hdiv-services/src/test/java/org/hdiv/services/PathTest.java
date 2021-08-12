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

package org.hdiv.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PathTest {

	@Before
	public void setup() {
		Path.registerInvocationMethodProvider(new InvocationMethodProviderForTesting());
	}

	@Test
	public void springSuggestPathTest() throws Exception {

		SuggestObjectWrapper suggest = new SuggestObjectWrapper("name");
		Assert.assertEquals("name", suggest.getSvalue());
		Assert.assertEquals("svalue", SuggestObjectWrapper.ID);
	}

	@Test
	public void springPathFinalParamTest() throws Exception {

		String pathId = Path.path(Path.on(Patheable.class).getName());
		Patheable patheable = new Patheable("id");
		Assert.assertEquals("id", patheable.getName());
		Assert.assertEquals("name", pathId);

	}

	@Test
	public void springPathNonFinalParamTest() throws Exception {

		String pathId = Path.path(Path.on(Patheable.class).getSubName());
		Patheable patheable = new Patheable("id");
		Assert.assertEquals("id_", patheable.getSubName().getName());
		Assert.assertEquals("subName", pathId);
	}

	public class Patheable {

		private final String name;

		private final SubClass subName;

		public Patheable(final String name) {
			this.name = name;
			subName = new SubClass(name + "_");
		}

		public String getName() {
			return name;
		}

		public SubClass getSubName() {
			return subName;
		}

	}

	public class SubClass {

		String name;

		public SubClass(final String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}

}
