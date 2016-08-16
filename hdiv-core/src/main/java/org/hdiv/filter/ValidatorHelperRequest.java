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
package org.hdiv.filter;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.config.Strategy;
import org.hdiv.context.RequestContext;
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.dataValidator.IDataValidator;
import org.hdiv.dataValidator.IValidationResult;
import org.hdiv.exception.HDIVException;
import org.hdiv.session.ISession;
import org.hdiv.state.IPage;
import org.hdiv.state.IParameter;
import org.hdiv.state.IState;
import org.hdiv.state.StateUtil;
import org.hdiv.state.scope.StateScope;
import org.hdiv.state.scope.StateScopeManager;
import org.hdiv.urlProcessor.BasicUrlProcessor;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.util.HDIVUtil;
import org.hdiv.util.Method;
import org.hdiv.validator.EditableDataValidationResult;
import org.springframework.web.util.HtmlUtils;

/**
 * It validates client requests by consuming an object of type IState and validating all the entry data, besides replacing relative values
 * by its real values.
 *
 * @author Roberto Velasco
 * @author Gorka Vicente
 * @author Gotzon Illarramendi
 * @since HDIV 2.0
 */
public class ValidatorHelperRequest implements IValidationHelper {

	/**
	 * Commons Logging instance.
	 */
	private static final Log log = LogFactory.getLog(ValidatorHelperRequest.class);

	/**
	 * HDIV configuration object.
	 */
	protected HDIVConfig hdivConfig;

	/**
	 * Utility methods for state
	 */
	protected StateUtil stateUtil;

	/**
	 * State that represents all the data of a request or a form existing in a page <code>page</code>
	 */
	protected ISession session;

	/**
	 * IDataValidator factory
	 */
	protected IDataValidator dataValidator;

	/**
	 * {@link IDataComposer} factory
	 */
	protected DataComposerFactory dataComposerFactory;

	/**
	 * Compiled numeric <code>Pattern</code>
	 */
	protected Pattern numberPattern = Pattern.compile("[0-9]+");

	/**
	 * URL String processor.
	 */
	protected BasicUrlProcessor urlProcessor;

	/**
	 * State scope manager.
	 */
	protected StateScopeManager stateScopeManager;

	/**
	 * Initialization of the objects needed for the validation process.
	 *
	 * @throws HDIVException if there is an initialization error.
	 */
	public void init() {
	}

	protected final String getDecodedTarget(final StringBuilder sb, final HttpServletRequest request) {
		/**
		 * Remove contest path and session info first
		 */
		String target = HDIVUtil.stripSession(request.getRequestURI().substring(request.getContextPath().length()));
		return decodeUrl(sb, target);
	}

	/**
	 * Checks if the values of the parameters received in the request <code>request</code> are valid. These values are valid if and only if
	 * the noneditable parameters haven't been modified.<br>
	 * Validation process is as follows.<br>
	 * 1. If the action to which the request is directed is an init page, then it is a valid request.<br>
	 * 2. if the cookies received in the request are not found in the user session, the validation is incorrect.<br>
	 * 3. if the state recover process has produced an error, incorrect validation.<br>
	 * 4. If the action received in the request is different to the action of the recovered state, incorrect validation. <br>
	 * 5. If not, all the parameter values are checked and if all the received values are valid then the request is valid. <br>
	 * 5.1. If it is an init parameter or a HDIV parameter then it is a valid parameter.<br>
	 * 5.2. If the received parameter is not in the state:<br>
	 * 5.2.1. If it has been defined by the user as a no validation required parameter, then it is a valid parameter. <br>
	 * 5.2.2. otherwise, it is a no valid request.<br>
	 * 5.3. If the parameter is editable, if validations have been defined values are checked.<br>
	 * 5.4. If it is a non editable parameter, all the received values are checked.
	 *
	 * @param request HttpServletRequest to validate
	 * @return valid result If all the parameter values of the request <code>request</code> pass the the HDIV validation. False, otherwise.
	 * @throws HDIVException If the request doesn't pass the HDIV validation an exception is thrown explaining the cause of the error.
	 */
	public ValidatorHelperResult validate(final HttpServletRequest request) {
		StringBuilder sb = new StringBuilder(128);
		String target = getDecodedTarget(sb, request);

		// Hook before the validation
		ValidatorHelperResult result = preValidate(request, target);
		if (result != null) {
			return result;
		}

		if (hdivConfig.hasExtensionToExclude(target)) {
			if (log.isDebugEnabled()) {
				log.debug("The target [" + target + "] has an extension to exclude from validation");
			}
			return ValidatorHelperResult.VALIDATION_NOT_REQUIRED;
		}

		if (!hdivConfig.isValidationInUrlsWithoutParamsActivated()) {

			boolean requestHasParameters = request.getParameterNames() != null && request.getParameterNames().hasMoreElements();
			if (!requestHasParameters) {
				if (log.isDebugEnabled()) {
					log.debug("The url [" + request.getRequestURI() + "] is not be validated because it has not got parameters");
				}
				return ValidatorHelperResult.VALIDATION_NOT_REQUIRED;
			}
		}

		if (isStartPage(request, target)) {
			result = validateStartPageParameters(request, target);
			if (result.isValid()) {
				return ValidatorHelperResult.VALIDATION_NOT_REQUIRED;
			}
			else {
				return result;
			}
		}

		if (hdivConfig.isCookiesIntegrityActivated()) {
			result = validateRequestCookies(request, target);
			if (!result.isValid()) {
				return result;
			}
		}

		// Hdiv parameter name
		String hdivParameter = getHdivParameter(request);

		// Restore state from request or memory
		result = restoreState(hdivParameter, request, target);
		if (!result.isValid()) {
			return result;
		}
		// Get resultant object, the stored state
		IState state = result.getValue();

		result = isTheSameAction(request, target, state);
		if (!result.isValid()) {
			return result;
		}

		// Extract url params from State
		Map<String, String[]> stateParams = urlProcessor.getUrlParamsAsMap(sb, request, state.getParams());

		result = allRequiredParametersReceived(request, state, target, stateParams);
		if (!result.isValid()) {
			return result;
		}

		List<ValidatorError> unauthorizedEditableParameters = new ArrayList<ValidatorError>();
		Enumeration<?> parameters = request.getParameterNames();
		while (parameters.hasMoreElements()) {

			String parameter = (String) parameters.nextElement();

			// Validate parameter
			result = validateParameter(request, state.getParameter(parameter), stateParams.get(parameter), unauthorizedEditableParameters,
					hdivParameter, target, parameter);
			if (!result.isValid()) {
				return result;
			}

		}

		if (!unauthorizedEditableParameters.isEmpty()) {
			return new ValidatorHelperResult(unauthorizedEditableParameters);
		}
		return ValidatorHelperResult.VALID;
	}

