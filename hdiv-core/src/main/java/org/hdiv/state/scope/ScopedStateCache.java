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
package org.hdiv.state.scope;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.hdiv.state.IState;
import org.hdiv.util.HDIVStateUtils;

/**
 * Cache than manages scoped states for a specific type of {@link StateScope}.
 *
 * @since 2.1.7
 */
public class ScopedStateCache implements Serializable {

	private static final long serialVersionUID = 5141785794691242839L;

	private final Map<Integer, StateAndToken> states = new HashMap<Integer, StateAndToken>();

	private final AtomicInteger index = new AtomicInteger();

	public String addState(final IState state, final String token) {

		Integer previousStateId = existEqualState(state);
		if (previousStateId != null) {
			StateAndToken previousState = states.get(previousStateId);
			return HDIVStateUtils.getScopedState(previousStateId, previousState.getToken());
		}

		int id = index.getAndIncrement();
		states.put(id, new StateAndToken(state, token));

		return HDIVStateUtils.getScopedState(id, token);
	}

	public IState getState(final int stateId) {

		StateAndToken st = states.get(stateId);
		return st == null ? null : st.getState();
	}

	public String getStateToken(final int stateId) {

		StateAndToken st = states.get(stateId);
		return st == null ? null : st.getToken();
	}

	protected Integer existEqualState(final IState state) {

		for (Entry<Integer, StateAndToken> entry : states.entrySet()) {
			IState cacheState = entry.getValue().getState();

			if (areEqualStates(cacheState, state)) {
				return entry.getKey();
			}
		}

		return null;
	}

	protected boolean areEqualStates(final IState state1, final IState state2) {
		return state1.isEquivalent(state2);
	}

	class StateAndToken implements Serializable {

		private static final long serialVersionUID = -7927456168851506372L;

		private final IState state;

		private final String token;

		public StateAndToken(final IState state, final String token) {
			this.state = state;
			this.token = token;
		}

		/**
		 * @return the state
		 */
		public IState getState() {
			return state;
		}

		/**
		 * @return the token
		 */
		public String getToken() {
			return token;
		}

	}

}
