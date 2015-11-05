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
package org.hdiv.context.jsf1.support;

import java.util.Iterator;

import javax.el.ELContext;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;

/**
 * <p>Provides a simple implementation of {@link FacesContext} that can
 * be subclassed by developers wishing to provide specialized behavior
 * to an existing {@link FacesContext} instance.  The default
 * implementation of all methods is to call through to the wrapped
 * {@link FacesContext} instance.</p>
 *
 * <p>Usage: extend this class and override {@link #getWrapped} to
 * return the instance being wrapping.</p>
 *
 */
public abstract class FacesContextWrapper extends FacesContext {

	// ----------------------------------------------- Methods from FacesWrapper

	/**
	 * @return the wrapped {@link FacesContext} instance
	 * @see javax.faces.FacesWrapper#getWrapped()
	 */
	public abstract FacesContext getWrapped();

	// ----------------------------------------------- Methods from FacesContext

	/**
	 * <p>The default behavior of this method is to
	 * call {@link FacesContext#getApplication()}
	 * on the wrapped {@link FacesContext} object.</p>
	 *
	 * @see javax.faces.context.FacesContext#getApplication()
	 */
	public Application getApplication() {
		return getWrapped().getApplication();
	}

	/**
	 * <p>The default behavior of this method is to
	 * call {@link javax.faces.context.FacesContext#getClientIdsWithMessages()}
	 * on the wrapped {@link FacesContext} object.</p>
	 *
	 * @see FacesContext#getClientIdsWithMessages()
	 */
	public Iterator getClientIdsWithMessages() {
		return getWrapped().getClientIdsWithMessages();
	}

	/**
	 * <p>The default behavior of this method is to
	 * call {@link FacesContext#getExternalContext()}
	 * on the wrapped {@link FacesContext} object.</p>
	 *
	 * @see javax.faces.context.FacesContext#getExternalContext()
	 */
	public ExternalContext getExternalContext() {
		return getWrapped().getExternalContext();
	}

	/**
	 * <p>The default behavior of this method is to
	 * call {@link FacesContext#getMaximumSeverity()}
	 * on the wrapped {@link FacesContext} object.</p>
	 *
	 * @see javax.faces.context.FacesContext#getMaximumSeverity()
	 */
	public FacesMessage.Severity getMaximumSeverity() {
		return getWrapped().getMaximumSeverity();
	}

	/**
	 * <p>The default behavior of this method is to
	 * call {@link FacesContext#getMessages()}
	 * on the wrapped {@link FacesContext} object.</p>
	 *
	 * @see javax.faces.context.FacesContext#getMessages()
	 */
	public Iterator getMessages() {
		return getWrapped().getMessages();
	}

	/**
	 * <p>The default behavior of this method is to
	 * call {@link FacesContext#getMessages(String)}
	 * on the wrapped {@link FacesContext} object.</p>
	 *
	 * @see javax.faces.context.FacesContext#getMessages(String)
	 */
	public Iterator getMessages(String clientId) {
		return getWrapped().getMessages(clientId);
	}

	/**
	 * <p>The default behavior of this method is to
	 * call {@link FacesContext#getRenderKit()}
	 * on the wrapped {@link FacesContext} object.</p>
	 *
	 * @see javax.faces.context.FacesContext#getRenderKit()
	 */
	public RenderKit getRenderKit() {
		return getWrapped().getRenderKit();
	}

	/**
	 * <p>The default behavior of this method is to
	 * call {@link FacesContext#getRenderResponse()}
	 * on the wrapped {@link FacesContext} object.</p>
	 *
	 * @see javax.faces.context.FacesContext#getRenderResponse()
	 */
	public boolean getRenderResponse() {
		return getWrapped().getRenderResponse();
	}

	/**
	 * <p>The default behavior of this method is to
	 * call {@link FacesContext#getResponseComplete()}
	 * on the wrapped {@link FacesContext} object.</p>
	 *
	 * @see javax.faces.context.FacesContext#getResponseComplete()
	 */
	public boolean getResponseComplete() {
		return getWrapped().getResponseComplete();
	}

