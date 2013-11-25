/**
 * Copyright 2005-2013 hdiv.org
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
package org.hdiv.dataValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.state.IParameter;
import org.hdiv.state.IState;
import org.hdiv.util.HDIVUtil;

/**
 * It uses an object of type IState and validates all the entry data, besides to replacing the relative values by its
 * real values.
 * 
 * @author Roberto Velasco
 * @author Oscar Ocariz
 */
public class DataValidator implements IDataValidator {

	/**
	 * Commons Logging instance.
	 */
	private Log log = LogFactory.getLog(DataValidator.class);

	/**
	 * Object that represents the result of the validation.
	 */
	private IValidationResult validationResult;

	/**
	 * State that represents all the data that composes a request or a form.
	 */
	private IState state;

	/**
	 * HDIV general configuration.
	 */
	private HDIVConfig config;

	/**
	 * <p>
	 * Checks if the value <code>data</code> sent by the user to the server in the parameter <code>parameter</code> is
	 * correct or not. The received value is checked with the one stored in the state to decide if it is correct.
	 * </p>
	 * <p>
	 * In the encoded and hash strategies, the state is obtained from the user request. However, in the memory strategy
	 * the state is obtained from the user session, using the state identifier received within the request.
	 * </p>
	 * 
	 * @param value
	 *            value sent by the client
	 * @param target
	 *            target action name
	 * @param parameter
	 *            parameter name
	 * @return object that represents the result of the validation process for the parameter <code>parameter</code> and
	 *         the value <code>data</code>.
	 */
	public IValidationResult validate(String value, String target, String parameter) {

		boolean confidentiality = this.config.getConfidentiality();
		boolean noConfidentiality = this.config.isParameterWithoutConfidentiality(parameter);
		if (log.isDebugEnabled() && noConfidentiality) {
			log.debug("Parameter [" + parameter + "] is ParameterWithoutConfidentiality.");
		}

		IParameter stateParameter = this.state.getParameter(parameter);
		if (!confidentiality || noConfidentiality) {
			// Confidentiality = false

			if (stateParameter.existValue(value)) {
				validationResult.setResult(value);
				validationResult.setLegal(true);
			} else {
				validationResult.setLegal(false);
			}

			return validationResult;

		} else {
			// Confidentiality = true
			if (!this.isInt(value)) {
				validationResult.setLegal(false);
				return validationResult;
			}

			// Confidentiality assures that data is int value
			int position = new Integer(value).intValue();

			if (stateParameter.existPosition(position)) {

				validationResult.setLegal(true);

				// update position value with the original value
				validationResult.setResult(stateParameter.getValuePosition(position));
				return validationResult;

			} else {
				validationResult.setLegal(false);
				return validationResult;
			}
		}
	}

	/**
	 * Is data an integer?
	 * 
	 * @param data
	 *            Data to check
	 * @return Returns true if <code>data</code> is a number. False in otherwise.
	 */
	private boolean isInt(String data) {
		Pattern p = HDIVUtil.intPattern;
		Matcher m = p.matcher(data);
		return m.matches();
	}

	public IValidationResult getValidationResult() {
		return validationResult;
	}

	public void setValidationResult(IValidationResult validationResult) {
		this.validationResult = validationResult;
	}

	/**
	 * @param state
	 *            The validation process state to set.
	 */
	public void setState(IState state) {
		this.state = state;
	}

	/**
	 * @param config
	 *            the config to set
	 */
	public void setConfig(HDIVConfig config) {
		this.config = config;
	}

}