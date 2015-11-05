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
package org.hdiv;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.config.HDIVConfig;
import org.hdiv.util.HDIVUtil;

public abstract class AbstractJsfHDIVTestCase extends AbstractHDIVTestCase {

	protected ShaleMockObjects shaleMockObjects;

	@Override
	protected void onSetUp() throws Exception {

		HttpServletRequest request = HDIVUtil.getHttpServletRequest();

		this.shaleMockObjects = new ShaleMockObjects();
		this.shaleMockObjects.setUp(request);

		this.innerSetUp();
	}

	@Override
	protected void postCreateHdivConfig(HDIVConfig config) {

		// Disable not supported features
		config.setConfidentiality(Boolean.FALSE);
		config.setAvoidCookiesConfidentiality(Boolean.TRUE);
		config.setAvoidCookiesIntegrity(Boolean.TRUE);
	}

	abstract protected void innerSetUp() throws Exception;

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		this.shaleMockObjects.tearDown();
	}

}
