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
package org.hdiv.config.annotation;

import java.util.HashMap;
import java.util.Map;

import org.hdiv.state.scope.StateScope;
import org.hdiv.state.scope.StateScopeType;
import org.springframework.util.Assert;

/**
 * Contains the data of a new long living page.
 * 
 * @since 2.1.7
 */
public class LongLivingPagesRegistration {

	private String[] urlPatterns;

	private StateScopeType scopeType = StateScopeType.USER_SESSION;

	public LongLivingPagesRegistration(String[] urlPatterns) {
		Assert.notEmpty(urlPatterns, "A URL path is required to create a start page.");
		this.urlPatterns = urlPatterns;
	}

	/**
	 * <p>
	 * Long living pages store their states in a particular {@link StateScope}.
	 * </p>
	 * <p>
	 * Determine which {@link StateScopeType} to use.
	 * </p>
	 * 
	 * @param scopeType
	 *            Scope to use.
	 */
	public void scope(StateScopeType scopeType) {
		Assert.notNull(scopeType, "Scope is required");
		this.scopeType = scopeType;
	}

	protected Map<String, String> getLongLivingPages() {

		Map<String, String> pages = new HashMap<String, String>();
		for (String pattern : this.urlPatterns) {

			pages.put(pattern, scopeType.getName());
		}
		return pages;
	}
}
