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
package org.hdiv.tiles;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.InvalidCancelException;
import org.apache.struts.config.ForwardConfig;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.tiles.TilesRequestProcessor;

/**
 * <p><strong>RequestProcessor</strong> contains the processing logic that
 * the Struts controller servlet performs as it receives each servlet request
 * from the container.</p>
 * <p>This processor subclasses the Struts RequestProcessor in order to intercept calls to forward
 * or include. When such calls are done, the Tiles processor checks if the specified URI
 * is a definition name. If true, the definition is retrieved and included. If
 * false, the original URI is included or a forward is performed.
 * <p>
 * Actually, catching is done by overloading the following methods:
 * <ul>
 * <li>{@link #processForwardConfig(HttpServletRequest,HttpServletResponse,ForwardConfig)}</li>
 * <li>{@link #internalModuleRelativeForward(String, HttpServletRequest , HttpServletResponse)}</li>
 * <li>{@link #internalModuleRelativeInclude(String, HttpServletRequest , HttpServletResponse)}</li>
 * </ul>
 * </p>
 * @author Gorka Vicente
 * @see org.apache.struts.tiles.TilesRequestProcessor
 * @since HDIV 1.1.2
 */
public class HDIVTilesRequestProcessor extends TilesRequestProcessor {

	/**
	 * The request attributes key under HDIV should store errors produced in the
	 * editable fields.
	 */
	private static final String EDITABLE_PARAMETER_ERROR = "org.hdiv.action.EDITABLE_PARAMETER_ERROR";

	/**
	 * Property that contains the error message to be shown by Struts when the value
	 * of the editable parameter is not valid.
	 */
	private static final String HDIV_EDITABLE_ERROR = "hdiv.editable.error";

	/**
	 * Property that contains the error message to be shown by Struts when the value
	 * of the editable password parameter is not valid.
	 */
	private static final String HDIV_EDITABLE_PASSWORD_ERROR = "hdiv.editable.password.error";	

	/**
	 * <p>
	 * If this request was not cancelled, and the request's {@link ActionMapping} has
	 * not disabled validation, call the <code>validate</code> method of the
	 * specified {@link ActionForm}, and forward to the input path if there were any
	 * errors. Return <code>true</code> if we should continue processing, or
	 * <code>false</code> if we have already forwarded control back to the input
	 * form.
	 * </p>
	 * 
	 * @param request The servlet request we are processing
	 * @param response The servlet response we are creating
	 * @param form The ActionForm instance we are populating
	 * @param mapping The ActionMapping we are using
	 * @exception IOException if an input/output error occurs
	 * @exception ServletException if a servlet exception occurs
	 * @exception InvalidCancelException if a cancellation is attempted without the
	 *                proper action configuration
	 */
	protected boolean processValidate(HttpServletRequest request, HttpServletResponse response,
			ActionForm form, ActionMapping mapping) throws IOException, ServletException,
			InvalidCancelException {

		if (form == null) {
			return (true);
		}

		// Has validation been turned off for this mapping?
		if (!mapping.getValidate()) {
			return (true);
		}

		// Was this request cancelled? If it has been, the mapping also
		// needs to state whether the cancellation is permissable; otherwise
		// the cancellation is considered to be a symptom of a programmer
		// error or a spoof.
		if (request.getAttribute(Globals.CANCEL_KEY) != null) {
			if (mapping.getCancellable()) {
				if (log.isDebugEnabled()) {
					log.debug(" Cancelled transaction, skipping validation");
				}
				return (true);
			} else {
				request.removeAttribute(Globals.CANCEL_KEY);
				throw new InvalidCancelException();
			}
		}

		// Call the form bean's validation method
		if (log.isDebugEnabled()) {
			log.debug(" Validating input form properties");
		}
		ActionMessages errors = form.validate(mapping, request);
		if ((errors == null) || errors.isEmpty()) {

			errors = this.getEditableParametersErrors(request);
			if ((errors == null) || errors.isEmpty()) {

				if (log.isTraceEnabled()) {
					log.trace("  No errors detected, accepting input");
				}
				return (true);
			}
		}

		// Special handling for multipart request
		if (form.getMultipartRequestHandler() != null) {
			if (log.isTraceEnabled()) {
				log.trace("  Rolling back multipart request");
			}
			form.getMultipartRequestHandler().rollback();
		}

		// Was an input path (or forward) specified for this mapping?
		String input = mapping.getInput();
		if (input == null) {
			if (log.isTraceEnabled()) {
				log.trace("  Validation failed but no input form available");
			}
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, getInternal()
					.getMessage("noInput", mapping.getPath()));
			return (false);
		}

		// Save our error messages and return to the input form if possible
		if (log.isDebugEnabled()) {
			log.debug(" Validation failed, returning to '" + input + "'");
		}
		request.setAttribute(Globals.ERROR_KEY, errors);

		if (moduleConfig.getControllerConfig().getInputForward()) {
			ForwardConfig forward = mapping.findForward(input);
			processForwardConfig(request, response, forward);
		} else {
			internalModuleRelativeForward(input, request, response);
		}

		return (false);

	}

	/**
	 * Obtains the errors detected by HDIV during the validation process of the
	 * editable parameters.
	 * 
	 * @param request The servlet request we are processing
	 * @return errors detected by HDIV during the validation process of the editable
	 *         parameters.
	 */
	public ActionMessages getEditableParametersErrors(HttpServletRequest request) {

		Hashtable unauthorizedEditableParameters = (Hashtable) request
				.getAttribute(EDITABLE_PARAMETER_ERROR);

		ActionMessages errors = null;
		if ((unauthorizedEditableParameters != null) && (unauthorizedEditableParameters.size() > 0)) {

			errors = new ActionMessages();

			for (Iterator it = unauthorizedEditableParameters.keySet().iterator(); it.hasNext();) {
				
				String currentParameter = (String) it.next();
				String [] currentUnauthorizedValues = (String []) unauthorizedEditableParameters.get(currentParameter);
				
				ActionMessage error = null;
				if ((currentUnauthorizedValues.length == 1) && (currentUnauthorizedValues[0].equals(HDIV_EDITABLE_PASSWORD_ERROR))) {					
					error = new ActionMessage(HDIV_EDITABLE_PASSWORD_ERROR);															
					
				} else {			
					String printedValue = this.createMessageError(currentUnauthorizedValues);	
					error = new ActionMessage(HDIV_EDITABLE_ERROR, printedValue);
				}	
				errors.add("hdiv.editable." + currentParameter, error);				
			}
		}
		return errors;
	}

	/**
	 * It creates the message error from the values <code>values</code>.
	 * 
	 * @param values values with not allowed characters
	 * @return message error to show
	 */
	public String createMessageError(String[] values) {

		StringBuffer printedValue = new StringBuffer();

		for (int i = 0; i < values.length; i++) {

			if (i > 0) {
				printedValue.append(", ");
			}
			if (values[i].length() > 20) {
				printedValue.append(TagUtils.getInstance().filter(values[i]).substring(0, 20) + "...");
			} else {
				printedValue.append(TagUtils.getInstance().filter(values[i]));
			}

			if (printedValue.length() > 20) {
				break;
			}
		}

		return printedValue.toString();
	}	
	
}
