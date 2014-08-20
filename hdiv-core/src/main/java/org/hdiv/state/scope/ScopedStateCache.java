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
package org.hdiv.state.scope;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.hdiv.state.IParameter;
import org.hdiv.state.IState;

/**
 * Cache than manages scoped states for a specific type of {@link StateScope}.
 * 
 * @since 2.1.7
 */
public class ScopedStateCache implements Serializable {

	private static final long serialVersionUID = 5141785794691242839L;

	private Map<Integer, StateAndToken> states = new HashMap<Integer, StateAndToken>();

	private AtomicInteger index = new AtomicInteger();

	public String addState(IState state, String token) {

		Integer previousStateId = this.existEqualState(state);
		if (previousStateId != null) {
			StateAndToken previousState = states.get(previousStateId);
			return previousStateId + "-" + previousState.getToken();
		}

		int id = this.index.getAndIncrement();
		states.put(id, new StateAndToken(state, token));

		return id + "-" + token;
	}

	public IState getState(int stateId) {

		StateAndToken st = states.get(stateId);
		return st == null ? null : st.getState();
	}

	public String getStateToken(int stateId) {

		StateAndToken st = states.get(stateId);
		return st == null ? null : st.getToken();
	}

	protected Integer existEqualState(IState state) {

		for (Entry<Integer, StateAndToken> entry : states.entrySet()) {
			IState cacheState = entry.getValue().getState();

			if (this.areEqualStates(cacheState, state)) {
				return entry.getKey();
			}
		}

		return null;
	}

	protected boolean areEqualStates(IState state1, IState state2) {

		// Same action
		if (!(state1.getAction().equals(state2.getAction()))) {
			return false;
		}

		// Same Parameters
		Collection<IParameter> params1 = state1.getParameters();
		Collection<IParameter> params2 = state2.getParameters();
		if (params1 != null && params2 != null) {
			if (params1.size() != params2.size()) {
				return false;
			}
			for (IParameter param1 : params1) {

				boolean exist = false;
				for (IParameter param2 : params2) {
					if (this.areEqualParameters(param1, param2)) {
						exist = true;
					}
				}
				if (!exist) {
					return false;
				}
			}
		}

		String parameters1 = state1.getParams();
		String parameters2 = state2.getParams();
		if (parameters1 != null && parameters2 != null) {
			if (!parameters1.equals(parameters2)) {
				return false;
			}
		}

		// Same required Parameters
		List<String> requiredParams1 = state1.getRequiredParams();
		List<String> requiredParams2 = state2.getRequiredParams();
		if (requiredParams1 != null && requiredParams2 != null) {
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

	protected boolean areEqualParameters(IParameter param1, IParameter param2) {

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

	class StateAndToken implements Serializable {

		private static final long serialVersionUID = -7927456168851506372L;

		private IState state;
		private String token;

		public StateAndToken(IState state, String token) {
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
