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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hdiv.application.ApplicationHDIV;
import org.hdiv.cipher.CipherHTTP;
import org.hdiv.cipher.KeyFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.config.HDIVValidations;
import org.hdiv.config.StartPage;
import org.hdiv.config.multipart.JsfMultipartConfig;
import org.hdiv.config.multipart.SpringMVCMultipartConfig;
import org.hdiv.context.RedirectHelper;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataValidator.DataValidatorFactory;
import org.hdiv.dataValidator.ValidationResult;
import org.hdiv.events.HDIVFacesEventListener;
import org.hdiv.filter.DefaultRequestInitializer;
import org.hdiv.filter.DefaultValidatorErrorHandler;
import org.hdiv.filter.IValidationHelper;
import org.hdiv.filter.JsfValidatorHelper;
import org.hdiv.filter.RequestInitializer;
import org.hdiv.filter.ValidatorErrorHandler;
import org.hdiv.filter.ValidatorHelperRequest;
import org.hdiv.idGenerator.RandomGuidUidGenerator;
import org.hdiv.idGenerator.SequentialPageIdGenerator;
import org.hdiv.idGenerator.UidGenerator;
import org.hdiv.logs.Logger;
import org.hdiv.logs.UserData;
import org.hdiv.regex.PatternMatcherFactory;
import org.hdiv.session.ISession;
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
import org.springframework.web.servlet.support.RequestDataValueProcessor;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * BeanDefinitionParser for <hdiv:config> element.
 */
public class ConfigBeanDefinitionParser implements BeanDefinitionParser {

	public static final String CONFIG_BEAN_NAME = HDIVConfig.class.getName();

	public static final String UID_GENERATOR_BEAN_NAME = UidGenerator.class.getName();

	public static final String SESSION_BEAN_NAME = ISession.class.getName();

	public static final String VALIDATOR_ERROR_HANDLER_BEAN_NAME = ValidatorErrorHandler.class.getName();

	public static final String LOGGER_BEAN_NAME = Logger.class.getName();

	public static final String VALIDATOR_HELPER_NAME = IValidationHelper.class.getName();

	public static final String REQUEST_INITIALIZER_NAME = RequestInitializer.class.getName();

	public static final String PATTERN_MATCHER_FACTORY_NAME = PatternMatcherFactory.class.getName();

	public static final String LINK_URL_PROCESSOR_NAME = LinkUrlProcessor.class.getName();

	public static final String FORM_URL_PROCESSOR_NAME = FormUrlProcessor.class.getName();

	/**
	 * The name of the bean to use to look up in an implementation of {@link RequestDataValueProcessor} has been
	 * configured.
	 */
	private static final String REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME = "requestDataValueProcessor";

	private final boolean springMvcPresent = ClassUtils.isPresent("org.springframework.web.servlet.DispatcherServlet",
			ConfigBeanDefinitionParser.class.getClassLoader());

	private final boolean grailsPresent = ClassUtils.isPresent(
			"org.codehaus.groovy.grails.web.servlet.GrailsDispatcherServlet",
			ConfigBeanDefinitionParser.class.getClassLoader());

	private final boolean jsfPresent = ClassUtils.isPresent("javax.faces.webapp.FacesServlet",
			ConfigBeanDefinitionParser.class.getClassLoader());

	private final boolean jsfModulePresent = ClassUtils.isPresent("org.hdiv.filter.JsfValidatorHelper",
			ConfigBeanDefinitionParser.class.getClassLoader());

	/**
	 * List of StartPage objects
	 */
	private List<StartPage> startPages = new ArrayList<StartPage>();

	/* Bean references */
	private RuntimeBeanReference patternMatcherFactoryRef;

	private RuntimeBeanReference configRef;

	private RuntimeBeanReference sessionRef;

	private RuntimeBeanReference encodingUtilRef;

	private RuntimeBeanReference uidGeneratorRef;

	private RuntimeBeanReference stateUtilRef;

	private RuntimeBeanReference dataValidatorFactoryRef;

	private RuntimeBeanReference dataComposerFactoryRef;

	private RuntimeBeanReference linkUrlProcessorRef;

	private RuntimeBeanReference formUrlProcessorRef;

	private RuntimeBeanReference loggerRef;

	private RuntimeBeanReference userDataRef;

