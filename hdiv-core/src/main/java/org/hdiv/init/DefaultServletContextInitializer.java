/**
 * Copyright 2005-2015 hdiv.org
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
package org.hdiv.init;

import javax.servlet.ServletContext;

import org.hdiv.application.IApplication;
import org.hdiv.config.HDIVConfig;
import org.hdiv.urlProcessor.FormUrlProcessor;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.util.Assert;

/**
 * Default implementation of {@link ServletContextInitializer}.
 * <p>
 * Initializes and destroys {@link ServletContext} scoped attributes.
 * 
 * @since 2.1.10
 */
public class DefaultServletContextInitializer implements ServletContextInitializer, ApplicationContextAware {

	protected HDIVConfig config;

	protected ApplicationContext applicationContext;

	protected IApplication application;

	protected LinkUrlProcessor linkUrlProcessor;

	protected FormUrlProcessor formUrlProcessor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.init.ServletContextInitializer#initializeServletContext(javax.servlet.ServletContext)
	 */
	public void initializeServletContext(ServletContext servletContext) {

		Assert.notNull(this.config);
		Assert.notNull(this.application);
		Assert.notNull(this.linkUrlProcessor);
		Assert.notNull(this.formUrlProcessor);
		Assert.notNull(servletContext);

		// Init servlet context scoped objects
		HDIVUtil.setHDIVConfig(this.config, servletContext);
		HDIVUtil.setApplication(this.application, servletContext);
		HDIVUtil.setLinkUrlProcessor(this.linkUrlProcessor, servletContext);
		HDIVUtil.setFormUrlProcessor(this.formUrlProcessor, servletContext);

		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBeanClassLoader(this.applicationContext.getClassLoader());
		messageSource.setBasename(Constants.MESSAGE_SOURCE_PATH);
		HDIVUtil.setMessageSource(messageSource, servletContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.init.ServletContextInitializer#destroyServletContext(javax.servlet.ServletContext)
	 */
	public void destroyServletContext(ServletContext servletContext) {

	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

		this.applicationContext = applicationContext;
	}

	/**
	 * @param config
	 *            the config to set
	 */
	public void setConfig(HDIVConfig config) {
		this.config = config;
	}

	/**
	 * @param application
	 *            the application to set
	 */
	public void setApplication(IApplication application) {
		this.application = application;
	}

	/**
	 * @param linkUrlProcessor
	 *            the linkUrlProcessor to set
	 */
	public void setLinkUrlProcessor(LinkUrlProcessor linkUrlProcessor) {
		this.linkUrlProcessor = linkUrlProcessor;
	}

	/**
	 * @param formUrlProcessor
	 *            the formUrlProcessor to set
	 */
	public void setFormUrlProcessor(FormUrlProcessor formUrlProcessor) {
		this.formUrlProcessor = formUrlProcessor;
	}

}