	/**
	 * Check if the current request is a start page.
	 *
	 * @param request HttpServletRequest to validate
	 * @param target Part of the url that represents the target action
	 * @return true if it is a start page
	 */
	protected boolean isStartPage(final HttpServletRequest request, final String target) {

		return hdivConfig.isStartPage(target, Method.valueOf(request.getMethod()));
	}

	/**
	 * It decodes the url to replace the character represented by percentage with its equivalent.
	 *
	 * @param url url to decode
	 * @return decoder url
	 */
	protected String decodeUrl(final StringBuilder sb, final String url) {
		try {
			return HDIVUtil.decodeValue(sb, url, Constants.ENCODING_UTF_8);
		}
		catch (final UnsupportedEncodingException e) {
			throw new HDIVException("Error decoding url", e);
		}
		catch (final IllegalArgumentException e) {
			throw new HDIVException("Error decoding url", e);
		}
	}

	/**
	 * Checks if the action received in the request is the same as the one stored in the HDIV state.
	 *
	 * @param request HttpServletRequest to validate
	 * @param target Part of the url that represents the target action
	 * @param state The restored state for this url
	 * @return valid result if the actions are the same. False otherwise.
	 */
	protected ValidatorHelperResult isTheSameAction(final HttpServletRequest request, final String target, final IState state) {

		String stateAction = state.getAction();

		// Remove HTML escaped content from the action, for example, HTML entities like &Ntilde;
		stateAction = HtmlUtils.htmlUnescape(stateAction);

		if (stateAction.equalsIgnoreCase(target)) {
			return ValidatorHelperResult.VALID;
		}

		if (target.endsWith("/")) {
			String actionSlash = stateAction + "/";
			if (actionSlash.equalsIgnoreCase(target)) {
				return ValidatorHelperResult.VALID;
			}
		}

		if (log.isDebugEnabled()) {
			log.debug(
					"Validation error in the action. Action in state [" + state.getAction() + "], action in the request [" + target + "]");
		}
		ValidatorError error = new ValidatorError(HDIVErrorCodes.ACTION_ERROR, target);
		return new ValidatorHelperResult(error);
	}

	/**
	 * It validates the parameters of an init page because our application can receive requests that require validation but don't have any
	 * HDIV state. So, despite being init pages, editable data validation must be done.
	 *
	 * @param request HttpServletRequest to validate
	 * @param target Part of the url that represents the target action
	 * @return valid result if the values of the editable parameters pass the validations defined in hdiv-config.xml. False otherwise.
	 * @since HDIV 1.1.2
	 */
	protected ValidatorHelperResult validateStartPageParameters(final HttpServletRequest request, final String target) {

		List<ValidatorError> unauthorizedEditableParameters = new ArrayList<ValidatorError>();

		Enumeration<?> parameters = request.getParameterNames();
		while (parameters.hasMoreElements()) {

			String parameter = (String) parameters.nextElement();
			String[] values = request.getParameterValues(parameter);

			validateEditableParameter(request, target, parameter, values, "text", unauthorizedEditableParameters);
		}

		if (unauthorizedEditableParameters.size() > 0) {
			return new ValidatorHelperResult(unauthorizedEditableParameters);
		}
		return ValidatorHelperResult.VALID;
	}

