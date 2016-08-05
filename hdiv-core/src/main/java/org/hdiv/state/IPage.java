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
import java.util.Collection;

public interface IPage extends Serializable {

	/**
	 * Adds a new state to the page <code>this</code>.
	 *
	 * @param state State that represents all the data that composes a possible request.
	 */
	void addState(IState state);

	/**
	 * Checks if exists a state with the given identifier <code>key</code>.
	 *
	 * @param id State identifier
	 * @return true if exist
	 */
	boolean existState(int id);

	/**
	 * Returns the state with the given identifier <code>key</code> from the map of states
	 *
	 * @param id State identifier
	 * @return IState State with the identifier <code>key</code>.
	 */
	IState getState(int id);

	/**
	 * @return Returns the page id.
	 */
	int getId();

	/**
	 * @return Returns the page states.
	 */
	Collection<? extends IState> getStates();

	/**
	 * @return number of states.
	 */
	int getStatesCount();

	/**
	 * Obtain next valid state id.
	 *
	 * @return State Id to use.
	 */
	int getNextStateId();

	/**
	 * Mark this page as reused in more than one request. Most common case is in Ajax requests.
	 */
	void markAsReused();

	/**
	 * Is this request reused in more than one request?
	 *
	 * @return isReused
	 */
	boolean isReused();

	/**
	 * Returns the unique id of flow.
	 *
	 * @return the flow id
	 */
	String getFlowId();

	/**
	 * @param flowId the flowId to set
	 */
	void setFlowId(String flowId);

	/**
	 * Returns the corresponding token for the given HTTP method.
	 *
	 * @param method HTTP method
	 * @return the randomToken
	 * @since HDIV 2.1.7
	 */
	String getRandomToken(RandomTokenType method);

	/**
	 * @param randomToken the randomToken to set
	 * @param method HTTP method
	 * @since HDIV 2.1.7
	 */
	void setRandomToken(String randomToken, RandomTokenType method);

	/**
	 * @param parentStateId the parentStateId to set
	 *
	 * @since HDIV 2.1.13
	 */
	void setParentStateId(String parentStateId);

	/**
	 * Returns the state id of the parent page
	 *
	 * @return the parent state id
	 *
	 * @since HDIV 2.1.13
	 */
	String getParentStateId();

}
