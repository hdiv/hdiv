/**
 * Copyright 2005-2012 hdiv.org
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

import org.apache.strutsel.taglib.utils.EvalHelper;
import org.hdiv.taglib.html.FrameTagHDIV;

/**
 * Generate an HTML <code>&lt;frame&gt;</code> tag with similar capabilities
 * as those the <code>&lt;html:link&gt;</code> tag provides for hyperlink
 * elements. The <code>src</code> element is rendered using the same technique
 * that {@link org.hdiv.taglib.html.LinkTagHDIV LinkTagHDIV} uses to render the
 * <code>href</code> attribute of a hyperlink. Additionally, the HTML 4.0
 * frame tag attributes <code>noresize</code>, <code>scrolling</code>,
 * <code>marginheight</code>, <code>marginwidth</code>,
 * <code>frameborder</code>, and <code>longdesc</code> are supported. The
 * frame <code>name</code> attribute is rendered based on the
 * <code>frameName</code> property.
 * <p>
 * Note that the value of <code>longdesc</code> is intended to be a URI, but
 * currently no rewriting is supported. The attribute is set directly from the
 * property value.
 * <p>
 * This class is a subclass of the class
 * <code>org.hdiv.taglib.html.FrameTagHDIV</code> which provides most of the
 * described functionality. This subclass allows all attribute values to be
 * specified as expressions utilizing the JavaServer Pages Standard Library
 * expression language.
 * 
 * @author Gorka Vicente
 * @since HDIV 2.0
 */
public class ELFrameTagHDIV extends FrameTagHDIV {

	/**
	 * Instance variable mapped to "action" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	private String actionExpr;

	/**
	 * Instance variable mapped to "module" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	private String moduleExpr;

	/**
	 * Instance variable mapped to "anchor" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	private String anchorExpr;

	/**
	 * Instance variable mapped to "bundle" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	private String bundleExpr;

	/**
	 * Instance variable mapped to "forward" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	private String forwardExpr;

	/**
	 * Instance variable mapped to "frameborder" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	private String frameborderExpr;

	/**
	 * Instance variable mapped to "frameName" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	private String frameNameExpr;

	/**
	 * Instance variable mapped to "href" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	private String hrefExpr;

	/**
	 * Instance variable mapped to "longdesc" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	private String longdescExpr;

	/**
	 * Instance variable mapped to "marginheight" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	private String marginheightExpr;

	/**
	 * Instance variable mapped to "marginwidth" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	private String marginwidthExpr;

	/**
	 * Instance variable mapped to "name" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	private String nameExpr;

	/**
	 * Instance variable mapped to "noresize" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	private String noresizeExpr;

	/**
	 * Instance variable mapped to "page" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	private String pageExpr;

	/**
	 * Instance variable mapped to "paramId" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	private String paramIdExpr;

	/**
	 * Instance variable mapped to "paramName" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	private String paramNameExpr;

	/**
	 * Instance variable mapped to "paramProperty" tag attribute. (Mapping set
	 * in associated BeanInfo class.)
	 */
	private String paramPropertyExpr;

