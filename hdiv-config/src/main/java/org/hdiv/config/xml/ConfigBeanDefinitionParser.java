/**
 * Copyright 2005-2015 hdiv.org
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hdiv.application.ApplicationHDIV;
import org.hdiv.cipher.CipherHTTP;
import org.hdiv.cipher.ICipherHTTP;
import org.hdiv.cipher.IKeyFactory;
import org.hdiv.cipher.KeyFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.config.StartPage;
import org.hdiv.config.Strategy;
import org.hdiv.config.multipart.IMultipartConfig;
import org.hdiv.config.multipart.JsfMultipartConfig;
import org.hdiv.config.multipart.SpringMVCMultipartConfig;
import org.hdiv.config.multipart.StrutsMultipartConfig;
import org.hdiv.context.RedirectHelper;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataValidator.DataValidator;
import org.hdiv.dataValidator.IDataValidator;
import org.hdiv.dataValidator.ValidationResult;
import org.hdiv.events.HDIVFacesEventListener;
import org.hdiv.filter.DefaultRequestInitializer;
import org.hdiv.filter.DefaultValidatorErrorHandler;
import org.hdiv.filter.IValidationHelper;
import org.hdiv.filter.JsfValidatorHelper;
import org.hdiv.filter.RequestInitializer;
import org.hdiv.filter.ValidatorErrorHandler;
import org.hdiv.filter.ValidatorHelperRequest;
import org.hdiv.idGenerator.PageIdGenerator;
import org.hdiv.idGenerator.RandomGuidUidGenerator;
import org.hdiv.idGenerator.SequentialPageIdGenerator;
import org.hdiv.idGenerator.UidGenerator;
import org.hdiv.logs.IUserData;
import org.hdiv.logs.Logger;
import org.hdiv.logs.UserData;
import org.hdiv.regex.PatternMatcherFactory;
import org.hdiv.session.ISession;
import org.hdiv.session.IStateCache;
import org.hdiv.session.SessionHDIV;
import org.hdiv.session.StateCache;
import org.hdiv.state.StateUtil;
import org.hdiv.state.scope.AppStateScope;
import org.hdiv.state.scope.DefaultStateScopeManager;
import org.hdiv.state.scope.StateScopeManager;
import org.hdiv.state.scope.UserSessionStateScope;
import org.hdiv.urlProcessor.BasicUrlProcessor;
import org.hdiv.urlProcessor.FormUrlProcessor;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.util.EncodingUtil;
import org.hdiv.validator.DefaultEditableDataValidationProvider;
import org.hdiv.validator.DefaultEditableDataValidationProvider.ValidationTarget;
import org.hdiv.validator.IValidation;
import org.hdiv.validators.EditableValidator;
import org.hdiv.validators.HtmlInputHiddenValidator;
import org.hdiv.validators.RequestParameterValidator;
import org.hdiv.validators.UICommandValidator;
import org.hdiv.web.servlet.support.GrailsHdivRequestDataValueProcessor;
import org.hdiv.web.servlet.support.HdivRequestDataValueProcessor;
import org.hdiv.web.servlet.support.ThymeleafHdivRequestDataValueProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.SpringVersion;
import org.springframework.security.web.servlet.support.csrf.CsrfRequestDataValueProcessor;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.RequestDataValueProcessor;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * BeanDefinitionParser for &lt;hdiv:config&gt; element.
 */
public class ConfigBeanDefinitionParser implements BeanDefinitionParser {

	public static final String CONFIG_BEAN_NAME = HDIVConfig.class.getName();

	public static final String PATTERN_MATCHER_FACTORY_NAME = PatternMatcherFactory.class.getName();

	public static final String USER_DATA_NAME = IUserData.class.getName();

	/**
	 * The name of the bean to use to look up in an implementation of {@link RequestDataValueProcessor} has been
	 * configured.
	 */
	public static final String REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME = "requestDataValueProcessor";

	/**
	 * Minimum Spring version to enable Spring Security integration.
	 */
	protected static final String MIN_SPRING_VERSION = "4.0.0.RELEASE";

	protected static final boolean springVersionGrEqThan4 = SpringVersion.getVersion().compareTo(MIN_SPRING_VERSION) >= 0;

	/* Framework present flags */

	protected final boolean springMvcPresent = ClassUtils.isPresent(
			"org.springframework.web.servlet.DispatcherServlet", ConfigBeanDefinitionParser.class.getClassLoader());

