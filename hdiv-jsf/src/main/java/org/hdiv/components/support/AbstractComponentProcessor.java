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
package org.hdiv.components.support;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.hdiv.config.HDIVConfig;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;

public abstract class AbstractComponentProcessor {

	protected HDIVConfig hdivConfig;

	protected LinkUrlProcessor urlProcessor;

	protected void init(FacesContext context) {

		if (this.hdivConfig == null) {
			ExternalContext externalContext = context.getExternalContext();
			HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
			ServletContext servletContext = request.getSession().getServletContext();

			this.hdivConfig = HDIVUtil.getHDIVConfig(servletContext);

			if (this.urlProcessor == null) {
				this.urlProcessor = HDIVUtil.getLinkUrlProcessor(servletContext);
			}
		}

	}

	public void removeHdivStateUIParameter(FacesContext context, UIComponent component) {

		// Remove the component with the HDIV state, we don't want to store it in the state
		String hdivParameter = (String) context.getExternalContext().getSessionMap().get(Constants.HDIV_PARAMETER);

		// First we add to a list the components to remove
		// The list used by MyFaces has a problem with the iterator
		List<Integer> toRemoveList = new ArrayList<Integer>();
		for (UIComponent comp : component.getChildren()) {
			if (comp instanceof UIParameter) {
				UIParameter param = (UIParameter) comp;
				String name = param.getName();
				if (name != null && name.equals(hdivParameter)) {
					Integer index = component.getChildren().indexOf(param);
					toRemoveList.add(index);
				}
			}
		}
		// Remove the ones founded before
		for (Integer index : toRemoveList) {
			component.getChildren().remove(index.intValue());
		}
	}

}
