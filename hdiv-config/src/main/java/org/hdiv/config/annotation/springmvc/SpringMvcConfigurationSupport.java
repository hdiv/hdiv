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
package org.hdiv.config.annotation.springmvc;

import org.hdiv.config.annotation.condition.ConditionalOnFramework;
import org.hdiv.config.annotation.condition.SupportedFramework;
import org.hdiv.config.annotation.configuration.ConfigTools;
import org.hdiv.config.multipart.IMultipartConfig;
import org.hdiv.config.multipart.SpringMVCMultipartConfig;
import org.hdiv.config.xml.ConfigBeanDefinitionParser;
import org.hdiv.config.xml.EditableValidationsBeanDefinitionParser;
import org.hdiv.urlProcessor.FormUrlProcessor;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.web.servlet.support.HdivRequestDataValueProcessor;
import org.hdiv.web.validator.EditableParameterValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.web.servlet.support.csrf.CsrfRequestDataValueProcessor;
import org.springframework.util.ClassUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

/**
 * Contains the configuration beans for Spring MVC framework support.
 *
 * @since 2.1.7
 */
@Configuration
@ConditionalOnFramework(SupportedFramework.SPRING_MVC)
public class SpringMvcConfigurationSupport implements ApplicationListener<ContextRefreshedEvent> {

	protected static final boolean jsr303Present = ClassUtils.isPresent("javax.validation.Validator",
			SpringMvcConfigurationSupport.class.getClassLoader());

	public void onApplicationEvent(final ContextRefreshedEvent event) {
		ApplicationContext applicationContext = event.getApplicationContext();
		if (applicationContext
				.getBean(ConfigBeanDefinitionParser.REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME) instanceof CsrfRequestDataValueProcessor) {
			if (applicationContext instanceof ConfigurableApplicationContext) {
				@SuppressWarnings("resource")
				ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
				if (context.getBeanFactory() instanceof DefaultSingletonBeanRegistry) {
					DefaultSingletonBeanRegistry factory = (DefaultSingletonBeanRegistry) context.getBeanFactory();
					factory.destroySingleton(ConfigBeanDefinitionParser.REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME);

					context.getBeanFactory().registerSingleton(ConfigBeanDefinitionParser.REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME,
							requestDataValueProcessor());
				}
			}

		}
	}

	@Autowired
	protected FormUrlProcessor formUrlProcessor;

	@Autowired
	protected LinkUrlProcessor linkUrlProcessor;

	public RequestDataValueProcessor requestDataValueProcessor() {
		HdivRequestDataValueProcessor processor = new HdivRequestDataValueProcessor();
		processor.setFormUrlProcessor(formUrlProcessor);
		processor.setLinkUrlProcessor(linkUrlProcessor);
		if (ConfigTools.springSecurityPresent) {
			processor.setInnerRequestDataValueProcessor(new CsrfRequestDataValueProcessor());
		}
		return processor;
	}

	@Bean
	public static BeanDefinitionRegistryPostProcessor requestDataValueProcessorPostProcessor() {
		return ConfigTools.requestDataValueProcessorPostProcessor(HdivRequestDataValueProcessor.class);
	}

	@Bean(name = EditableValidationsBeanDefinitionParser.EDITABLE_VALIDATOR_BEAN_NAME)
	public Validator editableParameterValidator() {

		EditableParameterValidator validator = new EditableParameterValidator();
		if (jsr303Present) {
			validator.setInnerValidator(editableLocalValidatorFactoryBean());
		}
		return validator;
	}

	@Bean
	public LocalValidatorFactoryBean editableLocalValidatorFactoryBean() {
		return new LocalValidatorFactoryBean();
	}

	@Bean
	public IMultipartConfig securityMultipartConfig() {
		return new SpringMVCMultipartConfig();
	}
}
