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
package org.hdiv.context.jsf1.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.context.ExternalContext;

public abstract class ExternalContextWrapper extends ExternalContext {

	// ----------------------------------------------- Methods from FacesWrapper

	/**
	 * @return the wrapped {@link ExternalContext} instance
	 * @see javax.faces.FacesWrapper#getWrapped()
	 */
	public abstract ExternalContext getWrapped();

	// -------------------------------------------- Methods from ExternalContext

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#dispatch(String)} on the wrapped {@link ExternalContext}
	 * object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#dispatch(String)
	 */
	@Override
	public void dispatch(final String path) throws IOException {
		getWrapped().dispatch(path);
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#encodeActionURL(String)} on the wrapped {@link ExternalContext}
	 * object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#encodeActionURL(String)
	 */
	@Override
	public String encodeActionURL(final String url) {
		return getWrapped().encodeActionURL(url);
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#encodeNamespace(String)} on the wrapped {@link ExternalContext}
	 * object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#encodeNamespace(String)
	 */
	@Override
	public String encodeNamespace(final String name) {
		return getWrapped().encodeNamespace(name);
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#encodeResourceURL(String)} on the wrapped
	 * {@link ExternalContext} object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#encodeResourceURL(String)
	 */
	@Override
	public String encodeResourceURL(final String url) {
		return getWrapped().encodeResourceURL(url);
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getApplicationMap} on the wrapped {@link ExternalContext}
	 * object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getApplicationMap()
	 */
	@Override
	public Map getApplicationMap() {
		return getWrapped().getApplicationMap();
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getAuthType} on the wrapped {@link ExternalContext} object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getAuthType()
	 */
	@Override
	public String getAuthType() {
		return getWrapped().getAuthType();
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getContext} on the wrapped {@link ExternalContext} object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getContext()
	 */
	@Override
	public Object getContext() {
		return getWrapped().getContext();
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getInitParameter(String)} on the wrapped
	 * {@link ExternalContext} object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getInitParameter(String)
	 */
	@Override
	public String getInitParameter(final String name) {
		return getWrapped().getInitParameter(name);
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getInitParameterMap} on the wrapped {@link ExternalContext}
	 * object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getInitParameterMap()
	 */
	@Override
	public Map getInitParameterMap() {
		return getWrapped().getInitParameterMap();
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getRemoteUser} on the wrapped {@link ExternalContext} object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getRemoteUser()
	 */
	@Override
	public String getRemoteUser() {
		return getWrapped().getRemoteUser();
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getRequest} on the wrapped {@link ExternalContext} object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getRequest()
	 */
	@Override
	public Object getRequest() {
		return getWrapped().getRequest();
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getRequestContextPath} on the wrapped {@link ExternalContext}
	 * object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getRequestContextPath()
	 */
	@Override
	public String getRequestContextPath() {
		return getWrapped().getRequestContextPath();
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getRequestCookieMap} on the wrapped {@link ExternalContext}
	 * object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getRequestCookieMap()
	 */
	@Override
	public Map getRequestCookieMap() {
		return getWrapped().getRequestCookieMap();
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getRequestHeaderMap} on the wrapped {@link ExternalContext}
	 * object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getRequestHeaderMap()
	 */
	@Override
	public Map getRequestHeaderMap() {
		return getWrapped().getRequestHeaderMap();
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getRequestHeaderValuesMap} on the wrapped
	 * {@link ExternalContext} object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getRequestHeaderValuesMap()
	 */
	@Override
	public Map getRequestHeaderValuesMap() {
		return getWrapped().getRequestHeaderValuesMap();
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getRequestLocale} on the wrapped {@link ExternalContext}
	 * object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getRequestLocale()
	 */
	@Override
	public Locale getRequestLocale() {
		return getWrapped().getRequestLocale();
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getRequestLocales} on the wrapped {@link ExternalContext}
	 * object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getRequestLocales()
	 */
	@Override
	public Iterator getRequestLocales() {
		return getWrapped().getRequestLocales();
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getRequestMap} on the wrapped {@link ExternalContext} object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getRequestMap()
	 */
	@Override
	public Map getRequestMap() {
		return getWrapped().getRequestMap();
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getRequestParameterMap} on the wrapped {@link ExternalContext}
	 * object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getRequestParameterMap()
	 */
	@Override
	public Map getRequestParameterMap() {
		return getWrapped().getRequestParameterMap();
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getRequestParameterNames} on the wrapped
	 * {@link ExternalContext} object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getRequestParameterNames()
	 */
	@Override
	public Iterator getRequestParameterNames() {
		return getWrapped().getRequestParameterNames();
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getRequestParameterValuesMap} on the wrapped
	 * {@link ExternalContext} object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getRequestParameterValuesMap()
	 */
	@Override
	public Map getRequestParameterValuesMap() {
		return getWrapped().getRequestParameterValuesMap();
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getRequestPathInfo} on the wrapped {@link ExternalContext}
	 * object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getRequestPathInfo()
	 */
	@Override
	public String getRequestPathInfo() {
		return getWrapped().getRequestPathInfo();
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getRequestServletPath} on the wrapped {@link ExternalContext}
	 * object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getRequestServletPath()
	 */
	@Override
	public String getRequestServletPath() {
		return getWrapped().getRequestServletPath();
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getResource(String)} on the wrapped {@link ExternalContext}
	 * object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getResource(String)
	 */
	@Override
	public URL getResource(final String path) throws MalformedURLException {
		return getWrapped().getResource(path);
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getResourceAsStream(String)} on the wrapped
	 * {@link ExternalContext} object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getResourceAsStream(String)
	 */
	@Override
	public InputStream getResourceAsStream(final String path) {
		return getWrapped().getResourceAsStream(path);
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getResourcePaths(String)} on the wrapped
	 * {@link ExternalContext} object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getResourcePaths(String)
	 */
	@Override
	public Set getResourcePaths(final String path) {
		return getWrapped().getResourcePaths(path);
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getResponse} on the wrapped {@link ExternalContext} object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getResponse()
	 */
	@Override
	public Object getResponse() {
		return getWrapped().getResponse();
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getSession(boolean)} on the wrapped {@link ExternalContext}
	 * object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getSession(boolean)
	 */
	@Override
	public Object getSession(final boolean create) {
		return getWrapped().getSession(create);
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getAuthType} on the wrapped {@link ExternalContext} object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getAuthType()
	 */
	@Override
	public Map getSessionMap() {
		return getWrapped().getSessionMap();
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getUserPrincipal} on the wrapped {@link ExternalContext}
	 * object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getUserPrincipal()
	 */
	@Override
	public Principal getUserPrincipal() {
		return getWrapped().getUserPrincipal();
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#isUserInRole(String)} on the wrapped {@link ExternalContext}
	 * object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#isUserInRole(String)
	 */
	@Override
	public boolean isUserInRole(final String role) {
		return getWrapped().isUserInRole(role);
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#log(String)} on the wrapped {@link ExternalContext} object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#log(String)
	 */
	@Override
	public void log(final String message) {
		getWrapped().log(message);
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#log(String, Throwable)} on the wrapped {@link ExternalContext}
	 * object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#log(String, Throwable)
	 */
	@Override
	public void log(final String message, final Throwable exception) {
		getWrapped().log(message, exception);
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#redirect(String)} on the wrapped {@link ExternalContext}
	 * object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#redirect(String)
	 */
	@Override
	public void redirect(final String url) throws IOException {
		getWrapped().redirect(url);
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#setRequest(Object)} on the wrapped {@link ExternalContext}
	 * object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#setRequest(Object)
	 */

	@Override
	public void setRequest(final Object request) {
		getWrapped().setRequest(request);
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#setRequestCharacterEncoding(String)} on the wrapped
	 * {@link ExternalContext} object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#setRequestCharacterEncoding(String)
	 */

	@Override
	public void setRequestCharacterEncoding(final String encoding) throws UnsupportedEncodingException {
		getWrapped().setRequestCharacterEncoding(encoding);
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getRequestCharacterEncoding} on the wrapped
	 * {@link ExternalContext} object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getRequestCharacterEncoding()
	 */

	@Override
	public String getRequestCharacterEncoding() {
		return getWrapped().getRequestCharacterEncoding();
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getRequestContentType} on the wrapped {@link ExternalContext}
	 * object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getRequestContentType()
	 */

	@Override
	public String getRequestContentType() {
		return getWrapped().getRequestContentType();
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getResponseCharacterEncoding} on the wrapped
	 * {@link ExternalContext} object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getResponseCharacterEncoding()
	 */

	@Override
	public String getResponseCharacterEncoding() {
		return getWrapped().getResponseCharacterEncoding();
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getResponseContentType} on the wrapped {@link ExternalContext}
	 * object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getResponseContentType()
	 */

	@Override
	public String getResponseContentType() {
		return getWrapped().getResponseContentType();
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#setResponse(Object)} on the wrapped {@link ExternalContext}
	 * object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#setResponse(Object)
	 */

	@Override
	public void setResponse(final Object response) {
		getWrapped().setResponse(response);
	}

	/**
	 * <p>
	 * The default behavior of this method is to call {@link ExternalContext#getResponseCharacterEncoding} on the wrapped
	 * {@link ExternalContext} object.
	 * </p>
	 *
	 * @see javax.faces.context.ExternalContext#getResponseCharacterEncoding()
	 */

	@Override
	public void setResponseCharacterEncoding(final String encoding) {
		getWrapped().setResponseCharacterEncoding(encoding);
	}

}
