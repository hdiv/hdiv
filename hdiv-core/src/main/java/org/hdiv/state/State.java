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
package org.hdiv.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data structure to store all data related with one request (parameters, parameter
 * values, ...)
 * 
 * @author Roberto Velasco
 */
public class State implements IState, Serializable {

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that
	 * a loaded class corresponds exactly to a serialized object.
	 */
	private static final long serialVersionUID = -5179573248448214135L;

	/**
	 * Name of the action related with the state <code>this</code>
	 */	
	private String action;

	/**
	 * Map to store all the parameters in a HTTP (GET or POST) request
	 */
	private Map<String, IParameter> parameters = new HashMap<String, IParameter>();

	/**
	 * State identifier <code>this</code>
	 */
	private int id;

	/**
	 * Page identifier which the state <code>this</code> belongs to
	 */
	private String pageId;
	
	/**
	 * List with the required parameters to be able to do a correct request with state
	 * <code>this</code>. We consider required parameters all of the parameters
	 * that can be sent via GET or those that are added to the name of an action.
	 */
	private List<String> requiredParams = new ArrayList<String>();

	public State(int id) {
		this.id = id;
	}

	/**
	 * Adds a new parameter to the state <code>this</code>. If it is a required parameter
	 * <code>parameter</code>, it is also added to the required parameters map.
	 *
	 * @param parameter The parameter
	 */
	public void addParameter(IParameter parameter) {
		
		String paramName = parameter.getName();
		
		if (parameter.isActionParam()) {
			this.requiredParams.add(paramName);
		}
		this.parameters.put(paramName, parameter);
	}

	/**
	 * Checks if exists a parameter with the given identifier <code>key</code>.
	 *
	 * @param key parameter identifier
	 * @return True if exists a parameter with this identifier <code>key</code>. False
	 *         otherwise.
	 */
	public boolean existParameter(String key) {
		return this.parameters.containsKey(key);
	}

	/**
	 * Returns the parameter that matches the given identifier <code>key</code>.
	 *
	 * @param key parameter identifier
	 * @return IParameter object that matches the given identifier <code>key</code>.
	 */
	public IParameter getParameter(String key) {
		return (IParameter) this.parameters.get(key);
	}

	/**
	 * @return Returns the action asociated to state <code>this</code>.
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action The action to set.
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return Returns the parameters asociated to state <code>this</code>.
	 */
	public Collection<IParameter> getParameters() {
		return parameters.values();
	}

	/**
	 * @param parameters The parameters to set.
	 */
	public void setParameters(Map<String, IParameter> parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return Returns the <code>this</code> id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return Returns the page identifier which the state <code>this</code> belongs to.
	 */	
	public String getPageId() {
		return pageId;
	}

	/**
	 * @param pageId The pageId to set.
	 */
	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	/**
	 * @return Returns required parameters map.
	 */
	public List<String> getRequiredParams() {
		return requiredParams;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id: ").append(this.id);
		sb.append("action: ").append(this.action);
		sb.append("parameters: ").append(this.parameters);
		sb.append("requiredParams: ").append(this.requiredParams);
		sb.append("pageId: ").append(this.pageId);
		return super.toString();
	}

}
