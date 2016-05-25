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
package org.hdiv.tiles;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
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
import org.apache.struts.util.RequestUtils;
import org.hdiv.filter.RequestWrapper;
import org.hdiv.filter.ValidatorError;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.urlProcessor.UrlData;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;

/**
 * <p>
 * <strong>RequestProcessor</strong> contains the processing logic that the Struts controller servlet performs as it receives each servlet
 * request from the container.
 * </p>
 * <p>
 * This processor subclasses the Struts RequestProcessor in order to intercept calls to forward or include. When such calls are done, the
 * Tiles processor checks if the specified URI is a definition name. If true, the definition is retrieved and included. If false, the
 * original URI is included or a forward is performed.
 * <p>
 * Actually, catching is done by overloading the following methods:
 * <ul>
 * <li>{@link #processForwardConfig(HttpServletRequest,HttpServletResponse,ForwardConfig)}</li>
 * <li>{@link #internalModuleRelativeForward(String, HttpServletRequest , HttpServletResponse)}</li>
 * <li>{@link #internalModuleRelativeInclude(String, HttpServletRequest , HttpServletResponse)}</li>
 * </ul>
 * </p>
 * 
 * @author Gorka Vicente
 * @see org.apache.struts.tiles.TilesRequestProcessor
 * @since HDIV 1.1.2
 */
public class HDIVTilesRequestProcessor extends TilesRequestProcessor {

	/**
	 * The request attributes key under HDIV should store errors produced in the editable fields.
	 */
	private static final String EDITABLE_PARAMETER_ERROR = Constants.EDITABLE_PARAMETER_ERROR;

	/**
	 * Property that contains the error message to be shown by Struts when the value of the editable parameter is not valid.
	 */
	private static final String HDIV_EDITABLE_ERROR = Constants.HDIV_EDITABLE_ERROR_KEY;

	/**
	 * Property that contains the error message to be shown by Struts when the value of the editable password parameter is not valid.
	 */
	private static final String HDIV_EDITABLE_PASSWORD_ERROR = Constants.HDIV_EDITABLE_PASSWORD_ERROR_KEY;

