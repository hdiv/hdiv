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
import org.hdiv.taglib.html.OptionTagHDIV;

/**
 * Tag for select options. The body of this tag is presented to the user in the option list, while the value attribute
 * is the value returned to the server if this option is selected.
 * <p>
 * This class is a subclass of the class <code>org.hdiv.taglib.html.OptionTagHDIV</code> which provides most of the
 * described functionality. This subclass allows all attribute values to be specified as expressions utilizing the
 * JavaServer Pages Standard Library expression language.
 * 
 * @author Gorka Vicente
 * @since HDIV 2.0
 */
public class ELOptionTagHDIV extends OptionTagHDIV {

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
	 * Instance variable mapped to "filter" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String filterExpr;

	/**
	 * Instance variable mapped to "lang" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String langExpr;

	/**
	 * Instance variable mapped to "key" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String keyExpr;

	/**
	 * Instance variable mapped to "locale" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String localeExpr;

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
	 * Instance variable mapped to "value" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String valueExpr;

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
	 * Getter method for "filter" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getFilterExpr() {
		return (filterExpr);
	}

	/**
	 * Getter method for "lang" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getLangExpr() {
		return (langExpr);
	}

	/**
	 * Getter method for "key" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getKeyExpr() {
		return (keyExpr);
	}

	/**
	 * Getter method for "locale" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getLocaleExpr() {
		return (localeExpr);
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
	 * Getter method for "value" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getValueExpr() {
		return (valueExpr);
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
	 * Setter method for "filter" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setFilterExpr(String filterExpr) {
		this.filterExpr = filterExpr;
	}

	/**
	 * Setter method for "key" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setKeyExpr(String keyExpr) {
		this.keyExpr = keyExpr;
	}

	/**
	 * Setter method for "lang" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setLangExpr(String langExpr) {
		this.langExpr = langExpr;
	}

	/**
	 * Setter method for "locale" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setLocaleExpr(String localeExpr) {
		this.localeExpr = localeExpr;
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
		setBundleExpr(null);
		setDirExpr(null);
		setDisabledExpr(null);
		setFilterExpr(null);
		setLangExpr(null);
		setKeyExpr(null);
		setLocaleExpr(null);
		setStyleExpr(null);
		setStyleClassExpr(null);
		setStyleIdExpr(null);
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

		if ((string = EvalHelper.evalString("bundle", getBundleExpr(), this, pageContext)) != null) {
			setBundle(string);
		}

		if ((string = EvalHelper.evalString("dir", getDirExpr(), this, pageContext)) != null) {
			setDir(string);
		}

		if ((bool = EvalHelper.evalBoolean("disabled", getDisabledExpr(), this, pageContext)) != null) {
			setDisabled(bool.booleanValue());
		}

		if ((bool = EvalHelper.evalBoolean("filter", getFilterExpr(), this, pageContext)) != null) {
			setFilter(bool.booleanValue());
		}

		if ((string = EvalHelper.evalString("lang", getLangExpr(), this, pageContext)) != null) {
			setLang(string);
		}

		if ((string = EvalHelper.evalString("key", getKeyExpr(), this, pageContext)) != null) {
			setKey(string);
		}

		if ((string = EvalHelper.evalString("locale", getLocaleExpr(), this, pageContext)) != null) {
			setLocale(string);
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

		if ((string = EvalHelper.evalString("value", getValueExpr(), this, pageContext)) != null) {
			setValue(string);
		}
	}
}
