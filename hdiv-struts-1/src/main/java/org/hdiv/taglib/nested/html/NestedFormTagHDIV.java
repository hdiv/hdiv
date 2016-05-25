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
import org.hdiv.taglib.html.FormTagHDIV;

/**
 * <p>
 * This tag is an extension of the <code>&lt;html:form&gt;</code> tag.
 * </p>
 * 
 * @author Gorka Vicente
 * @see org.hdiv.taglib.html.FormTagHDIV
 */
public class NestedFormTagHDIV extends FormTagHDIV implements NestedNameSupport {

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized
	 * object.
	 */
	private static final long serialVersionUID = 5398511484328451925L;

	// original nesting environment
	private String originalNesting = null;

	private String originalNestingName = null;

	/**
	 * The name
	 */
	protected String name = null;

	/**
	 * Return the name.
	 */
	public String getName() {

		return (this.name);

	}

	/**
	 * Set the name.
	 * 
	 * @param name The new name
	 */
	public void setName(String name) {

		this.name = name;

	}

	/**
	 * Get the string value of the "property" property.
	 * 
	 * @return the property property
	 */
	public String getProperty() {
		return "";
	}

	/**
	 * Setter for the "property" property
	 * 
	 * @param newProperty new value for the property
	 */
	public void setProperty(String newProperty) {
	}

	/**
	 * Overriding to allow the chance to set the details of the system, so that dynamic includes can be possible
	 * 
	 * @return int JSP continuation directive.
	 */
	public int doStartTag() throws JspException {
		// store original result
		int temp = super.doStartTag();

		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		// original nesting details
		originalNesting = NestedPropertyHelper.getCurrentProperty(request);
		originalNestingName = NestedPropertyHelper.getCurrentName(request, this);

		// some new details
		NestedPropertyHelper.setProperty(request, null);
		NestedPropertyHelper.setName(request, super.getBeanName());

		// continue
		return temp;
	}

	/**
	 * This is only overriden to clean up the include reference
	 * 
	 * @return int JSP continuation directive.
	 */
	public int doEndTag() throws JspException {
		// super result
		int temp = super.doEndTag();

		// all done. clean up
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		// reset the original nesting values
		if (originalNesting == null) {
			NestedPropertyHelper.deleteReference(request);
		}
		else {
			NestedPropertyHelper.setProperty(request, originalNesting);
			NestedPropertyHelper.setName(request, originalNestingName);
		}

		// return the super result
		return temp;
	}

	/**
	 * Release the tag's resources and reset the values.
	 */
	public void release() {
		// let the super release
		super.release();
		// reset the original value place holders
		originalNesting = null;
		originalNestingName = null;
	}

}