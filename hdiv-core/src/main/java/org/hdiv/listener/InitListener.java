/**
 * Copyright 2005-2011 hdiv.org
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
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.cipher.IKeyFactory;
import org.hdiv.cipher.Key;
import org.hdiv.config.HDIVConfig;
import org.hdiv.idGenerator.PageIdGenerator;
import org.hdiv.session.IStateCache;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;
import org.springframework.context.ApplicationContext;
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
public class InitListener implements HttpSessionListener {

	/**
	 * Commons Logging instance.
	 */
	private static Log log = LogFactory.getLog(InitListener.class);

	/**
	 * Hdiv configuration for this app.
	 */
	private HDIVConfig config;
	
	/**
	 * @see javax.servlet.http.HttpSessionListener#void
	 *      (javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionDestroyed(HttpSessionEvent event) {
		
		if (log.isInfoEnabled()) {
			log.info("HDIV's session destroyed:" + event.getSession().getId());
		}		
	}

	/**
	 * For each user session, a new cipher key is created if the cipher strategy has
	 * been chosen, and a new cache is created to store the data to be validated.
	 * 
	 * @see javax.servlet.http.HttpSessionListener#void
	 *      (javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionCreated(HttpSessionEvent httpSessionEvent) {
		
		ServletContext servletContext = httpSessionEvent.getSession().getServletContext();
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);		
		
		config = (HDIVConfig)wac.getBean("config");
		
		this.initStrategies(wac, httpSessionEvent.getSession());
		this.initCache(wac, httpSessionEvent.getSession());
		this.initPageIdGenerator(wac, httpSessionEvent.getSession());
		this.initHDIVState(wac, httpSessionEvent.getSession());		
		
		if (log.isInfoEnabled()) {
			log.info("HDIV's session created:" + httpSessionEvent.getSession().getId());
		}
		
	}

	/**
	 * Strategies initialization.
	 * @param context application context
	 * @param httpSessionEvent http session event
	 */
	public void initStrategies(ApplicationContext context, HttpSession httpSession) {
	
		if (this.config.getStrategy().equalsIgnoreCase("cipher")) {
			IKeyFactory keyFactory = (IKeyFactory) context.getBean("keyFactory");
			// creating encryption key
			Key key = keyFactory.generateKeyWithDefaultValues();			
			String keyName = (String) context.getBean("keyName");
			
			httpSession.setAttribute((keyName == null) ? Constants.KEY_NAME : keyName, key);									
		
		} else {
			// @since HDIV 1.1
			httpSession.setAttribute(Constants.STATE_SUFFIX, String.valueOf(System.currentTimeMillis()));
		}		
	}
	
	/**
	 * Cache initialization.
	 * @param context application context
	 * @param httpSessionEvent http session event
	 */	
	public void initCache(ApplicationContext context, HttpSession httpSession) {
		
		IStateCache cache = (IStateCache) context.getBean("cache");
		String cacheName = (String) context.getBean("cacheName");		
		httpSession.setAttribute((cacheName == null) ? Constants.CACHE_NAME : cacheName, cache);
	}
	
	/**
	 * PageIdGenerator initialization.
	 * @param context application context
	 * @param httpSessionEvent http session event
	 */
	public void initPageIdGenerator(ApplicationContext context, HttpSession httpSession) {
		
		String pageIdGeneratorName = (String) context.getBean("pageIdGeneratorName");
		PageIdGenerator pageIdGenerator = (PageIdGenerator) context.getBean("pageIdGenerator");//Conseguir una nueva instancia de PageIdGenerator
		httpSession.setAttribute((pageIdGeneratorName == null) ? Constants.PAGE_ID_GENERATOR_NAME : pageIdGeneratorName, pageIdGenerator);
	}
	
	/**
	 * HDIV state parameter initialization.
	 * @param context application context
	 * @param httpSessionEvent http session event
	 * @since HDIV 1.1
	 */	
	public void initHDIVState(ApplicationContext context, HttpSession httpSession) {
	
		String hdivParameterName = null;
		
		Boolean isRandomName = Boolean.valueOf(this.config.isRandomName());
		if (Boolean.TRUE.equals(isRandomName)) {
			hdivParameterName = HDIVUtil.createRandomToken(Integer.MAX_VALUE);	
		} else {
			hdivParameterName = (String) context.getBean("hdivParameter");	
		}		
		
		httpSession.setAttribute(Constants.HDIV_PARAMETER, hdivParameterName);
	}

	/**
	 * @return the config
	 */
	public HDIVConfig getConfig() {
		return config;
	}

	/**
	 * @param config the config to set
	 */
	public void setConfig(HDIVConfig config) {
		this.config = config;
	}
	
}