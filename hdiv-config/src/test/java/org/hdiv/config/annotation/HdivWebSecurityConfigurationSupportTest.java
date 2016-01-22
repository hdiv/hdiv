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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.hdiv.config.HDIVConfig;
import org.hdiv.config.Strategy;
import org.hdiv.config.annotation.builders.SecurityConfigBuilder;
import org.hdiv.state.scope.StateScopeType;
import org.junit.Before;
import org.junit.Test;

public class HdivWebSecurityConfigurationSupportTest {

	private HdivWebSecurityConfigurationSupport configuration;

	@Before
	public void setUp() {
		configuration = new HdivWebSecurityConfigurationSupport() {

			@Override
			public void addExclusions(ExclusionRegistry registry) {

				registry.addUrlExclusions("/", "/login.html", "/logout.html").method("GET");
				registry.addUrlExclusions("/j_spring_security_check").method("POST");
				registry.addUrlExclusions("/attacks/.*");

				registry.addParamExclusions("param1.*", "param2").forUrls("/attacks/.*");
				registry.addParamExclusions("param3.*", "param4");
			}

			@Override
			public void addLongLivingPages(LongLivingPagesRegistry registry) {

				registry.addLongLivingPages("/longLivingPage.html", "/longLiving/.*").scope(StateScopeType.APP);
				registry.addLongLivingPages("/longLivingPageApp.html");
			}

			@Override
			public void addRules(RuleRegistry registry) {

				registry.addRule("safeText").acceptedPattern("^[a-zA-Z0-9@.\\-_]*$");
			}

			@Override
			public void configureEditableValidation(ValidationConfigurer validationConfigurer) {

				validationConfigurer.addValidation("/secure/.*").rules("safeText").disableDefaults();
				validationConfigurer.addValidation("/safetext/.*");
			}

			// @formatter:off
			@Override
			public void configure(SecurityConfigBuilder builder) {

				builder
					.sessionExpired()
						.homePage("/").loginPage("/login.html").and()
					.debugMode(true)
					.confidentiality(false)
					.errorPage("/customErrorPage.html")
					.randomName(true)
					.strategy(Strategy.MEMORY)
					.validateUrlsWithoutParams(false);
			}
			// @formatter:on
		};
	}

	@Test
	public void config() {
		HDIVConfig config = configuration.hdivConfig();
		assertNotNull(config);

		assertEquals(true, config.isDebugMode());
		assertEquals(false, config.getConfidentiality());
		assertEquals("/customErrorPage.html", config.getErrorPage());
		assertEquals(true, config.isRandomName());
		assertEquals(Strategy.MEMORY, config.getStrategy());
		assertEquals(false, config.isValidationInUrlsWithoutParamsActivated());
	}

	@Test
	public void exclusions() {
		HDIVConfig config = configuration.hdivConfig();
		assertNotNull(config);

		assertEquals(true, config.isStartPage("/attacks/view.html", null));
		assertEquals(false, config.isStartPage("/j_spring_security_check", "GET"));
		assertEquals(true, config.isStartPage("/", "GET"));

		assertEquals(true, config.isParameterWithoutValidation("/attacks/home.html", "param1"));
		assertEquals(true, config.isParameterWithoutValidation("/attacks/home.html", "param1234"));
		assertEquals(true, config.isParameterWithoutValidation("/attacks/home.html", "param2"));
		assertEquals(false, config.isParameterWithoutValidation("/attacks/home.html", "param234"));
		assertEquals(false, config.isParameterWithoutValidation("/out/home.html", "param2"));

		assertEquals(true, config.isStartParameter("param3"));
		assertEquals(true, config.isStartParameter("param34"));
		assertEquals(true, config.isStartParameter("param4"));
		assertEquals(false, config.isStartParameter("param456"));
	}

	@Test
	public void longLivingPages() {
		HDIVConfig config = configuration.hdivConfig();
		assertNotNull(config);

		assertEquals("app", config.isLongLivingPages("/longLiving/sample.html"));
		assertEquals("user-session", config.isLongLivingPages("/longLivingPageApp.html"));
		assertEquals(null, config.isLongLivingPages("/noLongLiving.html"));
	}

}
