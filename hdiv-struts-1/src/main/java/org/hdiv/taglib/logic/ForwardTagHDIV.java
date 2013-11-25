/**
 * Copyright 2005-2013 hdiv.org
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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.logic.ForwardTag;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.util.HDIVUtil;

/**
 * Perform a forward or redirect to a page that is looked up in the
 * configuration information associated with our application.
 *
 * @author Gorka Vicente
 * @see org.apache.struts.taglib.logic.ForwardTag
 */
public class ForwardTagHDIV extends ForwardTag {

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that
	 * a loaded class corresponds exactly to a serialized object.
	 */
	private static final long serialVersionUID = -762185680912315095L;

	protected LinkUrlProcessor linkUrlProcessor;
	
	/**
	 * Redirect to the given path converting exceptions to JspException.
	 * @param path The path to redirect to.
	 * @throws JspException
	 * @since Struts 1.2
	 */
	protected void doRedirect(String path) throws JspException {
		
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
		
		try {
			if (path.startsWith("/")) {
				path = request.getContextPath() + path;
			}			

			// generate a new encoded values for the url parameters
			// Call to Hdiv LinkUrlProcessor
			if(this.linkUrlProcessor == null){
				this.linkUrlProcessor = HDIVUtil.getLinkUrlProcessor(request.getSession().getServletContext());
			}
			String urlHDIVstate = linkUrlProcessor.processUrl(request, path);
				
			String encodedURL = response.encodeRedirectURL(urlHDIVstate);
			response.sendRedirect(encodedURL);

		} catch (Exception e) {
			TagUtils.getInstance().saveException(pageContext, e);
			throw new JspException(messages.getMessage("forward.redirect", name, e.toString()));
		}
	}

}
