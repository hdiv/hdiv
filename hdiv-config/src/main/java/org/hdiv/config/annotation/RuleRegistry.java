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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hdiv.validator.IValidation;
import org.hdiv.validator.Validation;
import org.springframework.util.Assert;

/**
 * Registry to add new editable validation rules to the validation phase.
 * 
 * @since 2.1.7
 */
public class RuleRegistry {

	/**
	 * All user defined editable validations rules.
	 */
	private final List<RuleRegistration> registrations = new ArrayList<RuleRegistration>();

	public RuleRegistration addRule(String name) {
		Assert.notNull(name, "Rule name is required");
		RuleRegistration registration = new RuleRegistration(name);
		registrations.add(registration);
		return registration;
	}

	protected Map<String, IValidation> getRules() {

		Map<String, IValidation> rules = new HashMap<String, IValidation>();

		for (RuleRegistration regitration : registrations) {
			Validation rule = regitration.getRule();
			rules.put(rule.getName(), rule);
		}
		return rules;
	}
}
