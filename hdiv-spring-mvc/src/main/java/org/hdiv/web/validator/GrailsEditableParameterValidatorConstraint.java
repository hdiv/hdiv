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
package org.hdiv.web.validator;

import org.codehaus.groovy.grails.validation.Constraint;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;

public class GrailsEditableParameterValidatorConstraint extends AbstractEditableParameterValidator implements
		Constraint {

	public static final String NAME = "editableValidation";

	private boolean enabled;

	private String propertyName;

	private Class<?> owningClass;

	public void setParameter(Object parameter) {

		if (parameter == null) {
			this.enabled = true;
		}
		else if (!(parameter instanceof Boolean)) {
			throw new IllegalArgumentException("Parameter for constraint [" + NAME + "] of property [" + propertyName
					+ "] of class [" + owningClass + "] must be a boolean value");
		}
		else {
			this.enabled = ((Boolean) parameter).booleanValue();
		}
	}

	public Object getParameter() {
		return this.enabled;
	}

	@SuppressWarnings("rawtypes")
	public boolean supports(Class type) {
		return type != null && String.class.isAssignableFrom(type);
	}

	public String getName() {
		return NAME;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public boolean isValid() {
		return true;
	}

	public void setMessageSource(MessageSource messageSource) {
		// Not necessary
	}

	@SuppressWarnings("rawtypes")
	public void setOwningClass(Class owningClass) {
		this.owningClass = owningClass;
	}

	public void validate(Object target, Object propertyValue, Errors errors) {
		if (!enabled) {
			return;
		}
		super.validateEditableParameter(this.propertyName, errors);
	}

}
