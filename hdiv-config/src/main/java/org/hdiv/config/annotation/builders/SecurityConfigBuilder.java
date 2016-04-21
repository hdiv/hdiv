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

	public SecurityConfigBuilder(PatternMatcherFactory patternMatcherFactory) {
		this.config = new HDIVConfig();
		this.config.setPatternMatcherFactory(patternMatcherFactory);
	}

	public SecurityConfigBuilder cookiesConfidentiality(boolean cookiesConfidentiality) {
		this.config.setAvoidCookiesConfidentiality(!cookiesConfidentiality);
		return this;
	}

	public SecurityConfigBuilder cookiesIntegrity(boolean cookiesIntegrity) {
		this.config.setAvoidCookiesIntegrity(!cookiesIntegrity);
		return this;
	}

	public SecurityConfigBuilder reuseExistingPageInAjaxRequest(boolean reuseExistingPageInAjaxRequest) {
		this.config.setReuseExistingPageInAjaxRequest(reuseExistingPageInAjaxRequest);
		return this;
	}

	public SecurityConfigBuilder validateUrlsWithoutParams(boolean validateUrlsWithoutParams) {
		this.config.setAvoidValidationInUrlsWithoutParams(!validateUrlsWithoutParams);
		return this;
	}

	public SecurityConfigBuilder confidentiality(boolean confidentiality) {
		this.config.setConfidentiality(confidentiality);
		return this;
	}

	public SecurityConfigBuilder debugMode(boolean debugMode) {
		this.config.setDebugMode(debugMode);
		return this;
	}

	public SecurityConfigBuilder errorPage(String errorPage) {
		this.config.setErrorPage(errorPage);
		return this;
	}

	public SecurityConfigBuilder randomName(boolean randomName) {
		this.config.setRandomName(randomName);
		return this;
	}

	public SecurityConfigBuilder showErrorPageOnEditableValidation(boolean showErrorPageOnEditableValidation) {
		this.config.setShowErrorPageOnEditableValidation(showErrorPageOnEditableValidation);
		return this;
	}

	public SecurityConfigBuilder strategy(Strategy strategy) {
		this.config.setStrategy(strategy);
		return this;
	}

	public SecurityConfigBuilder stateParameterName(String stateParameterName) {
		this.config.setStateParameterName(stateParameterName);
		return this;
	}

	public SecurityConfigBuilder modifyStateParameterName(String modifyStateParameterName) {
		this.config.setModifyStateParameterName(modifyStateParameterName);
		return this;
	}

	public SecurityConfigBuilder maxPagesPerSession(int maxPagesPerSession) {
		this.maxPagesPerSession = maxPagesPerSession;
		return this;
	}

	public SessionExpiredConfigure sessionExpired() {
		return this.sessionExpiredConfigure;
	}

	public HDIVConfig build() {
		return this.config;
	}

	public int getMaxPagesPerSession() {
		return maxPagesPerSession;
	}

	public class SessionExpiredConfigure {

		public SessionExpiredConfigure homePage(String sessionExpiredHomePage) {
			SecurityConfigBuilder.this.config.setSessionExpiredHomePage(sessionExpiredHomePage);
			return this;
		}

		public SessionExpiredConfigure loginPage(String sessionExpiredLoginPage) {
			SecurityConfigBuilder.this.config.setSessionExpiredLoginPage(sessionExpiredLoginPage);
			return this;
		}

		public SecurityConfigBuilder and() {
			return SecurityConfigBuilder.this;
		}
	}
}
