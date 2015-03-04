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

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.support.RequestDataValueProcessor;

/**
 * {@link RequestDataValueProcessor} implementation for HDIV on Grails.
 * 
 * @author Ugaitz Urien
 */
public class GrailsHdivRequestDataValueProcessor extends HdivRequestDataValueProcessor {

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
		String urlToProcess = url;
		Boolean modified = false;
		String contextPath = request.getContextPath();
		if (url.indexOf("/") == 0 && contextPath.length() > 1 && url.indexOf(contextPath) != 0) {
			urlToProcess = contextPath + url;
			modified = true;
		}
		String processedUrl = super.processUrl(request, urlToProcess);
		if (modified) {
			return processedUrl.substring(contextPath.length());
		}
		return processedUrl;
	}
}