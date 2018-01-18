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
package org.hdiv.taglib.html;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.TagUtils;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.util.HDIVUtil;

/**
 * Generate a URL-encoded URI as a string.
 *
 * @author Gorka Vicente
 * @since HDIV 2.0.4
 */
public class RewriteTagHDIV extends LinkTagHDIV {

	protected LinkUrlProcessor linkUrlProcessor;

	/**
	 * Render the URI.
	 *
	 * @throws JspException if a JSP exception has occurred
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int doEndTag() throws JspException {
		// Generate the hyperlink URL
		Map params = TagUtils.getInstance().computeParameters(pageContext, paramId, paramName, paramProperty, paramScope, name, property,
				scope, transaction);

		// Add parameters collected from the tag's inner body
		if (!parameters.isEmpty()) {
			if (params == null) {
				params = new HashMap();
			}
			params.putAll(parameters);
		}

		String url = null;

		try {
			// Note that we're encoding the & character to &amp; in XHTML mode only,
			// otherwise the & is written as is to work in javascripts.
			url = TagUtils.getInstance().computeURLWithCharEncoding(pageContext, forward, href, page, action, module, params, anchor, false,
					isXhtml(), useLocalEncoding);
		}
		catch (MalformedURLException e) {
			TagUtils.getInstance().saveException(pageContext, e);
			throw new JspException(messages.getMessage("rewrite.url", e.toString()));
		}

		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		String charEncoding = useLocalEncoding ? charEncoding = pageContext.getResponse().getCharacterEncoding() : "UTF-8";

		// Call to Hdiv LinkUrlProcessor
		if (linkUrlProcessor == null) {
			linkUrlProcessor = HDIVUtil.getLinkUrlProcessor(request.getSession().getServletContext());
		}
		url = linkUrlProcessor.processUrl(HDIVUtil.getRequestContext(request), url, charEncoding);

		TagUtils.getInstance().write(pageContext, url);
		return EVAL_PAGE;
	}

}