	/**
	 * Checks if the cookies received in the request are correct. For that, it checks if they are in the user session.
	 *
	 * @param request HttpServletRequest to validate
	 * @param target Part of the url that represents the target action
	 * @return valid result if all the cookies received in the request are correct. They must have been previously stored in the user
	 * session by HDIV to be correct. False otherwise.
	 * @since HDIV 1.1
	 */
	protected ValidatorHelperResult validateRequestCookies(final HttpServletRequest request, final String target) {

		Cookie[] requestCookies = request.getCookies();

		if (requestCookies == null || requestCookies.length == 0) {
			return ValidatorHelperResult.VALID;
		}

		@SuppressWarnings("unchecked")
		Map<String, SavedCookie> sessionCookies = session.getAttribute(new RequestContext(request), // TODO cache
																									// context?
				Constants.HDIV_COOKIES_KEY, Map.class);

		if (sessionCookies == null) {
			return ValidatorHelperResult.VALID;
		}

		boolean cookiesConfidentiality = hdivConfig.getConfidentiality() && hdivConfig.isCookiesConfidentialityActivated();

		for (int i = 0; i < requestCookies.length; i++) {

			boolean found = false;
			if (requestCookies[i].getName().equals(Constants.JSESSIONID)) {
				continue;
			}

			if (sessionCookies.containsKey(requestCookies[i].getName())) {

				SavedCookie savedCookie = sessionCookies.get(requestCookies[i].getName());
				if (savedCookie.isEqual(requestCookies[i], cookiesConfidentiality)) {

					found = true;
					if (cookiesConfidentiality) {
						if (savedCookie.getValue() != null) {
							requestCookies[i].setValue(savedCookie.getValue());
						}
					}
				}
			}

			if (!found) {
				ValidatorError error = new ValidatorError(HDIVErrorCodes.COOKIE_INCORRECT, target, "cookie:" + requestCookies[i].getName(),
						requestCookies[i].getValue());
				return new ValidatorHelperResult(error);
			}
		}
		return ValidatorHelperResult.VALID;
	}

	/**
	 * Checks if the values <code>values</code> are valid for the editable parameter <code>parameter</code>. If the values are not valid, an
	 * error message with the parameter and the received values will be log.
	 *
	 * @param request HttpServletRequest to validate
	 * @param target Part of the url that represents the target action
	 * @param parameter parameter name
	 * @param values parameter's values
	 * @param dataType editable data type
	 * @param unauthorizedParameters Unauthorized editable parameters
	 * @since HDIV 1.1
	 */
	protected void validateEditableParameter(final HttpServletRequest request, final String target, final String parameter,
			final String[] values, final String dataType, final List<ValidatorError> unauthorizedParameters) {

		EditableDataValidationResult result = hdivConfig.getEditableDataValidationProvider().validate(target, parameter, values, dataType);
		if (!result.isValid()) {

			String value;

			if (dataType.equals("password")) {
				value = Constants.HDIV_EDITABLE_PASSWORD_ERROR_KEY;
			}
			else {
				StringBuilder unauthorizedValues = new StringBuilder(values[0]);

				// TODO include only unauthorized values, not all values
				for (int i = 1; i < values.length; i++) {
					unauthorizedValues.append(',').append(values[i]);
				}
				value = unauthorizedValues.toString();
			}

			ValidatorError error = new ValidatorError(HDIVErrorCodes.EDITABLE_VALIDATION_ERROR, target, parameter, value, null,
					result.getValidationId());
			unauthorizedParameters.add(error);
		}
	}

	/**
	 * Check if all required parameters are received in <code>request</code>.
	 *
	 * @param request HttpServletRequest to validate
	 * @param state IState The restored state for this url
	 * @param target Part of the url that represents the target action
	 * @param stateParams Url params from State
	 * @return valid result if all required parameters are received. False in otherwise.
	 */
	protected ValidatorHelperResult allRequiredParametersReceived(final HttpServletRequest request, final IState state, final String target,
			final Map<String, String[]> stateParams) {

		List<String> requiredParameters = state.getRequiredParams();
		List<String> requiredParams = new ArrayList<String>(stateParams.keySet());

		Enumeration<?> requestParameters = request.getParameterNames();

		List<String> required = new ArrayList<String>();
		required.addAll(requiredParameters);
		required.addAll(requiredParams);

		while (requestParameters.hasMoreElements()) {

			String currentParameter = (String) requestParameters.nextElement();

			required.remove(currentParameter);

			// If multiple parameters are received, it is possible to pass this
			// verification without checking all the request parameters.
			if (required.isEmpty()) {
				return ValidatorHelperResult.VALID;
			}
		}

		// Fix for IBM Websphere different behavior with parameters without values.
		// For example, param1=val1&param2
		// This kind of parameters are excluded from request.getParameterNames() API.
		// http://www.ibm.com/support/docview.wss?uid=swg1PM35450
		if (required.size() > 0) {
			Iterator<String> it = required.iterator();
			while (it.hasNext()) {
				String req = it.next();
				if (isNoValueParameter(request, req)) {
					it.remove();
				}
			}
		}

		if (required.size() > 0) {
			ValidatorError error = new ValidatorError(HDIVErrorCodes.REQUIRED_PARAMETERS, target, required.toString());
			return new ValidatorHelperResult(error);
		}

		return ValidatorHelperResult.VALID;
	}

