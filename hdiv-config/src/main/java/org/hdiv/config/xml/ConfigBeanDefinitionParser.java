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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.hdiv.application.ApplicationHDIV;
import org.hdiv.cipher.CipherHTTP;
import org.hdiv.cipher.KeyFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.config.HDIVValidations;
import org.hdiv.config.StartPage;
import org.hdiv.config.multipart.SpringMVCMultipartConfig;
import org.hdiv.context.RedirectHelper;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataValidator.DataValidatorFactory;
import org.hdiv.dataValidator.ValidationResult;
import org.hdiv.events.HDIVFacesEventListener;
import org.hdiv.exception.HDIVException;
import org.hdiv.filter.JsfValidatorHelper;
import org.hdiv.filter.ValidatorHelperRequest;
import org.hdiv.idGenerator.RandomGuidUidGenerator;
import org.hdiv.idGenerator.SequentialPageIdGenerator;
import org.hdiv.logs.Logger;
import org.hdiv.logs.UserData;
import org.hdiv.session.SessionHDIV;
import org.hdiv.session.StateCache;
import org.hdiv.state.StateUtil;
import org.hdiv.urlProcessor.FormUrlProcessor;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.util.EncodingUtil;
import org.hdiv.validators.EditableValidator;
import org.hdiv.validators.HtmlInputHiddenValidator;
import org.hdiv.validators.RequestParameterValidator;
import org.hdiv.validators.UICommandValidator;
import org.hdiv.web.servlet.support.GrailsHdivRequestDataValueProcessor;
import org.hdiv.web.servlet.support.HdivRequestDataValueProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
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

	/**
	 * List of StartPage objects
	 */
	private List startPages = new ArrayList();

	private final boolean springMvcPresent = ClassUtils.isPresent("org.springframework.web.servlet.DispatcherServlet",
			ConfigBeanDefinitionParser.class.getClassLoader());

	private final boolean grailsPresent = ClassUtils.isPresent(
			"org.codehaus.groovy.grails.web.servlet.GrailsDispatcherServlet",
			ConfigBeanDefinitionParser.class.getClassLoader());

	private final boolean jsfPresent = ClassUtils.isPresent("javax.faces.webapp.FacesServlet",
			ConfigBeanDefinitionParser.class.getClassLoader());

	public BeanDefinition parse(Element element, ParserContext parserContext) {

		Object source = parserContext.extractSource(element);

		parserContext.getRegistry().registerBeanDefinition("config",
				this.createConfigBean(element, source, parserContext));

		parserContext.getRegistry().registerBeanDefinition("uidGenerator",
				this.createUidGenerator(element, source, parserContext));
		parserContext.getRegistry().registerBeanDefinition("pageIdGenerator",
				this.createPageIdGenerator(element, source));
		parserContext.getRegistry().registerBeanDefinition("keyFactory", this.createKeyFactory(element, source));
		String userData = element.getAttribute("userData");
		if (userData == null || userData.length() < 1) {
			// If user dont define userData bean, create default
			parserContext.getRegistry().registerBeanDefinition("userData", this.createUserData(element, source));
		}
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
		parserContext.getRegistry().registerBeanDefinition("linkUrlProcessor",
				this.createLinkUrlProcessor(element, source));
		parserContext.getRegistry().registerBeanDefinition("formUrlProcessor",
				this.createFormUrlProcessor(element, source));
		parserContext.getRegistry().registerBeanDefinition("hdivParameter",
				this.createStringBean("_HDIV_STATE_", source));
		parserContext.getRegistry().registerBeanDefinition("modifyHdivStateParameter",
				this.createStringBean("_MODIFY_HDIV_STATE_", source));
		parserContext.getRegistry().registerBeanDefinition("cacheName", this.createStringBean("cache", source));
		parserContext.getRegistry().registerBeanDefinition("pageIdGeneratorName",
				this.createStringBean("pageIdGenerator", source));
		parserContext.getRegistry().registerBeanDefinition("keyName", this.createStringBean("key", source));
		parserContext.getRegistry().registerBeanDefinition("messageSourcePath",
				this.createStringBean("org.hdiv.msg.MessageResources", source));

		// register Spring MVC beans if we are using Spring MVC web framework
		if (this.grailsPresent) {
			parserContext.getRegistry().registerBeanDefinition("requestDataValueProcessor",
					this.createGrailsRequestDataValueProcessor(element, source));
			parserContext.getRegistry().registerBeanDefinition("multipartConfig",
					this.createSpringMVCMultipartConfig(element, source));
		} else if (this.springMvcPresent) {
			parserContext.getRegistry().registerBeanDefinition("requestDataValueProcessor",
					this.createRequestDataValueProcessor(element, source));
			parserContext.getRegistry().registerBeanDefinition("multipartConfig",
					this.createSpringMVCMultipartConfig(element, source));

		}

		// register JSF especific beans if we are using this web framework
		if (this.jsfPresent) {
			parserContext.getRegistry().registerBeanDefinition("HDIVFacesEventListener",
					this.createFacesEventListener(element, source));

			// Register ComponentValidator objects
			parserContext.getRegistry().registerBeanDefinition("requestParameterValidator",
					this.createRequestParameterValidator(element, source));
			parserContext.getRegistry().registerBeanDefinition("uiCommandValidator",
					this.createUiCommandValidator(element, source));
			parserContext.getRegistry().registerBeanDefinition("htmlInputHiddenValidator",
					this.createHtmlInputHiddenValidator(element, source));
			parserContext.getRegistry().registerBeanDefinition("editableValidator",
					this.createEditableValidator(element, source));
			parserContext.getRegistry().registerBeanDefinition("redirectHelper",
					this.createRedirectHelper(element, source));

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
		bean.getPropertyValues().addPropertyValue("algorithm", "AES");
		bean.getPropertyValues().addPropertyValue("keySize", "128");
		bean.getPropertyValues().addPropertyValue("prngAlgorithm", "SHA1PRNG");
		bean.getPropertyValues().addPropertyValue("provider", "SUN");
		return bean;
	}

	private RootBeanDefinition createUserData(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(UserData.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		return bean;
	}

	private RootBeanDefinition createLogger(Element element, Object source) {
		String userData = element.getAttribute("userData");
		if (userData == null || userData.length() < 1) {
			userData = "userData";// default userData bean id
		}
		RootBeanDefinition bean = new RootBeanDefinition(Logger.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("userData", new RuntimeBeanReference(userData));
		return bean;
	}

	private RootBeanDefinition createStateCache(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(StateCache.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setScope(BeanDefinition.SCOPE_PROTOTYPE);
		bean.setInitMethodName("init");

		String maxSize = element.getAttribute("maxPagesPerSession");
		if (StringUtils.hasText(maxSize)) {
			bean.getPropertyValues().addPropertyValue("maxSize", maxSize);
		}
		return bean;
	}

	private RootBeanDefinition createEncodingUtil(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(EncodingUtil.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setInitMethodName("init");
		bean.getPropertyValues().addPropertyValue("session", new RuntimeBeanReference("sessionHDIV"));
		return bean;
	}

	private RootBeanDefinition createSessionHDIV(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(SessionHDIV.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setInitMethodName("init");
		bean.getPropertyValues().addPropertyValue("cipherName", "cipher");
		bean.getPropertyValues().addPropertyValue("pageIdGeneratorName",
				new RuntimeBeanReference("pageIdGeneratorName"));
		bean.getPropertyValues().addPropertyValue("cacheName", new RuntimeBeanReference("cacheName"));
		bean.getPropertyValues().addPropertyValue("keyName", new RuntimeBeanReference("keyName"));
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
		bean.getPropertyValues().addPropertyValue("transformation", "AES/CBC/PKCS5Padding");
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
		bean.getPropertyValues().addPropertyValue("encodingUtil", new RuntimeBeanReference("encoding"));
		bean.getPropertyValues().addPropertyValue("config", new RuntimeBeanReference("config"));
		return bean;
	}

	private RootBeanDefinition createDataValidatorFactory(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(DataValidatorFactory.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("hdivConfig", new RuntimeBeanReference("config"));
		return bean;
	}

	private RootBeanDefinition createDataComposerFactory(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(DataComposerFactory.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("hdivConfig", new RuntimeBeanReference("config"));
		bean.getPropertyValues().addPropertyValue("session", new RuntimeBeanReference("sessionHDIV"));
		bean.getPropertyValues().addPropertyValue("encodingUtil", new RuntimeBeanReference("encoding"));
		bean.getPropertyValues().addPropertyValue("uidGenerator", new RuntimeBeanReference("uidGenerator"));
		bean.getPropertyValues().addPropertyValue("allowedLength", "4000");

		return bean;
	}

	private RootBeanDefinition createValidatorHelper(Element element, Object source) {

		RootBeanDefinition bean = null;
		if (this.jsfPresent) {
			bean = new RootBeanDefinition(JsfValidatorHelper.class);
		} else {
			bean = new RootBeanDefinition(ValidatorHelperRequest.class);
		}
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setInitMethodName("init");
		bean.getPropertyValues().addPropertyValue("logger", new RuntimeBeanReference("logger"));
		bean.getPropertyValues().addPropertyValue("stateUtil", new RuntimeBeanReference("stateUtil"));
		bean.getPropertyValues().addPropertyValue("hdivConfig", new RuntimeBeanReference("config"));
		bean.getPropertyValues().addPropertyValue("session", new RuntimeBeanReference("sessionHDIV"));
		bean.getPropertyValues().addPropertyValue("dataValidatorFactory",
				new RuntimeBeanReference("dataValidatorFactory"));
		return bean;
	}

	private RootBeanDefinition createLinkUrlProcessor(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(LinkUrlProcessor.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("config", new RuntimeBeanReference("config"));

		return bean;
	}

	private RootBeanDefinition createFormUrlProcessor(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(FormUrlProcessor.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("config", new RuntimeBeanReference("config"));

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
		bean.getPropertyValues().addPropertyValue("linkUrlProcessor", new RuntimeBeanReference("linkUrlProcessor"));
		bean.getPropertyValues().addPropertyValue("formUrlProcessor", new RuntimeBeanReference("formUrlProcessor"));
		return bean;
	}

	private RootBeanDefinition createGrailsRequestDataValueProcessor(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(GrailsHdivRequestDataValueProcessor.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("linkUrlProcessor", new RuntimeBeanReference("linkUrlProcessor"));
		bean.getPropertyValues().addPropertyValue("formUrlProcessor", new RuntimeBeanReference("formUrlProcessor"));
		return bean;
	}

	private RootBeanDefinition createConfigBean(Element element, Object source, ParserContext parserContext) {

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
		String debugMode = element.getAttribute("debugMode");
		String showErrorPageOnEditableValidation = element.getAttribute("showErrorPageOnEditableValidation");

		if (StringUtils.hasText(confidentiality)) {
			if (jsfPresent == true && confidentiality.equalsIgnoreCase("true")) {
				throw new HDIVException(
						"Confidentiality is not implemented in HDIV for JSF, disable it in hdiv-config.xml");
			}
			bean.getPropertyValues().addPropertyValue("confidentiality", confidentiality);
		}

		if (StringUtils.hasText(avoidCookiesIntegrity)) {
			if (jsfPresent == true && avoidCookiesIntegrity.equalsIgnoreCase("false")) {
				throw new HDIVException(
						"CookiesIntegrity is not implemented in HDIV for JSF, disable it in hdiv-config.xml");
			}
			bean.getPropertyValues().addPropertyValue("cookiesIntegrity", avoidCookiesIntegrity);
		}

		if (StringUtils.hasText(cookiesConfidentiality)) {

			if (jsfPresent == true && cookiesConfidentiality.equalsIgnoreCase("false")) {
				throw new HDIVException(
						"CookiesConfidentiality is not implemented in HDIV for JSF, disable it in hdiv-config.xml");
			}

			bean.getPropertyValues().addPropertyValue("cookiesConfidentiality", cookiesConfidentiality);
		}

		if (StringUtils.hasText(avoidValidationInUrlsWithoutParams)) {
			bean.getPropertyValues().addPropertyValue("avoidValidationInUrlsWithoutParams",
					avoidValidationInUrlsWithoutParams);
		}

		if (StringUtils.hasText(strategy)) {
			bean.getPropertyValues().addPropertyValue("strategy", strategy);
		}

		if (StringUtils.hasText(randomName)) {
			bean.getPropertyValues().addPropertyValue("randomName", randomName);
		}

		if (StringUtils.hasText(errorPage)) {
			bean.getPropertyValues().addPropertyValue("errorPage", errorPage);
		}

		if (StringUtils.hasText(protectedExtensions)) {
			bean.getPropertyValues().addPropertyValue("protectedExtensions", this.convertToList(protectedExtensions));
		}

		if (StringUtils.hasText(excludedExtensions)) {
			bean.getPropertyValues().addPropertyValue("excludedExtensions", this.convertToList(excludedExtensions));
		}

		if (StringUtils.hasText(debugMode)) {
			bean.getPropertyValues().addPropertyValue("debugMode", debugMode);
		}

		if (StringUtils.hasText(showErrorPageOnEditableValidation)) {
			bean.getPropertyValues().addPropertyValue("showErrorPageOnEditableValidation",
					showErrorPageOnEditableValidation);
		}

		bean.getPropertyValues().addPropertyValue("validations",
				new RuntimeBeanReference("editableParametersValidations"));

		if (!parserContext.getRegistry().containsBeanDefinition("editableParametersValidations")) {
			parserContext.getRegistry().registerBeanDefinition("editableParametersValidations",
					this.createDefaultEditableParametersValidations(element, source));
		}

		// Process startPages, startParameters and paramsWithoutValidation elements
		this.processChilds(element, bean);
		return bean;

	}

	private RootBeanDefinition createDefaultEditableParametersValidations(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(HDIVValidations.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setInitMethodName("init");
		Map map = new Hashtable();
		bean.getPropertyValues().addPropertyValue("rawUrls", map);
		return bean;
	}

	private RootBeanDefinition createFacesEventListener(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(HDIVFacesEventListener.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("config", new RuntimeBeanReference("config"));
		bean.getPropertyValues().addPropertyValue("logger", new RuntimeBeanReference("logger"));
		bean.getPropertyValues().addPropertyValue("htmlInputHiddenValidator",
				new RuntimeBeanReference("htmlInputHiddenValidator"));
		bean.getPropertyValues().addPropertyValue("requestParamValidator",
				new RuntimeBeanReference("requestParameterValidator"));
		bean.getPropertyValues().addPropertyValue("uiCommandValidator", new RuntimeBeanReference("uiCommandValidator"));
		bean.getPropertyValues().addPropertyValue("editabeValidator", new RuntimeBeanReference("editableValidator"));
		return bean;
	}

	// JSF Beans

	private RootBeanDefinition createRequestParameterValidator(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(RequestParameterValidator.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("hdivConfig", new RuntimeBeanReference("config"));
		return bean;
	}

	private RootBeanDefinition createUiCommandValidator(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(UICommandValidator.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		return bean;
	}

	private RootBeanDefinition createHtmlInputHiddenValidator(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(HtmlInputHiddenValidator.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		return bean;
	}

	private RootBeanDefinition createEditableValidator(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(EditableValidator.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("hdivConfig", new RuntimeBeanReference("config"));
		return bean;
	}

	private RootBeanDefinition createRedirectHelper(Element element, Object source) {
		RootBeanDefinition bean = new RootBeanDefinition(RedirectHelper.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("linkUrlProcessor", new RuntimeBeanReference("linkUrlProcessor"));
		return bean;
	}

	private void processChilds(Element element, RootBeanDefinition bean) {
		NodeList nodeList = element.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getLocalName().equalsIgnoreCase("startPages")) {
					this.processStartPages(node, bean);
				} else if (node.getLocalName().equalsIgnoreCase("startParameters")) {
					this.processStartParameters(node, bean);

				} else if (node.getLocalName().equalsIgnoreCase("paramsWithoutValidation")) {
					this.processParamsWithoutValidation(node, bean);
				}
			}
		}
	}

	private void processStartPages(Node node, RootBeanDefinition bean) {

		String method = null;
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) node;
			method = element.getAttribute("method");
		}

		String value = node.getTextContent();

		List patterns = this.convertToList(value);
		for (int i = 0; i < patterns.size(); i++) {
			String pattern = (String) patterns.get(i);
			StartPage startPage = new StartPage(method, pattern);
			this.startPages.add(startPage);
		}

		bean.getPropertyValues().addPropertyValue("userStartPages", this.startPages);
	}

	private void processStartParameters(Node node, RootBeanDefinition bean) {
		String value = node.getTextContent();
		bean.getPropertyValues().addPropertyValue("userStartParameters", this.convertToList(value));
	}

	private void processParamsWithoutValidation(Node node, RootBeanDefinition bean) {
		NodeList nodeList = node.getChildNodes();

		Map map = new Hashtable();
		bean.getPropertyValues().addPropertyValue("paramsWithoutValidation", map);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node mappingNode = nodeList.item(i);
			if (mappingNode.getNodeType() == Node.ELEMENT_NODE) {
				if (mappingNode.getLocalName().equalsIgnoreCase("mapping")) {
					this.processMapping(mappingNode, map);
				}
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
		List list = new ArrayList();
		// clean the edges of the item - spaces/returns/tabs etc may be used for readability in the configs
		for (int i = 0; i < result.length; i++) {
			// trims leading and trailing whitespace
			list.add(StringUtils.trimWhitespace(result[i]));
		}
		return list;
	}

}
