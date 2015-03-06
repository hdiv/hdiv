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

import java.util.List;
import java.util.Map;

import org.hdiv.config.HDIVConfig;
import org.hdiv.config.annotation.builders.SecurityConfigBuilder;
import org.hdiv.config.annotation.configuration.HdivWebSecurityConfigurerAdapter;
import org.hdiv.state.scope.StateScopeType;
import org.hdiv.validator.DefaultValidationRepository;
import org.hdiv.validator.IValidation;
import org.hdiv.validator.ValidationRepository;
import org.hdiv.validator.ValidationTarget;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
// ApplicationContext will be loaded from the static inner ContextConfiguration class
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class HdivWebSecurityTest {

	@Configuration
	@EnableHdivWebSecurity
	static class ContextConfiguration extends HdivWebSecurityConfigurerAdapter {

		@Override
		public void addExclusions(ExclusionRegistry registry) {

			registry.addUrlExclusions("/", "/login.html", "/logout.html").method("GET");
			registry.addUrlExclusions("/j_spring_security_check").method("POST");
			registry.addUrlExclusions("/attacks/.*");

			registry.addParamExclusions("param1", "param2").forUrls("/attacks/.*");
			registry.addParamExclusions("param3", "param4");
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

			validationConfigurer.addValidation("/secure/.*").forParameters("param1", "params2").rules("safeText")
					.disableDefaults();
			validationConfigurer.addValidation("/safetext/.*");
		}

		@Override
		public void configure(SecurityConfigBuilder builder) {

			builder.sessionExpired().homePage("/").loginPage("/login.html").and().cipher().keySize(128).and()
					.debugMode(true);
		}
	}

	@Autowired
	private HDIVConfig config;

	@Autowired
	private ValidationRepository validationRepository;

	@Test
	public void config() {
		assertNotNull(config);

		assertEquals("/", config.getSessionExpiredHomePage());
		assertEquals("/login.html", config.getSessionExpiredLoginPage());
	}

	@Test
	public void validations() {
		assertNotNull(validationRepository);

		Map<ValidationTarget, List<IValidation>> validations = ((DefaultValidationRepository) validationRepository)
				.getValidations();

		assertEquals(2, validations.size());

		List<IValidation> urlValidations = this.getValidations(validations, "/secure/.*");
		assertEquals(1, urlValidations.size()); // Only safetext
		ValidationTarget target = this.getTarget(validations, "/secure/.*");
		assertEquals(2, target.getParams().size());

		urlValidations = this.getValidations(validations, "/safetext/.*");
		assertEquals(6, urlValidations.size());// Defaults
		target = this.getTarget(validations, "/safetext/.*");
		assertEquals(0, target.getParams().size());
	}

	@Test
	public void addLongLivingPages() {

		assertEquals("app", config.isLongLivingPages("/longLiving/sample.html"));
		assertEquals("user-session", config.isLongLivingPages("/longLivingPageApp.html"));
		assertEquals(null, config.isLongLivingPages("/noLongLiving.html"));
	}

	protected List<IValidation> getValidations(Map<ValidationTarget, List<IValidation>> validations, String pattern) {

		for (ValidationTarget target : validations.keySet()) {
			if (target.getUrl().matches(pattern)) {
				return validations.get(target);
			}
		}
		return null;
	}

	protected ValidationTarget getTarget(Map<ValidationTarget, List<IValidation>> validations, String pattern) {

		for (ValidationTarget target : validations.keySet()) {
			if (target.getUrl().matches(pattern)) {
				return target;
			}
		}
		return null;
	}
}