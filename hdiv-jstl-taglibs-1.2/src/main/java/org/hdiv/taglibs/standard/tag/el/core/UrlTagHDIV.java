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
import org.hdiv.taglibs.standard.tag.common.core.UrlSupportHDIV;

/**
 * <p>A handler for &lt;urlEncode&gt; that accepts attributes as Strings
 * and evaluates them as expressions at runtime.</p>
 *
 * @author Gorka Vicente
 * @since HDIV 2.0
 */
public class UrlTagHDIV extends UrlSupportHDIV {

	/**
	 * stores EL-based property
	 */
	private String value_;

	/**
	 * stores EL-based property
	 */
	private String context_;


	/**
	 * Constructs a new URLEncodeTag.  As with TagSupport, subclasses
	 * should not provide other constructors and are expected to call
	 * the superclass constructor
	 */
	public UrlTagHDIV() {
		super();
		init();
	}

	/**
	 * evaluates expression and chains to parent
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

	public void setValue(String value_) {
		this.value_ = value_;
	}

	public void setContext(String context_) {
		this.context_ = context_;
	}

	/**
	 * (re)initializes state (during release() or construction)
	 */
	private void init() {
		// null implies "no expression"
		value_ = null;
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

		value = (String) ExpressionUtil.evalNotNull("url", "value", value_, String.class, this,
													pageContext);
		context = (String) ExpressionUtil.evalNotNull("url", "context", context_, String.class,
														this, pageContext);
	}
}
