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
import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.html.CancelTag;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.util.HDIVUtil;

/**
 * <p>
 * Renders an HTML &lt;input&gt; element of type submit. This tag is only valid when
 * nested inside a form tag body. Pressing of this submit button causes the action
 * servlet to bypass calling the associated form bean validate() method. The action
 * is called normally.
 * </p>
 * 
 * @author Gorka Vicente
 * @see org.apache.struts.taglib.html.CancelTag
 */
public class CancelTagHDIV extends CancelTag {
	
	/**
	 * Universal version identifier. Deserialization uses this number to ensure that
	 * a loaded class corresponds exactly to a serialized object.
	 */
	private static final long serialVersionUID = 2674438950379133049L;

	/**
	 * Process the start of this tag.
	 * 
	 * @exception JspException if a JSP exception has occurred
	 * @see org.hdiv.dataComposer.IDataComposer#compose(String, String, boolean)
	 */
	public int doStartTag() throws JspException {

		HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
		IDataComposer dataComposer = HDIVUtil.getDataComposer(request);	

		// this property is editable and we must check it
		dataComposer.compose(property, "", true);

		return super.doStartTag();
	}	

}
