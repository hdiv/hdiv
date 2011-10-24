/**
 * Copyright 2005-2011 hdiv.org
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
package org.hdiv.taglibs.standard.tag.el.core;

import javax.servlet.jsp.JspException;

import org.apache.taglibs.standard.tag.el.core.ExpressionUtil;
import org.hdiv.taglibs.standard.tag.common.core.RedirectSupportHDIV;

/**
 * <p>A handler for &lt;redirect&gt;, which redirects the browser to a
 * new URL.
 *
 * @author Gorka Vicente
 * @since HDIV 2.0
 */
public class RedirectTagHDIV extends RedirectSupportHDIV {

	/**
	 * stores EL-based property
	 */
	private String url_;

	/**
	 * stores EL-based property
	 */
	private String context_;

	/**
	 * Constructs a new URLEncodeTag.  As with TagSupport, subclasses
	 * should not provide other constructors and are expected to call
	 * the superclass constructor
	 */
	public RedirectTagHDIV() {
		super();
		init();
	}

	/**
	 * Evaluates expression and chains to parent
	 */
	public int doStartTag() throws JspException {

		// evaluate any expressions we were passed, once per invocation
		evaluateExpressions();

		// chain to the parent implementation
		return super.doStartTag();
	}

	/**
	 * Releases any resources we may have (or inherit)
	 */
	public void release() {
		super.release();
		init();
	}

	public void setUrl(String url_) {
		this.url_ = url_;
	}

	public void setContext(String context_) {
		this.context_ = context_;
	}

	/**
	 * (re)initializes state (during release() or construction)
	 */
	private void init() {
		// null implies "no expression"
		url_ = context_ = null;
	}

	/**
	 * Evaluates expressions as necessary
	 */
	private void evaluateExpressions() throws JspException {
		/* 
		 * Note: we don't check for type mismatches here; we assume
		 * the expression evaluator will return the expected type
		 * (by virtue of knowledge we give it about what that type is).
		 * A ClassCastException here is truly unexpected, so we let it
		 * propagate up.
		 */

		url = (String) ExpressionUtil.evalNotNull("redirect", "url", url_, String.class, this,
													pageContext);
		context = (String) ExpressionUtil.evalNotNull("redirect", "context", context_,
														String.class, this, pageContext);
	}
}
