package org.hdiv;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.application.IApplication;
import org.hdiv.config.HDIVConfig;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.listener.InitListener;
import org.hdiv.util.HDIVUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mock.web.MockHttpServletRequest;

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
				"/org/hdiv/config/applicationContext-test.xml"
		};

		if(this.applicationContext==null){
			this.applicationContext = new ClassPathXmlApplicationContext(files);
		}
		
		//Servlet API mock 
		HttpServletRequest request = (MockHttpServletRequest) this.applicationContext.getBean("mockRequest");
		HttpSession httpSession = request.getSession();
		ServletContext servletContext = httpSession.getServletContext();
		HDIVUtil.setHttpServletRequest(request);

		//Initialize config
		this.config = (HDIVConfig) this.applicationContext.getBean("config");
		
		//Initialize HttpSession
		InitListener initListener = new InitListener();
		initListener.setConfig(this.config);
		initListener.initStrategies(this.applicationContext, httpSession);
		initListener.initCache(this.applicationContext, httpSession);
		initListener.initPageIdGenerator(this.applicationContext, httpSession);
		initListener.initHDIVState(this.applicationContext, httpSession);
		
		//Initialize HDIVConfig in ServletContext
		HDIVUtil.setHDIVConfig(this.config, servletContext);
		
		//Initialize IApplication in ServletContext
		IApplication application = (IApplication) this.applicationContext.getBean("application");
		HDIVUtil.setApplication(application, servletContext);
		
		//Initialize MessageSource in ServletContext
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBeanClassLoader(this.applicationContext.getClassLoader());
		String messageSourcePath = (String)this.applicationContext.getBean("messageSourcePath");
		messageSource.setBasename(messageSourcePath);
		HDIVUtil.setMessageSource(messageSource, servletContext);
		
		//Initialize the IDataComposer
		DataComposerFactory dataComposerFactory = (DataComposerFactory) this.applicationContext
				.getBean("dataComposerFactory");
		IDataComposer dataComposer = dataComposerFactory.newInstance();
		HDIVUtil.setDataComposer(dataComposer, request);

		onSetUp();
	}
	
	protected abstract void onSetUp() throws Exception;

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
