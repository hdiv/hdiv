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

import java.util.Collection;
import java.util.List;

import org.hdiv.util.Method;

public interface IState {

	/**
	 * Adds a new parameter to the state <code>this</code>. If it is a required parameter <code>parameter</code>, it is also added to the
	 * required parameters.
	 *
	 * @param parameter The parameter
	 */
	void addParameter(IParameter parameter);

	/**
	 * Returns the parameter that matches the given identifier <code>key</code>. Null is returned if the parameter name is not found.
	 *
	 * @param key parameter identifier
	 * @return IParameter object that matches the given identifier <code>key</code>.
	 */
	IParameter getParameter(String key);

	/**
	 * Returns all the parameters of the IState.
	 *
	 * @return List of {@link IParameter}
	 */
	Collection<IParameter> getParameters();

	/**
	 * @return Returns the action associated to state <code>this</code>.
	 */
	String getAction();

	/**
	 * @param action The action to set.
	 */
	void setAction(String action);

	/**
	 * @return Returns the id.
	 */
	int getId();

	/**
	 * Checks if exists a parameter with the given identifier <code>key</code>.
	 *
	 * @param key parameter identifier
	 * @return True if exists a parameter with this identifier <code>key</code>. False otherwise.
	 */
	boolean existParameter(String key);

	/**
	 * @return Returns required parameters.
	 */
	List<String> getRequiredParams();

	/**
	 * @return IState parameters in one String.
	 */
	String getParams();

	/**
	 * @param params IState parameters in one String.
	 */
	void setParams(String params);

	/**
	 * @param method HTTP method for this request
	 */
	void setMethod(Method method);

	/**
	 * Returns true if a method is valid for this state
	 * @param method
	 * @return
	 */
	boolean contains(Method method);

	/**
	 * Returns if two states are equivalent
	 * @param state
	 * @return
	 */
	boolean isEquivalent(IState state);

	/**
	 * Returns the type of random token
	 * @return
	 */
	RandomTokenType getTokenType();
	
	IPage getPage();

	void setPage(IPage currentPage);

}