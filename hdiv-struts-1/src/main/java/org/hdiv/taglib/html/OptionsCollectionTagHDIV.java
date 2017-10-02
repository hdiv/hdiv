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
package org.hdiv.taglib.html;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.Constants;
import org.apache.struts.taglib.html.OptionsCollectionTag;
import org.apache.struts.taglib.html.SelectTag;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.util.HDIVUtil;

/**
 * <p>
 * Renders a set of HTML <code>&lt;option&gt;</code> elements, representing possible choices for a <code>&lt;select&gt;</code> element. This
 * tag can be used multiple times within a single <code>&lt;html:select&gt;</code> element, either in conjunction with or instead of one or
 * more <code>&lt;html:option&gt;</code> or <code>&lt;html:options&gt;</code> elements.
 * </p>
 * <p>
 * This tag operates on a collection of beans, where each bean has a <strong>label</strong> property and a <strong>value</strong> property.
 * The actual names of these properties can be configured using the <code>label</code> and <code>value</code> attributes of this tag.
 * </p>
 * <p>
 * This tag differs from the <code>&lt;html:options&gt;</code> tag in that it makes more consistent use of the <code>name</code> and
 * <code>property</code> attributes, and allows the collection to be more easily obtained from the enclosing form bean.
 * </p>
 * <p>
 * Note that this tag does not support a <code>styleId</code> attribute, as it would have to apply the value to all the <code>option</code>
 * elements created by this element, which would mean that more than one <code>id</code> element might have the same value, which the HTML
 * specification says is illegal.
 * </p>
 * 
 * @author Gorka Vicente
 * @see org.apache.struts.taglib.html.OptionsCollectionTag
 */
public class OptionsCollectionTagHDIV extends OptionsCollectionTag {

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized
	 * object.
	 */
	private static final long serialVersionUID = -5125638576746874243L;

	/**
	 * Add an option element to the specified StringBuffer based on the specified parameters.
	 * <p>
	 * Note that this tag specifically does not support the <code>styleId</code> tag attribute, which causes the HTML <code>id</code>
	 * attribute to be emitted. This is because the HTML specification states that all "id" attributes in a document have to be unique. This
	 * tag will likely generate more than one <code>option</code> element element, but it cannot use the same <code>id</code> value. It's
	 * conceivable some sort of mechanism to supply an array of <code>id</code> values could be devised, but that doesn't seem to be worth
	 * the trouble.
	 *
	 * @param sb StringBuffer accumulating our results
	 * @param value Value to be returned to the server for this option
	 * @param label Value to be shown to the user for this option
	 * @param matched Should this value be marked as selected?
	 * 
	 * @see org.hdiv.dataComposer.IDataComposer#composeFormField(String, String, boolean, String)
	 */
	@Override
	protected void addOption(final StringBuffer sb, final String label, final String value, final boolean matched) {

		// Acquire the select tag we are associated with. If selectTag is null
		// super.doStartTag returns JspException before invoke this method.
		SelectTag selectTag = (SelectTag) pageContext.getAttribute(Constants.SELECT_KEY);

		sb.append("<option value=\"");

		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		IDataComposer dataComposer = HDIVUtil.getDataComposer(request);
		String cipheredValue = dataComposer != null ? dataComposer.composeFormField(selectTag.getProperty(), value, false, null) : value;

		if (filter) {
			sb.append(TagUtils.getInstance().filter(cipheredValue));
		}
		else {
			sb.append(cipheredValue);
		}
		sb.append("\"");
		if (matched) {
			renderAttribute(sb, "selected", "selected");
		}

		renderAttribute(sb, "style", getStyle());
		renderAttribute(sb, "class", getStyleClass());

		sb.append(">");

		if (filter) {
			sb.append(TagUtils.getInstance().filter(label));
		}
		else {
			sb.append(label);
		}

		sb.append("</option>\r\n");
	}

	/**
	 * Prepares an attribute if the value is not null, appending it to the the given StringBuffer.
	 * @param handlers The StringBuffer that output will be appended to.
	 */
	protected void renderAttribute(final StringBuffer handlers, final String name, final Object value) {

		if (value != null) {
			handlers.append(" ");
			handlers.append(name);
			handlers.append("=\"");
			handlers.append(value);
			handlers.append("\"");
		}
	}

}
