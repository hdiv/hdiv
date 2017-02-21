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
package org.hdiv.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.exception.HDIVException;
import org.hdiv.filter.ValidatorError;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.util.UtilsJsf;
import org.hdiv.validators.ComponentValidator;
import org.hdiv.validators.EditableValidator;
import org.hdiv.validators.GenericComponentValidator;
import org.hdiv.validators.HtmlInputHiddenValidator;
import org.hdiv.validators.UICommandValidator;

public class DefaultComponentTreeValidator implements ComponentTreeValidator {

	private static final Log log = LogFactory.getLog(DefaultComponentTreeValidator.class);

	protected final List<ComponentValidator> componentValidators = new ArrayList<ComponentValidator>();

	protected HDIVConfig config;

	public void createComponentValidators() {

		componentValidators.add(new GenericComponentValidator());
		componentValidators.add(new HtmlInputHiddenValidator());
		componentValidators.add(new UICommandValidator());
		EditableValidator editableValidator = new EditableValidator();
		editableValidator.setHdivConfig(config);
		componentValidators.add(editableValidator);
	}

	public List<ValidatorError> validateComponentTree(final FacesContext facesContext) {

		ValidationContext context = new ValidationContext(facesContext);

		PartialViewContext partialContext = facesContext.getPartialViewContext();
		if (partialContext != null && partialContext.isPartialRequest()) {
			// Is an ajax call partially processing the component tree

			String source = facesContext.getExternalContext().getRequestParameterMap().get("javax.faces.source");
			UIComponent sourceComp = null;
			if (source != null) {
				sourceComp = facesContext.getViewRoot().findComponent(source);
			}

			Set<UIComponent> componentsToValidate = new HashSet<UIComponent>();

			Collection<String> execIds = partialContext.getExecuteIds();
			for (String execId : execIds) {
				UIComponent execComp = null;
				if (execId.startsWith("@")) {
					if (execId.equals("@all")) {
						execComp = facesContext.getViewRoot();
					}
					else if (execId.equals("@form")) {
						if (sourceComp != null) {
							execComp = UtilsJsf.findParentForm(sourceComp);
						}
						else {
							throw new HDIVException("Cant determine the component to validate!");
						}
					}
					else if (execId.equals("@this")) {
						if (sourceComp != null) {
							execComp = sourceComp;
						}
						else {
							throw new HDIVException("Cant determine the component to validate!");
						}
					}
					else if (execId.equals("@none")) {
						execComp = null;
					}
					else if (execId.equals("@parent")) {
						if (sourceComp != null) {
							execComp = sourceComp.getParent();
						}
						else {
							throw new HDIVException("Cant determine the component to validate!");
						}
					}
					else {
						log.error("Component reference '" + execId + "' is not supported");
					}
				}
				else {
					execComp = facesContext.getViewRoot().findComponent(execId);
				}

				UIComponent compToValidate = execComp;

				UIForm submittedForm = findParentSubmittedForm(facesContext, execComp);
				if (submittedForm != null) {
					compToValidate = submittedForm;
				}

				if (compToValidate != null) {
					componentsToValidate.add(compToValidate);
				}
			}

			if (componentsToValidate.size() > 0) {
				for (UIComponent comp : componentsToValidate) {
					validateComponentTree(context, comp);
				}
			}

		}
		else {
			// This is not an Ajax request
			// Find submitted form

			UIForm submittedForm = findSubmittedForm(facesContext, facesContext.getViewRoot());

			if (submittedForm == null) {
				if (log.isErrorEnabled()) {
					log.error("Can't find submitted form.");
				}
				// TODO review this error key
				return Collections.singletonList(new ValidatorError("ERROR_VALIDATING"));
			}
			// Validate component tree starting in form
			validateComponentTree(context, submittedForm);
		}

		List<ValidatorError> errors = checkParameters(context);

		errors.addAll(context.getErrors());
		return errors;
	}

	protected void validateComponentTree(final ValidationContext context, final UIComponent component) {

		validateComponent(context, component);

		Iterator<UIComponent> it = component.getFacetsAndChildren();
		while (it.hasNext()) {
			UIComponent child = it.next();

			validateComponentTree(context, child);
		}
	}

	protected void validateComponent(final ValidationContext context, final UIComponent component) {

		for (ComponentValidator validator : componentValidators) {
			if (validator.supports(component)) {
				validator.validate(context, component);
			}
		}
	}

	protected List<ValidatorError> checkParameters(final ValidationContext context) {

		List<ValidatorError> errors = new ArrayList<ValidatorError>();

		for (String param : context.getRequestParameters().keySet()) {

			String value = context.getRequestParameters().get(param);
			Map<String, Set<Object>> validParameters = context.getValidParameters();

			boolean paramIsPressent = validParameters.keySet().contains(param);
			boolean paramValuePressent = false;
			if (paramIsPressent) {
				if (validParameters.get(param).contains(value)) {
					paramValuePressent = true;
				}
			}

			if (!paramIsPressent || !paramValuePressent) {

				if (!isExcludedParameter(context, param, value)) {

					if (log.isDebugEnabled()) {
						if (!paramIsPressent) {
							log.debug("Invalid parameter name: " + param);
						}
						else if (!paramValuePressent) {
							log.debug("Invalid parameter value for parameter: " + param + ". Valid values are: "
									+ validParameters.get(param));
						}
					}

					String type = paramIsPressent ? HDIVErrorCodes.PARAMETER_VALUE_INCORRECT : HDIVErrorCodes.PARAMETER_NOT_EXISTS;
					ValidatorError error = new ValidatorError(type, null, param, value);
					errors.add(error);
				}
			}
		}
		return errors;
	}

	protected boolean isExcludedParameter(final ValidationContext context, final String paramName, final String paramValue) {

		if (UtilsJsf.isFacesViewParamName(paramName)) {
			return true;
		}

		// TODO check startParameters and paramsWithoutValidation

		return false;
	}

	/**
	 * Searches the form inside the component. Input component must be UICommand type and must be inside a form.
	 * 
	 * @param comp Base component
	 * @return UIForm component
	 */
	protected UIForm findSubmittedForm(final FacesContext facesContext, final UIComponent comp) {

		if (comp instanceof UIForm) {
			UIForm form = (UIForm) comp;
			String clientId = form.getClientId();
			String paramValue = facesContext.getExternalContext().getRequestParameterMap().get(clientId);
			if (paramValue != null && paramValue.equals(clientId)) {
				return form;
			}
		}
		for (UIComponent child : comp.getChildren()) {
			UIForm form = findSubmittedForm(facesContext, child);
			if (form != null) {
				return form;
			}
		}
		return null;
	}

	/**
	 * Searches the form inside the component. Input component must be UICommand type and must be inside a form.
	 * 
	 * @param comp Base component
	 * @return UIForm component
	 */
	protected UIForm findParentSubmittedForm(final FacesContext facesContext, final UIComponent comp) {

		if (comp == null || comp instanceof UIViewRoot) {
			return null;
		}

		if (comp instanceof UIForm) {
			UIForm form = (UIForm) comp;
			String clientId = form.getClientId();
			String paramValue = facesContext.getExternalContext().getRequestParameterMap().get(clientId);
			if (paramValue != null && paramValue.equals(clientId)) {
				return form;
			}
		}

		return findParentSubmittedForm(facesContext, comp.getParent());
	}

	public void setConfig(final HDIVConfig config) {
		this.config = config;
	}

}
