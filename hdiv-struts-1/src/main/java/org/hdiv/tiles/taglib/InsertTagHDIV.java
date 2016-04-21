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
package org.hdiv.tiles.taglib;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.struts.tiles.ComponentDefinition;
import org.apache.struts.tiles.Controller;
import org.apache.struts.tiles.taglib.InsertTag;
import org.hdiv.filter.RequestWrapper;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.urlProcessor.UrlData;
import org.hdiv.util.HDIVUtil;

/**
 * This is the tag handler for &lt;tiles:insert&gt;, which includes a template. The tag's body content consists of
 * &lt;tiles:put&gt; tags, which are accessed by &lt;tiles:get&gt; in the template.
 *
 * @author Gorka Vicente
 * @see org.apache.struts.tiles.taglib.InsertTag
 * @since HDIV 2.1.13
 */
public class InsertTagHDIV extends InsertTag {

	private static final long serialVersionUID = -7501549724315338219L;

	/**
	 * End of Process tag attribute "definition". Overload definition with tag attributes "template" and "role". Then,
	 * create appropriate tag handler.
	 * 
	 * @param definition Definition to process.
	 * @return Appropriate TagHandler.
	 * @throws JspException InstantiationException Can't create requested controller
	 */
	protected TagHandler processDefinition(ComponentDefinition definition) throws JspException {

		String currentPage = this.page;

		if (currentPage == null) {
			currentPage = definition.getTemplate();
		}

		if (log.isDebugEnabled())
			log.debug("Processing definition: " + currentPage);

		this.addParametersToRequestWrapper((HttpServletRequest) pageContext.getRequest(), currentPage);
		return super.processDefinition(definition);
	}

	/**
	 * Process the url.
	 * 
	 * @throws JspException If failed to create controller
	 */
	public TagHandler processUrl(String url) throws JspException {

		if (log.isDebugEnabled())
			log.debug("Processing url: " + url);

		this.addParametersToRequestWrapper((HttpServletRequest) pageContext.getRequest(), url);
		return new InsertHandler(url, role, getController());
	}

	/**
	 * Adds parameters of <code>url</code> to <code>request</code>.
	 * @param request HTTP Servlet Request
	 * @param url Url to process
	 */
	private void addParametersToRequestWrapper(HttpServletRequest request, String url) {
		RequestWrapper requestWrapper = HDIVUtil.getNativeRequest(request, RequestWrapper.class);
		if (requestWrapper != null) {

			LinkUrlProcessor linkUrlProcessorForForward = HDIVUtil.getLinkUrlProcessor(pageContext.getSession()
					.getServletContext());
			UrlData urlData = linkUrlProcessorForForward.createUrlData(url, "GET", request);
			Map<String, String[]> urlParamsAsMap = linkUrlProcessorForForward.getUrlParamsAsMap(request,
					urlData.getUrlParams());
			for (Map.Entry<String, String[]> entry : urlParamsAsMap.entrySet()) {
				requestWrapper.addParameter(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * Get instantiated Controller. Return controller denoted by controllerType, or <code>null</code> if controllerType
	 * is null.
	 * 
	 * @throws JspException If controller can't be created.
	 */
	private Controller getController() throws JspException {

		if (controllerType == null) {
			return null;
		}

		try {
			return ComponentDefinition.createController(controllerName, controllerType);

		}
		catch (InstantiationException ex) {
			throw new JspException(ex);
		}
	}
}
