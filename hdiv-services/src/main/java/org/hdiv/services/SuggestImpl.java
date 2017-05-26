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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.hateoas.Identifiable;

public class SuggestImpl<T> implements Suggest<T> {

	private final T value;

	private final String valueField;

	public SuggestImpl(final T value) {
		this(value, null);
	}

	public SuggestImpl(final T value, final String valueField) {
		this.value = value;
		this.valueField = valueField;
	}

	public T getValue() {
		return value;
	}

	public String getValueField() {
		return valueField;
	}

	public String getValueAsString() {
		if (value != null) {
			try {
				if (valueField != null) {
					return String.valueOf(getField(valueField).get(value));
				}
				else {
					return value.toString();
				}
			}
			catch (Exception e) {
				throw new IllegalArgumentException("Valuefield could not be serialized", e);
			}
		}
		return null;
	}

	private Field getField(final String name) throws NoSuchFieldException, SecurityException {
		Field field = value.getClass().getDeclaredField(name);
		field.setAccessible(true);
		return field;
	}

	public static <T> List<Suggest<T>> wrap(final List<T> list, final String valueField) {
		List<Suggest<T>> suggests = new ArrayList<Suggest<T>>(list.size());
		for (T value : list) {
			suggests.add(new SuggestImpl<T>(value, valueField));
		}
		return suggests;
	}

	public static <T extends Serializable, S extends Identifiable<T>> List<Suggest<T>> wrapIdentifiable(final Collection<S> list,
			final String valueField) {
		List<Suggest<T>> suggests = new ArrayList<Suggest<T>>(list.size());
		for (S value : list) {
			suggests.add(new SuggestImpl<T>(value.getId(), valueField));
		}
		return suggests;
	}

	public static <T, S> List<Suggest<T>> wrap(final Collection<S> list, final String valueField, final SuggestSupplier<S, T> supplier) {
		List<Suggest<T>> suggests = new ArrayList<Suggest<T>>(list.size());
		for (S value : list) {
			suggests.add(new SuggestImpl<T>(supplier.get(value), valueField));
		}
		return suggests;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <U> U getUnwrappedValue() {
		if (value instanceof WrappedValue) {
			return (U) ((WrappedValue) value).getValue();
		}
		return (U) value;
	}
}
