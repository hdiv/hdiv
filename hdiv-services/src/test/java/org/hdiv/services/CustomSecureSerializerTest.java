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

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

public class CustomSecureSerializerTest {

	private CustomSecureSerializer serializer;

	private JsonGenerator jsonGenerator;

	@Before
	public void init() throws Exception {
		serializer = new CustomSecureSerializer() {
			@Override
			protected void writeBody(final Object obj) {
				BeanWrapper beanWrapper = new BeanWrapperImpl(obj);

				for (PropertyDescriptor prop : Arrays.asList(beanWrapper.getPropertyDescriptors())) {

					String propertyName = prop.getName();

					if ("class".equals(propertyName)) {
						continue;
					}

					if (beanWrapper.isReadableProperty(propertyName)) {
						try {
							writeField(beanWrapper, propertyName, false);
						}
						catch (IOException e) {
							throw new RuntimeException();
						}
					}
				}
			}

		};

		jsonGenerator = mock(JsonGenerator.class);

		serializer.setDelegatedSerializer(new CustomContextualSerializer(jsonGenerator));

	}

	@Test
	public void emptyBeanSerializationTest() throws Exception {
		serializer.serialize(new TestBean(), jsonGenerator, null);
		verify(jsonGenerator).writeFieldName("id");
		verify(jsonGenerator).writeFieldName("otherAttr");
		verify(jsonGenerator).writeFieldName("otherAttr2");
		verify(jsonGenerator, times(3)).writeFieldName(anyString());
		verify(jsonGenerator, times(3)).writeObject(null);
	}

	@Test
	public void beanSerializationTest() throws Exception {
		serializer.serialize(new TestBean(1L, "testValue1", "testValue2"), jsonGenerator, null);
		verify(jsonGenerator).writeFieldName("otherAttr");
		verify(jsonGenerator).writeFieldName("otherAttr2");
		verify(jsonGenerator).writeObject(1L);
		verify(jsonGenerator).writeObject("testValue1");
		verify(jsonGenerator).writeObject("testValue2");
		verify(jsonGenerator, times(3)).writeFieldName(anyString());
		verify(jsonGenerator, times(3)).writeObject(Mockito.any());

	}

	@Test
	public void emptySecureIdentifiableSerializationTest() throws Exception {
		serializer.serialize(new SecuredTestBean(), jsonGenerator, null);
		verify(jsonGenerator, times(1)).writeFieldName("isSecure");
		verify(jsonGenerator, times(1)).writeFieldName("id");
		verify(jsonGenerator).writeFieldName("otherAttr");
		verify(jsonGenerator).writeFieldName("otherAttr2");
		verify(jsonGenerator, times(4)).writeFieldName(anyString());
		verify(jsonGenerator, times(3)).writeObject(null);
		verify(jsonGenerator, times(1)).writeObject(true);
	}

	@Test
	public void secureIdentifiableSerializationTest() throws Exception {
		serializer.serialize(new SecuredTestBean(1L, "testValue1", "testValue2"), jsonGenerator, null);
		verify(jsonGenerator, times(1)).writeFieldName("isSecure");
		verify(jsonGenerator, times(1)).writeFieldName("id");
		verify(jsonGenerator).writeFieldName("otherAttr");
		verify(jsonGenerator).writeFieldName("otherAttr2");
		verify(jsonGenerator, times(1)).writeObject(true);
		verify(jsonGenerator, times(1)).writeObject(1L);
		verify(jsonGenerator).writeObject("testValue1");
		verify(jsonGenerator).writeObject("testValue2");
		verify(jsonGenerator, times(4)).writeFieldName(anyString());
		verify(jsonGenerator, times(4)).writeObject(Mockito.any());
	}

	@Test
	public void emptyTrustedIdentifiableSerializationTest() throws Exception {
		serializer.serialize(new TrustedTestBean(), jsonGenerator, null);
		verify(jsonGenerator, times(1)).writeFieldName("isSecure");
		verify(jsonGenerator, times(1)).writeFieldName("code");
		verify(jsonGenerator).writeFieldName("id");
		verify(jsonGenerator).writeFieldName("otherAttr");
		verify(jsonGenerator).writeFieldName("otherAttr2");
		verify(jsonGenerator, times(5)).writeFieldName(anyString());
		verify(jsonGenerator, times(4)).writeObject(null);
		verify(jsonGenerator, times(1)).writeObject(true);
	}

