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

	/**
	 * Default size of the parameter list.
	 */
	private static final int PARAMETERS_LIST_SIZE = 3;

	/**
	 * Name of the action related with the state <code>this</code>
	 */
	private String action;

	/**
	 * State url parameters in UTF-8. Used for links and action attribute of forms, null otherwise.
	 */
	private byte[] params;

	/**
	 * Contains all fields of a form if the state contains the data of a form.
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
	 * Required parameters to be able to do a correct request with this state. We consider required parameters all of
	 * the parameters that can be sent via GET or those that are added to the name of an action.
	 */
	private List<String> requiredParams;

	/**
	 * HTTP method for this state.
	 * <p>
	 * Null value is equivalent to GET.
	 */
	private String method;

	public State(int id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.state.IState#getParameters()
	 */
	public List<IParameter> getParameters() {
		return this.parameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.state.IState#addParameter(org.hdiv.state.IParameter)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.state.IState#getParameter(java.lang.String)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.state.IState#getAction()
	 */
	public String getAction() {
		return this.action;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.state.IState#setAction(java.lang.String)
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.state.IState#getParams()
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.state.IState#setParams(java.lang.String)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.state.IState#getId()
	 */
	public int getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.state.IState#getRequiredParams()
	 */
	@SuppressWarnings("unchecked")
	public List<String> getRequiredParams() {
		if (!parametersInitialized) {
			return Collections.EMPTY_LIST;
		}

		return requiredParams;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.state.IState#getPageId()
	 */
	public int getPageId() {
		return this.pageId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.state.IState#setPageId(int)
	 */
	public void setPageId(int pageId) {
		this.pageId = pageId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.state.IState#getMethod()
	 */
	public String getMethod() {
		if (this.method == null) {
			return "GET";
		}
		return method;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.state.IState#setMethod(java.lang.String)
	 */
	public void setMethod(String method) {
		if (method == null) {
			this.method = method;
		}
		if (method.equalsIgnoreCase("GET")) {
			this.method = null;
		}
		this.method = method.toUpperCase();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.state.IState#existParameter(java.lang.String)
	 */
	public boolean existParameter(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id: ").append(this.id);
		sb.append("action: ").append(this.action);
		sb.append("parameters: ").append(this.parameters);
		sb.append("params: ").append(this.params);
		sb.append("requiredParams: ").append(this.requiredParams);
		sb.append("method: ").append(this.method == null ? "GET" : this.method);
		return super.toString();
	}

}
