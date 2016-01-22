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
package org.hdiv.strutsel.taglib.logic;

import javax.servlet.jsp.JspException;

import org.apache.strutsel.taglib.utils.EvalHelper;
import org.hdiv.taglib.logic.RedirectTagHDIV;

/**
 * Generate a URL-encoded redirect to the specified URI.
 * <p>
 * This class is a subclass of the class <code>org.apache.struts.taglib.logic.RedirectTag</code> which provides most of
 * the described functionality. This subclass allows all attribute values to be specified as expressions utilizing the
 * JavaServer Pages Standard Library expression language.
 * 
 * @author Gorka Vicente
 * @since HDIV 2.0
 */
public class ELRedirectTagHDIV extends RedirectTagHDIV {

	/**
	 * Instance variable mapped to "action" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String actionExpr;

	/**
	 * Instance variable mapped to "anchor" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String anchorExpr;

	/**
	 * Instance variable mapped to "forward" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String forwardExpr;

	/**
	 * Instance variable mapped to "href" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String hrefExpr;

	/**
	 * Instance variable mapped to "name" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String nameExpr;

	/**
	 * Instance variable mapped to "page" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String pageExpr;

	/**
	 * Instance variable mapped to "paramId" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String paramIdExpr;

	/**
	 * Instance variable mapped to "paramName" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String paramNameExpr;

	/**
	 * Instance variable mapped to "paramProperty" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String paramPropertyExpr;

	/**
	 * Instance variable mapped to "paramScope" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String paramScopeExpr;

	/**
	 * Instance variable mapped to "property" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String propertyExpr;

	/**
	 * Instance variable mapped to "scope" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String scopeExpr;

	/**
	 * Instance variable mapped to "transaction" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String transactionExpr;

	/**
	 * Instance variable mapped to "useLocalEncoding" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String useLocalEncodingExpr;

	/**
	 * Getter method for "action" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getActionExpr() {
		return (actionExpr);
	}

	/**
	 * Getter method for "anchor" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getAnchorExpr() {
		return (anchorExpr);
	}

	/**
	 * Getter method for "forward" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getForwardExpr() {
		return (forwardExpr);
	}

	/**
	 * Getter method for "href" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getHrefExpr() {
		return (hrefExpr);
	}

	/**
	 * Getter method for "name" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getNameExpr() {
		return (nameExpr);
	}

	/**
	 * Getter method for "page" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getPageExpr() {
		return (pageExpr);
	}

	/**
	 * Getter method for "paramId" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getParamIdExpr() {
		return (paramIdExpr);
	}

	/**
	 * Getter method for "paramName" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getParamNameExpr() {
		return (paramNameExpr);
	}

	/**
	 * Getter method for "paramProperty" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getParamPropertyExpr() {
		return (paramPropertyExpr);
	}

	/**
	 * Getter method for "paramScope" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getParamScopeExpr() {
		return (paramScopeExpr);
	}

	/**
	 * Getter method for "property" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getPropertyExpr() {
		return (propertyExpr);
	}

	/**
	 * Getter method for "scope" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getScopeExpr() {
		return (scopeExpr);
	}

	/**
	 * Getter method for "transaction" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getTransactionExpr() {
		return (transactionExpr);
	}

	/**
	 * Getter method for "useLocalEncoding" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getUseLocalEncodingExpr() {
		return (useLocalEncodingExpr);
	}

	/**
	 * Setter method for "action" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setActionExpr(String actionExpr) {
		this.actionExpr = actionExpr;
	}

	/**
	 * Setter method for "anchor" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setAnchorExpr(String anchorExpr) {
		this.anchorExpr = anchorExpr;
	}

	/**
	 * Setter method for "forward" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setForwardExpr(String forwardExpr) {
		this.forwardExpr = forwardExpr;
	}

	/**
	 * Setter method for "href" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setHrefExpr(String hrefExpr) {
		this.hrefExpr = hrefExpr;
	}

	/**
	 * Setter method for "name" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setNameExpr(String nameExpr) {
		this.nameExpr = nameExpr;
	}

	/**
	 * Setter method for "page" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setPageExpr(String pageExpr) {
		this.pageExpr = pageExpr;
	}

	/**
	 * Setter method for "paramId" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setParamIdExpr(String paramIdExpr) {
		this.paramIdExpr = paramIdExpr;
	}

	/**
	 * Setter method for "paramName" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setParamNameExpr(String paramNameExpr) {
		this.paramNameExpr = paramNameExpr;
	}

	/**
	 * Setter method for "paramProperty" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setParamPropertyExpr(String paramPropertyExpr) {
		this.paramPropertyExpr = paramPropertyExpr;
	}

	/**
	 * Setter method for "paramScope" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setParamScopeExpr(String paramScopeExpr) {
		this.paramScopeExpr = paramScopeExpr;
	}

	/**
	 * Setter method for "property" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setPropertyExpr(String propertyExpr) {
		this.propertyExpr = propertyExpr;
	}

	/**
	 * Setter method for "scope" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setScopeExpr(String scopeExpr) {
		this.scopeExpr = scopeExpr;
	}

	/**
	 * Setter method for "transaction" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setTransactionExpr(String transactionExpr) {
		this.transactionExpr = transactionExpr;
	}

	/**
	 * Setter method for "useLocalEncoding" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setUseLocalEncodingExpr(String useLocalEncodingExpr) {
		this.useLocalEncodingExpr = useLocalEncodingExpr;
	}

	/**
	 * Resets attribute values for tag reuse.
	 */
	public void release() {
		super.release();
		setActionExpr(null);
		setAnchorExpr(null);
		setForwardExpr(null);
		setHrefExpr(null);
		setNameExpr(null);
		setPageExpr(null);
		setParamIdExpr(null);
		setParamNameExpr(null);
		setParamPropertyExpr(null);
		setParamScopeExpr(null);
		setPropertyExpr(null);
		setScopeExpr(null);
		setTransactionExpr(null);
		setUseLocalEncodingExpr(null);
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
		Boolean bool = null;

		if ((string = EvalHelper.evalString("action", getActionExpr(), this, pageContext)) != null) {
			setAction(string);
		}

		if ((string = EvalHelper.evalString("anchor", getAnchorExpr(), this, pageContext)) != null) {
			setAnchor(string);
		}

		if ((string = EvalHelper.evalString("forward", getForwardExpr(), this, pageContext)) != null) {
			setForward(string);
		}

		if ((string = EvalHelper.evalString("href", getHrefExpr(), this, pageContext)) != null) {
			setHref(string);
		}

		if ((string = EvalHelper.evalString("name", getNameExpr(), this, pageContext)) != null) {
			setName(string);
		}

		if ((string = EvalHelper.evalString("page", getPageExpr(), this, pageContext)) != null) {
			setPage(string);
		}

		if ((string = EvalHelper.evalString("paramId", getParamIdExpr(), this, pageContext)) != null) {
			setParamId(string);
		}

		if ((string = EvalHelper.evalString("paramName", getParamNameExpr(), this, pageContext)) != null) {
			setParamName(string);
		}

		if ((string = EvalHelper.evalString("paramProperty", getParamPropertyExpr(), this, pageContext)) != null) {
			setParamProperty(string);
		}

		if ((string = EvalHelper.evalString("paramScope", getParamScopeExpr(), this, pageContext)) != null) {
			setParamScope(string);
		}

		if ((string = EvalHelper.evalString("property", getPropertyExpr(), this, pageContext)) != null) {
			setProperty(string);
		}

		if ((string = EvalHelper.evalString("scope", getScopeExpr(), this, pageContext)) != null) {
			setScope(string);
		}

		if ((bool = EvalHelper.evalBoolean("transaction", getTransactionExpr(), this, pageContext)) != null) {
			setTransaction(bool.booleanValue());
		}

		if ((bool = EvalHelper.evalBoolean("useLocalEncoding", getUseLocalEncodingExpr(), this, pageContext)) != null) {
			setUseLocalEncoding(bool.booleanValue());
		}
	}
}
