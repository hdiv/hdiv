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
package org.hdiv.dataComposer;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.config.HDIVConfig;
import org.hdiv.exception.HDIVException;
import org.hdiv.idGenerator.UidGenerator;
import org.hdiv.session.ISession;
import org.hdiv.state.IPage;
import org.hdiv.state.IState;
import org.hdiv.state.StateUtil;
import org.hdiv.util.Constants;
import org.hdiv.util.EncodingUtil;
import org.hdiv.util.HDIVUtil;

/**
 * DataComposer object factory, more efficient than to use the Spring factory.
 * 
 * @author Gotzon Illarramendi
 * @since HDIV 2.1.0
 */
public class DataComposerFactory {

	/**
	 * HDIV configuration object.
	 */
	protected HDIVConfig hdivConfig;

	/**
	 * Http session wrapper
	 */
	protected ISession session;

	/**
	 * Unique Id generator
	 */
	protected UidGenerator uidGenerator;

	/**
	 * Maximum size allowed to represent page state
	 */
	protected int allowedLength;

	/**
	 * Utility methods for encoding
	 */
	protected EncodingUtil encodingUtil;

	/**
	 * State management utility
	 */
	protected StateUtil stateUtil;

	/**
	 * Creates a new instance of DataComposer based on the defined strategy.
	 * 
	 * @param request
	 *            {@link HttpServletRequest} instance
	 * 
	 * @return IDataComposer instance
	 */
	public IDataComposer newInstance(HttpServletRequest request) {

		IDataComposer dataComposer = null;

		if (this.hdivConfig.getStrategy().equalsIgnoreCase("memory")) {
			DataComposerMemory composer = new DataComposerMemory();
			composer.setHdivConfig(this.hdivConfig);
			composer.setSession(this.session);
			composer.setUidGenerator(this.uidGenerator);
			composer.init();
			dataComposer = composer;

		} else if (this.hdivConfig.getStrategy().equalsIgnoreCase("cipher")) {
			DataComposerCipher composer = new DataComposerCipher();
			composer.setHdivConfig(this.hdivConfig);
			composer.setSession(this.session);
			composer.setUidGenerator(this.uidGenerator);
			composer.setAllowedLength(this.allowedLength);
			composer.setEncodingUtil(this.encodingUtil);
			composer.init();
			dataComposer = composer;

		} else if (this.hdivConfig.getStrategy().equalsIgnoreCase("hash")) {
			DataComposerHash composer = new DataComposerHash();
			composer.setHdivConfig(this.hdivConfig);
			composer.setSession(this.session);
			composer.setUidGenerator(this.uidGenerator);
			composer.setAllowedLength(this.allowedLength);
			composer.setEncodingUtil(this.encodingUtil);
			composer.init();
			dataComposer = composer;

		} else {
			String errorMessage = HDIVUtil.getMessage("strategy.error", this.hdivConfig.getStrategy());
			throw new HDIVException(errorMessage);
		}

		this.initDataComposer(dataComposer, request);

		return dataComposer;
	}

	/**
	 * Initialize IDataComposer instance.
	 * 
	 * @param dataComposer
	 *            IDataComposer instance
	 * @param request
	 *            actual HttpServletRequest instance
	 */
	protected void initDataComposer(IDataComposer dataComposer, HttpServletRequest request) {

		String paramName = (String) request.getSession().getAttribute(Constants.MODIFY_STATE_HDIV_PARAMETER);
		String preState = request.getParameter(paramName);
		if (preState != null && preState.length() > 0) {

			// We are modifying an existing state, preload dataComposer with it
			IState state = this.stateUtil.restoreState(preState);
			if (state.getPageId() > 0) {
				IPage page = this.session.getPage(state.getPageId() + "");
				if (page != null) {
					dataComposer.startPage(page);
				}
			}
			if (state != null) {
				dataComposer.beginRequest(state);
			}
		} else {
			dataComposer.startPage();
		}
	}

	/**
	 * @param hdivConfig
	 *            the hdivConfig to set
	 */
	public void setHdivConfig(HDIVConfig hdivConfig) {
		this.hdivConfig = hdivConfig;
	}

	/**
	 * @param session
	 *            the session to set
	 */
	public void setSession(ISession session) {
		this.session = session;
	}

	/**
	 * @param uidGenerator
	 *            the uidGenerator to set
	 */
	public void setUidGenerator(UidGenerator uidGenerator) {
		this.uidGenerator = uidGenerator;
	}

	/**
	 * @param allowedLength
	 *            the allowedLength to set
	 */
	public void setAllowedLength(int allowedLength) {
		this.allowedLength = allowedLength;
	}

	/**
	 * @param encodingUtil
	 *            the encodingUtil to set
	 */
	public void setEncodingUtil(EncodingUtil encodingUtil) {
		this.encodingUtil = encodingUtil;
	}

	/**
	 * @param stateUtil
	 *            the stateUtil to set
	 */
	public void setStateUtil(StateUtil stateUtil) {
		this.stateUtil = stateUtil;
	}

}