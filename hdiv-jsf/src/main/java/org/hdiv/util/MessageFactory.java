/**
 * Copyright 2005-2012 hdiv.org
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
package org.hdiv.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * Utility class to create FacesMessage instances.
 * 
 * @author Gotzon Illarramendi
 */
public class MessageFactory {

	private MessageFactory() {
	}

	/**
	 * This version of getMessage() is used in the RI for localizing RI
	 * specific messages.
	 */
	public static FacesMessage getMessage(String messageId, Object params[]) {
		Locale locale = null;
		FacesContext context = FacesContext.getCurrentInstance();
		// context.getViewRoot() may not have been initialized at this point.
		if (context != null && context.getViewRoot() != null) {
			locale = context.getViewRoot().getLocale();
			if (locale == null) {
				locale = Locale.getDefault();
			}
		} else {
			locale = Locale.getDefault();
		}

		return getMessage(locale, messageId, params);
	}

	public static FacesMessage getMessage(Locale locale, String messageId, Object params[]) {

		String summary = null, detail = null, bundleName = null;
		ResourceBundle bundle = null;

		// see if we have a user-provided bundle
		if (null != (bundleName = getApplication().getMessageBundle())) {
			if (null != (bundle = ResourceBundle.getBundle(bundleName, locale, getCurrentLoader(bundleName)))) {
				// see if we have a hit
				try {
					summary = bundle.getString(messageId);
				} catch (MissingResourceException e) {
				}
			}
		}

		// we couldn't find a summary in the user-provided bundle
		if (null == summary) {
			// see if we have a summary in the app provided bundle
			bundle = ResourceBundle.getBundle(FacesMessage.FACES_MESSAGES, locale, getCurrentLoader(bundleName));
			if (null == bundle) {
				throw new NullPointerException();
			}
			// see if we have a hit
			try {
				summary = bundle.getString(messageId);
			} catch (MissingResourceException e) {
			}
		}

		// we couldn't find a summary anywhere!  Return null
		if (null == summary) {
			return null;
		}

		// At this point, we have a summary and a bundle.
		if (null == summary || null == bundle) {
			throw new NullPointerException(" summary " + summary + " bundle " + bundle);
		}
		summary = substituteParams(locale, summary, params);

		try {
			detail = substituteParams(locale, bundle.getString(messageId + "_detail"), params);
		} catch (MissingResourceException e) {
		}

		return (new FacesMessage(summary, detail));
	}

	private static String substituteParams(Locale locale, String msgtext, Object params[]) {
		String localizedStr = null;

		if (params == null || msgtext == null) {
			return msgtext;
		}
		StringBuffer b = new StringBuffer(100);
		MessageFormat mf = new MessageFormat(msgtext);
		if (locale != null) {
			mf.setLocale(locale);
			b.append(mf.format(params));
			localizedStr = b.toString();
		}
		return localizedStr;
	}

	protected static Application getApplication() {
		FacesContext context = FacesContext.getCurrentInstance();
		if (context != null) {
			return (context.getApplication());
		}
		ApplicationFactory afactory = (ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
		return (afactory.getApplication());
	}

	protected static ClassLoader getCurrentLoader(Object fallbackClass) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
			loader = fallbackClass.getClass().getClassLoader();
		}
		return loader;
	}

}
