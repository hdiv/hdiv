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
import javax.servlet.jsp.PageContext;

import org.apache.struts.Globals;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.MultiboxTag;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.util.HDIVUtil;

/**
 * <p>
 * Renders an HTML &lt;input&gt; element of type <code>checkbox</code>, whose "checked" status is initialized based on whether the specified
 * value matches one of the elements of the underlying property's array of current values. This element is useful when you have large
 * numbers of checkboxes, and prefer to combine the values into a single array-valued property instead of multiple boolean properties. This
 * tag is only valid when nested inside a form tag body.
 * </p>
 * <p>
 * <strong>WARNING</strong>: In order to correctly recognize cases where none of the associated checkboxes are selected, the
 * <code>ActionForm</code> bean associated with this form must include a statement setting the corresponding array to zero length in the
 * <code>reset()</code> method.
 * </p>
 * <p>
 * The value to be returned to the server, if this checkbox is selected, must be defined by one of the following methods:
 * </p>
 * <ul>
 * <li>Specify a <code>value</code> attribute, whose contents will be used literally as the value to be returned.</li>
 * <li>Specify no <code>value</code> attribute, and the nested body content of this tag will be used as the value to be returned.</li>
 * </ul>
 *
 * @author Gorka Vicente
 * @see org.apache.struts.taglib.html.MultiboxTag
 */
public class MultiboxTagHDIV extends MultiboxTag {

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized
	 * object.
	 */
	private static final long serialVersionUID = -3943308978040613425L;

	/**
	 * Render the value element
	 *
	 * @param results The StringBuffer that output will be appended to.
	 * @see org.hdiv.dataComposer.IDataComposer#composeFormField(String, String, boolean, String)
	 */
	@Override
	protected String prepareValue(final StringBuffer results) throws JspException {

		final String value = (this.value == null) ? constant : this.value;
		if (value == null) {
			final JspException e = new JspException(messages.getMessage("multiboxTag.value"));
			pageContext.setAttribute(Globals.EXCEPTION_KEY, e, PageContext.REQUEST_SCOPE);
			throw e;
		}

		final HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		final IDataComposer dataComposer = HDIVUtil.getDataComposer(request);
		final String cipheredValue = dataComposer.composeFormField(property, value, false, null);

		prepareAttribute(results, "value", TagUtils.getInstance().filter(cipheredValue));

		// returns unciphered value to check selected options
		return value;
	}

}
