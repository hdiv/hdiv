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
 * It generates the states of each page storing them in the client. These states will
 * be encoded to guarantee its integrity.
 * <p>
 * Non editable values are hidden to the client to guarantee confidentiality
 * <b>confidentiality</b>.
 * </p>
 * 
 * @see org.hdiv.dataComposer.DataComposerMemory
 * @author Roberto Velasco
 * @author Gorka Vicente 
 */
public class DataComposerCipher extends DataComposerMemory {

	/**
	 * Commons Logging instance.
	 */
	private static Log log = LogFactory.getLog(DataComposerCipher.class);

	/**
	 * Utility methods for encoding
	 */
	private EncodingUtil encodingUtil;

	/**
	 * Maximum size allowed to represent page state
	 */
	private int allowedLength;

	/**
	 * Indicates if the Encoded strategy has used the Memory strategy to store a
	 * state, because if the generated state to store all the data of a link or form
	 * exceeds the allowed length <code>allowedLength</code> this state is stored
	 * in the user session, as in the Memory strategy.
	 */
	private boolean savePage = false;

	/**
	 * It is called by each request or form existing in the page returned by the
	 * server.
	 * <p>
	 * it generates an encoded string containing the current state.
	 * </p>
	 * <p>
	 * If the size of the generated state exceeds the maximun length allowed
	 * <code>allowedLength</code> the Memory strategy will be used, adding the
	 * state of the request or form to the page <code>page</code> and returning an
	 * identifier composed by the page identifier and the state identifier as a
	 * result.
	 * </p>
	 * 
	 * @return String with the encoded state. If the Memory strategy has been used,
	 *         an identifier.
	 */
	public String endRequest() {

		IState state = (IState) super.getStatesStack().pop();
		state.setPageId(this.getPage().getName());

		String stateData = this.encodingUtil.encode64Cipher(state);
		String id = null;

		// if state's length it's too long for GET methods we have to change the
		// strategy to memory
		if (stateData.length() > this.allowedLength) {

			if (log.isDebugEnabled()) {
				log.debug("Move from Cipher strategy to Memory because state data [" + stateData.length()
						+ "] is greater than allowedLength [" + this.allowedLength);
			}

			this.savePage = true;
			super.startPage();

			this.getPage().addState(state);
			state.setPageId(this.getPage().getName());

			id = this.getPage().getName() + DASH + state.getId() + DASH + this.getHdivStateSuffix();
		}

		return (id != null) ? id : stateData;
	}

	/**
	 * Only if the generated encoded state exceeds the maximun length allowed it will be
	 * necessary to store in session the object representing the current page.
	 * 
	 * @see org.hdiv.dataComposer.DataComposerMemory#endPage() 
	 */
	public void endPage() {

		if (savePage) {
			super.endPage();
		}
	}

	public void startPage() {
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
