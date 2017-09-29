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
package org.hdiv.config.annotation.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.hdiv.config.HDIVConfig;
import org.hdiv.regex.PatternMatcherFactory;
import org.junit.Before;
import org.junit.Test;

public class SecurityConfigBuilderTest {

	private SecurityConfigBuilder builder;

	@Before
	public void setUp() {
		builder = new SecurityConfigBuilder(new PatternMatcherFactory());
	}

	// @formatter:off
	@Test
	public void build() {
		assertNotNull(builder);

		builder
			.cookiesConfidentiality(false)
			.maxPagesPerSession(23)
			.reuseExistingPageInAjaxRequest(true)
			.sessionExpired()
				.loginPage("/login.html");

		HDIVConfig config = builder.build();
		assertNotNull(config);
		assertEquals(false, config.isCookiesConfidentialityActivated());
		assertEquals(true, config.isReuseExistingPageInAjaxRequest());
		
		assertEquals(23, builder.getMaxPagesPerSession());
	}
	// @formatter:on
}
