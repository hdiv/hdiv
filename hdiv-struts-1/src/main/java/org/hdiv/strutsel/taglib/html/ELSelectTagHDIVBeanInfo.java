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
 * <code>org.hdiv.strutsel.taglib.html.ELSelectTagHDIV</code> class. It is
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
public class ELSelectTagHDIVBeanInfo extends SimpleBeanInfo {

	public PropertyDescriptor[] getPropertyDescriptors() {
		ArrayList proplist = new ArrayList();

		try {
			proplist.add(new PropertyDescriptor("alt", ELSelectTagHDIV.class, null, "setAltExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("altKey", ELSelectTagHDIV.class, null, "setAltKeyExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("bundle", ELSelectTagHDIV.class, null, "setBundleExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("dir", ELSelectTagHDIV.class, null, "setDirExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("disabled", ELSelectTagHDIV.class, null, "setDisabledExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("errorKey", ELSelectTagHDIV.class, null, "setErrorKeyExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("errorStyle", ELSelectTagHDIV.class, null, "setErrorStyleExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("errorStyleClass", ELSelectTagHDIV.class, null, "setErrorStyleClassExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("errorStyleId", ELSelectTagHDIV.class, null, "setErrorStyleIdExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("indexed", ELSelectTagHDIV.class, null, "setIndexedExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("lang", ELSelectTagHDIV.class, null, "setLangExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("multiple", ELSelectTagHDIV.class, null, "setMultipleExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("name", ELSelectTagHDIV.class, null, "setNameExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onblur", ELSelectTagHDIV.class, null, "setOnblurExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onchange", ELSelectTagHDIV.class, null, "setOnchangeExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onclick", ELSelectTagHDIV.class, null, "setOnclickExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("ondblclick", ELSelectTagHDIV.class, null, "setOndblclickExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onfocus", ELSelectTagHDIV.class, null, "setOnfocusExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onkeydown", ELSelectTagHDIV.class, null, "setOnkeydownExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onkeypress", ELSelectTagHDIV.class, null, "setOnkeypressExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onkeyup", ELSelectTagHDIV.class, null, "setOnkeyupExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmousedown", ELSelectTagHDIV.class, null, "setOnmousedownExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmousemove", ELSelectTagHDIV.class, null, "setOnmousemoveExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmouseout", ELSelectTagHDIV.class, null, "setOnmouseoutExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmouseover", ELSelectTagHDIV.class, null, "setOnmouseoverExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmouseup", ELSelectTagHDIV.class, null, "setOnmouseupExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("property", ELSelectTagHDIV.class, null, "setPropertyExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("size", ELSelectTagHDIV.class, null, "setSizeExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("style", ELSelectTagHDIV.class, null, "setStyleExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("styleClass", ELSelectTagHDIV.class, null, "setStyleClassExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("styleId", ELSelectTagHDIV.class, null, "setStyleIdExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("tabindex", ELSelectTagHDIV.class, null, "setTabindexExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("title", ELSelectTagHDIV.class, null, "setTitleExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("titleKey", ELSelectTagHDIV.class, null, "setTitleKeyExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("value", ELSelectTagHDIV.class, null, "setValueExpr"));
		} catch (IntrospectionException ex) {
		}

		PropertyDescriptor[] result = new PropertyDescriptor[proplist.size()];

		return ((PropertyDescriptor[]) proplist.toArray(result));
	}
}
