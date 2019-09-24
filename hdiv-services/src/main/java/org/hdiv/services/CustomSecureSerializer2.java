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
import org.springframework.beans.BeanWrapperImpl;

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

public abstract class CustomSecureSerializer2 extends JsonSerializer<Object> {

	private JsonSerializer<Object> delegatedSerializer;

	private JsonGenerator jsonGen = null;

	private SerializerProvider serialProvider = null;

	private String secureIdName = null;

	private Object secureIdValue = null;

	private Object target = null;

	public void setDelegatedSerializer(final JsonSerializer<Object> delegatedSerializer) {
		this.delegatedSerializer = delegatedSerializer;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public final void serialize(final Object object, final JsonGenerator jgen, final SerializerProvider provider)
			throws IOException, JsonProcessingException {

		jsonGen = jgen;
		serialProvider = provider;
		target = object;

		jsonGen.writeStartObject();

		if (delegatedSerializer != null) {
			TrustAssertion trustAssertion = null;
			if (object instanceof SecureIdentifiable<?>) {
				secureIdName = "id";
				secureIdValue = ((SecureIdentifiable) object).getId();
				if (delegatedSerializer instanceof ContextualSerializer) {

					// efectiveSerializer.serialize(value, jgen, provider);

					try {
						JsonSerializer<Object> efective = (JsonSerializer<Object>) ((ContextualSerializer) delegatedSerializer)
								.createContextual(serialProvider, getBeanProperty(secureIdName, secureIdValue, trustAssertion,
										object.getClass().getField("id").getType()));
						jsonGen.writeFieldName(secureIdName);
						efective.serialize(secureIdValue, jsonGen, serialProvider);
					}
					catch (Exception e) {
						System.out.println("Error geting id of object " + object);
					}

				}
				else {
					delegatedSerializer.serialize(secureIdValue, jsonGen, serialProvider);
				}
				// rupBaseDTO.setId(((SecureIdentifiable) object).getId());
			}
			else if (object instanceof SecureIdContainer) {
				for (Field field : object.getClass().getDeclaredFields()) {
					trustAssertion = field.getAnnotation(TrustAssertion.class);
					if (trustAssertion != null) {
						if (delegatedSerializer instanceof ContextualSerializer) {
							try {
								field.setAccessible(true);
								secureIdName = field.getName();
								secureIdValue = field.get(object);

								if (delegatedSerializer instanceof ContextualSerializer) {
									JsonSerializer<Object> efective = ((JsonSerializer<Object>) ((ContextualSerializer) delegatedSerializer)
											.createContextual(serialProvider,
													getBeanProperty(secureIdName, secureIdValue, trustAssertion, field.getType())));
									jsonGen.writeFieldName(secureIdName);
									efective.serialize(secureIdValue, jsonGen, serialProvider);
								}
								break;
							}
							catch (Exception e) {
								System.out.println("Error geting id of object " + object);
							}
						}
						else {
							delegatedSerializer.serialize(secureIdValue, jsonGen, serialProvider);
						}
					}
				}

			}
		}

		writeBody(new BeanWrapperImpl(object));

		jsonGen.writeEndObject();

	}

	protected void writeFieldName(final String name) throws IOException {
		jsonGen.writeFieldName(name);
	}

	protected void writeFieldValue(final BeanWrapper beanWrapper, final String name, final boolean nullValueAsBlank) throws IOException {

		// if (name.equals(secureIdName) && secureIdValue != null && beanWrapper.getWrappedInstance().equals(target)) {
		// jsonGen.writeObject(secureIdValue);
		// }
		// else {

		Object propertyValue = beanWrapper.getPropertyValue(name);

		// Se escribe en el JSON el value
		if (propertyValue == null && nullValueAsBlank) {
			jsonGen.writeString("");
		}
		else {
			jsonGen.writeObject(propertyValue);
		}
		// }
	}

	protected abstract void writeBody(final BeanWrapper beanWrapper) throws IOException;

	private BeanProperty getBeanProperty(final String name, final Object value, final TrustAssertion trustAssertion, final Class<?> type) {

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
				return null;
			}

			public PropertyMetadata getMetadata() {
				return null;
			}

			public boolean isRequired() {
				return false;
			}

			@SuppressWarnings("unchecked")
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
