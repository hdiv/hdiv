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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hdiv.config.StartPage;
import org.hdiv.regex.PatternMatcherFactory;
import org.springframework.util.Assert;

/**
 * <p>
 * Registry to add exclusions to the validation phase.
 * </p>
 * <p>
 * Can contain two types of exclusions, URL and parameter. The first to exclude URLs from validation and the second to
 * exclude parameter names.
 * </p>
 *
 * @since 2.1.7
 */
public class ExclusionRegistry {

	private final PatternMatcherFactory patternMatcherFactory;

	private final List<UrlExclusionRegistration> urlRegistrations = new ArrayList<UrlExclusionRegistration>();

	private final List<ParamExclusionRegistration> paramRegistrations = new ArrayList<ParamExclusionRegistration>();

	public ExclusionRegistry(final PatternMatcherFactory patternMatcherFactory) {
		this.patternMatcherFactory = patternMatcherFactory;
	}

	/**
	 * <p>
	 * Configure one or more url exclusion.
	 * </p>
	 * <p>
	 * Excluded urls are not validated by HDIV.
	 * </p>
	 *
	 * @param urlPatterns Url patterns.
	 * @return more configuration options
	 */
	public UrlExclusionRegistration addUrlExclusions(final String... urlPatterns) {
		Assert.notEmpty(urlPatterns, "Url patterns are required");
		final UrlExclusionRegistration registration = new UrlExclusionRegistration(urlPatterns);
		urlRegistrations.add(registration);
		return registration;
	}

	/**
	 * <p>
	 * Configure one or more parameter exclusion.
	 * </p>
	 * <p>
	 * Excluded parameters are not validated by HDIV.
	 * </p>
	 *
	 * @param parameterPatterns Parameter name patterns.
	 * @return more configuration options
	 */
	public ParamExclusionRegistration addParamExclusions(final String... parameterPatterns) {
		Assert.notEmpty(parameterPatterns, "Parameter patterns are required");
		final ParamExclusionRegistration registration = new ParamExclusionRegistration(Arrays.asList(parameterPatterns));
		paramRegistrations.add(registration);
		return registration;
	}

	protected List<StartPage> getUrlExclusions() {

		final List<StartPage> allStartPages = new ArrayList<StartPage>();

		for (final UrlExclusionRegistration regitration : urlRegistrations) {
			final List<StartPage> startPages = regitration.getExclusions();
			for (final StartPage sp : startPages) {
				// Compile Pattern
				sp.setCompiledPattern(patternMatcherFactory.getPatternMatcher(sp.getPattern()));
				allStartPages.add(sp);
			}
		}
		return allStartPages;
	}

	protected List<String> getParamExclusions() {

		final List<String> paramExclusions = new ArrayList<String>();

		for (final ParamExclusionRegistration regitration : paramRegistrations) {
			final String urlPattern = regitration.getUrlPattern();
			if (urlPattern == null) {
				paramExclusions.addAll(regitration.getParameterPatterns());
			}
		}
		return paramExclusions;
	}

	protected Map<String, List<String>> getParamExclusionsForUrl() {

		final Map<String, List<String>> paramExclusions = new HashMap<String, List<String>>();

		for (final ParamExclusionRegistration regitration : paramRegistrations) {
			final String urlPattern = regitration.getUrlPattern();
			if (urlPattern != null) {
				paramExclusions.put(urlPattern, regitration.getParameterPatterns());
			}
		}
		return paramExclusions;
	}
}
