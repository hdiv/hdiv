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
package org.hdiv.config.annotation.configuration;

import org.hdiv.config.annotation.ExclusionRegistry;
import org.hdiv.config.annotation.LongLivingPagesRegistry;
import org.hdiv.config.annotation.RuleRegistry;
import org.hdiv.config.annotation.ValidationConfigurer;
import org.hdiv.config.annotation.builders.SecurityConfigBuilder;

/**
 * Utility class to extend {@link HdivWebSecurityConfigurer} interface.
 */
public class HdivWebSecurityConfigurerAdapter implements HdivWebSecurityConfigurer {

	public void configure(SecurityConfigBuilder securityConfigBuilder) {

	}

	public void addExclusions(ExclusionRegistry registry) {

	}

	public void addLongLivingPages(LongLivingPagesRegistry registry) {

	}

	public void addRules(RuleRegistry registry) {

	}

	public void configureEditableValidation(ValidationConfigurer validationConfigurer) {

	}

}
