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
package org.hdiv.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class LimitedCache<T> {

	private static final int CACHE_LIMIT = 5;

	private final LinkedHashMap<String, T> cache = new LinkedHashMap<String, T>(CACHE_LIMIT + 1) {

		private static final long serialVersionUID = 870739249442571505L;

		@Override
		protected boolean removeEldestEntry(final Map.Entry<String, T> eldest) {
			return size() > CACHE_LIMIT;
		}
	};

	public void register(final String key, final T value) {
		cache.put(key, value);
	}

	public T getCached(final String key) {
		return cache.get(key);
	}
}
