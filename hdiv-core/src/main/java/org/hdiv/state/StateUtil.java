/**
 * Copyright 2005-2011 hdiv.org
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
import org.hdiv.dataComposer.AbstractDataComposer;
import org.hdiv.exception.HDIVException;
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
	 * Memory strategy name 
	 */
	private static final String MEMORY_STRATEGY = "memory";
	
	/**
	 * Cipher strategy name
	 */
	private static final String CIPHER_STRATEGY = "cipher";
	
	/**
	 * Hash strategy name
	 */
	private static final String HASH_STRATEGY = "hash";
	
	/**
	 * Pattern to check if the memory strategy is being used
	 */
	private static final String MEMORY_PATTERN = "([0-9]+"+AbstractDataComposer.DASH+"){2}[A-Za-z0-9]+";
	
	/**
	 * Compiled MEMORY_PATTERN
	 */
	private Pattern memoryPattern = Pattern.compile(MEMORY_PATTERN);
	
	/**
	 * Parameter name included by HDIV in the requests or forms which will contain
	 * the state identifier, in the memory strategy, or the state itself, in the
	 * encoded and hash strategies.
	 */
	private String HDIVParameter;

	/**
	 * Utility methods for encoding
	 */
	private EncodingUtil encodingUtil;

	/**
	 * Hdiv configuration for this app. Access to user defined strategy.
	 */
	private HDIVConfig config;

	/**
	 * StateUtil initialization with HDIV parameter name stored in http session.
	 */
	public void init() {
	}	

	/**
	 * Restore state data from <code>request</code>. State restore from memory can
	 * be done using an identifier or or using the serialized data received in the
	 * request.
	 * 
	 * @param requestState String that contains HDIV state received in the request
	 * @return State Restore state data from <code>request</code>.
	 * @throws HDIVException If the state doesn't exist a new HDIV exception is
	 *             thrown.
	 */ 
	public IState restoreState(String requestState) {

		IState restoredState = null;
		
		if (this.isMemoryStrategy(requestState)) {
			restoredState = this.getStateFromSession(requestState);

		} else if (this.isCipherStrategy()) {
			restoredState = (IState) encodingUtil.decode64Cipher(requestState);

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
	 * @return True if strategy is memory. False in otherwise.
	 */
	public boolean isMemoryStrategy(String value) {

		Matcher m = this.memoryPattern.matcher(value);

		return (m.matches() ? true : this.config.getStrategy().equalsIgnoreCase(MEMORY_STRATEGY));
	}

	/**
	 * Checks if the cipher (encoded) strategy is being used.
	 *
	 * @return True if strategy is cipher. False in otherwise.
	 */
	private boolean isCipherStrategy() {
		return this.config.getStrategy().equalsIgnoreCase(CIPHER_STRATEGY);
	}

	/**
	 * Checks if the hash strategy is being used.
	 *
	 * @return True if strategy is hash. False in otherwise.
	 */
	private boolean isHashStrategy() {
		return this.config.getStrategy().equalsIgnoreCase(HASH_STRATEGY);
	}

	/**
	 * Restores the state using the identifier obtained from the
	 * <code>HDIVParameter</code> of the request.
	 * 
	 * @param value <code>HDIVParameter</code> parameter value.
	 * @return State with all the page data.
	 */
	private IState getStateFromSession(String value) {

		int firstSeparator = value.indexOf("-");
		int lastSeparator = value.lastIndexOf("-");
		if ((firstSeparator == -1) || (lastSeparator == -1)) { 
			throw new HDIVException(HDIVErrorCodes.HDIV_PARAMETER_INCORRECT_VALUE);
		}
		
		String page = value.substring(0, firstSeparator);
		String stateId = value.substring(firstSeparator + 1, lastSeparator);

		IState sessionState =  this.encodingUtil.getSession().getState(page, stateId);
		
		if (sessionState == null) {
			throw new HDIVException(HDIVErrorCodes.HDIV_PARAMETER_INCORRECT_VALUE);
		}
		return sessionState;
	}
	
	/**
	 * Checks if the state hash received from the client and the hash stored in
	 * session match. If it is true, an object of type <code>IState</code> is
	 * returned. Otherwise, a HDIVException is thrown.
	 * 
	 * @param value State received in the request encoded in Base64
	 * @return Decoded state of type <code>IState</code> obtained from
	 *         <code>value</code>
	 */
	protected IState restoreHashState(String value) {

		String restoredStateHash = this.encodingUtil.calculateStateHash(value);

		IState decodedState = (IState) encodingUtil.decode64(value);
		String sessionStateHash = this.encodingUtil.getSession().getStateHash(decodedState.getPageId(), decodedState.getId());

		if (restoredStateHash.equals(sessionStateHash)) {
			return decodedState;
		}
		return null;
	}

	/**
	 * @return Returns the HDIV state parameter.
	 */
	public String getHDIVParameter() {
		return HDIVParameter;
	}
	
	/**
	 * @param parameter The hDIVParameter to set.
	 */
	public void setHDIVParameter(String parameter) {
		HDIVParameter = parameter;
	}	
	
	/**
	 * @return Returns the encoding util.
	 */
	public EncodingUtil getEncodingUtil() {
		return encodingUtil;
	}

	/**
	 * @param encodingUtil The encodingUtil to set.
	 */
	public void setEncodingUtil(EncodingUtil encodingUtil) {
		this.encodingUtil = encodingUtil;
	}

	/**
	 * @param config the config to set
	 */
	public void setConfig(HDIVConfig config) {
		this.config = config;
	}

}
