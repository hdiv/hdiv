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
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.util.HDIVUtil;

/**
 * Generate an HTML <code>&lt;frame&gt;</code> tag with similar capabilities as those the <code>&lt;html:link&gt;</code> tag provides for
 * hyperlink elements. The <code>src</code> element is rendered using the same technique that {@link LinkTagHDIV} uses to render the
 * <code>href</code> attribute of a hyperlink. Additionall, the HTML 4.0 frame tag attributes <code>noresize</code>, <code>scrolling</code>,
 * <code>marginheight</code>, <code>marginwidth</code>, <code>frameborder</code>, and <code>longdesc</code> are supported. The frame
 * <code>name</code> attribute is rendered based on the <code>frameName</code> property. Note that the value of <code>longdesc</code> is
 * intended to be a URI, but currently no rewriting is supported. The attribute is set directly from the property value.
 *
 * @author Gorka Vicente
 * @see org.apache.struts.taglib.html.FrameTag
 * @see org.apache.struts.taglib.html.LinkTag
 */
public class FrameTagHDIV extends LinkTagHDIV {

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized
	 * object.
	 */
	private static final long serialVersionUID = -376718532211972980L;

	/**
	 * The frameborder attribute that should be rendered (1, 0).
	 */
	protected String frameborder = null;

	/**
	 * The <code>name</code> attribute that should be rendered for this frame.
	 */
	protected String frameName = null;

	/**
	 * URI of a long description of this frame (complements title).
	 */
	protected String longdesc = null;

	/**
	 * The margin height in pixels, or zero for no setting.
	 */
	protected Integer marginheight = null;

	/**
	 * The margin width in pixels, or null for no setting.
	 */
	protected Integer marginwidth = null;

	/**
	 * Should users be disallowed to resize the frame?
	 */
	protected boolean noresize = false;

	/**
	 * What type of scrolling should be supported (yes, no, auto)?
	 */
	protected String scrolling = null;

	protected LinkUrlProcessor linkUrlProcessor;

	public String getFrameborder() {
		return (frameborder);
	}

	public void setFrameborder(final String frameborder) {
		this.frameborder = frameborder;
	}

	public String getFrameName() {
		return (frameName);
	}

	public void setFrameName(final String frameName) {
		this.frameName = frameName;
	}

	public String getLongdesc() {
		return (longdesc);
	}

	public void setLongdesc(final String longdesc) {
		this.longdesc = longdesc;
	}

	public Integer getMarginheight() {
		return (marginheight);
	}

	public void setMarginheight(final Integer marginheight) {
		this.marginheight = marginheight;
	}

	public Integer getMarginwidth() {
		return (marginwidth);
	}

	public void setMarginwidth(final Integer marginwidth) {
		this.marginwidth = marginwidth;
	}

	public boolean getNoresize() {
		return (noresize);
	}

	public void setNoresize(final boolean noresize) {
		this.noresize = noresize;
	}

	public String getScrolling() {
		return (scrolling);
	}

	public void setScrolling(final String scrolling) {
		this.scrolling = scrolling;
	}

	/**
	 * Render the appropriately encoded URI.
	 *
	 * @exception JspException if a JSP exception has occurred
	 * @see org.hdiv.urlProcessor.LinkUrlProcessor
	 */
	@Override
	public int doStartTag() throws JspException {

		final HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

		// return the complete URL to which this hyperlink will direct the user.
		final String url = calculateURL();

		// Print this element to our output writer
		final StringBuffer results = new StringBuffer("<frame ");
		results.append("src=\"");

		// Call to Hdiv LinkUrlProcessor
		if (linkUrlProcessor == null) {
			linkUrlProcessor = HDIVUtil.getLinkUrlProcessor(request.getSession().getServletContext());
		}
		final String urlWithHDIVParameter = linkUrlProcessor.processUrl(request, url);

		results.append(urlWithHDIVParameter);

		results.append("\"");
		super.prepareAttribute(results, "name", getFrameName());

		if (noresize) {
			results.append(" noresize=\"noresize\"");
		}

		prepareAttribute(results, "scrolling", getScrolling());
		prepareAttribute(results, "marginheight", getMarginheight());
		prepareAttribute(results, "marginwidth", getMarginwidth());
		prepareAttribute(results, "frameborder", getFrameborder());
		prepareAttribute(results, "longdesc", getLongdesc());

		results.append(prepareStyles());
		prepareOtherAttributes(results);
		results.append(getElementClose());

		TagUtils.getInstance().write(pageContext, results.toString());

		// Skip the body of this tag
		return (SKIP_BODY);
	}

	/**
	 * Ignore the end of this tag.
	 *
	 * @exception JspException if a JSP exception has occurred
	 */
	@Override
	public int doEndTag() throws JspException {

		return (EVAL_PAGE);
	}

	/**
	 * Release any acquired resources.
	 */
	@Override
	public void release() {

		super.release();
		frameborder = null;
		frameName = null;
		longdesc = null;
		marginheight = null;
		marginwidth = null;
		noresize = false;
		scrolling = null;
	}
}