	public BeanDefinition parse(Element element, ParserContext parserContext) {

		Object source = parserContext.extractSource(element);

		this.patternMatcherFactoryRef = this.createSimpleBean(element, source, parserContext,
				PatternMatcherFactory.class, PATTERN_MATCHER_FACTORY_NAME);

		this.configRef = this.createConfigBean(element, source, parserContext);

		this.uidGeneratorRef = this.createSimpleBean(element, source, parserContext, RandomGuidUidGenerator.class,
				UID_GENERATOR_BEAN_NAME);
		this.createPageIdGenerator(element, source, parserContext);
		this.createKeyFactory(element, source, parserContext);
		this.userDataRef = this.createUserData(element, source, parserContext);

		this.createStringBean("hdivParameter", "_HDIV_STATE_", source, parserContext);
		this.createStringBean("modifyHdivStateParameter", "_MODIFY_HDIV_STATE_", source, parserContext);

		this.createValidatorErrorHandler(element, source, parserContext);
		this.loggerRef = this.createLogger(element, source, parserContext);
		this.createStateCache(element, source, parserContext);
		this.sessionRef = this.createSession(element, source, parserContext);
		this.encodingUtilRef = this.createEncodingUtil(element, source, parserContext);
		this.createSimpleBean(element, source, parserContext, ApplicationHDIV.class);
		this.createCipher(element, source, parserContext);
		this.createSimpleBean(element, source, parserContext, ValidationResult.class);
		this.stateUtilRef = this.createStateUtil(element, source, parserContext);
		this.dataValidatorFactoryRef = this.createDataValidatorFactory(element, source, parserContext);
		this.dataComposerFactoryRef = this.createDataComposerFactory(element, source, parserContext);
		this.linkUrlProcessorRef = this.createLinkUrlProcessor(element, source, parserContext);
		this.formUrlProcessorRef = this.createFormUrlProcessor(element, source, parserContext);
		this.createRequestInitializer(element, source, parserContext);

		// register Spring MVC beans if we are using Spring MVC web framework
		if (this.grailsPresent) {
			this.createGrailsRequestDataValueProcessor(element, source, parserContext);
			this.createSimpleBean(element, source, parserContext, SpringMVCMultipartConfig.class);
		} else if (this.springMvcPresent) {
			this.createRequestDataValueProcessor(element, source, parserContext);
			this.createSimpleBean(element, source, parserContext, SpringMVCMultipartConfig.class);
		}

		// register JSF specific beans if we are using this web framework
		if (this.jsfPresent && this.jsfModulePresent) {
			this.createJsfValidatorHelper(element, source, parserContext);
			this.createSimpleBean(element, source, parserContext, JsfMultipartConfig.class);

			this.createFacesEventListener(element, source, parserContext);

			this.createRedirectHelper(element, source, parserContext);

		} else {
			this.createValidatorHelper(element, source, parserContext);
		}

		return null;

	}

