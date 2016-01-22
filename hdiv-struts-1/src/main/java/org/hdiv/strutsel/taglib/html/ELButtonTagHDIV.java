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

import org.apache.strutsel.taglib.utils.EvalHelper;
import org.hdiv.taglib.html.ButtonTagHDIV;

/**
 * Renders an HTML BUTTON tag within the Struts framework.
 * <p>
 * This class is a subclass of the class <code>org.hdiv.taglib.html.ButtonTagHDIV</code> which provides most of the
 * described functionality. This subclass allows all attribute values to be specified as expressions utilizing the
 * JavaServer Pages Standard Library expression language.
 * 
 * @author Gorka Vicente
 * @since HDIV 2.0
 */
public class ELButtonTagHDIV extends ButtonTagHDIV {

	/**
	 * Instance variable mapped to "accessKey" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String accessKeyExpr;

	/**
	 * Instance variable mapped to "alt" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String altExpr;

	/**
	 * Instance variable mapped to "altKey" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String altKeyExpr;

	/**
	 * Instance variable mapped to "bundle" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String bundleExpr;

	/**
	 * Instance variable mapped to "dir" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String dirExpr;

	/**
	 * Instance variable mapped to "disabled" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String disabledExpr;

	/**
	 * Instance variable mapped to "indexed" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String indexedExpr;

	/**
	 * Instance variable mapped to "lang" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String langExpr;

	/**
	 * Instance variable mapped to "onblur" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String onblurExpr;

	/**
	 * Instance variable mapped to "onchange" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String onchangeExpr;

	/**
	 * Instance variable mapped to "onclick" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String onclickExpr;

	/**
	 * Instance variable mapped to "ondblclick" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String ondblclickExpr;

	/**
	 * Instance variable mapped to "onfocus" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String onfocusExpr;

	/**
	 * Instance variable mapped to "onkeydown" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String onkeydownExpr;

	/**
	 * Instance variable mapped to "onkeypress" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String onkeypressExpr;

	/**
	 * Instance variable mapped to "onkeyup" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String onkeyupExpr;

	/**
	 * Instance variable mapped to "onmousedown" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String onmousedownExpr;

	/**
	 * Instance variable mapped to "onmousemove" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String onmousemoveExpr;

	/**
	 * Instance variable mapped to "onmouseout" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String onmouseoutExpr;

	/**
	 * Instance variable mapped to "onmouseover" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String onmouseoverExpr;

	/**
	 * Instance variable mapped to "onmouseup" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String onmouseupExpr;

	/**
	 * Instance variable mapped to "property" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String propertyExpr;

	/**
	 * Instance variable mapped to "style" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String styleExpr;

	/**
	 * Instance variable mapped to "styleClass" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String styleClassExpr;

	/**
	 * Instance variable mapped to "styleId" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String styleIdExpr;

	/**
	 * Instance variable mapped to "tabindex" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String tabindexExpr;

	/**
	 * Instance variable mapped to "title" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String titleExpr;

	/**
	 * Instance variable mapped to "titleKey" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String titleKeyExpr;

	/**
	 * Instance variable mapped to "value" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String valueExpr;

	/**
	 * Getter method for "accessKey" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getAccesskeyExpr() {
		return (accessKeyExpr);
	}

	/**
	 * Getter method for "alt" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getAltExpr() {
		return (altExpr);
	}

	/**
	 * Getter method for "altKey" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getAltKeyExpr() {
		return (altKeyExpr);
	}

	/**
	 * Getter method for "bundle" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getBundleExpr() {
		return (bundleExpr);
	}

	/**
	 * Getter method for "dir" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getDirExpr() {
		return (dirExpr);
	}

	/**
	 * Getter method for "disabled" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getDisabledExpr() {
		return (disabledExpr);
	}

	/**
	 * Getter method for "indexed" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getIndexedExpr() {
		return (indexedExpr);
	}

	/**
	 * Getter method for "lang" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getLangExpr() {
		return (langExpr);
	}

	/**
	 * Getter method for "onblur" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getOnblurExpr() {
		return (onblurExpr);
	}

	/**
	 * Getter method for "onchange" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getOnchangeExpr() {
		return (onchangeExpr);
	}

	/**
	 * Getter method for "onclick" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getOnclickExpr() {
		return (onclickExpr);
	}

	/**
	 * Getter method for "ondblclick" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getOndblclickExpr() {
		return (ondblclickExpr);
	}

	/**
	 * Getter method for "onfocus" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getOnfocusExpr() {
		return (onfocusExpr);
	}

	/**
	 * Getter method for "onkeydown" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getOnkeydownExpr() {
		return (onkeydownExpr);
	}

	/**
	 * Getter method for "onkeypress" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getOnkeypressExpr() {
		return (onkeypressExpr);
	}

	/**
	 * Getter method for "onkeyup" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getOnkeyupExpr() {
		return (onkeyupExpr);
	}

	/**
	 * Getter method for "onmousedown" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getOnmousedownExpr() {
		return (onmousedownExpr);
	}

	/**
	 * Getter method for "onmousemove" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getOnmousemoveExpr() {
		return (onmousemoveExpr);
	}

	/**
	 * Getter method for "onmouseout" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getOnmouseoutExpr() {
		return (onmouseoutExpr);
	}

	/**
	 * Getter method for "onmouseover" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getOnmouseoverExpr() {
		return (onmouseoverExpr);
	}

	/**
	 * Getter method for "onmouseup" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getOnmouseupExpr() {
		return (onmouseupExpr);
	}

	/**
	 * Getter method for "property" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getPropertyExpr() {
		return (propertyExpr);
	}

	/**
	 * Getter method for "style" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getStyleExpr() {
		return (styleExpr);
	}

	/**
	 * Getter method for "styleClass" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getStyleClassExpr() {
		return (styleClassExpr);
	}

	/**
	 * Getter method for "styleId" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getStyleIdExpr() {
		return (styleIdExpr);
	}

	/**
	 * Getter method for "tabindex" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getTabindexExpr() {
		return (tabindexExpr);
	}

	/**
	 * Getter method for "title" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getTitleExpr() {
		return (titleExpr);
	}

	/**
	 * Getter method for "titleKey" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getTitleKeyExpr() {
		return (titleKeyExpr);
	}

	/**
	 * Getter method for "value" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getValueExpr() {
		return (valueExpr);
	}

	/**
	 * Setter method for "accessKey" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setAccesskeyExpr(String accessKeyExpr) {
		this.accessKeyExpr = accessKeyExpr;
	}

	/**
	 * Setter method for "alt" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setAltExpr(String altExpr) {
		this.altExpr = altExpr;
	}

	/**
	 * Setter method for "altKey" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setAltKeyExpr(String altKeyExpr) {
		this.altKeyExpr = altKeyExpr;
	}

	/**
	 * Setter method for "bundle" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setBundleExpr(String bundleExpr) {
		this.bundleExpr = bundleExpr;
	}

	/**
	 * Setter method for "dir" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setDirExpr(String dirExpr) {
		this.dirExpr = dirExpr;
	}

	/**
	 * Setter method for "disabled" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setDisabledExpr(String disabledExpr) {
		this.disabledExpr = disabledExpr;
	}

	/**
	 * Setter method for "indexed" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setIndexedExpr(String indexedExpr) {
		this.indexedExpr = indexedExpr;
	}

	/**
	 * Setter method for "lang" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setLangExpr(String langExpr) {
		this.langExpr = langExpr;
	}

	/**
	 * Setter method for "onblur" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setOnblurExpr(String onblurExpr) {
		this.onblurExpr = onblurExpr;
	}

	/**
	 * Setter method for "onchange" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setOnchangeExpr(String onchangeExpr) {
		this.onchangeExpr = onchangeExpr;
	}

	/**
	 * Setter method for "onclick" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setOnclickExpr(String onclickExpr) {
		this.onclickExpr = onclickExpr;
	}

	/**
	 * Setter method for "ondblclick" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setOndblclickExpr(String ondblclickExpr) {
		this.ondblclickExpr = ondblclickExpr;
	}

	/**
	 * Setter method for "onfocus" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setOnfocusExpr(String onfocusExpr) {
		this.onfocusExpr = onfocusExpr;
	}

	/**
	 * Setter method for "onkeydown" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setOnkeydownExpr(String onkeydownExpr) {
		this.onkeydownExpr = onkeydownExpr;
	}

	/**
	 * Setter method for "onkeypress" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setOnkeypressExpr(String onkeypressExpr) {
		this.onkeypressExpr = onkeypressExpr;
	}

	/**
	 * Setter method for "onkeyup" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setOnkeyupExpr(String onkeyupExpr) {
		this.onkeyupExpr = onkeyupExpr;
	}

	/**
	 * Setter method for "onmousedown" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setOnmousedownExpr(String onmousedownExpr) {
		this.onmousedownExpr = onmousedownExpr;
	}

	/**
	 * Setter method for "onmousemove" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setOnmousemoveExpr(String onmousemoveExpr) {
		this.onmousemoveExpr = onmousemoveExpr;
	}

	/**
	 * Setter method for "onmouseout" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setOnmouseoutExpr(String onmouseoutExpr) {
		this.onmouseoutExpr = onmouseoutExpr;
	}

	/**
	 * Setter method for "onmouseover" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setOnmouseoverExpr(String onmouseoverExpr) {
		this.onmouseoverExpr = onmouseoverExpr;
	}

	/**
	 * Setter method for "onmouseup" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setOnmouseupExpr(String onmouseupExpr) {
		this.onmouseupExpr = onmouseupExpr;
	}

	/**
	 * Setter method for "property" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setPropertyExpr(String propertyExpr) {
		this.propertyExpr = propertyExpr;
	}

	/**
	 * Setter method for "style" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setStyleExpr(String styleExpr) {
		this.styleExpr = styleExpr;
	}

	/**
	 * Setter method for "styleClass" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setStyleClassExpr(String styleClassExpr) {
		this.styleClassExpr = styleClassExpr;
	}

	/**
	 * Setter method for "styleId" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setStyleIdExpr(String styleIdExpr) {
		this.styleIdExpr = styleIdExpr;
	}

	/**
	 * Setter method for "tabindex" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setTabindexExpr(String tabindexExpr) {
		this.tabindexExpr = tabindexExpr;
	}

	/**
	 * Setter method for "title" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setTitleExpr(String titleExpr) {
		this.titleExpr = titleExpr;
	}

	/**
	 * Setter method for "titleKey" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setTitleKeyExpr(String titleKeyExpr) {
		this.titleKeyExpr = titleKeyExpr;
	}

	/**
	 * Setter method for "value" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setValueExpr(String valueExpr) {
		this.valueExpr = valueExpr;
	}

	/**
	 * Resets attribute values for tag reuse.
	 */
	public void release() {
		super.release();
		setAccesskeyExpr(null);
		setAltExpr(null);
		setAltKeyExpr(null);
		setBundleExpr(null);
		setDirExpr(null);
		setDisabledExpr(null);
		setIndexedExpr(null);
		setLangExpr(null);
		setOnblurExpr(null);
		setOnchangeExpr(null);
		setOnclickExpr(null);
		setOndblclickExpr(null);
		setOnfocusExpr(null);
		setOnkeydownExpr(null);
		setOnkeypressExpr(null);
		setOnkeyupExpr(null);
		setOnmousedownExpr(null);
		setOnmousemoveExpr(null);
		setOnmouseoutExpr(null);
		setOnmouseoverExpr(null);
		setOnmouseupExpr(null);
		setPropertyExpr(null);
		setStyleExpr(null);
		setStyleClassExpr(null);
		setStyleIdExpr(null);
		setTabindexExpr(null);
		setTitleExpr(null);
		setTitleKeyExpr(null);
		setValueExpr(null);
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

		if ((string = EvalHelper.evalString("accessKey", getAccesskeyExpr(), this, pageContext)) != null) {
			setAccesskey(string);
		}

		if ((string = EvalHelper.evalString("alt", getAltExpr(), this, pageContext)) != null) {
			setAlt(string);
		}

		if ((string = EvalHelper.evalString("altKey", getAltKeyExpr(), this, pageContext)) != null) {
			setAltKey(string);
		}

		if ((string = EvalHelper.evalString("bundle", getBundleExpr(), this, pageContext)) != null) {
			setBundle(string);
		}

		if ((string = EvalHelper.evalString("dir", getDirExpr(), this, pageContext)) != null) {
			setDir(string);
		}

		if ((bool = EvalHelper.evalBoolean("disabled", getDisabledExpr(), this, pageContext)) != null) {
			setDisabled(bool.booleanValue());
		}

		if ((bool = EvalHelper.evalBoolean("indexed", getIndexedExpr(), this, pageContext)) != null) {
			setIndexed(bool.booleanValue());
		}

		if ((string = EvalHelper.evalString("lang", getLangExpr(), this, pageContext)) != null) {
			setLang(string);
		}

		if ((string = EvalHelper.evalString("onblur", getOnblurExpr(), this, pageContext)) != null) {
			setOnblur(string);
		}

		if ((string = EvalHelper.evalString("onchange", getOnchangeExpr(), this, pageContext)) != null) {
			setOnchange(string);
		}

		if ((string = EvalHelper.evalString("onclick", getOnclickExpr(), this, pageContext)) != null) {
			setOnclick(string);
		}

		if ((string = EvalHelper.evalString("ondblclick", getOndblclickExpr(), this, pageContext)) != null) {
			setOndblclick(string);
		}

		if ((string = EvalHelper.evalString("onfocus", getOnfocusExpr(), this, pageContext)) != null) {
			setOnfocus(string);
		}

		if ((string = EvalHelper.evalString("onkeydown", getOnkeydownExpr(), this, pageContext)) != null) {
			setOnkeydown(string);
		}

		if ((string = EvalHelper.evalString("onkeypress", getOnkeypressExpr(), this, pageContext)) != null) {
			setOnkeypress(string);
		}

		if ((string = EvalHelper.evalString("onkeyup", getOnkeyupExpr(), this, pageContext)) != null) {
			setOnkeyup(string);
		}

		if ((string = EvalHelper.evalString("onmousedown", getOnmousedownExpr(), this, pageContext)) != null) {
			setOnmousedown(string);
		}

		if ((string = EvalHelper.evalString("onmousemove", getOnmousemoveExpr(), this, pageContext)) != null) {
			setOnmousemove(string);
		}

		if ((string = EvalHelper.evalString("onmouseout", getOnmouseoutExpr(), this, pageContext)) != null) {
			setOnmouseout(string);
		}

		if ((string = EvalHelper.evalString("onmouseover", getOnmouseoverExpr(), this, pageContext)) != null) {
			setOnmouseover(string);
		}

		if ((string = EvalHelper.evalString("onmouseup", getOnmouseupExpr(), this, pageContext)) != null) {
			setOnmouseup(string);
		}

		if ((string = EvalHelper.evalString("property", getPropertyExpr(), this, pageContext)) != null) {
			setProperty(string);
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

		if ((string = EvalHelper.evalString("tabindex", getTabindexExpr(), this, pageContext)) != null) {
			setTabindex(string);
		}

		if ((string = EvalHelper.evalString("title", getTitleExpr(), this, pageContext)) != null) {
			setTitle(string);
		}

		if ((string = EvalHelper.evalString("titleKey", getTitleKeyExpr(), this, pageContext)) != null) {
			setTitleKey(string);
		}

		if ((string = EvalHelper.evalString("value", getValueExpr(), this, pageContext)) != null) {
			setValue(string);
		}
	}
}