	@Test
	public void TrustedSerializationTest() throws Exception {
		serializer.serialize(new TrustedTestBean(2L, 1L, "testValue1", "testValue2"), jsonGenerator, null);
		verify(jsonGenerator, times(1)).writeFieldName("isSecure");
		verify(jsonGenerator, times(1)).writeFieldName("code");
		verify(jsonGenerator).writeFieldName("id");
		verify(jsonGenerator).writeFieldName("otherAttr");
		verify(jsonGenerator).writeFieldName("otherAttr2");
		verify(jsonGenerator, times(1)).writeObject(true);
		verify(jsonGenerator, times(1)).writeObject(2L);
		verify(jsonGenerator).writeObject(1L);
		verify(jsonGenerator).writeObject("testValue1");
		verify(jsonGenerator).writeObject("testValue2");
		verify(jsonGenerator, times(5)).writeFieldName(anyString());
		verify(jsonGenerator, times(5)).writeObject(Mockito.any());
	}

	public class TestBean {

		private Long id;

		private String otherAttr;

		private String otherAttr2;

		public TestBean() {
		}

		public TestBean(final Long id, final String otherAttr, final String otherAttr2) {
			this.id = id;
			this.otherAttr = otherAttr;
			this.otherAttr2 = otherAttr2;
		}

		public Long getId() {
			return id;
		}

		public void setId(final long id) {
			this.id = id;
		}

		public String getOtherAttr() {
			return otherAttr;
		}

		public void setOtherAttr(final String otherAttr) {
			this.otherAttr = otherAttr;
		}

		public String getOtherAttr2() {
			return otherAttr2;
		}

		public void setOtherAttr2(final String otherAttr2) {
			this.otherAttr2 = otherAttr2;
		}

	}

	public class SecuredTestBean extends TestBean implements SecureIdentifiable<Long> {
		public SecuredTestBean() {
			super();
		}

		public SecuredTestBean(final Long id, final String otherAttr, final String otherAttr2) {
			super(id, otherAttr, otherAttr2);
		}
	}

	public class TrustedTestBean extends TestBean implements SecureIdContainer {

		@TrustAssertion(idFor = TrustedTestBean.class)
		private Long code;

		public TrustedTestBean() {
			super();
		}

		public TrustedTestBean(final Long code, final Long id, final String otherAttr, final String otherAttr2) {
			super(id, otherAttr, otherAttr2);
			this.code = code;
		}

		public Long getCode() {
			return code;
		}

		public void setCode(final Long code) {
			this.code = code;
		}

	}

	public class CustomContextualSerializer extends JsonSerializer<Object> implements ContextualSerializer {

		private Boolean identifiable = null;

		private final JsonGenerator originalJgen;

		public CustomContextualSerializer(final JsonGenerator originalJgen) {
			this.originalJgen = originalJgen;
		}

		@Override
		public void serialize(final Object br, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
			if (identifiable == null) {
				identifiable = isIdentifiable();
			}
			originalJgen.writeObject(br);
			originalJgen.writeFieldName("isSecure");
			originalJgen.writeObject(identifiable);
		}

		public JsonSerializer<?> createContextual(final SerializerProvider prov, final BeanProperty property) throws JsonMappingException {
			return new CustomContextualSerializer(originalJgen);
		}

	}

	@SuppressWarnings("unchecked")
	private boolean isIdentifiable() {
		try {

			Field f = CustomSecureSerializer.class.getDeclaredField("secureIdSerializer");
			f.setAccessible(true);

			Map<String, JsonSerializer<Object>> secureIdSerializer = (Map<String, JsonSerializer<Object>>) f.get(serializer);
			if (!secureIdSerializer.isEmpty()) {
				return true;
			}
			else {
				return false;
			}
		}
		catch (Exception e) {
			return false;
		}
	}

}
