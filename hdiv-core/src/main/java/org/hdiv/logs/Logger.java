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
package org.hdiv.logs;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.util.HDIVUtil;

/**
 * Log that shows the attacks detected by HDIV. It includes type of attack and the identity (application user) of the
 * user. Since the way to obtain this user may vary from application to application, an standard interface has been
 * defined to be implemented by each application.
 * <p>
 * Log format =
 * type;target;parameterName;parameterValue;[originalParameterValue];userLocalIP;ip;userId;[validationRuleName]
 * </p>
 * 
 * @author Roberto Velasco
 * @see org.hdiv.logs.IUserData
 */
public class Logger {

	/**
	 * Commons Logging instance.
	 */
	private static Log log = LogFactory.getLog(Logger.class);

	/**
	 * Obtains user data from the request
	 */
	protected IUserData userData;

	/**
	 * Logger is initialized.
	 */
	public void init() {
	}

	/**
	 * Prints formatted attack produced by the user if the logging level defined in the Web application rate should be
	 * at least INFO.
	 * 
	 * @param type
	 *            Error type
	 * @param target
	 *            target name
	 * @param parameterName
	 *            parameter name
	 * @param parameterValue
	 *            parameter value
	 */
	public void log(String type, String target, String parameterName, String parameterValue) {

		this.log(type, target, parameterName, parameterValue, null);
	}

	/**
	 * Prints formatted attack produced by the user if the logging level defined in the Web application rate should be
	 * at least INFO.
	 * 
	 * @param type
	 *            Error type
	 * @param target
	 *            target name
	 * @param parameterName
	 *            parameter name
	 * @param parameterValue
	 *            parameter value
	 * @param originalParameterValue
	 *            original parameter value
	 * 
	 */
	public void log(String type, String target, String parameterName, String parameterValue,
			String originalParameterValue) {

		this.log(type, target, parameterName, parameterValue, originalParameterValue, null);
	}

	/**
	 * Prints formatted attack produced by the user if the logging level defined in the Web application rate should be
	 * at least INFO.
	 * 
	 * @param type
	 *            Error type
	 * @param target
	 *            target name
	 * @param parameterName
	 *            parameter name
	 * @param parameterValue
	 *            parameter value
	 * @param originalParameterValue
	 *            original parameter value
	 * @param validationRuleName
	 *            In an attack of type 'EDITABLE_VALIDATION_ERROR', contains the name of the rule that rejected the
	 *            value
	 */
	public void log(String type, String target, String parameterName, String parameterValue,
			String originalParameterValue, String validationRuleName) {

		HttpServletRequest request = this.getHttpServletRequest();

		String localIp = this.getUserLocalIP(request);
		String remoteIp = request.getRemoteAddr();
		String userName = this.userData.getUsername(request);

		String contextPath = request.getContextPath();
		if (!target.startsWith(contextPath)) {
			target = request.getContextPath() + target;
		}

		this.log(type, target, parameterName, parameterValue, originalParameterValue, localIp, remoteIp, userName,
				validationRuleName);
	}

	/**
	 * Logs formatted attack produced by the user.
	 * 
	 * @param type
	 *            Error type
	 * @param target
	 *            target name
	 * @param parameterName
	 *            parameter name
	 * @param parameterValue
	 *            parameter value
	 * @param originalParameterValue
	 *            original parameter value
	 * @param localIp
	 *            user local ip
	 * @param remoteIp
	 *            user remote ip
	 * @param userName
	 *            user name in application
	 * @param validationRuleName
	 *            In an attack of type 'EDITABLE_VALIDATION_ERROR', contains the name of the rule that rejected the
	 *            value
	 */
	protected void log(String type, String target, String parameterName, String parameterValue,
			String originalParameterValue, String localIp, String remoteIp, String userName, String validationRuleName) {

		String formatedData = this.format(type, target, parameterName, parameterValue, originalParameterValue, localIp,
				remoteIp, userName, validationRuleName);
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
	 * @param type
	 *            Error type
	 * @param target
	 *            target name
	 * @param parameterName
	 *            parameter name
	 * @param parameterValue
	 *            parameter value
	 * @param originalParameterValue
	 *            original parameter value
	 * @param localIp
	 *            user local ip
	 * @param remoteIp
	 *            user remote ip
	 * @param userName
	 *            user name in application
	 * @param validationRuleName
	 *            In an attack of type 'EDITABLE_VALIDATION_ERROR', contains the name of the rule that rejected the
	 *            value
	 * 
	 * @return String Formatted text with the attach.
	 */
	protected String format(String type, String target, String parameterName, String parameterValue,
			String originalParameterValue, String localIp, String remoteIp, String userName, String validationRuleName) {

		StringBuffer buffer = new StringBuffer();
		buffer.append(type);
		buffer.append(";");
		buffer.append(target);
		buffer.append(";");
		buffer.append(parameterName);
		buffer.append(";");
		buffer.append(parameterValue);
		buffer.append(";");
		if (originalParameterValue != null) {
			buffer.append(originalParameterValue);
		}
		buffer.append(";");
		buffer.append(localIp);
		buffer.append(";");
		buffer.append(remoteIp);
		buffer.append(";");
		buffer.append(userName);
		buffer.append(";");
		if (validationRuleName != null) {
			buffer.append(validationRuleName);
		}

		return buffer.toString();
	}

	/**
	 * Obtain user local ip.
	 * 
	 * @param request
	 *            the HttpServletRequest of the request
	 * @return Returns the remote user IP address if behind the proxy.
	 */
	protected String getUserLocalIP(HttpServletRequest request) {

		String ipAddress = null;

		if (request.getHeader("X-Forwarded-For") == null) {
			ipAddress = request.getRemoteAddr();
		} else {
			ipAddress = request.getHeader("X-Forwarded-For");
		}
		return ipAddress;
	}

	/**
	 * Obtains the request instance
	 * 
	 * @return request
	 */
	protected HttpServletRequest getHttpServletRequest() {

		return HDIVUtil.getHttpServletRequest();

	}

	public void setUserData(IUserData userData) {
		this.userData = userData;
	}
}