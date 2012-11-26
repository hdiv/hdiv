/**
 * Copyright 2005-2012 hdiv.org
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
 * <code>org.hdiv.strutsel.taglib.html.ELLinkTagHDIV</code> class. It is
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
public class ELLinkTagHDIVBeanInfo extends SimpleBeanInfo {

	public PropertyDescriptor[] getPropertyDescriptors() {
		ArrayList proplist = new ArrayList();

		try {
			proplist.add(new PropertyDescriptor("accesskey", ELLinkTagHDIV.class, null, "setAccesskeyExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("action", ELLinkTagHDIV.class, null, "setActionExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("module", ELLinkTagHDIV.class, null, "setModuleExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("anchor", ELLinkTagHDIV.class, null, "setAnchorExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("bundle", ELLinkTagHDIV.class, null, "setBundleExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("forward", ELLinkTagHDIV.class, null, "setForwardExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("dir", ELLinkTagHDIV.class, null, "setDirExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("href", ELLinkTagHDIV.class, null, "setHrefExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("indexed", ELLinkTagHDIV.class, null, "setIndexedExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("indexId", ELLinkTagHDIV.class, null, "setIndexIdExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("lang", ELLinkTagHDIV.class, null, "setLangExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("linkName", ELLinkTagHDIV.class, null, "setLinkNameExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("name", ELLinkTagHDIV.class, null, "setNameExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onblur", ELLinkTagHDIV.class, null, "setOnblurExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onclick", ELLinkTagHDIV.class, null, "setOnclickExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("ondblclick", ELLinkTagHDIV.class, null, "setOndblclickExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onfocus", ELLinkTagHDIV.class, null, "setOnfocusExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onkeydown", ELLinkTagHDIV.class, null, "setOnkeydownExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onkeypress", ELLinkTagHDIV.class, null, "setOnkeypressExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onkeyup", ELLinkTagHDIV.class, null, "setOnkeyupExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmousedown", ELLinkTagHDIV.class, null, "setOnmousedownExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmousemove", ELLinkTagHDIV.class, null, "setOnmousemoveExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmouseout", ELLinkTagHDIV.class, null, "setOnmouseoutExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmouseover", ELLinkTagHDIV.class, null, "setOnmouseoverExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("onmouseup", ELLinkTagHDIV.class, null, "setOnmouseupExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("page", ELLinkTagHDIV.class, null, "setPageExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("paramId", ELLinkTagHDIV.class, null, "setParamIdExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("paramName", ELLinkTagHDIV.class, null, "setParamNameExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("paramProperty", ELLinkTagHDIV.class, null, "setParamPropertyExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("paramScope", ELLinkTagHDIV.class, null, "setParamScopeExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("property", ELLinkTagHDIV.class, null, "setPropertyExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("scope", ELLinkTagHDIV.class, null, "setScopeExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("style", ELLinkTagHDIV.class, null, "setStyleExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("styleClass", ELLinkTagHDIV.class, null, "setStyleClassExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("styleId", ELLinkTagHDIV.class, null, "setStyleIdExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("tabindex", ELLinkTagHDIV.class, null, "setTabindexExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("target", ELLinkTagHDIV.class, null, "setTargetExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("title", ELLinkTagHDIV.class, null, "setTitleExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("titleKey", ELLinkTagHDIV.class, null, "setTitleKeyExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("transaction", ELLinkTagHDIV.class, null, "setTransactionExpr"));
		} catch (IntrospectionException ex) {
		}

		try {
			proplist.add(new PropertyDescriptor("useLocalEncoding", ELLinkTagHDIV.class, null, "setUseLocalEncodingExpr"));
		} catch (IntrospectionException ex) {
		}

		PropertyDescriptor[] result = new PropertyDescriptor[proplist.size()];

		return ((PropertyDescriptor[]) proplist.toArray(result));
	}
}
