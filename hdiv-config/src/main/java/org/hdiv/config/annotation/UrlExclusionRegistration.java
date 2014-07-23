/**
 * Copyright 2005-2013 hdiv.org
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
import java.util.List;

import org.hdiv.config.StartPage;
import org.springframework.util.Assert;

/**
 * Contains the data of one URL exclusion.
 * 
 * @since 2.1.7
 */
public class UrlExclusionRegistration {

	private String[] urlPatterns;

	private String method;// TODO Better create an enum?

	public UrlExclusionRegistration(String[] urlPatterns) {
		Assert.notEmpty(urlPatterns, "A URL path is required to create a start page.");
		this.urlPatterns = urlPatterns;
	}

	public void method(String method) {
		Assert.notNull(method, "Method is required");
		this.method = method;
	}

	protected List<StartPage> getExclusions() {

		List<StartPage> exclusions = new ArrayList<StartPage>();
		for (String pattern : this.urlPatterns) {
			StartPage startPage = new StartPage(this.method, pattern);
			exclusions.add(startPage);
		}
		return exclusions;
	}
}
