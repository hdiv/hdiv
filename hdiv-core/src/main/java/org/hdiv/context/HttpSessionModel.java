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
package org.hdiv.context;

import javax.servlet.http.HttpSession;

import org.hdiv.session.SessionModel;

public class HttpSessionModel implements SessionModel {

	private final HttpSession session;

	public HttpSessionModel(final HttpSession session) {
		this.session = session;
	}

	public Object getAttribute(final String name) {
		if (session != null) {
			return session.getAttribute(name);
		}
		return null;
	}

	public void removeAttribute(final String name) {
		if (session != null) {
			session.removeAttribute(name);
		}
	}

	public void setAttribute(final String name, final Object cache) {
		if (session != null) {
			session.setAttribute(name, cache);
		}
	}

}
