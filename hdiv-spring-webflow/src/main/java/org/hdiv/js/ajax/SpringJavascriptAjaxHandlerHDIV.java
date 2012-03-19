/**
 * Copyright 2005-2010 hdiv.org
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

package org.hdiv.js.ajax;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.util.HDIVUtil;
import org.springframework.js.ajax.SpringJavascriptAjaxHandler;

/**
 * Ajax handler for Spring Javascript (Spring.js).
 *
 * @author Gorka Vicente
 * @since HDIV 2.1.0
 */
public class SpringJavascriptAjaxHandlerHDIV extends SpringJavascriptAjaxHandler {

	private static final Log log = LogFactory.getLog(SpringJavascriptAjaxHandlerHDIV.class);

	public void sendAjaxRedirectInternal(String targetUrl, HttpServletRequest request, HttpServletResponse response, boolean popup) throws IOException {

		if (popup) {
			response.setHeader(POPUP_VIEW_HEADER, "true");
		}

		String encodeRedirectURL = response.encodeRedirectURL(targetUrl);

		if (request.getSession(false) != null) {			

			if (!encodeRedirectURL.startsWith("/")) {
				String requestUri = request.getRequestURI();
				int lastSlash = requestUri.lastIndexOf('/');
				
				encodeRedirectURL = requestUri.substring(0, lastSlash) + "/" + encodeRedirectURL;	
			}
			LinkUrlProcessor linkUrlProcessor = HDIVUtil.getLinkUrlProcessor(request.getSession().getServletContext());
			encodeRedirectURL = linkUrlProcessor.processUrl(request, targetUrl);
		}

		/* original...
		if (request.getSession(false) != null) {			

			ServletContext servletContext = request.getSession().getServletContext();
			HDIVConfig hdivConfig = HDIVUtil.getHDIVConfig(servletContext);
			if (HDIVRequestUtils.hasActionOrServletExtension(encodeRedirectURL, hdivConfig.getProtectedURLPatterns())) {
				if (!encodeRedirectURL.startsWith("/")) {
					String requestUri = request.getRequestURI();
					int lastSlash = requestUri.lastIndexOf('/');
					
					encodeRedirectURL = requestUri.substring(0, lastSlash) + "/" + encodeRedirectURL;	
				}
				encodeRedirectURL = HDIVRequestUtils.addHDIVParameterIfNecessary(request, encodeRedirectURL, hdivConfig.isValidationInUrlsWithoutParamsActivated());
			}
		}
		*/

		if(log.isDebugEnabled()){
			log.debug("Send Ajax Redirect to:"+encodeRedirectURL);
		}

		response.setHeader(REDIRECT_URL_HEADER, encodeRedirectURL);
	}

}