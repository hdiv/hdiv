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
package org.hdiv.config.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hdiv.config.HDIVValidations;
import org.hdiv.regex.PatternMatcher;
import org.hdiv.regex.PatternMatcherFactory;
import org.hdiv.validator.IValidation;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * {@link FactoryBean} to create {@link HDIVValidations} instances.
 * 
 * @since 2.1.7
 */
public class ValidationsFactoryBean extends AbstractFactoryBean<HDIVValidations> {

	/**
	 * Regular expression executor factory.
	 */
	private transient PatternMatcherFactory patternMatcherFactory;

	/**
	 * <p>
	 * Map for configuration purpose.
	 * </p>
	 * <p>
	 * Url pattern -> List of Validation ids.
	 * </p>
	 */
	protected Map<String, List<String>> validationsData;

	@Override
	public Class<?> getObjectType() {
		return HDIVValidations.class;
	}

	@Override
	protected HDIVValidations createInstance() throws Exception {
		// create HDIVvalidations instance for XML config

		HDIVValidations validations = new HDIVValidations();

		Map<PatternMatcher, List<IValidation>> urls = new HashMap<PatternMatcher, List<IValidation>>();

		for (String url : this.validationsData.keySet()) {
			List<String> ids = validationsData.get(url);
			PatternMatcher matcher = this.patternMatcherFactory.getPatternMatcher(url);
			urls.put(matcher, this.createValidationList(ids));
		}
		validations.setUrls(urls);

		return validations;
	}

	/**
	 * Convert List with bean ids in another List with the bean instances.
	 * 
	 * @param ids
	 *            List with bean ids.
	 * @return List with bean instances.
	 */
	@SuppressWarnings("unchecked")
	private List<IValidation> createValidationList(List<String> ids) {
		List<IValidation> newList = new ArrayList<IValidation>();

		for (String id : ids) {
			Object bean = this.getBeanFactory().getBean(id);
			if (bean instanceof IValidation) {
				IValidation validation = (IValidation) bean;
				newList.add(validation);
			} else if (bean instanceof List<?>) {
				List<IValidation> validations = (List<IValidation>) bean;
				newList.addAll(validations);
			}
		}
		return newList;
	}

	public void setPatternMatcherFactory(PatternMatcherFactory patternMatcherFactory) {
		this.patternMatcherFactory = patternMatcherFactory;
	}

	public void setValidationsData(Map<String, List<String>> validationsData) {
		this.validationsData = validationsData;
	}

}
