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
package org.hdiv.logs;

import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UISelectBoolean;
import javax.faces.component.html.HtmlInputSecret;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.hdiv.filter.ValidatorError;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.util.HDIVUtil;
import org.hdiv.util.UtilsJsf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for logging messages found in the components that show any data modification by the user.
 * 
 * @author Gotzon Illarramendi
 */
public class ComponentMessagesLog {

	private static final Logger log = LoggerFactory.getLogger(ComponentMessagesLog.class);

	/**
	 * HDIV logger
	 */
	private final org.hdiv.logs.Logger logger;

	/**
	 * Default constructor
	 * 
	 * @param logger Logger instance
	 */
	public ComponentMessagesLog(final org.hdiv.logs.Logger logger) {
		this.logger = logger;
	}

	/**
	 * Searches for validation messages created by the framework itself that show value modification by the user and creates logs for them.
	 * 
	 * @param facesContext request context
	 */
	public void processMessages(final FacesContext facesContext) {
		Iterator<String> clientIterator = facesContext.getClientIdsWithMessages();
		while (clientIterator.hasNext()) {
			String clientId = clientIterator.next();
			if (clientId != null) {
				HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
				String requestValue = request.getParameter(clientId);

				// Remove row identifiers from clientId
				String clientIdWithoutRowId = UtilsJsf.removeRowId(clientId);

				UIComponent clientComponent = facesContext.getViewRoot().findComponent(clientIdWithoutRowId);
				Iterator<FacesMessage> clientMessages = facesContext.getMessages(clientId);
				String requestUri = HDIVUtil.getRequestContext(request).getRequestURI();
				manageClientMessages(requestValue, facesContext, clientComponent, clientMessages, requestUri);
			}
		}
	}

	/**
	 * Creates a log for the corresponding component
	 * 
	 * @param requestValue real value
	 * @param facesContext request context
	 * @param clientComponent component that has been modified
	 * @param clientMessages messages created by the framework
	 * @param requestUri original url of the request
	 */
	private void manageClientMessages(final String requestValue, final FacesContext facesContext, final UIComponent clientComponent,
			final Iterator<FacesMessage> clientMessages, final String requestUri) {

		// If it is editable data, that is: HtmlInputText, HtmlInputTextarea
		// or HtmlInputSecret
		if (clientComponent instanceof UIInput) {

			boolean isTextEditable = clientComponent instanceof HtmlInputText || clientComponent instanceof HtmlInputTextarea
					|| clientComponent instanceof HtmlInputSecret;
			boolean isUISelectBoolean = clientComponent instanceof UISelectBoolean;

			// If it is UISelectOne or UISelectMany:
			if (!isTextEditable && !isUISelectBoolean) {
				// Check all messages from component
				while (clientMessages.hasNext()) {
					FacesMessage message = clientMessages.next();
					if (log.isDebugEnabled()) {
						log.debug("Message detail:" + message.getDetail());
					}

					String value = null;
					if (clientComponent instanceof UIInput) {
						value = requestValue;
					}

					ValidatorError error = new ValidatorError(HDIVErrorCodes.INVALID_PARAMETER_VALUE, requestUri,
							clientComponent.getClientId(facesContext), value);
					logger.log(error);
				}
			}
		}
	}

}
