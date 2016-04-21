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
package org.hdiv.config.annotation.condition;

import java.util.List;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;

/**
 * {@link ConfigurationCondition} for {@link ConditionalOnFramework} condition.
 * 
 * @since 2.1.7
 */
public class OnFrameworkCondition implements ConfigurationCondition {

	protected final boolean springMvcModulePresent = ClassUtils.isPresent("org.hdiv.web.servlet.support.HdivRequestDataValueProcessor",
			OnFrameworkCondition.class.getClassLoader());

	protected final boolean grailsModulePresent = ClassUtils.isPresent("org.hdiv.web.servlet.support.GrailsHdivRequestDataValueProcessor",
			OnFrameworkCondition.class.getClassLoader());

	protected final boolean grailsPresent = ClassUtils.isPresent("org.codehaus.groovy.grails.web.servlet.GrailsDispatcherServlet",
			OnFrameworkCondition.class.getClassLoader());

	protected final boolean jsfPresent = ClassUtils.isPresent("javax.faces.webapp.FacesServlet",
			OnFrameworkCondition.class.getClassLoader());

	protected final boolean jsfModulePresent = ClassUtils.isPresent("org.hdiv.filter.JsfValidatorHelper",
			OnFrameworkCondition.class.getClassLoader());

	protected final boolean struts1ModulePresent = ClassUtils.isPresent("org.hdiv.action.HDIVRequestProcessor",
			OnFrameworkCondition.class.getClassLoader());

	protected final boolean thymeleafModulePresent = ClassUtils
			.isPresent("org.hdiv.web.servlet.support.ThymeleafHdivRequestDataValueProcessor", OnFrameworkCondition.class.getClassLoader());

	public ConfigurationPhase getConfigurationPhase() {
		return ConfigurationPhase.PARSE_CONFIGURATION;
	}

	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

		MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(ConditionalOnFramework.class.getName(), true);

		List<Object> values = attributes.get("value");
		Assert.notEmpty(values);
		SupportedFramework frwk = (SupportedFramework) values.get(0);

		if (frwk == SupportedFramework.SPRING_MVC) {
			return springMvcModulePresent;
		}
		else if (frwk == SupportedFramework.THYMELEAF) {
			return thymeleafModulePresent;
		}
		else if (frwk == SupportedFramework.GRAILS) {
			return grailsPresent && grailsModulePresent;
		}
		else if (frwk == SupportedFramework.JSF) {
			return jsfPresent && jsfModulePresent;
		}
		else if (frwk == SupportedFramework.STRUTS1) {
			return struts1ModulePresent;
		}
		else {
			return false;
		}
	}

}
