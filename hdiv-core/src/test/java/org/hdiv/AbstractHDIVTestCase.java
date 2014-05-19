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
package org.hdiv;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.filter.RequestInitializer;
import org.hdiv.listener.InitListener;
import org.hdiv.util.HDIVUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * HDIV test parent class.
 * 
 * @author Gotzon Illarramendi
 */
public abstract class AbstractHDIVTestCase extends TestCase {

	private static Log log = LogFactory.getLog(AbstractHDIVTestCase.class);

	/**
	 * Spring Factory
	 */
	private ApplicationContext applicationContext = null;

	/**
	 * Hdiv config for this app.
	 */
	private HDIVConfig config;

	protected final void setUp() throws Exception {

		String[] files = { 
				"/org/hdiv/config/hdiv-core-applicationContext.xml", 
				"/org/hdiv/config/hdiv-config.xml",
				"/org/hdiv/config/hdiv-validations.xml", 
				"/org/hdiv/config/applicationContext-extra.xml" };

		// Servlet API mock
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/path/testAction.do");
		HttpSession httpSession = request.getSession();
		ServletContext servletContext = httpSession.getServletContext();
		HDIVUtil.setHttpServletRequest(request);

		// Init Spring Context
		XmlWebApplicationContext webApplicationContext = new XmlWebApplicationContext();
		webApplicationContext.setServletContext(servletContext);
		webApplicationContext.setConfigLocations(files);
		servletContext
				.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);
		// Create beans
		webApplicationContext.refresh();

		this.applicationContext = webApplicationContext;

		// Initialize config
		this.config = (HDIVConfig) this.applicationContext.getBean(HDIVConfig.class);
		// Configure for testing
		this.postCreateHdivConfig(this.config);

		InitListener initListener = new InitListener();
		// Initialize ServletContext
		ServletContextEvent servletContextEvent = new ServletContextEvent(servletContext);
		initListener.contextInitialized(servletContextEvent);
		// Initialize HttpSession
		HttpSessionEvent httpSessionEvent = new HttpSessionEvent(httpSession);
		initListener.sessionCreated(httpSessionEvent);

		// Init Request scoped data
		RequestInitializer requestInitializer = this.applicationContext.getBean(RequestInitializer.class);
		requestInitializer.initRequest(request);
		DataComposerFactory dataComposerFactory = (DataComposerFactory) this.applicationContext
				.getBean(DataComposerFactory.class);
		IDataComposer dataComposer = dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		if (log.isDebugEnabled()) {
			log.debug("Hdiv test context initialized");
		}

		onSetUp();
	}

	protected abstract void onSetUp() throws Exception;

	/**
	 * Hook method for {@link HDIVConfig} customization
	 * 
	 * @param config
	 */
	protected void postCreateHdivConfig(HDIVConfig config) {

	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * @return the config
	 */
	public HDIVConfig getConfig() {
		return config;
	}

}