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

import org.hdiv.filter.ValidatorError;
import org.slf4j.LoggerFactory;

/**
 * Log that shows the attacks detected by HDIV. It includes type of attack and the identity (application user) of the user. Since the way to
 * obtain this user may vary from application to application, an standard interface has been defined to be implemented by each application.
 * <p>
 * Log format = type;target;parameterName;parameterValue;[originalParameterValue];userLocalIP;IP;userId;[validationRuleName]
 * </p>
 * 
 * @author Roberto Velasco
 * @author Gotzon Illarramendi
 * @see org.hdiv.logs.IUserData
 */
public class Logger {

	/**
	 * Commons Logging instance.
	 */
	private static final org.slf4j.Logger log = LoggerFactory.getLogger(Logger.class);

	/**
	 * Logger is initialized.
	 */
	public void init() {
	}

	/**
	 * Prints formatted attack produced by the user if the logging level defined in the Web application rate should be at least INFO.
	 * 
	 * @param error Validator error data
	 */
	public void log(final ValidatorError error) {

		this.log(error.getType(), error.getTarget(), error.getParameterName(), error.getParameterValue(), error.getOriginalParameterValue(),
				error.getLocalIp(), error.getRemoteIp(), error.getUserName(), error.getValidationRuleName());
	}

	/**
	 * Logs formatted attack produced by the user.
	 * 
	 * @param type Error type
	 * @param target target name
	 * @param parameterName parameter name
	 * @param parameterValue parameter value
	 * @param originalParameterValue original parameter value
	 * @param localIp user local IP
	 * @param remoteIp user remote IP
	 * @param userName user name in application
	 * @param validationRuleName In an attack of type 'EDITABLE_VALIDATION_ERROR', contains the name of the rule that rejected the value
	 */
	protected void log(final String type, final String target, final String parameterName, final String parameterValue,
			final String originalParameterValue, final String localIp, final String remoteIp, final String userName,
			final String validationRuleName) {

		String formatedData = format(type, target, parameterName, parameterValue, originalParameterValue, localIp, remoteIp, userName,
				validationRuleName);
		log.info(formatedData);
	}

	/**
	 * <p>
	 * Formatted text with information from the attack produced by the user. The log format is as follows:
	 * </p>
	 * <p>
	 * <code>[error type];[target];[parameterName];[parameterValue];[originalParameterValue];[user local IP address];[IP address of the client or the last proxy that sent the request];[userId];[validationRuleName]</code>
	 * </p>
	 * 
	 * @param type Error type
	 * @param target target name
	 * @param parameterName parameter name
	 * @param parameterValue parameter value
	 * @param originalParameterValue original parameter value
	 * @param localIp user local IP
	 * @param remoteIp user remote IP
	 * @param userName user name in application
	 * @param validationRuleName In an attack of type 'EDITABLE_VALIDATION_ERROR', contains the name of the rule that rejected the value
	 * 
	 * @return String Formatted text with the attach.
	 */
	protected String format(final String type, final String target, final String parameterName, final String parameterValue,
			final String originalParameterValue, final String localIp, final String remoteIp, final String userName,
			final String validationRuleName) {

		StringBuilder buffer = new StringBuilder();
		buffer.append(type);
		buffer.append(";");
		buffer.append(target);
		buffer.append(";");
		if (parameterName != null) {
			buffer.append(parameterName);
		}
		buffer.append(";");
		if (parameterValue != null) {
			buffer.append(parameterValue);
		}
		buffer.append(";");
		if (originalParameterValue != null) {
			buffer.append(originalParameterValue);
		}
		buffer.append(";");
		if (localIp != null) {
			buffer.append(localIp);
		}
		buffer.append(";");
		if (remoteIp != null) {
			buffer.append(remoteIp);
		}
		buffer.append(";");
		if (userName != null) {
			buffer.append(userName);
		}
		buffer.append(";");
		if (validationRuleName != null) {
			buffer.append(validationRuleName);
		}

		return buffer.toString();
	}

}