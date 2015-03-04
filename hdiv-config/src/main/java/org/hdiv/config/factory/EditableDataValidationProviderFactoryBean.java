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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hdiv.regex.PatternMatcher;
import org.hdiv.regex.PatternMatcherFactory;
import org.hdiv.validator.DefaultEditableDataValidationProvider;
import org.hdiv.validator.DefaultEditableDataValidationProvider.ValidationTarget;
import org.hdiv.validator.EditableDataValidationProvider;
import org.hdiv.validator.IValidation;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * {@link FactoryBean} to create {@link EditableDataValidationProvider} instances.
 * 
 * @since 2.1.10
 */
public class EditableDataValidationProviderFactoryBean extends AbstractFactoryBean<EditableDataValidationProvider> {

	/**
	 * Regular expression executor factory.
	 */
	private transient PatternMatcherFactory patternMatcherFactory;

	/**
	 * <p>
	 * Map for configuration purpose.
	 * </p>
	 * <p>
	 * ValidationTargetData :: List of Validation ids.
	 * </p>
	 */
	protected Map<ValidationTargetData, List<String>> validationsData;

	@Override
	public Class<?> getObjectType() {
		return EditableDataValidationProvider.class;
	}

	@Override
	protected EditableDataValidationProvider createInstance() throws Exception {
		// create EditableDataValidationProvider instance from XML config

		DefaultEditableDataValidationProvider provider = new DefaultEditableDataValidationProvider();

		Map<ValidationTarget, List<IValidation>> vals = new LinkedHashMap<ValidationTarget, List<IValidation>>();

		for (ValidationTargetData data : this.validationsData.keySet()) {

			ValidationTarget target = new ValidationTarget();
			List<String> ids = validationsData.get(data);

			if (data.getUrl() != null) {
				PatternMatcher matcher = this.patternMatcherFactory.getPatternMatcher(data.getUrl());
				target.setUrl(matcher);
			}
			if (data.getParams() != null && data.getParams().size() > 0) {
				List<String> params = data.getParams();
				List<PatternMatcher> matchers = new ArrayList<PatternMatcher>();
				for (String param : params) {
					PatternMatcher matcher = this.patternMatcherFactory.getPatternMatcher(param);
					matchers.add(matcher);
				}
				target.setParams(matchers);
			}
			vals.put(target, this.createValidationList(ids));
		}
		provider.setValidations(vals);

		return provider;
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

	public void setValidationsData(Map<ValidationTargetData, List<String>> validationsData) {
		this.validationsData = validationsData;
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

		public void setUrl(String url) {
			this.url = url;
		}

		public void setParams(List<String> params) {
			this.params = params;
		}

	}

}