	/**
	 * Check if the given parameter doesn't have values looking in the query string.
	 * 
	 * @param request HttpServletRequest instance
	 * @param parameter Parameter name
	 * @return true if the parameter does't have value
	 */
	private boolean isNoValueParameter(final HttpServletRequest request, final String parameter) {

		String queryString = request.getQueryString();
		if (queryString == null) {
			return false;
		}

		String[] parts = queryString.split("&");
		if (parts.length == 0) {
			return false;
		}

		List<String> partsList = Arrays.asList(parts);
		return partsList.contains(parameter);
	}

	/**
	 * Validate single parameter values.
	 *
	 * @param request HttpServletRequest to validate
	 * @param stateParameter IParameter The restored state for this url
	 * @param actionParamValues actio params values
	 * @param unauthorizedEditableParameters Editable parameters with errors
	 * @param hdivParameter Hdiv state parameter name
	 * @param target Part of the url that represents the target action
	 * @param parameter Parameter name to validate
	 * @return Valid if parameter has not errors
	 * @since HDIV 2.1.5
	 */
	protected ValidatorHelperResult validateParameter(final HttpServletRequest request, final IParameter stateParameter,
			final String[] actionParamValues, final List<ValidatorError> unauthorizedEditableParameters, final String hdivParameter,
			final String target, final String parameter) {

		// If the parameter requires no validation it is considered a valid parameter
		if (isUserDefinedNonValidationParameter(target, parameter, hdivParameter)) {
			return ValidatorHelperResult.VALIDATION_NOT_REQUIRED;
		}

		if (stateParameter == null && actionParamValues == null) {

			// The parameter is not defined in the state, it is an extra parameter.
			return validateExtraParameter(request, stateParameter, actionParamValues, unauthorizedEditableParameters, hdivParameter, target,
					parameter);
		}

		// At this point we are processing a noneditable parameter
		String[] values = request.getParameterValues(parameter);

		// Check if the parameter is editable
		if (stateParameter != null && stateParameter.isEditable()) {

			// Mark parameter as editable
			addEditableParameter(request, parameter);

			if (stateParameter.getEditableDataType() != null) {
				validateEditableParameter(request, target, parameter, values, stateParameter.getEditableDataType(),
						unauthorizedEditableParameters);
			}
			return ValidatorHelperResult.VALID;
		}

		try {
			ValidatorHelperResult result = validateParameterValues(request, target, stateParameter, actionParamValues, parameter, values);
			return result;
		}
		catch (final HDIVException e) {
			String errorMessage = HDIVUtil.getMessage(request, "validation.error", e.getMessage());
			throw new HDIVException(errorMessage, e);
		}
	}

	/**
	 * Validate parameter non present in the state.
	 *
	 * @param request HttpServletRequest to validate
	 * @param stateParameter IParameter The restored state for this url
	 * @param actionParamValues actio params values
	 * @param unauthorizedEditableParameters Editable parameters with errors
	 * @param hdivParameter Hdiv state parameter name
	 * @param target Part of the url that represents the target action
	 * @param parameter Parameter name to validate
	 * @return Valid if parameter has not errors
	 * @since HDIV 2.1.13
	 */
	protected ValidatorHelperResult validateExtraParameter(final HttpServletRequest request, final IParameter stateParameter,
			final String[] actionParamValues, final List<ValidatorError> unauthorizedEditableParameters, final String hdivParameter,
			final String target, final String parameter) {

		// If the parameter is not defined in the state, it is an error.
		// With this verification we guarantee that no extra parameters are added.
		if (log.isDebugEnabled()) {
			log.debug("Validation Error Detected: Parameter [" + parameter + "] does not exist in the state for action [" + target + "]");
		}

		ValidatorError error = new ValidatorError(HDIVErrorCodes.PARAMETER_NOT_EXISTS, target, parameter);
		return new ValidatorHelperResult(error);
	}

	/**
	 * Checks if the parameter <code>parameter</code> is defined by the user as a no required validation parameter for the action
	 * <code>this.target</code>.
	 *
	 * @param target target
	 * @param parameter parameter name
	 * @param hdivParameter Hdiv state parameter name
	 * @return True if the parameter doesn't need validation. False otherwise.
	 */
	protected boolean isUserDefinedNonValidationParameter(final String target, final String parameter, final String hdivParameter) {

		// Check if the HDIV validation must be applied to the parameter
		if (!hdivConfig.needValidation(parameter, hdivParameter)) {

			if (log.isDebugEnabled() && !parameter.equals(hdivParameter)) {
				log.debug("Parameter [" + parameter + "] doesn't need validation. It is configured as 'StartParameter'");
			}
			return true;
		}

		if (hdivConfig.isParameterWithoutValidation(target, parameter)) {

			if (log.isDebugEnabled()) {
				log.debug("Parameter [" + parameter + "] doesn't need validation. It is configured as 'ParameterWithoutValidation'.");
			}
			return true;
		}
		return false;
	}

