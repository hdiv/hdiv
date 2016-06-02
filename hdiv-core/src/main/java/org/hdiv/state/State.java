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

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hdiv.exception.HDIVException;
import org.hdiv.util.Constants;
import org.hdiv.util.Method;

/**
 * Data structure to store all data related with one request (parameters, parameter values, ...)
 *
 * @author Roberto Velasco
 */
public class State implements IState, Serializable {

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized
	 * object.
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
	 * HTTP method for this state.
	 * <p>
	 * Null value is equivalent to GET.
	 */
	private Method method;

	private transient IPage page;

	public State() {
	}

	public State(final int id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.hdiv.state.IState#getParameters()
	 */
	public List<IParameter> getParameters() {
		return parameters;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.hdiv.state.IState#addParameter(org.hdiv.state.IParameter)
	 */
	public void addParameter(final IParameter parameter) {
		if (parameters == null) {
			parameters = new ArrayList<IParameter>(PARAMETERS_LIST_SIZE);
		}
		parameters.add(parameter);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.hdiv.state.IState#getParameter(java.lang.String)
	 */
	public IParameter getParameter(final String key) {
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
		return action;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.hdiv.state.IState#setAction(java.lang.String)
	 */
	public void setAction(final String action) {
		this.action = action;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.hdiv.state.IState#getParams()
	 */
	public String getParams() {
		if (params == null) {
			return null;
		}

		try {
			return new String(params, Constants.ENCODING_UTF_8);
		}
		catch (final UnsupportedEncodingException e) {
			throw new HDIVException("Error converting parameters to String", e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.hdiv.state.IState#setParams(java.lang.String)
	 */
	public void setParams(final String params) {
		try {
			if (params != null) {
				this.params = params.getBytes(Constants.ENCODING_UTF_8);
			}
			else {
				this.params = null;
			}
		}
		catch (UnsupportedEncodingException e) {
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

	/**
	 * Required parameters to be able to do a correct request with this state. We consider required parameters all of the parameters that
	 * can be sent via GET or those that are added to the name of an action.
	 */
	public List<String> getRequiredParams() {
		if (parameters == null) {
			return Collections.emptyList();
		}
		else {
			List<String> requiredParams = new ArrayList<String>(parameters.size());
			for (IParameter parameter : parameters) {
				if (parameter.isActionParam()) {
					requiredParams.add(parameter.getName());
				}
			}
			return requiredParams;
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.hdiv.state.IState#getPageId()
	 */
	public int getPageId() {
		return pageId;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.hdiv.state.IState#setPageId(int)
	 */
	public void setPageId(final int pageId) {
		this.pageId = pageId;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.hdiv.state.IState#getMethod()
	 */
	public final Method getMethod() {
		return method != null ? method : Method.GET;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.hdiv.state.IState#setMethod(java.lang.String)
	 */
	public void setMethod(final Method method) {
		this.method = method;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.hdiv.state.IState#existParameter(java.lang.String)
	 */
	public boolean existParameter(final String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(32);
		sb.append("id: ").append(id);
		sb.append("action: ").append(action);
		sb.append("parameters: ").append(parameters);
		sb.append("params: ").append(params);
		sb.append("requiredParams: ").append(getRequiredParams());
		sb.append("method: ").append(method == null ? Method.GET : method);
		return super.toString();
	}

	public IPage getPage() {
		IPage temp = page;
		page = null;
		return temp;
	}

	public void setPage(final IPage page) {
		this.page = page;
	}

}
