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

import java.util.Collection;
import java.util.List;

public interface IState {

	/**
	 * Adds a new parameter to the state <code>this</code>. If it is a required parameter <code>parameter</code>, it is
	 * also added to the required parameters.
	 * 
	 * @param parameter
	 *            The parameter
	 */
	public void addParameter(IParameter parameter);

	/**
	 * Returns the parameter that matches the given identifier <code>key</code>. Null is returned if the parameter name
	 * is not found.
	 * 
	 * @param key
	 *            parameter identifier
	 * @return IParameter object that matches the given identifier <code>key</code>.
	 */
	public IParameter getParameter(String key);

	/**
	 * Returns all the parameters of the IState.
	 * 
	 * @return List of {@link IParameter}
	 */
	public Collection<IParameter> getParameters();

	/**
	 * @return Returns the action associated to state <code>this</code>.
	 */
	public String getAction();

	/**
	 * @param action
	 *            The action to set.
	 */
	public void setAction(String action);

	/**
	 * @return Returns the id.
	 */
	public int getId();

	/**
	 * @return Returns the page identifier which the state <code>this</code> belongs to.
	 */
	public int getPageId();

	/**
	 * @param pageId
	 *            The pageId to set.
	 */
	public void setPageId(int pageId);

	/**
	 * Checks if exists a parameter with the given identifier <code>key</code>.
	 * 
	 * @param key
	 *            parameter identifier
	 * @return True if exists a parameter with this identifier <code>key</code>. False otherwise.
	 */
	public boolean existParameter(String key);

	/**
	 * @return Returns required parameters.
	 */
	public List<String> getRequiredParams();

	/**
	 * @return IState parameters in one String.
	 */
	public String getParams();

	/**
	 * @param params
	 *            IState parameters in one String.
	 */
	public void setParams(String params);

	/**
	 * @return HTTP method
	 */
	public String getMethod();

	/**
	 * @param method
	 *            HTTP method for this request
	 */
	public void setMethod(String method);

}