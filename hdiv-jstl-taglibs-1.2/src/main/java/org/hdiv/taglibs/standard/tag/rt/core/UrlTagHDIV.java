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
package org.hdiv.taglibs.standard.tag.rt.core;

import javax.servlet.jsp.JspTagException;

import org.hdiv.taglibs.standard.tag.common.core.UrlSupportHDIV;

/**
 * <p>
 * A handler for &lt;urlEncode&gt; that supports rtexprvalue-based attributes.
 * </p>
 *
 * @author Gorka Vicente.
 * @since HDIV 2.0
 */
public class UrlTagHDIV extends UrlSupportHDIV {

	public void setValue(String value) throws JspTagException {
		this.value = value;
	}

	public void setContext(String context) throws JspTagException {
		this.context = context;
	}
}
