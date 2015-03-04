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
import org.hdiv.config.annotation.configuration.HdivWebSecurityConfigurerAdapter;
import org.hdiv.regex.DefaultPatternMatcher;
import org.hdiv.validator.DefaultEditableDataValidationProvider;
import org.hdiv.validator.DefaultEditableDataValidationProvider.ValidationTarget;
import org.hdiv.validator.EditableDataValidationProvider;
import org.hdiv.validator.EditableDataValidationResult;
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
			validationConfigurer.addValidation("/secureParam/.*").forParameters("param1");
			validationConfigurer.addValidation("/secure/.*").rules("safeText").disableDefaults();
			validationConfigurer.addValidation("/.*");
		}

	}

	@Autowired
	private HDIVConfig config;

	@Autowired
	private EditableDataValidationProvider validationProvider;

	@Test
	public void editableValidations() {
		assertNotNull(config);

		EditableDataValidationResult result = config.areEditableParameterValuesValid("/insecure/action", "parameter",
				new String[] { "<script>" }, "text");
		assertTrue(result.isValid());

		result = config.areEditableParameterValuesValid("/secureParam/action", "param1", new String[] { "<script>" },
				"text");
		assertFalse(result.isValid());

		result = config.areEditableParameterValuesValid("/secure/action", "parameter", new String[] { "<script>" },
				"text");
		assertFalse(result.isValid());
	}

	@Test
	public void editableValidationsOrder() {
		assertNotNull(validationProvider);

		Map<ValidationTarget, List<IValidation>> vals = ((DefaultEditableDataValidationProvider) validationProvider)
				.getValidations();

		assertEquals(4, vals.size());

		Object[] ptrs = vals.keySet().toArray();

		ValidationTarget vt0 = (ValidationTarget) ptrs[0];
		ValidationTarget vt1 = (ValidationTarget) ptrs[1];
		ValidationTarget vt2 = (ValidationTarget) ptrs[2];
		ValidationTarget vt3 = (ValidationTarget) ptrs[3];

		assertEquals(new DefaultPatternMatcher("/insecure/.*"), vt0.getUrl());
		assertEquals(new DefaultPatternMatcher("/secureParam/.*"), vt1.getUrl());
		assertEquals(new DefaultPatternMatcher("/secure/.*"), vt2.getUrl());
		assertEquals(new DefaultPatternMatcher("/.*"), vt3.getUrl());
	}

}