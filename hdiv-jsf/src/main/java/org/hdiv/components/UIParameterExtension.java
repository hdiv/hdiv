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
package org.hdiv.components;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;

import org.hdiv.util.Constants;
import org.hdiv.util.ConstantsJsf;
import org.hdiv.util.UtilsJsf;

/**
 * <p>
 * UIParameter component extension
 * </p>
 * <p>
 * This component is used to define the parameters of CommandLink and outputLink. It stores the real values as
 * component's attributes, storing them in the state.
 * </p>
 * <p>
 * This data will be used to validate that it matches the data received in the next request.
 * </p>
 * 
 * @author Gotzon Illarramendi
 * 
 */
public class UIParameterExtension extends UIParameter {

	/**
	 * Returns the value of the parameter for the requested row in the dataTable
	 * 
	 * @param parentClientId Parent ClientId
	 * @return parameter value
	 */
	@SuppressWarnings("unchecked")
	public Object getValue(String parentClientId) {

		Object val = this.getValue();

		// If it has previously been stored in the state, return the stored value
		// else return the default
		Map<String, Object> values = (Map<String, Object>) this.getAttributes().get(ConstantsJsf.HDIV_ATTRIBUTE_KEY);
		if (values != null) {
			val = values.get(parentClientId);
		}
		return val;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.UIComponent#encodeBegin(javax.faces.context. FacesContext)
	 */
	@SuppressWarnings("unchecked")
	public void encodeBegin(FacesContext context) throws IOException {

		// HDIV parameter name
		String hdivParameter = (String) context.getExternalContext().getSessionMap().get(Constants.HDIV_PARAMETER);

		String name = this.getName();
		if (name != null && name.equals(hdivParameter)) {
			// It is the Hdiv parameter added automatically by the link, so do
			// nothing

		}
		else {
			UIComponent parent = this.getParent();
			String parentClientId = parent.getClientId(context);
			Map<String, Object> values = (Map<String, Object>) this.getAttributes()
					.get(ConstantsJsf.HDIV_ATTRIBUTE_KEY);
			if (values == null) {
				values = new HashMap<String, Object>();
			}

			// It is a parameter added by the application, so store its value
			// in the JSF state to be able to validate it in future requests.
			UIData uiDataComp = UtilsJsf.findParentUIData(this);
			if (uiDataComp != null) {

				// The component is in a table, store its value depending on the row
				int rowIndex = uiDataComp.getRowIndex();
				if (rowIndex < 0) {
					rowIndex = 0;
				}
			}
			Object val = this.getValue();
			values.put(parentClientId, val);

			this.getAttributes().put(ConstantsJsf.HDIV_ATTRIBUTE_KEY, values);
		}
		super.encodeBegin(context);
	}

}