	/**
	 * Restore state from session or <code>request</code> with <code>request</code> identifier. Strategy defined by the user determines the
	 * way the state is restored.
	 *
	 * @param request HTTP request
	 * @param target Part of the url that represents the target action
	 * @return valid result if restored state is valid. False in otherwise.
	 */
	protected ValidatorHelperResult restoreState(final HttpServletRequest request, final String target) {
		return restoreState(getHdivParameter(request), request, target);
	}

	/**
	 * Restore state from session or <code>request</code> with <code>request</code> identifier. Strategy defined by the user determines the
	 * way the state is restored.
	 *
	 * @param request HTTP request
	 * @param target Part of the url that represents the target action
	 * @return valid result if restored state is valid. False in otherwise.
	 */
	protected ValidatorHelperResult restoreState(final String hdivParameter, final HttpServletRequest request, final String target) {

		// checks if the parameter HDIV parameter exists in the parameters of
		// the request
		String requestState = request.getParameter(hdivParameter);

		if (requestState == null) {
			ValidatorError error = new ValidatorError(HDIVErrorCodes.HDIV_PARAMETER_DOES_NOT_EXIST, target, hdivParameter);
			return new ValidatorHelperResult(error);
		}

		// In some browsers (eg: IE 6), fragment identifier is sent with the request, it has to be removed from the
		// requestState
		if (requestState.contains("#")) {
			requestState = requestState.split("#")[0];
		}

		try {
			int pageId = stateUtil.getPageId(requestState);
			RequestContext context = new RequestContext(request);
			IState state = stateUtil.restoreState(context, requestState);

			// Save current page id in request
			HDIVUtil.setCurrentPageId(pageId, request);

			if (stateUtil.isMemoryStrategy(requestState)) {

				if (!validateHDIVSuffix(context, requestState, state)) {
					ValidatorError error = new ValidatorError(HDIVErrorCodes.HDIV_PARAMETER_INCORRECT_VALUE, target, hdivParameter,
							requestState);
					return new ValidatorHelperResult(error);
				}
			}

			// return validation OK and resultant state
			return new ValidatorHelperResult(true, state);

		}
		catch (final HDIVException e) {
			if (log.isDebugEnabled()) {
				log.debug("Error while restoring state:" + requestState, e);
			}
			if (!hdivConfig.getStrategy().equals(Strategy.MEMORY)) {
				requestState = null;
			}

			// HDIVException message contains error code
			ValidatorError error = new ValidatorError(e.getMessage(), target, hdivParameter, requestState);
			return new ValidatorHelperResult(error);
		}
	}

	/**
	 * Checks if the suffix added in the memory version to all requests in the HDIV parameter is the same as the one stored in session,
	 * which is the original suffix. So any request using the memory version should keep the suffix unchanged.
	 *
	 * @param context Request context holder
	 * @param value value received in the HDIV parameter
	 * @param restoredState restored state
	 * @return True if the received value of the suffix is valid. False otherwise.
	 */
	protected boolean validateHDIVSuffix(final RequestContext context, final String value, final IState restoredState) {
		int firstSeparator = value.indexOf(Constants.STATE_ID_SEPARATOR);
		int lastSeparator = value.lastIndexOf(Constants.STATE_ID_SEPARATOR);

		if (firstSeparator == -1) {

			return false;
		}

		if (firstSeparator >= lastSeparator) {

			return false;
		}

		try {
			// read hdiv's suffix from request
			String requestSuffix = value.substring(lastSeparator + 1);

			// read suffix from page stored in session
			String pId = value.substring(0, firstSeparator);
			String sId = value.substring(firstSeparator + 1, lastSeparator);
			int pageId = 0;
			int stateId = 0;
			try {
				stateId = Integer.parseInt(sId);
			}
			catch (final NumberFormatException e) {
				throw new HDIVException(HDIVErrorCodes.PAGE_ID_INCORRECT, e);
			}

			StateScope stateScope = stateScopeManager.getStateScope(value);
			if (stateScope != null) {

				String token = stateScope.getStateToken(context, stateId);
				return requestSuffix.equals(token);
			}

			try {
				pageId = Integer.parseInt(pId);
			}
			catch (final NumberFormatException e) {
				throw new HDIVException(HDIVErrorCodes.PAGE_ID_INCORRECT, e);
			}

			IPage currentPage = restoredState.getPage();
			if (currentPage == null) {
				currentPage = session.getPage(context, pageId);
			}

			if (currentPage == null) {
				if (log.isErrorEnabled()) {
					log.error("Page with id [" + pageId + "] not found in session.");
				}
				throw new HDIVException(HDIVErrorCodes.PAGE_ID_INCORRECT);
			}
			return currentPage.getRandomToken(restoredState.getTokenType()).equals(requestSuffix);

		}
		catch (final IndexOutOfBoundsException e) {
			String errorMessage = HDIVUtil.getMessage(context.getRequest(), "validation.error", e.getMessage());
			if (log.isErrorEnabled()) {
				log.error(errorMessage);
			}
			throw new HDIVException(errorMessage, e);
		}
	}

