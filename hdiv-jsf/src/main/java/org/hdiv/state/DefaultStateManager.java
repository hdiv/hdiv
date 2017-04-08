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
package org.hdiv.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.hdiv.util.ConstantsJsf;

public class DefaultStateManager implements StateManager {

	protected final FacesContext context;

	public DefaultStateManager(final FacesContext context) {
		this.context = context;
	}

	public void saveState(final String componentid, final Object value) {

		Map<String, List<Object>> stateHolder = getStateHolder();
		List<Object> values = stateHolder.get(componentid);
		if (values == null) {
			values = new ArrayList<Object>();
			stateHolder.put(componentid, values);
		}
		values.add(value);
	}

	public List<Object> restoreState(final String componentId) {
		Map<String, List<Object>> stateHolder = getStateHolder();
		return stateHolder.get(componentId);
	}

	protected Map<String, List<Object>> getStateHolder() {

		@SuppressWarnings("unchecked")
		Map<String, List<Object>> stateHolder = (Map<String, List<Object>>) context.getViewRoot().getAttributes()
				.get(ConstantsJsf.HDIV_STATE_HOLDER_ATTRIBUTE_KEY);
		if (stateHolder == null) {
			stateHolder = new HashMap<String, List<Object>>();
			context.getViewRoot().getAttributes().put(ConstantsJsf.HDIV_STATE_HOLDER_ATTRIBUTE_KEY, stateHolder);
		}
		return stateHolder;
	}

}
