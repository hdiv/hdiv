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
package org.hdiv.web.servlet.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.urlProcessor.FormUrlProcessor;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

/**
 * {@link RequestDataValueProcessor} implementation for HDIV.
 * 
 * @author Gotzon Illarramendi
 */
public class HdivRequestDataValueProcessor implements RequestDataValueProcessor {

	protected LinkUrlProcessor linkUrlProcessor;

	protected FormUrlProcessor formUrlProcessor;

	/**
	 * Inner delegate {@link RequestDataValueProcessor} implementation. Only works with Spring greater or equal to
	 * 4.0.0.
	 */
	protected RequestDataValueProcessor innerRequestDataValueProcessor;

	/**
	 * No editable field types.
	 */
	protected List<String> noEditableTypes = new ArrayList<String>();

	public HdivRequestDataValueProcessor() {

		// Initialize no editable types list
		initNoEditableTypes();
	}

	/**
	 * <p>
	 * Initialize no editable input types.
	 * </p>
	 * Non editable input types:
	 * <ul>
	 * <li>checkbox</li>
	 * <li>hidden</li>
	 * <li>option</li>
	 * <li>radio</li>
	 * <li>select</li>
	 * <li>submit</li>
	 * </ul>
	 * Editable input types:
	 * <ul>
	 * <li>text</li>
	 * <li>textarea</li>
	 * <li>password</li>
	 * <li>other input types: (number, tel, email...)</li>
	 * </ul>
	 */
	protected void initNoEditableTypes() {

		this.noEditableTypes.add("checkbox");
		this.noEditableTypes.add("hidden");
		this.noEditableTypes.add("option");
		this.noEditableTypes.add("radio");
		this.noEditableTypes.add("select");
		this.noEditableTypes.add("submit");
	}

	/**
	 * Process the action url of the form tag, maintained for legacy support Spring 3.1.x.
	 * 
	 * @param request request object
	 * @param action form action url
	 * @return processed action url
	 */
	public String processAction(final HttpServletRequest request, final String action) {
		return this.processAction(request, action, "POST");
	}

	/**
	 * Process the action url of the form tag.
	 * 
	 * @param request request object
	 * @param action form action url
	 * @param method form submit method
	 * @return processed action url
	 */
	public String processAction(final HttpServletRequest request, String action, final String method) {

		if (this.innerRequestDataValueProcessor != null) {
			final String processedAction = this.innerRequestDataValueProcessor.processAction(request, action, method);
			if (processedAction != action) {
				action = processedAction;
			}
		}

		final String result = this.formUrlProcessor.processUrl(request, action, method);
		return result;
	}

	/**
	 * Process form field value.
	 * 
	 * @param request request object
	 * @param name the name of the field
	 * @param value the value of the field
	 * @param type the type of the field
	 * @return processed field value
	 */
	public String processFormFieldValue(final HttpServletRequest request, final String name, String value, final String type) {

		if (this.innerRequestDataValueProcessor != null) {
			final String processedValue = this.innerRequestDataValueProcessor.processFormFieldValue(request, name, value, type);
			if (processedValue != value) {
				value = processedValue;
			}
		}

		if (name == null) {
			return value;
		}

		final IDataComposer dataComposer = HDIVUtil.getDataComposer(request);

		if (dataComposer == null || dataComposer.isRequestStarted() == false) {
			return value;
		}

		if (isEditable(type)) {
			dataComposer.composeFormField(name, value, true, type);
			return value;
		}
		else {
			final String result = dataComposer.composeFormField(name, value, false, type);
			return result;
		}

	}

	/**
	 * Extra hidden fields with the HDIV state value.
	 * 
	 * @param request request object
	 * @return hidden field name/value
	 */
	public Map<String, String> getExtraHiddenFields(final HttpServletRequest request) {

		final IDataComposer dataComposer = HDIVUtil.getDataComposer(request);
		final Map<String, String> extraFields = new HashMap<String, String>();

		if (this.innerRequestDataValueProcessor != null) {
			final Map<String, String> innerExtras = this.innerRequestDataValueProcessor.getExtraHiddenFields(request);
			if (innerExtras != null) {
				extraFields.putAll(innerExtras);
			}
		}

		if (dataComposer == null || dataComposer.isRequestStarted() == false) {
			return extraFields;
		}

		final String requestId = dataComposer.endRequest();

		if (requestId != null && requestId.length() > 0) {
			final String hdivStateParam = (String) request.getSession().getAttribute(Constants.HDIV_PARAMETER);
			extraFields.put(hdivStateParam, requestId);

			// Publish the state in request to make it accessible on jsp
			request.setAttribute(FormUrlProcessor.FORM_STATE_ID, requestId);
		}
		return extraFields;
	}

	/**
	 * Process the url for a link.
	 * 
	 * @param request request object
	 * @param url link url
	 * @return processed url
	 */
	public String processUrl(final HttpServletRequest request, String url) {

		if (this.innerRequestDataValueProcessor != null) {
			final String processedUrl = this.innerRequestDataValueProcessor.processUrl(request, url);
			if (processedUrl != null) {
				url = processedUrl;
			}
		}

		final String result = this.linkUrlProcessor.processUrl(request, url);
		return result;
	}

	/**
	 * Determines if a field type is editable or not.
	 * 
	 * @param type field type
	 * @return editable
	 */
	protected boolean isEditable(final String type) {

		if (this.noEditableTypes.contains(type)) {
			return false;
		}
		return true;
	}

	/**
	 * @param linkUrlProcessor the linkUrlProcessor to set
	 */
	public void setLinkUrlProcessor(final LinkUrlProcessor linkUrlProcessor) {
		this.linkUrlProcessor = linkUrlProcessor;
	}

	/**
	 * @param formUrlProcessor the formUrlProcessor to set
	 */
	public void setFormUrlProcessor(final FormUrlProcessor formUrlProcessor) {
		this.formUrlProcessor = formUrlProcessor;
	}

	/**
	 * @param innerRequestDataValueProcessor the innerRequestDataValueProcessor to set
	 */
	public void setInnerRequestDataValueProcessor(final RequestDataValueProcessor innerRequestDataValueProcessor) {
		this.innerRequestDataValueProcessor = innerRequestDataValueProcessor;
	}

	/**
	 * @return the innerRequestDataValueProcessor
	 */
	public RequestDataValueProcessor getInnerRequestDataValueProcessor() {
		return innerRequestDataValueProcessor;
	}

}