	/**
	 * Checks if all the received parameter <code>parameter</code> values are valid, that is, are expected values. Received value number is
	 * checked and then these values are validated.
	 *
	 * @param request HttpServletRequest to validate
	 * @param target Part of the url that represents the target action
	 * @param stateParameter Parameter stored in state
	 * @param actionParamValues values of the parameters of the form action
	 * @param parameter Url parameters
	 * @param values parameter <code>parameter</code> values
	 * @return valid result if the validation is correct. False otherwise.
	 * @throws HDIVException if there is an error in parameter validation process.
	 */
	protected ValidatorHelperResult validateParameterValues(final HttpServletRequest request, final String target,
			final IParameter stateParameter, final String[] actionParamValues, final String parameter, final String[] values) {

		try {
			// Only for required parameters must be checked if the number of received
			// values is the same as number of values in the state. If this wasn't
			// taken into account, this verification will be done for every parameter,
			// including for example, a multiple combo where hardly ever are all its
			// values received.
			if (actionParamValues != null) {

				if (values.length != actionParamValues.length) {

					String valueMessage = "";
					if (values.length > actionParamValues.length) {
						if (log.isDebugEnabled()) {
							log.debug("Received more values than expected for the parameter '" + parameter + "'. Received=" + values
									+ ", Expected=" + actionParamValues);
							valueMessage = Arrays.toString(values);
						}
						else {
							log.debug("Received fewer values than expected for the parameter '" + parameter + "'. Received=" + values
									+ ", Expected=" + actionParamValues);
							valueMessage = Arrays.toString(actionParamValues);
						}
					}

					ValidatorError error = new ValidatorError(HDIVErrorCodes.VALUE_LENGTH_INCORRECT, target, parameter, valueMessage);
					return new ValidatorHelperResult(error);
				}
			}

			List<String> stateParamValues = null;
			if (stateParameter != null) {
				stateParamValues = stateParameter.getValues();
			}
			else {
				stateParamValues = Arrays.asList(actionParamValues);
			}

			ValidatorHelperResult result = hasRepeatedOrInvalidValues(request, target, parameter, values, stateParamValues);
			if (!result.isValid()) {
				return result;
			}

			// At this point, we know that the number of received values is the same
			// as the number of values sent to the client. Now we have to check if
			// the received values are all tha ones stored in the state.
			return validateReceivedValuesInState(request, target, stateParameter, actionParamValues, parameter, values);

		}
		catch (final HDIVException e) {
			String errorMessage = HDIVUtil.getMessage(request, "validation.error", e.getMessage());
			throw new HDIVException(errorMessage, e);
		}
	}

	/**
	 * Checks if repeated or no valid values have been received for the parameter <code>parameter</code>.
	 *
	 * @param request HttpServletRequest to validate
	 * @param target Part of the url that represents the target action
	 * @param parameter parameter name
	 * @param values Parameter <code>parameter</code> values
	 * @param stateValues values stored in state for <code>parameter</code>
	 * @return True If repeated or no valid values have been received for the parameter <code>parameter</code>.
	 */
	protected ValidatorHelperResult hasRepeatedOrInvalidValues(final HttpServletRequest request, final String target,
			final String parameter, final String[] values, final List<String> stateValues) {

		List<String> tempStateValues = new ArrayList<String>();
		tempStateValues.addAll(stateValues);

		if (hdivConfig.getConfidentiality()) {
			return hasConfidentialIncorrectValues(request, target, parameter, values, tempStateValues);
		}
		else {
			return hasNonConfidentialIncorrectValues(target, parameter, values, tempStateValues);
		}
	}

	/**
	 * Checks if repeated values have been received for the parameter <code>parameter</code>.
	 *
	 * @param request HttpServletRequest to validate
	 * @param target Part of the url that represents the target action
	 * @param parameter parameter name
	 * @param values Parameter <code>parameter</code> values
	 * @param stateValues real values for <code>parameter</code>
	 * @return True If repeated values have been received for the parameter <code>parameter</code>.
	 */
	protected ValidatorHelperResult hasConfidentialIncorrectValues(final HttpServletRequest request, final String target,
			final String parameter, final String[] values, final List<String> stateValues) {

		Set<String> receivedValues = new HashSet<String>();

		for (int i = 0; i < values.length; i++) {

			if (hdivConfig.isParameterWithoutConfidentiality(request, parameter)) {
				return ValidatorHelperResult.VALID;
			}

			ValidatorHelperResult result = isInRange(target, parameter, values[i], stateValues);
			if (!result.isValid()) {
				return result;
			}

			if (receivedValues.contains(values[i])) {
				String originalValue = stateValues.size() > 1 ? stateValues.toString() : stateValues.get(0);
				ValidatorError error = new ValidatorError(HDIVErrorCodes.REPEATED_VALUES, target, parameter, values[i], originalValue);
				return new ValidatorHelperResult(error);
			}

			receivedValues.add(values[i]);
		}
		return ValidatorHelperResult.VALID;
	}

