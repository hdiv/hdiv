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
package org.hdiv.urlProcessor;

import java.util.Map;

/**
 * Contains the data of an url. Comparing to {@link UrlData}, only contains basic data.
 * 
 * @since 2.1.7
 * @author Gotzon Illarramendi
 */
public class BasicUrlData {

	/**
	 * Url that starts with contextPath
	 */
	private String contextPathRelativeUrl;

	/**
	 * Map with original url parameter name and values
	 */
	private Map<String, String[]> urlParams;

	/**
	 * @return the contextPathRelativeUrl
	 */
	public String getContextPathRelativeUrl() {
		return contextPathRelativeUrl;
	}

	/**
	 * @param contextPathRelativeUrl the contextPathRelativeUrl to set
	 */
	public void setContextPathRelativeUrl(String contextPathRelativeUrl) {
		this.contextPathRelativeUrl = contextPathRelativeUrl;
	}

	/**
	 * @return the urlParams
	 */
	public Map<String, String[]> getUrlParams() {
		return urlParams;
	}

	/**
	 * @param urlParams the urlParams to set
	 */
	public void setUrlParams(Map<String, String[]> urlParams) {
		this.urlParams = urlParams;
	}

}
