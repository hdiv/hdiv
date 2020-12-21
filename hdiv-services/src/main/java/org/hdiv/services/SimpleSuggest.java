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

import java.util.ArrayList;
import java.util.List;

public class SimpleSuggest<T> extends SuggestImpl<SuggestObjectWrapperImpl<T>> {

	public SimpleSuggest(final String svalue) {
		this(new SuggestObjectWrapperImpl<T>(svalue));
	}

	public SimpleSuggest(final SuggestObjectWrapperImpl<T> wrapper) {
		super(wrapper, SuggestObjectWrapperImpl.ID);
	}

	public static <T> List<Suggest<SuggestObjectWrapperImpl<T>>> wrap(final T[] values) {
		List<Suggest<SuggestObjectWrapperImpl<T>>> suggests = new ArrayList<Suggest<SuggestObjectWrapperImpl<T>>>(values.length);
		for (int i = 0; i < values.length; i++) {
			suggests.add(new SimpleSuggest<T>(new SuggestObjectWrapperImpl<T>(String.valueOf(values[i]))));
		}
		return suggests;
	}

}
