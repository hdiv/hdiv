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

import java.util.Hashtable;
import java.util.Map;

import org.hdiv.config.HDIVValidations;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * BeanDefinitionParser for <hdiv:editableValidations> element.
 */
public class EditableValidationsBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	protected Class getBeanClass(Element element) {
		return HDIVValidations.class;
	}

	protected void doParse(Element element, BeanDefinitionBuilder bean) {

		Map map = new Hashtable();
		bean.addPropertyValue("xmlData", map);
		bean.setInitMethodName("init");

		NodeList list = element.getChildNodes();

		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node.getNodeName().equalsIgnoreCase("hdiv:validationRule")) {

				this.processValidationRule(node, bean, map);
			}
		}
	}

	private void processValidationRule(Node node, BeanDefinitionBuilder bean, Map map) {

		String value = node.getTextContent();
		NamedNodeMap attributes = node.getAttributes();
		String url = attributes.getNamedItem("url").getTextContent();
		map.put(url, value);

	}

}