	/**
	 * Checks if repeated or no valid values have been received for the parameter <code>parameter</code>.
	 *
	 * @param target Part of the url that represents the target action
	 * @param parameter parameter name
	 * @param values Parameter <code>parameter</code> values
	 * @param tempStateValues values stored in state for <code>parameter</code>
	 * @return True If repeated or no valid values have been received for the parameter <code>parameter</code>.
	 */
	protected ValidatorHelperResult hasNonConfidentialIncorrectValues(final String target, final String parameter, final String[] values,
			final List<String> tempStateValues) {

		Set<String> receivedValues = new HashSet<String>();

		for (int i = 0; i < values.length; i++) {

			boolean exists = false;
			for (int j = 0; j < tempStateValues.size() && !exists; j++) {

				String tempValue = tempStateValues.get(j);

				if (tempValue.equalsIgnoreCase(values[i])) {
					tempStateValues.remove(j);
					exists = true;
				}
			}

			if (!exists) {

				String originalValue = "";
				if (tempStateValues.size() == 1) {
					originalValue = tempStateValues.get(0);
				}
				else if (tempStateValues.size() > 1) {
					originalValue = tempStateValues.toString();
				}

				if (receivedValues.contains(values[i])) {
					ValidatorError error = new ValidatorError(HDIVErrorCodes.REPEATED_VALUES, target, parameter, values[i], originalValue);
					return new ValidatorHelperResult(error);
				}

				ValidatorError error = new ValidatorError(HDIVErrorCodes.PARAMETER_VALUE_INCORRECT, target, parameter, values[i],
						originalValue);
				return new ValidatorHelperResult(error);
			}

			receivedValues.add(values[i]);
		}
		return ValidatorHelperResult.VALID;
	}

	/**
	 * Checks if the confidential value received in <code>value</code> is a value lower than the number or values received for the parameter
	 * <code>parameter</code>.
	 *
	 * @param target Part of the url that represents the target action
	 * @param parameter parameter
	 * @param value value
	 * @param stateValues real values for <code>parameter</code>
	 * @return ValidatorHelperResult with the result of the validation.
	 * @since HDIV 2.0
	 */
	protected ValidatorHelperResult isInRange(final String target, final String parameter, final String value,
			final List<String> stateValues) {

		Matcher m = numberPattern.matcher(value);

		try {
			if (!m.matches() || Integer.parseInt(value) >= stateValues.size()) {
				String originalValue = stateValues.size() > 1 ? stateValues.toString() : stateValues.get(0);

				ValidatorError error = new ValidatorError(HDIVErrorCodes.CONFIDENTIAL_VALUE_INCORRECT, target, parameter, value,
						originalValue);
				return new ValidatorHelperResult(error);
			}
		}
		catch (final NumberFormatException e) {
			// value is greater than the length of Integer.MAX_VALUE
			String originalValue = stateValues.size() > 1 ? stateValues.toString() : stateValues.get(0);
			ValidatorError error = new ValidatorError(HDIVErrorCodes.CONFIDENTIAL_VALUE_INCORRECT, target, parameter, value, originalValue);
			return new ValidatorHelperResult(error);
		}
		return ValidatorHelperResult.VALID;
	}

	/**
	 * Checks that values <code>values</code> for the <code>parameter</code> are valid.
	 *
	 * @param request HttpServletRequest to validate
	 * @param target Part of the url that represents the target action
	 * @param stateParameter parameters from the state
	 * @param actionParamValues parameter from the state
	 * @param parameter Parameter name
	 * @param values Parameter <code>parameter</code> values.
	 * @return True If the <code>values</code> validation is correct. False otherwise.
	 */
	protected ValidatorHelperResult validateReceivedValuesInState(final HttpServletRequest request, final String target,
			final IParameter stateParameter, final String[] actionParamValues, final String parameter, final String[] values) {

		int size = values.length;
		String[] originalValues = new String[size];

		for (int i = 0; i < size; i++) {

			IValidationResult result = dataValidator.validate(request, values[i], target, parameter, stateParameter, actionParamValues);
			if (!result.getLegal()) {
				ValidatorError error = new ValidatorError(HDIVErrorCodes.PARAMETER_VALUE_INCORRECT, target, parameter, values[i]);
				return new ValidatorHelperResult(error);
			}
			else {
				originalValues[i] = result.getResult();
			}
		}

		if (hdivConfig.getConfidentiality()) {
			addParameterToRequest(request, parameter, originalValues);
		}

		return ValidatorHelperResult.VALID;
	}

	/**
	 * Adds one parameter to the request. Since the HttpServletRequest object's parameters are unchanged according to the Servlet
	 * specification, the instance of request should be passed as a parameter of type RequestWrapper.
	 *
	 * @param request HttpServletRequest to validate
	 * @param name new parameter name
	 * @param value new parameter value
	 * @throws HDIVException if the request object is not of type RequestWrapper
	 */
	protected void addParameterToRequest(final HttpServletRequest request, final String name, final String[] value) {

		RequestWrapper wrapper = null;

		if (request instanceof RequestWrapper) {
			wrapper = (RequestWrapper) request;
		}
		else {
			wrapper = (RequestWrapper) getNativeRequest(request, RequestWrapper.class);
		}

		if (wrapper != null) {
			wrapper.addParameter(name, value);
		}
		else {
			String errorMessage = HDIVUtil.getMessage(request, "helper.notwrapper");
			throw new HDIVException(errorMessage);
		}

	}

