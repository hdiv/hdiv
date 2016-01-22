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
package org.hdiv.dataValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.state.IParameter;
import org.hdiv.util.HDIVUtil;

/**
 * Validates that one parameter value or values are correct, besides to replacing the relative values by its real
 * values.
 * 
 * @author Roberto Velasco
 * @author Oscar Ocariz
 */
public class DataValidator implements IDataValidator {

	/**
	 * Commons Logging instance.
	 */
	protected Log log = LogFactory.getLog(DataValidator.class);

	/**
	 * HDIV general configuration.
	 */
	protected HDIVConfig config;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.dataValidator.IDataValidator#validate(javax.servlet.http.HttpServletRequest, java.lang.String,
	 * java.lang.String, java.lang.String, org.hdiv.state.IParameter, java.lang.String[])
	 */
	public IValidationResult validate(HttpServletRequest request, String value, String target, String parameter,
			IParameter stateParameter, String[] actionParamValues) {

		boolean confidentiality = this.config.getConfidentiality();
		boolean noConfidentiality = this.config.isParameterWithoutConfidentiality(request, parameter);
		if (log.isDebugEnabled() && noConfidentiality) {
			log.debug("Parameter [" + parameter + "] is ParameterWithoutConfidentiality.");
		}

		IValidationResult result = new ValidationResult();

		// TODO include here checking that there are no more values. Currently done in the helper

		if (!confidentiality || noConfidentiality) {
			// Confidentiality = false

			if (stateParameter != null) {
				if (stateParameter.existValue(value)) {
					result.setResult(value);
					result.setLegal(true);
				}
				else {
					result.setLegal(false);
				}
				return result;
			}
			else {
				// actionParamValues contains values
				for (int i = 0; i < actionParamValues.length; i++) {
					if (value.equals(actionParamValues[i])) {
						result.setResult(value);
						result.setLegal(true);
						return result;
					}
				}
				result.setLegal(false);
				return result;
			}

		}
		else {
			// Confidentiality = true
			if (!this.isInt(value)) {
				result.setLegal(false);
				return result;
			}

			// Confidentiality assures that data is int value
			int position = new Integer(value).intValue();

			if (stateParameter != null) {

				if (stateParameter.existPosition(position)) {

					result.setLegal(true);

					// update position value with the original value
					result.setResult(stateParameter.getValuePosition(position));
					return result;

				}
				else {
					result.setLegal(false);
					return result;
				}
			}
			else {

				if (actionParamValues.length > position) {

					result.setLegal(true);
					result.setResult(actionParamValues[position]);
					return result;
				}
				result.setLegal(false);
				return result;
			}

		}
	}

	/**
	 * Is data an integer?
	 * 
	 * @param data Data to check
	 * @return Returns true if <code>data</code> is a number. False in otherwise.
	 */
	protected boolean isInt(String data) {
		Pattern p = HDIVUtil.intPattern;
		Matcher m = p.matcher(data);
		return m.matches();
	}

	/**
	 * @param config the config to set
	 */
	public void setConfig(HDIVConfig config) {
		this.config = config;
	}

}