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
package org.hdiv.config.annotation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hdiv.application.ApplicationHDIV;
import org.hdiv.application.IApplication;
import org.hdiv.cipher.CipherHTTP;
import org.hdiv.cipher.ICipherHTTP;
import org.hdiv.cipher.IKeyFactory;
import org.hdiv.cipher.KeyFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.config.StartPage;
import org.hdiv.config.annotation.ValidationConfigurer.ValidationConfig;
import org.hdiv.config.annotation.ValidationConfigurer.ValidationConfig.EditableValidationConfigurer;
import org.hdiv.config.annotation.builders.SecurityConfigBuilder;
import org.hdiv.config.annotation.builders.SecurityConfigBuilder.CipherConfigure;
import org.hdiv.config.annotation.grails.GrailsConfigurationSupport;
import org.hdiv.config.annotation.jsf.JsfConfigurationSupport;
import org.hdiv.config.annotation.springmvc.SpringMvcConfigurationSupport;
import org.hdiv.config.annotation.struts1.Struts1ConfigurationSupport;
import org.hdiv.config.annotation.thymeleaf.ThymeleafConfigurationSupport;
import org.hdiv.config.validations.DefaultValidationParser;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataValidator.DataValidator;
import org.hdiv.dataValidator.IDataValidator;
import org.hdiv.dataValidator.ValidationResult;
import org.hdiv.filter.DefaultValidatorErrorHandler;
import org.hdiv.filter.IValidationHelper;
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
import org.hdiv.regex.PatternMatcher;
import org.hdiv.regex.PatternMatcherFactory;
import org.hdiv.session.ISession;
import org.hdiv.session.IStateCache;
import org.hdiv.session.SessionHDIV;
import org.hdiv.session.StateCache;
import org.hdiv.state.StateUtil;
import org.hdiv.state.scope.AppStateScope;
import org.hdiv.state.scope.DefaultStateScopeManager;
import org.hdiv.state.scope.StateScope;
import org.hdiv.state.scope.StateScopeManager;
import org.hdiv.state.scope.UserSessionStateScope;
import org.hdiv.urlProcessor.BasicUrlProcessor;
import org.hdiv.urlProcessor.FormUrlProcessor;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.util.EncodingUtil;
import org.hdiv.validator.DefaultEditableDataValidationProvider;
import org.hdiv.validator.DefaultValidationRepository;
import org.hdiv.validator.EditableDataValidationProvider;
import org.hdiv.validator.IValidation;
import org.hdiv.validator.Validation;
import org.hdiv.validator.ValidationRepository;
import org.hdiv.validator.ValidationTarget;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

/**
 * Main class of {@link Configuration} support. Creates all internal bean instances.
 * 
 * @since 2.1.7
 */
@Import({ SpringMvcConfigurationSupport.class, ThymeleafConfigurationSupport.class, GrailsConfigurationSupport.class,
		JsfConfigurationSupport.class, Struts1ConfigurationSupport.class })
public abstract class HdivWebSecurityConfigurationSupport {

	@Bean
	public HDIVConfig hdivConfig() {

		SecurityConfigBuilder securityConfigBuilder = this.securityConfigBuilder();
		this.configure(securityConfigBuilder);

		HDIVConfig config = securityConfigBuilder.build();
		config.setEditableDataValidationProvider(editableDataValidationProvider());

		// User configured exclusions
		ExclusionRegistry exclusionRegistry = new ExclusionRegistry(patternMatcherFactory());
		this.addExclusions(exclusionRegistry);
		// Start Pages
		List<StartPage> exclusions = exclusionRegistry.getUrlExclusions();
		config.setUserStartPages(exclusions);
		// StartParameters
		List<String> paramExclusions = exclusionRegistry.getParamExclusions();
		config.setUserStartParameters(paramExclusions);
		// ParamsWithoutValidation
		Map<String, List<String>> paramsWithoutValidation = exclusionRegistry.getParamExclusionsForUrl();
		config.setParamsWithoutValidation(paramsWithoutValidation);

		// Long living pages
		LongLivingPagesRegistry longLivingPagesRegistry = new LongLivingPagesRegistry();
		this.addLongLivingPages(longLivingPagesRegistry);
		Map<String, String> longLivingPages = longLivingPagesRegistry.getLongLivingPages();
		config.setLongLivingPages(longLivingPages);

		return config;
	}

