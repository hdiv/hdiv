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
package org.hdiv.filter;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

public class AsyncRequestWrapper extends RequestWrapper {

	/**
	 * Commons Logging instance.
	 */
	private static final Log log = LogFactory.getLog(AsyncRequestWrapper.class);

	/**
	 * True if this is an Async request.
	 */
	protected boolean isAsyncRequest = false;

	public AsyncRequestWrapper(final HttpServletRequest servletRequest) {

		super(servletRequest);

		Assert.notNull(servletRequest);

		if (log.isDebugEnabled()) {
			log.debug("New RequestWrapper instance.");
		}
	}

	@Override
	public AsyncContext startAsync() {
		isAsyncRequest = true;
		return super.startAsync();
	}

	@Override
	public AsyncContext startAsync(final ServletRequest servletRequest, final ServletResponse servletResponse) {
		isAsyncRequest = true;
		return super.startAsync(servletRequest, servletResponse);
	}

	/**
	 * @return the isAsyncRequest
	 */
	public boolean isAsyncRequest() {
		return isAsyncRequest;
	}

}