	protected final boolean grailsPresent = ClassUtils.isPresent(
			"org.codehaus.groovy.grails.web.servlet.GrailsDispatcherServlet",
			ConfigBeanDefinitionParser.class.getClassLoader());

	protected final boolean jsfPresent = ClassUtils.isPresent("javax.faces.webapp.FacesServlet",
			ConfigBeanDefinitionParser.class.getClassLoader());

	protected final boolean thymeleafPresent = ClassUtils.isPresent("org.thymeleaf.spring3.SpringTemplateEngine",
			ConfigBeanDefinitionParser.class.getClassLoader())
			|| ClassUtils.isPresent("org.thymeleaf.spring4.SpringTemplateEngine",
					ConfigBeanDefinitionParser.class.getClassLoader());

	protected static final boolean springSecurityPresent = ClassUtils.isPresent(
			"org.springframework.security.web.servlet.support.csrf.CsrfRequestDataValueProcessor",
			ConfigBeanDefinitionParser.class.getClassLoader());

	/* HDIV module present flags */

	protected final boolean springMvcModulePresent = ClassUtils.isPresent(
			"org.hdiv.web.servlet.support.HdivRequestDataValueProcessor",
			ConfigBeanDefinitionParser.class.getClassLoader());

	protected final boolean struts1ModulePresent = ClassUtils.isPresent("org.hdiv.action.HDIVRequestProcessor",
			ConfigBeanDefinitionParser.class.getClassLoader());

	protected final boolean jsfModulePresent = ClassUtils.isPresent("org.hdiv.filter.JsfValidatorHelper",
			ConfigBeanDefinitionParser.class.getClassLoader());

	/**
	 * List of StartPage objects
	 */
	protected List<StartPage> startPages = new ArrayList<StartPage>();

	/**
	 * Long-living pages configured by the user
	 */
	protected Map<String, String> longLivingPages = new HashMap<String, String>();

	/* Bean references */
	protected RuntimeBeanReference patternMatcherFactoryRef;

	protected RuntimeBeanReference configRef;

	protected RuntimeBeanReference sessionRef;

	protected RuntimeBeanReference encodingUtilRef;

	protected RuntimeBeanReference uidGeneratorRef;

	protected RuntimeBeanReference stateUtilRef;

	protected RuntimeBeanReference dataValidatorRef;

	protected RuntimeBeanReference dataComposerFactoryRef;

	protected RuntimeBeanReference linkUrlProcessorRef;

	protected RuntimeBeanReference formUrlProcessorRef;

	protected RuntimeBeanReference basicUrlProcessorRef;

	protected RuntimeBeanReference loggerRef;

	protected RuntimeBeanReference userDataRef;

	protected RuntimeBeanReference stateScopeManagerRef;

	public BeanDefinition parse(Element element, ParserContext parserContext) {

		Object source = parserContext.extractSource(element);

		this.patternMatcherFactoryRef = this.createPatternMatcherFactory(element, source, parserContext);

		this.configRef = this.createConfigBean(element, source, parserContext);

		this.uidGeneratorRef = this.createSimpleBean(element, source, parserContext, RandomGuidUidGenerator.class,
				UidGenerator.class.getName());
		this.createPageIdGenerator(element, source, parserContext);
		this.createKeyFactory(element, source, parserContext);
		this.userDataRef = this.createUserData(element, source, parserContext);

		this.stateScopeManagerRef = this.createStateScopeManager(element, source, parserContext);
		this.createValidatorErrorHandler(element, source, parserContext);
		this.loggerRef = this.createLogger(element, source, parserContext);
		this.createStateCache(element, source, parserContext);
		this.sessionRef = this.createSession(element, source, parserContext);
		this.encodingUtilRef = this.createEncodingUtil(element, source, parserContext);
		this.createSimpleBean(element, source, parserContext, ApplicationHDIV.class);
		this.createCipher(element, source, parserContext);
		this.createSimpleBean(element, source, parserContext, ValidationResult.class);
		this.stateUtilRef = this.createStateUtil(element, source, parserContext);
		this.dataValidatorRef = this.createDataValidator(element, source, parserContext);
		this.dataComposerFactoryRef = this.createDataComposerFactory(element, source, parserContext);
		this.linkUrlProcessorRef = this.createLinkUrlProcessor(element, source, parserContext);
		this.formUrlProcessorRef = this.createFormUrlProcessor(element, source, parserContext);
		this.basicUrlProcessorRef = this.createBasicUrlProcessor(element, source, parserContext);
		this.createRequestInitializer(element, source, parserContext);

		// Register Spring MVC beans if we are using Spring MVC web framework
		if (this.springMvcPresent && this.springMvcModulePresent) {
			if (this.grailsPresent) {
				this.createGrailsRequestDataValueProcessor(element, source, parserContext);
			} else if (this.thymeleafPresent) {
				this.createThymeleafRequestDataValueProcessor(element, source, parserContext);
			} else {
				this.createRequestDataValueProcessor(element, source, parserContext);
			}
			this.createSimpleBean(element, source, parserContext, SpringMVCMultipartConfig.class,
					IMultipartConfig.class.getName());
		}

		if (this.struts1ModulePresent) {

			this.createSimpleBean(element, source, parserContext, StrutsMultipartConfig.class,
					IMultipartConfig.class.getName());
		}

		// Register JSF specific beans if we are using this web framework
		if (this.jsfPresent && this.jsfModulePresent) {
			this.createJsfValidatorHelper(element, source, parserContext);
			this.createSimpleBean(element, source, parserContext, JsfMultipartConfig.class,
					IMultipartConfig.class.getName());

			this.createFacesEventListener(element, source, parserContext);

			this.createRedirectHelper(element, source, parserContext);

		} else {
			this.createValidatorHelper(element, source, parserContext);
		}

		return null;

	}

