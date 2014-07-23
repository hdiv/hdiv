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
package org.hdiv.config.annotation.jsf;

import org.hdiv.config.HDIVConfig;
import org.hdiv.config.annotation.condition.ConditionalOnFramework;
import org.hdiv.config.annotation.condition.SupportedFramework;
import org.hdiv.config.multipart.JsfMultipartConfig;
import org.hdiv.context.RedirectHelper;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataValidator.IDataValidator;
import org.hdiv.events.HDIVFacesEventListener;
import org.hdiv.filter.IValidationHelper;
import org.hdiv.filter.JsfValidatorHelper;
import org.hdiv.filter.ValidatorHelperRequest;
import org.hdiv.logs.Logger;
import org.hdiv.session.ISession;
import org.hdiv.state.StateUtil;
import org.hdiv.urlProcessor.BasicUrlProcessor;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.validators.EditableValidator;
import org.hdiv.validators.HtmlInputHiddenValidator;
import org.hdiv.validators.RequestParameterValidator;
import org.hdiv.validators.UICommandValidator;
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
	private Logger logger;

	@Autowired
	private StateUtil stateUtil;

	@Autowired
	private HDIVConfig config;

	@Autowired
	private ISession session;

	@Autowired
	private IDataValidator dataValidator;

	@Autowired
	private BasicUrlProcessor basicUrlProcessor;

	@Autowired
	private DataComposerFactory dataComposerFactory;

	@Autowired
	private LinkUrlProcessor linkUrlProcessor;

	@Bean
	@Primary
	public IValidationHelper jsfValidatorHelper() {

		ValidatorHelperRequest validatorHelperRequest = new JsfValidatorHelper();
		validatorHelperRequest.setLogger(this.logger);
		validatorHelperRequest.setStateUtil(this.stateUtil);
		validatorHelperRequest.setHdivConfig(this.config);
		validatorHelperRequest.setSession(this.session);
		validatorHelperRequest.setDataValidator(this.dataValidator);
		validatorHelperRequest.setUrlProcessor(this.basicUrlProcessor);
		validatorHelperRequest.setDataComposerFactory(this.dataComposerFactory);
		validatorHelperRequest.init();
		return validatorHelperRequest;
	}

	@Bean
	public RedirectHelper redirectHelper() {

		RedirectHelper redirectHelper = new RedirectHelper();
		redirectHelper.setLinkUrlProcessor(this.linkUrlProcessor);
		return redirectHelper;
	}

	@Bean
	public JsfMultipartConfig jsfMultipartConfig() {
		JsfMultipartConfig multipartConfig = new JsfMultipartConfig();
		return multipartConfig;
	}

	@Bean
	public HDIVFacesEventListener hdivFacesEventListener() {

		// ComponentValidator instances
		RequestParameterValidator requestParameterValidator = new RequestParameterValidator();
		requestParameterValidator.setHdivConfig(this.config);

		UICommandValidator uiCommandValidator = new UICommandValidator();

		HtmlInputHiddenValidator htmlInputHiddenValidator = new HtmlInputHiddenValidator();

		EditableValidator editableValidator = new EditableValidator();
		editableValidator.setHdivConfig(this.config);

		// EventListener instance
		HDIVFacesEventListener listener = new HDIVFacesEventListener();
		listener.setConfig(this.config);
		listener.setLogger(this.logger);
		listener.setHtmlInputHiddenValidator(htmlInputHiddenValidator);
		listener.setRequestParamValidator(requestParameterValidator);
		listener.setUiCommandValidator(uiCommandValidator);
		listener.setEditableValidator(editableValidator);
		return listener;
	}

}
