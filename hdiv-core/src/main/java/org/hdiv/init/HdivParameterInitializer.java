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
package org.hdiv.init;

import org.hdiv.config.HDIVConfig;
import org.hdiv.util.HDIVUtil;

public class HdivParameterInitializer {

	/**
	 * HDIV configuration object
	 */
	protected HDIVConfig config;

	public HdivParameterInitializer() {
		// TODO Auto-generated constructor stub
	}

	protected String getHdivParameter() {
		if (config.isRandomName()) {
			return HDIVUtil.createRandomToken(Integer.MAX_VALUE);
		}
		else {
			return config.getStateParameterName();
		}
	}

	protected String getModifyHdivParameter() {
		if (config.isRandomName()) {
			return HDIVUtil.createRandomToken(Integer.MAX_VALUE);
		}
		else {
			return config.getModifyStateParameterName();
		}
	}

}
