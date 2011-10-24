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

import org.hdiv.application.ApplicationHDIV;
import org.hdiv.cipher.CipherHTTP;
import org.hdiv.cipher.KeyFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.config.multipart.SpringMVCMultipartConfig;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataValidator.DataValidatorFactory;
import org.hdiv.dataValidator.ValidationResult;
import org.hdiv.filter.ValidatorHelperRequest;
import org.hdiv.idGenerator.RandomGuidUidGenerator;
import org.hdiv.idGenerator.SequentialPageIdGenerator;
import org.hdiv.logs.Logger;
import org.hdiv.logs.UserData;
import org.hdiv.session.SessionHDIV;
import org.hdiv.session.StateCache;
import org.hdiv.state.StateUtil;
import org.hdiv.util.EncodingUtil;
import org.hdiv.web.servlet.support.HdivRequestDataValueProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.scheduling.config.AnnotationDrivenBeanDefinitionParser;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * BeanDefinitionParser for <hdiv:config> element.
 */
public class ConfigBeanDefinitionParser implements BeanDefinitionParser {

	private final boolean springMvcPresent = ClassUtils.isPresent("org.springframework.web.servlet.DispatcherServlet",
			AnnotationDrivenBeanDefinitionParser.class.getClassLoader());

	public BeanDefinition parse(Element element, ParserContext parserContext) {

		Object source = parserContext.extractSource(element);

		parserContext.getRegistry().registerBeanDefinition("config", this.createConfigBean(element, source));

		parserContext.getRegistry().registerBeanDefinition("uidGenerator",
				this.createUidGenerator(element, source, parserContext));
		parserContext.getRegistry().registerBeanDefinition("pageIdGenerator",
				this.createPageIdGenerator(element, source));
		parserContext.getRegistry().registerBeanDefinition("keyFactory", this.createKeyFactory(element, source));
		parserContext.getRegistry().registerBeanDefinition("userData", this.createUserData(element, source));
		parserContext.getRegistry().registerBeanDefinition("logger", this.createLogger(element, source));
		parserContext.getRegistry().registerBeanDefinition("cache", this.createStateCache(element, source));
		parserContext.getRegistry().registerBeanDefinition("encoding", this.createEncodingUtil(element, source));
		parserContext.getRegistry().registerBeanDefinition("sessionHDIV", this.createSessionHDIV(element, source));
		parserContext.getRegistry().registerBeanDefinition("application", this.createApplication(element, source));
		parserContext.getRegistry().registerBeanDefinition("cipher", this.createCipher(element, source));
		parserContext.getRegistry().registerBeanDefinition("results", this.createValidationResult(element, source));
		parserContext.getRegistry().registerBeanDefinition("stateUtil", this.createStateUtil(element, source));
		parserContext.getRegistry().registerBeanDefinition("dataValidatorFactory",
				this.createDataValidatorFactory(element, source));
		parserContext.getRegistry().registerBeanDefinition("dataComposerFactory",
				this.createDataComposerFactory(element, source));
		parserContext.getRegistry().registerBeanDefinition("validatorHelper",
				this.createValidatorHelper(element, source));
		parserContext.getRegistry().registerBeanDefinition("hdivParameter",
				this.createStringBean("_HDIV_STATE_", source));
		parserContext.getRegistry().registerBeanDefinition("cacheName", this.createStringBean("cache", source));
		parserContext.getRegistry().registerBeanDefinition("pageIdGeneratorName",
				this.createStringBean("pageIdGenerator", source));
		parserContext.getRegistry().registerBeanDefinition("keyName", this.createStringBean("key", source));
		parserContext.getRegistry().registerBeanDefinition("messageSourcePath",
				this.createStringBean("org.hdiv.msg.MessageResources", source));

		// register Spring MVC beans if we are using Spring MVC web framework
		if (this.springMvcPresent) {
			parserContext.getRegistry().registerBeanDefinition("requestDataValueProcessor",
					this.createRequestDataValueProcessor(element, source));
			parserContext.getRegistry().registerBeanDefinition("multipartConfig",
					this.createSpringMVCMultipartConfig(element, source));

		}

		return null;

	}

