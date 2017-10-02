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

import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.Constants;
import org.apache.struts.taglib.html.OptionTag;
import org.apache.struts.taglib.html.SelectTag;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.util.HDIVUtil;

/**
 * <p>
 * Render an HTML <b>&lt;option&gt;</b> element, representing one of the choices for an enclosing <b>&lt;select&gt;</b> element. The text
 * displayed to the user comes from either the body of this tag, or from a message string looked up based on the bundle, locale, and key
 * attributes. The value attribute is the value returned to the server if this option is selected.
 * </p>
 * <p>
 * If the value of the corresponding bean property matches the specified value, this option will be marked selected. This tag is only valid
 * when nested inside a <b>&lt;html:select&gt;</b> tag body.
 * </p>
 * 
 * @author Gorka Vicente
 * @see org.apache.struts.taglib.html.OptionTag
 */
public class OptionTagHDIV extends OptionTag {

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized
	 * object.
	 */
	private static final long serialVersionUID = -8794640501351327833L;

	/**
	 * The message text to be displayed to the user for this tag if no body text and no key to lookup so display the value
	 * 
	 * @see org.hdiv.dataComposer.IDataComposer#composeFormField(String, String, boolean, String)
	 */
	protected String valueWithoutEncrypt = null;

	/**
	 * Process the end of this tag.
	 * @exception JspException if a JSP exception has occurred
	 */
	@Override
	public int doEndTag() throws JspException {

		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		IDataComposer dataComposer = HDIVUtil.getDataComposer(request);

		// Acquire the select tag we are associated with
		SelectTag selectTag = (SelectTag) findAncestorWithClass(this, SelectTag.class);
		if (selectTag == null) {
			// This tag should only be nested in an select tag
			// If it's not, throw exception
			JspException e = new JspException(messages.getMessage("optionTag.select"));
			TagUtils.getInstance().saveException(pageContext, e);
			throw e;
		}

		String cipheredValue = dataComposer != null ? dataComposer.composeFormField(selectTag.getProperty(), value, false, null) : value;

		// If there isn't any content in the body of the tag option, and there isn't
		// any value for the property key, the value of the property value is shown.
		// This is, the encoded value is shown. That is the reason why we store the
		// plain value, without encoding.
		valueWithoutEncrypt = value;

		value = cipheredValue;

		return super.doEndTag();
	}

	/**
	 * Return the text to be displayed to the user for this option (if any).
	 * @exception JspException if an error occurs
	 */
	@Override
	protected String text() throws JspException {

		String optionText = text;

		if ((optionText == null) && (key != null)) {
			optionText = TagUtils.getInstance().message(pageContext, bundle, locale, key);
		}

		// no body text and no key to lookup so display the value
		if (optionText == null) {
			optionText = valueWithoutEncrypt;
		}

		return optionText;
	}

	/**
	 * Generate an HTML %lt;option&gt; element.
	 * 
	 * @throws JspException
	 * @since Struts 1.1
	 */
	@Override
	protected String renderOptionElement() throws JspException {

		StringBuilder results = new StringBuilder("<option value=\"");

		if (filter) {
			results.append(TagUtils.getInstance().filter(value));
		}
		else {
			results.append(value);
		}
		results.append("\"");

		if (disabled) {
			results.append(" disabled=\"disabled\"");
		}
		// we check if any element is selected using the plain value (without
		// encoding).
		if (selectTag().isMatched(valueWithoutEncrypt)) {
			results.append(" selected=\"selected\"");
		}
		renderAttribute(results, "style", getStyle());
		renderAttribute(results, "id", styleId);
		renderAttribute(results, "class", getStyleClass());

		results.append(">");
		results.append(text());
		results.append("</option>");

		return results.toString();
	}

	/**
	 * Acquire the select tag we are associated with.
	 * 
	 * @throws JspException
	 */
	private SelectTag selectTag() throws JspException {

		SelectTag selectTag = (SelectTag) pageContext.getAttribute(Constants.SELECT_KEY);

		if (selectTag == null) {
			JspException e = new JspException(messages.getMessage("optionTag.select"));

			TagUtils.getInstance().saveException(pageContext, e);
			throw e;
		}

		return selectTag;
	}

	/**
	 * Prepares an attribute if the value is not null, appending it to the the given StringBuilder.
	 * 
	 * @param handlers The StringBuilder that output will be appended to.
	 */
	protected void renderAttribute(final StringBuilder handlers, final String name, final Object value) {

		if (value != null) {
			handlers.append(" ");
			handlers.append(name);
			handlers.append("=\"");
			handlers.append(value);
			handlers.append("\"");
		}
	}

}
