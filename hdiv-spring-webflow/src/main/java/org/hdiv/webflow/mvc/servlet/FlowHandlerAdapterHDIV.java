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

package org.hdiv.webflow.mvc.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.js.ajax.SpringJavascriptAjaxHandlerHDIV;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.util.HDIVUtil;
import org.springframework.js.ajax.AjaxHandler;
import org.springframework.webflow.context.servlet.FlowUrlHandler;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.mvc.servlet.FlowHandler;
import org.springframework.webflow.mvc.servlet.FlowHandlerAdapter;

/**
 * A custom MVC HandlerAdapter that encapsulates the generic workflow associated with executing flows in a Servlet
 * environment. Delegates to mapped {@link FlowHandler flow handlers} to manage the interaction with executions of
 * specific flow definitions.
 * 
 * @author Gorka Vicente
 * @author Gotzon Illarramendi
 * @since HDIV 2.1.0
 */
public class FlowHandlerAdapterHDIV extends FlowHandlerAdapter {

	private static final Log logger = LogFactory.getLog(FlowHandlerAdapterHDIV.class);

	/**
	 * Creates a new flow handler adapter.
	 * @see #setFlowExecutor(FlowExecutor)
	 * @see #setFlowUrlHandler(FlowUrlHandler)
	 * @see #setAjaxHandler(AjaxHandler)
	 * @see #afterPropertiesSet()
	 */
	public FlowHandlerAdapterHDIV() {
		
		this.setAjaxHandler(new SpringJavascriptAjaxHandlerHDIV());
	}
	
	protected void sendRedirect(String url, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		if (this.getAjaxHandler().isAjaxRequest(request, response)) {
			this.getAjaxHandler().sendAjaxRedirect(url, request, response, false);
		} else {

			if (request.getSession(false) != null) {
				LinkUrlProcessor linkUrlProcessor = HDIVUtil.getLinkUrlProcessor(request.getSession().getServletContext());
				url = linkUrlProcessor.processUrl(request, url);
			}
			/*
			if (request.getSession(false) != null) {			
				HDIVConfig hdivConfig = (HDIVConfig) HDIVUtil.getApplication().getBean("config");	
				if (HDIVRequestUtils.hasActionOrServletExtension(url, hdivConfig.getProtectedURLPatterns())) {
					url = HDIVRequestUtils.addHDIVParameterIfNecessary(request, url, hdivConfig.isValidationInUrlsWithoutParamsActivated());
				}
			}
			*/
			logger.debug("[sendRedirect] Redirecting to url: "+url);
			
			if (this.getRedirectHttp10Compatible()) {
				// Always send status code 302.
				response.sendRedirect(response.encodeRedirectURL(url));
			} else {
				// Correct HTTP status code is 303, in particular for POST requests.
				response.setStatus(303);
				response.setHeader("Location", response.encodeRedirectURL(url));
			}
		}
	}

}
