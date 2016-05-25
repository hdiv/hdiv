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
package org.hdiv.taglib.nested.html;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.nested.NestedNameSupport;
import org.apache.struts.taglib.nested.NestedPropertyHelper;
import org.hdiv.taglib.html.MultiboxTagHDIV;

/**
 * <p>
 * This tag is an extension of the <code>&lt;html:multibox&gt;</code> tag.
 * </p>
 * 
 * @author Gorka Vicente
 * @see org.hdiv.taglib.html.MultiboxTagHDIV
 */
public class NestedMultiboxTagHDIV extends MultiboxTagHDIV implements NestedNameSupport {

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized
	 * object.
	 */
	private static final long serialVersionUID = 5398511484328451925L;

	private String originalName = null;

	private String originalProperty = null;

	/**
	 * Overriding method of the heart of the matter. Gets the relative property and leaves the rest up to the original tag implementation.
	 * Sweet.
	 * @return int JSP continuation directive. This is in the hands of the super class.
	 */
	public int doStartTag() throws JspException {

		// get the original properties
		originalName = getName();
		originalProperty = getProperty();

		// request
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		// set the properties
		NestedPropertyHelper.setNestedProperties(request, this);

		// let the super do it's thing
		return super.doStartTag();
	}

	/**
	 * Complete the processing of the tag. The nested tags here will restore all the original value for the tag itself and the nesting
	 * context.
	 * @return int to describe the next step for the JSP processor
	 * @throws JspException for the bad things JSP's do
	 */
	public int doEndTag() throws JspException {

		// do the super's ending part
		int i = super.doEndTag();

		// reset the properties
		setName(originalName);
		setProperty(originalProperty);

		// continue
		return i;
	}

	/**
	 * Release the tag's resources and reset the values.
	 */
	public void release() {

		super.release();
		// reset the originals
		originalName = null;
		originalProperty = null;
	}

}