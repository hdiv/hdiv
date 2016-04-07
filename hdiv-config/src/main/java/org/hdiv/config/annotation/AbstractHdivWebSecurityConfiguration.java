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
import org.hdiv.config.HDIVConfig;
import org.hdiv.config.StartPage;
import org.hdiv.config.annotation.ValidationConfigurer.ValidationConfig;
import org.hdiv.config.annotation.ValidationConfigurer.ValidationConfig.EditableValidationConfigurer;
import org.hdiv.config.annotation.builders.SecurityConfigBuilder;
import org.hdiv.config.validations.DefaultValidationParser;
import org.hdiv.config.validations.DefaultValidationParser.ValidationParam;
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

		final SecurityConfigBuilder securityConfigBuilder = securityConfigBuilder();
		configure(securityConfigBuilder);

		final HDIVConfig config = securityConfigBuilder.build();
		config.setEditableDataValidationProvider(editableDataValidationProvider());

		// User configured exclusions
		final ExclusionRegistry exclusionRegistry = new ExclusionRegistry(patternMatcherFactory());
		addExclusions(exclusionRegistry);
		// Start Pages
		final List<StartPage> exclusions = exclusionRegistry.getUrlExclusions();
		config.setUserStartPages(exclusions);
		// StartParameters
		final List<String> paramExclusions = exclusionRegistry.getParamExclusions();
		config.setUserStartParameters(paramExclusions);
		// ParamsWithoutValidation
		final Map<String, List<String>> paramsWithoutValidation = exclusionRegistry.getParamExclusionsForUrl();
		config.setParamsWithoutValidation(paramsWithoutValidation);

		// Long living pages
		final LongLivingPagesRegistry longLivingPagesRegistry = new LongLivingPagesRegistry();
		addLongLivingPages(longLivingPagesRegistry);
		final Map<String, String> longLivingPages = longLivingPagesRegistry.getLongLivingPages();
		config.setLongLivingPages(longLivingPages);

		return config;
	}

	@Bean
	protected SecurityConfigBuilder securityConfigBuilder() {
		final SecurityConfigBuilder builder = new SecurityConfigBuilder(patternMatcherFactory());
		return builder;
	}

	@Bean
	public IApplication securityApplication() {
		final ApplicationHDIV application = new ApplicationHDIV();
		return application;
	}

	@Bean
	public ValidationResult validationResult() {
		final ValidationResult result = new ValidationResult();
		return result;
	}

	@Bean
	public PatternMatcherFactory patternMatcherFactory() {

		final PatternMatcherFactory patternMatcherFactory = new PatternMatcherFactory();
		return patternMatcherFactory;
	}

	@Bean
	public UidGenerator uidGenerator() {
		final RandomGuidUidGenerator uidGenerator = new RandomGuidUidGenerator();
		return uidGenerator;
	}

	@Bean
	@Scope(BeanDefinition.SCOPE_PROTOTYPE)
	public PageIdGenerator pageIdGenerator() {

		final SequentialPageIdGenerator pageIdGenerator = new SequentialPageIdGenerator();
		return pageIdGenerator;
	}

	@Bean
	public IUserData securityUserData() {
		final UserData userData = new UserData();
		return userData;
	}

	@Bean
	public Logger securityLogger() {
		final Logger logger = new Logger();
		return logger;
	}

	@Bean
	public ValidatorErrorHandler validatorErrorHandler() {
		final DefaultValidatorErrorHandler validatorErrorHandler = new DefaultValidatorErrorHandler();
		validatorErrorHandler.setConfig(hdivConfig());
		return validatorErrorHandler;
	}

	@Bean
	@Scope(BeanDefinition.SCOPE_PROTOTYPE)
	public IStateCache stateCache() {

		final SecurityConfigBuilder builder = securityConfigBuilder();
		final int maxPagesPerSession = builder.getMaxPagesPerSession();

		final StateCache stateCache = new StateCache();
		if (maxPagesPerSession > 0) {
			stateCache.setMaxSize(maxPagesPerSession);
		}
		return stateCache;
	}

	@Bean
	public ISession securitySession() {
		final SessionHDIV session = new SessionHDIV();
		return session;
	}

	@Bean
	public StateUtil stateUtil() {
		final StateUtil stateUtil = new StateUtil();
		stateUtil.setConfig(hdivConfig());
		stateUtil.setSession(securitySession());
		stateUtil.setStateScopeManager(stateScopeManager());
		stateUtil.init();
		return stateUtil;
	}

	@Bean
	public IDataValidator dataValidator() {
		final DataValidator dataValidator = new DataValidator();
		dataValidator.setConfig(hdivConfig());
		return dataValidator;
	}

	@Bean
	public StateScopeManager stateScopeManager() {
		final List<StateScope> stateScopes = new ArrayList<StateScope>();
		stateScopes.add(userSessionStateScope());
		stateScopes.add(appStateScope());
		final DefaultStateScopeManager scopeManager = new DefaultStateScopeManager(stateScopes);
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
		final DataComposerFactory dataComposerFactory = new DataComposerFactory();
		dataComposerFactory.setConfig(hdivConfig());
		dataComposerFactory.setSession(securitySession());
		dataComposerFactory.setStateUtil(stateUtil());
		dataComposerFactory.setUidGenerator(uidGenerator());
		dataComposerFactory.setStateScopeManager(stateScopeManager());
		return dataComposerFactory;
	}

	@Bean
	public IValidationHelper requestValidationHelper() {

		final ValidatorHelperRequest validatorHelperRequest = new ValidatorHelperRequest();
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
		final DefaultRequestInitializer requestInitializer = new DefaultRequestInitializer();
		requestInitializer.setConfig(hdivConfig());
		return requestInitializer;
	}

	@Bean
	public ServletContextInitializer securityServletContextInitializer() {
		final DefaultServletContextInitializer servletContextInitializer = new DefaultServletContextInitializer();
		servletContextInitializer.setConfig(hdivConfig());
		servletContextInitializer.setApplication(securityApplication());
		servletContextInitializer.setFormUrlProcessor(formUrlProcessor());
		servletContextInitializer.setLinkUrlProcessor(linkUrlProcessor());
		return servletContextInitializer;
	}

	@Bean
	public SessionInitializer securitySessionInitializer() {
		final DefaultSessionInitializer sessionInitializer = new DefaultSessionInitializer();
		sessionInitializer.setConfig(hdivConfig());
		return sessionInitializer;
	}

	@Bean
	public LinkUrlProcessor linkUrlProcessor() {
		final LinkUrlProcessor linkUrlProcessor = new LinkUrlProcessor();
		linkUrlProcessor.setConfig(hdivConfig());
		return linkUrlProcessor;
	}

	@Bean
	public FormUrlProcessor formUrlProcessor() {
		final FormUrlProcessor formUrlProcessor = new FormUrlProcessor();
		formUrlProcessor.setConfig(hdivConfig());
		return formUrlProcessor;
	}

	@Bean
	public BasicUrlProcessor basicUrlProcessor() {
		final BasicUrlProcessor basicUrlProcessor = new BasicUrlProcessor();
		basicUrlProcessor.setConfig(hdivConfig());
		return basicUrlProcessor;
	}

	@Bean
	public EditableDataValidationProvider editableDataValidationProvider() {

		final DefaultEditableDataValidationProvider provider = new DefaultEditableDataValidationProvider();
		provider.setValidationRepository(editableValidationRepository());
		return provider;
	}

	@Bean
	public ValidationRepository editableValidationRepository() {

		// Default rules
		final List<IValidation> defaultRules = getDefaultRules();
		// Custom rules
		final Map<String, IValidation> customRules = getCustomRules();

		// Validation configuration
		final Map<ValidationTarget, List<IValidation>> validationsData = getValidationsData(defaultRules, customRules);

		final DefaultValidationRepository repository = new DefaultValidationRepository();
		repository.setValidations(validationsData);
		repository.setDefaultValidations(defaultRules);
		return repository;
	}

	protected List<IValidation> getDefaultRules() {

		// Load validations from xml
		final DefaultValidationParser parser = new DefaultValidationParser();
		parser.readDefaultValidations();
		final List<Map<ValidationParam, String>> validations = parser.getValidations();

		final List<IValidation> defaultRules = new ArrayList<IValidation>();

		for (final Map<ValidationParam, String> validation : validations) {
			// Map contains validation id and regex extracted from the xml
			final String id = validation.get(ValidationParam.ID);
			final String regex = validation.get(ValidationParam.REGEX);

			// Create bean for the validation
			final Validation validationBean = new Validation();
			validationBean.setName(id);
			validationBean.setDefaultValidation(true);
			validationBean.setRejectedPattern(regex);

			defaultRules.add(validationBean);
		}
		return defaultRules;
	}

	protected Map<String, IValidation> getCustomRules() {

		final RuleRegistry registry = new RuleRegistry();
		addRules(registry);
		final Map<String, IValidation> customRules = registry.getRules();
		return customRules;
	}

	protected Map<ValidationTarget, List<IValidation>> getValidationsData(final List<IValidation> defaultRules,
			final Map<String, IValidation> customRules) {

		final PatternMatcherFactory patternMatcherFactory = patternMatcherFactory();

		final ValidationConfigurer validationConfigurer = new ValidationConfigurer();
		configureEditableValidation(validationConfigurer);
		final List<ValidationConfig> validationConfigs = validationConfigurer.getValidationConfigs();

		final Map<ValidationTarget, List<IValidation>> validationsData = new LinkedHashMap<ValidationTarget, List<IValidation>>();

		for (final ValidationConfig validationConfig : validationConfigs) {

			final String urlPattern = validationConfig.getUrlPattern();
			final EditableValidationConfigurer editableValidationConfigurer = validationConfig.getEditableValidationConfigurer();
			final boolean useDefaultRules = editableValidationConfigurer.isDefaultRules();
			final List<String> selectedRules = editableValidationConfigurer.getRules();
			final List<String> selectedParams = editableValidationConfigurer.getParameters();

			// Add selected rules
			final List<IValidation> activeRules = new ArrayList<IValidation>();
			for (final String ruleName : selectedRules) {

				final IValidation val = customRules.get(ruleName);
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
			final ValidationTarget target = new ValidationTarget();
			if (urlPattern != null) {
				final PatternMatcher urlMatcher = patternMatcherFactory.getPatternMatcher(urlPattern);
				target.setUrl(urlMatcher);
			}
			final List<PatternMatcher> paramMatchers = new ArrayList<PatternMatcher>();
			for (final String param : selectedParams) {
				final PatternMatcher matcher = patternMatcherFactory.getPatternMatcher(param);
				paramMatchers.add(matcher);
			}
			target.setParams(paramMatchers);

			validationsData.put(target, activeRules);
		}

		return validationsData;
	}
}
