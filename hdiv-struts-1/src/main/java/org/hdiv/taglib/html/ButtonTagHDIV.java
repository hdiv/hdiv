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
import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.html.ButtonTag;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.util.HDIVUtil;

/**
 * <p>
 * Renders an HTML &lt;input&gt; element of type <code>button</code>, populated from the specified value or the content
 * of this tag body. This tag is only valid when nested inside a form tag body.
 * </p>
 * <p>
 * If a graphical button is needed (a button with an image), then the <code>image</code> tag is more appropriate.
 * </p>
 * 
 * @author Gorka Vicente
 * @see org.apache.struts.taglib.html.ButtonTag
 */
public class ButtonTagHDIV extends ButtonTag {

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that a loaded class corresponds exactly
	 * to a serialized object.
	 */
	private static final long serialVersionUID = 5398511484328451925L;

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
		dataComposer.composeFormField(prepareName(), "", true, null);

		return super.doStartTag();
	}

}
