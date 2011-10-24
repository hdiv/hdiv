/**
 * Copyright 2005-2011 hdiv.org
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
package org.hdiv.config.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Main configuration for the Hdiv namespace.
 */
public class HdivNamespaceHandler extends NamespaceHandlerSupport {

	/**
	 * Register custom BeanDefinitionParser.
	 */
	public void init() {
		this.registerBeanDefinitionParser("validation", new ValidationBeanDefinitionParser());
		this.registerBeanDefinitionParser("config", new ConfigBeanDefinitionParser());
		this.registerBeanDefinitionParser("editableValidations", new EditableValidationsBeanDefinitionParser());

	}
}
