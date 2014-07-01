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
package org.hdiv.config.annotation.builders;

import org.hdiv.config.HDIVConfig;
import org.hdiv.config.Strategy;
import org.hdiv.regex.PatternMatcherFactory;

/**
 * Creates a new instance of {@link HDIVConfig}.
 */
public class SecurityConfigBuilder {

	private HDIVConfig config;

	private int maxPagesPerSession;

	private SessionExpiredConfigure sessionExpiredConfigure = new SessionExpiredConfigure();

	private CipherConfigure cipherConfigure = new CipherConfigure();

	public SecurityConfigBuilder(PatternMatcherFactory patternMatcherFactory) {
		this.config = new HDIVConfig();
		this.config.setPatternMatcherFactory(patternMatcherFactory);
	}

	public SecurityConfigBuilder avoidCookiesConfidentiality(boolean avoidCookiesConfidentiality) {
		this.config.setAvoidCookiesConfidentiality(avoidCookiesConfidentiality);
		return this;
	}

	public SecurityConfigBuilder avoidCookiesIntegrity(boolean avoidCookiesIntegrity) {
		this.config.setAvoidCookiesIntegrity(avoidCookiesIntegrity);
		return this;
	}

	public SecurityConfigBuilder avoidValidationInUrlsWithoutParams(boolean avoidValidationInUrlsWithoutParams) {
		this.config.setAvoidValidationInUrlsWithoutParams(avoidValidationInUrlsWithoutParams);
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

	public CipherConfigure cipher() {
		return this.cipherConfigure;
	}

	public CipherConfigure getCipherConfigure() {
		return this.cipherConfigure;
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

	public class CipherConfigure {

		private String provider;

		private String algorithm;

		private String prngAlgorithm;

		private String transformation;

		private int keySize;

		/**
		 * @return the provider
		 */
		public String getProvider() {
			return provider;
		}

		/**
		 * @param provider
		 *            the provider to set
		 */
		public CipherConfigure provider(String provider) {
			this.provider = provider;
			return this;
		}

		/**
		 * @return the algorithm
		 */
		public String getAlgorithm() {
			return algorithm;
		}

		/**
		 * @param algorithm
		 *            the algorithm to set
		 */
		public CipherConfigure algorithm(String algorithm) {
			this.algorithm = algorithm;
			return this;
		}

		/**
		 * @return the prngAlgorithm
		 */
		public String getPrngAlgorithm() {
			return prngAlgorithm;
		}

		/**
		 * @param prngAlgorithm
		 *            the prngAlgorithm to set
		 */
		public CipherConfigure prngAlgorithm(String prngAlgorithm) {
			this.prngAlgorithm = prngAlgorithm;
			return this;
		}

		/**
		 * @return the transformation
		 */
		public String getTransformation() {
			return transformation;
		}

		/**
		 * @param transformation
		 *            the transformation to set
		 */
		public CipherConfigure transformation(String transformation) {
			this.transformation = transformation;
			return this;
		}

		/**
		 * @return the keySize
		 */
		public int getKeySize() {
			return keySize;
		}

		/**
		 * @param keySize
		 *            the keySize to set
		 */
		public CipherConfigure keySize(int keySize) {
			this.keySize = keySize;
			return this;
		}

		public SecurityConfigBuilder and() {
			return SecurityConfigBuilder.this;
		}

	}

}
