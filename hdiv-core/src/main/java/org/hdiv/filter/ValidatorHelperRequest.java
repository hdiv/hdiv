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
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.HDIVConfig;
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
 * It validates client requests by comsuming an object of type IState and
 * validating all the entry data, besides replacing relative values by its real
 * values.
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
	 * State that represents all the data of a request or a form existing in a
	 * page <code>page</code>
	 */
	private ISession session;

	/**
	 * IDataValidator factory
	 */
	private DataValidatorFactory dataValidatorFactory;

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
	 * Checks if the values of the parameters received in the request
	 * <code>request</code> are valid. These values are valid if and only if the
	 * noneditable parameters haven't been modified.<br>
	 * Validation process is as follows.<br>
	 * 1. If the action to which the request is directed is an init page, then
	 * it is a valid request.<br>
	 * 2. if the cookies received in the request are not found in the user
	 * session, the validation is incorrect.<br>
	 * 3. if the state recover process has produced an error, incorrect
	 * validation.<br>
	 * 4. If the action received in the request is different to the action of
	 * the recovered state, incorrect validation.<br>
	 * 5. If not, all the parameter values are checked and if all the received
	 * values are valid then the request is valid. <br>
	 * 5.1. If it is an init parameter or a HDIV parameter then it is a valid
	 * parameter.<br>
	 * 5.2. If the received parameter is not in the state:<br>
	 * 5.2.1. If it has been defined by the user as a no validation required
	 * parameter, then it is a valid parameter.<br>
	 * 5.2.2. otherwise, it is a no valid request.<br>
	 * 5.3. If the parameter is editable, if validations have been defined
	 * values are checked.<br>
	 * 5.4. If it is a noneditable parameter, all the received values are
	 * checked.
	 * 
	 * @param request
	 *            HttpServletRequest to validate
	 * @return True If all the parameter values of the request
	 *         <code>request</code> pass the the HDIV validation. False,
	 *         otherwise.
	 * @throws HDIVException
	 *             If the request doesn't pass the HDIV validation an exception
	 *             is thrown explaining the cause of the error.
	 */
	public boolean validate(HttpServletRequest request) {

		String target = this.getTarget(request);
		String targetWithoutContextPath = this.getTargetWithoutContextPath(request, target);

		// Hook before the validation
		Boolean pre = this.preValidate(request, target);
		if (pre != null) {
			return pre.booleanValue();
		}

		if (this.hdivConfig.hasExtensionToExclude(target)) {
			log.debug("The target " + target + " has an extension to exclude from validation");
			return true;
		}

		if (!this.hdivConfig.isValidationInUrlsWithoutParamsActivated()) {

			boolean requestHasParameters = (request.getParameterNames() != null)
					&& (request.getParameterNames().hasMoreElements());
			if (!requestHasParameters) {
				log.debug("The url " + request.getRequestURI()
						+ " is not be validated because it has not got parameters");
				return true;
			}
		}

		if (this.hdivConfig.isStartPage(targetWithoutContextPath)) {
			return (this.validateStartPageParameters(request, target));
		}

		if (this.hdivConfig.isCookiesIntegrityActivated()) {
			if (!this.validateRequestCookies(request, target)) {
				return false;
			}
		}

		// restore state from request or from memory
		IState state = this.restoreState(request, target);
		if (state == null) {
			return false;
		}

		if (!this.isTheSameAction(request, target, state)) {
			return false;
		}

		if (!this.allRequiredParametersReceived(request, state, target)) {
			return false;
		}

		// Hdiv parameter name
		String hdivParameter = getHdivParameter(request);

		Hashtable unauthorizedEditableParameters = new Hashtable();
		Enumeration parameters = request.getParameterNames();
		while (parameters.hasMoreElements()) {

			String parameter = (String) parameters.nextElement();

			// check if the HDIV validation must be applied to the parameter
			if (!this.hdivConfig.needValidation(parameter, hdivParameter)) {

				if (log.isDebugEnabled() && !parameter.equals(hdivParameter)) {
					log.debug("parameter " + parameter + " doesn't need validation");
				}
				continue;
			}

			// if the parameter requires no validation it is considered a
			// valid parameter
			if (this.isUserDefinedNonValidationParameter(targetWithoutContextPath, parameter)) {
				continue;
			}

			IParameter stateParameter = state.getParameter(parameter);
			if (stateParameter == null) {

				// If the parameter is not defined in the state, it is an error.
				// With this verification we guarantee that no extra parameters
				// are
				// added.
				this.logger.log(HDIVErrorCodes.PARAMETER_NOT_EXISTS, target, parameter, null);

				if (log.isDebugEnabled()) {
					log.debug("Validation Error Detected: Parameter [" + parameter
							+ "] does not exist in the state for action [" + target + "]");
				}

				return false;
			}

			// At this point we are processing a noneditable parameter
			String[] values = request.getParameterValues(parameter);

			// check if the parameter is editable
			if (stateParameter.isEditable()) {

				if (hdivConfig.existValidations() && (stateParameter.getEditableDataType() != null)) {
					this.validateEditableParameter(request, target, parameter, values,
							stateParameter.getEditableDataType(), unauthorizedEditableParameters);
				}
				continue;
			}

			try {
				if (!this.validateParameterValues(request, target, state, stateParameter, parameter, values)) {
					return false;
				}
			} catch (Exception e) {
				String errorMessage = HDIVUtil.getMessage("validation.error", e.getMessage());
				throw new HDIVException(errorMessage, e);
			}
		}

		if (unauthorizedEditableParameters.size() > 0) {
			if (!this.hdivConfig.isDebugMode()) {
				request.setAttribute(HDIVErrorCodes.EDITABLE_PARAMETER_ERROR, unauthorizedEditableParameters);
			}
		}

		return true;
	}

	/**
	 * Checks if the action received in the request is the same as the one
	 * stored in the HDIV state.
	 * 
	 * @param request
	 *            HttpServletRequest to validate
	 * @param target
	 *            Part of the url that represents the target action
	 * @param state
	 *            The restored state for this url
	 * @return True if the actions are the same. False otherwise.
	 */
	public boolean isTheSameAction(HttpServletRequest request, String target, IState state) {

		if (state.getAction().equalsIgnoreCase(target)) {
			return true;
		}

		if (target.endsWith("/")) {
			String actionSlash = state.getAction() + "/";
			if (actionSlash.equalsIgnoreCase(target)) {
				return true;
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

		return false;
	}

	/**
	 * It validates the parameters of an init page because our application can
	 * receive requests that require validation but don't have any HDIV state.
	 * So, despite being init pages, editable data validation must be done.
	 * 
	 * @param request
	 *            HttpServletRequest to validate
	 * @param target
	 *            Part of the url that represents the target action
	 * @return True if the values of the editable parameters pass the
	 *         validations defined in hdiv-config.xml. False otherwise.
	 * @since HDIV 1.1.2
	 */
	public boolean validateStartPageParameters(HttpServletRequest request, String target) {

		if (hdivConfig.existValidations()) {

			Hashtable unauthorizedEditableParameters = new Hashtable();

			Enumeration parameters = request.getParameterNames();
			while (parameters.hasMoreElements()) {

				String parameter = (String) parameters.nextElement();
				String[] values = request.getParameterValues(parameter);

				this.validateEditableParameter(request, target, parameter, values, "text",
						unauthorizedEditableParameters);

			}

			if (unauthorizedEditableParameters.size() > 0) {
				if (!this.hdivConfig.isDebugMode()) {
					request.setAttribute(HDIVErrorCodes.EDITABLE_PARAMETER_ERROR, unauthorizedEditableParameters);
				}
			}
		}
		return true;
	}

	/**
	 * Checks if the cookies received in the request are correct. For that, it
	 * checks if they are in the user session.
	 * 
	 * @param request
	 *            HttpServletRequest to validate
	 * @param target
	 *            Part of the url that represents the target action
	 * @return True if all the cookies received in the request are correct. They
	 *         must have been previously stored in the user session by HDIV to
	 *         be correct. False otherwise.
	 * @since HDIV 1.1
	 */
	public boolean validateRequestCookies(HttpServletRequest request, String target) {

		Cookie[] requestCookies = request.getCookies();

		if ((requestCookies == null) || (requestCookies.length == 0)) {
			return true;
		}

		Hashtable sessionCookies = (Hashtable) request.getSession().getAttribute(Constants.HDIV_COOKIES_KEY);

		if (sessionCookies == null) {
			return true;
		}

		boolean cookiesConfidentiality = Boolean.TRUE.equals(this.hdivConfig.getConfidentiality())
				&& this.hdivConfig.isCookiesConfidentialityActivated();

		for (int i = 0; i < requestCookies.length; i++) {

			boolean found = false;
			if (requestCookies[i].getName().equals(Constants.JSESSIONID)) {
				continue;
			}

			if (sessionCookies.containsKey(requestCookies[i].getName())) {

				SavedCookie savedCookie = (SavedCookie) sessionCookies.get(requestCookies[i].getName());
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
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the values <code>values</code> are valid for the editable
	 * parameter <code>parameter</code>. This validation is defined by the user
	 * in the hdiv-validations.xml file of Spring. If the values are not valid,
	 * an error message with the parameter and the received values will be log.
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
	public void validateEditableParameter(HttpServletRequest request, String target, String parameter, String[] values,
			String dataType, Hashtable unauthorizedParameters) {

		String targetWithoutContextPath = this.getTargetWithoutContextPath(request, target);

		boolean isValid = hdivConfig.areEditableParameterValuesValid(targetWithoutContextPath, parameter, values,
				dataType);
		if (!isValid) {

			StringBuffer unauthorizedValues = new StringBuffer(values[0]);

			for (int i = 1; i < values.length; i++) {
				unauthorizedValues.append("," + values[i]);
			}

			if (dataType.equals("password")) {
				String[] passwordError = { "hdiv.editable.password.error" };
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
	 * @return True if all required parameters are received. False in otherwise.
	 */
	private boolean allRequiredParametersReceived(HttpServletRequest request, IState state, String target) {

		Hashtable receivedParameters = new Hashtable(state.getRequiredParams());

		String currentParameter = null;
		Enumeration requestParameters = request.getParameterNames();
		while (requestParameters.hasMoreElements()) {

			currentParameter = (String) requestParameters.nextElement();
			if (receivedParameters.containsKey(currentParameter)) {
				receivedParameters.remove(currentParameter);
			}

			// If multiple parameters are received, it is possible to pass this
			// verification without checking all the request parameters.
			if (receivedParameters.size() == 0) {
				return true;
			}
		}

		if (receivedParameters.size() > 0) {
			this.logger.log(HDIVErrorCodes.REQUIRED_PARAMETERS, target, receivedParameters.keySet().toString(), null);
			return false;
		}

		return true;
	}

	/**
	 * Checks if the parameter <code>parameter</code> is defined by the user as
	 * a no required validation parameter for the action
	 * <code>this.target</code>.
	 * 
	 * @param targetWithoutContextPath
	 *            target with the ContextPath stripped
	 * @param parameter
	 *            parameter name
	 * @return True If it is parameter that needs no validation. False
	 *         otherwise.
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
	 * Restore state from session or <code>request</code> with
	 * <code>request</code> identifier. Strategy defined by the user determines
	 * the way the state is restored.
	 * 
	 * @param request
	 *            HTTP request
	 * @param target
	 *            Part of the url that represents the target action
	 * @return True if restored state is valid. False in otherwise.
	 * @throws HDIVException
	 *             if there is an error restoring state from request or session.
	 */
	private IState restoreState(HttpServletRequest request, String target) {

		// Hdiv parameter name
		String hdivParameter = getHdivParameter(request);

		// checks if the parameter HDIV parameter exists in the parameters of
		// the request
		String requestState = request.getParameter(hdivParameter);

		if (requestState == null) {
			this.logger.log(HDIVErrorCodes.HDIV_PARAMETER_NOT_EXISTS, target, hdivParameter, null);
			return null;
		}

		try {
			if (stateUtil.isMemoryStrategy(requestState)) {

				if (!this.validateHDIVSuffix(requestState)) {
					this.logger.log(HDIVErrorCodes.HDIV_PARAMETER_INCORRECT_VALUE, target, hdivParameter, requestState);
					return null;
				}
			}

			IState state = stateUtil.restoreState(requestState);

			return state;

		} catch (HDIVException e) {

			if (!this.hdivConfig.getStrategy().equalsIgnoreCase("memory")) {
				requestState = null;
			}

			this.logger.log(e.getMessage(), target, hdivParameter, requestState);
			return null;
		}
	}

	/**
	 * Checks if the suffix added in the memory version to all requests in the
	 * HDIV parameter is the same as the one stored in session, which is the
	 * original suffix. So any request using the memory version should keep the
	 * suffix unchanged.
	 * 
	 * @param value
	 *            value received in the HDIV parameter
	 * @return True if the received value of the suffix is valid. False
	 *         otherwise.
	 */
	public boolean validateHDIVSuffix(String value) {

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
				String errorMessage = HDIVUtil.getMessage("helper.nopageinsession", pageId);
				throw new HDIVException(errorMessage);
			}

			return currentPage.getRandomToken().equals(requestSuffix);

		} catch (Exception e) {
			String errorMessage = HDIVUtil.getMessage("validation.error", e.getMessage());
			throw new HDIVException(errorMessage, e);
		}
	}

	/**
	 * Checks if all the received parameter <code>parameter</code> values are
	 * valid, that is, are expected values. Received value number is checked and
	 * then these values are validated.
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
	 * @return True if the validation is correct. False otherwise.
	 * @throws HDIVException
	 *             if there is an error in parameter validation process.
	 */
	private boolean validateParameterValues(HttpServletRequest request, String target, IState state,
			IParameter stateParameter, String parameter, String[] values) {

		try {
			// Only for required parameters must be checked if the number of
			// received
			// values is the same as number of values in the state. If this
			// wasn't
			// taken into account, this verification will be done for every
			// parameter,
			// including for example, a multiple combo where hardly ever are all
			// its
			// values received.
			if (stateParameter.isActionParam()) {

				if (values.length != stateParameter.getValues().size()) {

					String valueMessage = (values.length > stateParameter.getValues().size()) ? "extra value"
							: "more values expected";
					this.logger.log(HDIVErrorCodes.VALUE_LENGTH_INCORRECT, target, parameter, valueMessage);
					return false;
				}
			}

			if (this.hasRepeatedOrInvalidValues(target, parameter, values, stateParameter.getValues())) {
				return false;
			}

			// At this point, we know that the number of received values is the
			// same
			// as the number of values sent to the client. Now we have to check
			// if
			// the received values are all tha ones stored in the state.
			return this.validateReceivedValuesInState(request, target, state, parameter, values);

		} catch (Exception e) {
			String errorMessage = HDIVUtil.getMessage("validation.error", e.getMessage());
			throw new HDIVException(errorMessage, e);
		}
	}

	/**
	 * Checks if repeated or no valid values have been received for the
	 * parameter <code>parameter</code>.
	 * 
	 * @param target
	 *            Part of the url that represents the target action
	 * @param parameter
	 *            parameter name
	 * @param values
	 *            Parameter <code>parameter</code> values
	 * @param stateValues
	 *            values stored in state for <code>parameter</code>
	 * @return True If repeated or no valid values have been received for the
	 *         parameter <code>parameter</code>.
	 */
	private boolean hasRepeatedOrInvalidValues(String target, String parameter, String[] values, List stateValues) {

		List tempStateValues = new ArrayList();
		tempStateValues.addAll(stateValues);

		if (Boolean.TRUE.equals(this.hdivConfig.getConfidentiality())) {
			return this.hasConfidentialIncorrectValues(target, parameter, values, stateValues.size());
		} else {
			return this.hasNonConfidentialIncorrectValues(target, parameter, values, tempStateValues);
		}
	}

	/**
	 * Checks if repeated values have been received for the parameter
	 * <code>parameter</code>.
	 * 
	 * @param target
	 *            Part of the url that represents the target action
	 * @param parameter
	 *            parameter name
	 * @param values
	 *            Parameter <code>parameter</code> values
	 * @param size
	 *            number of values received for <code>parameter</code>
	 * @return True If repeated values have been received for the parameter
	 *         <code>parameter</code>.
	 */
	private boolean hasConfidentialIncorrectValues(String target, String parameter, String[] values, int size) {

		Hashtable receivedValues = new Hashtable();

		for (int i = 0; i < values.length; i++) {

			if (!this.isInRange(target, parameter, values[i], size)) {
				return true;
			}

			if (receivedValues.containsKey(values[i])) {
				this.logger.log(HDIVErrorCodes.REPEATED_VALUES, target, parameter, values[i]);
				return true;
			}

			receivedValues.put(values[i], values[i]);
		}
		return false;
	}

	/**
	 * Checks if repeated or no valid values have been received for the
	 * parameter <code>parameter</code>.
	 * 
	 * @param target
	 *            Part of the url that represents the target action
	 * @param parameter
	 *            parameter name
	 * @param values
	 *            Parameter <code>parameter</code> values
	 * @param tempStateValues
	 *            values stored in state for <code>parameter</code>
	 * @return True If repeated or no valid values have been received for the
	 *         parameter <code>parameter</code>.
	 */
	private boolean hasNonConfidentialIncorrectValues(String target, String parameter, String[] values,
			List tempStateValues) {

		Hashtable receivedValues = new Hashtable();

		for (int i = 0; i < values.length; i++) {

			boolean exists = false;
			for (int j = 0; j < tempStateValues.size() && !exists; j++) {

				String tempValue = (String) tempStateValues.get(j);

				if (tempValue.equalsIgnoreCase(values[i])) {
					tempStateValues.remove(j);
					exists = true;
				}
			}

			if (!exists) {

				if (receivedValues.containsKey(values[i])) {
					this.logger.log(HDIVErrorCodes.REPEATED_VALUES, target, parameter, values[i]);
					return true;
				}
				this.logger.log(HDIVErrorCodes.PARAMETER_VALUE_INCORRECT, target, parameter, values[i]);
				return true;
			}

			receivedValues.put(values[i], values[i]);
		}
		return false;
	}

	/**
	 * Checks if the confidential value received in <code>value</code> is a
	 * value lower than the number or values received for the parameter
	 * <code>parameter</code>.
	 * 
	 * @param target
	 *            Part of the url that represents the target action
	 * @param parameter
	 *            parameter
	 * @param value
	 *            value
	 * @param valuesNumber
	 *            number of values received for <code>parameter</code>
	 * @return True if <code>value</code> is correct. False otherwise.
	 * @since HDIV 2.0
	 */
	private boolean isInRange(String target, String parameter, String value, int valuesNumber) {

		// Pattern p = Pattern.compile("[0-9]+");
		Matcher m = this.numberPattern.matcher(value);

		if (!m.matches() || (Integer.valueOf(value).intValue() >= valuesNumber)) {
			this.logger.log(HDIVErrorCodes.CONFIDENTIAL_VALUE_INCORRECT, target, parameter, value);
			return false;
		}
		return true;
	}

	/**
	 * Checks that values <code>values</code> for the <code>parameter</code> are
	 * valid.
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
	 * @return True If the <code>values</code> validation is correct. False
	 *         otherwise.
	 * @throws Exception
	 *             if there is an internal error.
	 */
	private boolean validateReceivedValuesInState(HttpServletRequest request, String target, IState state,
			String parameter, String[] values) throws Exception {

		int size = values.length;
		String[] originalValues = new String[size];

		IDataValidator dataValidator = this.dataValidatorFactory.newInstance(state);

		String targetWithoutContextPath = this.getTargetWithoutContextPath(request, target);

		IValidationResult result = null;
		for (int i = 0; i < size; i++) {

			result = dataValidator.validate(values[i], targetWithoutContextPath, parameter);

			if (!result.getLegal()) {
				this.logger.log(HDIVErrorCodes.PARAMETER_VALUE_INCORRECT, target, parameter, values[i]);
				return false;
			} else {
				originalValues[i] = (String) result.getResult();
			}
		}

		if (this.hdivConfig.getConfidentiality().equals(Boolean.TRUE)) {
			this.addParameterToRequest(request, parameter, originalValues);
		}

		return true;
	}

	/**
	 * Adds one parameter to the request. Since the HttpServletRequest object's
	 * parameters are unchanged according to the Servlet specification, the
	 * instance of request should be passed as a parameter of type
	 * RequestWrapper.
	 * 
	 * @param request
	 *            HttpServletRequest to validate
	 * @param name
	 *            new parameter name
	 * @param value
	 *            new parameter value
	 * @throws HDIVException
	 *             si el objeto request no es de tipo RequestWrapper
	 */
	protected void addParameterToRequest(HttpServletRequest request, String name, Object value) {

		if (request instanceof RequestWrapper) {

			RequestWrapper wrapper = (RequestWrapper) request;
			wrapper.addParameter(name, value);

		} else {
			throw new HDIVException("El objeto request no es de tipo RequestWrapper.");
		}

	}

	/**
	 * Gets the part of the url that represents the action to be executed in
	 * this request.
	 * 
	 * @param request
	 *            HttpServletRequest to validate
	 * @return target Part of the url that represents the target action
	 * @throws HDIVException
	 */
	protected String getTarget(HttpServletRequest request) {
		try {
			String requestUri = request.getRequestURI();
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
	 *            target to stripp the ContextPath
	 * @return target without the ContextPath
	 */
	protected String getTargetWithoutContextPath(HttpServletRequest request, String target) {
		String targetWithoutContextPath = target.substring(request.getContextPath().length());
		return targetWithoutContextPath;
	}

	/**
	 * Name of the parameter that HDIV will include in the requests or/and forms
	 * which contains the state identifier in the memory strategy or the state
	 * itself in the Encoded or Hash strategies.
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
	 * Method invoked before validation. Designed to change the validation logic
	 * beyond the base implementation.
	 * </p>
	 * <p>
	 * The response of the method can have three meanings:<br/>
	 * -Boolean = true: The validation has been completed correctly and is not
	 * necessary to run the entire validation process.<br/>
	 * -Boolean = false: The validation has encountered an error and terminates
	 * the validation process. <br/>
	 * -Boolean = null: It should continue with the validation process (default
	 * answer).<br/>
	 * </p>
	 * 
	 * @param request
	 *            HttpServletRequest to validate
	 * @return Boolean result
	 */
	protected Boolean preValidate(HttpServletRequest request, String target) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.filter.IValidationHelper#startPage(javax.servlet.http.
	 * HttpServletRequest)
	 */
	public void startPage(HttpServletRequest request) {
		// DataComposer startPage moved to InitListener
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.filter.IValidationHelper#endPage(javax.servlet.http.
	 * HttpServletRequest)
	 */
	public void endPage(HttpServletRequest request) {
		// DataComposer endPage moved to InitListener
	}

	/**
	 * @return Returns the user logger.
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * @param logger
	 *            The user logger to set.
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	/**
	 * @return Returns the utility methods for state.
	 */
	public StateUtil getStateUtil() {
		return stateUtil;
	}

	/**
	 * @param stateUtil
	 *            The state utility to set.
	 */
	public void setStateUtil(StateUtil stateUtil) {
		this.stateUtil = stateUtil;
	}

	/**
	 * @return Returns the HDIV configuration object.
	 */
	public HDIVConfig getHdivConfig() {
		return hdivConfig;
	}

	/**
	 * @param hdivConfig
	 *            The HDIV configuration object to set.
	 */
	public void setHdivConfig(HDIVConfig hdivConfig) {
		this.hdivConfig = hdivConfig;
	}

	/**
	 * @return the session
	 */
	public ISession getSession() {
		return session;
	}

	/**
	 * @param session
	 *            the session to set
	 */
	public void setSession(ISession session) {
		this.session = session;
	}

	/**
	 * @return the dataValidatorFactory
	 */
	public DataValidatorFactory getDataValidatorFactory() {
		return dataValidatorFactory;
	}

	/**
	 * @param dataValidatorFactory
	 *            the dataValidatorFactory to set
	 */
	public void setDataValidatorFactory(DataValidatorFactory dataValidatorFactory) {
		this.dataValidatorFactory = dataValidatorFactory;
	}

	/**
	 * @return the numberPattern
	 */
	public Pattern getNumberPattern() {
		return numberPattern;
	}

	/**
	 * @param numberPattern
	 *            the numberPattern to set
	 */
	public void setNumberPattern(Pattern numberPattern) {
		this.numberPattern = numberPattern;
	}

}