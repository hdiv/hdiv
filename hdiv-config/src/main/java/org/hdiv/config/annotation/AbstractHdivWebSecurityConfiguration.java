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
package org.hdiv.config.annotation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hdiv.application.ApplicationHDIV;
import org.hdiv.application.IApplication;
import org.hdiv.config.HDIVConfig;
import org.hdiv.config.StartPage;
import org.hdiv.config.annotation.ValidationConfigurer.ValidationConfig;
import org.hdiv.config.annotation.ValidationConfigurer.ValidationConfig.EditableValidationConfigurer;
import org.hdiv.config.annotation.builders.SecurityConfigBuilder;
import org.hdiv.config.validations.DefaultValidationParser;
import org.hdiv.config.validations.DefaultValidationParser.ValidationParam;
import org.hdiv.context.RequestContextFactory;
import org.hdiv.context.RequestContextFactoryImpl;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataValidator.DataValidator;
import org.hdiv.dataValidator.IDataValidator;
import org.hdiv.dataValidator.ValidationResult;
import org.hdiv.filter.DefaultValidationContextFactory;
import org.hdiv.filter.DefaultValidatorErrorHandler;
import org.hdiv.filter.IValidationHelper;
import org.hdiv.filter.ValidationContextFactory;
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
import org.springframework.context.annotation.Scope;

/**
 * Main abstract class for {@link Configuration} support. Creates all internal bean instances.
 *
 * @since 3.0.0
 */
public abstract class AbstractHdivWebSecurityConfiguration {

	/**
	 * Override this method to configure HDIV
	 *
	 * @param securityConfigBuilder {@link SecurityConfigBuilder} instance
	 * @see SecurityConfigBuilder
	 */
	protected void configure(final SecurityConfigBuilder securityConfigBuilder) {
	}

	/**
	 * Override this method to add exclusions to the validation process.
	 *
	 * @param registry {@link ExclusionRegistry} instance
	 * @see ExclusionRegistry
	 */
	protected void addExclusions(final ExclusionRegistry registry) {
	}

	/**
	 * Override this method to add long living pages to the application.
	 *
	 * @param registry {@link LongLivingPagesRegistry} instance
	 * @see LongLivingPagesRegistry
	 */
	protected void addLongLivingPages(final LongLivingPagesRegistry registry) {
	}

	/**
	 * Override this method to add editable validation rules.
	 *
	 * @param registry {@link RuleRegistry} instance
	 * @see RuleRegistry
	 */
	protected void addRules(final RuleRegistry registry) {
	}

	/**
	 * Override this method to add editable validations to the application.
	 *
	 * @param validationConfigurer {@link ValidationConfigurer} instance
	 * @see ValidationConfigurer
	 */
	protected void configureEditableValidation(final ValidationConfigurer validationConfigurer) {
	}

	@Bean
	public HDIVConfig hdivConfig() {

		SecurityConfigBuilder securityConfigBuilder = securityConfigBuilder();
		configure(securityConfigBuilder);

		HDIVConfig config = securityConfigBuilder.build();
		config.setEditableDataValidationProvider(editableDataValidationProvider());

		// User configured exclusions
		ExclusionRegistry exclusionRegistry = new ExclusionRegistry(patternMatcherFactory());
		addExclusions(exclusionRegistry);
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
		addLongLivingPages(longLivingPagesRegistry);
		Map<String, String> longLivingPages = longLivingPagesRegistry.getLongLivingPages();
		config.setLongLivingPages(longLivingPages);

		return config;
	}

	@Bean
	public RequestContextFactory requestContextFactory() {
		return new RequestContextFactoryImpl();
	}

	@Bean
	protected SecurityConfigBuilder securityConfigBuilder() {
		return new SecurityConfigBuilder(patternMatcherFactory());
	}

	@Bean
	public IApplication securityApplication() {
		return new ApplicationHDIV();
	}

	@Bean
	public ValidationResult validationResult() {
		return new ValidationResult();
	}

	@Bean
	public PatternMatcherFactory patternMatcherFactory() {
		return new PatternMatcherFactory();
	}

	@Bean
	public UidGenerator uidGenerator() {
		return new RandomGuidUidGenerator();
	}

	@Bean
	@Scope(BeanDefinition.SCOPE_PROTOTYPE)
	public PageIdGenerator pageIdGenerator() {
		return new SequentialPageIdGenerator();
	}

	@Bean
	public IUserData securityUserData() {
		return new UserData();
	}

	@Bean
	public Logger securityLogger() {
		return new Logger();
	}

	@Bean
	public ValidatorErrorHandler validatorErrorHandler() {
		DefaultValidatorErrorHandler validatorErrorHandler = new DefaultValidatorErrorHandler();
		validatorErrorHandler.setConfig(hdivConfig());
		return validatorErrorHandler;
	}

