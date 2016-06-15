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
package org.hdiv.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.init.ServletContextInitializer;
import org.hdiv.init.SessionInitializer;
import org.hdiv.util.HDIVUtil;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * HDIV initialization listener.
 * 
 * @version 1.1.1
 * @author Roberto Velasco
 * @author Gorka Vicente
 * @author Gotzon Illarramendi
 */
public class InitListener implements ServletContextListener, HttpSessionListener {

	/**
	 * Commons Logging instance.
	 */
	private static final Log log = LogFactory.getLog(InitListener.class);

	/**
	 * Has servlet context been initialized?
	 */
	protected boolean servletContextInitialized = false;

	/**
	 * Initializer for the {@link ServletContext}
	 */
	protected ServletContextInitializer servletContextInitializer;

	/**
	 * Initializer for the {@link HttpSession}
	 */
	protected SessionInitializer sessionInitializer;

	/**
	 * Initialize {@link ServletContext} scoped objects.
	 * 
	 * @param servletContextEvent ServletContext creation event
	 * @since HDIV 2.1.0
	 */
	public void contextInitialized(final ServletContextEvent servletContextEvent) {

		ServletContext servletContext = servletContextEvent.getServletContext();
		WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(servletContext);

		if (wac != null) {
			initServletContext(servletContext);
		}
		else {
			if (log.isWarnEnabled()) {
				log.warn(
						"Hdiv's InitListener is registered before Spring's ContextLoaderListener. It must be after ContextLoaderListener.");
			}
		}
	}

	/**
	 * Executed at {@link ServletContext} destroy.
	 * 
	 * @param servletContextEvent ServletContext destroy event
	 * @since HDIV 2.1.0
	 */
	public void contextDestroyed(final ServletContextEvent servletContextEvent) {

		if (servletContextInitializer != null) {
			servletContextInitializer.destroyServletContext(servletContextEvent.getServletContext());
		}
	}

	/**
	 * Initialize {@link ServletContext} scoped objects.
	 * 
	 * @param servletContext ServletContext instance
	 */
	protected void initServletContext(final ServletContext servletContext) {

		WebApplicationContext wac = HDIVUtil.findWebApplicationContext(servletContext);

		// Get initializer instances
		servletContextInitializer = wac.getBean(ServletContextInitializer.class);
		sessionInitializer = wac.getBean(SessionInitializer.class);

		servletContextInitializer.initializeServletContext(servletContext);

		servletContextInitialized = true;
	}

	/**
	 * Initialize {@link HttpSession} scoped objects.
	 * 
	 * @param httpSessionEvent session created event
	 */
	public void sessionCreated(final HttpSessionEvent httpSessionEvent) {

		ServletContext servletContext = httpSessionEvent.getSession().getServletContext();

		if (!servletContextInitialized) {
			initServletContext(servletContext);
		}

		sessionInitializer.initializeSession(httpSessionEvent.getSession());

	}

	/**
	 * Executed at {@link HttpSession} destroy.
	 * 
	 * @param httpSessionEvent HttpSession destroy event
	 */
	public void sessionDestroyed(final HttpSessionEvent httpSessionEvent) {
		if (sessionInitializer != null) {
			// Prevent error in development environment
			sessionInitializer.destroySession(httpSessionEvent.getSession());
		}
	}

}