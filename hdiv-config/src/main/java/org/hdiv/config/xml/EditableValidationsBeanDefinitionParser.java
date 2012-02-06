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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hdiv.config.HDIVValidations;
import org.hdiv.config.validations.DefaultValidationParser;
import org.hdiv.validator.Validation;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * BeanDefinitionParser for <hdiv:editableValidations> element.
 */
public class EditableValidationsBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	/**
	 * Location of the xml file with default editable validations.
	 */
	private static final String DEFAULT_VALIDATION_PATH = "org/hdiv/config/validations/defaultEditableValidations.xml";

	/**
	 * List with default editable validation bean ids.
	 */
	private List defaultValidationIds = new ArrayList();;

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
	 * org.springframework.beans.factory.xml.ParserContext, org.springframework.beans.factory.support.BeanDefinitionBuilder)
	 */
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder bean) {

		Map map = new Hashtable();
		bean.addPropertyValue("rawUrls", map);
		bean.setInitMethodName("init");

		//Register default editable validation
		boolean registerDefaults = true;
		String registerDefaultsValue = element.getAttributes().getNamedItem("registerDefaults").getTextContent();
		if (registerDefaultsValue != null) {
			registerDefaults = Boolean.TRUE.toString().equalsIgnoreCase(registerDefaultsValue);
		}

		if (registerDefaults) {
			//Create beans for default validations
			createDefaultEditableValidations(element, parserContext);
		}

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
		List ids = this.convertToList(value);

		NamedNodeMap attributes = node.getAttributes();
		String url = attributes.getNamedItem("url").getTextContent();

		boolean enableDefaults = false;
		String enableDefaultsVal = attributes.getNamedItem("enableDefaults").getTextContent();
		if (enableDefaultsVal != null) {
			enableDefaults = Boolean.TRUE.toString().equalsIgnoreCase(enableDefaultsVal);
		}
		if (enableDefaults) {
			// Add defaults
			ids.addAll(this.defaultValidationIds);
		}

		map.put(url, ids);

	}

	/**
	 * Convert String with bean id's in List
	 * @param data String data
	 * @return List with bean id's
	 */
	private List convertToList(String data) {
		data = data.trim();
		if (data == null || data.length() == 0) {
			return new ArrayList();
		}
		String[] result = data.split(",");
		List list = Arrays.asList(result);
		return new ArrayList(list);

	}

	/**
	 * Create beans for the default editable validations.
	 * @param element xml element
	 * @param parserContext xml parser context
	 */
	private void createDefaultEditableValidations(Element element, ParserContext parserContext) {

		//Load validations from xml
		DefaultValidationParser parser = new DefaultValidationParser();
		parser.readDefaultValidations(DEFAULT_VALIDATION_PATH);
		List validations = parser.getValidations();

		this.defaultValidationIds = new ArrayList();

		Iterator it = validations.iterator();
		while (it.hasNext()) {
			
			// Map contains validation id and regex extracted from the xml
			Map validation = (Map) it.next();
			String id = (String) validation.get("id");
			id = "defaultValidation_" + id;
			String regex = (String) validation.get("regex");

			this.defaultValidationIds.add(id);

			// Create bean for the validation
			Object source = parserContext.extractSource(element);
			RootBeanDefinition bean = new RootBeanDefinition(Validation.class);
			bean.setSource(source);
			bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			bean.getPropertyValues().add("rejectedPattern", regex);

			// Register bean
			parserContext.getRegistry().registerBeanDefinition(id, bean);

		}

	}

}