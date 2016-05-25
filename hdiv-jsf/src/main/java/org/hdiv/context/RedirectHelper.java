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

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.hdiv.urlProcessor.LinkUrlProcessor;

/**
 * Helper class for redirect operations This class is independent from the JSF version. It is valid for 1.x and 2.0.
 * 
 * @author Gotzon Illarramendi
 */
public class RedirectHelper {

	/**
	 * UrlProcessor to protect the url.
	 */
	private LinkUrlProcessor linkUrlProcessor;

	/**
	 * Checks that url needs to be securized (points to the application itself) and if so creates HDIV state and adds the identifier as a
	 * parameter to the url.
	 * 
	 * @param url Url to secure
	 * @return secured url
	 */
	public String addHDIVStateToURL(String url) {

		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

		String finalUrl = this.linkUrlProcessor.processUrl(request, url);
		return finalUrl;
	}

	/**
	 * @param linkUrlProcessor the linkUrlProcessor to set
	 */
	public void setLinkUrlProcessor(LinkUrlProcessor linkUrlProcessor) {
		this.linkUrlProcessor = linkUrlProcessor;
	}

}
