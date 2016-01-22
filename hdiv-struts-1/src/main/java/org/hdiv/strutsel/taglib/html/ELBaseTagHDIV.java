/**
 * Copyright 2005-2015 hdiv.org
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
package org.hdiv.strutsel.taglib.html;

import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.html.BaseTag;
import org.apache.strutsel.taglib.utils.EvalHelper;

/**
 * Renders an HTML <base> element with an href attribute pointing to the absolute location of the enclosing JSP page.
 * This tag is only valid when nested inside a head tag body. The presence of this tag allows the browser to resolve
 * relative URL's to images, CSS stylesheets and other resources in a manner independent of the URL used to call the
 * ActionServlet.
 * <p>
 * This class is a subclass of the class <code>org.apache.struts.taglib.html.BaseTag</code> which provides most of the
 * described functionality. This subclass allows all attribute values to be specified as expressions utilizing the
 * JavaServer Pages Standard Library expression language.
 *
 * @version $Rev: 471754 $
 */
public class ELBaseTagHDIV extends BaseTag {
	/**
	 * Instance variable mapped to "target" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String targetExpr;

	/**
	 * Instance variable mapped to "server" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String serverExpr;

	/**
	 * Instance variable mapped to "ref" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String refExpr;

	/**
	 * Getter method for "target" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getTargetExpr() {
		return (targetExpr);
	}

	/**
	 * Getter method for "server" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getServerExpr() {
		return (serverExpr);
	}

	/**
	 * Getter method for "ref" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getRefExpr() {
		return (refExpr);
	}

	/**
	 * Setter method for "target" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setTargetExpr(String targetExpr) {
		this.targetExpr = targetExpr;
	}

	/**
	 * Setter method for "server" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setServerExpr(String serverExpr) {
		this.serverExpr = serverExpr;
	}

	/**
	 * Setter method for "ref" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setRefExpr(String refExpr) {
		this.refExpr = refExpr;
	}

	/**
	 * Resets attribute values for tag reuse.
	 */
	public void release() {
		super.release();
		setTargetExpr(null);
		setServerExpr(null);
		setRefExpr(null);
	}

	/**
	 * Process the start tag.
	 *
	 * @throws JspException if a JSP exception has occurred
	 */
	public int doStartTag() throws JspException {
		evaluateExpressions();

		return (super.doStartTag());
	}

	/**
	 * Processes all attribute values which use the JSTL expression evaluation engine to determine their values.
	 *
	 * @throws JspException if a JSP exception has occurred
	 */
	private void evaluateExpressions() throws JspException {
		String string = null;

		if ((string = EvalHelper.evalString("target", getTargetExpr(), this, pageContext)) != null) {
			setTarget(string);
		}

		if ((string = EvalHelper.evalString("server", getServerExpr(), this, pageContext)) != null) {
			setServer(string);
		}

		if ((string = EvalHelper.evalString("ref", getRefExpr(), this, pageContext)) != null) {
			setRef(string);
		}
	}
}
