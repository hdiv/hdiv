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
package org.hdiv.taglib.html;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.html.CheckboxTag;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.util.HDIVUtil;

/**
 * <p>
 * Renders an HTML &lt;input&gt; element of type <code>checkbox</code>, populated
 * from the specified value or the specified property of the bean associated with our
 * current form. This tag is only valid when nested inside a form tag body.
 * </p>
 * <p>
 * <strong>NOTE</strong>: The underlying property value associated with this field
 * should be of type <code>boolean</code>, and any <code>value</code> you
 * specify should correspond to one of the Strings that indicate a true value
 * ("true", "yes", or "on"). If you wish to utilize a set of related String values,
 * consider using the <code>multibox</code> tag.
 * </p>
 * <p>
 * <strong>WARNING</strong>: In order to correctly recognize unchecked checkboxes,
 * the <code>ActionForm</code> bean associated with this form must include a
 * statement setting the corresponding boolean property to <code>false</code> in
 * the <code>reset()</code> method.
 * </p>
 * 
 * @author Gorka Vicente
 * @see org.apache.struts.taglib.html.CheckboxTag
 */
public class CheckboxTagHDIV extends CheckboxTag {

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that
	 * a loaded class corresponds exactly to a serialized object.
	 */
	private static final long serialVersionUID = -5058375036821473167L;

	/**
	 * Process the start of this tag.
	 * 
	 * @exception JspException if a JSP exception has occurred
	 * @see org.hdiv.dataComposer.IDataComposer#composeFormField(String, String, boolean, String)
	 */
	public int doStartTag() throws JspException {

		HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
		IDataComposer dataComposer = HDIVUtil.getDataComposer(request); 	

		// this property is editable and we must check it
		dataComposer.composeFormField(prepareName(), value, true, null);

		return super.doStartTag();
	}		

}
