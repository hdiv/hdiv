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
package org.hdiv.state;

public class HDIVState {
	private final int pageId;

	private final int stateId;

	private final String token;

	public HDIVState(final int pageId, final int stateId, final String token) {
		this.pageId = pageId;
		this.stateId = stateId;
		this.token = token;
	}

	public int getPageId() {
		return pageId;
	}

	public int getStateId() {
		return stateId;
	}

	public String getToken() {
		return token;
	}
}