	/**
	 * Instance variable mapped to "paramScope" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	private String paramScopeExpr;

	/**
	 * Instance variable mapped to "property" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	private String propertyExpr;

	/**
	 * Instance variable mapped to "scope" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	private String scopeExpr;

	/**
	 * Instance variable mapped to "scrolling" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	private String scrollingExpr;

	/**
	 * Instance variable mapped to "style" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	private String styleExpr;

	/**
	 * Instance variable mapped to "styleClass" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	private String styleClassExpr;

	/**
	 * Instance variable mapped to "styleId" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	private String styleIdExpr;

	/**
	 * Instance variable mapped to "title" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	private String titleExpr;

	/**
	 * Instance variable mapped to "titleKey" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	private String titleKeyExpr;

	/**
	 * Instance variable mapped to "transaction" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	private String transactionExpr;

	/**
	 * Getter method for "action" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public String getActionExpr() {
		return (actionExpr);
	}

	/**
	 * Getter method for "module" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public String getModuleExpr() {
		return (moduleExpr);
	}

	/**
	 * Getter method for "anchor" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public String getAnchorExpr() {
		return (anchorExpr);
	}

	/**
	 * Getter method for "bundle" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public String getBundleExpr() {
		return (bundleExpr);
	}

	/**
	 * Getter method for "forward" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public String getForwardExpr() {
		return (forwardExpr);
	}

	/**
	 * Getter method for "frameborder" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public String getFrameborderExpr() {
		return (frameborderExpr);
	}

	/**
	 * Getter method for "frameName" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public String getFrameNameExpr() {
		return (frameNameExpr);
	}

	/**
	 * Getter method for "href" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public String getHrefExpr() {
		return (hrefExpr);
	}

	/**
	 * Getter method for "longdesc" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public String getLongdescExpr() {
		return (longdescExpr);
	}

	/**
	 * Getter method for "marginheight" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	public String getMarginheightExpr() {
		return (marginheightExpr);
	}

	/**
	 * Getter method for "marginwidth" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public String getMarginwidthExpr() {
		return (marginwidthExpr);
	}

	/**
	 * Getter method for "name" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public String getNameExpr() {
		return (nameExpr);
	}

	/**
	 * Getter method for "noresize" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public String getNoresizeExpr() {
		return (noresizeExpr);
	}

	/**
	 * Getter method for "page" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public String getPageExpr() {
		return (pageExpr);
	}

	/**
	 * Getter method for "paramId" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public String getParamIdExpr() {
		return (paramIdExpr);
	}

	/**
	 * Getter method for "paramName" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public String getParamNameExpr() {
		return (paramNameExpr);
	}

	/**
	 * Getter method for "paramProperty" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	public String getParamPropertyExpr() {
		return (paramPropertyExpr);
	}

	/**
	 * Getter method for "paramScope" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public String getParamScopeExpr() {
		return (paramScopeExpr);
	}

	/**
	 * Getter method for "property" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public String getPropertyExpr() {
		return (propertyExpr);
	}

	/**
	 * Getter method for "scope" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public String getScopeExpr() {
		return (scopeExpr);
	}

	/**
	 * Getter method for "scrolling" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public String getScrollingExpr() {
		return (scrollingExpr);
	}

	/**
	 * Getter method for "style" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public String getStyleExpr() {
		return (styleExpr);
	}

	/**
	 * Getter method for "styleClass" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public String getStyleClassExpr() {
		return (styleClassExpr);
	}

	/**
	 * Getter method for "styleId" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public String getStyleIdExpr() {
		return (styleIdExpr);
	}

	/**
	 * Getter method for "title" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public String getTitleExpr() {
		return (titleExpr);
	}

	/**
	 * Getter method for "titleKey" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public String getTitleKeyExpr() {
		return (titleKeyExpr);
	}

	/**
	 * Getter method for "transaction" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public String getTransactionExpr() {
		return (transactionExpr);
	}

	/**
	 * Setter method for "action" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public void setActionExpr(String actionExpr) {
		this.actionExpr = actionExpr;
	}

	/**
	 * Setter method for "module" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public void setModuleExpr(String moduleExpr) {
		this.moduleExpr = moduleExpr;
	}

	/**
	 * Setter method for "anchor" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public void setAnchorExpr(String anchorExpr) {
		this.anchorExpr = anchorExpr;
	}

	/**
	 * Setter method for "bundle" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public void setBundleExpr(String bundleExpr) {
		this.bundleExpr = bundleExpr;
	}

	/**
	 * Setter method for "forward" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public void setForwardExpr(String forwardExpr) {
		this.forwardExpr = forwardExpr;
	}

	/**
	 * Setter method for "frameborder" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public void setFrameborderExpr(String frameborderExpr) {
		this.frameborderExpr = frameborderExpr;
	}

	/**
	 * Setter method for "frameName" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public void setFrameNameExpr(String frameNameExpr) {
		this.frameNameExpr = frameNameExpr;
	}

	/**
	 * Setter method for "href" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public void setHrefExpr(String hrefExpr) {
		this.hrefExpr = hrefExpr;
	}

	/**
	 * Setter method for "longdesc" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public void setLongdescExpr(String longdescExpr) {
		this.longdescExpr = longdescExpr;
	}

	/**
	 * Setter method for "marginheight" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	public void setMarginheightExpr(String marginheightExpr) {
		this.marginheightExpr = marginheightExpr;
	}

	/**
	 * Setter method for "marginwidth" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public void setMarginwidthExpr(String marginwidthExpr) {
		this.marginwidthExpr = marginwidthExpr;
	}

	/**
	 * Setter method for "name" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public void setNameExpr(String nameExpr) {
		this.nameExpr = nameExpr;
	}

	/**
	 * Setter method for "noresize" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public void setNoresizeExpr(String noresizeExpr) {
		this.noresizeExpr = noresizeExpr;
	}

	/**
	 * Setter method for "page" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public void setPageExpr(String pageExpr) {
		this.pageExpr = pageExpr;
	}

	/**
	 * Setter method for "paramId" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public void setParamIdExpr(String paramIdExpr) {
		this.paramIdExpr = paramIdExpr;
	}

	/**
	 * Setter method for "paramName" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public void setParamNameExpr(String paramNameExpr) {
		this.paramNameExpr = paramNameExpr;
	}

	/**
	 * Setter method for "paramProperty" tag attribute. (Mapping set in
	 * associated BeanInfo class.)
	 */
	public void setParamPropertyExpr(String paramPropertyExpr) {
		this.paramPropertyExpr = paramPropertyExpr;
	}