	protected RuntimeBeanReference createPatternMatcherFactory(Element element, Object source,
			ParserContext parserContext) {

		return createSimpleBean(element, source, parserContext, PatternMatcherFactory.class,
				PATTERN_MATCHER_FACTORY_NAME);
	}

	protected RuntimeBeanReference createPageIdGenerator(Element element, Object source, ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(SequentialPageIdGenerator.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setScope(BeanDefinition.SCOPE_PROTOTYPE);

		return this.registerBean(bean, PageIdGenerator.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createKeyFactory(Element element, Object source, ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(KeyFactory.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

		return this.registerBean(bean, IKeyFactory.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createUserData(Element element, Object source, ParserContext parserContext) {
		String userData = element.getAttribute("userData");
		if (userData == null || userData.length() < 1) {
			// If user don't define userData bean, create default
			return this.createSimpleBean(element, source, parserContext, UserData.class, USER_DATA_NAME);
		} else {
			// Use user defined
			parserContext.getRegistry().registerAlias(userData, USER_DATA_NAME);
			return new RuntimeBeanReference(USER_DATA_NAME);
		}
	}

	protected RuntimeBeanReference createLogger(Element element, Object source, ParserContext parserContext) {

		RootBeanDefinition bean = new RootBeanDefinition(Logger.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("userData", this.userDataRef);

		return this.registerBean(bean, Logger.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createValidatorErrorHandler(Element element, Object source,
			ParserContext parserContext) {

		RootBeanDefinition bean = new RootBeanDefinition(DefaultValidatorErrorHandler.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("userData", this.userDataRef);
		bean.getPropertyValues().addPropertyValue("config", this.configRef);

		return this.registerBean(bean, ValidatorErrorHandler.class.getName(), parserContext);

	}

	protected RuntimeBeanReference createStateCache(Element element, Object source, ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(StateCache.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setScope(BeanDefinition.SCOPE_PROTOTYPE);

		String maxSize = element.getAttribute("maxPagesPerSession");
		if (StringUtils.hasText(maxSize)) {
			bean.getPropertyValues().addPropertyValue("maxSize", maxSize);
		}

		return this.registerBean(bean, IStateCache.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createEncodingUtil(Element element, Object source, ParserContext parserContext) {

		RootBeanDefinition bean = new RootBeanDefinition(EncodingUtil.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setInitMethodName("init");
		bean.getPropertyValues().addPropertyValue("session", this.sessionRef);

		return this.registerBean(bean, EncodingUtil.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createSession(Element element, Object source, ParserContext parserContext) {

		return this.createSimpleBean(element, source, parserContext, SessionHDIV.class, ISession.class.getName());
	}

	protected RuntimeBeanReference createCipher(Element element, Object source, ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(CipherHTTP.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setScope(BeanDefinition.SCOPE_PROTOTYPE);
		bean.setInitMethodName("init");

		return this.registerBean(bean, ICipherHTTP.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createStateUtil(Element element, Object source, ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(StateUtil.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setInitMethodName("init");
		bean.getPropertyValues().addPropertyValue("encodingUtil", this.encodingUtilRef);
		bean.getPropertyValues().addPropertyValue("config", this.configRef);
		bean.getPropertyValues().addPropertyValue("session", this.sessionRef);
		bean.getPropertyValues().addPropertyValue("stateScopeManager", this.stateScopeManagerRef);

		return this.registerBean(bean, StateUtil.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createDataValidator(Element element, Object source, ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(DataValidator.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("config", this.configRef);

		return this.registerBean(bean, IDataValidator.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createStateScopeManager(Element element, Object source, ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(DefaultStateScopeManager.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

		ManagedList<RuntimeBeanReference> defs = new ManagedList<RuntimeBeanReference>();
		defs.add(this.createSimpleBean(element, source, parserContext, UserSessionStateScope.class));
		defs.add(this.createSimpleBean(element, source, parserContext, AppStateScope.class));

		RootBeanDefinition listBean = new RootBeanDefinition(ListFactoryBean.class);
		listBean.setSource(source);
		listBean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		listBean.getPropertyValues().addPropertyValue("sourceList", defs);

		bean.getConstructorArgumentValues().addGenericArgumentValue(listBean);

		return this.registerBean(bean, StateScopeManager.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createDataComposerFactory(Element element, Object source, ParserContext parserContext) {

		RootBeanDefinition bean = new RootBeanDefinition(DataComposerFactory.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("config", this.configRef);
		bean.getPropertyValues().addPropertyValue("session", this.sessionRef);
		bean.getPropertyValues().addPropertyValue("encodingUtil", this.encodingUtilRef);
		bean.getPropertyValues().addPropertyValue("stateUtil", this.stateUtilRef);
		bean.getPropertyValues().addPropertyValue("uidGenerator", this.uidGeneratorRef);
		bean.getPropertyValues().addPropertyValue("stateScopeManager", this.stateScopeManagerRef);

		return this.registerBean(bean, DataComposerFactory.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createValidatorHelper(Element element, Object source, ParserContext parserContext) {

		RootBeanDefinition bean = new RootBeanDefinition(ValidatorHelperRequest.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setInitMethodName("init");
		bean.getPropertyValues().addPropertyValue("logger", this.loggerRef);
		bean.getPropertyValues().addPropertyValue("stateUtil", this.stateUtilRef);
		bean.getPropertyValues().addPropertyValue("hdivConfig", this.configRef);
		bean.getPropertyValues().addPropertyValue("session", this.sessionRef);
		bean.getPropertyValues().addPropertyValue("dataValidator", this.dataValidatorRef);
		bean.getPropertyValues().addPropertyValue("urlProcessor", this.basicUrlProcessorRef);
		bean.getPropertyValues().addPropertyValue("dataComposerFactory", this.dataComposerFactoryRef);
		bean.getPropertyValues().addPropertyValue("stateScopeManager", this.stateScopeManagerRef);

		return this.registerBean(bean, IValidationHelper.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createRequestInitializer(Element element, Object source, ParserContext parserContext) {

		RootBeanDefinition bean = new RootBeanDefinition(DefaultRequestInitializer.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("config", this.configRef);

		return this.registerBean(bean, RequestInitializer.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createLinkUrlProcessor(Element element, Object source, ParserContext parserContext) {

		RootBeanDefinition bean = new RootBeanDefinition(LinkUrlProcessor.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("config", this.configRef);

		return this.registerBean(bean, LinkUrlProcessor.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createFormUrlProcessor(Element element, Object source, ParserContext parserContext) {

		RootBeanDefinition bean = new RootBeanDefinition(FormUrlProcessor.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("config", this.configRef);

		return this.registerBean(bean, FormUrlProcessor.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createBasicUrlProcessor(Element element, Object source, ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(BasicUrlProcessor.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("config", this.configRef);

		return this.registerBean(bean, BasicUrlProcessor.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createRequestDataValueProcessor(Element element, Object source,
			ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(HdivRequestDataValueProcessor.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("linkUrlProcessor", this.linkUrlProcessorRef);
		bean.getPropertyValues().addPropertyValue("formUrlProcessor", this.formUrlProcessorRef);

		if (springSecurityPresent && springVersionGrEqThan4) {
			// Spring Security is present and Spring >= 4.0.0
			// Enable Spring security integration

			bean.getPropertyValues().addPropertyValue("innerRequestDataValueProcessor",
					new CsrfRequestDataValueProcessor());
		}
		parserContext.getRegistry().registerBeanDefinition(REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME, bean);
		return new RuntimeBeanReference(REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME);
	}

	protected RuntimeBeanReference createGrailsRequestDataValueProcessor(Element element, Object source,
			ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(GrailsHdivRequestDataValueProcessor.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("linkUrlProcessor", this.linkUrlProcessorRef);
		bean.getPropertyValues().addPropertyValue("formUrlProcessor", this.formUrlProcessorRef);
		parserContext.getRegistry().registerBeanDefinition(REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME, bean);
		return new RuntimeBeanReference(REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME);
	}

	protected RuntimeBeanReference createThymeleafRequestDataValueProcessor(Element element, Object source,
			ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(ThymeleafHdivRequestDataValueProcessor.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("linkUrlProcessor", this.linkUrlProcessorRef);
		bean.getPropertyValues().addPropertyValue("formUrlProcessor", this.formUrlProcessorRef);
		parserContext.getRegistry().registerBeanDefinition(REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME, bean);
		return new RuntimeBeanReference(REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME);
	}

	protected RuntimeBeanReference createConfigBean(Element element, Object source, ParserContext parserContext) {

		BeanDefinition bean = createConfigBean(element, source, parserContext, HDIVConfig.class);

		parserContext.getRegistry().registerBeanDefinition(CONFIG_BEAN_NAME, bean);
		return new RuntimeBeanReference(CONFIG_BEAN_NAME);
	}

	protected BeanDefinition createConfigBean(Element element, Object source, ParserContext parserContext,
			Class<?> configClass) {

		RootBeanDefinition bean = new RootBeanDefinition(configClass);
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
		String reuseExistingPageInAjaxRequest = element.getAttribute("reuseExistingPageInAjaxRequest");

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
			bean.getPropertyValues().addPropertyValue("strategy", Strategy.valueOf(strategy.toUpperCase()));
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

		if (StringUtils.hasText(reuseExistingPageInAjaxRequest)) {
			bean.getPropertyValues().addPropertyValue("reuseExistingPageInAjaxRequest", reuseExistingPageInAjaxRequest);
		}

		bean.getPropertyValues().addPropertyValue("editableDataValidationProvider",
				new RuntimeBeanReference(EditableValidationsBeanDefinitionParser.EDITABLE_VALIDATION_PROVIDER_BEAN_NAME));

		if (!parserContext.getRegistry().containsBeanDefinition(
				EditableValidationsBeanDefinitionParser.EDITABLE_VALIDATION_PROVIDER_BEAN_NAME)) {
			this.createDefaultEditableDataValidationProvider(element, source, parserContext);
		}

		// Process startPages, startParameters and paramsWithoutValidation elements
		this.processChilds(element, bean);

		return bean;
	}

	protected RuntimeBeanReference createDefaultEditableDataValidationProvider(Element element, Object source,
			ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(DefaultEditableDataValidationProvider.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("validations",
				new LinkedHashMap<ValidationTarget, List<IValidation>>());
		parserContext.getRegistry().registerBeanDefinition(
				EditableValidationsBeanDefinitionParser.EDITABLE_VALIDATION_PROVIDER_BEAN_NAME, bean);
		return new RuntimeBeanReference(EditableValidationsBeanDefinitionParser.EDITABLE_VALIDATION_PROVIDER_BEAN_NAME);
	}

	protected RuntimeBeanReference createFacesEventListener(Element element, Object source, ParserContext parserContext) {

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
		bean.getPropertyValues().addPropertyValue("editableValidator", editableValidatorRef);

		String name = parserContext.getReaderContext().generateBeanName(bean);
		parserContext.getRegistry().registerBeanDefinition(name, bean);
		return new RuntimeBeanReference(name);
	}

	// JSF Beans

	protected RuntimeBeanReference createJsfValidatorHelper(Element element, Object source, ParserContext parserContext) {

		RootBeanDefinition bean = new RootBeanDefinition(JsfValidatorHelper.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setInitMethodName("init");
		bean.getPropertyValues().addPropertyValue("logger", this.loggerRef);
		bean.getPropertyValues().addPropertyValue("stateUtil", this.stateUtilRef);
		bean.getPropertyValues().addPropertyValue("hdivConfig", this.configRef);
		bean.getPropertyValues().addPropertyValue("session", this.sessionRef);
		bean.getPropertyValues().addPropertyValue("dataValidator", this.dataValidatorRef);
		bean.getPropertyValues().addPropertyValue("urlProcessor", this.basicUrlProcessorRef);
		bean.getPropertyValues().addPropertyValue("dataComposerFactory", this.dataComposerFactoryRef);
		bean.getPropertyValues().addPropertyValue("stateScopeManager", this.stateScopeManagerRef);

		return this.registerBean(bean, IValidationHelper.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createRequestParameterValidator(Element element, Object source,
			ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(RequestParameterValidator.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("hdivConfig", this.configRef);
		String name = parserContext.getReaderContext().generateBeanName(bean);
		parserContext.getRegistry().registerBeanDefinition(name, bean);
		return new RuntimeBeanReference(name);
	}

	protected RuntimeBeanReference createEditableValidator(Element element, Object source, ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(EditableValidator.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("hdivConfig", this.configRef);
		String name = parserContext.getReaderContext().generateBeanName(bean);
		parserContext.getRegistry().registerBeanDefinition(name, bean);
		return new RuntimeBeanReference(name);
	}

	protected RuntimeBeanReference createRedirectHelper(Element element, Object source, ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(RedirectHelper.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue("linkUrlProcessor", this.linkUrlProcessorRef);
		String name = parserContext.getReaderContext().generateBeanName(bean);
		parserContext.getRegistry().registerBeanDefinition(name, bean);
		return new RuntimeBeanReference(name);
	}

	protected RuntimeBeanReference createStringBean(String name, String value, Object source,
			ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(java.lang.String.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getConstructorArgumentValues().addIndexedArgumentValue(0, value);
		parserContext.getRegistry().registerBeanDefinition(name, bean);
		return new RuntimeBeanReference(name);
	}

	/**
	 * Register a bean in the registry if it doesn't exit previously.
	 * 
	 * @param bean
	 *            bean definition
	 * @param beanName
	 *            bean name
	 * @param parserContext
	 *            context to obtain the registry
	 * @return bean reference
	 */
	protected RuntimeBeanReference registerBean(RootBeanDefinition bean, String beanName, ParserContext parserContext) {

		// Simple bean overriding
		boolean exist = parserContext.getRegistry().containsBeanDefinition(beanName);

		if (!exist) {
			parserContext.getRegistry().registerBeanDefinition(beanName, bean);
			return new RuntimeBeanReference(beanName);
		} else {
			// Use user defined
			return new RuntimeBeanReference(beanName);
		}
	}

	protected RuntimeBeanReference createSimpleBean(Element element, Object source, ParserContext parserContext,
			Class<?> clazz) {
		RootBeanDefinition bean = new RootBeanDefinition(clazz);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		String name = parserContext.getReaderContext().generateBeanName(bean);
		parserContext.getRegistry().registerBeanDefinition(name, bean);
		return new RuntimeBeanReference(name);
	}

	protected RuntimeBeanReference createSimpleBean(Element element, Object source, ParserContext parserContext,
			Class<?> clazz, String beanName) {

		RootBeanDefinition bean = new RootBeanDefinition(clazz);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

		return this.registerBean(bean, beanName, parserContext);
	}

	protected void processChilds(Element element, RootBeanDefinition bean) {
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
				} else if (node.getLocalName().equalsIgnoreCase("longLivingPages")) {
					this.processLongLivingPages(node, bean);
				}
			}
		}
	}

	protected void processStartPages(Node node, RootBeanDefinition bean) {

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

	protected void processStartParameters(Node node, RootBeanDefinition bean) {
		String value = node.getTextContent();
		bean.getPropertyValues().addPropertyValue("userStartParameters", this.convertToList(value));
	}

	protected void processParamsWithoutValidation(Node node, RootBeanDefinition bean) {
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

	protected void processSessionExpired(Node node, RootBeanDefinition bean) {

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

	protected void processLongLivingPages(Node node, RootBeanDefinition bean) {

		NamedNodeMap attributes = node.getAttributes();
		Node named = attributes.getNamedItem("scope");
		String scope = named.getTextContent();

		String value = node.getTextContent();

		List<String> patterns = this.convertToList(value);
		for (String pattern : patterns) {
			this.longLivingPages.put(pattern, scope);
		}

		bean.getPropertyValues().addPropertyValue("longLivingPages", this.longLivingPages);
	}

	protected void processMapping(Node node, Map<String, List<String>> map) {
		NamedNodeMap attributes = node.getAttributes();
		Node named = attributes.getNamedItem("url");
		if (named != null) {
			String url = named.getTextContent();
			String parameters = attributes.getNamedItem("parameters").getTextContent();
			map.put(url, this.convertToList(parameters));
		}
	}

	protected List<String> convertToList(String data) {
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
