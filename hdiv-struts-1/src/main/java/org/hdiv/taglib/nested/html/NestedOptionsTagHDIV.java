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
package org.hdiv.taglib.nested.html;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.html.Constants;
import org.apache.struts.taglib.nested.NestedNameSupport;
import org.apache.struts.taglib.nested.NestedPropertyHelper;
import org.hdiv.taglib.html.OptionsTagHDIV;

/**
 * <p>
 * This tag is an extension of the <code>&lt;html:options&gt;</code> tag.
 * </p>
 * <p>
 * <strong>Note:</strong> The nested context of this tag relies on the use of the
 * "property" property, and the internal use of the "name" property. The nested tags
 * rely on these properties and will attempt to set them itself. The
 * <code>&lt;html:options&gt;</code> tag this tag extended allows other options for
 * the tag which don't use these properties. To take advantage of these options,
 * markup using the <code>&lt;html:options&gt;</code> tag instead of the nested
 * tag.
 * </p>
 * <p>
 * For example, the "collections" option allows you to specify a separate bean
 * reference which itself is a list of objects with properties to access the title
 * and value parts of the html option tag. You can use this in a nested context (the
 * list is a property of a nested bean) by using the nested define tag and the
 * original options tag.
 * </p>
 * 
 * <pre>
 * &lt;nested:nest property=&quot;myNestedLevel&quot; /&gt;
 *  &lt;nested:define property=&quot;collectionList&quot; /&gt;
 *  &lt;html:options collection=&quot;collectionList&quot;
 *  property=&quot;labelProperty&quot;
 *  valueProperty=&quot;valueProperty&quot; /&gt;
 *  &lt;/nested:nest &gt;
 * </pre>
 * <p>
 * 
 * @author Gorka Vicente
 * @see org.hdiv.taglib.html.OptionsTagHDIV
 */
public class NestedOptionsTagHDIV extends OptionsTagHDIV implements NestedNameSupport {

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that
	 * a loaded class corresponds exactly to a serialized object.
	 */
	private static final long serialVersionUID = 5398511484328451925L;

	private String originalName = null;

	private String originalProperty = null;

	private String originalLabelProperty = null;


	/**
	 * Overriding method of the heart of the matter. Gets the relative property
	 * and leaves the rest up to the original tag implementation. Sweet.
	 * @return int JSP continuation directive.
	 *             This is in the hands of the super class.
	 */
	public int doStartTag() throws JspException {
		// get the original properties
		originalName = getName();
		originalProperty = getProperty();
		originalLabelProperty = getLabelProperty();

		// request
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

		// if we have a label property
		if (originalLabelProperty != null) {
			// do the label property first
			if (getName() == null || Constants.BEAN_KEY.equals(getName())) {
				super.setLabelProperty(NestedPropertyHelper
						.getAdjustedProperty(request, originalLabelProperty));
			} else {
				super.setLabelProperty(originalLabelProperty);
			}
		}

		// set the other properties
		NestedPropertyHelper.setNestedProperties(request, this);

		// let the super do it's thing
		return super.doStartTag();
	}

	/**
	 * Complete the processing of the tag. The nested tags here will restore
	 * all the original value for the tag itself and the nesting context.
	 * @return int to describe the next step for the JSP processor
	 * @throws JspException for the bad things JSP's do
	 */
	public int doEndTag() throws JspException {
		// do the super's ending part
		int i = super.doEndTag();

		// reset the properties
		setName(originalName);
		setProperty(originalProperty);
		setLabelProperty(originalLabelProperty);

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
		originalLabelProperty = null;
	}

}