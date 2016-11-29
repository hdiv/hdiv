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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hdiv.application.ApplicationHDIV;
import org.hdiv.components.support.OutcomeTargetComponentProcessor;
import org.hdiv.components.support.OutputLinkComponentProcessor;
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
import org.hdiv.filter.DefaultValidatorErrorHandler;
import org.hdiv.filter.IValidationHelper;
import org.hdiv.filter.JsfValidatorHelper;
import org.hdiv.filter.ValidatorErrorHandler;
import org.hdiv.filter.ValidatorHelperRequest;
import org.hdiv.idGenerator.PageIdGenerator;
import org.hdiv.idGenerator.RandomGuidUidGenerator;
import org.hdiv.idGenerator.SequentialPageIdGenerator;
import org.hdiv.idGenerator.UidGenerator;
import org.hdiv.init.DefaultRequestInitializer;
import org.hdiv.init.DefaultServletContextInitializer;
import org.hdiv.init.DefaultSessionInitializer;
import org.hdiv.init.RequestInitializer;
import org.hdiv.init.ServletContextInitializer;
import org.hdiv.init.SessionInitializer;
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
import org.hdiv.validator.DefaultEditableDataValidationProvider;
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

	private static final String FORM_URL_PROCESSOR = "formUrlProcessor";

	private static final String LINK_URL_PROCESSOR = "linkUrlProcessor";

	private static final String HDIV_CONFIG = "hdivConfig";

	private static final String STATE_UTIL = "stateUtil";

	private static final String STATE_SCOPE_MANAGER = "stateScopeManager";

	private static final String SESSION = "session";

	private static final String CONFIG = "config";

	public static final String CONFIG_BEAN_NAME = HDIVConfig.class.getName();

	public static final String PATTERN_MATCHER_FACTORY_NAME = PatternMatcherFactory.class.getName();

	public static final String USER_DATA_NAME = IUserData.class.getName();

	/**
	 * The name of the bean to use to look up in an implementation of {@link RequestDataValueProcessor} has been configured.
	 */
	public static final String REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME = "requestDataValueProcessor";

	/**
	 * Minimum Spring version to enable Spring Security integration.
	 */
	protected static final String MIN_SPRING_VERSION = "4.0.0.RELEASE";

	/* Framework present flags */

	protected final boolean springMvcPresent = ClassUtils.isPresent("org.springframework.web.servlet.DispatcherServlet",
			ConfigBeanDefinitionParser.class.getClassLoader());

	protected final boolean grailsPresent = ClassUtils.isPresent("org.codehaus.groovy.grails.web.servlet.GrailsDispatcherServlet",
			ConfigBeanDefinitionParser.class.getClassLoader());

	protected final boolean jsfPresent = ClassUtils.isPresent("javax.faces.webapp.FacesServlet",
			ConfigBeanDefinitionParser.class.getClassLoader());

	protected final boolean jsf1Present = !ClassUtils.isPresent("javax.faces.component.UIOutcomeTarget",
			ConfigBeanDefinitionParser.class.getClassLoader());

	protected final boolean thymeleafPresent = ClassUtils.isPresent("org.thymeleaf.spring3.SpringTemplateEngine",
			ConfigBeanDefinitionParser.class.getClassLoader())
			|| ClassUtils.isPresent("org.thymeleaf.spring4.SpringTemplateEngine", ConfigBeanDefinitionParser.class.getClassLoader());

	protected static final boolean springSecurityPresent = ClassUtils.isPresent(
			"org.springframework.security.web.servlet.support.csrf.CsrfRequestDataValueProcessor",
			ConfigBeanDefinitionParser.class.getClassLoader());

	/* HDIV module present flags */

	protected final boolean springMvcModulePresent = ClassUtils.isPresent("org.hdiv.web.servlet.support.HdivRequestDataValueProcessor",
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

	protected RuntimeBeanReference applicationRef;

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

	protected boolean springVersionGrEqThan4() {
		String springVersion = SpringVersion.getVersion();
		if (springVersion == null || springVersion.compareTo(MIN_SPRING_VERSION) >= 0) {
			return true;
		}
		return false;
	}

	public BeanDefinition parse(final Element element, final ParserContext parserContext) {

		Object source = parserContext.extractSource(element);

		patternMatcherFactoryRef = createPatternMatcherFactory(source, parserContext);

		configRef = this.createConfigBean(element, source, parserContext);

		uidGeneratorRef = this.createSimpleBean(source, parserContext, RandomGuidUidGenerator.class, UidGenerator.class.getName());
		createPageIdGenerator(source, parserContext);
		userDataRef = createUserData(element, source, parserContext);

		sessionRef = createSession(source, parserContext);
		stateScopeManagerRef = createStateScopeManager(source, parserContext);
		createValidatorErrorHandler(source, parserContext);
		loggerRef = createLogger(source, parserContext);
		createStateCache(element, source, parserContext);
		applicationRef = this.createSimpleBean(source, parserContext, ApplicationHDIV.class);
		this.createSimpleBean(source, parserContext, ValidationResult.class);
		stateUtilRef = createStateUtil(source, parserContext);
		dataValidatorRef = createDataValidator(source, parserContext);
		dataComposerFactoryRef = createDataComposerFactory(source, parserContext);
		linkUrlProcessorRef = createLinkUrlProcessor(source, parserContext);
		formUrlProcessorRef = createFormUrlProcessor(source, parserContext);
		basicUrlProcessorRef = createBasicUrlProcessor(source, parserContext);
		createRequestInitializer(source, parserContext);
		createServletContextInitializer(source, parserContext);
		createSessionInitializer(source, parserContext);

		// Register Spring MVC beans if we are using Spring MVC web framework
		if (springMvcPresent && springMvcModulePresent) {
			if (grailsPresent) {
				createGrailsRequestDataValueProcessor(source, parserContext);
			}
			else if (thymeleafPresent) {
				createThymeleafRequestDataValueProcessor(source, parserContext);
			}
			else {
				createRequestDataValueProcessor(source, parserContext);
			}
			this.createSimpleBean(source, parserContext, SpringMVCMultipartConfig.class, IMultipartConfig.class.getName());
		}

		if (struts1ModulePresent) {

			this.createSimpleBean(source, parserContext, StrutsMultipartConfig.class, IMultipartConfig.class.getName());
		}

		// Register JSF specific beans if we are using this web framework
		if (jsfPresent && jsfModulePresent) {
			createJsfValidatorHelper(source, parserContext);
			this.createSimpleBean(source, parserContext, JsfMultipartConfig.class, IMultipartConfig.class.getName());

			createFacesEventListener(source, parserContext);

			createRedirectHelper(source, parserContext);

			if (!jsf1Present) {
				createOutcomeTargetComponentProcessor(source, parserContext);
			}
			createOutputLinkComponentProcessor(source, parserContext);

		}
		else {
			createValidatorHelper(source, parserContext);
		}

		return null;

	}

	protected RuntimeBeanReference createPatternMatcherFactory(final Object source, final ParserContext parserContext) {

		return createSimpleBean(source, parserContext, PatternMatcherFactory.class, PATTERN_MATCHER_FACTORY_NAME);
	}

	protected RuntimeBeanReference createPageIdGenerator(final Object source, final ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(SequentialPageIdGenerator.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setScope(BeanDefinition.SCOPE_PROTOTYPE);

		return registerBean(bean, PageIdGenerator.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createUserData(final Element element, final Object source, final ParserContext parserContext) {
		String userData = element.getAttribute("userData");
		if (userData == null || userData.length() < 1) {
			// If user don't define userData bean, create default
			return this.createSimpleBean(source, parserContext, UserData.class, USER_DATA_NAME);
		}
		else {
			// Use user defined
			parserContext.getRegistry().registerAlias(userData, USER_DATA_NAME);
			return new RuntimeBeanReference(USER_DATA_NAME);
		}
	}

	protected RuntimeBeanReference createLogger(final Object source, final ParserContext parserContext) {

		RootBeanDefinition bean = new RootBeanDefinition(Logger.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

		return registerBean(bean, Logger.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createValidatorErrorHandler(final Object source, final ParserContext parserContext) {

		RootBeanDefinition bean = new RootBeanDefinition(DefaultValidatorErrorHandler.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue(CONFIG, configRef);

		return registerBean(bean, ValidatorErrorHandler.class.getName(), parserContext);

	}

	protected RuntimeBeanReference createStateCache(final Element element, final Object source, final ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(StateCache.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setScope(BeanDefinition.SCOPE_PROTOTYPE);

		String maxSize = element.getAttribute("maxPagesPerSession");
		if (StringUtils.hasText(maxSize)) {
			bean.getPropertyValues().addPropertyValue("maxSize", maxSize);
		}

		return registerBean(bean, IStateCache.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createSession(final Object source, final ParserContext parserContext) {

		return this.createSimpleBean(source, parserContext, SessionHDIV.class, ISession.class.getName());
	}

	protected RuntimeBeanReference createStateUtil(final Object source, final ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(StateUtil.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setInitMethodName("init");
		bean.getPropertyValues().addPropertyValue(CONFIG, configRef);
		bean.getPropertyValues().addPropertyValue(SESSION, sessionRef);
		bean.getPropertyValues().addPropertyValue(STATE_SCOPE_MANAGER, stateScopeManagerRef);

		return registerBean(bean, StateUtil.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createDataValidator(final Object source, final ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(DataValidator.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue(CONFIG, configRef);

		return registerBean(bean, IDataValidator.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createStateScopeManager(final Object source, final ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(DefaultStateScopeManager.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

		ManagedList<RuntimeBeanReference> defs = new ManagedList<RuntimeBeanReference>();
		defs.add(createUserSessionStateScope(source, parserContext));
		defs.add(this.createSimpleBean(source, parserContext, AppStateScope.class));

		RootBeanDefinition listBean = new RootBeanDefinition(ListFactoryBean.class);
		listBean.setSource(source);
		listBean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		listBean.getPropertyValues().addPropertyValue("sourceList", defs);

		bean.getConstructorArgumentValues().addGenericArgumentValue(listBean);

		return registerBean(bean, StateScopeManager.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createUserSessionStateScope(final Object source, final ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(UserSessionStateScope.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue(SESSION, sessionRef);

		return registerBean(bean, UserSessionStateScope.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createDataComposerFactory(final Object source, final ParserContext parserContext) {

		RootBeanDefinition bean = new RootBeanDefinition(DataComposerFactory.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue(CONFIG, configRef);
		bean.getPropertyValues().addPropertyValue(SESSION, sessionRef);
		bean.getPropertyValues().addPropertyValue(STATE_UTIL, stateUtilRef);
		bean.getPropertyValues().addPropertyValue("uidGenerator", uidGeneratorRef);
		bean.getPropertyValues().addPropertyValue(STATE_SCOPE_MANAGER, stateScopeManagerRef);

		return registerBean(bean, DataComposerFactory.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createValidatorHelper(final Object source, final ParserContext parserContext) {

		RootBeanDefinition bean = new RootBeanDefinition(ValidatorHelperRequest.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setInitMethodName("init");
		bean.getPropertyValues().addPropertyValue(STATE_UTIL, stateUtilRef);
		bean.getPropertyValues().addPropertyValue(HDIV_CONFIG, configRef);
		bean.getPropertyValues().addPropertyValue(SESSION, sessionRef);
		bean.getPropertyValues().addPropertyValue("dataValidator", dataValidatorRef);
		bean.getPropertyValues().addPropertyValue("urlProcessor", basicUrlProcessorRef);
		bean.getPropertyValues().addPropertyValue("dataComposerFactory", dataComposerFactoryRef);
		bean.getPropertyValues().addPropertyValue(STATE_SCOPE_MANAGER, stateScopeManagerRef);

		return registerBean(bean, IValidationHelper.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createRequestInitializer(final Object source, final ParserContext parserContext) {

		RootBeanDefinition bean = new RootBeanDefinition(DefaultRequestInitializer.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue(CONFIG, configRef);
		bean.getPropertyValues().addPropertyValue(SESSION, sessionRef);

		return registerBean(bean, RequestInitializer.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createServletContextInitializer(final Object source, final ParserContext parserContext) {

		RootBeanDefinition bean = new RootBeanDefinition(DefaultServletContextInitializer.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue(CONFIG, configRef);
		bean.getPropertyValues().addPropertyValue("application", applicationRef);
		bean.getPropertyValues().addPropertyValue(LINK_URL_PROCESSOR, linkUrlProcessorRef);
		bean.getPropertyValues().addPropertyValue(FORM_URL_PROCESSOR, formUrlProcessorRef);

		return registerBean(bean, ServletContextInitializer.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createSessionInitializer(final Object source, final ParserContext parserContext) {

		RootBeanDefinition bean = new RootBeanDefinition(DefaultSessionInitializer.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue(CONFIG, configRef);

		return registerBean(bean, SessionInitializer.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createLinkUrlProcessor(final Object source, final ParserContext parserContext) {

		RootBeanDefinition bean = new RootBeanDefinition(LinkUrlProcessor.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue(CONFIG, configRef);

		return registerBean(bean, LinkUrlProcessor.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createFormUrlProcessor(final Object source, final ParserContext parserContext) {

		RootBeanDefinition bean = new RootBeanDefinition(FormUrlProcessor.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue(CONFIG, configRef);

		return registerBean(bean, FormUrlProcessor.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createBasicUrlProcessor(final Object source, final ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(BasicUrlProcessor.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue(CONFIG, configRef);

		return registerBean(bean, BasicUrlProcessor.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createRequestDataValueProcessor(final Object source, final ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(HdivRequestDataValueProcessor.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue(LINK_URL_PROCESSOR, linkUrlProcessorRef);
		bean.getPropertyValues().addPropertyValue(FORM_URL_PROCESSOR, formUrlProcessorRef);

		if (springSecurityPresent && springVersionGrEqThan4()) {
			// Spring Security is present and Spring >= 4.0.0
			// Enable Spring security integration

			bean.getPropertyValues().addPropertyValue("innerRequestDataValueProcessor", new CsrfRequestDataValueProcessor());
		}
		parserContext.getRegistry().registerBeanDefinition(REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME, bean);
		return new RuntimeBeanReference(REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME);
	}

	protected RuntimeBeanReference createGrailsRequestDataValueProcessor(final Object source, final ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(GrailsHdivRequestDataValueProcessor.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue(LINK_URL_PROCESSOR, linkUrlProcessorRef);
		bean.getPropertyValues().addPropertyValue(FORM_URL_PROCESSOR, formUrlProcessorRef);
		parserContext.getRegistry().registerBeanDefinition(REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME, bean);
		return new RuntimeBeanReference(REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME);
	}

	protected RuntimeBeanReference createThymeleafRequestDataValueProcessor(final Object source, final ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(ThymeleafHdivRequestDataValueProcessor.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue(LINK_URL_PROCESSOR, linkUrlProcessorRef);
		bean.getPropertyValues().addPropertyValue(FORM_URL_PROCESSOR, formUrlProcessorRef);
		parserContext.getRegistry().registerBeanDefinition(REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME, bean);
		return new RuntimeBeanReference(REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME);
	}

	protected RuntimeBeanReference createConfigBean(final Element element, final Object source, final ParserContext parserContext) {

		BeanDefinition bean = createConfigBean(element, source, parserContext, HDIVConfig.class);

		parserContext.getRegistry().registerBeanDefinition(CONFIG_BEAN_NAME, bean);
		return new RuntimeBeanReference(CONFIG_BEAN_NAME);
	}

	protected BeanDefinition createConfigBean(final Element element, final Object source, final ParserContext parserContext,
			final Class<?> configClass) {

		RootBeanDefinition bean = new RootBeanDefinition(configClass);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

		bean.getPropertyValues().addPropertyValue("patternMatcherFactory", patternMatcherFactoryRef);

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
			bean.getPropertyValues().addPropertyValue("avoidValidationInUrlsWithoutParams", avoidValidationInUrlsWithoutParams);
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
			bean.getPropertyValues().addPropertyValue("protectedExtensions", convertToList(protectedExtensions));
		}

		if (StringUtils.hasText(excludedExtensions)) {
			bean.getPropertyValues().addPropertyValue("excludedExtensions", convertToList(excludedExtensions));
		}

		if (StringUtils.hasText(debugMode)) {
			bean.getPropertyValues().addPropertyValue("debugMode", debugMode);
		}

		if (StringUtils.hasText(showErrorPageOnEditableValidation)) {
			bean.getPropertyValues().addPropertyValue("showErrorPageOnEditableValidation", showErrorPageOnEditableValidation);
		}

		if (StringUtils.hasText(reuseExistingPageInAjaxRequest)) {
			bean.getPropertyValues().addPropertyValue("reuseExistingPageInAjaxRequest", reuseExistingPageInAjaxRequest);
		}

		bean.getPropertyValues().addPropertyValue("editableDataValidationProvider",
				new RuntimeBeanReference(EditableValidationsBeanDefinitionParser.EDITABLE_VALIDATION_PROVIDER_BEAN_NAME));

		if (!parserContext.getRegistry()
				.containsBeanDefinition(EditableValidationsBeanDefinitionParser.EDITABLE_VALIDATION_PROVIDER_BEAN_NAME)) {
			createDefaultEditableDataValidationProvider(source, parserContext);
		}

		// Process startPages, startParameters and paramsWithoutValidation elements
		processChilds(element, bean);

		return bean;
	}

	protected RuntimeBeanReference createDefaultEditableDataValidationProvider(final Object source, final ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(DefaultEditableDataValidationProvider.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		parserContext.getRegistry().registerBeanDefinition(EditableValidationsBeanDefinitionParser.EDITABLE_VALIDATION_PROVIDER_BEAN_NAME,
				bean);
		return new RuntimeBeanReference(EditableValidationsBeanDefinitionParser.EDITABLE_VALIDATION_PROVIDER_BEAN_NAME);
	}

	// JSF Beans

	protected RuntimeBeanReference createFacesEventListener(final Object source, final ParserContext parserContext) {

		// Register ComponentValidator objects
		RuntimeBeanReference requestParameterValidatorRef = createRequestParameterValidator(source, parserContext);
		RuntimeBeanReference uiCommandValidatorRef = this.createSimpleBean(source, parserContext, UICommandValidator.class);
		RuntimeBeanReference htmlInputHiddenValidatorRef = this.createSimpleBean(source, parserContext, HtmlInputHiddenValidator.class);
		RuntimeBeanReference editableValidatorRef = createEditableValidator(source, parserContext);

		RootBeanDefinition bean = new RootBeanDefinition(HDIVFacesEventListener.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue(CONFIG, configRef);
		bean.getPropertyValues().addPropertyValue("logger", loggerRef);
		bean.getPropertyValues().addPropertyValue("htmlInputHiddenValidator", htmlInputHiddenValidatorRef);
		bean.getPropertyValues().addPropertyValue("requestParamValidator", requestParameterValidatorRef);
		bean.getPropertyValues().addPropertyValue("uiCommandValidator", uiCommandValidatorRef);
		bean.getPropertyValues().addPropertyValue("editableValidator", editableValidatorRef);

		return registerBean(bean, HDIVFacesEventListener.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createJsfValidatorHelper(final Object source, final ParserContext parserContext) {

		RootBeanDefinition bean = new RootBeanDefinition(JsfValidatorHelper.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.setInitMethodName("init");
		bean.getPropertyValues().addPropertyValue(STATE_UTIL, stateUtilRef);
		bean.getPropertyValues().addPropertyValue(HDIV_CONFIG, configRef);
		bean.getPropertyValues().addPropertyValue(SESSION, sessionRef);
		bean.getPropertyValues().addPropertyValue("dataValidator", dataValidatorRef);
		bean.getPropertyValues().addPropertyValue("urlProcessor", basicUrlProcessorRef);
		bean.getPropertyValues().addPropertyValue("dataComposerFactory", dataComposerFactoryRef);
		bean.getPropertyValues().addPropertyValue(STATE_SCOPE_MANAGER, stateScopeManagerRef);

		return registerBean(bean, IValidationHelper.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createRequestParameterValidator(final Object source, final ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(RequestParameterValidator.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue(HDIV_CONFIG, configRef);

		return registerBean(bean, RequestParameterValidator.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createEditableValidator(final Object source, final ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(EditableValidator.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue(HDIV_CONFIG, configRef);

		return registerBean(bean, EditableValidator.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createRedirectHelper(final Object source, final ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(RedirectHelper.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue(LINK_URL_PROCESSOR, linkUrlProcessorRef);

		return registerBean(bean, RedirectHelper.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createOutcomeTargetComponentProcessor(final Object source, final ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(OutcomeTargetComponentProcessor.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue(CONFIG, configRef);
		bean.getPropertyValues().addPropertyValue(LINK_URL_PROCESSOR, linkUrlProcessorRef);

		return registerBean(bean, OutcomeTargetComponentProcessor.class.getName(), parserContext);
	}

	protected RuntimeBeanReference createOutputLinkComponentProcessor(final Object source, final ParserContext parserContext) {
		RootBeanDefinition bean = new RootBeanDefinition(OutputLinkComponentProcessor.class);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		bean.getPropertyValues().addPropertyValue(CONFIG, configRef);
		bean.getPropertyValues().addPropertyValue(LINK_URL_PROCESSOR, linkUrlProcessorRef);

		return registerBean(bean, OutputLinkComponentProcessor.class.getName(), parserContext);
	}

	/**
	 * Utility method to register a bean of type String.
	 * 
	 * @param name bean name
	 * @param value String value
	 * @param source source object
	 * @param parserContext context to obtain the registry
	 * @return bean reference
	 */
	protected RuntimeBeanReference createStringBean(final String name, final String value, final Object source,
			final ParserContext parserContext) {
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
	 * @param bean bean definition
	 * @param beanName bean name
	 * @param parserContext context to obtain the registry
	 * @return bean reference
	 */
	protected RuntimeBeanReference registerBean(final RootBeanDefinition bean, final String beanName, final ParserContext parserContext) {

		// Simple bean overriding
		boolean exist = parserContext.getRegistry().containsBeanDefinition(beanName);

		if (!exist) {
			parserContext.getRegistry().registerBeanDefinition(beanName, bean);
			return new RuntimeBeanReference(beanName);
		}
		else {
			// Use user defined
			return new RuntimeBeanReference(beanName);
		}
	}

	protected RuntimeBeanReference createSimpleBean(final Object source, final ParserContext parserContext, final Class<?> clazz) {
		RootBeanDefinition bean = new RootBeanDefinition(clazz);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		String name = parserContext.getReaderContext().generateBeanName(bean);
		parserContext.getRegistry().registerBeanDefinition(name, bean);
		return new RuntimeBeanReference(name);
	}

	protected RuntimeBeanReference createSimpleBean(final Object source, final ParserContext parserContext, final Class<?> clazz,
			final String beanName) {

		RootBeanDefinition bean = new RootBeanDefinition(clazz);
		bean.setSource(source);
		bean.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

		return registerBean(bean, beanName, parserContext);
	}

	protected void processChilds(final Element element, final RootBeanDefinition bean) {
		NodeList nodeList = element.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if ("startPages".equalsIgnoreCase(node.getLocalName())) {
					processStartPages(node, bean);
				}
				else if ("startParameters".equalsIgnoreCase(node.getLocalName())) {
					processStartParameters(node, bean);
				}
				else if ("paramsWithoutValidation".equalsIgnoreCase(node.getLocalName())) {
					processParamsWithoutValidation(node, bean);
				}
				else if ("sessionExpired".equalsIgnoreCase(node.getLocalName())) {
					processSessionExpired(node, bean);
				}
				else if ("longLivingPages".equalsIgnoreCase(node.getLocalName())) {
					processLongLivingPages(node, bean);
				}
			}
		}
	}

	protected void processStartPages(final Node node, final RootBeanDefinition bean) {

		String method = null;
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) node;
			method = element.getAttribute("method");
		}

		String value = node.getTextContent();

		List<String> patterns = convertToList(value);
		for (int i = 0; i < patterns.size(); i++) {
			String pattern = patterns.get(i);
			StartPage startPage = new StartPage(method, pattern);
			startPages.add(startPage);
		}

		bean.getPropertyValues().addPropertyValue("userStartPages", startPages);
	}

	protected void processStartParameters(final Node node, final RootBeanDefinition bean) {
		String value = node.getTextContent();
		bean.getPropertyValues().addPropertyValue("userStartParameters", convertToList(value));
	}

	protected void processParamsWithoutValidation(final Node node, final RootBeanDefinition bean) {
		NodeList nodeList = node.getChildNodes();

		Map<String, List<String>> map = new HashMap<String, List<String>>();
		bean.getPropertyValues().addPropertyValue("paramsWithoutValidation", map);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node mappingNode = nodeList.item(i);
			if (mappingNode.getNodeType() == Node.ELEMENT_NODE && "mapping".equalsIgnoreCase(mappingNode.getLocalName())) {
				processMapping(mappingNode, map);
			}
		}
	}

	protected void processSessionExpired(final Node node, final RootBeanDefinition bean) {

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

	protected void processLongLivingPages(final Node node, final RootBeanDefinition bean) {

		NamedNodeMap attributes = node.getAttributes();
		Node named = attributes.getNamedItem("scope");
		String scope = named.getTextContent();

		String value = node.getTextContent();

		List<String> patterns = convertToList(value);
		for (String pattern : patterns) {
			longLivingPages.put(pattern, scope);
		}

		bean.getPropertyValues().addPropertyValue("longLivingPages", longLivingPages);
	}

	protected void processMapping(final Node node, final Map<String, List<String>> map) {
		NamedNodeMap attributes = node.getAttributes();
		Node named = attributes.getNamedItem("url");
		if (named != null) {
			String url = named.getTextContent();
			String parameters = attributes.getNamedItem("parameters").getTextContent();
			map.put(url, convertToList(parameters));
		}
	}

	protected List<String> convertToList(final String data) {
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
