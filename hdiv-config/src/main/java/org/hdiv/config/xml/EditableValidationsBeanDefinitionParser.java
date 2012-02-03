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

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
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

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser#getBeanClass(org.w3c.dom.Element)
	 */
	protected Class getBeanClass(Element element) {
		return HDIVValidations.class;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser#doParse(org.w3c.dom.Element, 
	 * org.springframework.beans.factory.support.BeanDefinitionBuilder)
	 */
	protected void doParse(Element element, BeanDefinitionBuilder bean) {

		Map map = new Hashtable();
		bean.addPropertyValue("rawUrls", map);
		bean.setInitMethodName("init");

		NodeList list = element.getChildNodes();

		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getLocalName().equalsIgnoreCase("validationRule")) {
	
					this.processValidationRule(node, bean, map);
				}
			}
		}
	}

	/**
	 * Initialize Map with url and ValidationRule data.
	 * @param node processing xml node
	 * @param bean bean configuration
	 * @param map Map with url and ValidationRule data
	 */
	private void processValidationRule(Node node, BeanDefinitionBuilder bean, Map map) {

		String value = node.getTextContent();
		NamedNodeMap attributes = node.getAttributes();
		String url = attributes.getNamedItem("url").getTextContent();
		List ids = this.convertToList(value);
		map.put(url, ids);

	}

	/**
	 * Convert String with bean id's in List
	 * @param data String data
	 * @return List with bean id's
	 */
	private List convertToList(String data) {
		String[] result = data.split(",");
		List list = Arrays.asList(result);
		return list;

	}

}
