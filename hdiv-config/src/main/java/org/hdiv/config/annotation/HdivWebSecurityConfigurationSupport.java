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

import org.hdiv.config.annotation.grails.GrailsConfigurationSupport;
import org.hdiv.config.annotation.jsf.JsfConfigurationSupport;
import org.hdiv.config.annotation.springmvc.SpringMvcConfigurationSupport;
import org.hdiv.config.annotation.struts1.Struts1ConfigurationSupport;
import org.hdiv.config.annotation.thymeleaf.ThymeleafConfigurationSupport;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Main class of {@link Configuration} support.
 * 
 * @since 2.1.7
 */
@Import({ SpringMvcConfigurationSupport.class, ThymeleafConfigurationSupport.class, GrailsConfigurationSupport.class,
		JsfConfigurationSupport.class, Struts1ConfigurationSupport.class })
public class HdivWebSecurityConfigurationSupport extends AbstractHdivWebSecurityConfiguration {

}
