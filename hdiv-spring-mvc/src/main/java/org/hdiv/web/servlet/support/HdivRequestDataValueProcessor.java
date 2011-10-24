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
package org.hdiv.web.servlet.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.config.HDIVConfig;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVRequestUtils;
import org.hdiv.util.HDIVUtil;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

/**
 * RequestDataValueProcessor implementation for HDIV.
 * 
 * @author Gotzon Illarramendi
 */
public class HdivRequestDataValueProcessor implements RequestDataValueProcessor {

	/**
	 * HDIV configuration for this app.
	 */
	private HDIVConfig hdivConfig;

	/**
	 * HDIV state param name.
	 */
	private String hdivStateParamName;

	/**
	 * No editable field types.
	 */
	private List<String> noEditableTypes = new ArrayList<String>();

	public HdivRequestDataValueProcessor() {

		// Non Editable:
		// checkbox
		// hidden
		// option
		// radio
		// select
		// Editable:
		// text
		// textarea
		// password
		// XXX Find a better way for this
		this.noEditableTypes.add("checkbox");
		this.noEditableTypes.add("hidden");
		this.noEditableTypes.add("option");
		this.noEditableTypes.add("radio");
		this.noEditableTypes.add("select");
	}

	/**
	 * Process the action url of the form tag.
	 * 
	 * @param request
	 *            request object
	 * @param action
	 *            form action url
	 * @return processed action url
	 */
	public String processAction(HttpServletRequest request, String action) {

		IDataComposer dataComposer = (IDataComposer) request.getAttribute(HDIVUtil.DATACOMPOSER_REQUEST_KEY);

		String beginAction = action;
		String queryString = null;
		if (action.contains("?")) {
			beginAction = action.substring(0, action.indexOf("?"));
			queryString = action.substring(action.indexOf("?") + 1);
		}

		// If action is a start page, do nothig
		boolean startPage = HDIVRequestUtils.isUrlStartPage(beginAction, request, hdivConfig);
		if (startPage) {
			return action;
		}

		// Obtain a context path relative url, completing relative urls
		beginAction = HDIVRequestUtils.getContextRelativePath(request, beginAction);

		dataComposer.beginRequest(HDIVUtil.getActionMappingName(beginAction));

		if (queryString != null) {
			String encodedParams = this.composeQueryString(request, dataComposer, queryString);
			action = beginAction + "?" + encodedParams;
		}

		return action;
	}

	/**
	 * Process form field value.
	 * 
	 * @param request
	 *            request object
	 * @param name
	 *            the name of the field
	 * @param value
	 *            the value of the field
	 * @param type
	 *            the type of the field
	 * @return processed field value
	 */
	public String processFormFieldValue(HttpServletRequest request, String name, String value, String type) {

		if (name == null) {
			return value;
		}

		IDataComposer dataComposer = (IDataComposer) request.getAttribute(HDIVUtil.DATACOMPOSER_REQUEST_KEY);

		if (dataComposer.isRequestStarted() == false) {
			return value;
		}

		if (isEditable(type)) {
			dataComposer.compose(name, value, true, type);
			return value;
		} else {
			String result = dataComposer.compose(name, value, false, type);
			return result;
		}

	}

	/**
	 * Extra hidden fields with the HDIV state value.
	 * 
	 * @param request
	 *            request object
	 * @return hidden field name/value
	 */
	public Map<String, String> getExtraHiddenFields(HttpServletRequest request) {

		IDataComposer dataComposer = (IDataComposer) request.getAttribute(HDIVUtil.DATACOMPOSER_REQUEST_KEY);
		Map<String, String> extraFields = new HashMap<String, String>();

		if (dataComposer.isRequestStarted() == false) {
			return extraFields;
		}

		String requestId = dataComposer.endRequest();

		if (requestId != null && requestId.length() > 0) {
			String hdivStateParam = (String) request.getSession().getAttribute(Constants.HDIV_PARAMETER);
			extraFields.put(hdivStateParam, requestId);
		}
		return extraFields;
	}

	/**
	 * Process the url for a link.
	 * 
	 * @param request
	 *            request object
	 * @param url
	 *            link url
	 * @return processed url
	 */
	public String processUrl(HttpServletRequest request, String url) {

		String urlStr = HDIVRequestUtils.composeLinkUrl(url, request);
		return urlStr;
	}

	/**
	 * Determines if a field type is editable or not.
	 * 
	 * @param type
	 *            field type
	 * @return editable
	 */
	protected boolean isEditable(String type) {

		if (this.noEditableTypes.contains(type)) {
			return false;
		}
		return true;
	}

	/**
	 * Removes HDIV parameter from <code>queryString</code> and it composes
	 * other parameters.
	 * 
	 * @param request
	 *            request object
	 * @param dataComposer
	 *            request DataComposer
	 * @param queryString
	 *            query string
	 * @return queryString without HDIV's parameter
	 */
	protected String composeQueryString(HttpServletRequest request, IDataComposer dataComposer, String queryString) {

		String token = null;
		StringBuffer result = new StringBuffer();

		StringTokenizer st = new StringTokenizer(queryString, "&");
		while (st.hasMoreTokens()) {

			token = st.nextToken();
			String param = token.substring(0, token.indexOf("="));

			if (!ignoreParameter(request, param)) {

				String originalValue = request.getParameter(param);
				String val = dataComposer.compose(param, originalValue, false);

				if (result.length() > 0) {
					result.append("&");
				}
				result.append(param + "=" + val);
			}
		}
		return result.toString();
	}

	/**
	 * @returns Returns true if parameter <code>param</code> must be ignored.
	 *          False otherwise.
	 */
	protected boolean ignoreParameter(HttpServletRequest request, String param) {

		if (this.hdivStateParamName == null) {
			this.hdivStateParamName = (String) request.getSession().getAttribute(Constants.HDIV_PARAMETER);
		}
		return param.equalsIgnoreCase(this.hdivStateParamName);
	}

	/**
	 * @return the hdivConfig
	 */
	public HDIVConfig getHdivConfig() {
		return hdivConfig;
	}

	/**
	 * @param hdivConfig
	 *            the hdivConfig to set
	 */
	public void setHdivConfig(HDIVConfig hdivConfig) {
		this.hdivConfig = hdivConfig;
	}

}
