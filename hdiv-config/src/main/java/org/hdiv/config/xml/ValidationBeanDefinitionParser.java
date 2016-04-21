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
package org.hdiv.config.xml;

import org.hdiv.validator.Validation;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * BeanDefinitionParser for &lt;hdiv:validation&gt; element.
 */
public class ValidationBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	protected Class<?> getBeanClass(Element element) {
		return Validation.class;
	}

	protected void doParse(Element element, BeanDefinitionBuilder bean) {

		String id = element.getAttribute("id");
		bean.addPropertyValue("name", id);

		String componentType = element.getAttribute("componentType");

		if (StringUtils.hasText(componentType)) {
			bean.addPropertyValue("componentType", componentType);

		}

		NodeList list = element.getChildNodes();

		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getLocalName().equalsIgnoreCase("acceptedPattern")) {

					String value = node.getTextContent();
					if (StringUtils.hasText(value)) {
						bean.addPropertyValue("acceptedPattern", value);
					}
				}

				if (node.getLocalName().equalsIgnoreCase("rejectedPattern")) {

					String value = node.getTextContent();
					if (StringUtils.hasText(value)) {
						bean.addPropertyValue("rejectedPattern", value);
					}
				}
			}
		}

	}
}
