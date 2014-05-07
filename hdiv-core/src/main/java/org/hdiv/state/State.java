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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hdiv.exception.HDIVException;
import org.hdiv.util.Constants;

/**
 * Data structure to store all data related with one request (parameters, parameter values, ...)
 * 
 * @author Roberto Velasco
 */
public class State implements IState, Serializable {

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that a loaded class corresponds exactly
	 * to a serialized object.
	 */
	private static final long serialVersionUID = -5179573248448214135L;

	private static final int PARAMETERS_LIST_SIZE = 3;

	/**
	 * Name of the action related with the state <code>this</code>
	 */
	private String action;

	/**
	 * State url parameters in UTF-8
	 */
	private byte[] params;

	/**
	 * Map to store all the parameters in a HTTP (GET or POST) request
	 */
	private List<IParameter> parameters;

	/**
	 * State identifier <code>this</code>
	 */
	private int id;

	/**
	 * Page identifier which the state <code>this</code> belongs to
	 */
	private int pageId;

	/**
	 * Flag to initialize the lists
	 */
	private boolean parametersInitialized = false;

	/**
	 * Map with the required parameters to be able to do a correct request with state <code>this</code>. We consider
	 * required parameters all of the parameters that can be sent via GET or those that are added to the name of an
	 * action.
	 */
	private List<String> requiredParams;

	public State(int id) {
		this.id = id;
	}

	public List<IParameter> getParameters() {
		return this.parameters;
	}

	/**
	 * Adds a new parameter to the state <code>this</code>. If it is a required parameter <code>parameter</code>, it is
	 * also added to the required parameters map.
	 * 
	 * @param parameter
	 *            The parameter
	 */
	public void addParameter(IParameter parameter) {
		if (!parametersInitialized) {
			parametersInitialized = true;
			this.parameters = new ArrayList<IParameter>(PARAMETERS_LIST_SIZE);
			this.requiredParams = new ArrayList<String>(PARAMETERS_LIST_SIZE);
		}

		if (parameter.isActionParam()) {
			this.requiredParams.add(parameter.getName());
		}

		this.parameters.add(parameter);
	}

	/**
	 * Returns the parameter that matches the given identifier <code>key</code>. Null is returned if the parameter name
	 * is not found.
	 * 
	 * @param key
	 *            parameter identifier
	 * @return IParameter object that matches the given identifier <code>key</code>.
	 */
	public IParameter getParameter(String key) {
		if (parameters != null) {
			for (IParameter parameter : parameters) {
				if (parameter.getName().equalsIgnoreCase(key)) {
					return parameter;
				}
			}
		}

		return null;
	}

	/**
	 * @return Returns the action asociated to state <code>this</code>.
	 */
	public String getAction() {
		return this.action;
	}

	/**
	 * @param action
	 *            The action to set.
	 */
	public void setAction(String action) {
		this.action = action;
	}

	public String getParams() {
		if (this.params == null) {
			return null;
		}

		try {
			return new String(params, Constants.ENCODING_UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new HDIVException("Error converting parameters to String", e);
		}
	}

	public void setParams(String params) {
		try {
			if (params != null) {
				this.params = params.getBytes(Constants.ENCODING_UTF_8);
			} else {
				this.params = null;
			}
		} catch (UnsupportedEncodingException e) {
			throw new HDIVException("Error converting action to byte array", e);
		}
	}

	/**
	 * @return Returns the <code>this</code> id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return Returns required parameters map.
	 */
	@SuppressWarnings("unchecked")
	public List<String> getRequiredParams() {
		if (!parametersInitialized) {
			return Collections.EMPTY_LIST;
		}

		return requiredParams;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id: ").append(this.id);
		sb.append("action: ").append(this.action);
		sb.append("parameters: ").append(this.parameters);
		sb.append("requiredParams: ").append(this.requiredParams);
		return super.toString();
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof IState) {

			IState state = (IState) obj;

			// Same action
			if (!(this.getAction().equals(state.getAction()))) {
				return false;
			}

			// Same Parameters
			Collection<IParameter> otherParams = state.getParameters();
			if (otherParams != null && this.parameters != null) {
				if (otherParams.size() != this.parameters.size()) {
					return false;
				}
				for (IParameter param : this.parameters) {

					if (!otherParams.contains(param)) {
						return false;
					}
				}
			}

			// Same required Parameters
			List<String> otherRequiredParams = state.getRequiredParams();
			if (otherRequiredParams != null && this.requiredParams != null) {
				if (otherRequiredParams.size() != this.requiredParams.size()) {
					return false;
				}
				for (String requiredParam : this.requiredParams) {
					if (!otherRequiredParams.contains(requiredParam)) {
						return false;
					}
				}
			}

			return true;
		}
		return false;
	}

	public int getPageId() {
		return this.pageId;
	}

	public void setPageId(int pageId) {
		this.pageId = pageId;
	}

	public boolean existParameter(String key) {
		throw new UnsupportedOperationException();
	}

}
