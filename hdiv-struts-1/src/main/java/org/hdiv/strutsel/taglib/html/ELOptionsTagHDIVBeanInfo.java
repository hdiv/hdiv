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
 * <code>org.hdiv.strutsel.taglib.html.ELOptionsTagHDIV</code> class. It is
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
public class ELOptionsTagHDIVBeanInfo extends SimpleBeanInfo {

	public PropertyDescriptor[] getPropertyDescriptors() {
		ArrayList proplist = new ArrayList();

		try {
			proplist.add(new PropertyDescriptor("collection", ELOptionsTagHDIV.class, null, "setCollectionExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("filter", ELOptionsTagHDIV.class, null, "setFilterExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("labelName", ELOptionsTagHDIV.class, null, "setLabelNameExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("labelProperty", ELOptionsTagHDIV.class, null, "setLabelPropertyExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("name", ELOptionsTagHDIV.class, null, "setNameExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("property", ELOptionsTagHDIV.class, null, "setPropertyExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("style", ELOptionsTagHDIV.class, null, "setStyleExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("styleClass", ELOptionsTagHDIV.class, null, "setStyleClassExpr"));
		} catch (IntrospectionException ex) {
		}

		PropertyDescriptor[] result = new PropertyDescriptor[proplist.size()];

		return ((PropertyDescriptor[]) proplist.toArray(result));
	}
}
