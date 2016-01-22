/**
 * Copyright 2005-2015 hdiv.org
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
package org.hdiv.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.state.scope.StateScopeType;
import org.hdiv.util.HDIVUtil;

/**
 * Tag to mark a part of the page as long living page.
 * 
 * @since 2.1.7
 */
public class LongLivingStatesTag extends TagSupport {

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that a loaded class corresponds exactly
	 * to a serialized object.
	 */
	private static final long serialVersionUID = 4998045113101933843L;

	/**
	 * Sets the type <code>scope</code> defined in the tag.
	 * 
	 * @param scope Scope name
	 */
	public void setScope(String scope) {
		this.setValue("scope", scope);
	}

	@Override
	public int doStartTag() throws JspException {

		IDataComposer dataComposer = (IDataComposer) this.pageContext.getRequest().getAttribute(
				HDIVUtil.DATACOMPOSER_REQUEST_KEY);

		String scope = (String) this.getValue("scope");
		if (scope == null) {
			// Default scope
			scope = StateScopeType.USER_SESSION.getName();
		}

		dataComposer.startScope(scope);

		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag() throws JspException {

		IDataComposer dataComposer = (IDataComposer) this.pageContext.getRequest().getAttribute(
				HDIVUtil.DATACOMPOSER_REQUEST_KEY);

		dataComposer.endScope();

		return EVAL_PAGE;
	}

}
