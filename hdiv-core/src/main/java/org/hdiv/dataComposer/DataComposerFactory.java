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

import org.hdiv.config.HDIVConfig;
import org.hdiv.exception.HDIVException;
import org.hdiv.idGenerator.UidGenerator;
import org.hdiv.session.ISession;
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
	private HDIVConfig hdivConfig;

	/**
	 * Http session wrapper
	 */
	private ISession session;

	/**
	 * Unique Id generator
	 */
	private UidGenerator uidGenerator;

	/**
	 * Maximum size allowed to represent page state
	 */
	private int allowedLength;

	/**
	 * Utility methods for encoding
	 */
	private EncodingUtil encodingUtil;

	/**
	 * Creates a new instance of DataComposer based on the defined strategy.
	 * 
	 * @return IDataComposer instance
	 */
	public IDataComposer newInstance() {

		if (this.hdivConfig.getStrategy().equalsIgnoreCase("memory")) {
			DataComposerMemory composer = new DataComposerMemory();
			composer.setHdivConfig(this.hdivConfig);
			composer.setSession(this.session);
			composer.setUidGenerator(this.uidGenerator);
			composer.init();
			return composer;

		} else if (this.hdivConfig.getStrategy().equalsIgnoreCase("cipher")) {
			DataComposerCipher composer = new DataComposerCipher();
			composer.setHdivConfig(this.hdivConfig);
			composer.setSession(this.session);
			composer.setUidGenerator(this.uidGenerator);
			composer.setAllowedLength(this.allowedLength);
			composer.setEncodingUtil(this.encodingUtil);
			composer.init();
			return composer;

		} else if (this.hdivConfig.getStrategy().equalsIgnoreCase("hash")) {
			DataComposerHash composer = new DataComposerHash();
			composer.setHdivConfig(this.hdivConfig);
			composer.setSession(this.session);
			composer.setUidGenerator(this.uidGenerator);
			composer.setAllowedLength(this.allowedLength);
			composer.setEncodingUtil(this.encodingUtil);
			composer.init();
			return composer;

		} else {
			String errorMessage = HDIVUtil.getMessage("strategy.error", this.hdivConfig.getStrategy());
			throw new HDIVException(errorMessage);
		}

	}

	/**
	 * @param hdivConfig the hdivConfig to set
	 */
	public void setHdivConfig(HDIVConfig hdivConfig) {
		this.hdivConfig = hdivConfig;
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(ISession session) {
		this.session = session;
	}

	/**
	 * @param uidGenerator the uidGenerator to set
	 */
	public void setUidGenerator(UidGenerator uidGenerator) {
		this.uidGenerator = uidGenerator;
	}

	/**
	 * @param allowedLength the allowedLength to set
	 */
	public void setAllowedLength(int allowedLength) {
		this.allowedLength = allowedLength;
	}

	/**
	 * @param encodingUtil the encodingUtil to set
	 */
	public void setEncodingUtil(EncodingUtil encodingUtil) {
		this.encodingUtil = encodingUtil;
	}

}