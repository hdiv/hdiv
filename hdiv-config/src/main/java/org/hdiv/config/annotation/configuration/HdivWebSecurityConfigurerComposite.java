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

import java.util.ArrayList;
import java.util.List;

import org.hdiv.config.annotation.ExclusionRegistry;
import org.hdiv.config.annotation.LongLivingPagesRegistry;
import org.hdiv.config.annotation.RuleRegistry;
import org.hdiv.config.annotation.ValidationConfigurer;
import org.hdiv.config.annotation.builders.SecurityConfigBuilder;

/**
 * Composite multiple {@link HdivWebSecurityConfigurer} instances.
 */
public class HdivWebSecurityConfigurerComposite implements HdivWebSecurityConfigurer {

	private final List<HdivWebSecurityConfigurer> delegates = new ArrayList<HdivWebSecurityConfigurer>();

	public void addHdivSecurityConfigurers(List<HdivWebSecurityConfigurer> configurers) {
		if (configurers != null) {
			this.delegates.addAll(configurers);
		}
	}

	public void configure(SecurityConfigBuilder securityConfigBuilder) {
		for (HdivWebSecurityConfigurer delegate : this.delegates) {
			delegate.configure(securityConfigBuilder);
		}
	}

	public void addExclusions(ExclusionRegistry registry) {
		for (HdivWebSecurityConfigurer delegate : this.delegates) {
			delegate.addExclusions(registry);
		}
	}

	public void addLongLivingPages(LongLivingPagesRegistry registry) {
		for (HdivWebSecurityConfigurer delegate : this.delegates) {
			delegate.addLongLivingPages(registry);
		}
	}

	public void addRules(RuleRegistry registry) {
		for (HdivWebSecurityConfigurer delegate : this.delegates) {
			delegate.addRules(registry);
		}
	}

	public void configureEditableValidation(ValidationConfigurer validationConfigurer) {
		for (HdivWebSecurityConfigurer delegate : this.delegates) {
			delegate.configureEditableValidation(validationConfigurer);
		}
	}

}
