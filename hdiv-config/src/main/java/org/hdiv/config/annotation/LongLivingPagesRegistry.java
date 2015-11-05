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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

/**
 * Registry to add long living pages to the configuration.
 * 
 * @since 2.1.7
 */
public class LongLivingPagesRegistry {

	private final List<LongLivingPagesRegistration> registrations = new ArrayList<LongLivingPagesRegistration>();

	/**
	 * <p>
	 * Configure one or more long living pages adding one or more url patterns.
	 * </p>
	 * <p>
	 * Links and forms inside a long living pages never expire.
	 * </p>
	 * 
	 * @param urlPatterns
	 *            Url patterns.
	 * @return more configuration options
	 */
	public LongLivingPagesRegistration addLongLivingPages(String... urlPatterns) {
		Assert.notEmpty(urlPatterns, "Url patterns are required");
		LongLivingPagesRegistration registration = new LongLivingPagesRegistration(urlPatterns);
		registrations.add(registration);
		return registration;
	}

	protected Map<String, String> getLongLivingPages() {

		Map<String, String> all = new HashMap<String, String>();

		for (LongLivingPagesRegistration regitration : registrations) {
			Map<String, String> pages = regitration.getLongLivingPages();
			all.putAll(pages);
		}
		return all;
	}
}
