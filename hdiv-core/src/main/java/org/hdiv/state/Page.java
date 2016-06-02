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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Data structure to store states of a page.
 *
 * @author Roberto Velasco
 */
public class Page implements IPage, Serializable {

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized
	 * object.
	 */
	private static final long serialVersionUID = -5701140762067196143L;

	/**
	 * Contains the states of the page. Only used in memory strategy.
	 */
	protected List<IState> states = new ArrayList<IState>();

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
	protected transient int size;

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
	protected boolean isReused;

	/**
	 * Parent's state id
	 *
	 * @since 2.1.13
	 */
	protected String parentStateId;

	@Deprecated
	public Page() {
	}

	public Page(final int id) {
		this.id = id;
	}

	/**
	 * Adds a new state to the page <code>this</code>.
	 *
	 * @param state State that represents all the data that composes a possible request.
	 */
	public void addState(final IState state) {
		int id = state.getId();
		int size = states.size();
		if (size < id) {
			// There are empty positions before id, fill with null values
			for (int i = size; i < id; i++) {
				states.add(i, null);
			}
			states.add(id, state);

		}
		else if (size > id) {
			// overwrite existing position
			states.set(id, state);

		}
		else {
			// list size == id
			states.add(id, state);
		}
	}

	/**
	 * Checks if exists a state with the given identifier <code>id</code>.
	 *
	 * @param id State identifier
	 */
	public boolean existState(final int id) {
		return states.get(id) != null;
	}

	/**
	 * Returns the state with the given identifier <code>id</code> from the map of states
	 *
	 * @param id State identifier
	 * @return IState State with the identifier <code>id</code>.
	 */
	public IState getState(final int id) {
		IState state = states.get(id);
		state.setPage(this);
		return state;
	}

	/**
	 * @return Returns the page id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return Returns the page states.
	 */
	public Collection<? extends IState> getStates() {
		return states;
	}

	public int getNextStateId() {

		if (isReused) {
			// We have to synchronize reused Pages due to concurrency problems
			synchronized (this) {
				return stateIdCounter++;
			}
		}
		return stateIdCounter++;
	}

	public void markAsReused() {
		isReused = true;
	}

	public boolean isReused() {
		return isReused;
	}

	/**
	 * @return Returns number of states.
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
	 * @param flowId the flowId to set
	 */
	public void setFlowId(final String flowId) {
		this.flowId = flowId;
	}

	/**
	 * Returns the corresponding token for the given HTTP method.
	 *
	 * @param type Token type
	 * @return the randomToken
	 * @since HDIV 2.1.7
	 */
	public final String getRandomToken(final RandomTokenType type) {

		if (type == RandomTokenType.FORM) {
			return formRandomToken;
		}
		else {
			return randomToken;
		}
	}

	/**
	 * @param randomToken the randomToken to set
	 * @param type Token type
	 * @since HDIV 2.1.7
	 */
	public void setRandomToken(final String randomToken, final RandomTokenType type) {
		if (type == RandomTokenType.FORM) {
			formRandomToken = randomToken;
		}
		else {
			this.randomToken = randomToken;
		}
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(final int size) {
		this.size = size;
	}

	/**
	 * @param parentStateId the parentStateId to set
	 */
	public void setParentStateId(final String parentStateId) {
		this.parentStateId = parentStateId;
	}

	/**
	 * @return the parentStateId
	 */
	public String getParentStateId() {
		return parentStateId;
	}

	@Override
	public String toString() {

		StringBuilder result = new StringBuilder();
		result.append("Page:").append(id).append(' ');

		for (IState state : states) {
			result.append(" ").append(state.toString());
		}

		return result.toString();
	}

}