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
package org.hdiv.taglib.html;


import javax.servlet.http.HttpServletRequest;

import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.Constants;
import org.apache.struts.taglib.html.OptionsTag;
import org.apache.struts.taglib.html.SelectTag;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.util.HDIVUtil;


/**
 * <p>
 * This Tag created multiple &lt;select&gt; options from a collection. The associated
 * values displayed to the user may optionally be specified by a second collection,
 * or will be the same as the values themselves. Each collection may be an array of
 * objects, a Collection, an Enumeration, an Iterator, or a Map.
 * </p>
 * <p>
 * The <b>&lt;html:options&gt;</b> element is only valid when nested inside a
 * <b>&lt;html:select&gt;</b> element. Renders a HTML <b>&lt;option&gt;</b>
 * element. This tag can be used multiple times within a single <html:select>
 * element.
 * </p>
 * 
 * @author Gorka Vicente
 * @see org.apache.struts.taglib.html.OptionsTag
 */
public class OptionsTagHDIV extends OptionsTag {

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that
	 * a loaded class corresponds exactly to a serialized object.
	 */	
	private static final long serialVersionUID = -6449963390432302145L;

	/**
	 * Add an option element to the specified StringBuffer based on the specified
	 * parameters.
	 * <p>
	 * Note that this tag specifically does not support the <code>styleId</code>
	 * tag attribute, which causes the HTML <code>id</code> attribute to be
	 * emitted. This is because the HTML specification states that all "id"
	 * attributes in a document have to be unique. This tag will likely generate more
	 * than one <code>option</code> element element, but it cannot use the same
	 * <code>id</code> value. It's conceivable some sort of mechanism to supply an
	 * array of <code>id</code> values could be devised, but that doesn't seem to
	 * be worth the trouble.
	 * 
	 * @param sb StringBuffer accumulating our results
	 * @param value Value to be returned to the server for this option
	 * @param label Value to be shown to the user for this option
	 * @param matched Should this value be marked as selected?
	 * @see org.hdiv.dataComposer.IDataComposer#composeFormField(String, String, boolean, String)
	 */
	protected void addOption(StringBuffer sb, String value, String label, boolean matched) {
		
		SelectTag selectTag = (SelectTag) pageContext.getAttribute(Constants.SELECT_KEY);
		
		sb.append("<option value=\"");
		String cipheredValue = null;

		HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
		IDataComposer dataComposer = HDIVUtil.getDataComposer(request);
		cipheredValue = dataComposer.composeFormField(selectTag.getProperty(), value, false, null);

		if (filter) {
			sb.append(TagUtils.getInstance().filter(cipheredValue));
		} else {
			sb.append(cipheredValue);
		}
		sb.append("\"");
		if (matched) {
			renderAttribute(sb, "selected", "selected");
		}
		
        renderAttribute(sb, "style", this.getStyle());
        renderAttribute(sb, "class", this.getStyleClass());
		
		sb.append(">");

		if (filter) {
			sb.append(TagUtils.getInstance().filter(label));
		} else {
			sb.append(label);
		}

		sb.append("</option>\r\n");
	}
	
	/**
	 * Prepares an attribute if the value is not null, appending it to the the given
	 * StringBuffer.
	 * @param handlers The StringBuffer that output will be appended to.
	 */
	protected void renderAttribute(StringBuffer handlers, String name, Object value) {

		if (value != null) {
			handlers.append(" ");
			handlers.append(name);
			handlers.append("=\"");
			handlers.append(value);
			handlers.append("\"");
		}
	}     
	
}
