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
package org.hdiv.config.annotation.configuration;

import org.hdiv.config.annotation.springmvc.SpringMvcConfigurationSupport;
import org.hdiv.config.xml.ConfigBeanDefinitionParser;
import org.hdiv.urlProcessor.FormUrlProcessor;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.security.web.servlet.support.csrf.CsrfRequestDataValueProcessor;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public class ConfigTools {

	protected static final boolean springSecurityPresent = ClassUtils.isPresent(
			"org.springframework.security.web.servlet.support.csrf.CsrfRequestDataValueProcessor",
			SpringMvcConfigurationSupport.class.getClassLoader());

	public static BeanDefinitionRegistryPostProcessor requestDataValueProcessorPostProcessor(final Class<?> processorClass) {
		return new BeanDefinitionRegistryPostProcessor() {

			public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
			}

			public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry) throws BeansException {
				try {
					registry.removeBeanDefinition(ConfigBeanDefinitionParser.REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME);
				}
				catch (Exception e) {
				}

				RootBeanDefinition processor = new RootBeanDefinition(processorClass);
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
