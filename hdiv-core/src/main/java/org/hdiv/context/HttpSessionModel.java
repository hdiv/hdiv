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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpSessionModel implements SessionModel {

	private static final Logger log = LoggerFactory.getLogger(HttpSessionModel.class);

	private final HttpSession session;

	public HttpSessionModel(final HttpSession session) {
		this.session = session;
	}

	public Object getAttribute(final String name) {
		if (session != null) {
			try {
				return session.getAttribute(name);
			}
			catch (IllegalStateException e) {
				log.debug("It was not possible to get an attribute from HttpSession. Msg: {}", e.getMessage());
			}
		}
		return null;
	}

	public void removeAttribute(final String name) {
		if (session != null) {
			try {
				session.removeAttribute(name);
			}
			catch (IllegalStateException e) {
				log.debug("It was not possible to remove an attribute from HttpSession. Msg: {}", e.getMessage());
			}
		}
	}

	public void setAttribute(final String name, final Object cache) {
		if (session != null) {
			try {
				session.setAttribute(name, cache);
			}
			catch (IllegalStateException e) {
				log.debug("It was not possible to set an attribute from HttpSession. Msg: {}", e.getMessage());
			}
		}
	}

}