	@Bean
	protected SecurityConfigBuilder securityConfigBuilder() {
		SecurityConfigBuilder builder = new SecurityConfigBuilder(patternMatcherFactory());
		return builder;
	}

	public abstract void configure(SecurityConfigBuilder securityConfigBuilder);

	public abstract void addExclusions(ExclusionRegistry registry);

	public abstract void addLongLivingPages(LongLivingPagesRegistry registry);

	@Bean
	public IApplication securityApplication() {
		ApplicationHDIV application = new ApplicationHDIV();
		return application;
	}

	@Bean
	public ValidationResult validationResult() {
		ValidationResult result = new ValidationResult();
		return result;
	}

	@Bean
	public PatternMatcherFactory patternMatcherFactory() {

		PatternMatcherFactory patternMatcherFactory = new PatternMatcherFactory();
		return patternMatcherFactory;
	}

	@Bean
	public UidGenerator uidGenerator() {
		RandomGuidUidGenerator uidGenerator = new RandomGuidUidGenerator();
		return uidGenerator;
	}

	@Bean
	@Scope(BeanDefinition.SCOPE_PROTOTYPE)
	public PageIdGenerator pageIdGenerator() {

		SequentialPageIdGenerator pageIdGenerator = new SequentialPageIdGenerator();
		return pageIdGenerator;
	}

	@Bean
	public IKeyFactory keyFactory() {
		KeyFactory keyFactory = new KeyFactory();

		SecurityConfigBuilder builder = this.securityConfigBuilder();
		CipherConfigure config = builder.getCipherConfigure();
		if (config.getAlgorithm() != null) {
			keyFactory.setAlgorithm(config.getAlgorithm());
		}
		if (config.getKeySize() > 0) {
			keyFactory.setKeySize(config.getKeySize());
		}
		if (config.getPrngAlgorithm() != null) {
			keyFactory.setPrngAlgorithm(config.getPrngAlgorithm());
		}
		if (config.getProvider() != null) {
			keyFactory.setProvider(config.getProvider());
		}
		return keyFactory;
	}

	@Bean
	public IUserData securityUserData() {
		UserData userData = new UserData();
		return userData;
	}

	@Bean
	public Logger securityLogger() {
		Logger logger = new Logger();
		logger.setUserData(securityUserData());
		return logger;
	}

	@Bean
	public ValidatorErrorHandler validatorErrorHandler() {
		DefaultValidatorErrorHandler validatorErrorHandler = new DefaultValidatorErrorHandler();
		validatorErrorHandler.setUserData(securityUserData());
		validatorErrorHandler.setConfig(hdivConfig());
		return validatorErrorHandler;
	}

	@Bean
	@Scope(BeanDefinition.SCOPE_PROTOTYPE)
	public IStateCache stateCache() {

		SecurityConfigBuilder builder = this.securityConfigBuilder();
		int maxPagesPerSession = builder.getMaxPagesPerSession();

		StateCache stateCache = new StateCache();
		if (maxPagesPerSession > 0) {
			stateCache.setMaxSize(maxPagesPerSession);
		}
		return stateCache;
	}

	@Bean
	public ISession securitySession() {
		SessionHDIV session = new SessionHDIV();
		return session;
	}

	@Bean
	public EncodingUtil encodingUtil() {
		EncodingUtil encodingUtil = new EncodingUtil();
		encodingUtil.setSession(securitySession());
		encodingUtil.init();
		return encodingUtil;
	}

