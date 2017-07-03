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
package org.hdiv.services;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

public class SecureModelAndView extends ModelAndView {

	private static SecureModelAndViewSerializer serializer;

	protected ModelMap jsonModel;

	private SecureModelAndView(final String viewName) {
		super(viewName);
	}

	public static SecureModelAndView forView(final String viewName) {
		return new SecureModelAndView(viewName);
	}

	public SecureModelAndView addJsonObject(final String attributeName, final Object attributeValue) {
		String json = getJson(attributeValue);
		if (json == null) {
			getModelMap().addAttribute(attributeName, attributeValue);
		}
		else {
			getModelMap().addAttribute(attributeName, json);
		}
		return this;
	}

	private String getJson(final Object value) {
		return serializer.serialize(value);
	}

	public static void setSerializer(final SecureModelAndViewSerializer serializer) {
		SecureModelAndView.serializer = serializer;
	}
}
