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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.Assert;
import org.junit.Test;

public class PathTest {

	@Test
	public void springSuggestPathTest() throws Exception {

		Path.registerInvocationMethodProvider(new DefaultInvocationMethodProvider());

		SuggestObjectWrapper<String> suggest = new SuggestObjectWrapperImpl<String>("name");
		Assert.assertEquals("name", suggest.getSvalue());
	}

	@Test
	public void springSuggestNonProviderPathTest() throws Exception {

		SuggestObjectWrapper<String> suggest = new SuggestObjectWrapperImpl<String>("name");
		Assert.assertEquals("name", suggest.getSvalue());
	}

	@Test
	public void nonSpringSuggestPathTest() throws Exception {

		setStaticParamValue("USING_SPRING", false);

		Path.registerInvocationMethodProvider(new DefaultInvocationMethodProvider());

		SuggestObjectWrapperImpl<String> suggest = new SuggestObjectWrapperImpl<String>("name");
		Assert.assertEquals("name", suggest.getSvalue());
	}

	@Test
	public void nonSpringSuggestNonProviderPathTest() throws Exception {

		setStaticParamValue("USING_SPRING", false);

		SuggestObjectWrapper<String> suggest = new SuggestObjectWrapperImpl<String>("name");
		Assert.assertEquals("name", suggest.getSvalue());
	}

	@Test
	public void springPathFinalParamTest() throws Exception {

		Path.registerInvocationMethodProvider(new DefaultInvocationMethodProvider());

		Path.path(Path.on(Patheable.class).getName());
		Patheable patheable = new Patheable("name");
		Assert.assertEquals("name", patheable.getName());
	}

	@Test
	public void springPathNonFinalParamTest() throws Exception {

		Path.registerInvocationMethodProvider(new DefaultInvocationMethodProvider());

		Path.path(Path.on(Patheable.class).getSubName());
		Patheable patheable = new Patheable("name");
		Assert.assertEquals("name_", patheable.getSubName());
	}

	@Test
	public void nonSpringPathFinalParamTest() throws Exception {

		setStaticParamValue("USING_SPRING", false);
		Path.registerInvocationMethodProvider(new DefaultInvocationMethodProvider());

		Path.path(Path.on(Patheable.class).getName());
		Patheable patheable = new Patheable("name");
		Assert.assertEquals("name", patheable.getName());
	}

	@Test
	public void nonSpringPathNonFinalParamTest() throws Exception {

		setStaticParamValue("USING_SPRING", false);
		Path.registerInvocationMethodProvider(new DefaultInvocationMethodProvider());

		Path.path(Path.on(Patheable.class).getSubName());
		Patheable patheable = new Patheable("name");
		Assert.assertEquals("name_", patheable.getSubName());
	}

	@Test
	public void agentSpringPathFinalParamTest() throws Exception {

		// Path.registerInvocationMethodProvider(new DefaultInvocationMethodProvider());

		Path.path(Path.on(Patheable.class).getName());
		Patheable patheable = new Patheable("name");
		Assert.assertEquals("name", patheable.getName());
	}

	@Test
	public void agentSpringPathNonFinalParamTest() throws Exception {

		// Path.registerInvocationMethodProvider(new DefaultInvocationMethodProvider());

		Path.path(Path.on(Patheable.class).getSubName());
		Patheable patheable = new Patheable("name");
		Assert.assertEquals("name_", patheable.getSubName());
	}

	@Test
	public void agentNonSpringPathFinalParamTest() throws Exception {

		setStaticParamValue("USING_SPRING", false);
		// Path.registerInvocationMethodProvider(new DefaultInvocationMethodProvider());

		Path.path(Path.on(Patheable.class).getName());
		Patheable patheable = new Patheable("name");
		Assert.assertEquals("name", patheable.getName());
	}

	@Test
	public void agentNonSpringPathNonFinalParamTest() throws Exception {

		setStaticParamValue("USING_SPRING", false);
		// Path.registerInvocationMethodProvider(new DefaultInvocationMethodProvider());

		Path.path(Path.on(Patheable.class).getSubName());
		Patheable patheable = new Patheable("name");
		Assert.assertEquals("name_", patheable.getSubName());
	}

	private void setStaticParamValue(final String fieldName, final Object newValue) throws Exception {

		Field field = Path.class.getDeclaredField(fieldName);
		field.setAccessible(true);

		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

		field.set(null, newValue);
	}

	// @Test
	// public void beanSerializationTest() throws Exception {
	// verify(jsonGenerator).writeObject("testValue1");
	// verify(jsonGenerator).writeObject("testValue2");
	// verify(jsonGenerator, times(3)).writeFieldName(anyString());
	// verify(jsonGenerator, times(3)).writeObject(Mockito.any());
	//
	// }

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
