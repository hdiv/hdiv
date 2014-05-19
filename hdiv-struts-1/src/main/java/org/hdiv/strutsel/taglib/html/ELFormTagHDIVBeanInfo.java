/**
 * Copyright 2005-2013 hdiv.org
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
 * <code>org.hdiv.strutsel.taglib.html.ELFormTagHDIV</code> class. It is
 * needed to override the default mapping of custom tag attribute names to class
 * attribute names.
 * 
 * @author Gorka Vicente
 * @since HDIV 2.0
 */
public class ELFormTagHDIVBeanInfo extends SimpleBeanInfo {

	public PropertyDescriptor[] getPropertyDescriptors() {
		ArrayList proplist = new ArrayList();

		try {
			proplist.add(new PropertyDescriptor("action", ELFormTagHDIV.class, null, "setActionExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("dir", ELFormTagHDIV.class, null, "setDirExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("disabled", ELFormTagHDIV.class, null, "setDisabledExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("enctype", ELFormTagHDIV.class, null, "setEnctypeExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("focus", ELFormTagHDIV.class, null, "setFocusExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("focusIndex", ELFormTagHDIV.class, null, "setFocusIndexExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("lang", ELFormTagHDIV.class, null, "setLangExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("method", ELFormTagHDIV.class, null, "setMethodExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("name", ELFormTagHDIV.class, null, "setNameExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onreset", ELFormTagHDIV.class, null, "setOnresetExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onsubmit", ELFormTagHDIV.class, null, "setOnsubmitExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("readonly", ELFormTagHDIV.class, null, "setReadonlyExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("scope", ELFormTagHDIV.class, null, "setScopeExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("scriptLanguage", ELFormTagHDIV.class, null, "setScriptLanguageExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("style", ELFormTagHDIV.class, null, "setStyleExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("styleClass", ELFormTagHDIV.class, null, "setStyleClassExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("styleId", ELFormTagHDIV.class, null, "setStyleIdExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("target", ELFormTagHDIV.class, null, "setTargetExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("type", ELFormTagHDIV.class, null, "setTypeExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("acceptCharset", ELFormTagHDIV.class, null, "setAcceptCharsetExpr"));
		} catch (IntrospectionException ex) {
		}

		PropertyDescriptor[] result = new PropertyDescriptor[proplist.size()];

		return ((PropertyDescriptor[]) proplist.toArray(result));
	}
}
