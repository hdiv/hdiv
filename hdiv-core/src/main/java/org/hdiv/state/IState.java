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

import java.util.Map;

public interface IState {

	/**
	 * @return Returns the parameters
	 */
	public Map<String, IParameter> getParameters();

	/**
	 * Adds a new parameter to the state <code>this</code>. If it is a required parameter
	 * <code>parameter</code>, it is also added to the required parameters map.
	 *
	 * @param key new parameter identifier
	 * @param parameter The parameter
	 */
	public void addParameter(String key, IParameter parameter);

	/**
	 * Returns the parameter that matches the given identifier <code>key</code>.
	 *
	 * @param key parameter identifier
	 * @return IParameter object that matches the given identifier <code>key</code>.
	 */
	public IParameter getParameter(String key);

	/**
	 * @return Returns the action asociated to state <code>this</code>.
	 */
	public String getAction();

	/**
	 * @param action The action to set.
	 */
	public void setAction(String action);

	/**
	 * @return Returns the id.
	 */
	public String getId();

	/**
	 * @return Returns the page identifier which the state <code>this</code> belongs to.
	 */
	public String getPageId();

	/**
	 * @param pageId The pageId to set.
	 */
	public void setPageId(String pageId);

	/**
	 * @param id The id to set.
	 */
	public void setId(String id);

	/**
	 * Checks if exists a parameter with the given identifier <code>key</code>.
	 *
	 * @param key parameter identifier
	 * @return True if exists a parameter with this identifier <code>key</code>. False
	 *         otherwise.
	 */
	public boolean existParameter(String key);

	/**
	 * @return Returns required parameters map.
	 */
	public Map<String, IParameter> getRequiredParams();
	
}
