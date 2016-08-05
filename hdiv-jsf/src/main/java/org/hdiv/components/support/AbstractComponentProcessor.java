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
package org.hdiv.components.support;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.hdiv.config.HDIVConfig;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.util.HDIVUtil;

public abstract class AbstractComponentProcessor {

	protected HDIVConfig config;

	protected LinkUrlProcessor linkUrlProcessor;

	public void removeHdivStateUIParameter(final FacesContext context, final UIComponent component) {

		// Remove the component with the HDIV state, we don't want to store it in the state
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		String hdivParameter = HDIVUtil.getHdivStateParameterName(request);

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

	/**
	 * @param config the config to set
	 */
	public void setConfig(final HDIVConfig config) {
		this.config = config;
	}

	/**
	 * @param linkUrlProcessor the linkUrlProcessor to set
	 */
	public void setLinkUrlProcessor(final LinkUrlProcessor linkUrlProcessor) {
		this.linkUrlProcessor = linkUrlProcessor;
	}

}
