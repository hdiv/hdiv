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
package org.hdiv.strutsel.taglib.html;

import javax.servlet.jsp.JspException;

import org.apache.strutsel.taglib.utils.EvalHelper;
import org.hdiv.taglib.html.FormTagHDIV;

/**
 * Custom tag that represents an input form, associated with a bean whose properties correspond to the various fields of the form.
 * <p>
 * This class is a subclass of the class <code>org.hdiv.taglib.html.FormTagHDIV</code> which provides most of the described functionality.
 * This subclass allows all attribute values to be specified as expressions utilizing the JavaServer Pages Standard Library expression
 * language.
 * 
 * @author Gorka Vicente
 * @since HDIV 2.0
 */
public class ELFormTagHDIV extends FormTagHDIV {

	/**
	 * Instance variable mapped to "action" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String actionExpr;

	/**
	 * Instance variable mapped to "dir" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String dirExpr;

	/**
	 * Instance variable mapped to "disabled" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String disabledExpr;

	/**
	 * Instance variable mapped to "enctype" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String enctypeExpr;

	/**
	 * Instance variable mapped to "focus" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String focusExpr;

	/**
	 * Instance variable mapped to "focusIndex" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String focusIndexExpr;

	/**
	 * Instance variable mapped to "lang" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String langExpr;

	/**
	 * Instance variable mapped to "method" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String methodExpr;

	/**
	 * Instance variable mapped to "onreset" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String onresetExpr;

	/**
	 * Instance variable mapped to "onsubmit" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String onsubmitExpr;

	/**
	 * Instance variable mapped to "readonly" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String readonlyExpr;

	/**
	 * Instance variable mapped to "scriptLanguage" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String scriptLanguageExpr;

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
	 * Instance variable mapped to "target" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String targetExpr;

	/**
	 * Instance variable mapped to "acceptCharset" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	private String acceptCharsetExpr;

	/**
	 * Getter method for "action" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getActionExpr() {
		return (actionExpr);
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
	 * Getter method for "enctype" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getEnctypeExpr() {
		return (enctypeExpr);
	}

	/**
	 * Getter method for "focus" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getFocusExpr() {
		return (focusExpr);
	}

	/**
	 * Getter method for "focusIndex" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getFocusIndexExpr() {
		return (focusIndexExpr);
	}

	/**
	 * Getter method for "lang" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getLangExpr() {
		return (langExpr);
	}

	/**
	 * Getter method for "method" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getMethodExpr() {
		return (methodExpr);
	}

	/**
	 * Getter method for "onreset" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getOnresetExpr() {
		return (onresetExpr);
	}

	/**
	 * Getter method for "onsubmit" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getOnsubmitExpr() {
		return (onsubmitExpr);
	}

	/**
	 * Getter method for "readonly" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getReadonlyExpr() {
		return (readonlyExpr);
	}

	/**
	 * Getter method for "scriptLanguage" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getScriptLanguageExpr() {
		return (scriptLanguageExpr);
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
	 * Getter method for "target" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getTargetExpr() {
		return (targetExpr);
	}

	/**
	 * Getter method for "acceptCharset" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public String getAcceptCharsetExpr() {
		return (acceptCharsetExpr);
	}

	/**
	 * Setter method for "action" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setActionExpr(String actionExpr) {
		this.actionExpr = actionExpr;
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
	 * Setter method for "enctype" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setEnctypeExpr(String enctypeExpr) {
		this.enctypeExpr = enctypeExpr;
	}

	/**
	 * Setter method for "focus" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setFocusExpr(String focusExpr) {
		this.focusExpr = focusExpr;
	}

	/**
	 * Setter method for "focusIndex" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setFocusIndexExpr(String focusIndexExpr) {
		this.focusIndexExpr = focusIndexExpr;
	}

	/**
	 * Setter method for "lang" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setLangExpr(String langExpr) {
		this.langExpr = langExpr;
	}

	/**
	 * Setter method for "method" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setMethodExpr(String methodExpr) {
		this.methodExpr = methodExpr;
	}

	/**
	 * Setter method for "onreset" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setOnresetExpr(String onresetExpr) {
		this.onresetExpr = onresetExpr;
	}

	/**
	 * Setter method for "onsubmit" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setOnsubmitExpr(String onsubmitExpr) {
		this.onsubmitExpr = onsubmitExpr;
	}

	/**
	 * Setter method for "readonly" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setReadonlyExpr(String readonlyExpr) {
		this.readonlyExpr = readonlyExpr;
	}

	/**
	 * Setter method for "scriptLanguage" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setScriptLanguageExpr(String scriptLanguageExpr) {
		this.scriptLanguageExpr = scriptLanguageExpr;
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
	 * Setter method for "target" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setTargetExpr(String targetExpr) {
		this.targetExpr = targetExpr;
	}

	/**
	 * Setter method for "acceptCharset" tag attribute. (Mapping set in associated BeanInfo class.)
	 */
	public void setAcceptCharsetExpr(String acceptCharsetExpr) {
		this.acceptCharsetExpr = acceptCharsetExpr;
	}

