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
package org.hdiv.components;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.hdiv.state.StateManager;
import org.hdiv.util.ConstantsJsf;
import org.hdiv.util.HDIVUtil;

/**
 * <p>
 * UIParameter component extension.
 * </p>
 * <p>
 * This component is used to define the parameters of CommandLink and OutputLink. It stores the real values as component's attributes, in
 * the JSF state.
 * </p>
 * <p>
 * Next request will be validated against the stored data.
 * </p>
 * @author Gotzon Illarramendi
 */
public class UIParameterExtension extends UIParameter {

	/**
	 * Returns the value of the parameter for the requested row in the dataTable
	 * 
	 * @param parentClientId Parent ClientId
	 * @return parameter value
	 */
	public Object getValue(final FacesContext context, final String parentClientId) {

		Object val = this.getValue();

		// If it has previously been stored in the state, return the stored value else return the default
		StateManager stateManager = getStateManager(context);
		if (stateManager != null) {
			List<Object> values = stateManager.restoreState(parentClientId + UINamingContainer.getSeparatorChar(context) + getName());

			if (values != null && values.size() > 0) {
				val = values.get(0);
			}
		}

		return val;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.UIComponent#encodeBegin(javax.faces.context. FacesContext)
	 */
	@Override
	public void encodeBegin(final FacesContext context) throws IOException {

		// HDIV parameter name
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		String hdivParameter = HDIVUtil.getRequestContext(request).getHdivParameterName();

		String name = getName();
		if (name != null && name.equals(hdivParameter)) {
			// It is the Hdiv parameter added automatically by the link, so do nothing
		}
		else {
			UIComponent parent = getParent();
			String parentClientId = parent.getClientId(context);

			// It is a parameter added by the application, so store its value in the JSF state to be able to validate it in future requests.
			Object val = this.getValue();

			StateManager stateManager = getStateManager(context);
			if (stateManager != null) {
				stateManager.saveState(parentClientId + UINamingContainer.getSeparatorChar(context) + name, val);
			}
		}
		super.encodeBegin(context);
	}

	protected StateManager getStateManager(final FacesContext context) {
		StateManager stateManager = (StateManager) context.getExternalContext().getRequestMap()
				.get(ConstantsJsf.HDIV_STATE_MANAGER_ATTRIBUTE_KEY);
		return stateManager;
	}

}