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
package org.hdiv.config.annotation.grails;

import org.hdiv.config.annotation.condition.ConditionalOnFramework;
import org.hdiv.config.annotation.condition.SupportedFramework;
import org.hdiv.config.annotation.configuration.ConfigTools;
import org.hdiv.web.servlet.support.GrailsHdivRequestDataValueProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Contains the configuration beans for Grails framework support.
 * 
 * @since 2.1.7
 */
@Configuration
@ConditionalOnFramework(SupportedFramework.GRAILS)
public class GrailsConfigurationSupport {

	@Bean
	public static BeanDefinitionRegistryPostProcessor requestDataValueProcessorPostProcessor() {
		return ConfigTools.requestDataValueProcessorPostProcessor(GrailsHdivRequestDataValueProcessor.class);
	}

}
