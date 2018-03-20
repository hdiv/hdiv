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
package org.hdiv.config.annotation.builders;

import org.hdiv.config.HDIVConfig;
import org.hdiv.config.Strategy;
import org.hdiv.regex.PatternMatcherFactory;

/**
 * Creates a new instance of {@link HDIVConfig}.
 */
public class SecurityConfigBuilder {

	protected HDIVConfig config;

	protected int maxPagesPerSession;

	protected SessionExpiredConfigure sessionExpiredConfigure = new SessionExpiredConfigure();

	public SecurityConfigBuilder(final PatternMatcherFactory patternMatcherFactory) {
		config = new HDIVConfig();
		config.setPatternMatcherFactory(patternMatcherFactory);
	}

	public SecurityConfigBuilder cookiesConfidentiality(final boolean cookiesConfidentiality) {
		config.setAvoidCookiesConfidentiality(!cookiesConfidentiality);
		return this;
	}

	public SecurityConfigBuilder cookiesIntegrity(final boolean cookiesIntegrity) {
		config.setAvoidCookiesIntegrity(!cookiesIntegrity);
		return this;
	}

	public SecurityConfigBuilder reuseExistingPageInAjaxRequest(final boolean reuseExistingPageInAjaxRequest) {
		config.setReuseExistingPageInAjaxRequest(reuseExistingPageInAjaxRequest);
		return this;
	}

	public SecurityConfigBuilder validateUrlsWithoutParams(final boolean validateUrlsWithoutParams) {
		config.setAvoidValidationInUrlsWithoutParams(!validateUrlsWithoutParams);
		return this;
	}

	public SecurityConfigBuilder confidentiality(final boolean confidentiality) {
		config.setConfidentiality(confidentiality);
		return this;
	}

	public SecurityConfigBuilder debugMode(final boolean debugMode) {
		config.setDebugMode(debugMode);
		return this;
	}

	public SecurityConfigBuilder errorPage(final String errorPage) {
		config.setErrorPage(errorPage);
		return this;
	}

	@SuppressWarnings("deprecation")
	public SecurityConfigBuilder randomName(final boolean randomName) {
		config.setRandomName(randomName);
		return this;
	}

	public SecurityConfigBuilder showErrorPageOnEditableValidation(final boolean showErrorPageOnEditableValidation) {
		config.setShowErrorPageOnEditableValidation(showErrorPageOnEditableValidation);
		return this;
	}

	public SecurityConfigBuilder editableFieldsRequiredByDefault(final boolean editableFieldsRequiredByDefault) {
		config.setEditableFieldsRequiredByDefault(editableFieldsRequiredByDefault);
		return this;
	}

	@Deprecated
	public SecurityConfigBuilder strategy(final Strategy strategy) {
		config.setStrategy(strategy);
		return this;
	}

	@SuppressWarnings("deprecation")
	public SecurityConfigBuilder stateParameterName(final String stateParameterName) {
		config.setStateParameterName(stateParameterName);
		return this;
	}

	@SuppressWarnings("deprecation")
	public SecurityConfigBuilder modifyStateParameterName(final String modifyStateParameterName) {
		config.setModifyStateParameterName(modifyStateParameterName);
		return this;
	}

	public SecurityConfigBuilder maxPagesPerSession(final int maxPagesPerSession) {
		this.maxPagesPerSession = maxPagesPerSession;
		return this;
	}

	public SecurityConfigBuilder urlObfuscation(final boolean urlObfuscation) {
		config.setUrlObfuscation(urlObfuscation);
		return this;
	}

	public SecurityConfigBuilder pentestingActive(final boolean penTestingActive) {
		config.setPentestingActive(penTestingActive);
		return this;
	}

	public SecurityConfigBuilder multipartIntegration(final boolean multipartIntegration) {
		config.setMultipartIntegration(multipartIntegration);
		return this;
	}

	public SessionExpiredConfigure sessionExpired() {
		return sessionExpiredConfigure;
	}

	public HDIVConfig build() {
		return config;
	}

	public int getMaxPagesPerSession() {
		return maxPagesPerSession;
	}

	public class SessionExpiredConfigure {

		public SessionExpiredConfigure homePage(final String sessionExpiredHomePage) {
			config.setSessionExpiredHomePage(sessionExpiredHomePage);
			return this;
		}

		public SessionExpiredConfigure loginPage(final String sessionExpiredLoginPage) {
			config.setSessionExpiredLoginPage(sessionExpiredLoginPage);
			return this;
		}

		public SecurityConfigBuilder and() {
			return SecurityConfigBuilder.this;
		}
	}
}
