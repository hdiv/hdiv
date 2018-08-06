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

import org.hdiv.util.HDIVErrorCodes;

public class ValidatorError {

	private static boolean debugMode;

	/**
	 * Error code from {@link HDIVErrorCodes}
	 */
	private final String type;

	/**
	 * Protection rule related to validation error
	 */
	private final String rule;

	/**
	 * Target url
	 */
	private String target;

	/**
	 * The name of the parameter
	 */
	private final String parameterName;

	/**
	 * The value of the parameter
	 */
	private final String parameterValue;

	/**
	 * The original (not modified) value of the parameter
	 */
	private final String originalParameterValue;

	/**
	 * Users local IP
	 */
	private String localIp;

	/**
	 * Users remote IP
	 */
	private String remoteIp;

	/**
	 * The name of the user that made the request
	 */
	private String userName;

	/**
	 * In an attack of type 'EDITABLE_VALIDATION_ERROR', contains the name of the rule that rejected the value
	 */
	private final String validationRuleName;

	private Throwable exception;

	private StackTraceElement[] stackTrace;

	public ValidatorError(final String type) {
		this(type, null);
	}

	public ValidatorError(final Throwable error, final String target) {
		this(error.getMessage(), target);
		setException(error);
	}

	public ValidatorError(final String type, final String target) {
		this(type, target, null);
	}

	public ValidatorError(final String type, final String target, final String parameterName) {
		this(type, target, parameterName, null);
	}

	public ValidatorError(final String type, final String target, final String parameterName, final String parameterValue) {
		this(type, target, parameterName, parameterValue, null);
	}

	public ValidatorError(final String type, final String target, final String parameterName, final String parameterValue,
			final String originalParameterValue) {
		this(type, target, parameterName, parameterValue, originalParameterValue, null);
	}

	public ValidatorError(final String type, final String target, final String parameterName, final String parameterValue,
			final String originalParameterValue, final String validationRuleName) {
		this(type, target, parameterName, parameterValue, originalParameterValue, null, null, null, validationRuleName);
	}

	public ValidatorError(final String type, final String target, final String parameterName, final String parameterValue,
			final String originalParameterValue, final String localIp, final String remoteIp, final String userName,
			final String validationRuleName) {
		this(type, !HDIVErrorCodes.isEditableError(type) ? "AUTOMATED_REAL_TIME_WHITELISTING" : "CUSTOM_INPUT_VALIDATION", target,
				parameterName, parameterValue, originalParameterValue, localIp, remoteIp, userName, validationRuleName);
	}

	public ValidatorError(final String type, final String rule, final String target, final String parameterName,
			final String parameterValue, final String originalParameterValue, final String localIp, final String remoteIp,
			final String userName, final String validationRuleName) {
		this.type = type;
		this.rule = rule;
		this.target = target;
		this.parameterName = parameterName;
		this.parameterValue = parameterValue;
		this.originalParameterValue = originalParameterValue;
		this.localIp = localIp;
		this.remoteIp = remoteIp;
		this.userName = userName;
		this.validationRuleName = validationRuleName;
		if (debugMode) {
			stackTrace = Thread.currentThread().getStackTrace();
		}
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the rule
	 */
	public String getRule() {
		return rule;
	}

	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * @return the parameterName
	 */
	public String getParameterName() {
		return parameterName;
	}

	/**
	 * @return the parameterValue
	 */
	public String getParameterValue() {
		return parameterValue;
	}

	/**
	 * @return the originalParameterValue
	 */
	public String getOriginalParameterValue() {
		return originalParameterValue;
	}

	/**
	 * @return the localIp
	 */
	public String getLocalIp() {
		return localIp;
	}

	/**
	 * @return the remoteIp
	 */
	public String getRemoteIp() {
		return remoteIp;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @return the validationRuleName
	 */
	public String getValidationRuleName() {
		return validationRuleName;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(final String target) {
		this.target = target;
	}

	/**
	 * @param localIp the localIp to set
	 */
	public void setLocalIp(final String localIp) {
		this.localIp = localIp;
	}

	/**
	 * @param remoteIp the remoteIp to set
	 */
	public void setRemoteIp(final String remoteIp) {
		this.remoteIp = remoteIp;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(final String userName) {
		this.userName = userName;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ValidatorError [type=" + type + ", rule=" + rule + ", target=" + target + ", parameterName=" + parameterName
				+ ", parameterValue=" + parameterValue + ", originalParameterValue=" + originalParameterValue + ", localIp=" + localIp
				+ ", remoteIp=" + remoteIp + ", userName=" + userName + ", validationRuleName=" + validationRuleName + "]";
	}

	public static void setDebug(final boolean debugMode) {
		ValidatorError.debugMode = debugMode;
	}

	public StackTraceElement[] getStackTrace() {
		return stackTrace;
	}

	public Throwable getException() {
		return exception;
	}

	public void setException(final Throwable exception) {
		this.exception = exception;
	}

}