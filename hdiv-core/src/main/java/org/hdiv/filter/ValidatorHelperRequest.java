/**
 * Copyright 2005-2011 hdiv.org
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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
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
import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.dataValidator.DataValidatorFactory;
import org.hdiv.dataValidator.IDataValidator;
import org.hdiv.dataValidator.IValidationResult;
import org.hdiv.exception.HDIVException;
import org.hdiv.logs.Logger;
import org.hdiv.session.ISession;
import org.hdiv.state.IPage;
import org.hdiv.state.IParameter;
import org.hdiv.state.IState;
import org.hdiv.state.StateUtil;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.util.HDIVUtil;

/**
 * It validates client requests by consuming an object of type IState and validating all the entry data, besides
 * replacing relative values by its real values.
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
	private static Log log = LogFactory.getLog(ValidatorHelperRequest.class);

	/**
	 * HDIV configuration object.
	 */
	private HDIVConfig hdivConfig;

	/**
	 * Logger to print the possible attacks detected by HDIV.
	 */
	private Logger logger;

	/**
	 * Utility methods for state
	 */
	private StateUtil stateUtil;

	/**
	 * State that represents all the data of a request or a form existing in a page <code>page</code>
	 */
	private ISession session;

	/**
	 * IDataValidator factory
	 */
	private DataValidatorFactory dataValidatorFactory;

	/**
	 * {@link IDataComposer} factory
	 */
	private DataComposerFactory dataComposerFactory;

	/**
	 * Compiled numeric <code>Pattern</code>
	 */
	private Pattern numberPattern = Pattern.compile("[0-9]+");

	/**
	 * Initialization of the objects needed for the validation process.
	 * 
	 * @throws HDIVException
	 *             if there is an initialization error.
	 */
	public void init() {
	}

	/**
	 * Checks if the values of the parameters received in the request <code>request</code> are valid. These values are
	 * valid if and only if the noneditable parameters haven't been modified.<br>
	 * Validation process is as follows.<br>
	 * 1. If the action to which the request is directed is an init page, then it is a valid request.<br>
	 * 2. if the cookies received in the request are not found in the user session, the validation is incorrect.<br>
	 * 3. if the state recover process has produced an error, incorrect validation.<br>
	 * 4. If the action received in the request is different to the action of the recovered state, incorrect validation.<br>
	 * 5. If not, all the parameter values are checked and if all the received values are valid then the request is
	 * valid. <br>
	 * 5.1. If it is an init parameter or a HDIV parameter then it is a valid parameter.<br>
	 * 5.2. If the received parameter is not in the state:<br>
	 * 5.2.1. If it has been defined by the user as a no validation required parameter, then it is a valid parameter.<br>
	 * 5.2.2. otherwise, it is a no valid request.<br>
	 * 5.3. If the parameter is editable, if validations have been defined values are checked.<br>
	 * 5.4. If it is a non editable parameter, all the received values are checked.
	 * 
	 * @param request
	 *            HttpServletRequest to validate
	 * @return valid result If all the parameter values of the request <code>request</code> pass the the HDIV
	 *         validation. False, otherwise.
	 * @throws HDIVException
	 *             If the request doesn't pass the HDIV validation an exception is thrown explaining the cause of the
	 *             error.
	 */
	public ValidatorHelperResult validate(HttpServletRequest request) {

		String target = this.getTarget(request);
		String targetWithoutContextPath = this.getTargetWithoutContextPath(request, target);

		// Hook before the validation
		ValidatorHelperResult result = this.preValidate(request, target);
		if (result != null) {
			return result;
		}

		if (this.hdivConfig.hasExtensionToExclude(target)) {
			if (log.isDebugEnabled()) {
				log.debug("The target [" + target + "] has an extension to exclude from validation");
			}
			return ValidatorHelperResult.VALIDATION_NOT_REQUIRED;
		}

		if (!this.hdivConfig.isValidationInUrlsWithoutParamsActivated()) {

			boolean requestHasParameters = (request.getParameterNames() != null)
					&& (request.getParameterNames().hasMoreElements());
			if (!requestHasParameters) {
				if (log.isDebugEnabled()) {
					log.debug("The url [" + request.getRequestURI()
							+ "] is not be validated because it has not got parameters");
				}
				return ValidatorHelperResult.VALIDATION_NOT_REQUIRED;
			}
		}

		if (this.hdivConfig.isStartPage(targetWithoutContextPath, request.getMethod())) {
			result = this.validateStartPageParameters(request, target);
			if (result.isValid()) {
				return ValidatorHelperResult.VALIDATION_NOT_REQUIRED;
			} else {
				return result;
			}
		}

		if (this.hdivConfig.isCookiesIntegrityActivated()) {
			result = this.validateRequestCookies(request, target);
			if (!result.isValid()) {
				return result;
			}
		}

		// Restore state from request or from memory
		result = this.restoreState(request, target);
		if (!result.isValid()) {
			return result;
		}
		// Get resultant object, the stored state
		IState state = (IState) result.getValue();

		result = this.isTheSameAction(request, target, state);
		if (!result.isValid()) {
			return result;
		}

		result = this.allRequiredParametersReceived(request, state, target);
		if (!result.isValid()) {
			return result;
		}

		// Hdiv parameter name
		String hdivParameter = this.getHdivParameter(request);

		Map<String, String[]> unauthorizedEditableParameters = new HashMap<String, String[]>();
		Enumeration<?> parameters = request.getParameterNames();
		while (parameters.hasMoreElements()) {

			String parameter = (String) parameters.nextElement();

			// Validate parameter
			result = this.validateParameter(request, state, unauthorizedEditableParameters, hdivParameter, target,
					targetWithoutContextPath, parameter);
			if (!result.isValid()) {
				return result;
			}

		}

		if (unauthorizedEditableParameters.size() > 0) {

			return this.processValidateParameterErrors(request, unauthorizedEditableParameters);
		}

		return ValidatorHelperResult.VALID;
	}

	/**
	 * Checks if the action received in the request is the same as the one stored in the HDIV state.
	 * 
	 * @param request
	 *            HttpServletRequest to validate
	 * @param target
	 *            Part of the url that represents the target action
	 * @param state
	 *            The restored state for this url
	 * @return valid result if the actions are the same. False otherwise.
	 */
	protected ValidatorHelperResult isTheSameAction(HttpServletRequest request, String target, IState state) {

		if (state.getAction().equalsIgnoreCase(target)) {
			return ValidatorHelperResult.VALID;
		}

		if (target.endsWith("/")) {
			String actionSlash = state.getAction() + "/";
			if (actionSlash.equalsIgnoreCase(target)) {
				return ValidatorHelperResult.VALID;
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("target:" + target);
			log.debug("state action:" + state.getAction());
		}

		this.logger.log(HDIVErrorCodes.ACTION_ERROR, target, null, null);

		if (log.isDebugEnabled()) {
			log.debug("Detected validation error in the action: action in state:" + state.getAction()
					+ ", action in the request:" + target);
		}

		return new ValidatorHelperResult(HDIVErrorCodes.ACTION_ERROR);
	}

	/**
	 * It validates the parameters of an init page because our application can receive requests that require validation
	 * but don't have any HDIV state. So, despite being init pages, editable data validation must be done.
	 * 
	 * @param request
	 *            HttpServletRequest to validate
	 * @param target
	 *            Part of the url that represents the target action
	 * @return valid result if the values of the editable parameters pass the validations defined in hdiv-config.xml.
	 *         False otherwise.
	 * @since HDIV 1.1.2
	 */
	protected ValidatorHelperResult validateStartPageParameters(HttpServletRequest request, String target) {

		if (this.hdivConfig.existValidations()) {

			Map<String, String[]> unauthorizedEditableParameters = new HashMap<String, String[]>();

			Enumeration<?> parameters = request.getParameterNames();
			while (parameters.hasMoreElements()) {

				String parameter = (String) parameters.nextElement();
				String[] values = request.getParameterValues(parameter);

				this.validateEditableParameter(request, target, parameter, values, "text",
						unauthorizedEditableParameters);

			}

			if (unauthorizedEditableParameters.size() > 0) {
				return this.processValidateParameterErrors(request, unauthorizedEditableParameters);
			}
		}
		return ValidatorHelperResult.VALID;
	}

	/**
	 * Called if there are editable validation errors. Process these errors.
	 * 
	 * @param request
	 *            HttpServletRequest instance
	 * @param unauthorizedEditableParameters
	 *            Request parameters with errors
	 * @return continue with the request processing if valid value
	 * @since 2.1.4
	 */
	protected ValidatorHelperResult processValidateParameterErrors(HttpServletRequest request,
			Map<String, String[]> unauthorizedEditableParameters) {

		if (!this.hdivConfig.isDebugMode()) {
			// Put the errors on request to be accessible from the Web framework
			request.setAttribute(Constants.EDITABLE_PARAMETER_ERROR, unauthorizedEditableParameters);

			if (this.hdivConfig.isShowErrorPageOnEditableValidation()) {
				// Redirect to error page
				// Put errors in session to be accessible from error page
				request.getSession().setAttribute(Constants.EDITABLE_PARAMETER_ERROR, unauthorizedEditableParameters);
				return new ValidatorHelperResult(HDIVErrorCodes.EDITABLE_VALIDATION_ERROR);
			}
		}
		return ValidatorHelperResult.VALID;
	}

	/**
	 * Checks if the cookies received in the request are correct. For that, it checks if they are in the user session.
	 * 
	 * @param request
	 *            HttpServletRequest to validate
	 * @param target
	 *            Part of the url that represents the target action
	 * @return valid result if all the cookies received in the request are correct. They must have been previously
	 *         stored in the user session by HDIV to be correct. False otherwise.
	 * @since HDIV 1.1
	 */
	protected ValidatorHelperResult validateRequestCookies(HttpServletRequest request, String target) {

		Cookie[] requestCookies = request.getCookies();

		if ((requestCookies == null) || (requestCookies.length == 0)) {
			return ValidatorHelperResult.VALID;
		}

		Map<String, SavedCookie> sessionCookies = (Map<String, SavedCookie>) request.getSession().getAttribute(
				Constants.HDIV_COOKIES_KEY);

		if (sessionCookies == null) {
			return ValidatorHelperResult.VALID;
		}

		boolean cookiesConfidentiality = this.hdivConfig.getConfidentiality()
				&& this.hdivConfig.isCookiesConfidentialityActivated();

		for (int i = 0; i < requestCookies.length; i++) {

			boolean found = false;
			if (requestCookies[i].getName().equals(Constants.JSESSIONID)) {
				continue;
			}

			if (sessionCookies.containsKey(requestCookies[i].getName())) {

				SavedCookie savedCookie = sessionCookies.get(requestCookies[i].getName());
				if (savedCookie.equals(requestCookies[i], cookiesConfidentiality)) {

					found = true;
					if (cookiesConfidentiality) {
						if (savedCookie.getValue() != null) {
							requestCookies[i].setValue(savedCookie.getValue());
						}
					}
				}
			}

			if (!found) {
				this.logger.log(HDIVErrorCodes.COOKIE_INCORRECT, target, "cookie:" + requestCookies[i].getName(),
						requestCookies[i].getValue());
				return new ValidatorHelperResult(HDIVErrorCodes.COOKIE_INCORRECT);
			}
		}
		return ValidatorHelperResult.VALID;
	}

	/**
	 * Checks if the values <code>values</code> are valid for the editable parameter <code>parameter</code>. This
	 * validation is defined by the user in the hdiv-validations.xml file of Spring. If the values are not valid, an
	 * error message with the parameter and the received values will be log.
	 * 
	 * @param request
	 *            HttpServletRequest to validate
	 * @param target
	 *            Part of the url that represents the target action
	 * @param parameter
	 *            parameter name
	 * @param values
	 *            parameter's values
	 * @param dataType
	 *            editable data type
	 * @param unauthorizedParameters
	 *            Unauthorized editable parameters
	 * @since HDIV 1.1
	 */
	protected void validateEditableParameter(HttpServletRequest request, String target, String parameter,
			String[] values, String dataType, Map<String, String[]> unauthorizedParameters) {

		String targetWithoutContextPath = this.getTargetWithoutContextPath(request, target);

		boolean isValid = hdivConfig.areEditableParameterValuesValid(targetWithoutContextPath, parameter, values,
				dataType);
		if (!isValid) {

			StringBuffer unauthorizedValues = new StringBuffer(values[0]);

			for (int i = 1; i < values.length; i++) {
				unauthorizedValues.append("," + values[i]);
			}

			if (dataType.equals("password")) {
				String[] passwordError = { Constants.HDIV_EDITABLE_PASSWORD_ERROR_KEY };
				unauthorizedParameters.put(parameter, passwordError);
			} else {
				unauthorizedParameters.put(parameter, values);
			}

			this.logger.log(HDIVErrorCodes.EDITABLE_VALIDATION_ERROR, target, parameter, unauthorizedValues.toString());
		}
	}

	/**
	 * Check if all required parameters are received in <code>request</code>.
	 * 
	 * @param request
	 *            HttpServletRequest to validate
	 * @param state
	 *            IState The restored state for this url
	 * @param target
	 *            Part of the url that represents the target action
	 * @return valid result if all required parameters are received. False in otherwise.
	 */
	private ValidatorHelperResult allRequiredParametersReceived(HttpServletRequest request, IState state, String target) {

		Map<String, IParameter> receivedParameters = new HashMap<String, IParameter>(state.getRequiredParams());

		String currentParameter = null;
		Enumeration<?> requestParameters = request.getParameterNames();
		while (requestParameters.hasMoreElements()) {

			currentParameter = (String) requestParameters.nextElement();
			if (receivedParameters.containsKey(currentParameter)) {
				receivedParameters.remove(currentParameter);
			}

			// If multiple parameters are received, it is possible to pass this
			// verification without checking all the request parameters.
			if (receivedParameters.size() == 0) {
				return ValidatorHelperResult.VALID;
			}
		}

		if (receivedParameters.size() > 0) {
			this.logger.log(HDIVErrorCodes.REQUIRED_PARAMETERS, target, receivedParameters.keySet().toString(), null);
			return new ValidatorHelperResult(HDIVErrorCodes.REQUIRED_PARAMETERS);
		}

		return ValidatorHelperResult.VALID;
	}

	/**
	 * 
	 * Validate single parameter values.
	 * 
	 * @param request
	 *            HttpServletRequest to validate
	 * @param state
	 *            IState The restored state for this url
	 * @param unauthorizedEditableParameters
	 *            Editable parameters with errors
	 * @param hdivParameter
	 *            Hdiv state parameter name
	 * @param target
	 *            Part of the url that represents the target action
	 * @param targetWithoutContextPath
	 *            target with the ContextPath stripped
	 * @param parameter
	 *            Parameter name to validate
	 * @return Valid if parameter has not errors
	 * @since HDIV 2.1.5
	 */
	protected ValidatorHelperResult validateParameter(HttpServletRequest request, IState state,
			Map<String, String[]> unauthorizedEditableParameters, String hdivParameter, String target,
			String targetWithoutContextPath, String parameter) {

		// Check if the HDIV validation must be applied to the parameter
		if (!this.hdivConfig.needValidation(parameter, hdivParameter)) {

			if (log.isDebugEnabled() && !parameter.equals(hdivParameter)) {
				log.debug("parameter " + parameter + " doesn't need validation");
			}
			return ValidatorHelperResult.VALIDATION_NOT_REQUIRED;
		}

		// If the parameter requires no validation it is considered a valid parameter
		if (this.isUserDefinedNonValidationParameter(targetWithoutContextPath, parameter)) {
			return ValidatorHelperResult.VALIDATION_NOT_REQUIRED;
		}

		IParameter stateParameter = state.getParameter(parameter);
		if (stateParameter == null) {

			// If the parameter is not defined in the state, it is an error.
			// With this verification we guarantee that no extra parameters are added.
			this.logger.log(HDIVErrorCodes.PARAMETER_NOT_EXISTS, target, parameter, null);

			if (log.isDebugEnabled()) {
				log.debug("Validation Error Detected: Parameter [" + parameter
						+ "] does not exist in the state for action [" + target + "]");
			}

			return new ValidatorHelperResult(HDIVErrorCodes.PARAMETER_NOT_EXISTS);
		}

		// At this point we are processing a noneditable parameter
		String[] values = request.getParameterValues(parameter);

		// Check if the parameter is editable
		if (stateParameter.isEditable()) {

			if (this.hdivConfig.existValidations() && (stateParameter.getEditableDataType() != null)) {
				this.validateEditableParameter(request, target, parameter, values,
						stateParameter.getEditableDataType(), unauthorizedEditableParameters);
			}
			return ValidatorHelperResult.VALID;
		}

		try {
			ValidatorHelperResult result = this.validateParameterValues(request, target, state, stateParameter,
					parameter, values);
			return result;
		} catch (HDIVException e) {
			String errorMessage = HDIVUtil.getMessage("validation.error", e.getMessage());
			throw new HDIVException(errorMessage, e);
		}
	}

	/**
	 * Checks if the parameter <code>parameter</code> is defined by the user as a no required validation parameter for
	 * the action <code>this.target</code>.
	 * 
	 * @param targetWithoutContextPath
	 *            target with the ContextPath stripped
	 * @param parameter
	 *            parameter name
	 * @return True If it is parameter that needs no validation. False otherwise.
	 */
	private boolean isUserDefinedNonValidationParameter(String targetWithoutContextPath, String parameter) {

		if (this.hdivConfig.isParameterWithoutValidation(targetWithoutContextPath, parameter)) {

			if (log.isDebugEnabled()) {
				log.debug("parameter " + parameter + " doesn't need validation. It is user defined parameter.");
			}
			return true;
		}
		return false;
	}

	/**
	 * Restore state from session or <code>request</code> with <code>request</code> identifier. Strategy defined by the
	 * user determines the way the state is restored.
	 * 
	 * @param request
	 *            HTTP request
	 * @param target
	 *            Part of the url that represents the target action
	 * @return valid result if restored state is valid. False in otherwise.
	 */
	private ValidatorHelperResult restoreState(HttpServletRequest request, String target) {

		// Hdiv parameter name
		String hdivParameter = getHdivParameter(request);

		// checks if the parameter HDIV parameter exists in the parameters of
		// the request
		String requestState = request.getParameter(hdivParameter);

		if (requestState == null) {
			this.logger.log(HDIVErrorCodes.HDIV_PARAMETER_NOT_EXISTS, target, hdivParameter, null);
			return new ValidatorHelperResult(HDIVErrorCodes.HDIV_PARAMETER_NOT_EXISTS);
		}

		try {
			if (this.stateUtil.isMemoryStrategy(requestState)) {

				if (!this.validateHDIVSuffix(requestState)) {
					this.logger.log(HDIVErrorCodes.HDIV_PARAMETER_INCORRECT_VALUE, target, hdivParameter, requestState);
					return new ValidatorHelperResult(HDIVErrorCodes.HDIV_PARAMETER_INCORRECT_VALUE);
				}
			}

			IState state = this.stateUtil.restoreState(requestState);

			// return validation OK and resultant state
			return new ValidatorHelperResult(true, state);

		} catch (HDIVException e) {

			if (!this.hdivConfig.getStrategy().equalsIgnoreCase("memory")) {
				requestState = null;
			}

			// HDIVException message contains error code
			this.logger.log(e.getMessage(), target, hdivParameter, requestState);
			return new ValidatorHelperResult(e.getMessage());
		}
	}

	/**
	 * Checks if the suffix added in the memory version to all requests in the HDIV parameter is the same as the one
	 * stored in session, which is the original suffix. So any request using the memory version should keep the suffix
	 * unchanged.
	 * 
	 * @param value
	 *            value received in the HDIV parameter
	 * @return True if the received value of the suffix is valid. False otherwise.
	 */
	protected boolean validateHDIVSuffix(String value) {

		int firstSeparator = value.indexOf("-");
		int lastSeparator = value.lastIndexOf("-");

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
			String pageId = value.substring(0, firstSeparator);
			IPage currentPage = this.session.getPage(pageId);

			if (currentPage == null) {
				if (log.isErrorEnabled()) {
					log.error("Page with id [" + pageId + "] not found in session.");
				}
				throw new HDIVException(HDIVErrorCodes.PAGE_ID_INCORRECT);
			}

			return currentPage.getRandomToken().equals(requestSuffix);

		} catch (IndexOutOfBoundsException e) {
			String errorMessage = HDIVUtil.getMessage("validation.error", e.getMessage());
			if (log.isErrorEnabled()) {
				log.error(errorMessage);
			}
			throw new HDIVException(errorMessage, e);
		}
	}

	/**
	 * Checks if all the received parameter <code>parameter</code> values are valid, that is, are expected values.
	 * Received value number is checked and then these values are validated.
	 * 
	 * @param request
	 *            HttpServletRequest to validate
	 * @param target
	 *            Part of the url that represents the target action
	 * @param state
	 *            IState The restored state for this url
	 * @param stateParameter
	 *            parameter stored in state
	 * @param parameter
	 *            Parameter to validate
	 * @param values
	 *            parameter <code>parameter</code> values
	 * @return valid result if the validation is correct. False otherwise.
	 * @throws HDIVException
	 *             if there is an error in parameter validation process.
	 */
	private ValidatorHelperResult validateParameterValues(HttpServletRequest request, String target, IState state,
			IParameter stateParameter, String parameter, String[] values) {

		try {
			// Only for required parameters must be checked if the number of received
			// values is the same as number of values in the state. If this wasn't
			// taken into account, this verification will be done for every parameter,
			// including for example, a multiple combo where hardly ever are all its
			// values received.
			if (stateParameter.isActionParam()) {

				if (values.length != stateParameter.getValues().size()) {

					String valueMessage = (values.length > stateParameter.getValues().size()) ? "extra value"
							: "more values expected";
					this.logger.log(HDIVErrorCodes.VALUE_LENGTH_INCORRECT, target, parameter, valueMessage);
					return new ValidatorHelperResult(HDIVErrorCodes.VALUE_LENGTH_INCORRECT);
				}
			}

			ValidatorHelperResult result = this.hasRepeatedOrInvalidValues(target, parameter, values,
					stateParameter.getValues());
			if (!result.isValid()) {
				return result;
			}

			// At this point, we know that the number of received values is the same
			// as the number of values sent to the client. Now we have to check if
			// the received values are all tha ones stored in the state.
			return this.validateReceivedValuesInState(request, target, state, parameter, values);

		} catch (HDIVException e) {
			String errorMessage = HDIVUtil.getMessage("validation.error", e.getMessage());
			throw new HDIVException(errorMessage, e);
		}
	}

	/**
	 * Checks if repeated or no valid values have been received for the parameter <code>parameter</code>.
	 * 
	 * @param target
	 *            Part of the url that represents the target action
	 * @param parameter
	 *            parameter name
	 * @param values
	 *            Parameter <code>parameter</code> values
	 * @param stateValues
	 *            values stored in state for <code>parameter</code>
	 * @return True If repeated or no valid values have been received for the parameter <code>parameter</code>.
	 */
	private ValidatorHelperResult hasRepeatedOrInvalidValues(String target, String parameter, String[] values,
			List<String> stateValues) {

		List<String> tempStateValues = new ArrayList<String>();
		tempStateValues.addAll(stateValues);

		if (this.hdivConfig.getConfidentiality()) {
			return this.hasConfidentialIncorrectValues(target, parameter, values, tempStateValues);
		} else {
			return this.hasNonConfidentialIncorrectValues(target, parameter, values, tempStateValues);
		}
	}

	/**
	 * Checks if repeated values have been received for the parameter <code>parameter</code>.
	 * 
	 * @param target
	 *            Part of the url that represents the target action
	 * @param parameter
	 *            parameter name
	 * @param values
	 *            Parameter <code>parameter</code> values
	 * @param stateValues
	 *            real values for <code>parameter</code>
	 * @return True If repeated values have been received for the parameter <code>parameter</code>.
	 */
	private ValidatorHelperResult hasConfidentialIncorrectValues(String target, String parameter, String[] values,
			List<String> stateValues) {

		Set<String> receivedValues = new HashSet<String>();

		for (int i = 0; i < values.length; i++) {

			if (this.hdivConfig.isParameterWithoutConfidentiality(parameter)) {
				return ValidatorHelperResult.VALID;
			}

			if (!this.isInRange(target, parameter, values[i], stateValues)) {
				return new ValidatorHelperResult(HDIVErrorCodes.CONFIDENTIAL_VALUE_INCORRECT);
			}

			if (receivedValues.contains(values[i])) {
				this.logger.log(HDIVErrorCodes.REPEATED_VALUES, target, parameter, values[i]);
				return new ValidatorHelperResult(HDIVErrorCodes.REPEATED_VALUES);
			}

			receivedValues.add(values[i]);
		}
		return ValidatorHelperResult.VALID;
	}

	/**
	 * Checks if repeated or no valid values have been received for the parameter <code>parameter</code>.
	 * 
	 * @param target
	 *            Part of the url that represents the target action
	 * @param parameter
	 *            parameter name
	 * @param values
	 *            Parameter <code>parameter</code> values
	 * @param tempStateValues
	 *            values stored in state for <code>parameter</code>
	 * @return True If repeated or no valid values have been received for the parameter <code>parameter</code>.
	 */
	private ValidatorHelperResult hasNonConfidentialIncorrectValues(String target, String parameter, String[] values,
			List<String> tempStateValues) {

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

				if (receivedValues.contains(values[i])) {
					this.logger.log(HDIVErrorCodes.REPEATED_VALUES, target, parameter, values[i]);
					return new ValidatorHelperResult(HDIVErrorCodes.REPEATED_VALUES);
				}
				this.logger.log(HDIVErrorCodes.PARAMETER_VALUE_INCORRECT, target, parameter, values[i]);
				return new ValidatorHelperResult(HDIVErrorCodes.PARAMETER_VALUE_INCORRECT);
			}

			receivedValues.add(values[i]);
		}
		return ValidatorHelperResult.VALID;
	}

	/**
	 * Checks if the confidential value received in <code>value</code> is a value lower than the number or values
	 * received for the parameter <code>parameter</code>.
	 * 
	 * @param target
	 *            Part of the url that represents the target action
	 * @param parameter
	 *            parameter
	 * @param value
	 *            value
	 * @param stateValues
	 *            real values for <code>parameter</code>
	 * @return True if <code>value</code> is correct. False otherwise.
	 * @since HDIV 2.0
	 */
	private boolean isInRange(String target, String parameter, String value, List<String> stateValues) {

		Matcher m = this.numberPattern.matcher(value);

		if (!m.matches() || (Integer.valueOf(value).intValue() >= stateValues.size())) {
			String paramValue = (stateValues.size() > 1 ? stateValues : stateValues.get(0)) + "(" + value + ")";
			this.logger.log(HDIVErrorCodes.CONFIDENTIAL_VALUE_INCORRECT, target, parameter, paramValue);
			return false;
		}
		return true;
	}

	/**
	 * Checks that values <code>values</code> for the <code>parameter</code> are valid.
	 * 
	 * @param request
	 *            HttpServletRequest to validate
	 * @param target
	 *            Part of the url that represents the target action
	 * @param state
	 *            IState The restored state for this url
	 * @param parameter
	 *            Parameter to validate
	 * @param values
	 *            Parameter <code>parameter</code> values.
	 * @return True If the <code>values</code> validation is correct. False otherwise.
	 */
	private ValidatorHelperResult validateReceivedValuesInState(HttpServletRequest request, String target,
			IState state, String parameter, String[] values) {

		int size = values.length;
		String[] originalValues = new String[size];

		IDataValidator dataValidator = this.dataValidatorFactory.newInstance(state);

		String targetWithoutContextPath = this.getTargetWithoutContextPath(request, target);

		IValidationResult result = null;
		for (int i = 0; i < size; i++) {

			result = dataValidator.validate(values[i], targetWithoutContextPath, parameter);

			if (!result.getLegal()) {
				this.logger.log(HDIVErrorCodes.PARAMETER_VALUE_INCORRECT, target, parameter, values[i]);
				return new ValidatorHelperResult(HDIVErrorCodes.PARAMETER_VALUE_INCORRECT);
			} else {
				originalValues[i] = (String) result.getResult();
			}
		}

		if (this.hdivConfig.getConfidentiality()) {
			this.addParameterToRequest(request, parameter, originalValues);
		}

		return ValidatorHelperResult.VALID;
	}

	/**
	 * Adds one parameter to the request. Since the HttpServletRequest object's parameters are unchanged according to
	 * the Servlet specification, the instance of request should be passed as a parameter of type RequestWrapper.
	 * 
	 * @param request
	 *            HttpServletRequest to validate
	 * @param name
	 *            new parameter name
	 * @param value
	 *            new parameter value
	 * @throws HDIVException
	 *             if the request object is not of type RequestWrapper
	 */
	protected void addParameterToRequest(HttpServletRequest request, String name, Object value) {

		RequestWrapper wrapper = null;

		if (request instanceof RequestWrapper) {
			wrapper = (RequestWrapper) request;
		} else {
			wrapper = (RequestWrapper) this.getNativeRequest(request, RequestWrapper.class);
		}

		if (wrapper != null) {
			wrapper.addParameter(name, value);
		} else {
			String errorMessage = HDIVUtil.getMessage("helper.notwrapper");
			throw new HDIVException(errorMessage);
		}

	}

	protected ServletRequest getNativeRequest(ServletRequest request, Class<?> requiredType) {
		if (requiredType != null) {
			if (requiredType.isInstance(request)) {
				return request;
			} else if (request instanceof ServletRequestWrapper) {
				return getNativeRequest(((ServletRequestWrapper) request).getRequest(), requiredType);
			}
		}
		return null;
	}

	/**
	 * Gets the part of the url that represents the action to be executed in this request.
	 * 
	 * @param request
	 *            HttpServletRequest to validate
	 * @return target Part of the url that represents the target action
	 * @throws HDIVException
	 */
	protected String getTarget(HttpServletRequest request) {
		try {
			String requestUri = request.getRequestURI();
			requestUri = HDIVUtil.stripSession(requestUri);
			return requestUri;
		} catch (Exception e) {
			String errorMessage = HDIVUtil.getMessage("helper.actionName");
			throw new HDIVException(errorMessage, e);
		}
	}

	/**
	 * Removes the target's ContextPath part
	 * 
	 * @param request
	 *            HttpServletRequest to validate
	 * @param target
	 *            target to strip the ContextPath
	 * @return target without the ContextPath
	 */
	protected String getTargetWithoutContextPath(HttpServletRequest request, String target) {
		String targetWithoutContextPath = target.substring(request.getContextPath().length());
		return targetWithoutContextPath;
	}

	/**
	 * Name of the parameter that HDIV will include in the requests or/and forms which contains the state identifier in
	 * the memory strategy or the state itself in the Encoded or Hash strategies.
	 * 
	 * @param request
	 *            request
	 * @return hdiv parameter value
	 */
	protected String getHdivParameter(HttpServletRequest request) {

		return (String) request.getSession().getAttribute(Constants.HDIV_PARAMETER);
	}

	/**
	 * <p>
	 * Method invoked before validation. Designed to change the validation logic beyond the base implementation.
	 * </p>
	 * <p>
	 * The response of the method can have three meanings:
	 * <ul>
	 * <li>Valid ValidatorHelperResult: The validation has been completed correctly and is not necessary to run the
	 * entire validation process.</li>
	 * <li>Error ValidatorHelperResult: The validation has encountered an error and terminates the validation process.</li>
	 * <li>null: It should continue with the validation process (default answer).</li>
	 * </ul>
	 * </p>
	 * 
	 * @param request
	 *            HttpServletRequest to validate
	 * @return ValidatorHelperResult result
	 */
	protected ValidatorHelperResult preValidate(HttpServletRequest request, String target) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.filter.IValidationHelper#startPage(javax.servlet.http. HttpServletRequest)
	 */
	public void startPage(HttpServletRequest request) {

		// Don`t create IDataComposer if it is not necessary
		boolean exclude = this.hdivConfig.hasExtensionToExclude(request.getRequestURI());
		if (!exclude) {

			// Init datacomposer
			IDataComposer dataComposer = this.dataComposerFactory.newInstance(request);

			HDIVUtil.setDataComposer(dataComposer, request);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.filter.IValidationHelper#endPage(javax.servlet.http. HttpServletRequest)
	 */
	public void endPage(HttpServletRequest request) {

		// End page in datacomposer
		boolean exist = HDIVUtil.isDataComposer(request);
		if (exist) {
			IDataComposer dataComposer = HDIVUtil.getDataComposer(request);
			dataComposer.endPage();
		}

	}

	/**
	 * @param logger
	 *            The user logger to set.
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	/**
	 * @param stateUtil
	 *            The state utility to set.
	 */
	public void setStateUtil(StateUtil stateUtil) {
		this.stateUtil = stateUtil;
	}

	/**
	 * @param hdivConfig
	 *            The HDIV configuration object to set.
	 */
	public void setHdivConfig(HDIVConfig hdivConfig) {
		this.hdivConfig = hdivConfig;
	}

	/**
	 * @param session
	 *            the session to set
	 */
	public void setSession(ISession session) {
		this.session = session;
	}

	/**
	 * @param dataValidatorFactory
	 *            the dataValidatorFactory to set
	 */
	public void setDataValidatorFactory(DataValidatorFactory dataValidatorFactory) {
		this.dataValidatorFactory = dataValidatorFactory;
	}

	/**
	 * @param numberPattern
	 *            the numberPattern to set
	 */
	public void setNumberPattern(Pattern numberPattern) {
		this.numberPattern = numberPattern;
	}

	/**
	 * @param dataComposerFactory
	 *            the dataComposerFactory to set
	 */
	public void setDataComposerFactory(DataComposerFactory dataComposerFactory) {
		this.dataComposerFactory = dataComposerFactory;
	}

	/**
	 * @return the hdivConfig
	 */
	protected HDIVConfig getHdivConfig() {
		return hdivConfig;
	}

	/**
	 * @return the logger
	 */
	protected Logger getLogger() {
		return logger;
	}

}