	/**
	 * Resets attribute values for tag reuse.
	 */
	public void release() {
		super.release();
		setActionExpr(null);
		setDirExpr(null);
		setDisabledExpr(null);
		setEnctypeExpr(null);
		setFocusExpr(null);
		setFocusIndexExpr(null);
		setLangExpr(null);
		setMethodExpr(null);
		setOnresetExpr(null);
		setOnsubmitExpr(null);
		setReadonlyExpr(null);
		setScriptLanguageExpr(null);
		setStyleExpr(null);
		setStyleClassExpr(null);
		setStyleIdExpr(null);
		setTargetExpr(null);
		setAcceptCharsetExpr(null);
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

		if ((string = EvalHelper.evalString("dir", getDirExpr(), this, pageContext)) != null) {
			setDir(string);
		}

		if ((bool = EvalHelper.evalBoolean("disabled", getDisabledExpr(), this, pageContext)) != null) {
			setDisabled(bool.booleanValue());
		}

		if ((string = EvalHelper.evalString("enctype", getEnctypeExpr(), this, pageContext)) != null) {
			setEnctype(string);
		}

		if ((string = EvalHelper.evalString("focus", getFocusExpr(), this, pageContext)) != null) {
			setFocus(string);
		}

		if ((string = EvalHelper.evalString("focusIndex", getFocusIndexExpr(), this, pageContext)) != null) {
			setFocusIndex(string);
		}

		if ((string = EvalHelper.evalString("lang", getLangExpr(), this, pageContext)) != null) {
			setLang(string);
		}

		if ((string = EvalHelper.evalString("method", getMethodExpr(), this, pageContext)) != null) {
			setMethod(string);
		}

		if ((string = EvalHelper.evalString("onreset", getOnresetExpr(), this, pageContext)) != null) {
			setOnreset(string);
		}

		if ((string = EvalHelper.evalString("onsubmit", getOnsubmitExpr(), this, pageContext)) != null) {
			setOnsubmit(string);
		}

		if ((bool = EvalHelper.evalBoolean("readonly", getReadonlyExpr(), this, pageContext)) != null) {
			setReadonly(bool.booleanValue());
		}

		if ((bool = EvalHelper.evalBoolean("scriptLanguage", getScriptLanguageExpr(), this, pageContext)) != null) {
			setScriptLanguage(bool.booleanValue());
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

		if ((string = EvalHelper.evalString("target", getTargetExpr(), this, pageContext)) != null) {
			setTarget(string);
		}

		if ((string = EvalHelper.evalString("acceptCharset", getAcceptCharsetExpr(), this, pageContext)) != null) {
			setAcceptCharset(string);
		}
	}
}
