/**
 * Copyright 2005-2013 hdiv.org
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hdiv.config.factory.ValidationsFactoryBean;
import org.hdiv.config.validations.DefaultValidationParser;
import org.hdiv.validator.IValidation;
import org.hdiv.validator.Validation;
import org.hdiv.web.validator.EditableParameterValidator;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.ClassUtils;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * BeanDefinitionParser for &lt;hdiv:editableValidations&gt; element.
 */
public class EditableValidationsBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	public static final String EDITABLE_VALIDATIONS_BEAN_NAME = "org.hdiv.editableValidations";

	public static final String DEFAULT_EDITABLE_VALIDATIONS_BEAN_NAME = "org.hdiv.defaultEditableValidations";

	public static final String EDITABLE_VALIDATOR_BEAN_NAME = "hdivEditableValidator";

	/**
	 * Is Spring MVC in classpath?
	 */
	private final boolean springMvcPresent = ClassUtils.isPresent("org.springframework.web.servlet.DispatcherServlet",
			EditableValidationsBeanDefinitionParser.class.getClassLoader());

	/**
	 * Is JSR303 library in classpath?
	 */
	private static final boolean jsr303Present = ClassUtils.isPresent("javax.validation.Validator",
			EditableValidationsBeanDefinitionParser.class.getClassLoader());

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#resolveId(org.w3c.dom.Element,
	 * org.springframework.beans.factory.support.AbstractBeanDefinition,
	 * org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext)
			throws BeanDefinitionStoreException {

		return EDITABLE_VALIDATIONS_BEAN_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser#getBeanClass(org.w3c.dom.Element)
	 */
	protected Class<?> getBeanClass(Element element) {
		return ValidationsFactoryBean.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser#doParse(org.w3c.dom.Element,
	 * org.springframework.beans.factory.xml.ParserContext,
	 * org.springframework.beans.factory.support.BeanDefinitionBuilder)
	 */
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder bean) {

		Object source = parserContext.extractSource(element);

		RuntimeBeanReference beanRef = new RuntimeBeanReference(ConfigBeanDefinitionParser.PATTERN_MATCHER_FACTORY_NAME);
		bean.getBeanDefinition().getPropertyValues().addPropertyValue("patternMatcherFactory", beanRef);

		Map<String, List<String>> map = new HashMap<String, List<String>>();
		bean.addPropertyValue("validationsData", map);

		// Register default editable validation
		boolean registerDefaults = true;
		Node named = element.getAttributes().getNamedItem("registerDefaults");
		if (named != null) {
			String registerDefaultsValue = named.getTextContent();
			if (registerDefaultsValue != null) {
				registerDefaults = Boolean.TRUE.toString().equalsIgnoreCase(registerDefaultsValue);
			}
		}

		if (registerDefaults) {
			// Create beans for default validations
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

		if (this.springMvcPresent) {
			parserContext.getRegistry().registerBeanDefinition(EDITABLE_VALIDATOR_BEAN_NAME,
					this.createValidator(element, source, parserContext));
		}
	}

	/**
	 * Initialize Map with url and ValidationRule data.
	 * 
	 * @param node
	 *            processing xml node
	 * @param bean
	 *            bean configuration
	 * @param map
	 *            Map with url and ValidationRule data
	 */
	private void processValidationRule(Node node, BeanDefinitionBuilder bean, Map<String, List<String>> map) {

		String value = node.getTextContent();
		List<String> ids = this.convertToList(value);

		NamedNodeMap attributes = node.getAttributes();
		Node named = attributes.getNamedItem("url");
		if (named != null) {
			String url = named.getTextContent();

			boolean enableDefaults = true;
			named = attributes.getNamedItem("enableDefaults");
			if (named != null) {
				String enableDefaultsVal = named.getTextContent();
				if (enableDefaultsVal != null) {
					enableDefaults = Boolean.TRUE.toString().equalsIgnoreCase(enableDefaultsVal);
				}
			}

			if (enableDefaults) {
				// Add defaults
				ids.add(DEFAULT_EDITABLE_VALIDATIONS_BEAN_NAME);
			}

			map.put(url, ids);
		}
	}

	/**
	 * Convert String with bean id's in List
	 * 
	 * @param data
	 *            String data
	 * @return List with bean id's
	 */
	private List<String> convertToList(String data) {
		data = data.trim();
		if (data == null || data.length() == 0) {
			return new ArrayList<String>();
		}
		String[] result = data.split(",");
		List<String> list = Arrays.asList(result);
		return new ArrayList<String>(list);

	}

	/**
	 * Create beans for the default editable validations.
	 * 
	 * @param element
	 *            xml element
	 * @param parserContext
	 *            xml parser context
	 */
	private void createDefaultEditableValidations(Element element, ParserContext parserContext) {

		// Load validations from xml
		DefaultValidationParser parser = new DefaultValidationParser();
		parser.readDefaultValidations();
		List<Map<String, String>> validations = parser.getValidations();

		List<IValidation> defaultValidations = new ArrayList<IValidation>();

		for (Map<String, String> validation : validations) {
			// Map contains validation id and regex extracted from the xml
			String id = validation.get("id");
			String regex = validation.get("regex");

			// Create validation instance
			Validation validationBean = new Validation();
			validationBean.setName(id);
			validationBean.setRejectedPattern(regex);

			defaultValidations.add(validationBean);
		}

		Object source = parserContext.extractSource(element);
		RootBeanDefinition bean = new RootBeanDefinition(ListFactoryBean.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("sourceList", defaultValidations);

		// Register validation list bean
		parserContext.getRegistry().registerBeanDefinition(DEFAULT_EDITABLE_VALIDATIONS_BEAN_NAME, bean);

	}

	private RootBeanDefinition createValidator(Element element, Object source, ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(EditableParameterValidator.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		if (jsr303Present) {

			RootBeanDefinition validatorDef = new RootBeanDefinition(LocalValidatorFactoryBean.class);
			validatorDef.setSource(source);
			validatorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			String validatorName = parserContext.getReaderContext().registerWithGeneratedName(validatorDef);
			parserContext.registerComponent(new BeanComponentDefinition(validatorDef, validatorName));

			bean.getPropertyValues().addPropertyValue("innerValidator", new RuntimeBeanReference(validatorName));
		}
		return bean;
	}

}