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
package org.hdiv.state.scope;

import javax.servlet.ServletContext;

import org.hdiv.context.RequestContext;
import org.springframework.web.context.ServletContextAware;

/**
 * <p>
 * {@link StateScope} that stores states at application level.
 * </p>
 * <p>
 * States scoped to 'app' are stored at {@link ServletContext} and are shared by all the users of the application.
 * </p>
 *
 * @since 2.1.7
 */

public final class AppStateScope extends AbstractStateScope implements ServletContextAware {

	public AppStateScope() {
		super(StateScopeType.APP);
	}

	private static final String APP_STATE_CONTEXT_ATTR = ScopedStateCache.class.getCanonicalName();

	protected ServletContext servletContext;

	@Override
	public ScopedStateCache getStateCache(final RequestContext context) {
		ScopedStateCache cache = (ScopedStateCache) servletContext.getAttribute(APP_STATE_CONTEXT_ATTR);
		return cache;
	}

	@Override
	public void setStateCache(final RequestContext context, final ScopedStateCache cache) {
		servletContext.setAttribute(APP_STATE_CONTEXT_ATTR, cache);
	}

	public void setServletContext(final ServletContext servletContext) {
		this.servletContext = servletContext;
	}

}