	@Bean
	@Scope(BeanDefinition.SCOPE_PROTOTYPE)
	public ICipherHTTP cipherHTTP() {

		SecurityConfigBuilder builder = this.securityConfigBuilder();
		CipherConfigure config = builder.getCipherConfigure();

		CipherHTTP cipherHTTP = new CipherHTTP();
		if (config.getProvider() != null) {
			cipherHTTP.setProvider(config.getProvider());
		}
		if (config.getTransformation() != null) {
			cipherHTTP.setTransformation(config.getTransformation());
		}
		cipherHTTP.init();
		return cipherHTTP;
	}

	@Bean
	public StateUtil stateUtil() {
		StateUtil stateUtil = new StateUtil();
		stateUtil.setEncodingUtil(encodingUtil());
		stateUtil.setConfig(hdivConfig());
		stateUtil.setSession(securitySession());
		stateUtil.setStateScopeManager(stateScopeManager());
		stateUtil.init();
		return stateUtil;
	}

	@Bean
	public IDataValidator dataValidator() {
		DataValidator dataValidator = new DataValidator();
		dataValidator.setConfig(hdivConfig());
		return dataValidator;
	}

	@Bean
	public StateScopeManager stateScopeManager() {
		List<StateScope> stateScopes = new ArrayList<StateScope>();
		stateScopes.add(userSessionStateScope());
		stateScopes.add(appStateScope());
		DefaultStateScopeManager scopeManager = new DefaultStateScopeManager(stateScopes);
		return scopeManager;
	}

	@Bean
	public UserSessionStateScope userSessionStateScope() {
		return new UserSessionStateScope();
	}

	@Bean
	public AppStateScope appStateScope() {
		return new AppStateScope();
	}

	@Bean
	public DataComposerFactory dataComposerFactory() {
		DataComposerFactory dataComposerFactory = new DataComposerFactory();
		dataComposerFactory.setConfig(hdivConfig());
		dataComposerFactory.setSession(securitySession());
		dataComposerFactory.setEncodingUtil(encodingUtil());
		dataComposerFactory.setStateUtil(stateUtil());
		dataComposerFactory.setUidGenerator(uidGenerator());
		dataComposerFactory.setStateScopeManager(stateScopeManager());
		return dataComposerFactory;
	}

	@Bean
	public IValidationHelper requestValidationHelper() {

		ValidatorHelperRequest validatorHelperRequest = new ValidatorHelperRequest();
		validatorHelperRequest.setLogger(securityLogger());
		validatorHelperRequest.setStateUtil(stateUtil());
		validatorHelperRequest.setHdivConfig(hdivConfig());
		validatorHelperRequest.setSession(securitySession());
		validatorHelperRequest.setDataValidator(dataValidator());
		validatorHelperRequest.setUrlProcessor(basicUrlProcessor());
		validatorHelperRequest.setDataComposerFactory(dataComposerFactory());
		validatorHelperRequest.setStateScopeManager(stateScopeManager());
		validatorHelperRequest.init();
		return validatorHelperRequest;
	}

	@Bean
	public RequestInitializer securityRequestInitializer() {
		DefaultRequestInitializer requestInitializer = new DefaultRequestInitializer();
		requestInitializer.setConfig(hdivConfig());
		return requestInitializer;
	}

	@Bean
	public ServletContextInitializer securityServletContextInitializer() {
		DefaultServletContextInitializer servletContextInitializer = new DefaultServletContextInitializer();
		servletContextInitializer.setConfig(hdivConfig());
		servletContextInitializer.setApplication(securityApplication());
		servletContextInitializer.setFormUrlProcessor(formUrlProcessor());
		servletContextInitializer.setLinkUrlProcessor(linkUrlProcessor());
		return servletContextInitializer;
	}

	@Bean
	public SessionInitializer securitySessionInitializer() {
		DefaultSessionInitializer sessionInitializer = new DefaultSessionInitializer();
		sessionInitializer.setConfig(hdivConfig());
		return sessionInitializer;
	}

	@Bean
	public LinkUrlProcessor linkUrlProcessor() {
		LinkUrlProcessor linkUrlProcessor = new LinkUrlProcessor();
		linkUrlProcessor.setConfig(hdivConfig());
		return linkUrlProcessor;
	}

