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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hdiv.config.HDIVConfig;
import org.hdiv.context.RequestContextHolder;
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
import org.hdiv.urlProcessor.UrlData;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.util.HDIVStateUtils;
import org.hdiv.util.HDIVUtil;
import org.hdiv.util.Method;
import org.hdiv.validator.EditableDataValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ValidatorHelperRequest implements IValidationHelper, StateRestorer {

	private static final String VALIDATION_ERROR = "validation.error";

	/**
	 * Commons Logging instance.
	 */
	private static final Logger log = LoggerFactory.getLogger(ValidatorHelperRequest.class);

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

	private void processPenTesting(final ValidationContext context) {
		ValidatorHelperResult ptresult = restoreState(context);
		if (ptresult.isValid()) {
			List<String> editable = new ArrayList<String>();
			context.getRequestContext().getResponse().setContentType("text/html");

			if (ptresult.getValue().getParameters() != null) {
				for (IParameter parameter : ptresult.getValue().getParameters()) {
					if (parameter.isEditable()) {
						editable.add(parameter.getName());
					}
				}
			}
			for (int i = 0; i < editable.size(); i++) {

				try {
					PrintWriter out = context.getRequestContext().getResponse().getWriter();
					if (i != 0) {
						out.write(',');
					}
					out.write(editable.get(i));
					out.flush();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			throw new ValidationErrorException(ValidatorHelperResult.PEN_TESTING);
		}
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
	 * @param context request context
	 * @return valid result If all the parameter values of the request <code>request</code> pass the the HDIV validation. False, otherwise.
	 * @throws HDIVException If the request doesn't pass the HDIV validation an exception is thrown explaining the cause of the error.
	 */
	@SuppressWarnings("unused")
	public ValidatorHelperResult validate(final ValidationContext context) {

		String target = context.getTarget();
		RequestContextHolder ctx = context.getRequestContext();

		if (target.endsWith(UrlData.PEN_TESTING_ROOT_PATH) && hdivConfig.isPentestingActive()) {
			processPenTesting(context);
		}

		// Hook before the validation
		ValidatorHelperResult result = preValidate(context);
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

			boolean requestHasParameters = ctx.getParameterNames() != null && ctx.getParameterNames().hasMoreElements();
			if (!requestHasParameters) {
				if (log.isDebugEnabled()) {
					log.debug("The url [" + ctx.getRequestURI() + "] is not be validated because it has not got parameters");
				}
				return ValidatorHelperResult.VALIDATION_NOT_REQUIRED;
			}
		}

		if (isStartPage(ctx, target)) {
			result = validateStartPageParameters(ctx, target);
			if (result.isValid()) {
				if (log.isDebugEnabled()) {
					log.debug("The target [" + target + "] is an start page and parameters are valid.");
				}
				return ValidatorHelperResult.VALIDATION_NOT_REQUIRED;
			}
			else {
				if (log.isDebugEnabled()) {
					log.debug("The target [" + target + "] is an start page and parameters are NOT valid.");
				}
				return result;
			}
		}

		if (hdivConfig.isCookiesIntegrityActivated()) {
			result = validateRequestCookies(context, target);
			if (!result.isValid()) {
				if (log.isDebugEnabled()) {
					log.debug("Invalid cookies found.");
				}
				return result;
			}
		}

		// Hdiv parameter name
		String hdivParameter = context.getRequestContext().getHdivParameterName();

		// Restore state from request or memory
		result = restoreState(context);
		if (!result.isValid()) {
			if (log.isDebugEnabled()) {
				log.debug("Error restoring the state: " + result);
			}
			return result;
		}
		// Get resultant object, the stored state
		IState state = result.getValue();

		result = isTheSameAction(ctx, target, state);
		if (!result.isValid()) {
			return result;
		}

		// Extract url params from State
		Map<String, String[]> stateParams = urlProcessor.getUrlParamsAsMap(hdivParameter, context.getBuffer(), state.getParams());

		result = allRequiredParametersReceived(ctx, state, target, stateParams);
		if (!result.isValid()) {
			return result;
		}

		List<ValidatorError> unauthorizedEditableParameters = new ArrayList<ValidatorError>();
		Enumeration<?> parameters = ctx.getParameterNames();
		while (parameters.hasMoreElements()) {

			String parameter = (String) parameters.nextElement();

			// Validate parameter
			result = validateParameter(ctx, stateParams, state.getParameter(parameter), stateParams.get(parameter),
					unauthorizedEditableParameters, hdivParameter, target, parameter);
			if (!result.isValid()) {
				return result;
			}

		}

		if (!unauthorizedEditableParameters.isEmpty()) {
			return new ValidatorHelperResult(unauthorizedEditableParameters);
		}
		return ValidatorHelperResult.VALID;
	}

	@Deprecated
	protected final boolean isStartPage(final HttpServletRequest request, final String target) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Check if the current request is a start page.
	 *
	 * @param request HttpServletRequest to validate
	 * @param target Part of the url that represents the target action
	 * @return true if it is a start page
	 */
	protected boolean isStartPage(final RequestContextHolder request, final String target) {
		return hdivConfig.isStartPage(target, Method.secureValueOf(request.getMethod()));
	}

	@Deprecated
	protected final ValidatorHelperResult isTheSameAction(final HttpServletRequest request, final String target, final IState state) {
		return isTheSameAction(request, target, state.getAction());
	}

	@Deprecated
	protected final ValidatorHelperResult isTheSameAction(final HttpServletRequest request, final String target, final String stateAction) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Checks if the action received in the request is the same as the one stored in the HDIV state.
	 *
	 * @param context Request context
	 * @param target Part of the url that represents the target action
	 * @param state The restored state for this url
	 * @return valid result if the actions are the same. False otherwise.
	 */
	protected ValidatorHelperResult isTheSameAction(final RequestContextHolder context, final String target, final IState state) {
		return isTheSameAction(context, target, state.getAction());
	}

	/**
	 * Checks if the action received in the request is the same as the one stored in the HDIV state.
	 *
	 * @param context Request context
	 * @param target Part of the url that represents the target action
	 * @param stateAction The restored state for this url
	 * @return valid result if the actions are the same. False otherwise.
	 */
	protected ValidatorHelperResult isTheSameAction(final RequestContextHolder context, final String target, String stateAction) {

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
			log.debug("Validation error in the action. Action in state [" + stateAction + "], action in the request [" + target + "]");
		}
		ValidatorError error = new ValidatorError(HDIVErrorCodes.INVALID_ACTION, target);
		return new ValidatorHelperResult(error);
	}

	@Deprecated
	protected final ValidatorHelperResult validateStartPageParameters(final HttpServletRequest request, final String target) {
		throw new UnsupportedOperationException();
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
	protected ValidatorHelperResult validateStartPageParameters(final RequestContextHolder request, final String target) {

		List<ValidatorError> unauthorizedEditableParameters = new ArrayList<ValidatorError>();

		Enumeration<?> parameters = request.getParameterNames();
		while (parameters.hasMoreElements()) {

			String parameter = (String) parameters.nextElement();
			String[] values = request.getParameterValues(parameter);

			validateEditableParameter(target, parameter, values, "text", unauthorizedEditableParameters);
		}

		if (!unauthorizedEditableParameters.isEmpty()) {
			return new ValidatorHelperResult(unauthorizedEditableParameters);
		}
		return ValidatorHelperResult.VALID;
	}

	/**
	 * Checks if the cookies received in the request are correct. For that, it checks if they are in the user session.
	 *
	 * @param context Validation context
	 * @param target Part of the url that represents the target action
	 * @return valid result if all the cookies received in the request are correct. They must have been previously stored in the user
	 * session by HDIV to be correct. False otherwise.
	 * @since HDIV 1.1
	 */
	protected final ValidatorHelperResult validateRequestCookies(final ValidationContext context, final String target) {

		Cookie[] requestCookies = context.getRequestContext().getCookies();

		if (requestCookies == null || requestCookies.length == 0) {
			return ValidatorHelperResult.VALID;
		}

		@SuppressWarnings("unchecked")
		Map<String, SavedCookie> sessionCookies = session.getAttribute(context.getRequestContext(), // TODO cache
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
			if (requestCookies[i].getName().equals(Constants.HDIV_RANDOM_COOKIE)) {
				continue;
			}

			SavedCookie savedCookie = null;
			if (sessionCookies.containsKey(requestCookies[i].getName())) {

				savedCookie = sessionCookies.get(requestCookies[i].getName());
				if (savedCookie.isEqual(requestCookies[i], cookiesConfidentiality)) {

					found = true;
					if (cookiesConfidentiality && savedCookie.getValue() != null) {
						requestCookies[i].setValue(savedCookie.getValue());
					}
				}
			}

			if (!found) {
				ValidatorError error = new ValidatorError(HDIVErrorCodes.INVALID_COOKIE, target, requestCookies[i].getName(),
						requestCookies[i].getValue(), savedCookie != null ? savedCookie.getValue() : null);
				return new ValidatorHelperResult(error);
			}
		}
		return ValidatorHelperResult.VALID;
	}

	/**
	 * Checks if the values <code>values</code> are valid for the editable parameter <code>parameter</code>. If the values are not valid, an
	 * error message with the parameter and the received values will be log.
	 *
	 * @param target Part of the url that represents the target action
	 * @param parameter parameter name
	 * @param values parameter's values
	 * @param dataType editable data type
	 * @param unauthorizedParameters Unauthorized editable parameters
	 * @since HDIV 1.1
	 */
	protected void validateEditableParameter(final String target, final String parameter, final String[] values, final String dataType,
			final List<ValidatorError> unauthorizedParameters) {

		EditableDataValidationResult result = hdivConfig.getEditableDataValidationProvider().validate(target, parameter, values, dataType);
		if (!result.isValid()) {

			String value;

			if ("password".equals(dataType)) {
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

			unauthorizedParameters.add(createEditableValidatorError(result, target, parameter, value));
		}
	}

	protected ValidatorError createEditableValidatorError(final EditableDataValidationResult result, final String target,
			final String parameter, final String value) {

		return new ValidatorError(HDIVErrorCodes.INVALID_EDITABLE_VALUE, result.getRule(), target, parameter, value, null, null, null, null,
				result.getValidationId());
	}

	@Deprecated
	protected final ValidatorHelperResult allRequiredParametersReceived(final HttpServletRequest request, final IState state,
			final String target, final Map<String, String[]> stateParams) {
		throw new UnsupportedOperationException();
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
	protected ValidatorHelperResult allRequiredParametersReceived(final RequestContextHolder request, final IState state,
			final String target, final Map<String, String[]> stateParams) {

		List<String> requiredParameters = state.getRequiredParams(hdivConfig.getEditableFieldsRequiredByDefault());
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
		if (!required.isEmpty()) {
			Iterator<String> it = required.iterator();
			while (it.hasNext()) {
				String req = it.next();
				if (isNoValueParameter(request, req)) {
					it.remove();
				}
			}
		}

		return validateMissingParameters(request, state, target, stateParams, required);
	}

	@Deprecated
	protected final ValidatorHelperResult validateMissingParameters(final HttpServletRequest request, final IState state,
			final String target, final Map<String, String[]> stateParams, final List<String> missingParameters) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Validate required parameters but not received in the request.
	 * 
	 * @param request HttpServletRequest to validate
	 * @param state IState The restored state for this url
	 * @param target Part of the url that represents the target action
	 * @param stateParams Url params from State
	 * @param missingParameters Required parameters not received in the request.
	 * @return result with the error
	 */
	protected ValidatorHelperResult validateMissingParameters(final RequestContextHolder request, final IState state, final String target,
			final Map<String, String[]> stateParams, final List<String> missingParameters) {

		if (missingParameters.isEmpty()) {
			return ValidatorHelperResult.VALID;
		}

		if (log.isDebugEnabled()) {
			log.debug("Missing some required parameters: " + missingParameters.toString());
		}

		ValidatorError error = new ValidatorError(HDIVErrorCodes.NOT_RECEIVED_ALL_REQUIRED_PARAMETERS, target,
				missingParameters.toString());
		return new ValidatorHelperResult(error);
	}

	/**
	 * Check if the given parameter doesn't have values looking in the query string.
	 * 
	 * @param request HttpServletRequest instance
	 * @param parameter Parameter name
	 * @return true if the parameter does't have value
	 */
	private boolean isNoValueParameter(final RequestContextHolder request, final String parameter) {

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

	@Deprecated
	protected final ValidatorHelperResult validateParameter(final HttpServletRequest request, final Map<String, String[]> stateParams,
			final IParameter stateParameter, final String[] actionParamValues, final List<ValidatorError> unauthorizedEditableParameters,
			final String hdivParameter, final String target, final String parameter) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Validate single parameter values.
	 *
	 * @param request HttpServletRequest to validate
	 * @param stateParams parameter and values from the parameters stored in the state
	 * @param stateParameter IParameter The restored state for this url
	 * @param actionParamValues action params values
	 * @param unauthorizedEditableParameters Editable parameters with errors
	 * @param hdivParameter Hdiv state parameter name
	 * @param target Part of the url that represents the target action
	 * @param parameter Parameter name to validate
	 * @return Valid if parameter has not errors
	 * @since HDIV 2.1.5
	 */
	protected ValidatorHelperResult validateParameter(final RequestContextHolder request, final Map<String, String[]> stateParams,
			final IParameter stateParameter, final String[] actionParamValues, final List<ValidatorError> unauthorizedEditableParameters,
			final String hdivParameter, final String target, final String parameter) {

		// If the parameter requires no validation it is considered a valid parameter
		if (isUserDefinedNonValidationParameter(target, parameter, hdivParameter)) {
			return ValidatorHelperResult.VALIDATION_NOT_REQUIRED;
		}

		if (stateParameter == null && actionParamValues == null) {

			// The parameter is not defined in the state, it is an extra parameter.
			return validateExtraParameter(request, stateParams, stateParameter, actionParamValues, unauthorizedEditableParameters,
					hdivParameter, target, parameter);
		}

		// At this point we are processing a noneditable parameter
		String[] values = request.getParameterValues(parameter);

		// Check if the parameter is editable
		if (stateParameter != null && stateParameter.isEditable()) {

			// Mark parameter as editable
			request.addEditableParameter(parameter);

			if (stateParameter.getEditableDataType() != null) {
				validateEditableParameter(target, parameter, values, stateParameter.getEditableDataType(), unauthorizedEditableParameters);
			}
			return ValidatorHelperResult.VALID;
		}

		try {
			return validateParameterValues(request, target, stateParameter, actionParamValues, parameter, values);
		}
		catch (final HDIVException e) {
			String errorMessage = request.getMessage(VALIDATION_ERROR, e.getMessage());
			throw new HDIVException(errorMessage, e);
		}
	}

	@Deprecated
	protected final ValidatorHelperResult validateExtraParameter(final HttpServletRequest request, final Map<String, String[]> stateParams,
			final IParameter stateParameter, final String[] actionParamValues, final List<ValidatorError> unauthorizedEditableParameters,
			final String hdivParameter, final String target, final String parameter) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Validate parameter non present in the state.
	 *
	 * @param request HttpServletRequest to validate
	 * @param stateParams parameter and values from the parameters stored in the state
	 * @param stateParameter IParameter The restored state for this url
	 * @param actionParamValues actio params values
	 * @param unauthorizedEditableParameters Editable parameters with errors
	 * @param hdivParameter Hdiv state parameter name
	 * @param target Part of the url that represents the target action
	 * @param parameter Parameter name to validate
	 * @return Valid if parameter has not errors
	 * @since HDIV 2.1.13
	 */
	protected ValidatorHelperResult validateExtraParameter(final RequestContextHolder request, final Map<String, String[]> stateParams,
			final IParameter stateParameter, final String[] actionParamValues, final List<ValidatorError> unauthorizedEditableParameters,
			final String hdivParameter, final String target, final String parameter) {

		// If the parameter is not defined in the state, it is an error.
		// With this verification we guarantee that no extra parameters are added.
		if (log.isDebugEnabled()) {
			log.debug("Validation Error Detected: Parameter [" + parameter + "] does not exist in the state for action [" + target + "]");
		}

		ValidatorError error = new ValidatorError(HDIVErrorCodes.INVALID_PARAMETER_NAME, target, parameter,
				request.getParameter(parameter));
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
	 * @param context validation context
	 * @return valid result if restored state is valid. False in otherwise.
	 */
	public final ValidatorHelperResult restoreState(final ValidationContext context) {
		return restoreState(context, context.getRequestContext().getHdivState());
	}

	@Deprecated
	protected final ValidatorHelperResult restoreState(final String hdivParameter, final ValidationContext context) {
		// checks if the parameter HDIV parameter exists in the parameters of the request
		return restoreState(hdivParameter, context.getRequestContext(), context.getRequestedTarget(),
				context.getRequestContext().getParameter(hdivParameter));
	}

	@Deprecated
	protected final ValidatorHelperResult restoreState(final String hdivParameter, final String hdivState,
			final ValidationContext context) {
		// checks if the parameter HDIV parameter exists in the parameters of the requestds
		return restoreState(hdivParameter, context.getRequestContext(), context.getRequestedTarget(), hdivState);
	}

	@Deprecated
	protected final ValidatorHelperResult restoreState(final String hdivParameter, final HttpServletRequest request, final String target,
			final String requestState) {
		return restoreState(hdivParameter, HDIVUtil.getRequestContext(request), target, requestState);
	}

	@Deprecated
	protected final ValidatorHelperResult restoreState(final String hdivParameter, final RequestContextHolder context, final String target,
			final String requestState) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Restore state from session or <code>request</code> with <code>request</code> identifier. Strategy defined by the user determines the
	 * way the state is restored.
	 *
	 * @param context Validation context
	 * @param requestState Request state
	 * @return valid result if restored state is valid. False in otherwise.
	 */
	public final ValidatorHelperResult restoreState(final ValidationContext context, String requestState) {

		if (requestState == null) {
			ValidatorError error = new ValidatorError(HDIVErrorCodes.HDIV_PARAMETER_DOES_NOT_EXIST, context.getRequestedTarget(),
					context.getRequestContext().getHdivParameterName());
			return new ValidatorHelperResult(error);
		}

		// In some browsers (eg: IE 6), fragment identifier is sent with the request, it has to be removed from the
		// requestState
		if (requestState.contains("#")) {
			requestState = requestState.split("#")[0];
		}

		try {
			RequestContextHolder ctx = context.getRequestContext();
			UUID pageId = stateUtil.getPageId(requestState);
			IState state = doRestoreState(context, requestState);

			// Save current page id in request
			ctx.setCurrentPageId(pageId);

			if (!validateHDIVSuffix(ctx, requestState, state)) {
				ValidatorError error = new ValidatorError(HDIVErrorCodes.INVALID_HDIV_PARAMETER_VALUE, context.getRequestedTarget(),
						context.getRequestContext().getHdivParameterName(), requestState);
				return new ValidatorHelperResult(error);
			}

			// return validation OK and resultant state
			return new ValidatorHelperResult(true, state);

		}
		catch (final HDIVException e) {
			if (log.isDebugEnabled()) {
				log.debug("Error while restoring state:" + requestState, e);
			}

			// HDIVException message contains error code
			ValidatorError error = new ValidatorError(e.getMessage(), context.getRequestedTarget(),
					context.getRequestContext().getHdivParameterName(), requestState);
			return new ValidatorHelperResult(error);
		}
	}

	protected IState doRestoreState(final ValidationContext ctx, final String requestState) {
		return stateUtil.restoreState(ctx.getRequestContext(), requestState);
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
	protected boolean validateHDIVSuffix(final RequestContextHolder context, final String value, final IState restoredState) {
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
			int stateId = 0;
			try {
				stateId = Integer.parseInt(sId);
			}
			catch (final NumberFormatException e) {
				throw new HDIVException(HDIVErrorCodes.INVALID_PAGE_ID, e);
			}

			StateScope stateScope = stateScopeManager.getStateScope(value);
			if (stateScope != null) {

				String token = stateScope.getStateToken(context, stateId);
				return requestSuffix.equals(token);
			}

			IPage currentPage = restoredState.getPage();
			if (currentPage == null) {
				currentPage = session.getPage(context, HDIVStateUtils.parsePageId(pId));
			}

			if (currentPage == null) {
				if (log.isErrorEnabled()) {
					log.error("Page with id [" + pId + "] not found in session.");
				}
				throw new HDIVException(HDIVErrorCodes.INVALID_PAGE_ID);
			}
			return currentPage.getRandomToken(restoredState.getTokenType()).equals(requestSuffix);

		}
		catch (final IndexOutOfBoundsException e) {
			String errorMessage = context.getMessage(VALIDATION_ERROR, e.getMessage());
			if (log.isErrorEnabled()) {
				log.error(errorMessage);
			}
			throw new HDIVException(errorMessage, e);
		}
	}

	@Deprecated
	protected final ValidatorHelperResult validateParameterValues(final HttpServletRequest request, final String target,
			final IParameter stateParameter, final String[] actionParamValues, final String parameter, final String[] values) {
		throw new UnsupportedOperationException();
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
	protected ValidatorHelperResult validateParameterValues(final RequestContextHolder request, final String target,
			final IParameter stateParameter, final String[] actionParamValues, final String parameter, final String[] values) {

		try {
			// Only for required parameters must be checked if the number of received
			// values is the same as number of values in the state. If this wasn't
			// taken into account, this verification will be done for every parameter,
			// including for example, a multiple combo where hardly ever are all its
			// values received.
			if (actionParamValues != null && values.length != actionParamValues.length) {

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

				ValidatorError error = new ValidatorError(HDIVErrorCodes.NOT_RECEIVED_ALL_PARAMETER_VALUES, target, parameter,
						valueMessage);
				return new ValidatorHelperResult(error);

			}

			List<String> stateParamValues;
			if (stateParameter != null) {
				stateParamValues = stateParameter.getValues();
			}
			else {
				stateParamValues = Arrays.asList(actionParamValues);
			}

			ValidatorHelperResult result = hasRepeatedOrInvalidValues(request, target, parameter, values, stateParamValues, stateParameter);
			if (!result.isValid()) {
				return result;
			}

			// At this point, we know that the number of received values is the same
			// as the number of values sent to the client. Now we have to check if
			// the received values are all tha ones stored in the state.
			return validateReceivedValuesInState(request, target, stateParameter, actionParamValues, parameter, values);

		}
		catch (final HDIVException e) {
			String errorMessage = request.getMessage(VALIDATION_ERROR, e.getMessage());
			throw new HDIVException(errorMessage, e);
		}
	}

	@Deprecated
	protected final ValidatorHelperResult hasRepeatedOrInvalidValues(final HttpServletRequest request, final String target,
			final String parameter, final String[] values, final List<String> stateValues) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	protected final ValidatorHelperResult hasRepeatedOrInvalidValues(final RequestContextHolder request, final String target,
			final String parameter, final String[] values, final List<String> stateValues) {
		return hasRepeatedOrInvalidValues(request, target, parameter, values, stateValues, null);
	}

	/**
	 * Checks if repeated or no valid values have been received for the parameter <code>parameter</code>.
	 *
	 * @param request HttpServletRequest to validate
	 * @param target Part of the url that represents the target action
	 * @param parameter parameter name
	 * @param values Parameter <code>parameter</code> values
	 * @param stateValues values stored in state for <code>parameter</code>
	 * @param stateParameter parameter info in the state
	 * @return True If repeated or no valid values have been received for the parameter <code>parameter</code>.
	 */
	protected final ValidatorHelperResult hasRepeatedOrInvalidValues(final RequestContextHolder request, final String target,
			final String parameter, final String[] values, final List<String> stateValues, final IParameter stateParameter) {

		List<String> tempStateValues = new ArrayList<String>();
		tempStateValues.addAll(stateValues);

		if (hdivConfig.getConfidentiality()) {
			return hasConfidentialIncorrectValues(request, target, parameter, values, tempStateValues, stateParameter);
		}
		else {
			return hasNonConfidentialIncorrectValues(target, parameter, values, tempStateValues);
		}
	}

	@Deprecated
	protected final ValidatorHelperResult hasConfidentialIncorrectValues(final HttpServletRequest request, final String target,
			final String parameter, final String[] values, final List<String> stateValues) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	protected final ValidatorHelperResult hasConfidentialIncorrectValues(final RequestContextHolder request, final String target,
			final String parameter, final String[] values, final List<String> stateValues) {
		return hasConfidentialIncorrectValues(request, target, parameter, values, stateValues, null);
	}

	/**
	 * Checks if repeated values have been received for the parameter <code>parameter</code>.
	 *
	 * @param request HttpServletRequest to validate
	 * @param target Part of the url that represents the target action
	 * @param parameter parameter name
	 * @param values Parameter <code>parameter</code> values
	 * @param stateValues real values for <code>parameter</code>
	 * @param stateParameter parameter info in the state
	 * @return True If repeated values have been received for the parameter <code>parameter</code>.
	 */
	protected final ValidatorHelperResult hasConfidentialIncorrectValues(final RequestContextHolder request, final String target,
			final String parameter, final String[] values, final List<String> stateValues, final IParameter stateParameter) {

		Set<String> receivedValues = new HashSet<String>();

		for (int i = 0; i < values.length; i++) {

			if (hdivConfig.isParameterWithoutConfidentiality(request, parameter)
					|| stateParameter != null && HDIVUtil.isNonConfidentialType(stateParameter.getEditableDataType())) {
				return ValidatorHelperResult.VALID;
			}

			ValidatorHelperResult result = isInRange(target, parameter, values[i], stateValues);
			if (!result.isValid()) {
				return result;
			}

			if (receivedValues.contains(values[i])) {
				String originalValue = stateValues.size() > 1 ? stateValues.toString() : stateValues.get(0);
				ValidatorError error = new ValidatorError(HDIVErrorCodes.REPEATED_VALUES_FOR_PARAMETER, target, parameter, values[i],
						originalValue);
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

				if (tempValue.equalsIgnoreCase(values[i]) || HDIVUtil.isTheSameEncodedValue(tempValue, values[i])) {
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
					ValidatorError error = new ValidatorError(HDIVErrorCodes.REPEATED_VALUES_FOR_PARAMETER, target, parameter, values[i],
							originalValue);
					return new ValidatorHelperResult(error);
				}

				ValidatorError error = new ValidatorError(HDIVErrorCodes.INVALID_PARAMETER_VALUE, target, parameter, values[i],
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

				ValidatorError error = new ValidatorError(HDIVErrorCodes.INVALID_CONFIDENTIAL_VALUE, target, parameter, value,
						originalValue);
				return new ValidatorHelperResult(error);
			}
		}
		catch (final NumberFormatException e) {
			// value is not a number or is greater than the length of Integer.MAX_VALUE
			String originalValue = stateValues.size() > 1 ? stateValues.toString() : stateValues.get(0);
			ValidatorError error = new ValidatorError(HDIVErrorCodes.INVALID_CONFIDENTIAL_VALUE, target, parameter, value, originalValue);
			return new ValidatorHelperResult(error);
		}
		return ValidatorHelperResult.VALID;
	}

	@Deprecated
	protected final ValidatorHelperResult validateReceivedValuesInState(final HttpServletRequest request, final String target,
			final IParameter stateParameter, final String[] actionParamValues, final String parameter, final String[] values) {
		throw new UnsupportedOperationException();
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
	protected ValidatorHelperResult validateReceivedValuesInState(final RequestContextHolder request, final String target,
			final IParameter stateParameter, final String[] actionParamValues, final String parameter, final String[] values) {

		int size = values.length;
		String[] originalValues = new String[size];

		for (int i = 0; i < size; i++) {

			IValidationResult result = dataValidator.validate(request, values[i], target, parameter, stateParameter, actionParamValues);
			if (!result.getLegal()) {
				ValidatorError error = new ValidatorError(HDIVErrorCodes.INVALID_PARAMETER_VALUE, target, parameter, values[i]);
				return new ValidatorHelperResult(error);
			}
			else {
				originalValues[i] = result.getResult();
			}
		}

		if (hdivConfig.getConfidentiality()) {
			request.addParameterToRequest(parameter, originalValues);
		}

		return ValidatorHelperResult.VALID;
	}

	@Deprecated
	protected final void addParameterToRequest(final HttpServletRequest request, final String name, final String[] value) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	protected final void addEditableParameter(final HttpServletRequest request, final String name) {
		throw new UnsupportedOperationException();
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
	 * @param context Validation context
	 * @return ValidatorHelperResult result
	 */
	protected ValidatorHelperResult preValidate(final ValidationContext context) {
		return null;
	}

	@Deprecated
	protected final ValidatorHelperResult preValidate(final HttpServletRequest request, final String target) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.hdiv.filter.IValidationHelper#startPage(javax.servlet.http. HttpServletRequest)
	 */
	public void startPage(final RequestContextHolder request) {

		// Don`t create IDataComposer if it is not necessary
		boolean exclude = hdivConfig.hasExtensionToExclude(request.getRequestURI());
		if (!exclude) {

			// Init datacomposer
			request.setDataComposer(dataComposerFactory.newInstance(request));
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.hdiv.filter.IValidationHelper#endPage(javax.servlet.http. HttpServletRequest)
	 */
	public void endPage(final RequestContextHolder request) {

		// End page in datacomposer
		IDataComposer dataComposer = request.getDataComposer();

		if (dataComposer != null) {
			dataComposer.endPage();

			boolean disableClean = Boolean.getBoolean("hdiv.async.clean.disabled");

			if (!disableClean && !request.isAsync()) {
				// If this is an Async request, don't remove IDataComposer from request.
				request.setDataComposer(null);
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

	public boolean isInternal(final HttpServletRequest request, final HttpServletResponse response) {
		return false;
	}

	public List<ValidatorError> findCustomErrors(final Throwable t, final String target) {
		return Collections.emptyList();
	}

	public boolean areErrorsLegal(final List<ValidatorError> errors) {
		if (errors != null && !errors.isEmpty() && (!hdivConfig.isIntegrityValidation() || !hdivConfig.isEditableValidation())) {
			for (Iterator<ValidatorError> iterator = errors.iterator(); iterator.hasNext();) {
				ValidatorError validatorError = iterator.next();
				if (shouldErrorBeRemoved(validatorError)) {
					iterator.remove();
				}
			}
			if (errors.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public boolean processEditableValidationErrors(final RequestContextHolder request, final List<ValidatorError> errors) {

		List<ValidatorError> editableErrors = new ArrayList<ValidatorError>();
		for (ValidatorError error : errors) {
			if (HDIVErrorCodes.isEditableError(error.getType())) {
				editableErrors.add(error);
			}
		}
		if (!editableErrors.isEmpty() && hdivConfig.isEditableValidation()) {

			// Put the errors on request to be accessible from the Web framework
			request.setAttribute(Constants.EDITABLE_PARAMETER_ERROR, editableErrors);

			if (hdivConfig.isShowErrorPageOnEditableValidation()) {
				// Redirect to error page
				// Put errors in session to be accessible from error page
				request.getSession().setAttribute(Constants.EDITABLE_PARAMETER_ERROR, editableErrors);
			}
		}
		return !editableErrors.isEmpty();
	}

	public boolean shouldErrorBeRemoved(final ValidatorError validatorError) {
		boolean editable = HDIVErrorCodes.isEditableError(validatorError.getType());
		if (!hdivConfig.isEditableValidation() && editable) {
			return true;
		}
		if (!hdivConfig.isIntegrityValidation() && !editable) {
			return true;
		}
		return false;
	}

}
