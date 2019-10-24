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
import com.fasterxml.jackson.core.JsonGenerator.Feature;
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

/**
 * @author Hdiv
 * @since 4.0
 *
 * Serializer to be used as custom base serializer into services application. Custom serializers do not ensure the securization of the
 * entities. Use CustomSecureSerializer as application custom serializer to protect ids.
 * 
 */
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

		boolean flushEnabled = jsonGen.isEnabled(Feature.FLUSH_PASSED_TO_STREAM);

		jsonGen.disable(Feature.FLUSH_PASSED_TO_STREAM);

		jsonGen.writeStartObject();

		if (delegatedSerializer != null) {
			Object value = null;
			if (object instanceof SecureIdentifiable<?>) {
				if (delegatedSerializer instanceof ContextualSerializer) {
					secureIdName = "id";
					value = ((SecureIdentifiable) object).getId();

					try {

						Field identifiableField = getIdentityField(object);
						efective = (JsonSerializer<Object>) ((ContextualSerializer) delegatedSerializer).createContextual(provider,
								getBeanProperty(secureIdName, value, null, identifiableField != null ? identifiableField.getType() : null));
						jsonGen.writeFieldName(secureIdName);
						efective.serialize(value, jsonGen, provider);
					}
					catch (Exception e) {
						// Error getting id of the object. Do not make any task to preserve the original functionality
					}

				}

			}
			else if (object instanceof SecureIdContainer) {
				for (Field field : object.getClass().getDeclaredFields()) {
					TrustAssertion trustAssertion = field.getAnnotation(TrustAssertion.class);
					if (trustAssertion != null) {
						if (delegatedSerializer instanceof ContextualSerializer) {
							try {
								field.setAccessible(true);
								secureIdName = field.getName();
								value = field.get(object);

								if (delegatedSerializer instanceof ContextualSerializer) {
									efective = ((JsonSerializer<Object>) ((ContextualSerializer) delegatedSerializer).createContextual(
											provider, getBeanProperty(secureIdName, value, trustAssertion, field.getType())));
									jsonGen.writeFieldName(secureIdName);
									efective.serialize(value, jsonGen, provider);
								}
								break;
							}
							catch (Exception e) {
								// Error getting id of the object. Do not make any task to preserve the original functionality
							}
						}

					}
				}

			}
		}

		writeBody(object);

		if (flushEnabled) {
			jsonGen.enable(Feature.FLUSH_PASSED_TO_STREAM);
		}

		jsonGen.writeEndObject();

	}

	/**
	 * 
	 * Gets the id field of the bean even if it is defined into any parent class
	 * 
	 **/
	private Field getIdentityField(final Object obj) {
		Class<?> clazz = obj.getClass();
		while (clazz != Object.class) {

			try {
				return clazz.getDeclaredField("id");
			}
			catch (NoSuchFieldException e) {
				// Search into parent classes
				clazz = clazz.getSuperclass();
			}

		}

		return null;
	}

	protected abstract void writeBody(final Object obj);

	/**
	 * Serializes the property with the name propertyName of the beanWrapper object.
	 * @param beanWrapper Representation of the object to be serialized.
	 * @param propertyName The name of the property. It will be used as the tag name.
	 * @param nullValueAsBlank In case of property value is null, if nullValueAsBlank is true blank value will be written, null if false.
	 * @throws IOException
	 */
	protected void writeField(final BeanWrapper beanWrapper, final String propertyName, final boolean nullValueAsBlank) throws IOException {
		writeField(beanWrapper, propertyName, propertyName, nullValueAsBlank);
	}

	/**
	 * Serializes the property with the name propertyName of the beanWrapper object.
	 * @param beanWrapper Representation of the object to be serialized.
	 * @param tagName The tag name of the property.
	 * @param propertyName The name of the property.
	 * @param nullValueAsBlank In case of property value is null, if nullValueAsBlank is true blank value will be written, null if false.
	 * @throws IOException
	 */
	protected void writeField(final BeanWrapper beanWrapper, final String tagName, final String propertyName,
			final boolean nullValueAsBlank) throws IOException {

		Object propertyValue = beanWrapper.getPropertyValue(propertyName);

		jsonGen.writeFieldName(tagName);
		if (propertyName.equals(secureIdName) && efective != null) {
			efective.serialize(propertyValue, jsonGen, jsonProvider);
		}
		else {
			if (propertyValue == null && nullValueAsBlank) {
				jsonGen.writeString("");
			}
			else {
				jsonGen.writeObject(propertyValue);
			}
		}
	}

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
