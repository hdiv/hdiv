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
package org.hdiv.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.application.IApplication;
import org.hdiv.cipher.IKeyFactory;
import org.hdiv.cipher.Key;
import org.hdiv.config.HDIVConfig;
import org.hdiv.idGenerator.PageIdGenerator;
import org.hdiv.urlProcessor.FormUrlProcessor;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ResourceBundleMessageSource;
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
	private static Log log = LogFactory.getLog(InitListener.class);

	/**
	 * Hdiv configuration for this app.
	 */
	private HDIVConfig config;

	/**
	 * Is servlet context Hdiv objects initialized?
	 */
	private boolean servletContextInitialized = false;

	/**
	 * Initialize servlet context objects.
	 * 
	 * @param servletContextEvent
	 *            servlet context created event
	 * @since HDIV 2.1.0
	 */
	public void contextInitialized(ServletContextEvent servletContextEvent) {

		ServletContext servletContext = servletContextEvent.getServletContext();
		WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(servletContext);

		if (wac != null) {
			this.initServletContext(servletContext);
		} else {
			if (log.isWarnEnabled()) {
				log.warn("Hdiv's InitListener is registered before Spring's ContextLoaderListener.");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet. ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent servletContextEvent) {

	}

	/**
	 * @see javax.servlet.http.HttpSessionListener#void (javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionDestroyed(HttpSessionEvent event) {

		if (log.isInfoEnabled()) {
			log.info("HDIV's session destroyed:" + event.getSession().getId());
		}
	}

	/**
	 * For each user session, a new cipher key is created if the cipher strategy has been chosen, and a new cache is
	 * created to store the data to be validated.
	 * 
	 * @see javax.servlet.http.HttpSessionListener#void (javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionCreated(HttpSessionEvent httpSessionEvent) {

		ServletContext servletContext = httpSessionEvent.getSession().getServletContext();

		if (!this.servletContextInitialized) {
			this.initServletContext(servletContext);
		}

		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

		this.initStrategies(wac, httpSessionEvent.getSession());
		this.initPageIdGenerator(wac, httpSessionEvent.getSession());
		this.initStateParameterNames(wac, httpSessionEvent.getSession());

		if (log.isInfoEnabled()) {
			log.info("HDIV's session created:" + httpSessionEvent.getSession().getId());
		}

	}

	/**
	 * Initialize ServletContext scoped objects.
	 * 
	 * @param servletContext
	 *            ServletContext instance
	 */
	protected void initServletContext(ServletContext servletContext) {

		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

		this.config = wac.getBean(HDIVConfig.class);

		// Init servlet context scoped objects
		HDIVUtil.setHDIVConfig(this.config, servletContext);

		IApplication application = wac.getBean(IApplication.class);
		HDIVUtil.setApplication(application, servletContext);

		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBeanClassLoader(wac.getClassLoader());
		messageSource.setBasename(Constants.MESSAGE_SOURCE_PATH);
		HDIVUtil.setMessageSource(messageSource, servletContext);

		LinkUrlProcessor linkUrlProcessor = wac.getBean(LinkUrlProcessor.class);
		HDIVUtil.setLinkUrlProcessor(linkUrlProcessor, servletContext);

		FormUrlProcessor formUrlProcessor = wac.getBean(FormUrlProcessor.class);
		HDIVUtil.setFormUrlProcessor(formUrlProcessor, servletContext);

		this.servletContextInitialized = true;
	}

	/**
	 * Strategies initialization.
	 * 
	 * @param context
	 *            application context
	 * @param httpSession
	 *            http session
	 */
	protected void initStrategies(ApplicationContext context, HttpSession httpSession) {

		if (this.config.getStrategy().equalsIgnoreCase("cipher")) {
			IKeyFactory keyFactory = context.getBean(IKeyFactory.class);
			// creating encryption key
			Key key = keyFactory.generateKey();
			httpSession.setAttribute(Constants.KEY_NAME, key);

		}
	}

	/**
	 * PageIdGenerator initialization.
	 * 
	 * @param context
	 *            application context
	 * @param httpSession
	 *            http session
	 */
	protected void initPageIdGenerator(ApplicationContext context, HttpSession httpSession) {

		// Obtain new instance of PageIdGenerator
		PageIdGenerator pageIdGenerator = context.getBean(PageIdGenerator.class);
		httpSession.setAttribute(Constants.PAGE_ID_GENERATOR_NAME, pageIdGenerator);
	}

	/**
	 * State parameter names initialization.
	 * 
	 * @param context
	 *            application context
	 * @param httpSession
	 *            http session
	 * @since HDIV 1.1
	 */
	protected void initStateParameterNames(ApplicationContext context, HttpSession httpSession) {

		String hdivParameterName = null;
		String modifyHdivStateParameterName = null;

		if (this.config.isRandomName()) {
			hdivParameterName = HDIVUtil.createRandomToken(Integer.MAX_VALUE);
			modifyHdivStateParameterName = HDIVUtil.createRandomToken(Integer.MAX_VALUE);
		} else {
			hdivParameterName = this.config.getStateParameterName();
			modifyHdivStateParameterName = this.config.getModifyStateParameterName();
		}

		httpSession.setAttribute(Constants.HDIV_PARAMETER, hdivParameterName);
		httpSession.setAttribute(Constants.MODIFY_STATE_HDIV_PARAMETER, modifyHdivStateParameterName);
	}

}