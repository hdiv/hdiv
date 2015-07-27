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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data structure to store states of a page.
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
	 * Contains the states of the page. Only used in memory and cipher strategy.
	 */
	protected List<IState> states = new ArrayList<IState>();

	/**
	 * Contains the state hashes of this page. Only used in hash strategy.
	 */
	protected Map<Integer, String> hashStates;

	/**
	 * Page <code>this</code> identifier.
	 */
	protected int id;

	/**
	 * Unique id of flow
	 */
	protected String flowId;

	/**
	 * Unique random token. Used only for links.
	 * 
	 * @since HDIV 2.0.4
	 */
	protected String randomToken;

	/**
	 * Unique random token. Used only for forms with PATCH, POST, PUT or DELETE methods.
	 * 
	 * @since 2.1.7
	 */
	protected String formRandomToken;

	/**
	 * Page size.
	 */
	protected long size;

	/**
	 * Sequential counter to generate state ids.
	 * 
	 * @since 2.1.11
	 */
	protected int stateIdCounter;

	/**
	 * True if this page is reused. The most common case is in an Ajax request.
	 * 
	 * @since 2.1.11
	 */
	protected Boolean isReused;

	/**
	 * Adds a new state to the page <code>this</code>.
	 * 
	 * @param state
	 *            State that represents all the data that composes a possible request.
	 */
	public void addState(IState state) {
		int id = state.getId();
		if (this.states.size() < id) {
			// There are empty positions before id, fill with null values
			for (int i = this.states.size(); i < id; i++) {
				this.states.add(i, null);
			}
			this.states.add(id, state);

		} else if (this.states.size() > id) {
			// overwrite existing position
			this.states.set(id, state);

		} else {
			// list size == id
			this.states.add(id, state);
		}
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
		if (this.hashStates == null) {
			this.hashStates = new HashMap<Integer, String>();
		}
		this.hashStates.put(id, stateHash);
	}

	/**
	 * Checks if exists a state with the given identifier <code>id</code>.
	 * 
	 * @param id
	 *            State identifier
	 */
	public boolean existState(int id) {
		return this.states.get(id) != null;
	}

	/**
	 * Returns the state with the given identifier <code>id</code> from the map of states
	 * 
	 * @param id
	 *            State identifier
	 * @return IState State with the identifier <code>id</code>.
	 */
	public IState getState(int id) {
		return this.states.get(id);
	}

	/**
	 * Returns the state hash with the given identifier <code>key</code> from the map of states
	 * 
	 * @param key
	 *            State identifier
	 * @return String hash with the identifier <code>key</code>.
	 */
	public String getStateHash(int key) {
		if (this.hashStates == null) {
			return null;
		}
		return this.hashStates.get(key);
	}

	/**
	 * @return Returns the page name.
	 */
	public String getName() {
		return this.id + "";
	}

	/**
	 * @return Returns the page id.
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            The page id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return Returns the page states.
	 */
	public Collection<? extends Object> getStates() {
		return states;
	}

	public int getNextStateId() {

		if (isReused != null && isReused) {
			// We have to synchronize reused Pages due to concurrency problems
			synchronized (this) {
				return this.stateIdCounter++;
			}
		}
		return this.stateIdCounter++;
	}

	public void markAsReused() {
		this.isReused = true;
	}

	public boolean isReused() {
		return this.isReused == null ? false : this.isReused;
	}

	/**
	 * @return Returns number of states.
	 */
	public int getStatesCount() {
		int count = states.size();
		if (hashStates != null) {
			count = count + hashStates.size();
		}
		return count;
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
	 * Returns the corresponding token for the given HTTP method.
	 * 
	 * @param method
	 *            HTTP method
	 * @return the randomToken
	 * @since HDIV 2.1.7
	 */
	public String getRandomToken(String method) {

		if (this.isFormMethod(method)) {
			return this.formRandomToken;
		} else {
			return randomToken;
		}
	}

	/**
	 * @param randomToken
	 *            the randomToken to set
	 * @param method
	 *            HTTP method
	 * @since HDIV 2.1.7
	 */
	public void setRandomToken(String randomToken, String method) {
		if (this.isFormMethod(method)) {
			this.formRandomToken = randomToken;
		} else {
			this.randomToken = randomToken;
		}
	}

	/**
	 * @param method
	 *            HTTP method
	 * @return true if method is POST, PATCH, PUT or DELETE, false otherwise.
	 */
	protected boolean isFormMethod(String method) {
		if (method == null) {
			// GET equivalent
			return false;
		}
		method = method.toUpperCase();
		if (method.equals("GET")) {
			return false;
		}
		if (method.equals("POST") || method.equals("PATCH") || method.equals("PUT") || method.equals("DELETE")) {

			return true;
		}
		// Otherwise
		return false;
	}

	/**
	 * @return the size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(long size) {
		this.size = size;
	}

	public String toString() {

		StringBuffer result = new StringBuffer();
		result.append("Page:" + this.id + " ");

		for (IState state : states) {
			result.append(" " + state.toString());
		}

		return result.toString();
	}

}