	/**
	 * Setter method for "paramScope" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public void setParamScopeExpr(String paramScopeExpr) {
		this.paramScopeExpr = paramScopeExpr;
	}

	/**
	 * Setter method for "property" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public void setPropertyExpr(String propertyExpr) {
		this.propertyExpr = propertyExpr;
	}

	/**
	 * Setter method for "scope" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public void setScopeExpr(String scopeExpr) {
		this.scopeExpr = scopeExpr;
	}

	/**
	 * Setter method for "scrolling" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public void setScrollingExpr(String scrollingExpr) {
		this.scrollingExpr = scrollingExpr;
	}

	/**
	 * Setter method for "style" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public void setStyleExpr(String styleExpr) {
		this.styleExpr = styleExpr;
	}

	/**
	 * Setter method for "styleClass" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public void setStyleClassExpr(String styleClassExpr) {
		this.styleClassExpr = styleClassExpr;
	}

	/**
	 * Setter method for "styleId" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public void setStyleIdExpr(String styleIdExpr) {
		this.styleIdExpr = styleIdExpr;
	}

	/**
	 * Setter method for "title" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public void setTitleExpr(String titleExpr) {
		this.titleExpr = titleExpr;
	}

	/**
	 * Setter method for "titleKey" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public void setTitleKeyExpr(String titleKeyExpr) {
		this.titleKeyExpr = titleKeyExpr;
	}

	/**
	 * Setter method for "transaction" tag attribute. (Mapping set in associated
	 * BeanInfo class.)
	 */
	public void setTransactionExpr(String transactionExpr) {
		this.transactionExpr = transactionExpr;
	}

