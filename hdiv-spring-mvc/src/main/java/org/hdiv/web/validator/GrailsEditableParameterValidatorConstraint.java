package org.hdiv.web.validator;

import org.codehaus.groovy.grails.validation.Constraint;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;

public class GrailsEditableParameterValidatorConstraint extends
		AbstractEditableParameterValidator implements Constraint {

	public static final String NAME = "editableValidation";

	private boolean enabled;

	private String propertyName;

	private Class<?> owningClass;

	public void setParameter(Object parameter) {

		if (parameter == null) {
			this.enabled = true;
		} else if (!(parameter instanceof Boolean)) {
			throw new IllegalArgumentException("Parameter for constraint ["
					+ NAME + "] of property [" + propertyName + "] of class ["
					+ owningClass + "] must be a boolean value");
		} else {
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
