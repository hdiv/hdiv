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
package org.hdiv.exceptionHandler;

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.exception.StateValidationException;
import org.hdiv.util.HDIVUtil;

/**
 * <p>
 * ExceptionHandler that processes HDIV exceptions
 * </p>
 * <p>
 * Only for JSF 2.0+
 * </p>
 * 
 * @author Gotzon Illarramendi
 */
public class HDIVExceptionHandler extends ExceptionHandlerWrapper {

	private static Log log = LogFactory.getLog(HDIVExceptionHandler.class);

	/**
	 * Original ExceptionHandler
	 */
	private ExceptionHandler original;

	/**
	 * Constructor
	 * 
	 * @param original
	 *            original ExceptionHandler
	 */
	public HDIVExceptionHandler(ExceptionHandler original) {
		this.original = original;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.context.ExceptionHandlerWrapper#getWrapped()
	 */
	public ExceptionHandler getWrapped() {

		return this.original;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.context.ExceptionHandlerWrapper#handle()
	 */
	public void handle() throws FacesException {

		for (Iterator<ExceptionQueuedEvent> i = super.getUnhandledExceptionQueuedEvents().iterator(); i.hasNext();) {
			ExceptionQueuedEvent event = i.next();
			ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();
			Throwable t = context.getException();
			Throwable cause = this.getRootCause(t);
			if (cause instanceof StateValidationException) {
				StateValidationException hdivExc = (StateValidationException) cause;
				if (log.isDebugEnabled()) {
					log.debug("HDIV StateValidationException captured: " + hdivExc);
				}
				try {
					FacesContext fc = FacesContext.getCurrentInstance();
					NavigationHandler nav = fc.getApplication().getNavigationHandler();
					nav.handleNavigation(fc, null, this.getErrorPage(fc));
					fc.renderResponse();
				} finally {
					i.remove();
				}
			}
		}
		getWrapped().handle();
	}

	/**
	 * Obtains error page from HDIV configuration.
	 * 
	 * @param facesContext
	 *            active FacesContext
	 * @return error page
	 */
	private String getErrorPage(FacesContext facesContext) {

		ServletContext servletContext = (ServletContext) facesContext.getExternalContext().getContext();
		HDIVConfig config = HDIVUtil.getHDIVConfig(servletContext);

		String errorPage = config.getErrorPage();
		return errorPage;

	}
}
