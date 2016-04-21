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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.ArrayList;

/**
 * This is the <code>BeanInfo</code> descriptor for the <code>org.apache.strutsel.taglib.html.ELCheckboxTag</code>
 * class. It is needed to override the default mapping of custom tag attribute names to class attribute names.
 * <p>
 * This is because the value of the unevaluated EL expression has to be kept separately from the evaluated value, which
 * is stored in the base class. This is related to the fact that the JSP compiler can choose to reuse different tag
 * instances if they received the same original attribute values, and the JSP compiler can choose to not re-call the
 * setter methods, because it can assume the same values are already set.
 * 
 * @author Gorka Vicente
 * @since HDIV 2.0
 */
public class ELCheckboxTagHDIVBeanInfo extends SimpleBeanInfo {

	public PropertyDescriptor[] getPropertyDescriptors() {
		ArrayList proplist = new ArrayList();

		try {
			proplist.add(new PropertyDescriptor("accesskey", ELCheckboxTagHDIV.class, null, "setAccesskeyExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("alt", ELCheckboxTagHDIV.class, null, "setAltExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("altKey", ELCheckboxTagHDIV.class, null, "setAltKeyExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("bundle", ELCheckboxTagHDIV.class, null, "setBundleExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("dir", ELCheckboxTagHDIV.class, null, "setDirExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("disabled", ELCheckboxTagHDIV.class, null, "setDisabledExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("errorKey", ELCheckboxTagHDIV.class, null, "setErrorKeyExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("errorStyle", ELCheckboxTagHDIV.class, null, "setErrorStyleExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("errorStyleClass", ELCheckboxTagHDIV.class, null,
					"setErrorStyleClassExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("errorStyleId", ELCheckboxTagHDIV.class, null, "setErrorStyleIdExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("indexed", ELCheckboxTagHDIV.class, null, "setIndexedExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("lang", ELCheckboxTagHDIV.class, null, "setLangExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("name", ELCheckboxTagHDIV.class, null, "setNameExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onblur", ELCheckboxTagHDIV.class, null, "setOnblurExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onchange", ELCheckboxTagHDIV.class, null, "setOnchangeExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onclick", ELCheckboxTagHDIV.class, null, "setOnclickExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("ondblclick", ELCheckboxTagHDIV.class, null, "setOndblclickExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onfocus", ELCheckboxTagHDIV.class, null, "setOnfocusExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onkeydown", ELCheckboxTagHDIV.class, null, "setOnkeydownExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onkeypress", ELCheckboxTagHDIV.class, null, "setOnkeypressExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onkeyup", ELCheckboxTagHDIV.class, null, "setOnkeyupExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmousedown", ELCheckboxTagHDIV.class, null, "setOnmousedownExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmousemove", ELCheckboxTagHDIV.class, null, "setOnmousemoveExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmouseout", ELCheckboxTagHDIV.class, null, "setOnmouseoutExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmouseover", ELCheckboxTagHDIV.class, null, "setOnmouseoverExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmouseup", ELCheckboxTagHDIV.class, null, "setOnmouseupExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("property", ELCheckboxTagHDIV.class, null, "setPropertyExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("style", ELCheckboxTagHDIV.class, null, "setStyleExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("styleClass", ELCheckboxTagHDIV.class, null, "setStyleClassExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("styleId", ELCheckboxTagHDIV.class, null, "setStyleIdExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("tabindex", ELCheckboxTagHDIV.class, null, "setTabindexExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("title", ELCheckboxTagHDIV.class, null, "setTitleExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("titleKey", ELCheckboxTagHDIV.class, null, "setTitleKeyExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("value", ELCheckboxTagHDIV.class, null, "setValueExpr"));
		}
		catch (IntrospectionException ex) {
		}

		PropertyDescriptor[] result = new PropertyDescriptor[proplist.size()];

		return ((PropertyDescriptor[]) proplist.toArray(result));
	}
}
