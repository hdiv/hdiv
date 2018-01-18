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
 * This is the <code>BeanInfo</code> descriptor for the <code>org.apache.strutsel.taglib.html.ELButtonTag</code> class. It is needed to
 * override the default mapping of custom tag attribute names to class attribute names.
 * <p>
 * This is because the value of the unevaluated EL expression has to be kept separately from the evaluated value, which is stored in the
 * base class. This is related to the fact that the JSP compiler can choose to reuse different tag instances if they received the same
 * original attribute values, and the JSP compiler can choose to not re-call the setter methods, because it can assume the same values are
 * already set.
 * 
 * @author Gorka Vicente
 * @since HDIV 2.0
 */
@SuppressWarnings("unchecked")
public class ELButtonTagHDIVBeanInfo extends SimpleBeanInfo {

	@Override
	public PropertyDescriptor[] getPropertyDescriptors() {
		ArrayList proplist = new ArrayList();

		try {
			proplist.add(new PropertyDescriptor("accesskey", ELButtonTagHDIV.class, null, "setAccesskeyExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("alt", ELButtonTagHDIV.class, null, "setAltExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("altKey", ELButtonTagHDIV.class, null, "setAltKeyExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("bundle", ELButtonTagHDIV.class, null, "setBundleExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("dir", ELButtonTagHDIV.class, null, "setDirExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("disabled", ELButtonTagHDIV.class, null, "setDisabledExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("indexed", ELButtonTagHDIV.class, null, "setIndexedExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("lang", ELButtonTagHDIV.class, null, "setLangExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onblur", ELButtonTagHDIV.class, null, "setOnblurExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onchange", ELButtonTagHDIV.class, null, "setOnchangeExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onclick", ELButtonTagHDIV.class, null, "setOnclickExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("ondblclick", ELButtonTagHDIV.class, null, "setOndblclickExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onfocus", ELButtonTagHDIV.class, null, "setOnfocusExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onkeydown", ELButtonTagHDIV.class, null, "setOnkeydownExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onkeypress", ELButtonTagHDIV.class, null, "setOnkeypressExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onkeyup", ELButtonTagHDIV.class, null, "setOnkeyupExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmousedown", ELButtonTagHDIV.class, null, "setOnmousedownExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmousemove", ELButtonTagHDIV.class, null, "setOnmousemoveExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmouseout", ELButtonTagHDIV.class, null, "setOnmouseoutExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmouseover", ELButtonTagHDIV.class, null, "setOnmouseoverExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmouseup", ELButtonTagHDIV.class, null, "setOnmouseupExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("property", ELButtonTagHDIV.class, null, "setPropertyExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("style", ELButtonTagHDIV.class, null, "setStyleExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("styleClass", ELButtonTagHDIV.class, null, "setStyleClassExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("styleId", ELButtonTagHDIV.class, null, "setStyleIdExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("tabindex", ELButtonTagHDIV.class, null, "setTabindexExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("title", ELButtonTagHDIV.class, null, "setTitleExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("titleKey", ELButtonTagHDIV.class, null, "setTitleKeyExpr"));
		}
		catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("value", ELButtonTagHDIV.class, null, "setValueExpr"));
		}
		catch (IntrospectionException ex) {
		}

		PropertyDescriptor[] result = new PropertyDescriptor[proplist.size()];

		return (PropertyDescriptor[]) proplist.toArray(result);
	}
}
