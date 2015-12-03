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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.config.Strategy;
import org.hdiv.context.RequestContext;
import org.hdiv.exception.HDIVException;
import org.hdiv.session.ISession;
import org.hdiv.state.scope.StateScope;
import org.hdiv.state.scope.StateScopeManager;
import org.hdiv.util.HDIVErrorCodes;

/**
 * Class containing utility methods for state.
 * 
 * @author Roberto Velasco
 */
public class StateUtil {

	/**
	 * Commons Logging instance.
	 */
	private Log log = LogFactory.getLog(StateUtil.class);

	/**
	 * Pattern to check if the memory strategy is being used
	 */
	protected static final String MEMORY_PATTERN = "([0-9]+-){2}[A-Za-z0-9]+";

	/**
	 * Compiled MEMORY_PATTERN
	 */
	protected Pattern memoryPattern = Pattern.compile(MEMORY_PATTERN);

	/**
	 * Hdiv configuration for this app. Access to user defined strategy.
	 */
	protected HDIVConfig config;

	/**
	 * User session wrapper.
	 */
	protected ISession session;

	/**
	 * State scope manager.
	 */
	protected StateScopeManager stateScopeManager;

	/**
	 * StateUtil initialization.
	 */
	public void init() {
		if (log.isDebugEnabled()) {
			log.debug("StateUtil instance created.");
		}
	}

	/**
	 * Restore state data from <code>request</code>. State restore from memory can be done using an identifier or or
	 * using the serialized data received in the request.
	 * 
	 * @param context
	 *            Context holder for request-specific state.
	 * @param requestState
	 *            String that contains HDIV state received in the request
	 * @return State Restore state data from <code>request</code>.
	 * @throws HDIVException
	 *             If the state doesn't exist a new HDIV exception is thrown.
	 */
	public IState restoreState(RequestContext context, String requestState) {

		IState restoredState = null;

		if (this.isMemoryStrategy(requestState)) {

			restoredState = this.restoreMemoryState(context, requestState);

		}

		if (restoredState == null) {
			throw new HDIVException(HDIVErrorCodes.HDIV_PARAMETER_INCORRECT_VALUE);
		}
		return restoredState;
	}

	/**
	 * Checks if the memory strategy is being used
	 * 
	 * @param value
	 *            State id value
	 * 
	 * @return True if strategy is memory. False in otherwise.
	 */
	public boolean isMemoryStrategy(String value) {

		Matcher m = this.memoryPattern.matcher(value);

		return (m.matches() ? true : this.config.getStrategy().equals(Strategy.MEMORY));
	}

	/**
	 * Restore a state from Memory Strategy.
	 * 
	 * @param context
	 *            Context holder for request-specific state.
	 * @param requestState
	 *            String that contains HDIV state received in the request
	 * @return State Restore state data from <code>request</code>.
	 */
	protected IState restoreMemoryState(RequestContext context, String requestState) {

		IState restoredState = null;

		// Extract pageId and stateId from the state identifier
		int firstSeparator = requestState.indexOf("-");
		int lastSeparator = requestState.lastIndexOf("-");
		if (firstSeparator == -1 || lastSeparator == -1) {
			throw new HDIVException(HDIVErrorCodes.HDIV_PARAMETER_INCORRECT_VALUE);
		}

		String pId;
		String sId;
		try {
			pId = requestState.substring(0, firstSeparator);
			sId = requestState.substring(firstSeparator + 1, lastSeparator);
		} catch (StringIndexOutOfBoundsException e) {
			throw new HDIVException(HDIVErrorCodes.HDIV_PARAMETER_INCORRECT_VALUE, e);
		}

		int stateId;
		try {
			stateId = Integer.parseInt(sId);
		} catch (NumberFormatException e) {
			throw new HDIVException(HDIVErrorCodes.HDIV_PARAMETER_INCORRECT_VALUE, e);
		}

		// Obtain State from a StateScopes
		StateScope stateScope = this.stateScopeManager.getStateScope(requestState);

		if (stateScope != null) {
			restoredState = stateScope.restoreState(context, stateId);
			if (restoredState == null) {
				throw new HDIVException(HDIVErrorCodes.PAGE_ID_INCORRECT);
			}
			return restoredState;
		}

		// Obtain State from a HttpSession
		int pageId;
		try {
			pageId = Integer.parseInt(pId);
		} catch (NumberFormatException e) {
			throw new HDIVException(HDIVErrorCodes.HDIV_PARAMETER_INCORRECT_VALUE, e);
		}

		restoredState = this.getStateFromSession(context, pageId, stateId);
		return restoredState;
	}

	/**
	 * Restores the state using the identifier obtained from the <code>HDIVParameter</code> of the request.
	 * 
	 * @param context
	 *            Context holder for request-specific state.
	 * @param pageId
	 *            current {@link IPage} id
	 * @param stateId
	 *            current {@link IState} id
	 * @return State with all the page data.
	 */
	protected IState getStateFromSession(RequestContext context, int pageId, int stateId) {

		IState sessionState = this.session.getState(context, pageId, stateId);

		if (sessionState == null) {
			throw new HDIVException(HDIVErrorCodes.HDIV_PARAMETER_INCORRECT_VALUE);
		}
		return sessionState;
	}

	/**
	 * @param config
	 *            the config to set
	 */
	public void setConfig(HDIVConfig config) {
		this.config = config;
	}

	/**
	 * @param session
	 *            the session to set
	 */
	public void setSession(ISession session) {
		this.session = session;
	}

	/**
	 * @param stateScopeManager
	 *            the stateScopeManager to set
	 */
	public void setStateScopeManager(StateScopeManager stateScopeManager) {
		this.stateScopeManager = stateScopeManager;
	}
}
