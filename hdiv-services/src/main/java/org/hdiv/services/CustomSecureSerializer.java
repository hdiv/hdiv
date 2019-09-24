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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.springframework.beans.BeanWrapper;

import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;

public abstract class CustomSecureSerializer extends JsonSerializer<Object> {

	private JsonSerializer<Object> delegatedSerializer;

	private String secureIdName = null;

	private JsonSerializer<Object> efective = null;

	private JsonGenerator jsonGen = null;

	private SerializerProvider jsonProvider = null;

	public void setDelegatedSerializer(final JsonSerializer<Object> delegatedSerializer) {
		this.delegatedSerializer = delegatedSerializer;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public final void serialize(final Object object, final JsonGenerator jgen, final SerializerProvider provider)
			throws IOException, JsonProcessingException {

		jsonGen = jgen;
		jsonProvider = provider;

		jgen.writeStartObject();

		if (delegatedSerializer != null) {
			Object value = null;
			TrustAssertion trustAssertion = null;
			if (object instanceof SecureIdentifiable<?>) {
				if (delegatedSerializer instanceof ContextualSerializer) {
					secureIdName = "id";
					value = ((SecureIdentifiable) object).getId();
					// efectiveSerializer.serialize(value, jgen, provider);

					try {
						efective = (JsonSerializer<Object>) ((ContextualSerializer) delegatedSerializer).createContextual(provider,
								getBeanProperty(secureIdName, value, trustAssertion, object.getClass().getField("id").getType()));
						jgen.writeFieldName(secureIdName);
						efective.serialize(value, jgen, provider);
					}
					catch (Exception e) {
						System.out.println("Error geting id of object " + object);
					}

				}

			}
			else if (object instanceof SecureIdContainer) {
				for (Field field : object.getClass().getDeclaredFields()) {
					trustAssertion = field.getAnnotation(TrustAssertion.class);
					if (trustAssertion != null) {
						if (delegatedSerializer instanceof ContextualSerializer) {
							try {
								field.setAccessible(true);
								secureIdName = field.getName();
								value = field.get(object);

								if (delegatedSerializer instanceof ContextualSerializer) {
									efective = ((JsonSerializer<Object>) ((ContextualSerializer) delegatedSerializer).createContextual(
											provider, getBeanProperty(secureIdName, value, trustAssertion, field.getType())));
									jgen.writeFieldName(secureIdName);
									efective.serialize(value, jgen, provider);
								}
								break;
							}
							catch (Exception e) {
								System.out.println("Error geting id of object " + object);
							}
						}

					}
				}

			}
		}

		writeBody(object);

		jgen.writeEndObject();

	}

	protected abstract void writeBody(final Object obj);

	protected void writeField(final BeanWrapper beanWrapper, final String tagName, final String propertyName,
			final boolean nullValueAsBlank) throws IOException {

		Object propertyValue = beanWrapper.getPropertyValue(propertyName);

		jsonGen.writeFieldName(tagName);
		if (propertyName.equals(secureIdName) && efective != null) {
			efective.serialize(propertyValue, jsonGen, jsonProvider);
			// jsonGen.writeObject(secureIdValue);
		}
		else {
			// Se escribe en el JSON el value
			if (propertyValue == null && nullValueAsBlank) {
				jsonGen.writeString("");
			}
			else {
				jsonGen.writeObject(propertyValue);
			}
		}
	}
	// @Override
	// @SuppressWarnings("unchecked")
	// public JsonSerializer<?> createContextual(final SerializerProvider prov, final BeanProperty property) throws JsonMappingException {
	// if (delegatedSerializer != null && delegatedSerializer.getClass().isAssignableFrom(ContextualSerializer.class)) {
	// efectiveSerializer = (JsonSerializer<Object>) ((ContextualSerializer) delegatedSerializer).createContextual(prov, property);
	// // return this;//((ContextualSerializer) delegatedSerializer).createContextual(prov, property);
	// }
	// // else {
	// //
	// // }
	// return this;
	// }

	private BeanProperty getBeanProperty(final String name, final Object value, final TrustAssertion trustAssertion, final Class type) {

		return new BeanProperty() {

			public String getName() {
				return name;
			}

			public PropertyName getFullName() {
				return new PropertyName(name);
			}

			public JavaType getType() {
				if (type != null) {
					return TypeFactory.defaultInstance().constructType(type);
				}
				else {
					return null;
				}

			}

			public PropertyName getWrapperName() {
				// TODO Auto-generated method stub
				return null;
			}

			public PropertyMetadata getMetadata() {
				// TODO Auto-generated method stub
				return null;
			}

			public boolean isRequired() {
				// TODO Auto-generated method stub
				return false;
			}

			public <A extends Annotation> A getAnnotation(final Class<A> acls) {
				if (acls.isAssignableFrom(TrustAssertion.class)) {
					return (A) trustAssertion;
				}
				else {
					return null;
				}

			}

			public <A extends Annotation> A getContextAnnotation(final Class<A> acls) {
				return null;
			}

			public AnnotatedMember getMember() {
				return null;
			}

			public Value findFormatOverrides(final AnnotationIntrospector intr) {
				return null;
			}

			public void depositSchemaProperty(final JsonObjectFormatVisitor objectVisitor) throws JsonMappingException {
			}

		};

	}

}
