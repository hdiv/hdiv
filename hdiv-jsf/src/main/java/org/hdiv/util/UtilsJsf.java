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
package org.hdiv.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIParameter;
import javax.faces.component.UIViewRoot;

/**
 * General HDIV utility methods
 * 
 * @author Gotzon Illarramendi
 */
public abstract class UtilsJsf {

	/**
	 * Checks if any of the names of the received parameters contains the ViewState.
	 * 
	 * @param paramNameSet parameter name group
	 * @return boolean
	 */
	public static boolean hasFacesViewParamName(final Set<String> paramNameSet) {
		String[] params = UtilsJsf.getFacesViewParamNames();

		for (int i = 0; i < params.length; i++) {
			if (paramNameSet.contains(params[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the parameter name contains the ViewState
	 * 
	 * @param paramName parameter name
	 * @return boolean
	 */
	public static boolean isFacesViewParamName(final String paramName) {
		String[] params = UtilsJsf.getFacesViewParamNames();

		for (int i = 0; i < params.length; i++) {
			if (paramName.equals(params[i])) {
				return true;
			}
		}
		return false;

	}

	/**
	 * Returns the names of the parameters that contain the ViewState
	 * 
	 * @return Param names
	 */
	public static String[] getFacesViewParamNames() {

		return ConstantsJsf.FACES_VIEWSTATE_PARAMNAMES;

	}

	/**
	 * Returns the list of parameters added by JSF implementations.
	 * 
	 * @param submitedFormClientId client id of the form sent in the last request.
	 * @return list of parameter names
	 */
	public static List<String> getJSFImplementationParamNames(final String submitedFormClientId) {

		List<String> list = new ArrayList<String>();

		// Adds parameter that indicates which component executed the event of MyFaces
		list.add(submitedFormClientId + ":_idcl");
		// SUN RI
		list.add(submitedFormClientId + ":_idcl");// Version 1.1
		list.add(submitedFormClientId + ":j_idcl");// Version 1.2 < 07

		// Rest of the parameters added by each implementation
		// Sun RI doesn't add any parameter
		// MyFaces added some extra parameters
		list.add(submitedFormClientId + "_SUBMIT");
		list.add(submitedFormClientId + ":_link_hidden_");

		return list;
	}

	/**
	 * Removes row identifier from client id. Converts: PageC:form2:tableid:0:link1 into: PageC:form2:tableid:link1
	 * 
	 * @param clientId ClientID
	 * @return clientId without row id
	 */
	public static String removeRowId(final String clientId) {
		if (clientId == null) {
			return null;
		}
		return clientId.replaceAll(":\\d*:", ":");
	}

	/**
	 * Searches in the parent components of comp if exists one of type UIData. Returns null if not
	 * 
	 * @param comp componente en el que se basa la busqueda
	 * @return componente UIData pariente o null si no existe
	 */
	public static UIData findParentUIData(final UIComponent comp) {

		UIComponent parent = comp.getParent();
		while (!(parent instanceof UIData)) {
			if (parent instanceof UIViewRoot) {
				return null;
			}
			parent = parent.getParent();
		}
		return (UIData) parent;

	}

	/**
	 * Search component children for UIParameter components.
	 * 
	 * @param component {@link UIComponent} component
	 * @return if component has children
	 */
	public static boolean hasUIParameterChild(final UIComponent component) {

		boolean hasParams = false;
		for (UIComponent comp : component.getChildren()) {
			if (comp instanceof UIParameter) {
				hasParams = true;
				break;
			}
		}
		return hasParams;
	}

}