	@Bean
	public FormUrlProcessor formUrlProcessor() {
		FormUrlProcessor formUrlProcessor = new FormUrlProcessor();
		formUrlProcessor.setConfig(hdivConfig());
		return formUrlProcessor;
	}

	@Bean
	public BasicUrlProcessor basicUrlProcessor() {
		BasicUrlProcessor basicUrlProcessor = new BasicUrlProcessor();
		basicUrlProcessor.setConfig(hdivConfig());
		return basicUrlProcessor;
	}

	@Bean
	public EditableDataValidationProvider editableDataValidationProvider() {

		DefaultEditableDataValidationProvider provider = new DefaultEditableDataValidationProvider();
		provider.setValidationRepository(editableValidationRepository());
		return provider;
	}

	@Bean
	public ValidationRepository editableValidationRepository() {

		// Default rules
		List<IValidation> defaultRules = defaultRules();
		// Custom rules
		RuleRegistry registry = new RuleRegistry();
		this.addRules(registry);
		Map<String, IValidation> registeredRules = registry.getRules();

		PatternMatcherFactory patternMatcherFactory = patternMatcherFactory();

		// Validation configuration
		ValidationConfigurer validationConfigurer = new ValidationConfigurer();
		this.configureEditableValidation(validationConfigurer);
		List<ValidationConfig> validationConfigs = validationConfigurer.getValidationConfigs();

		Map<ValidationTarget, List<IValidation>> validationsData = new LinkedHashMap<ValidationTarget, List<IValidation>>();

		for (ValidationConfig validationConfig : validationConfigs) {

			String urlPattern = validationConfig.getUrlPattern();
			EditableValidationConfigurer editableValidationConfigurer = validationConfig
					.getEditableValidationConfigurer();
			boolean useDefaultRules = editableValidationConfigurer.isDefaultRules();
			List<String> selectedRules = editableValidationConfigurer.getRules();
			List<String> selectedParams = editableValidationConfigurer.getParameters();

			// Add selected rules
			List<IValidation> activeRules = new ArrayList<IValidation>();
			for (String ruleName : selectedRules) {

				IValidation val = registeredRules.get(ruleName);
				if (val == null) {
					throw new BeanInitializationException("Rule with name '" + ruleName + "' not registered.");
				}
				activeRules.add(val);
			}

			// Add default rules if is required
			if (useDefaultRules) {
				activeRules.addAll(defaultRules);
			}

			// Create ValidationTarget object
			ValidationTarget target = new ValidationTarget();
			PatternMatcher urlMatcher = patternMatcherFactory.getPatternMatcher(urlPattern);
			List<PatternMatcher> paramMatchers = new ArrayList<PatternMatcher>();
			for (String param : selectedParams) {
				PatternMatcher matcher = patternMatcherFactory.getPatternMatcher(param);
				paramMatchers.add(matcher);
			}
			target.setUrl(urlMatcher);
			target.setParams(paramMatchers);

			validationsData.put(target, activeRules);
		}

		DefaultValidationRepository repository = new DefaultValidationRepository();
		repository.setValidations(validationsData);
		return repository;
	}

	public abstract void addRules(RuleRegistry registry);

	public abstract void configureEditableValidation(ValidationConfigurer validationConfigurer);

	private List<IValidation> defaultRules() {

		// Load validations from xml
		DefaultValidationParser parser = new DefaultValidationParser();
		parser.readDefaultValidations();
		List<Map<String, String>> validations = parser.getValidations();

		List<IValidation> defaultRules = new ArrayList<IValidation>();

		for (Map<String, String> validation : validations) {
			// Map contains validation id and regex extracted from the xml
			String id = validation.get("id");
			String regex = validation.get("regex");

			// Create bean for the validation
			Validation validationBean = new Validation();
			validationBean.setName(id);
			validationBean.setDefaultValidation(true);
			validationBean.setRejectedPattern(regex);

			defaultRules.add(validationBean);
		}
		return defaultRules;
	}

}