	/**
	 * <p>
	 * If this request was not cancelled, and the request's {@link ActionMapping} has not disabled validation, call the
	 * <code>validate</code> method of the specified {@link ActionForm}, and forward to the input path if there were any errors. Return
	 * <code>true</code> if we should continue processing, or <code>false</code> if we have already forwarded control back to the input
	 * form.
	 * </p>
	 * 
	 * @param request The servlet request we are processing
	 * @param response The servlet response we are creating
	 * @param form The ActionForm instance we are populating
	 * @param mapping The ActionMapping we are using
	 * @exception IOException if an input/output error occurs
	 * @exception ServletException if a servlet exception occurs
	 * @exception InvalidCancelException if a cancellation is attempted without the proper action configuration
	 */
	protected boolean processValidate(HttpServletRequest request, HttpServletResponse response, ActionForm form, ActionMapping mapping)
			throws IOException, ServletException, InvalidCancelException {

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
			}
			else {
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
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, getInternal().getMessage("noInput", mapping.getPath()));
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
		}
		else {
			internalModuleRelativeForward(input, request, response);
		}

		return (false);

	}

	/**
	 * Obtains the errors detected by HDIV during the validation process of the editable parameters.
	 * 
	 * @param request The servlet request we are processing
	 * @return errors detected by HDIV during the validation process of the editable parameters.
	 */
	public ActionMessages getEditableParametersErrors(HttpServletRequest request) {

		@SuppressWarnings("unchecked")
		List<ValidatorError> validationErrors = (List<ValidatorError>) request.getAttribute(EDITABLE_PARAMETER_ERROR);

		ActionMessages errors = null;
		if (validationErrors != null && validationErrors.size() > 0) {

			errors = new ActionMessages();

			for (ValidatorError validationError : validationErrors) {

				String errorValues = validationError.getParameterValue();

				ActionMessage error = null;
				if (errorValues.contains(HDIV_EDITABLE_PASSWORD_ERROR)) {
					error = new ActionMessage(HDIV_EDITABLE_PASSWORD_ERROR);
				}
				else {
					String printedValue = this.createMessageError(errorValues);
					error = new ActionMessage(HDIV_EDITABLE_ERROR, printedValue);
				}
				errors.add("hdiv.editable." + validationError.getParameterName(), error);
			}
		}
		return errors;
	}

	/**
	 * It creates the message error from the values <code>values</code>.
	 * 
	 * @param paramValues values with not allowed characters
	 * @return message error to show
	 */
	public String createMessageError(String paramValues) {

		String[] values = paramValues.split(",");
		StringBuilder printedValue = new StringBuilder();

		for (int i = 0; i < values.length; i++) {

			if (i > 0) {
				printedValue.append(", ");
			}
			if (values[i].length() > 20) {
				printedValue.append(TagUtils.getInstance().filter(values[i]).substring(0, 20) + "...");
			}
			else {
				printedValue.append(TagUtils.getInstance().filter(values[i]));
			}

			if (printedValue.length() > 20) {
				break;
			}
		}

		return printedValue.toString();
	}

	/**
	 * Overloaded method from Struts' RequestProcessor. Forward or redirect to the specified destination by the specified mechanism. This
	 * method catches the Struts' actionForward call. It checks if the actionForward is done on a Tiles definition name. If true, process
	 * the definition and insert it. If false, call the original parent's method.
	 * 
	 * @param request The servlet request we are processing.
	 * @param response The servlet response we are creating.
	 * @param forward The ActionForward controlling where we go next.
	 *
	 * @throws IOException if an input/output error occurs.
	 * @throws ServletException if a servlet exception occurs.
	 * @since HDIV 2.1.12
	 */
	protected void processForwardConfig(HttpServletRequest request, HttpServletResponse response, ForwardConfig forward)
			throws IOException, ServletException {

		// Required by struts contract
		if (forward == null) {
			return;
		}

		String forwardPath = forward.getPath();
		if (log.isDebugEnabled()) {
			log.debug("processForwardConfig(" + forwardPath + ")");
		}

		// Try to process the definition.
		if (processTilesDefinition(forwardPath, request, response)) {
			if (log.isDebugEnabled()) {
				log.debug("  '" + forwardPath + "' - processed as definition");
			}
			return;
		}

		if (log.isDebugEnabled()) {
			log.debug("  '" + forwardPath + "' - processed as uri");
		}

		// forward doesn't contain a definition, let parent do processing
		// If the forward can be unaliased into an action, then use the path of
		// the action
		String actionIdPath = RequestUtils.actionIdURL(forward, request, servlet);
		if (actionIdPath != null) {
			forwardPath = actionIdPath;
			ForwardConfig actionIdForward = new ForwardConfig(forward);
			actionIdForward.setPath(actionIdPath);
			forward = actionIdForward;
		}

		// paths not starting with / should be passed through without any
		// processing (i.e. they're absolute)
		String uri = forwardPath.startsWith("/") ? RequestUtils.forwardURL(request, forward, null) : forwardPath;

		if (forward.getRedirect()) {
			// only prepend context path for relative uri
			if (uri.startsWith("/")) {
				uri = request.getContextPath() + uri;
			}

			// Call to HDIV LinkUrlProcessor
			LinkUrlProcessor linkUrlProcessor = HDIVUtil.getLinkUrlProcessor(request.getSession().getServletContext());
			uri = linkUrlProcessor.processUrl(request, uri);

			if (log.isDebugEnabled()) {
				log.debug("redirecting to " + uri);
			}

			response.sendRedirect(response.encodeRedirectURL(uri));
		}
		else {
			this.doForward(uri, request, response);
		}
	}

	/**
	 * Do a forward using request dispatcher. Uri is a valid uri. If response has already been commited, do an include instead.
	 * 
	 * @param uri Uri or Definition name to forward.
	 * @param request Current page request.
	 * @param response Current page response.
	 */
	protected void doForward(String uri, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		RequestWrapper requestWrapper = HDIVUtil.getNativeRequest(request, RequestWrapper.class);
		if (requestWrapper != null) {

			LinkUrlProcessor linkUrlProcessorForForward = HDIVUtil.getLinkUrlProcessor(request.getSession().getServletContext());
			UrlData urlData = linkUrlProcessorForForward.createUrlData(uri, "GET", request);
			Map<String, String[]> urlParamsAsMap = linkUrlProcessorForForward.getUrlParamsAsMap(request, urlData.getUrlParams());
			for (Map.Entry<String, String[]> entry : urlParamsAsMap.entrySet()) {
				requestWrapper.addParameter(entry.getKey(), entry.getValue());
			}
		}

		if (response.isCommitted()) {
			this.doInclude(uri, request, response);

		}
		else {

			RequestDispatcher rd = getServletContext().getRequestDispatcher(uri);
			if (rd == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, getInternal().getMessage("requestDispatcher", uri));
				return;
			}

			rd.forward(request, response);
		}
	}
}