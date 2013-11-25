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
package org.hdiv.taglibs.standard.tag.common.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.taglibs.standard.tag.common.core.ImportSupport;
import org.apache.taglibs.standard.tag.common.core.ParamParent;
import org.apache.taglibs.standard.tag.common.core.ParamSupport;
import org.apache.taglibs.standard.tag.common.core.UrlSupport;
import org.apache.taglibs.standard.tag.common.core.Util;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.util.HDIVUtil;

/**
 * <p>
 * Support for tag handlers for &lt;redirect&gt;, JSTL 1.0's tag for redirecting
 * to a new URL (with optional query parameters).
 * </p>
 * 
 * @author Gorka Vicente
 * @since HDIV 2.0.6
 */
public abstract class RedirectSupportHDIV extends BodyTagSupport implements ParamParent {

	/**
	 * 'url' attribute
	 */
	protected String url;

	/**
	 * 'context' attribute
	 */
	protected String context;

	/**
	 * 'var' attribute
	 */
	private String var;

	/**
	 * processed 'scope' attr
	 */
	private int scope;

	/**
	 * added parameters
	 */
	private ParamSupport.ParamManager params;

	public RedirectSupportHDIV() {

		super();
		init();
	}

	private void init() {
		url = var = null;
		params = null;
		scope = PageContext.PAGE_SCOPE;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public void setScope(String scope) {
		this.scope = Util.getScope(scope);
	}

	public void addParameter(String name, String value) {
		params.addParameter(name, value);
	}

	/**
	 * resets any parameters that might be sent
	 */
	public int doStartTag() throws JspException {

		params = new ParamSupport.ParamManager();
		return EVAL_BODY_BUFFERED;
	}

	/**
	 * Gets the right value, encodes it, and prints or stores it
	 */
	public int doEndTag() throws JspException {

		String result; // the eventual result

		// add (already encoded) parameters
		String baseUrl = UrlSupport.resolveUrl(url, context, pageContext);
		result = params.aggregateParams(baseUrl);

		// if the URL is relative, rewrite it with 'redirect' encoding rules
		HttpServletResponse response = ((HttpServletResponse) pageContext.getResponse());
		if (!ImportSupport.isAbsoluteUrl(result)) {
			result = response.encodeRedirectURL(result);
		}

		// Call to HDIV
		LinkUrlProcessor linkUrlProcessor = HDIVUtil.getLinkUrlProcessor(pageContext.getServletContext());
		result = linkUrlProcessor.processUrl((HttpServletRequest) pageContext.getRequest(), result);

		// redirect!
		try {
			response.sendRedirect(result);
		} catch (java.io.IOException ex) {
			throw new JspTagException(ex.toString(), ex);
		}

		return SKIP_PAGE;
	}

	/**
	 * Releases any resources we may have (or inherit)
	 */
	public void release() {
		init();
	}
}
