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
import java.util.Collection;
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
/**
 * @author anderruiz
 *
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
	 * HTTP method for this state.
	 * <p>
	 * Null value is equivalent to GET.
	 */
	private Method method;

	/**
	 * Type of token to be used with this state
	 */
	private RandomTokenType tokenType = RandomTokenType.LINK;

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
	 * Required parameters to be able to do a correct request with this state. We consider required parameters all of
	 * the parameters that can be sent via GET or those that are added to the name of an action.
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

	public boolean contains(final Method method) {
		return getMethod().equals(method);
	}

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
		if (method.isForm) {
			tokenType = RandomTokenType.FORM;
		}
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

	public boolean isEquivalent(final IState state) {
		// Same action
		if (!(getAction().equals(state.getAction()))) {
			return false;
		}

		// Same method
		if (!(getMethod().equals(((State) state).getMethod()))) {
			return false;
		}

		// Same Parameters
		Collection<IParameter> params1 = getParameters();
		Collection<IParameter> params2 = state.getParameters();
		if (params1 != null && params2 == null) {
			return false;
		}
		else if (params1 == null && params2 != null) {
			return false;
		}
		else if (params1 != null && params2 != null) {
			if (params1.size() != params2.size()) {
				return false;
			}
			for (IParameter param1 : params1) {

				boolean exist = false;
				for (IParameter param2 : params2) {
					if (areEqualParameters(param1, param2)) {
						exist = true;
					}
				}
				if (!exist) {
					return false;
				}
			}
		}

		String parameters1 = getParams();
		String parameters2 = state.getParams();
		if (parameters1 != null && parameters2 == null) {
			return false;
		}
		else if (parameters1 == null && parameters2 != null) {
			return false;
		}
		else if (parameters1 != null && parameters2 != null) {
			if (!parameters1.equals(parameters2)) {
				return false;
			}
		}

		// Same required Parameters
		List<String> requiredParams1 = getRequiredParams();
		List<String> requiredParams2 = state.getRequiredParams();
		if (requiredParams1 != null && requiredParams2 == null) {
			return false;
		}
		else if (requiredParams1 == null && requiredParams2 != null) {
			return false;
		}
		else if (requiredParams1 != null && requiredParams2 != null) {
			if (requiredParams1.size() != requiredParams2.size()) {
				return false;
			}
			for (String requiredParam : requiredParams1) {
				if (!requiredParams2.contains(requiredParam)) {
					return false;
				}
			}
		}

		return true;
	}

	protected boolean areEqualParameters(final IParameter param1, final IParameter param2) {

		if (!param1.getName().equals(param2.getName())) {
			return false;
		}
		if (param1.isActionParam() != param2.isActionParam()) {
			return false;
		}
		if (param1.isEditable() != param2.isEditable()) {
			return false;
		}
		List<String> values = param2.getValues();
		if (values.size() != param1.getValues().size()) {
			return false;
		}
		for (String paramValue : values) {
			if (!param1.existValue(paramValue)) {
				return false;
			}
		}
		return true;
	}

	public RandomTokenType getTokenType() {
		return tokenType;
	}

	protected void setTokenType(final RandomTokenType tokenType) {
		this.tokenType = tokenType;
	}

}
