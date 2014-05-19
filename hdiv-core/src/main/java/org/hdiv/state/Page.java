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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * DataStructure to store server states
 * 
 * @author Roberto Velasco
 */
public class Page implements IPage, Serializable {

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that a loaded class corresponds exactly
	 * to a serialized object.
	 */
	private static final long serialVersionUID = -5701140762067196143L;

	/**
	 * Map with the states of the page <code>this</code>.
	 */
	protected Map<Integer, Object> states = new HashMap<Integer, Object>();

	/**
	 * Page <code>this</code> identifier.
	 */
	private String name;

	/**
	 * Unique id of flow
	 */
	private String flowId;

	/**
	 * Unique random token
	 * 
	 * @since HDIV 2.0.4
	 */
	private String randomToken;

	/**
	 * Adds a new state to the page <code>this</code>.
	 * 
	 * @param state
	 *            State that represents all the data that composes a possible request.
	 */
	public void addState(IState state) {
		this.states.put(state.getId(), state);
	}

	/**
	 * Adds a new state hash to the page <code>this</code>.
	 * 
	 * @param id
	 *            state identifier
	 * @param stateHash
	 *            Hash of a state that represents all the data that composes a possible request.
	 */
	public void addState(int id, String stateHash) {
		this.states.put(id, stateHash);
	}

	/**
	 * Checks if exists a state with the given identifier <code>key</code>.
	 * 
	 * @param id
	 *            State identifier
	 */
	public boolean existState(int id) {
		return this.states.containsKey(id);
	}

	/**
	 * Returns the state with the given identifier <code>key</code> from the map of states
	 * 
	 * @param id
	 *            State identifier
	 * @return IState State with the identifier <code>key</code>.
	 */
	public IState getState(int id) {
		return (IState) this.states.get(id);
	}

	/**
	 * Returns the state hash with the given identifier <code>key</code> from the map of states
	 * 
	 * @param key
	 *            State identifier
	 * @return String hash with the identifier <code>key</code>.
	 */
	public String getStateHash(int key) {
		return (String) this.states.get(key);
	}

	/**
	 * @return Returns the page name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The page name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the page states.
	 */
	public Collection<Object> getStates() {
		return states.values();
	}

	/**
	 * @return Returns the page states.
	 */
	public int getStatesCount() {
		return states.size();
	}

	/**
	 * Returns the unique id of flow.
	 * 
	 * @return the flow id
	 */
	public String getFlowId() {
		return flowId;
	}

	/**
	 * @param flowId
	 *            the flowId to set
	 */
	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	/**
	 * @return the randomToken
	 * @since HDIV 2.0.4
	 */
	public String getRandomToken() {
		return randomToken;
	}

	/**
	 * @param randomToken
	 *            the randomToken to set
	 * @since HDIV 2.0.4
	 */
	public void setRandomToken(String randomToken) {
		this.randomToken = randomToken;
	}

	public String toString() {

		StringBuffer result = new StringBuffer();
		result.append("Page:" + this.name + " ");

		for (Object state : states.values()) {
			result.append(" " + state.toString());
		}

		return result.toString();
	}

}