	/**
	 * Resets attribute values for tag reuse.
	 */
	public void release() {
		super.release();
		setActionExpr(null);
		setModuleExpr(null);
		setAnchorExpr(null);
		setBundleExpr(null);
		setForwardExpr(null);
		setFrameborderExpr(null);
		setFrameNameExpr(null);
		setHrefExpr(null);
		setLongdescExpr(null);
		setMarginheightExpr(null);
		setMarginwidthExpr(null);
		setNameExpr(null);
		setNoresizeExpr(null);
		setPageExpr(null);
		setParamIdExpr(null);
		setParamNameExpr(null);
		setParamPropertyExpr(null);
		setParamScopeExpr(null);
		setPropertyExpr(null);
		setScopeExpr(null);
		setScrollingExpr(null);
		setStyleExpr(null);
		setStyleClassExpr(null);
		setStyleIdExpr(null);
		setTitleExpr(null);
		setTitleKeyExpr(null);
		setTransactionExpr(null);
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
	 * Processes all attribute values which use the JSTL expression evaluation
	 * engine to determine their values.
	 * 
	 * @throws JspException if a JSP exception has occurred
	 */
	private void evaluateExpressions() throws JspException {
		String string = null;
		Integer integer = null;
		Boolean bool = null;

		if ((string = EvalHelper.evalString("action", getActionExpr(), this, pageContext)) != null) {
			setAction(string);
		}

		if ((string = EvalHelper.evalString("module", getModuleExpr(), this, pageContext)) != null) {
			setModule(string);
		}

		if ((string = EvalHelper.evalString("anchor", getAnchorExpr(), this, pageContext)) != null) {
			setAnchor(string);
		}

		if ((string = EvalHelper.evalString("bundle", getBundleExpr(), this, pageContext)) != null) {
			setBundle(string);
		}

		if ((string = EvalHelper.evalString("forward", getForwardExpr(), this, pageContext)) != null) {
			setForward(string);
		}

		if ((string = EvalHelper.evalString("frameborder", getFrameborderExpr(), this, pageContext)) != null) {
			setFrameborder(string);
		}

		if ((string = EvalHelper.evalString("frameName", getFrameNameExpr(), this, pageContext)) != null) {
			setFrameName(string);
		}

		if ((string = EvalHelper.evalString("href", getHrefExpr(), this, pageContext)) != null) {
			setHref(string);
		}

		if ((string = EvalHelper.evalString("longdesc", getLongdescExpr(), this, pageContext)) != null) {
			setLongdesc(string);
		}

		if ((integer = EvalHelper.evalInteger("marginheight", getMarginheightExpr(), this, pageContext)) != null) {
			setMarginheight(integer);
		}

		if ((integer = EvalHelper.evalInteger("marginwidth", getMarginwidthExpr(), this, pageContext)) != null) {
			setMarginwidth(integer);
		}

		if ((string = EvalHelper.evalString("name", getNameExpr(), this, pageContext)) != null) {
			setName(string);
		}

		if ((bool = EvalHelper.evalBoolean("noresize", getNoresizeExpr(), this, pageContext)) != null) {
			setNoresize(bool.booleanValue());
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

		if ((string = EvalHelper.evalString("scrolling", getScrollingExpr(), this, pageContext)) != null) {
			setScrolling(string);
		}

		if ((string = EvalHelper.evalString("style", getStyleExpr(), this, pageContext)) != null) {
			setStyle(string);
		}

		if ((string = EvalHelper.evalString("styleClass", getStyleClassExpr(), this, pageContext)) != null) {
			setStyleClass(string);
		}

		if ((string = EvalHelper.evalString("styleId", getStyleIdExpr(), this, pageContext)) != null) {
			setStyleId(string);
		}

		if ((string = EvalHelper.evalString("title", getTitleExpr(), this, pageContext)) != null) {
			setTitle(string);
		}

		if ((string = EvalHelper.evalString("titleKey", getTitleKeyExpr(), this, pageContext)) != null) {
			setTitleKey(string);
		}

		if ((bool = EvalHelper.evalBoolean("transaction", getTransactionExpr(), this, pageContext)) != null) {
			setTransaction(bool.booleanValue());
		}
	}
}
