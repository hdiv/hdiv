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
package org.hdiv.config.annotation.jsf;

import org.hdiv.components.support.OutcomeTargetComponentProcessor;
import org.hdiv.components.support.OutputLinkComponentProcessor;
import org.hdiv.config.HDIVConfig;
import org.hdiv.config.annotation.condition.ConditionalOnFramework;
import org.hdiv.config.annotation.condition.SupportedFramework;
import org.hdiv.config.multipart.JsfMultipartConfig;
import org.hdiv.context.RedirectHelper;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataValidator.IDataValidator;
import org.hdiv.filter.IValidationHelper;
import org.hdiv.filter.JsfValidatorErrorHandler;
import org.hdiv.filter.JsfValidatorHelper;
import org.hdiv.filter.ValidatorErrorHandler;
import org.hdiv.filter.ValidatorHelperRequest;
import org.hdiv.session.ISession;
import org.hdiv.state.StateUtil;
import org.hdiv.state.scope.StateScopeManager;
import org.hdiv.urlProcessor.BasicUrlProcessor;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.validation.DefaultComponentTreeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Contains the configuration beans for JavaServer Faces framework support.
 * 
 * @since 2.1.7
 */
@Configuration
@ConditionalOnFramework(SupportedFramework.JSF)
public class JsfConfigurationSupport {

	@Autowired
	protected StateUtil stateUtil;

	@Autowired
	protected HDIVConfig config;

	@Autowired
	protected ISession session;

	@Autowired
	protected IDataValidator dataValidator;

	@Autowired
	protected BasicUrlProcessor basicUrlProcessor;

	@Autowired
	protected DataComposerFactory dataComposerFactory;

	@Autowired
	protected LinkUrlProcessor linkUrlProcessor;

	@Autowired
	protected StateScopeManager stateScopeManager;

	@Bean
	@Primary
	public ValidatorErrorHandler validatorErrorHandler() {
		JsfValidatorErrorHandler validatorErrorHandler = new JsfValidatorErrorHandler();
		validatorErrorHandler.setConfig(config);
		return validatorErrorHandler;
	}

	@Bean
	@Primary
	public IValidationHelper jsfValidatorHelper() {

		ValidatorHelperRequest validatorHelperRequest = new JsfValidatorHelper();
		validatorHelperRequest.setStateUtil(stateUtil);
		validatorHelperRequest.setHdivConfig(config);
		validatorHelperRequest.setSession(session);
		validatorHelperRequest.setDataValidator(dataValidator);
		validatorHelperRequest.setUrlProcessor(basicUrlProcessor);
		validatorHelperRequest.setDataComposerFactory(dataComposerFactory);
		validatorHelperRequest.setStateScopeManager(stateScopeManager);
		validatorHelperRequest.init();
		return validatorHelperRequest;
	}

	@Bean
	public DefaultComponentTreeValidator componentTreeValidator() {

		DefaultComponentTreeValidator componentTreeValidator = new DefaultComponentTreeValidator();
		componentTreeValidator.setConfig(config);
		componentTreeValidator.createComponentValidators();
		return componentTreeValidator;
	}

	@Bean
	public RedirectHelper redirectHelper() {

		RedirectHelper redirectHelper = new RedirectHelper();
		redirectHelper.setLinkUrlProcessor(linkUrlProcessor);
		return redirectHelper;
	}

	@Bean
	public JsfMultipartConfig jsfMultipartConfig() {
		return new JsfMultipartConfig();
	}

	@Bean
	public OutcomeTargetComponentProcessor outcomeTargetComponentProcessor() {

		OutcomeTargetComponentProcessor processor = new OutcomeTargetComponentProcessor();
		processor.setConfig(config);
		processor.setLinkUrlProcessor(linkUrlProcessor);
		return processor;
	}

	@Bean
	public OutputLinkComponentProcessor outputLinkComponentProcessor() {

		OutputLinkComponentProcessor processor = new OutputLinkComponentProcessor();
		processor.setConfig(config);
		processor.setLinkUrlProcessor(linkUrlProcessor);
		return processor;
	}

}