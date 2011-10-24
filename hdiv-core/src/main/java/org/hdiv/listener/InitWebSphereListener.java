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
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.cipher.IKeyFactory;
import org.hdiv.cipher.Key;
import org.hdiv.session.IStateCache;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * HDIV listener for WebSphere environment.
 * 
 * @version 1.1.1
 * @author Roberto Velasco
 * @author Gorka Vicente
 */
public class InitWebSphereListener implements ServletContextListener,
		ServletContextAttributeListener, HttpSessionListener, HttpSessionAttributeListener {

	/**
	 * Commons Logging instance.
	 */
	private static Log log = LogFactory.getLog(InitWebSphereListener.class);
	

	/**
	 * @see javax.servlet.http.HttpSessionAttributeListener#void
	 *      (javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void attributeAdded(HttpSessionBindingEvent arg0) {
	}

	/**
	 * @see javax.servlet.http.HttpSessionAttributeListener#void
	 *      (javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void attributeReplaced(HttpSessionBindingEvent arg0) {
	}

	/**
	 * @see javax.servlet.http.HttpSessionAttributeListener#void
	 *      (javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void attributeRemoved(HttpSessionBindingEvent arg0) {
	}

	/**
	 * @see javax.servlet.ServletContextListener#void
	 *      (javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	/**
	 * @see javax.servlet.http.HttpSessionListener#void
	 *      (javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionDestroyed(HttpSessionEvent arg0) {
		
		HDIVUtil.resetLocalData();
		if (log.isInfoEnabled()) {
			log.info("HDIV's session destroyed:" + arg0.getSession().getId());
		}
	}

	/**
	 * @see javax.servlet.ServletContextAttributeListener#void
	 *      (javax.servlet.ServletContextAttributeEvent)
	 */
	public void attributeAdded(ServletContextAttributeEvent arg0) {
	}

	/**
	 * @see javax.servlet.ServletContextAttributeListener#void
	 *      (javax.servlet.ServletContextAttributeEvent)
	 */
	public void attributeReplaced(ServletContextAttributeEvent arg0) {
	}

	/**
	 * @see javax.servlet.ServletContextAttributeListener#void
	 *      (javax.servlet.ServletContextAttributeEvent)
	 */
	public void attributeRemoved(ServletContextAttributeEvent arg0) {
	}

	/**
	 * @see javax.servlet.ServletContextListener#void
	 *      (javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0) {

//		HDIVUtil.setServletContext(arg0.getServletContext());
	}

	/**
	 * For each user session, a new cipher key is created if the cipher strategy has
	 * been chosen, and a new cache is created to store the data to be validated.
	 * 
	 * @see javax.servlet.http.HttpSessionListener#void
	 *      (javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionCreated(HttpSessionEvent arg0) {

		ServletContext servletContext = arg0.getSession().getServletContext();
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

		this.initStrategies(wac, arg0);
		this.initCache(wac, arg0);
		this.initHDIVState(wac, arg0);		
				
//		HDIVUtil.setHttpSession(arg0.getSession());	
		
		if (log.isInfoEnabled()) {
			log.info("HDIV's session created:" + arg0.getSession().getId());
		}
		
	}

	/**
	 * Strategies initialization.
	 * @param wac web application context
	 * @param httpSessionEvent http session event
	 */
	private void initStrategies(WebApplicationContext wac, HttpSessionEvent httpSessionEvent) {
	
		String strategy = (String) wac.getBean("strategy");
		
		if (strategy.equalsIgnoreCase("cipher")) {
			IKeyFactory keyFactory = (IKeyFactory) wac.getBean("keyFactory");
			// creating encryption key
			Key key = keyFactory.generateKeyWithDefaultValues();			
			String keyName = (String) wac.getBean("keyName");
			
			httpSessionEvent.getSession().setAttribute((keyName == null) ? Constants.KEY_NAME : keyName, key);									
		
		} else {
			// @since HDIV 1.1
			httpSessionEvent.getSession().setAttribute(Constants.STATE_SUFFIX, String.valueOf(System.currentTimeMillis()));
		}		
	}
	
	/**
	 * Cache initialization.
	 * @param wac web application context
	 * @param httpSessionEvent http session event
	 */	
	private void initCache(WebApplicationContext wac, HttpSessionEvent httpSessionEvent) {
		
		IStateCache cache = (IStateCache) wac.getBean("cache");
		String cacheName = (String) wac.getBean("cacheName");		
		httpSessionEvent.getSession().setAttribute((cacheName == null) ? Constants.CACHE_NAME : cacheName, cache);
	}
	
	/**
	 * HDIV state parameter initialization.
	 * @param wac web application context
	 * @param httpSessionEvent http session event
	 * @since HDIV 1.1
	 */	
	private void initHDIVState(WebApplicationContext wac, HttpSessionEvent httpSessionEvent) {
	
		String hdivParameterName = null;
				
		Boolean isRandomName = (Boolean) wac.getBean("randomName");
		if (Boolean.TRUE.equals(isRandomName)) {
			hdivParameterName = HDIVUtil.createRandomToken(Integer.MAX_VALUE);	
		} else {
			hdivParameterName = (String) wac.getBean("hdivParameter");	
		}		
		
		httpSessionEvent.getSession().setAttribute(Constants.HDIV_PARAMETER, hdivParameterName);
	}
	
}