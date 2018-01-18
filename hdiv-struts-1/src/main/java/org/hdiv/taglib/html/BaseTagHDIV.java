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

import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.BaseTag;
import org.apache.struts.util.RequestUtils;
import org.hdiv.util.HDIVUtil;

public class BaseTagHDIV extends BaseTag {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Render a fully formed HTML &lt;base&gt; element and return it as a String.
	 *
	 * @param scheme The scheme used in the url (ie. http or https).
	 * @param serverName
	 * @param port
	 * @param uri The portion of the url from the protocol name up to the query string.
	 * @return String An HTML &lt;base&gt; element.
	 * @since Struts 1.1
	 */
	@Override
	protected String renderBaseElement(final String scheme, final String serverName, final int port, final String uri) {
		final StringBuilder tag = new StringBuilder("<base href=\"");

		String finalUri = null;

		if (ref.equals(REF_SITE)) {
			final StringBuilder contextBase = new StringBuilder(((HttpServletRequest) pageContext.getRequest()).getContextPath());

			contextBase.append("/");
			finalUri = RequestUtils.createServerUriStringBuffer(scheme, serverName, port, contextBase.toString()).toString();
			tag.append(finalUri);
		}
		else {
			finalUri = RequestUtils.createServerUriStringBuffer(scheme, serverName, port, uri).toString();
			tag.append(finalUri);
		}

		// Store the base url value on request to be accessible from hdiv core
		final HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HDIVUtil.getRequestContext(request).setBaseURL(finalUri);

		tag.append("\"");

		if (target != null) {
			tag.append(" target=\"");
			tag.append(target);
			tag.append("\"");
		}

		if (TagUtils.getInstance().isXhtml(pageContext)) {
			tag.append(" />");
		}
		else {
			tag.append(">");
		}

		return tag.toString();
	}
}
