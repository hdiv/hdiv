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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.ArrayList;

/**
 * This is the <code>BeanInfo</code> descriptor for the
 * <code>org.apache.strutsel.taglib.html.ELTextTagHDIV</code> class. It is
 * needed to override the default mapping of custom tag attribute names to class
 * attribute names.
 * <p>
 * This is because the value of the unevaluated EL expression has to be kept
 * separately from the evaluated value, which is stored in the base class. This
 * is related to the fact that the JSP compiler can choose to reuse different
 * tag instances if they received the same original attribute values, and the
 * JSP compiler can choose to not re-call the setter methods, because it can
 * assume the same values are already set.
 * 
 * @author Gorka Vicente
 * @since HDIV 2.0
 */
public class ELTextTagHDIVBeanInfo extends SimpleBeanInfo {

	public PropertyDescriptor[] getPropertyDescriptors() {
		ArrayList proplist = new ArrayList();

		try {
			proplist.add(new PropertyDescriptor("accesskey", ELTextTagHDIV.class, null, "setAccesskeyExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("alt", ELTextTagHDIV.class, null, "setAltExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("altKey", ELTextTagHDIV.class, null, "setAltKeyExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("bundle", ELTextTagHDIV.class, null, "setBundleExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("dir", ELTextTagHDIV.class, null, "setDirExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("disabled", ELTextTagHDIV.class, null, "setDisabledExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("errorKey", ELTextTagHDIV.class, null, "setErrorKeyExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("errorStyle", ELTextTagHDIV.class, null, "setErrorStyleExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("errorStyleClass", ELTextTagHDIV.class, null, "setErrorStyleClassExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("errorStyleId", ELTextTagHDIV.class, null, "setErrorStyleIdExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("indexed", ELTextTagHDIV.class, null, "setIndexedExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("lang", ELTextTagHDIV.class, null, "setLangExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("maxlength", ELTextTagHDIV.class, null, "setMaxlengthExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("name", ELTextTagHDIV.class, null, "setNameExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onblur", ELTextTagHDIV.class, null, "setOnblurExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onchange", ELTextTagHDIV.class, null, "setOnchangeExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onclick", ELTextTagHDIV.class, null, "setOnclickExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("ondblclick", ELTextTagHDIV.class, null, "setOndblclickExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onfocus", ELTextTagHDIV.class, null, "setOnfocusExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onkeydown", ELTextTagHDIV.class, null, "setOnkeydownExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onkeypress", ELTextTagHDIV.class, null, "setOnkeypressExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onkeyup", ELTextTagHDIV.class, null, "setOnkeyupExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmousedown", ELTextTagHDIV.class, null, "setOnmousedownExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmousemove", ELTextTagHDIV.class, null, "setOnmousemoveExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmouseout", ELTextTagHDIV.class, null, "setOnmouseoutExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmouseover", ELTextTagHDIV.class, null, "setOnmouseoverExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmouseup", ELTextTagHDIV.class, null, "setOnmouseupExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onselect", ELTextTagHDIV.class, null, "setOnselectExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("property", ELTextTagHDIV.class, null, "setPropertyExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("readonly", ELTextTagHDIV.class, null, "setReadonlyExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("size", ELTextTagHDIV.class, null, "setSizeExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("style", ELTextTagHDIV.class, null, "setStyleExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("styleClass", ELTextTagHDIV.class, null, "setStyleClassExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("styleId", ELTextTagHDIV.class, null, "setStyleIdExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("tabindex", ELTextTagHDIV.class, null, "setTabindexExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("title", ELTextTagHDIV.class, null, "setTitleExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("titleKey", ELTextTagHDIV.class, null, "setTitleKeyExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("value", ELTextTagHDIV.class, null, "setValueExpr"));
		} catch (IntrospectionException ex) {
		}

		PropertyDescriptor[] result = new PropertyDescriptor[proplist.size()];

		return ((PropertyDescriptor[]) proplist.toArray(result));
	}
}
