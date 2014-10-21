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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.config.Strategy;
import org.hdiv.exception.HDIVException;
import org.hdiv.session.ISession;
import org.hdiv.state.scope.StateScope;
import org.hdiv.state.scope.StateScopeManager;
import org.hdiv.util.EncodingUtil;
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
	 * Utility methods for encoding
	 */
	protected EncodingUtil encodingUtil;

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
	 * @param requestState
	 *            String that contains HDIV state received in the request
	 * @return State Restore state data from <code>request</code>.
	 * @throws HDIVException
	 *             If the state doesn't exist a new HDIV exception is thrown.
	 */
	public IState restoreState(String requestState) {

		IState restoredState = null;

		if (this.isMemoryStrategy(requestState)) {

			restoredState = this.restoreMemoryState(requestState);

		} else if (this.isCipherStrategy()) {
			restoredState = this.restoreCipherState(requestState);

		} else if (this.isHashStrategy()) {
			restoredState = this.restoreHashState(requestState);
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
	 * Checks if the cipher (encoded) strategy is being used.
	 * 
	 * @return True if strategy is cipher. False in otherwise.
	 */
	protected boolean isCipherStrategy() {
		return this.config.getStrategy().equals(Strategy.CIPHER);
	}

	/**
	 * Checks if the hash strategy is being used.
	 * 
	 * @return True if strategy is hash. False in otherwise.
	 */
	protected boolean isHashStrategy() {
		return this.config.getStrategy().equals(Strategy.HASH);
	}

	/**
	 * Restore a state from Memory Strategy.
	 * 
	 * @param requestState
	 *            String that contains HDIV state received in the request
	 * @return State Restore state data from <code>request</code>.
	 */
	protected IState restoreMemoryState(String requestState) {

		IState restoredState = null;

		// Extract pageId and stateId from the state identifier
		int firstSeparator = requestState.indexOf("-");
		int lastSeparator = requestState.lastIndexOf("-");
		if (firstSeparator == -1 || lastSeparator == -1) {
			throw new HDIVException(HDIVErrorCodes.HDIV_PARAMETER_INCORRECT_VALUE);
		}

		String pageId;
		String sId;
		try {
			pageId = requestState.substring(0, firstSeparator);
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

		// Obtain Scopes
		StateScope stateScope = this.stateScopeManager.getStateScope(requestState);

		if (stateScope != null) {
			restoredState = stateScope.restoreState(stateId);
		} else {

			restoredState = this.getStateFromSession(pageId, stateId);
		}

		return restoredState;
	}

	/**
	 * Restores the state using the identifier obtained from the <code>HDIVParameter</code> of the request.
	 * 
	 * @param pageId
	 *            current {@link IPage} id
	 * @param stateId
	 *            current {@link IState} id
	 * @return State with all the page data.
	 */
	protected IState getStateFromSession(String pageId, int stateId) {

		IState sessionState = this.session.getState(pageId, stateId);

		if (sessionState == null) {
			throw new HDIVException(HDIVErrorCodes.HDIV_PARAMETER_INCORRECT_VALUE);
		}
		return sessionState;
	}

	/**
	 * Restore state in cipher strategy.
	 * 
	 * @param requestState
	 *            State received in the request
	 * @return Decoded state of type <code>IState</code> obtained from <code>value</code>
	 */
	protected IState restoreCipherState(String requestState) {

		Object[] cipherData = (Object[]) this.encodingUtil.decode64Cipher(requestState);
		IState restoredState = (IState) cipherData[0];
		return restoredState;
	}

	/**
	 * Checks if the state hash received from the client and the hash stored in session match. If it is true, an object
	 * of type <code>IState</code> is returned. Otherwise, a HDIVException is thrown.
	 * 
	 * @param value
	 *            State received in the request encoded in Base64
	 * @return Decoded state of type <code>IState</code> obtained from <code>value</code>
	 */
	protected IState restoreHashState(String value) {

		String restoredStateHash = this.encodingUtil.calculateStateHash(value);

		IState decodedState = (IState) encodingUtil.decode64(value);
		String sessionStateHash = this.session.getStateHash(decodedState.getPageId() + "", decodedState.getId());

		if (restoredStateHash.equals(sessionStateHash)) {
			return decodedState;
		}
		return null;
	}

	/**
	 * @param encodingUtil
	 *            The encodingUtil to set.
	 */
	public void setEncodingUtil(EncodingUtil encodingUtil) {
		this.encodingUtil = encodingUtil;
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
