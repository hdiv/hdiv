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
package org.hdiv;

import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.context.RequestContext;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.init.RequestInitializer;
import org.hdiv.listener.InitListener;
import org.hdiv.util.HDIVUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import junit.framework.TestCase;

/**
 * HDIV test parent class.
 * 
 * @author Gotzon Illarramendi
 */
public abstract class AbstractHDIVTestCase extends TestCase {

	private static final Log log = LogFactory.getLog(AbstractHDIVTestCase.class);

	/**
	 * Pattern to check if the memory strategy is being used
	 */
	protected static final String MEMORY_PATTERN = "([0-9]+-){2}[A-Za-z0-9]+";

	/**
	 * Compiled MEMORY_PATTERN
	 */
	protected Pattern memoryPattern = Pattern.compile(MEMORY_PATTERN);

	/**
	 * Spring Factory
	 */
	private ApplicationContext applicationContext = null;

	/**
	 * Hdiv config for this app.
	 */
	private HDIVConfig config;

	private InitListener initListener;

	private MockHttpServletRequest mockRequest;

	private MockHttpServletResponse mockResponse;

	private RequestContext requestContext;

	// @formatter:off
	private String[] files = { 
			"/org/hdiv/config/hdiv-core-applicationContext.xml",
			"/org/hdiv/config/hdiv-config.xml",
			"/org/hdiv/config/hdiv-validations.xml",
			"/org/hdiv/config/applicationContext-extra.xml" };
	// @formatter:on

	@Override
	protected final void setUp() throws Exception {

		preSetUp();

		// Servlet API mock
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/path/testAction.do");
		HttpServletResponse response = new MockHttpServletResponse();
		HttpSession httpSession = request.getSession();
		ServletContext servletContext = httpSession.getServletContext();
		requestContext = new RequestContext(request, response);
		// Store objects for teardown cleanup
		mockRequest = request;
		mockResponse = (MockHttpServletResponse) response;

		// Init Spring Context
		XmlWebApplicationContext webApplicationContext = new XmlWebApplicationContext();
		webApplicationContext.setServletContext(servletContext);
		webApplicationContext.setConfigLocations(files);

		servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);

		// Create beans
		webApplicationContext.refresh();

		applicationContext = webApplicationContext;

		// Initialize config
		config = applicationContext.getBean(HDIVConfig.class);
		// Configure for testing
		postCreateHdivConfig(config);

		initListener = new InitListener();
		// Initialize ServletContext
		ServletContextEvent servletContextEvent = new ServletContextEvent(servletContext);
		initListener.contextInitialized(servletContextEvent);
		// Initialize HttpSession
		HttpSessionEvent httpSessionEvent = new HttpSessionEvent(httpSession);
		initListener.sessionCreated(httpSessionEvent);

		// Init Request scoped data
		RequestInitializer requestInitializer = applicationContext.getBean(RequestInitializer.class);
		requestInitializer.initRequest(request, response);

		DataComposerFactory dataComposerFactory = applicationContext.getBean(DataComposerFactory.class);

		IDataComposer dataComposer = dataComposerFactory.newInstance(request);
		HDIVUtil.setDataComposer(dataComposer, request);

		if (log.isDebugEnabled()) {
			log.debug("Hdiv test context initialized");
		}

		onSetUp();
	}

	/**
	 * Hook method for test initialization
	 * 
	 * @throws Exception
	 */
	protected abstract void onSetUp() throws Exception;

	/**
	 * Hook method for test pre-initialization
	 * 
	 * @throws Exception
	 */
	protected void preSetUp() throws Exception {

	}

	/**
	 * Hook method for test end
	 * 
	 * @throws Exception
	 */
	protected void onTearDown() throws Exception {

	}

	/**
	 * Hook method for test pre-end
	 * 
	 * @throws Exception
	 */
	protected void preTearDown() throws Exception {

	}

	@Override
	protected void tearDown() throws Exception {

		preTearDown();

		RequestInitializer requestInitializer = applicationContext.getBean(RequestInitializer.class);
		requestInitializer.endRequest(mockRequest, mockResponse);

		// Destroy HttpSession
		HttpSessionEvent httpSessionEvent = new HttpSessionEvent(mockRequest.getSession());
		initListener.sessionDestroyed(httpSessionEvent);
		// Destroy ServletContext
		ServletContextEvent servletContextEvent = new ServletContextEvent(mockRequest.getSession().getServletContext());
		initListener.contextDestroyed(servletContextEvent);

		((ConfigurableApplicationContext) applicationContext).close();

		onTearDown();
	}

	/**
	 * Hook method for {@link HDIVConfig} customization
	 * 
	 * @param config
	 */
	protected void postCreateHdivConfig(final HDIVConfig config) {

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

	public RequestContext getRequestContext() {
		return requestContext;
	}

	public MockHttpServletRequest getMockRequest() {
		return mockRequest;
	}

	public MockHttpServletResponse getMockResponse() {
		return mockResponse;
	}

	/**
	 * Return the configuration files
	 * 
	 * @return files configuration files
	 */
	protected String[] getFiles() {
		return files;
	}

	/**
	 * Set the configuration files
	 * 
	 * @param files configuration files
	 */
	protected void setFiles(final String[] files) {
		this.files = files;
	}

	protected String getState(final String url) {
		return getParameter(url, HDIVUtil.getHdivStateParameterName(getMockRequest()));
	}

	protected String getModifyState(final String url) {
		return getParameter(url, HDIVUtil.getModifyHdivStateParameterName(getMockRequest()));
	}

	protected String getParameter(final String url, final String parameter) {
		String value = parameter + "=";
		if (url.indexOf(value) == -1) {
			return null;
		}
		int finish = url.length();
		if (url.indexOf('&', url.indexOf(value)) != -1) {
			finish = url.indexOf('&', url.indexOf(value));
		}
		return url.substring(url.indexOf(value) + value.length(), finish);
	}
}