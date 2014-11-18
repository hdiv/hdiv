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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.hdiv.config.HDIVConfig;
import org.hdiv.config.HDIVValidations;
import org.hdiv.config.annotation.configuration.HdivWebSecurityConfigurerAdapter;
import org.hdiv.regex.DefaultPatternMatcher;
import org.hdiv.regex.PatternMatcher;
import org.hdiv.validator.IValidation;
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
public class EditableValidationsTest {

	@Configuration
	@EnableHdivWebSecurity
	static class ContextConfiguration extends HdivWebSecurityConfigurerAdapter {

		@Override
		public void addRules(RuleRegistry registry) {

			registry.addRule("safeText").acceptedPattern("^[a-zA-Z0-9@.\\-_]*$");
		}

		@Override
		public void configureEditableValidation(ValidationConfigurer validationConfigurer) {

			validationConfigurer.addValidation("/insecure/.*").disableDefaults();
			validationConfigurer.addValidation("/secure/.*").rules("safeText").disableDefaults();
			validationConfigurer.addValidation("/.*");
		}

	}

	@Autowired
	private HDIVConfig config;

	@Autowired
	private HDIVValidations validations;

	@Test
	public void editableValidations() {
		assertNotNull(config);

		boolean result = config.areEditableParameterValuesValid("/insecure/action", "parameter",
				new String[] { "<script>" }, "text");
		assertTrue(result);

		result = config.areEditableParameterValuesValid("/secure/action", "parameter", new String[] { "<script>" },
				"text");
		assertFalse(result);
	}

	@Test
	public void editableValidationsOrder() {
		assertNotNull(validations);

		assertEquals(3, validations.getUrls().size());

		assertNotNull(validations);

		Map<PatternMatcher, List<IValidation>> urls = validations.getUrls();
		assertEquals(3, urls.size());

		Object[] ptrs = urls.keySet().toArray();

		assertEquals(new DefaultPatternMatcher("/insecure/.*"), ptrs[0]);
		assertEquals(new DefaultPatternMatcher("/secure/.*"), ptrs[1]);
		assertEquals(new DefaultPatternMatcher("/.*"), ptrs[2]);
	}

}