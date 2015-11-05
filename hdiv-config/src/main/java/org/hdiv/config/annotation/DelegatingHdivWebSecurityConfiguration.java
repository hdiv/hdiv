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

import java.util.List;

import javax.annotation.PostConstruct;

import org.hdiv.config.annotation.builders.SecurityConfigBuilder;
import org.hdiv.config.annotation.configuration.HdivWebSecurityConfigurer;
import org.hdiv.config.annotation.configuration.HdivWebSecurityConfigurerComposite;
import org.hdiv.exception.HDIVException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.SpringVersion;

@Configuration
public class DelegatingHdivWebSecurityConfiguration extends HdivWebSecurityConfigurationSupport {

	/**
	 * Minimum Spring version to use HDIV JavaConfig feature.
	 */
	protected static final String MIN_SPRING_VERSION = "4.0.0.RELEASE";

	/**
	 * Composite with all {@link HdivWebSecurityConfigurer} instances.
	 */
	private final HdivWebSecurityConfigurerComposite configurers = new HdivWebSecurityConfigurerComposite();

	@PostConstruct
	public void performVersionChecks() {
		String springVersion = SpringVersion.getVersion();
		if (springVersion != null && springVersion.compareTo(MIN_SPRING_VERSION) < 0) {
			// Spring version is lower than '4.0.0.RELEASE'
			throw new HDIVException("HDIV JavaConfig feature require Spring version equal or greater than "
					+ MIN_SPRING_VERSION + ". Use XML configuration instead of JavaConfig or update Spring version.");
		}
	}

	@Autowired(required = false)
	public void setConfigurers(List<HdivWebSecurityConfigurer> configurers) {
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
	public void addLongLivingPages(LongLivingPagesRegistry registry) {

		this.configurers.addLongLivingPages(registry);
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
	public void configure(SecurityConfigBuilder securityConfigBuilder) {

		this.configurers.configure(securityConfigBuilder);
	}

}
