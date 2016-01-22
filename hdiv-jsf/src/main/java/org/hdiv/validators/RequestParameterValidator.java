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
package org.hdiv.validators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.util.UtilsJsf;
import org.hdiv.validation.ValidationError;

/**
 * Validates that all parameters received in a request are valid and that no extra parameters has been added
 * 
 * @author Gotzon Illarramendi
 */
public class RequestParameterValidator implements ComponentValidator {

	private static Log log = LogFactory.getLog(RequestParameterValidator.class);

	/**
	 * HDIV configuration
	 */
	private HDIVConfig hdivConfig;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.validators.ComponentValidator#validate(javax.faces.context.FacesContext,
	 * javax.faces.component.UIComponent)
	 */
	public ValidationError validate(FacesContext context, UIComponent component) {

		UIForm form = (UIForm) component;
		ValidationError error = this.validateRequestParameters(context, form);

		return error;
	}

	/**
	 * Verifies that all the received parameters correspond to an attribute of the form that has been sent
	 * 
	 * @param context Request context
	 * @param formComponent for component to validate
	 * @return validation result
	 */
	private ValidationError validateRequestParameters(FacesContext context, UIForm formComponent) {

		List<String> clientIds = this.getClientIds(context, formComponent);

		boolean validParameter = true;
		boolean validParameters = true;

		ValidationError error = null;

		Map<String, String> requestParameters = context.getExternalContext().getRequestParameterMap();
		for (Entry<String, String> entry : requestParameters.entrySet()) {
			String requestParamName = entry.getKey().toString().trim();
			if (UtilsJsf.isFacesViewParamName(requestParamName)) {
				continue;
			}

			// Row identifier is removed from parameter name if it is inside
			// a datatable
			String requestParamNameWithRowId = requestParamName;
			requestParamName = UtilsJsf.removeRowId(requestParamName);

			// In MyFaces, some clientId of tables contain a rowId
			validParameter = ((clientIds.contains(requestParamName)) || (clientIds.contains(requestParamNameWithRowId)));
			if (!validParameter) {

				// It may be a parameter added in the client, for instance
				// using JavaScript.
				// In this case check if it is a userStartParameters

				boolean isStartParam = this.hdivConfig.isStartParameter(requestParamName);
				if (isStartParam) {
					if (log.isDebugEnabled()) {
						log.debug("Parameter '" + requestParamName + "' is a startParameter");
					}
					validParameter = true;
				}
				else {
					// It is not a startParameter, non expected parameter
					// raise error
					validParameter = false;

					Object value = requestParameters.get(requestParamName);
					String paramValue = "";
					if (value != null) {
						paramValue = value.toString();
					}
					error = new ValidationError(HDIVErrorCodes.PARAMETER_NOT_EXISTS, null, requestParamName, paramValue);
				}
			}
			validParameters = validParameters && validParameter;
		}
		return error;
	}

	/**
	 * Stores all the component ids that are children of the form
	 * 
	 * @param context Request context
	 * @param component form component
	 * @return list with client ids
	 */
	private List<String> getClientIds(FacesContext context, UIForm component) {

		List<String> clientIds = new ArrayList<String>();

		String submittedForm = component.getClientId(context);
		clientIds.add(UtilsJsf.removeRowId(submittedForm));
		this.getAllClientIds(context, component, clientIds);

		// Add those parameters that are proprietary for each implementation
		clientIds.addAll(UtilsJsf.getJSFImplementationParamNames(submittedForm));

		return clientIds;
	}

	/**
	 * Adds to 'clientIds' all the component ids susceptible to creating parameters in the request
	 * 
	 * @param context Request context
	 * @param component Component to validate
	 * @param clientIds possible clientId values
	 */
	@SuppressWarnings("rawtypes")
	private void getAllClientIds(FacesContext context, UIComponent component, List<String> clientIds) {

		// In A4J there are components that are not children but facets
		Iterator it = component.getFacetsAndChildren();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof UIComponent) {
				UIComponent uicom = (UIComponent) obj;
				if (uicom instanceof UIParameter) {
					UIParameter parameter = (UIParameter) uicom;
					clientIds.add(parameter.getName());
				}
				else {
					String id = UtilsJsf.removeRowId(uicom.getClientId(context));
					clientIds.add(id);
				}
				getAllClientIds(context, uicom, clientIds);
			}
		}
	}

	public void setHdivConfig(HDIVConfig hdivConfig) {
		this.hdivConfig = hdivConfig;
	}

}
