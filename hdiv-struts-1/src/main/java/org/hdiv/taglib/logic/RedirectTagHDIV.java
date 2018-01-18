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
package org.hdiv.taglib.logic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.logic.RedirectTag;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.util.HDIVUtil;

/**
 * Generate a URL-encoded redirect to the specified URI.
 *
 * @author Gorka Vicente
 * @see org.apache.struts.taglib.logic.RedirectTag
 */
public class RedirectTagHDIV extends RedirectTag {

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized
	 * object.
	 */
	private static final long serialVersionUID = 1717614535121671431L;

	protected LinkUrlProcessor linkUrlProcessor;

	/**
	 * Render the redirect and skip the remainder of this page.
	 *
	 * @exception JspException if a JSP exception has occurred
	 */
	@Override
	public int doEndTag() throws JspException {

		// calculate the url to redirect to
		String url = generateRedirectURL();

		// If useLocalEncoding set to true, urlencoding is done on the bytes of
		// character encoding from ServletResponse#getCharacterEncoding. Use UTF-8
		// otherwise.
		String charEncoding = "UTF-8";
		if (useLocalEncoding) {
			charEncoding = pageContext.getResponse().getCharacterEncoding();
		}

		// generate a new encoded values for the url parameters
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		// Call to Hdiv LinkUrlProcessor
		if (linkUrlProcessor == null) {
			linkUrlProcessor = HDIVUtil.getLinkUrlProcessor(request.getSession().getServletContext());
		}
		String urlHDIVstate = linkUrlProcessor.processUrl(HDIVUtil.getRequestContext(request), url, charEncoding);

		// redirect to the given url
		doRedirect(urlHDIVstate);

		return SKIP_PAGE;
	}

}
