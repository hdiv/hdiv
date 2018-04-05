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
package org.hdiv.context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.filter.ValidationContext;
import org.hdiv.session.SessionModel;

public interface RequestContextHolder {

	SessionModel getSession();

	HttpServletResponse getResponse();

	@Deprecated
	HttpServletRequest getRequest();

	String getHdivParameterName();

	String getHdivModifyParameterName();

	String getHdivState();

	void setHdivState(String hdivState);

	String getRequestURI();

	UUID getCurrentPageId();

	void setCurrentPageId(UUID pageId);

	void setBaseURL(String baseURL);

	String getBaseURL();

	String getParameter(String name);

	boolean isAjax();

	String getUrlWithoutContextPath();

	IDataComposer getDataComposer();

	void setDataComposer(IDataComposer composer);

	long getRenderTime();

	void addRenderTime(long time);

	String getMethod();

	String getContextPath();

	String getServletPath();

	String getServerName();

	Object getAttribute(String attributeName);

	void setAttribute(String attributeName, Object value);

	boolean isAsync();

	Map<String, String[]> getParameterMap();

	void setFormStateId(String formStateId);

	String getFormStateId();

	Enumeration<String> getParameterNames();

	String[] getParameterValues(String parameter);

	Cookie[] getCookies();

	String getQueryString();

	void addEditableParameter(final String name);

	String getMessage(final String key, final String o);

	void addParameterToRequest(final String name, final String[] value);

	String getContentType();

	void setRedirectAction(String redirect);

	String getRedirectAction();

	InputStream getInputStream() throws IOException;

	String getHeader(String header);

	void setValidationContext(final ValidationContext validationContext);

	<T extends ValidationContext> T getValidationContext();

	ServletContext getServletContext();

}
