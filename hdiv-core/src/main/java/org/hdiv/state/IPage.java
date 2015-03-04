/**
 * Copyright 2005-2015 hdiv.org
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

public interface IPage {

	/**
	 * Adds a new state to the page <code>this</code>.
	 * 
	 * @param state
	 *            State that represents all the data that composes a possible request.
	 */
	public void addState(IState state);

	/**
	 * Adds a new state hash to the page <code>this</code>.
	 * 
	 * @param id
	 *            state identifier
	 * @param state
	 *            Hash of a state that represents all the data that composes a possible request.
	 */
	public void addState(int id, String state);

	/**
	 * Checks if exists a state with the given identifier <code>key</code>.
	 * 
	 * @param id
	 *            State identifier
	 * @return true if exist
	 */
	public boolean existState(int id);

	/**
	 * Returns the state with the given identifier <code>key</code> from the map of states
	 * 
	 * @param id
	 *            State identifier
	 * @return IState State with the identifier <code>key</code>.
	 */
	public IState getState(int id);

	/**
	 * Returns the state hash with the given identifier <code>key</code> from the map of states
	 * 
	 * @param id
	 *            State identifier
	 * @return String hash with the identifier <code>key</code>.
	 */
	public String getStateHash(int id);

	/**
	 * @return Returns the page name.
	 */
	public String getName();

	/**
	 * @return Returns the page id.
	 */
	public int getId();

	/**
	 * @param id
	 *            The page id to set.
	 */
	public void setId(int id);

	/**
	 * @return Returns the page states.
	 */
	public Collection<? extends Object> getStates();

	/**
	 * @return number of states.
	 */
	public int getStatesCount();

	/**
	 * Returns the unique id of flow.
	 * 
	 * @return the flow id
	 */
	public String getFlowId();

	/**
	 * @param flowId
	 *            the flowId to set
	 */
	public void setFlowId(String flowId);

	/**
	 * Returns the corresponding token for the given HTTP method.
	 * 
	 * @param method
	 *            HTTP method
	 * @return the randomToken
	 * @since HDIV 2.1.7
	 */
	public String getRandomToken(String method);

	/**
	 * @param randomToken
	 *            the randomToken to set
	 * @param method
	 *            HTTP method
	 * @since HDIV 2.1.7
	 */
	public void setRandomToken(String randomToken, String method);
}
