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
package org.hdiv.taglib.html;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.util.HDIVUtil;

/**
 * Tag to cipher data without using Struts.
 * <p>
 * Being "hdiv" the identifier used to reference HDIV tag library, the format of the
 * tag will be as follows:
 * <code>&lt;hdiv:cipher action="a1" parameter="p1" value="v1" /&gt;</code>.
 * </p>
 * <p>
 * The result will be an encoded value that only HDIV can interpretate.
 * </p>
 * 
 * @author Aritz Rabadan
 * @author Gorka Vicente
 */
public class CipherTag extends TagSupport {

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that
	 * a loaded class corresponds exactly to a serialized object.
	 */
	private static final long serialVersionUID = -3083219212736900931L;

	/**
	 * Sets the action <code>action</code> defined in the tag.
	 * 
	 * @param action Action
	 */
	public void setAction(String action) {
		this.setValue("action", action);
	}

	/**
	 * Sets the parameter <code>parameter</code> defined in the tag.
	 * 
	 * @param parameter Parameter
	 */
	public void setParameter(String parameter) {
		this.setValue("parameter", parameter);
	}

	/**
	 * Sets the value <code>value</code> defined in the tag.
	 * 
	 * @param value Value
	 */
	public void setValue(String value) {
		this.setValue("value", value);
	}

	/**
	 * Process the start of this tag.
	 * 
	 * @throws JspException If the attributes passed to the tag are incorrect, an
	 *             exception will be thrown.
	 */
	public int doStartTag() throws JspException {

		HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
		IDataComposer dataComposer = HDIVUtil.getDataComposer(request);

		String action = (String) this.getValue("action");
		String parameter = (String) this.getValue("parameter");
		String value = (String) this.getValue("value");

		String cipheredValue;
		if (action != null) {
			cipheredValue = dataComposer.compose(action, parameter, value, false);
		} else {
			cipheredValue = dataComposer.compose(parameter, value, false);
		}

		try {
			// getJspWriter to output content
			JspWriter out = this.pageContext.getOut();
			out.print(cipheredValue);
		} catch (IOException e) {
			throw new JspException("Error:" + e.getMessage());
		}

		return SKIP_BODY;
	}

}
