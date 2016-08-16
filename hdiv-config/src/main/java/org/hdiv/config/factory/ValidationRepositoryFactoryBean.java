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
package org.hdiv.config.factory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hdiv.regex.PatternMatcher;
import org.hdiv.regex.PatternMatcherFactory;
import org.hdiv.validator.DefaultValidationRepository;
import org.hdiv.validator.IValidation;
import org.hdiv.validator.ValidationTarget;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * {@link FactoryBean} to create {@link DefaultValidationRepository} instances.
 * 
 * @since 2.1.10
 */
public class ValidationRepositoryFactoryBean extends AbstractFactoryBean<DefaultValidationRepository> {

	/**
	 * Regular expression executor factory.
	 */
	protected PatternMatcherFactory patternMatcherFactory;

	/**
	 * <p>
	 * Map for configuration purpose.
	 * </p>
	 * <p>
	 * ValidationTargetData :: List of Validation ids.
	 * </p>
	 */
	protected Map<ValidationTargetData, List<String>> validationsData;

	/**
	 * All default editable validations.
	 */
	protected List<IValidation> defaultValidations;

	@Override
	public Class<?> getObjectType() {
		return DefaultValidationRepository.class;
	}

	@Override
	protected DefaultValidationRepository createInstance() throws Exception {
		// create DefaultValidationRepository instance from XML config

		DefaultValidationRepository repository = new DefaultValidationRepository();

		Map<ValidationTarget, List<IValidation>> vals = new LinkedHashMap<ValidationTarget, List<IValidation>>();

		for (Entry<ValidationTargetData, List<String>> entry : validationsData.entrySet()) {

			ValidationTarget target = new ValidationTarget();
			ValidationTargetData data = entry.getKey();
			List<String> ids = entry.getValue();

			if (data.getUrl() != null) {
				PatternMatcher matcher = patternMatcherFactory.getPatternMatcher(data.getUrl());
				target.setUrl(matcher);
			}
			if (data.getParams() != null && !data.getParams().isEmpty()) {
				List<String> params = data.getParams();
				List<PatternMatcher> matchers = new ArrayList<PatternMatcher>();
				for (String param : params) {
					PatternMatcher matcher = patternMatcherFactory.getPatternMatcher(param);
					matchers.add(matcher);
				}
				target.setParams(matchers);
			}
			vals.put(target, createValidationList(ids));
		}
		repository.setValidations(vals);

		repository.setDefaultValidations(defaultValidations);

		return repository;
	}

	/**
	 * Convert List with bean ids in another List with the bean instances.
	 * 
	 * @param ids List with bean ids.
	 * @return List with bean instances.
	 */
	@SuppressWarnings("unchecked")
	protected List<IValidation> createValidationList(final List<String> ids) {
		List<IValidation> newList = new ArrayList<IValidation>();

		for (String id : ids) {
			Object bean = getBeanFactory().getBean(id);
			if (bean instanceof IValidation) {
				IValidation validation = (IValidation) bean;
				newList.add(validation);
			}
			else if (bean instanceof List<?>) {
				List<IValidation> validations = (List<IValidation>) bean;
				newList.addAll(validations);
			}
		}
		return newList;
	}

	public static class ValidationTargetData implements Serializable {

		private static final long serialVersionUID = 4991119416021943596L;

		private String url;

		private List<String> params;

		public String getUrl() {
			return url;
		}

		public List<String> getParams() {
			return params;
		}

		public void setUrl(final String url) {
			this.url = url;
		}

		public void setParams(final List<String> params) {
			this.params = params;
		}

	}

	public void setPatternMatcherFactory(final PatternMatcherFactory patternMatcherFactory) {
		this.patternMatcherFactory = patternMatcherFactory;
	}

	public void setValidationsData(final Map<ValidationTargetData, List<String>> validationsData) {
		this.validationsData = validationsData;
	}

	public void setDefaultValidations(final List<IValidation> defaultValidations) {
		this.defaultValidations = defaultValidations;
	}
}
