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
package org.hdiv.filter;

import org.hdiv.util.HDIVErrorCodes;

public class ValidatorError {

	/**
	 * Error code from {@link HDIVErrorCodes}
	 */
	protected String type;

	/**
	 * Target url
	 */
	protected String target;

	/**
	 * The name of the parameter
	 */
	protected String parameterName;

	/**
	 * The value of the parameter
	 */
	protected String parameterValue;

	/**
	 * The original (not modified) value of the parameter
	 */
	protected String originalParameterValue;

	/**
	 * Users local IP
	 */
	protected String localIp;

	/**
	 * Users remote IP
	 */
	protected String remoteIp;

	/**
	 * The name of the user that made the request
	 */
	protected String userName;

	/**
	 * In an attack of type 'EDITABLE_VALIDATION_ERROR', contains the name of the rule that rejected the value
	 */
	protected String validationRuleName;

	public ValidatorError(String type) {
		this.type = type;
	}

	public ValidatorError(String type, String target) {
		this.type = type;
		this.target = target;
	}

	public ValidatorError(String type, String target, String parameterName) {
		this.type = type;
		this.target = target;
		this.parameterName = parameterName;
	}

	public ValidatorError(String type, String target, String parameterName, String parameterValue) {
		this.type = type;
		this.target = target;
		this.parameterName = parameterName;
		this.parameterValue = parameterValue;
	}

	public ValidatorError(String type, String target, String parameterName, String parameterValue,
			String originalParameterValue) {
		this.type = type;
		this.target = target;
		this.parameterName = parameterName;
		this.parameterValue = parameterValue;
		this.originalParameterValue = originalParameterValue;
	}

	public ValidatorError(String type, String target, String parameterName, String parameterValue,
			String originalParameterValue, String validationRuleName) {
		this.type = type;
		this.target = target;
		this.parameterName = parameterName;
		this.parameterValue = parameterValue;
		this.originalParameterValue = originalParameterValue;
		this.validationRuleName = validationRuleName;
	}

	public ValidatorError(String type, String target, String parameterName, String parameterValue,
			String originalParameterValue, String localIp, String remoteIp, String userName, String validationRuleName) {
		this.type = type;
		this.target = target;
		this.parameterName = parameterName;
		this.parameterValue = parameterValue;
		this.originalParameterValue = originalParameterValue;
		this.localIp = localIp;
		this.remoteIp = remoteIp;
		this.userName = userName;
		this.validationRuleName = validationRuleName;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
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
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @param target
	 *            the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * @param parameterName
	 *            the parameterName to set
	 */
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	/**
	 * @param parameterValue
	 *            the parameterValue to set
	 */
	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}

	/**
	 * @param originalParameterValue
	 *            the originalParameterValue to set
	 */
	public void setOriginalParameterValue(String originalParameterValue) {
		this.originalParameterValue = originalParameterValue;
	}

	/**
	 * @param validationRuleName
	 *            the validationRuleName to set
	 */
	public void setValidationRuleName(String validationRuleName) {
		this.validationRuleName = validationRuleName;
	}

	/**
	 * @param localIp
	 *            the localIp to set
	 */
	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}

	/**
	 * @param remoteIp
	 *            the remoteIp to set
	 */
	public void setRemoteIp(String remoteIp) {
		this.remoteIp = remoteIp;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ValidatorError [type=" + type + ", target=" + target + ", parameterName=" + parameterName
				+ ", parameterValue=" + parameterValue + ", originalParameterValue=" + originalParameterValue
				+ ", localIp=" + localIp + ", remoteIp=" + remoteIp + ", userName=" + userName
				+ ", validationRuleName=" + validationRuleName + "]";
	}

}