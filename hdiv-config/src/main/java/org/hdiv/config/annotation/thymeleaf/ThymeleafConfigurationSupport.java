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
package org.hdiv.config.annotation.thymeleaf;

import org.hdiv.config.annotation.condition.ConditionalOnFramework;
import org.hdiv.config.annotation.condition.SupportedFramework;
import org.hdiv.config.xml.ConfigBeanDefinitionParser;
import org.hdiv.urlProcessor.FormUrlProcessor;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.web.servlet.support.ThymeleafHdivRequestDataValueProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.servlet.support.csrf.CsrfRequestDataValueProcessor;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Contains the configuration beans for Thymeleaf support.
 * 
 * @since 2.1.7
 */
@Configuration
@ConditionalOnFramework(SupportedFramework.THYMELEAF)
public class ThymeleafConfigurationSupport {

	protected static final boolean springSecurityPresent = ClassUtils.isPresent(
			"org.springframework.security.web.servlet.support.csrf.CsrfRequestDataValueProcessor",
			ThymeleafConfigurationSupport.class.getClassLoader());

	@Bean
	public static BeanDefinitionRegistryPostProcessor requestDataValueProcessorPostProcessor() {
		return new BeanDefinitionRegistryPostProcessor() {

			public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
			}

			public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry) throws BeansException {
				registry.removeBeanDefinition(ConfigBeanDefinitionParser.REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME);
				RootBeanDefinition processor = new RootBeanDefinition(ThymeleafHdivRequestDataValueProcessor.class);
				processor.getPropertyValues().addPropertyValue("formUrlProcessor",
						registry.getBeanDefinition(StringUtils.uncapitalize(FormUrlProcessor.class.getSimpleName())));
				processor.getPropertyValues().addPropertyValue("linkUrlProcessor",
						registry.getBeanDefinition(StringUtils.uncapitalize(LinkUrlProcessor.class.getSimpleName())));
				if (springSecurityPresent) {
					processor.getPropertyValues().addPropertyValue("innerRequestDataValueProcessor",
							new RootBeanDefinition(CsrfRequestDataValueProcessor.class));
				}
				registry.registerBeanDefinition(ConfigBeanDefinitionParser.REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME, processor);
			}
		};
	}

}