	/**
	 * <p>The default behavior of this method is to
	 * call {@link FacesContext#getResponseStream()}
	 * on the wrapped {@link FacesContext} object.</p>
	 *
	 * @see javax.faces.context.FacesContext#getResponseStream()
	 */
	public ResponseStream getResponseStream() {
		return getWrapped().getResponseStream();
	}

	/**
	 * <p>The default behavior of this method is to
	 * call {@link FacesContext#setResponseStream(ResponseStream)}
	 * on the wrapped {@link FacesContext} object.</p>
	 *
	 * @see javax.faces.context.FacesContext#setResponseStream(ResponseStream)
	 */
	public void setResponseStream(ResponseStream responseStream) {
		getWrapped().setResponseStream(responseStream);
	}

	/**
	 * <p>The default behavior of this method is to
	 * call {@link FacesContext#getResponseWriter()}
	 * on the wrapped {@link FacesContext} object.</p>
	 *
	 * @see javax.faces.context.FacesContext#getResponseWriter()
	 */
	public ResponseWriter getResponseWriter() {
		return getWrapped().getResponseWriter();
	}

	/**
	 * <p>The default behavior of this method is to
	 * call {@link FacesContext#setResponseWriter(ResponseWriter)}
	 * on the wrapped {@link FacesContext} object.</p>
	 *
	 * @see javax.faces.context.FacesContext#setResponseWriter(ResponseWriter)
	 */
	public void setResponseWriter(ResponseWriter responseWriter) {
		getWrapped().setResponseWriter(responseWriter);
	}

	/**
	 * <p>The default behavior of this method is to
	 * call {@link FacesContext#getViewRoot()}
	 * on the wrapped {@link FacesContext} object.</p>
	 *
	 * @see javax.faces.context.FacesContext#getViewRoot()
	 */
	public UIViewRoot getViewRoot() {
		return getWrapped().getViewRoot();
	}

	/**
	 * <p>The default behavior of this method is to
	 * call {@link FacesContext#setViewRoot(UIViewRoot)}
	 * on the wrapped {@link FacesContext} object.</p>
	 *
	 * @see javax.faces.context.FacesContext#setViewRoot(UIViewRoot)
	 */
	public void setViewRoot(UIViewRoot root) {
		getWrapped().setViewRoot(root);
	}

	/**
	 * <p>The default behavior of this method is to
	 * call {@link FacesContext#addMessage(String, FacesMessage)}
	 * on the wrapped {@link FacesContext} object.</p>
	 *
	 * @see javax.faces.context.FacesContext#addMessage(String, FacesMessage)
	 */
	public void addMessage(String clientId, FacesMessage message) {
		getWrapped().addMessage(clientId, message);
	}

	/**
	 * <p>The default behavior of this method is to
	 * call {@link FacesContext#release()}
	 * on the wrapped {@link FacesContext} object.</p>
	 *
	 * @see javax.faces.context.FacesContext#release()
	 */
	public void release() {
		getWrapped().release();
	}

	/**
	 * <p>The default behavior of this method is to
	 * call {@link FacesContext#renderResponse()}
	 * on the wrapped {@link FacesContext} object.</p>
	 *
	 * @see javax.faces.context.FacesContext#renderResponse()
	 */
	public void renderResponse() {
		getWrapped().renderResponse();
	}

	/**
	 * <p>The default behavior of this method is to
	 * call {@link FacesContext#responseComplete()}
	 * on the wrapped {@link FacesContext} object.</p>
	 *
	 * @see javax.faces.context.FacesContext#responseComplete()
	 */
	public void responseComplete() {
		getWrapped().responseComplete();
	}

	/**
	 * <p>The default behavior of this method is to
	 * call {@link FacesContext#getELContext()}
	 * on the wrapped {@link FacesContext} object.</p>
	 *
	 * @see javax.faces.context.FacesContext#getELContext()
	 */
	public ELContext getELContext() {
		return getWrapped().getELContext();
	}

}