	private RootBeanDefinition createUidGenerator(Element element, Object source, ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(RandomGuidUidGenerator.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		return bean;
	}

	private RootBeanDefinition createPageIdGenerator(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(SequentialPageIdGenerator.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setScope(BeanDefinition.SCOPE_PROTOTYPE);
		return bean;
	}

	private RootBeanDefinition createKeyFactory(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(KeyFactory.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().add("algorithm", "AES");
		bean.getPropertyValues().add("keySize", "128");
		bean.getPropertyValues().add("prngAlgorithm", "SHA1PRNG");
		bean.getPropertyValues().add("provider", "SUN");
		return bean;
	}

	private RootBeanDefinition createUserData(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(UserData.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		return bean;
	}

	private RootBeanDefinition createLogger(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(Logger.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().add("userData", new RuntimeBeanReference("userData"));
		return bean;
	}

	private RootBeanDefinition createStateCache(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(StateCache.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setScope(BeanDefinition.SCOPE_PROTOTYPE);
		bean.setInitMethodName("init");
		bean.getPropertyValues().add("maxSize", "5");
		return bean;
	}

	private RootBeanDefinition createEncodingUtil(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(EncodingUtil.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setInitMethodName("init");
		bean.getPropertyValues().add("session", new RuntimeBeanReference("sessionHDIV"));
		return bean;
	}

	private RootBeanDefinition createSessionHDIV(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(SessionHDIV.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setInitMethodName("init");
		bean.getPropertyValues().add("cipherName", "cipher");
		bean.getPropertyValues().add("pageIdGeneratorName", new RuntimeBeanReference("pageIdGeneratorName"));
		bean.getPropertyValues().add("cacheName", new RuntimeBeanReference("cacheName"));
		bean.getPropertyValues().add("keyName", new RuntimeBeanReference("keyName"));
		return bean;
	}

	private RootBeanDefinition createApplication(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(ApplicationHDIV.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		return bean;
	}

	private RootBeanDefinition createCipher(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(CipherHTTP.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setInitMethodName("init");
		bean.getPropertyValues().add("transformation", "AES/CBC/PKCS5Padding");
		return bean;
	}

	private RootBeanDefinition createValidationResult(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(ValidationResult.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		return bean;
	}

	private RootBeanDefinition createStateUtil(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(StateUtil.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setInitMethodName("init");
		bean.getPropertyValues().add("encodingUtil", new RuntimeBeanReference("encoding"));
		bean.getPropertyValues().add("config", new RuntimeBeanReference("config"));
		return bean;
	}

	private RootBeanDefinition createDataValidatorFactory(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(DataValidatorFactory.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().add("hdivConfig", new RuntimeBeanReference("config"));
		return bean;
	}

	private RootBeanDefinition createDataComposerFactory(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(DataComposerFactory.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().add("hdivConfig", new RuntimeBeanReference("config"));
		bean.getPropertyValues().add("session", new RuntimeBeanReference("sessionHDIV"));
		bean.getPropertyValues().add("encodingUtil", new RuntimeBeanReference("encoding"));
		bean.getPropertyValues().add("uidGenerator", new RuntimeBeanReference("uidGenerator"));
		bean.getPropertyValues().add("allowedLength", "4000");

		return bean;
	}

	private RootBeanDefinition createValidatorHelper(Element element, Object source) {

		RootBeanDefinition bean = new RootBeanDefinition(ValidatorHelperRequest.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setInitMethodName("init");
		bean.getPropertyValues().add("logger", new RuntimeBeanReference("logger"));
		bean.getPropertyValues().add("stateUtil", new RuntimeBeanReference("stateUtil"));
		bean.getPropertyValues().add("hdivConfig", new RuntimeBeanReference("config"));
		bean.getPropertyValues().add("session", new RuntimeBeanReference("sessionHDIV"));
		bean.getPropertyValues().add("dataValidatorFactory", new RuntimeBeanReference("dataValidatorFactory"));
		return bean;
	}

	private RootBeanDefinition createStringBean(String value, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(java.lang.String.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getConstructorArgumentValues().addIndexedArgumentValue(0, value);
		return bean;
	}

	private RootBeanDefinition createSpringMVCMultipartConfig(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(SpringMVCMultipartConfig.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		return bean;
	}

	private RootBeanDefinition createRequestDataValueProcessor(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(HdivRequestDataValueProcessor.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().add("hdivConfig", new RuntimeBeanReference("config"));
		return bean;
	}

	private RootBeanDefinition createConfigBean(Element element, Object source) {

		RootBeanDefinition bean = new RootBeanDefinition(HDIVConfig.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

		String confidentiality = element.getAttribute("confidentiality");
		String avoidCookiesIntegrity = element.getAttribute("avoidCookiesIntegrity");
		String cookiesConfidentiality = element.getAttribute("avoidCookiesConfidentiality");
		String avoidValidationInUrlsWithoutParams = element.getAttribute("avoidValidationInUrlsWithoutParams");
		String strategy = element.getAttribute("strategy");
		String randomName = element.getAttribute("randomName");
		String errorPage = element.getAttribute("errorPage");
		String protectedExtensions = element.getAttribute("protectedExtensions");
		String excludedExtensions = element.getAttribute("excludedExtensions");

		if (StringUtils.hasText(confidentiality)) {
			bean.getPropertyValues().add("confidentiality", confidentiality);

		}

		if (StringUtils.hasText(avoidCookiesIntegrity)) {
			bean.getPropertyValues().add("cookiesIntegrity", avoidCookiesIntegrity);
		}

		if (StringUtils.hasText(avoidCookiesIntegrity)) {

			bean.getPropertyValues().add("cookiesConfidentiality", cookiesConfidentiality);
		}

		if (StringUtils.hasText(avoidValidationInUrlsWithoutParams)) {
			bean.getPropertyValues().add("avoidValidationInUrlsWithoutParams", avoidValidationInUrlsWithoutParams);
		}

		if (StringUtils.hasText(strategy)) {
			bean.getPropertyValues().add("strategy", strategy);
		}

		if (StringUtils.hasText(randomName)) {
			bean.getPropertyValues().add("randomName", randomName);
		}

		if (StringUtils.hasText(errorPage)) {
			bean.getPropertyValues().add("errorPage", errorPage);
		}

		if (StringUtils.hasText(protectedExtensions)) {
			bean.getPropertyValues().add("protectedExtensions", this.convertToList(protectedExtensions));
		}

		if (StringUtils.hasText(excludedExtensions)) {
			bean.getPropertyValues().add("excludedExtensions", this.convertToList(excludedExtensions));
		}

		bean.getPropertyValues().add("validations", new RuntimeBeanReference("editableParametersValidations"));

		// process startPages, startParameters and paramsWithoutValidation
		// elements
		this.processChilds(element, bean);
		return bean;

	}

	private void processChilds(Element element, RootBeanDefinition bean) {
		NodeList nodeList = element.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			if (node.getNodeName().equalsIgnoreCase("hdiv:startPages")) {
				this.processStartPages(node, bean);
			} else if (node.getNodeName().equalsIgnoreCase("hdiv:startParameters")) {
				this.processStartParameters(node, bean);

			} else if (node.getNodeName().equalsIgnoreCase("hdiv:paramsWithoutValidation")) {
				this.processParamsWithoutValidation(node, bean);
			}
		}
	}

	private void processStartPages(Node node, RootBeanDefinition bean) {
		String value = node.getTextContent();
		bean.getPropertyValues().add("userStartPages", this.convertToList(value));
	}

	private void processStartParameters(Node node, RootBeanDefinition bean) {
		String value = node.getTextContent();
		bean.getPropertyValues().add("userStartParameters", this.convertToList(value));
	}

	private void processParamsWithoutValidation(Node node, RootBeanDefinition bean) {
		NodeList nodeList = node.getChildNodes();

		Map map = new Hashtable();
		bean.getPropertyValues().add("ParamsWithoutValidation", map);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node mappingNode = nodeList.item(i);
			if (mappingNode.getNodeName().equalsIgnoreCase("hdiv:mapping")) {
				this.processMapping(mappingNode, map);
			}
		}
	}

	private void processMapping(Node node, Map map) {
		NamedNodeMap attributes = node.getAttributes();
		String url = attributes.getNamedItem("url").getTextContent();
		String parameters = attributes.getNamedItem("parameters").getTextContent();
		map.put(url, this.convertToList(parameters));
	}

	private List convertToList(String data) {
		String[] result = data.split(",");
		List list = Arrays.asList(result);
		return list;

	}

}
