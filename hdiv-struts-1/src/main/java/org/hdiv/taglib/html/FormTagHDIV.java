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
package org.hdiv.taglib.html;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.struts.Globals;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.FormTag;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.urlProcessor.FormUrlProcessor;
import org.hdiv.util.HDIVUtil;

/**
 * Renders an HTML <b>&lt;form&gt;</b> element whose contents are described by the
 * body content of this tag. The form implicitly interacts with the specified request
 * scope or session scope bean to populate the input fields with the current property
 * values from the bean. The form bean is located, and created if necessary, based on
 * the form bean specification for the associated ActionMapping.
 * 
 * @author Aritz Rabadan
 * @author Gorka Vicente
 * @see org.apache.struts.taglib.html.FormTag
 */
public class FormTagHDIV extends FormTag {

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that
	 * a loaded class corresponds exactly to a serialized object.
	 */
	private static final long serialVersionUID = 5073853465806606664L;

	protected FormUrlProcessor formUrlProcessor;

	/**
	 * Renders the action attribute
	 * @see org.hdiv.composer.IDataComposer
	 * @see org.hdiv.util.HDIVUtil#composeAction(String, int, String)
	 */
	protected void renderAction(StringBuffer results) {

		HttpServletResponse response = (HttpServletResponse) this.pageContext.getResponse();
		HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();

		String calcAction = (this.action == null ? this.getPostbackAction() : this.action);

		String url = response.encodeURL(TagUtils.getInstance().getActionMappingURL(calcAction, this.pageContext));

		// Call to Hdiv FormUrlProcessor
		if(this.formUrlProcessor == null){
			this.formUrlProcessor = HDIVUtil.getFormUrlProcessor(request.getSession().getServletContext());
		}
		String encodedURL = formUrlProcessor.processUrl(request, url);

		results.append(" action=\"");
		results.append(encodedURL);
		results.append("\"");
	}
	
	/**
	 * @return If the action is not specified returns the original request uri.
	 */
	public String getPostbackAction() {
		
		String tempPostbackAction = null;
		
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		tempPostbackAction = (String) request.getAttribute(Globals.ORIGINAL_URI_KEY);

		String prefix = moduleConfig.getPrefix();
		if (tempPostbackAction != null && prefix.length() > 0 && tempPostbackAction.startsWith(prefix)) {
			tempPostbackAction = tempPostbackAction.substring(prefix.length());
		}
		return tempPostbackAction;
	}

	/**
	 * Render the end of this form adding HDIV parameter if strategy is cipher or hash.
	 * @exception JspException if a JSP exception has occurred
	 */
	public int doEndTag() throws JspException {

		this.addHDIVParameter();
		return super.doEndTag();
	}
	
	/**
	 * Adds HDIV state as parameter.
	 */
	protected void addHDIVParameter() throws JspException {
		
		HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
		IDataComposer dataComposer = HDIVUtil.getDataComposer(request);
		if(!dataComposer.isRequestStarted()){
			return;
		}
		String requestId = dataComposer.endRequest();
		
		if (requestId.length() > 0) {
			String hdivParameter = (String) request.getSession().getAttribute("HDIVParameter");
			TagUtils.getInstance().write(pageContext, this.generateHiddenTag(hdivParameter, requestId));
		}		
	}	
    
	/**
	 * Renders an HTML <b>&lt;input&gt;</b> element of type hidden.
	 * 
	 * @param name hidden parameter name
	 * @param requestId request identification
	 * @return HTML <b>&lt;input&gt;</b> element of type hidden
	 */
	private String generateHiddenTag(String name, String requestId) {
		
		StringBuffer hdivParameter = new StringBuffer();
		
		hdivParameter.append("<input type=\"hidden\"");		
		renderAttribute(hdivParameter, "name", name);
		renderAttribute(hdivParameter, "value", requestId);
		hdivParameter.append(">\n");
		
		return hdivParameter.toString();
	}

}
