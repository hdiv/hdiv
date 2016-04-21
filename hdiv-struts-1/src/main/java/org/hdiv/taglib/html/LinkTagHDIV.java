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
import org.apache.struts.taglib.html.LinkTag;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.util.HDIVUtil;

/**
 * Generate a URL-encoded hyperlink to the specified URI.
 *
 * @author Aritz Rabadan
 * @author Gorka Vicente
 * @see org.apache.struts.taglib.html.LinkTag
 */
public class LinkTagHDIV extends LinkTag {

    /**
     * Universal version identifier. Deserialization uses this number to ensure that a loaded class corresponds exactly
     * to a serialized object.
     */
    private static final long serialVersionUID = -376718532211972980L;

    protected LinkUrlProcessor linkUrlProcessor;

    /**
     * Render the end of the hyperlink.
     *
     * @exception JspException if a JSP exception has occurred
     */
    @Override
    public int doEndTag() throws JspException {

        final HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

        // Validate if any specifier was included
        if ((forward == null) && (href == null) && (page == null) && (action == null)) {
            return super.doEndTag();
        }

        // the value specified in linkname will render a "name" element in the
        // generated anchor tag
        if (this.getLinkName() != null) {
            return super.doEndTag();
        }

        // return the complete URL to which this hyperlink will direct the user.
        final String url = calculateURL();

        // Generate the opening anchor element
        final StringBuilder results = new StringBuilder("<a");

        // If useLocalEncoding set to true, urlencoding is done on the bytes of
        // character encoding from ServletResponse#getCharacterEncoding. Use UTF-8
        // otherwise.
        String charEncoding = "UTF-8";
        if (useLocalEncoding) {
            charEncoding = pageContext.getResponse().getCharacterEncoding();
        }

        // Call to Hdiv LinkUrlProcessor
        if (this.linkUrlProcessor == null) {
            this.linkUrlProcessor = HDIVUtil.getLinkUrlProcessor(request.getSession().getServletContext());
        }
        final String urlWithHDIVParameter = this.linkUrlProcessor.processUrl(request, url, charEncoding);

        renderAttribute(results, "href", urlWithHDIVParameter);

        renderAttribute(results, "target", getTarget());
        renderAttribute(results, "accesskey", getAccesskey());
        renderAttribute(results, "tabindex", getTabindex());

        results.append(prepareStyles());
        results.append(prepareEventHandlers());
        prepareOtherAttributes(results);
        results.append(">");

        // Prepare the textual content and ending element of this hyperlink
        if (text != null) {
            results.append(text);
        }
        results.append("</a>");
        TagUtils.getInstance().write(pageContext, results.toString());

        return (EVAL_PAGE);
    }

    /**
     * Prepares an attribute if the <code>value</code> is not null, appending it to the the given StringBuilder
     * <code>result</code>.
     *
     * @param result The StringBuilder that output will be appended to.
     */
    private void renderAttribute(final StringBuilder result, final String name, final String value) {

        if (value != null) {
            result.append(" ");
            result.append(name);
            result.append("=\"");
            result.append(value);
            result.append("\"");
        }
    }

    /**
     * 'Hook' to enable tags to be extended and additional attributes added.
     *
     * @param handlers The StringBuilder that output will be appended to.
     */
    protected void prepareOtherAttributes(final StringBuilder handlers) {

    }

}