	private RuntimeBeanReference createPageIdGenerator(Element element, Object source, ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(SequentialPageIdGenerator.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setScope(BeanDefinition.SCOPE_PROTOTYPE);
		String name = parserContext.getReaderContext().generateBeanName(bean);
		parserContext.getRegistry().registerBeanDefinition(name, bean);
		return new RuntimeBeanReference(name);
	}

	private RuntimeBeanReference createKeyFactory(Element element, Object source, ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(KeyFactory.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("algorithm", "AES");
		bean.getPropertyValues().addPropertyValue("keySize", "128");
		bean.getPropertyValues().addPropertyValue("prngAlgorithm", "SHA1PRNG");
		bean.getPropertyValues().addPropertyValue("provider", "SUN");
		String name = parserContext.getReaderContext().generateBeanName(bean);
		parserContext.getRegistry().registerBeanDefinition(name, bean);
		return new RuntimeBeanReference(name);
	}

	private RuntimeBeanReference createUserData(Element element, Object source, ParserContext parserContext) {
		String userData = element.getAttribute("userData");
		if (userData == null || userData.length() < 1) {
			// If user don't define userData bean, create default
			return this.createSimpleBean(element, source, parserContext, UserData.class);
		} else {
			// Use user defined
			return new RuntimeBeanReference(userData);
		}
	}

	private RuntimeBeanReference createLogger(Element element, Object source, ParserContext parserContext) {

		// Simple bean overriding
		boolean existBean = parserContext.getRegistry().containsBeanDefinition(LOGGER_BEAN_NAME);

		if (!existBean) {
			// If user don't define Logger bean, create default
			RootBeanDefinition bean = new RootBeanDefinition(Logger.class);
			bean.setSource(source);
			bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			bean.getPropertyValues().addPropertyValue("userData", this.userDataRef);
			parserContext.getRegistry().registerBeanDefinition(LOGGER_BEAN_NAME, bean);
			return new RuntimeBeanReference(LOGGER_BEAN_NAME);

		} else {
			// Use user defined
			return new RuntimeBeanReference(LOGGER_BEAN_NAME);
		}

	}

	private RuntimeBeanReference createValidatorErrorHandler(Element element, Object source, ParserContext parserContext) {

		// Simple bean overriding
		boolean existBean = parserContext.getRegistry().containsBeanDefinition(VALIDATOR_ERROR_HANDLER_BEAN_NAME);

		if (!existBean) {
			// If user don't define ValidatorErrorHandler bean, create default
			RootBeanDefinition bean = new RootBeanDefinition(DefaultValidatorErrorHandler.class);
			bean.setSource(source);
			bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			bean.getPropertyValues().addPropertyValue("userData", this.userDataRef);
			bean.getPropertyValues().addPropertyValue("config", this.configRef);
			parserContext.getRegistry().registerBeanDefinition(VALIDATOR_ERROR_HANDLER_BEAN_NAME, bean);
			return new RuntimeBeanReference(VALIDATOR_ERROR_HANDLER_BEAN_NAME);

		} else {
			// Use user defined
			return new RuntimeBeanReference(VALIDATOR_ERROR_HANDLER_BEAN_NAME);
		}

	}

	private RuntimeBeanReference createStateCache(Element element, Object source, ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(StateCache.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setScope(BeanDefinition.SCOPE_PROTOTYPE);

		String maxSize = element.getAttribute("maxPagesPerSession");
		if (StringUtils.hasText(maxSize)) {
			bean.getPropertyValues().addPropertyValue("maxSize", maxSize);
		}
		String name = parserContext.getReaderContext().generateBeanName(bean);
		parserContext.getRegistry().registerBeanDefinition(name, bean);
		return new RuntimeBeanReference(name);
	}

	private RuntimeBeanReference createEncodingUtil(Element element, Object source, ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(EncodingUtil.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setInitMethodName("init");
		bean.getPropertyValues().addPropertyValue("session", this.sessionRef);
		String name = parserContext.getReaderContext().generateBeanName(bean);
		parserContext.getRegistry().registerBeanDefinition(name, bean);
		return new RuntimeBeanReference(name);
	}

	private RuntimeBeanReference createSession(Element element, Object source, ParserContext parserContext) {

		// Simple bean overriding
		boolean existSession = parserContext.getRegistry().containsBeanDefinition(SESSION_BEAN_NAME);

		if (!existSession) {
			// If user don't define ISession bean, create default
			return this.createSimpleBean(element, source, parserContext, SessionHDIV.class, SESSION_BEAN_NAME);
		} else {
			// Use user defined
			return new RuntimeBeanReference(SESSION_BEAN_NAME);
		}
	}

	private RuntimeBeanReference createCipher(Element element, Object source, ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(CipherHTTP.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setScope(BeanDefinition.SCOPE_PROTOTYPE);
		bean.setInitMethodName("init");
		bean.getPropertyValues().addPropertyValue("transformation", "AES/CBC/PKCS5Padding");
		String name = parserContext.getReaderContext().generateBeanName(bean);
		parserContext.getRegistry().registerBeanDefinition(name, bean);
		return new RuntimeBeanReference(name);
	}

	private RuntimeBeanReference createStateUtil(Element element, Object source, ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(StateUtil.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setInitMethodName("init");
		bean.getPropertyValues().addPropertyValue("encodingUtil", this.encodingUtilRef);
		bean.getPropertyValues().addPropertyValue("config", this.configRef);
		bean.getPropertyValues().addPropertyValue("session", this.sessionRef);
		String name = parserContext.getReaderContext().generateBeanName(bean);
		parserContext.getRegistry().registerBeanDefinition(name, bean);
		return new RuntimeBeanReference(name);
	}

	private RuntimeBeanReference createDataValidatorFactory(Element element, Object source, ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(DataValidatorFactory.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("hdivConfig", this.configRef);
		String name = parserContext.getReaderContext().generateBeanName(bean);
		parserContext.getRegistry().registerBeanDefinition(name, bean);
		return new RuntimeBeanReference(name);
	}

	private RuntimeBeanReference createDataComposerFactory(Element element, Object source, ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(DataComposerFactory.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("hdivConfig", this.configRef);
		bean.getPropertyValues().addPropertyValue("session", this.sessionRef);
		bean.getPropertyValues().addPropertyValue("encodingUtil", this.encodingUtilRef);
		bean.getPropertyValues().addPropertyValue("stateUtil", this.stateUtilRef);
		bean.getPropertyValues().addPropertyValue("uidGenerator", this.uidGeneratorRef);
		bean.getPropertyValues().addPropertyValue("allowedLength", "4000");

		String name = parserContext.getReaderContext().generateBeanName(bean);
		parserContext.getRegistry().registerBeanDefinition(name, bean);
		return new RuntimeBeanReference(name);
	}

	private RuntimeBeanReference createValidatorHelper(Element element, Object source, ParserContext parserContext) {

		// Simple bean overriding
		boolean exist = parserContext.getRegistry().containsBeanDefinition(VALIDATOR_HELPER_NAME);

		if (!exist) {
			// If user don't define one, create default
			RootBeanDefinition bean = new RootBeanDefinition(ValidatorHelperRequest.class);
			bean.setSource(source);
			bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			bean.setInitMethodName("init");
			bean.getPropertyValues().addPropertyValue("logger", this.loggerRef);
			bean.getPropertyValues().addPropertyValue("stateUtil", this.stateUtilRef);
			bean.getPropertyValues().addPropertyValue("hdivConfig", this.configRef);
			bean.getPropertyValues().addPropertyValue("session", this.sessionRef);
			bean.getPropertyValues().addPropertyValue("dataValidatorFactory", this.dataValidatorFactoryRef);
			bean.getPropertyValues().addPropertyValue("dataComposerFactory", this.dataComposerFactoryRef);
			parserContext.getRegistry().registerBeanDefinition(VALIDATOR_HELPER_NAME, bean);
			return new RuntimeBeanReference(VALIDATOR_HELPER_NAME);
		} else {
			// Use user defined
			return new RuntimeBeanReference(VALIDATOR_HELPER_NAME);
		}

	}

	private RuntimeBeanReference createRequestInitializer(Element element, Object source, ParserContext parserContext) {

		// Simple bean overriding
		boolean exist = parserContext.getRegistry().containsBeanDefinition(REQUEST_INITIALIZER_NAME);

		if (!exist) {
			// If user don't define one, create default
			RootBeanDefinition bean = new RootBeanDefinition(DefaultRequestInitializer.class);
			bean.setSource(source);
			bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			bean.getPropertyValues().addPropertyValue("config", this.configRef);
			parserContext.getRegistry().registerBeanDefinition(REQUEST_INITIALIZER_NAME, bean);
			return new RuntimeBeanReference(REQUEST_INITIALIZER_NAME);
		} else {
			// Use user defined
			return new RuntimeBeanReference(REQUEST_INITIALIZER_NAME);
		}

	}

	private RuntimeBeanReference createLinkUrlProcessor(Element element, Object source, ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(LinkUrlProcessor.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("config", this.configRef);

		parserContext.getRegistry().registerBeanDefinition(LINK_URL_PROCESSOR_NAME, bean);
		return new RuntimeBeanReference(LINK_URL_PROCESSOR_NAME);
	}

	private RuntimeBeanReference createFormUrlProcessor(Element element, Object source, ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(FormUrlProcessor.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("config", this.configRef);

		parserContext.getRegistry().registerBeanDefinition(FORM_URL_PROCESSOR_NAME, bean);
		return new RuntimeBeanReference(FORM_URL_PROCESSOR_NAME);
	}

	private RuntimeBeanReference createRequestDataValueProcessor(Element element, Object source,
			ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(HdivRequestDataValueProcessor.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("linkUrlProcessor", this.linkUrlProcessorRef);
		bean.getPropertyValues().addPropertyValue("formUrlProcessor", this.formUrlProcessorRef);
		parserContext.getRegistry().registerBeanDefinition(REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME, bean);
		return new RuntimeBeanReference(REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME);
	}

	private RuntimeBeanReference createGrailsRequestDataValueProcessor(Element element, Object source,
			ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(GrailsHdivRequestDataValueProcessor.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("linkUrlProcessor", this.linkUrlProcessorRef);
		bean.getPropertyValues().addPropertyValue("formUrlProcessor", this.formUrlProcessorRef);
		parserContext.getRegistry().registerBeanDefinition(REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME, bean);
		return new RuntimeBeanReference(REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME);
	}

	private RuntimeBeanReference createConfigBean(Element element, Object source, ParserContext parserContext) {

		RootBeanDefinition bean = new RootBeanDefinition(HDIVConfig.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

		bean.getPropertyValues().addPropertyValue("patternMatcherFactory", this.patternMatcherFactoryRef);

		String confidentiality = element.getAttribute("confidentiality");
		String avoidCookiesIntegrity = element.getAttribute("avoidCookiesIntegrity");
		String avoidCookiesConfidentiality = element.getAttribute("avoidCookiesConfidentiality");
		String avoidValidationInUrlsWithoutParams = element.getAttribute("avoidValidationInUrlsWithoutParams");
		String strategy = element.getAttribute("strategy");
		String randomName = element.getAttribute("randomName");
		String errorPage = element.getAttribute("errorPage");
		String protectedExtensions = element.getAttribute("protectedExtensions");
		String excludedExtensions = element.getAttribute("excludedExtensions");
		String debugMode = element.getAttribute("debugMode");
		String showErrorPageOnEditableValidation = element.getAttribute("showErrorPageOnEditableValidation");

		if (StringUtils.hasText(confidentiality)) {
			bean.getPropertyValues().addPropertyValue("confidentiality", confidentiality);
		}

		if (StringUtils.hasText(avoidCookiesIntegrity)) {
			bean.getPropertyValues().addPropertyValue("avoidCookiesIntegrity", avoidCookiesIntegrity);
		}

		if (StringUtils.hasText(avoidCookiesConfidentiality)) {
			bean.getPropertyValues().addPropertyValue("avoidCookiesConfidentiality", avoidCookiesConfidentiality);
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
				new RuntimeBeanReference(EditableValidationsBeanDefinitionParser.EDITABLE_VALIDATIONS_BEAN_NAME));

		if (!parserContext.getRegistry().containsBeanDefinition(
				EditableValidationsBeanDefinitionParser.EDITABLE_VALIDATIONS_BEAN_NAME)) {
			this.createDefaultEditableParametersValidations(element, source, parserContext);
		}

		// Process startPages, startParameters and paramsWithoutValidation elements
		this.processChilds(element, bean);

		parserContext.getRegistry().registerBeanDefinition(CONFIG_BEAN_NAME, bean);
		return new RuntimeBeanReference(CONFIG_BEAN_NAME);

	}

	private RuntimeBeanReference createDefaultEditableParametersValidations(Element element, Object source,
			ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(HDIVValidations.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setInitMethodName("init");
		bean.getPropertyValues().addPropertyValue("rawUrls", new HashMap<String, List<String>>());
		parserContext.getRegistry().registerBeanDefinition(
				EditableValidationsBeanDefinitionParser.EDITABLE_VALIDATIONS_BEAN_NAME, bean);
		return new RuntimeBeanReference(EditableValidationsBeanDefinitionParser.EDITABLE_VALIDATIONS_BEAN_NAME);
	}

	private RuntimeBeanReference createFacesEventListener(Element element, Object source, ParserContext parserContext) {

		// Register ComponentValidator objects
		RuntimeBeanReference requestParameterValidatorRef = this.createRequestParameterValidator(element, source,
				parserContext);
		RuntimeBeanReference uiCommandValidatorRef = this.createSimpleBean(element, source, parserContext,
				UICommandValidator.class);
		RuntimeBeanReference htmlInputHiddenValidatorRef = this.createSimpleBean(element, source, parserContext,
				HtmlInputHiddenValidator.class);
		RuntimeBeanReference editableValidatorRef = this.createEditableValidator(element, source, parserContext);

		RootBeanDefinition bean = new RootBeanDefinition(HDIVFacesEventListener.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("config", this.configRef);
		bean.getPropertyValues().addPropertyValue("logger", this.loggerRef);
		bean.getPropertyValues().addPropertyValue("htmlInputHiddenValidator", htmlInputHiddenValidatorRef);
		bean.getPropertyValues().addPropertyValue("requestParamValidator", requestParameterValidatorRef);
		bean.getPropertyValues().addPropertyValue("uiCommandValidator", uiCommandValidatorRef);
		bean.getPropertyValues().addPropertyValue("editabeValidator", editableValidatorRef);

		String name = parserContext.getReaderContext().generateBeanName(bean);
		parserContext.getRegistry().registerBeanDefinition(name, bean);
		return new RuntimeBeanReference(name);
	}

	// JSF Beans

	private RuntimeBeanReference createJsfValidatorHelper(Element element, Object source, ParserContext parserContext) {

		RootBeanDefinition bean = new RootBeanDefinition(JsfValidatorHelper.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setInitMethodName("init");
		bean.getPropertyValues().addPropertyValue("logger", this.loggerRef);
		bean.getPropertyValues().addPropertyValue("stateUtil", this.stateUtilRef);
		bean.getPropertyValues().addPropertyValue("hdivConfig", this.configRef);
		bean.getPropertyValues().addPropertyValue("session", this.sessionRef);
		bean.getPropertyValues().addPropertyValue("dataValidatorFactory", this.dataValidatorFactoryRef);
		bean.getPropertyValues().addPropertyValue("dataComposerFactory", this.dataComposerFactoryRef);

		String name = parserContext.getReaderContext().generateBeanName(bean);
		parserContext.getRegistry().registerBeanDefinition(name, bean);
		return new RuntimeBeanReference(name);
	}

	private RuntimeBeanReference createRequestParameterValidator(Element element, Object source,
			ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(RequestParameterValidator.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("hdivConfig", this.configRef);
		String name = parserContext.getReaderContext().generateBeanName(bean);
		parserContext.getRegistry().registerBeanDefinition(name, bean);
		return new RuntimeBeanReference(name);
	}

	private RuntimeBeanReference createEditableValidator(Element element, Object source, ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(EditableValidator.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("hdivConfig", this.configRef);
		String name = parserContext.getReaderContext().generateBeanName(bean);
		parserContext.getRegistry().registerBeanDefinition(name, bean);
		return new RuntimeBeanReference(name);
	}

	private RuntimeBeanReference createRedirectHelper(Element element, Object source, ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(RedirectHelper.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("linkUrlProcessor", this.linkUrlProcessorRef);
		String name = parserContext.getReaderContext().generateBeanName(bean);
		parserContext.getRegistry().registerBeanDefinition(name, bean);
		return new RuntimeBeanReference(name);
	}

	private RuntimeBeanReference createStringBean(String name, String value, Object source, ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(java.lang.String.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getConstructorArgumentValues().addIndexedArgumentValue(0, value);
		parserContext.getRegistry().registerBeanDefinition(name, bean);
		return new RuntimeBeanReference(name);
	}

	private RuntimeBeanReference createSimpleBean(Element element, Object source, ParserContext parserContext,
			Class<?> clazz) {
		RootBeanDefinition bean = new RootBeanDefinition(clazz);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		String name = parserContext.getReaderContext().generateBeanName(bean);
		parserContext.getRegistry().registerBeanDefinition(name, bean);
		return new RuntimeBeanReference(name);
	}

	private RuntimeBeanReference createSimpleBean(Element element, Object source, ParserContext parserContext,
			Class<?> clazz, String beanName) {
		RootBeanDefinition bean = new RootBeanDefinition(clazz);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		parserContext.getRegistry().registerBeanDefinition(beanName, bean);
		return new RuntimeBeanReference(beanName);
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
				} else if (node.getLocalName().equalsIgnoreCase("sessionExpired")) {
					this.processSessionExpired(node, bean);
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

		List<String> patterns = this.convertToList(value);
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

		Map<String, List<String>> map = new HashMap<String, List<String>>();
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

	private void processSessionExpired(Node node, RootBeanDefinition bean) {

		NamedNodeMap attributes = node.getAttributes();
		Node named = attributes.getNamedItem("loginPage");
		if (named != null) {
			String loginPage = named.getTextContent();
			bean.getPropertyValues().addPropertyValue("sessionExpiredLoginPage", loginPage);
		}

		named = attributes.getNamedItem("homePage");
		if (named != null) {
			String homePage = named.getTextContent();
			bean.getPropertyValues().addPropertyValue("sessionExpiredHomePage", homePage);
		}

	}

	private void processMapping(Node node, Map<String, List<String>> map) {
		NamedNodeMap attributes = node.getAttributes();
		Node named = attributes.getNamedItem("url");
		if (named != null) {
			String url = named.getTextContent();
			String parameters = attributes.getNamedItem("parameters").getTextContent();
			map.put(url, this.convertToList(parameters));
		}
	}

	private List<String> convertToList(String data) {
		String[] result = data.split(",");
		List<String> list = new ArrayList<String>();
		// clean the edges of the item - spaces/returns/tabs etc may be used for readability in the
		// configs
		for (int i = 0; i < result.length; i++) {
			// trims leading and trailing whitespace
			list.add(StringUtils.trimWhitespace(result[i]));
		}
		return list;
	}

}