	@Bean
	@Scope(BeanDefinition.SCOPE_PROTOTYPE)
	public IStateCache stateCache() {

		SecurityConfigBuilder builder = securityConfigBuilder();
		int maxPagesPerSession = builder.getMaxPagesPerSession();

		StateCache stateCache = new StateCache();
		if (maxPagesPerSession > 0) {
			stateCache.setMaxSize(maxPagesPerSession);
		}
		return stateCache;
	}

	@Bean
	public ISession securitySession() {
		return new SessionHDIV();
	}

	@Bean
	public StateUtil stateUtil() {
		StateUtil stateUtil = new StateUtil();
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
		return new DefaultStateScopeManager(stateScopes);
	}

	@Bean
	public UserSessionStateScope userSessionStateScope() {
		UserSessionStateScope scope = new UserSessionStateScope();
		scope.setSession(securitySession());
		return scope;
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
		dataComposerFactory.setStateUtil(stateUtil());
		dataComposerFactory.setUidGenerator(uidGenerator());
		dataComposerFactory.setStateScopeManager(stateScopeManager());
		return dataComposerFactory;
	}

	@Bean
	public IValidationHelper requestValidationHelper() {

		ValidatorHelperRequest validatorHelperRequest = new ValidatorHelperRequest();
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
		requestInitializer.setSession(securitySession());
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
		List<IValidation> defaultRules = getDefaultRules();
		// Custom rules
		Map<String, IValidation> customRules = getCustomRules();

		// Validation configuration
		Map<ValidationTarget, List<IValidation>> validationsData = getValidationsData(defaultRules, customRules);

		DefaultValidationRepository repository = new DefaultValidationRepository();
		repository.setValidations(validationsData);
		repository.setDefaultValidations(defaultRules);
		return repository;
	}

	@Bean
	public ValidationContextFactory validationContextFactory() {
		return new DefaultValidationContextFactory();
	}

	protected List<IValidation> getDefaultRules() {

		// Load validations from xml
		DefaultValidationParser parser = new DefaultValidationParser();
		parser.readDefaultValidations();
		List<Map<ValidationParam, String>> validations = parser.getValidations();

		List<IValidation> defaultRules = new ArrayList<IValidation>();

		for (Map<ValidationParam, String> validation : validations) {
			// Map contains validation id and regex extracted from the xml
			String id = validation.get(ValidationParam.ID);
			String regex = validation.get(ValidationParam.REGEX);

			// Create bean for the validation
			Validation validationBean = new Validation();
			validationBean.setName(id);
			validationBean.setDefaultValidation(true);
			validationBean.setRejectedPattern(regex);

			defaultRules.add(validationBean);
		}
		return defaultRules;
	}

	protected Map<String, IValidation> getCustomRules() {

		RuleRegistry registry = new RuleRegistry();
		addRules(registry);
		return registry.getRules();
	}

	protected Map<ValidationTarget, List<IValidation>> getValidationsData(final List<IValidation> defaultRules,
			final Map<String, IValidation> customRules) {

		PatternMatcherFactory patternMatcherFactory = patternMatcherFactory();

		ValidationConfigurer validationConfigurer = new ValidationConfigurer();
		configureEditableValidation(validationConfigurer);
		List<ValidationConfig> validationConfigs = validationConfigurer.getValidationConfigs();

		Map<ValidationTarget, List<IValidation>> validationsData = new LinkedHashMap<ValidationTarget, List<IValidation>>();

		for (ValidationConfig validationConfig : validationConfigs) {

			String urlPattern = validationConfig.getUrlPattern();
			EditableValidationConfigurer editableValidationConfigurer = validationConfig.getEditableValidationConfigurer();
			boolean useDefaultRules = editableValidationConfigurer.isDefaultRules();
			List<String> selectedRules = editableValidationConfigurer.getRules();
			List<String> selectedParams = editableValidationConfigurer.getParameters();

			// Add selected rules
			List<IValidation> activeRules = new ArrayList<IValidation>();
			for (String ruleName : selectedRules) {

				IValidation val = customRules.get(ruleName);
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
			if (urlPattern != null) {
				PatternMatcher urlMatcher = patternMatcherFactory.getPatternMatcher(urlPattern);
				target.setUrl(urlMatcher);
			}
			List<PatternMatcher> paramMatchers = new ArrayList<PatternMatcher>();
			for (String param : selectedParams) {
				PatternMatcher matcher = patternMatcherFactory.getPatternMatcher(param);
				paramMatchers.add(matcher);
			}
			target.setParams(paramMatchers);

			validationsData.put(target, activeRules);
		}

		return validationsData;
	}
}
