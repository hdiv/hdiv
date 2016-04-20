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
import java.util.Arrays;
import java.util.List;

import org.hdiv.config.annotation.ValidationConfigurer.ValidationConfig.EditableValidationConfigurer;
import org.springframework.util.Assert;

/**
 * Editable validation configurer.
 * 
 * @since 2.1.7
 */
public class ValidationConfigurer {

	private List<ValidationConfig> validationConfigs = new ArrayList<ValidationConfig>();

	/**
	 * Add an editable validation for all urls.
	 * 
	 * @return More configuration options
	 */
	public EditableValidationConfigurer addValidation() {

		ValidationConfig validationConfig = new ValidationConfig();
		this.validationConfigs.add(validationConfig);
		EditableValidationConfigurer editableValidationConfigurer = validationConfig.getEditableValidationConfigurer();
		return editableValidationConfigurer;
	}

	/**
	 * Add an editable validation for a url pattern.
	 * 
	 * @param urlPattern Url pattern
	 * @return More configuration options
	 */
	public EditableValidationConfigurer addValidation(String urlPattern) {

		ValidationConfig validationConfig = new ValidationConfig(urlPattern);
		this.validationConfigs.add(validationConfig);
		EditableValidationConfigurer editableValidationConfigurer = validationConfig.getEditableValidationConfigurer();
		return editableValidationConfigurer;
	}

	protected List<ValidationConfig> getValidationConfigs() {

		return this.validationConfigs;
	}

	public class ValidationConfig {

		private String urlPattern;

		private EditableValidationConfigurer editableValidationConfigurer = new EditableValidationConfigurer();

		public ValidationConfig() {
		}

		public ValidationConfig(String urlPattern) {
			this.urlPattern = urlPattern;
		}

		protected String getUrlPattern() {
			return urlPattern;
		}

		protected EditableValidationConfigurer getEditableValidationConfigurer() {
			return editableValidationConfigurer;
		}

		public class EditableValidationConfigurer extends RuleConfigurer {

			private List<String> parameters = new ArrayList<String>();

			/**
			 * Configure editable validation only for some parameters.
			 * 
			 * @param parameterNames parameter name patterns
			 * @return More configuration options
			 */
			public RuleConfigurer forParameters(String... parameterNames) {
				Assert.notEmpty(parameterNames, "Parameter names are required");
				this.parameters.addAll(Arrays.asList(parameterNames));
				return this;
			}

			protected List<String> getParameters() {
				return this.parameters;
			}
		}

		public class RuleConfigurer {

			private List<String> rules = new ArrayList<String>();

			/**
			 * Default rules are included by default.
			 */
			private boolean defaultRules = true;

			/**
			 * Rule names to apply to the editable validation.
			 * 
			 * @param ruleNames Rule names
			 * @return More configuration options
			 */
			public RuleConfigurer rules(String... ruleNames) {
				Assert.notEmpty(ruleNames, "Rule names are required");
				this.rules.addAll(Arrays.asList(ruleNames));
				return this;
			}

			/**
			 * Disable default editable rules. They are enabled by default.
			 * 
			 * @return More configuration options
			 */
			public RuleConfigurer disableDefaults() {

				this.defaultRules = false;
				return this;
			}

			protected List<String> getRules() {
				return rules;
			}

			protected boolean isDefaultRules() {
				return defaultRules;
			}

		}
	}

}