	/**
	 * Mark parameter as editable.
	 *
	 * @param request HttpServletRequest to validate
	 * @param name parameter name
	 */
	protected void addEditableParameter(final HttpServletRequest request, final String name) {

		if (request instanceof RequestWrapper) {
			if (log.isDebugEnabled()) {
				log.debug("Editable parameter [" + name + "] added.");
			}
			RequestWrapper wrapper = (RequestWrapper) request;
			wrapper.addEditableParameter(name);
		}
	}

	protected ServletRequest getNativeRequest(final ServletRequest request, final Class<?> requiredType) {
		if (requiredType != null) {
			if (requiredType.isInstance(request)) {
				return request;
			}
			else if (request instanceof ServletRequestWrapper) {
				return getNativeRequest(((ServletRequestWrapper) request).getRequest(), requiredType);
			}
		}
		return null;
	}

	/**
	 * Name of the parameter that HDIV will include in the requests or/and forms which contains the state identifier in the memory strategy.
	 *
	 * @param request request
	 * @return hdiv parameter value
	 */
	protected String getHdivParameter(final HttpServletRequest request) {

		String paramName = HDIVUtil.getHdivStateParameterName(request);

		if (paramName == null) {
			throw new HDIVException("HDIV parameter name missing in session. Deleted by the app?");
		}
		return paramName;
	}

	/**
	 * <p>
	 * Method invoked before validation. Designed to change the validation logic beyond the base implementation.
	 * </p>
	 * The response of the method can have three meanings:
	 * <ul>
	 * <li>Valid ValidatorHelperResult: The validation has been completed correctly and is not necessary to run the entire validation
	 * process.</li>
	 * <li>Error ValidatorHelperResult: The validation has encountered an error and terminates the validation process.</li>
	 * <li>null: It should continue with the validation process (default answer).</li>
	 * </ul>
	 *
	 * @param request HttpServletRequest to validate
	 * @param target target url
	 * @return ValidatorHelperResult result
	 */
	protected ValidatorHelperResult preValidate(final HttpServletRequest request, final String target) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.hdiv.filter.IValidationHelper#startPage(javax.servlet.http. HttpServletRequest)
	 */
	public void startPage(final HttpServletRequest request) {

		// Don`t create IDataComposer if it is not necessary
		boolean exclude = hdivConfig.hasExtensionToExclude(request.getRequestURI());
		if (!exclude) {

			// Init datacomposer
			IDataComposer dataComposer = dataComposerFactory.newInstance(request);

			HDIVUtil.setDataComposer(dataComposer, request);
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.hdiv.filter.IValidationHelper#endPage(javax.servlet.http. HttpServletRequest)
	 */
	public void endPage(final HttpServletRequest request) {

		// End page in datacomposer
		IDataComposer dataComposer = HDIVUtil.getDataComposer(request);

		if (dataComposer != null) {
			dataComposer.endPage();

			RequestWrapper wrapper = HDIVUtil.getNativeRequest(request, RequestWrapper.class);
			if (wrapper == null || !wrapper.isAsyncRequest()) {
				// If this is an Async request, don't remove IDataComposer from request.
				HDIVUtil.removeDataComposer(request);
			}
		}

	}

	/**
	 * @param stateUtil The state utility to set.
	 */
	public void setStateUtil(final StateUtil stateUtil) {
		this.stateUtil = stateUtil;
	}

	/**
	 * @param hdivConfig The HDIV configuration object to set.
	 */
	public void setHdivConfig(final HDIVConfig hdivConfig) {
		this.hdivConfig = hdivConfig;
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(final ISession session) {
		this.session = session;
	}

	/**
	 * @param dataValidator the dataValidator to set
	 */
	public void setDataValidator(final IDataValidator dataValidator) {
		this.dataValidator = dataValidator;
	}

	/**
	 * @param numberPattern the numberPattern to set
	 */
	public void setNumberPattern(final Pattern numberPattern) {
		this.numberPattern = numberPattern;
	}

	/**
	 * @param dataComposerFactory the dataComposerFactory to set
	 */
	public void setDataComposerFactory(final DataComposerFactory dataComposerFactory) {
		this.dataComposerFactory = dataComposerFactory;
	}

	/**
	 * @param urlProcessor the urlProcessor to set
	 */
	public void setUrlProcessor(final BasicUrlProcessor urlProcessor) {
		this.urlProcessor = urlProcessor;
	}

	/**
	 * @param stateScopeManager the stateScopeManager to set
	 */
	public void setStateScopeManager(final StateScopeManager stateScopeManager) {
		this.stateScopeManager = stateScopeManager;
	}

}
