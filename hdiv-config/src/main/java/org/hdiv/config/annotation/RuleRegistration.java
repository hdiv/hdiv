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
package org.hdiv.config.annotation;

import java.util.regex.Pattern;

import org.hdiv.validator.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Contains the data for a editable validation rule.
 * 
 * @since 2.1.7
 */
public class RuleRegistration {

	private static final Logger log = LoggerFactory.getLogger(RuleRegistration.class);

	/**
	 * Validation rule.
	 */
	private final Validation validation;

	public RuleRegistration(final String name) {
		validation = new Validation();
		validation.setName(name);
	}

	/**
	 * <p>
	 * Determine the component type for which apply the validation rule.
	 * </p>
	 * <p>
	 * Some options are, 'text' and 'textarea'.
	 * </p>
	 * 
	 * @param componentType Type of the component.
	 * @return More configuration options
	 */
	public RuleRegistration componentType(final String componentType) {
		Assert.notNull(componentType, "Component type is required");
		validation.setComponentType(componentType);
		return this;
	}

	/**
	 * <p>
	 * Java {@link Pattern} to validate values.
	 * </p>
	 * <p>
	 * Contains the whitelist validation.
	 * </p>
	 * 
	 * @param acceptedPattern Accepter pattern
	 * @return More configuration options
	 */
	public RuleRegistration acceptedPattern(final String acceptedPattern) {
		Assert.notNull(acceptedPattern, "Accepted pattern is required");
		if (validation.getAcceptedPattern() != null) {
			log.warn("Can't add more than one acceptedPattern per rule. This will overwrite the previous one.");
		}
		validation.setAcceptedPattern(acceptedPattern);
		return this;
	}

	/**
	 * <p>
	 * Java {@link Pattern} to validate values.
	 * </p>
	 * <p>
	 * Contains the blacklist validation.
	 * </p>
	 * 
	 * @param rejectedPattern Rejected pattern
	 * @return More configuration options
	 */
	public RuleRegistration rejectedPattern(final String rejectedPattern) {
		Assert.notNull(rejectedPattern, "Rejected pattern is required");
		if (validation.getRejectedPattern() != null) {
			log.warn("Can't add more than one rejectedPattern per rule. This will overwrite the previous one.");
		}
		validation.setRejectedPattern(rejectedPattern);
		return this;
	}

	protected Validation getRule() {
		return validation;
	}

}
