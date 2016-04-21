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

import java.net.URL;
import java.net.URLClassLoader;

import javax.faces.FactoryFinder;
import javax.faces.application.ApplicationFactory;
import javax.faces.component.UIViewRoot;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.render.RenderKitFactory;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shale.test.mock.MockApplication;
import org.apache.shale.test.mock.MockExternalContext;
import org.apache.shale.test.mock.MockFacesContext;
import org.apache.shale.test.mock.MockFacesContextFactory;
import org.apache.shale.test.mock.MockHttpServletRequest;
import org.apache.shale.test.mock.MockHttpServletResponse;
import org.apache.shale.test.mock.MockHttpSession;
import org.apache.shale.test.mock.MockLifecycle;
import org.apache.shale.test.mock.MockLifecycleFactory;
import org.apache.shale.test.mock.MockRenderKit;
import org.apache.shale.test.mock.MockServletConfig;
import org.apache.shale.test.mock.MockServletContext;

/**
 * Great utility class from: http://www.jroller.com/RickHigh/entry/shale_mock_objects_testng_and
 */
public class ShaleMockObjects {

	public void setUp(HttpServletRequest request) throws Exception {

		// Set up a new thread context class loader
		threadContextClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(new URLClassLoader(new URL[0], this.getClass().getClassLoader()));

		// Set up Servlet API Objects
		servletContext = request.getSession().getServletContext();// new MockServletContext();
		config = new MockServletConfig(servletContext);
		session = request.getSession();// new MockHttpSession();
		// session.setServletContext(servletContext);
		this.request = request;// new MockHttpServletRequest(session);
		// request.setServletContext(servletContext);
		response = new MockHttpServletResponse();

		// Set up JSF API Objects
		FactoryFinder.releaseFactories();
		FactoryFinder
				.setFactory(FactoryFinder.APPLICATION_FACTORY, "org.apache.shale.test.mock.MockApplicationFactory");
		FactoryFinder.setFactory(FactoryFinder.FACES_CONTEXT_FACTORY,
				"org.apache.shale.test.mock.MockFacesContextFactory");
		FactoryFinder.setFactory(FactoryFinder.LIFECYCLE_FACTORY, "org.apache.shale.test.mock.MockLifecycleFactory");
		FactoryFinder.setFactory(FactoryFinder.RENDER_KIT_FACTORY, "org.apache.shale.test.mock.MockRenderKitFactory");

		externalContext = new MockExternalContext(servletContext, request, response);
		lifecycleFactory = (MockLifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
		lifecycle = (MockLifecycle) lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
		facesContextFactory = (MockFacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
		facesContext = (MockFacesContext) facesContextFactory.getFacesContext(servletContext, request, response,
				lifecycle);
		externalContext = (MockExternalContext) facesContext.getExternalContext();
		UIViewRoot root = new UIViewRoot();
		root.setViewId("/viewId");
		root.setRenderKitId(RenderKitFactory.HTML_BASIC_RENDER_KIT);
		facesContext.setViewRoot(root);
		ApplicationFactory applicationFactory = (ApplicationFactory) FactoryFinder
				.getFactory(FactoryFinder.APPLICATION_FACTORY);
		application = new MockApplication();
		applicationFactory.setApplication(application);
		facesContext.setApplication(application);
		RenderKitFactory renderKitFactory = (RenderKitFactory) FactoryFinder
				.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
		renderKit = new MockRenderKit();
		renderKitFactory.addRenderKit(RenderKitFactory.HTML_BASIC_RENDER_KIT, renderKit);

	}

	public void tearDown() throws Exception {

		application = null;
		config = null;
		externalContext = null;
		facesContext.release();
		facesContext = null;
		lifecycle = null;
		lifecycleFactory = null;
		renderKit = null;
		request = null;
		response = null;
		servletContext = null;
		session = null;
		FactoryFinder.releaseFactories();

		Thread.currentThread().setContextClassLoader(threadContextClassLoader);
		threadContextClassLoader = null;

	}

	// ------------------------------------------------------ Instance Variables

	// Mock object instances for our tests
	protected MockApplication application = null;

	protected MockServletConfig config = null;

	protected MockExternalContext externalContext = null;

	protected MockFacesContext facesContext = null;

	protected MockFacesContextFactory facesContextFactory = null;

	protected MockLifecycle lifecycle = null;

	protected MockLifecycleFactory lifecycleFactory = null;

	protected MockRenderKit renderKit = null;

	protected HttpServletRequest request = null;

	protected MockHttpServletResponse response = null;

	protected ServletContext servletContext = null;

	protected HttpSession session = null;

	// Thread context class loader saved and restored after each test
	private ClassLoader threadContextClassLoader = null;

	public MockApplication getApplication() {
		return application;
	}

	public void setApplication(MockApplication application) {
		this.application = application;
	}

	public MockServletConfig getConfig() {
		return config;
	}

	public void setConfig(MockServletConfig config) {
		this.config = config;
	}

	public MockExternalContext getExternalContext() {
		return externalContext;
	}

	public void setExternalContext(MockExternalContext externalContext) {
		this.externalContext = externalContext;
	}

	public MockFacesContext getFacesContext() {
		return facesContext;
	}

	public void setFacesContext(MockFacesContext facesContext) {
		this.facesContext = facesContext;
	}

	public MockFacesContextFactory getFacesContextFactory() {
		return facesContextFactory;
	}

	public void setFacesContextFactory(MockFacesContextFactory facesContextFactory) {
		this.facesContextFactory = facesContextFactory;
	}

	public MockLifecycle getLifecycle() {
		return lifecycle;
	}

	public void setLifecycle(MockLifecycle lifecycle) {
		this.lifecycle = lifecycle;
	}

	public MockLifecycleFactory getLifecycleFactory() {
		return lifecycleFactory;
	}

	public void setLifecycleFactory(MockLifecycleFactory lifecycleFactory) {
		this.lifecycleFactory = lifecycleFactory;
	}

	public MockRenderKit getRenderKit() {
		return renderKit;
	}

	public void setRenderKit(MockRenderKit renderKit) {
		this.renderKit = renderKit;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(MockHttpServletRequest request) {
		this.request = request;
	}

	public MockHttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(MockHttpServletResponse response) {
		this.response = response;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(MockServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public HttpSession getSession() {
		return session;
	}

	public void setSession(MockHttpSession session) {
		this.session = session;
	}

	public ClassLoader getThreadContextClassLoader() {
		return threadContextClassLoader;
	}

	public void setThreadContextClassLoader(ClassLoader threadContextClassLoader) {
		this.threadContextClassLoader = threadContextClassLoader;
	}

}
