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
package org.hdiv.dataComposer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.state.IState;
import org.hdiv.util.EncodingUtil;

/**
 * It generates the page states stored in the client and in the user session. These
 * states will be added to each possible request encoded in Base64 and the hash of
 * each state will be stored in the user session. With this implementation we are
 * able to check if the hash received in a request is equal to the one stored in
 * session, which means the request is correct.
 * 
 * @see org.hdiv.dataComposer.DataComposerMemory
 * @author Gorka Vicente
 */
public class DataComposerHash extends DataComposerMemory {

	/**
	 * Commons Logging instance.
	 */
	private static Log log = LogFactory.getLog(DataComposerHash.class);

	/**
	 * Utility methods for encoding
	 */
	private EncodingUtil encodingUtil;

	/**
	 * Maximum size allowed to represent page state
	 */
	private int allowedLength;

	/**
	 * The state that is sent to the client is generated in Base64 and the hash of
	 * this state is stored in the session. Thus, it is able to check the state
	 * received in the request with the hash in the server.
	 * 
	 * @return Obtains the state encoded in Base64 that will be added to the request
	 *         in the HDIV extra parameter.
	 */
	public String endRequest() {

		IState state = (IState) super.getStatesStack().pop();
		state.setPageId(this.getPage().getName());

		String id = null;
		String stateWithSuffix = null;

		String stateData = encodingUtil.encode64(state);

		// if state's length it's too long for GET methods we have to change the
		// strategy to memory
		if (stateData.length() > this.allowedLength) {

			if (log.isDebugEnabled()) {
				log.debug("Move from Hash strategy to Memory because state data [" + stateData.length()
						+ "] is greater than allowedLength [" + this.allowedLength);
			}

			super.startPage();

			this.getPage().addState(state);
			state.setPageId(this.getPage().getName());

			id = this.getPage().getName() + DASH + state.getId() + DASH + this.getHdivStateSuffix();

		} else {
			// generate hash to add to the page that will be stored in session
			stateWithSuffix = stateData + DASH + this.getHdivStateSuffix();
			String stateHash = this.encodingUtil.calculateStateHash(stateWithSuffix);
			this.getPage().addState(state.getId(), stateHash);
		}

		return (id != null) ? id : stateWithSuffix;
	}

	/**
	 * @return Returns the encoding util.
	 */
	public EncodingUtil getEncodingUtil() {
		return encodingUtil;
	}

	/**
	 * @param encodingUtil The encoding util to set.
	 */
	public void setEncodingUtil(EncodingUtil encodingUtil) {
		this.encodingUtil = encodingUtil;
	}

	/**
	 * @param allowedLength The allowed length to set.
	 */
	public void setAllowedLength(int allowedLength) {
		this.allowedLength = allowedLength;
	}

}
