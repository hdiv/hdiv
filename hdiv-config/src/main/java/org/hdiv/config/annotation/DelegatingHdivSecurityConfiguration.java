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

import java.util.List;

import org.hdiv.config.annotation.builders.SecurityConfigBuilder;
import org.hdiv.config.annotation.configuration.HdivSecurityConfigurer;
import org.hdiv.config.annotation.configuration.HdivSecurityConfigurerComposite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DelegatingHdivSecurityConfiguration extends HdivSecurityConfigurationSupport {

	/**
	 * Composite with all {@link HdivSecurityConfigurer} instances.
	 */
	private final HdivSecurityConfigurerComposite configurers = new HdivSecurityConfigurerComposite();

	@Autowired(required = false)
	public void setConfigurers(List<HdivSecurityConfigurer> configurers) {
		if (configurers == null || configurers.isEmpty()) {
			return;
		}
		this.configurers.addHdivSecurityConfigurers(configurers);
	}

	@Override
	public void addExclusions(ExclusionRegistry registry) {

		this.configurers.addExclusions(registry);
	}

	@Override
	public void addRules(RuleRegistry registry) {

		this.configurers.addRules(registry);
	}

	@Override
	public void configureEditableValidation(ValidationConfigurer validationConfigurer) {

		this.configurers.configureEditableValidation(validationConfigurer);
	}

	@Override
	void configure(SecurityConfigBuilder securityConfigBuilder) {

		this.configurers.configure(securityConfigBuilder);
	}

}
