/**
 * Copyright 2005-2012 hdiv.org
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIData;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.util.ConstantsJsf;
import org.hdiv.util.UtilsJsf;

/**
 * HtmlInputHidden component extension
 * 
 * @author Gotzon Illarramendi
 */
public class HtmlInputHiddenExtension extends HtmlInputHidden {

	private static Log log = LogFactory.getLog(HtmlInputHiddenExtension.class);

	/**
	 * Obtains hidden real value which has been stored in the JSF state
	 * 
	 * @return hidden real value
	 */
	@SuppressWarnings("unchecked")
	public Object getRealValue(String clientId) {
		Map<String, Object> values = (Map<String, Object>) this.getAttributes().get(ConstantsJsf.HDIV_ATTRIBUTE_KEY);
		return values.get(clientId);
	}

	/**
	 * Returns component's client id for the row passed as a parameter
	 * 
	 * @param rowIndex
	 *            row index in UIData
	 * @return component id
	 */
	@SuppressWarnings("unchecked")
	public String getRequestId(int rowIndex) {
		List<String> clientIds = (List<String>) this.getAttributes().get(ConstantsJsf.HDIV_ATTRIBUTE_CLIENTIDS_KEY);
		return clientIds.get(rowIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.UIComponent#encodeBegin(javax.faces.context. FacesContext)
	 */
	@SuppressWarnings("unchecked")
	public void encodeBegin(FacesContext context) throws IOException {

		Map<String, Object> values = null;
		List<String> clientIds = null;
		UIData tableComp = UtilsJsf.findParentUIData(this);
		String clientId = this.getClientId(context);
		if (tableComp != null) {
			values = (Map<String, Object>) this.getAttributes().get(ConstantsJsf.HDIV_ATTRIBUTE_KEY);
			clientIds = (List<String>) this.getAttributes().get(ConstantsJsf.HDIV_ATTRIBUTE_CLIENTIDS_KEY);
		}
		if (values == null) {
			values = new HashMap<String, Object>();
		}
		if (clientIds == null) {
			clientIds = new ArrayList<String>();
		}
		values.put(clientId, super.getValue());
		clientIds.add(clientId);
		this.getAttributes().put(ConstantsJsf.HDIV_ATTRIBUTE_KEY, values);
		this.getAttributes().put(ConstantsJsf.HDIV_ATTRIBUTE_CLIENTIDS_KEY, clientIds);

		if (log.isDebugEnabled()) {
			log.debug("Hidden real value :" + values.get(clientId));
		}

		super.encodeBegin(context);
	}

	/**
	 * Returns list of component's client id
	 * 
	 * @return list of ids
	 */
	@SuppressWarnings("unchecked")
	public List<String> getClientIds() {
		return (List<String>) this.getAttributes().get(ConstantsJsf.HDIV_ATTRIBUTE_CLIENTIDS_KEY);
	}

}
