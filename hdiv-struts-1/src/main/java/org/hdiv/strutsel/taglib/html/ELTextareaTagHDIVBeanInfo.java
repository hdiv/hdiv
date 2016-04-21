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
 * This is the <code>BeanInfo</code> descriptor for the <code>org.hdiv.strutsel.taglib.html.ELTextareaTagHDIV</code>
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
public class ELTextareaTagHDIVBeanInfo extends SimpleBeanInfo {

	public PropertyDescriptor[] getPropertyDescriptors() {
		ArrayList proplist = new ArrayList();

		try {
			proplist.add(new PropertyDescriptor("accesskey", ELTextareaTagHDIV.class, null, "setAccesskeyExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("alt", ELTextareaTagHDIV.class, null, "setAltExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("altKey", ELTextareaTagHDIV.class, null, "setAltKeyExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("bundle", ELTextareaTagHDIV.class, null, "setBundleExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("cols", ELTextareaTagHDIV.class, null, "setColsExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("dir", ELTextareaTagHDIV.class, null, "setDirExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("disabled", ELTextareaTagHDIV.class, null, "setDisabledExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("errorKey", ELTextareaTagHDIV.class, null, "setErrorKeyExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("errorStyle", ELTextareaTagHDIV.class, null, "setErrorStyleExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("errorStyleClass", ELTextareaTagHDIV.class, null,
					"setErrorStyleClassExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("errorStyleId", ELTextareaTagHDIV.class, null, "setErrorStyleIdExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("indexed", ELTextareaTagHDIV.class, null, "setIndexedExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("lang", ELTextareaTagHDIV.class, null, "setLangExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("name", ELTextareaTagHDIV.class, null, "setNameExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onblur", ELTextareaTagHDIV.class, null, "setOnblurExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onchange", ELTextareaTagHDIV.class, null, "setOnchangeExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onclick", ELTextareaTagHDIV.class, null, "setOnclickExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("ondblclick", ELTextareaTagHDIV.class, null, "setOndblclickExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onfocus", ELTextareaTagHDIV.class, null, "setOnfocusExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onkeydown", ELTextareaTagHDIV.class, null, "setOnkeydownExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onkeypress", ELTextareaTagHDIV.class, null, "setOnkeypressExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onkeyup", ELTextareaTagHDIV.class, null, "setOnkeyupExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmousedown", ELTextareaTagHDIV.class, null, "setOnmousedownExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmousemove", ELTextareaTagHDIV.class, null, "setOnmousemoveExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmouseout", ELTextareaTagHDIV.class, null, "setOnmouseoutExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmouseover", ELTextareaTagHDIV.class, null, "setOnmouseoverExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmouseup", ELTextareaTagHDIV.class, null, "setOnmouseupExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onselect", ELTextareaTagHDIV.class, null, "setOnselectExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("property", ELTextareaTagHDIV.class, null, "setPropertyExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("readonly", ELTextareaTagHDIV.class, null, "setReadonlyExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("rows", ELTextareaTagHDIV.class, null, "setRowsExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("style", ELTextareaTagHDIV.class, null, "setStyleExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("styleClass", ELTextareaTagHDIV.class, null, "setStyleClassExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("styleId", ELTextareaTagHDIV.class, null, "setStyleIdExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("tabindex", ELTextareaTagHDIV.class, null, "setTabindexExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("title", ELTextareaTagHDIV.class, null, "setTitleExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("titleKey", ELTextareaTagHDIV.class, null, "setTitleKeyExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("value", ELTextareaTagHDIV.class, null, "setValueExpr"));
		}
		catch (IntrospectionException ex) {
		}

		PropertyDescriptor[] result = new PropertyDescriptor[proplist.size()];

		return ((PropertyDescriptor[]) proplist.toArray(result));
	}
}
