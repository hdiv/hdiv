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
package org.hdiv.config;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;
import org.hdiv.validator.IValidation;

/**
 * Class containing HDIV configuration initialized from Spring Factory.
 * 
 * @author Roberto Velasco
 * @author Gorka Vicente
 * @author Gotzon Illarramendi
 */
public class HDIVConfig {

	/**
	 * Map with the pages that will not be Treated by the HDIV filter. The init pages are initialized by the Spring
	 * factory.
	 */
	protected Hashtable startPages = new Hashtable();

	/**
	 * Map with the parameters that will not be validated by the HDIV filter. The init parameters are initialized by the
	 * Spring factory.
	 */
	private Hashtable startParameters = new Hashtable();

	/**
	 * Map of the validated actions that are init pages. It is necessary to create this map to improve the efficiency at
	 * checking init-pages.
	 * 
	 * @since HDIV 1.1.1
	 */
	private Hashtable matchedPages = new Hashtable();

	/**
	 * Map of the validated parameters that are init parameters. It is necessary to create this map to improve the
	 * efficiency at checking init-parameters.
	 * 
	 * @since HDIV 1.1.1
	 */
	private Hashtable matchedParameters = new Hashtable();

	/**
	 * Url of the error page to which HDIV will redirect the request if it doesn't pass the HDIV validation.
	 */
	private String errorPage;

	/**
	 * Url of the error page to which HDIV will redirect the request if it doesn't pass the HDIV validation caused by
	 * session expiration and the user is not logged in the application.
	 */
	private String sessionExpiredLoginPage;

	/**
	 * Url of the error page to which HDIV will redirect the request if it doesn't pass the HDIV validation caused by
	 * session expiration and the user is logged in the application.
	 */
	private String sessionExpiredHomePage;

	/**
	 * Confidentiality indicator to know if information is accessible only for those who are authorized.
	 */
	private Boolean confidentiality;

	/**
	 * Parameters which HDIV validation will not be applied to.
	 */
	private Map paramsWithoutValidation;

	/**
	 * Validations for editable fields (text/textarea) defined by the user in the hdiv-validations.xml configuration
	 * file of Spring.
	 */
	private HDIVValidations validations;

	/**
	 * If <code>avoidCookiesIntegrity</code> is true, cookie integrity will not be applied.
	 */
	private boolean avoidCookiesIntegrity;

	/**
	 * If <code>avoidCookiesConfidentiality</code> is true, cookie confidentiality will not be applied.
	 */
	private boolean avoidCookiesConfidentiality;

	/**
	 * if <code>avoidValidationInUrlsWithoutParams</code> is true, HDIV validation will not be applied in urls without
	 * parameters.
	 * 
	 * @since HDIV 2.1.0
	 */
	private boolean avoidValidationInUrlsWithoutParams;

	/**
	 * Extensions that we have to protect with HDIV's state.
	 * 
	 * @since HDIV 2.0
	 */
	private Hashtable protectedURLPatterns;

	/**
	 * Extensions that we have not to protect with HDIV's state.
	 * 
	 * @since HDIV 2.1.0
	 */
	private List excludedURLExtensions;

	/**
	 * HDIV adds an extra parameter to all links and forms. By default this parameter is _HDIV_STATE. If
	 * <code>randomName</code> is true a random name is generated instead of default name (_HDIV_STATE_)
	 * 
	 * @since HDIV 2.1.0
	 */
	private boolean randomName;

	/**
	 * HDIV behaviour strategy. There are 3 possible options: memory, cipher, hash
	 * 
	 * @since HDIV 2.1.0
	 */
	private String strategy;

	/**
	 * If debug mode is enabled, the attacks are logged but the requests are not stopped.
	 * 
	 * @since HDIV 2.1.1
	 */
	private boolean debugMode = false;

	/**
	 * Show error page on request with editable validation errors
	 * 
	 * @since 2.1.4
	 */
	private boolean showErrorPageOnEditableValidation;

	/**
	 * @param strategy
	 *            the strategy to set
	 */
	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}

	/**
	 * Checks if <code>parameter</code> is an init parameter, in which case it will not be treated by HDIV.
	 * 
	 * @param parameter
	 *            Parameter name
	 * @return True if <code>parameter</code> is an init parameter. False otherwise.
	 */
	public boolean isStartParameter(String parameter) {

		if (this.matchedParameters.containsKey(parameter)) {
			return true;
		}

		String value = this.checkValue(parameter, this.startParameters);
		if (value == null) {
			return false;
		}

		this.matchedParameters.put(parameter, value);
		return true;
	}

	/**
	 * Checks if <code>target</code> is an init action, in which case it will not be treated by HDIV.
	 * 
	 * @param target
	 *            target name
	 * @param isFormUrl
	 *            true if is a Form Url
	 * @return True if <code>target</code> is an init action. False otherwise.
	 */
	public boolean isStartPage(String target, boolean isFormUrl) {
		String method = "GET";
		if (isFormUrl) {
			method = "POST";
		}
		return this.isStartPage(target, method);
	}

	/**
	 * Checks if <code>target</code> is an init action, in which case it will not be treated by HDIV.
	 * 
	 * @param target
	 *            target name
	 * @param method
	 *            request method (get,post...)
	 * @return True if <code>target</code> is an init action. False otherwise.
	 */
	public boolean isStartPage(String target, String method) {

		if (method != null) {
			method = method.toUpperCase();
		}

		String key = target + "_" + method;

		if (this.matchedPages.containsKey(key)) {
			return true;
		}

		String value = this.checkStartPageValue(target, method, this.startPages);
		if (value == null) {
			return false;
		}

		this.matchedPages.put(key, value);
		return true;
	}

	public boolean hasExtensionToExclude(String path) {

		if (this.excludedURLExtensions == null) {
			return false;
		}

		if (path.indexOf("?") > 0) {
			path = path.substring(0, path.indexOf("?"));
		}

		if (path.charAt(path.length() - 1) == '/') {
			return false;
		}

		int pound = path.indexOf("#");
		if (pound >= 0) {
			path = path.substring(0, pound);
		}

		for (Iterator iter = this.excludedURLExtensions.iterator(); iter.hasNext();) {

			String extension = (String) iter.next();
			if (path.endsWith(extension)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Check if the parameter needs confidentiality.
	 * 
	 * @param paramName
	 *            parameter name to check
	 * @return boolean
	 */
	public boolean isParameterWithoutConfidentiality(String paramName) {

		if (HDIVUtil.getHttpSession() != null) {
			String modifyHdivStateParameterName = (String) HDIVUtil.getHttpSession().getAttribute(
					Constants.MODIFY_STATE_HDIV_PARAMETER);
			if (modifyHdivStateParameterName != null && modifyHdivStateParameterName.equals(paramName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the parameter <code>parameter</code> is defined by the user as a no required validation parameter for
	 * the action <code>action</code>.
	 * 
	 * @param action
	 *            action name
	 * @param parameter
	 *            parameter name
	 * @return True if it is parameter that needs no validation. False otherwise.
	 */
	public boolean isParameterWithoutValidation(String action, String parameter) {

		if (action == null) {
			return false;
		}

		if (this.paramsWithoutValidation == null) {
			return false;
		}

		Pattern p = null;
		Matcher m = null;

		String key = null;
		for (Iterator iter = this.paramsWithoutValidation.keySet().iterator(); iter.hasNext();) {

			key = (String) iter.next();

			p = Pattern.compile(key);
			m = p.matcher(action);

			if (m.matches()) {

				List parametersWithoutValidation = (List) this.paramsWithoutValidation.get(key);

				String definedParameter = null;
				for (int i = 0; i < parametersWithoutValidation.size(); i++) {

					definedParameter = (String) parametersWithoutValidation.get(i);

					p = Pattern.compile(definedParameter);
					m = p.matcher(parameter);

					if (m.matches()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Checks if <code>value</code> is an init action or parameter, in which case it will not be treated by HDIV.
	 * 
	 * @param value
	 *            target or parameter name
	 * @param startValues
	 *            Map with start values
	 * @return True if <code>value</code> is an init action or parameter. False otherwise.
	 * @since HDIV 1.1.1
	 */
	public String checkValue(String value, Map startValues) {

		for (Iterator iter = startValues.keySet().iterator(); iter.hasNext();) {

			String key = (String) iter.next();
			Pattern p = (Pattern) startValues.get(key);

			Matcher m = p.matcher(value);

			if (m.matches()) {
				return key;
			}
		}
		return null;
	}

	/**
	 * Checks if <code>value</code> is an init action or parameter, in which case it will not be treated by HDIV.
	 * 
	 * @param value
	 *            target or parameter name
	 * @param method
	 *            request method (get,post)
	 * @param startValues
	 *            Map with start values
	 * @return True if <code>value</code> is an init action or parameter. False otherwise.
	 * @since HDIV 1.1.1
	 */
	public String checkStartPageValue(String value, String method, Map startValues) {

		for (Iterator iter = startValues.keySet().iterator(); iter.hasNext();) {

			String key = (String) iter.next();
			StartPage startPage = (StartPage) startValues.get(key);

			Matcher m = startPage.getCompiledPattern().matcher(value);

			if (m.matches()) {
				if (startPage.isAnyMethod()) {
					return key;
				} else if (startPage.getMethod().equalsIgnoreCase(method)) {
					return key;
				}
			}
		}
		return null;
	}

	/**
	 * Checks if the HDIV validation must be applied to the parameter <code>parameter</code>
	 * 
	 * @param parameter
	 *            Parameter name
	 * @param hdivParameter
	 *            Name of the parameter that HDIV will include in the requests or/and forms which contains the state
	 *            identifier parameter
	 * @return True if <code>parameter</code> doesn't need HDIV validation.
	 */
	public boolean needValidation(String parameter, String hdivParameter) {

		if (this.isStartParameter(parameter) || (parameter.equals(hdivParameter))) {
			return false;
		}
		return true;
	}

	public String getErrorPage() {
		return errorPage;
	}

	public void setErrorPage(String errorPage) {

		if (errorPage != null && !errorPage.startsWith("/")) {
			errorPage = "/" + errorPage;
		}
		this.errorPage = errorPage;
		if (errorPage != null) {
			this.startPages.put(errorPage, new StartPage(null, errorPage));
		}
	}

	public String getSessionExpiredLoginPage() {
		return sessionExpiredLoginPage;
	}

	public void setSessionExpiredLoginPage(String sessionExpiredLoginPage) {
		if (sessionExpiredLoginPage != null && !sessionExpiredLoginPage.startsWith("/")) {
			sessionExpiredLoginPage = "/" + sessionExpiredLoginPage;
		}
		this.sessionExpiredLoginPage = sessionExpiredLoginPage;
		if (sessionExpiredLoginPage != null) {
			this.startPages.put(sessionExpiredLoginPage, new StartPage(null, sessionExpiredLoginPage));
		}
	}

	public String getSessionExpiredHomePage() {
		return sessionExpiredHomePage;
	}

	public void setSessionExpiredHomePage(String sessionExpiredHomePage) {
		if (sessionExpiredHomePage != null && !sessionExpiredHomePage.startsWith("/")) {
			sessionExpiredHomePage = "/" + sessionExpiredHomePage;
		}
		this.sessionExpiredHomePage = sessionExpiredHomePage;
		if (sessionExpiredHomePage != null) {
			this.startPages.put(sessionExpiredHomePage, new StartPage(null, sessionExpiredHomePage));
		}
	}

	public Boolean getConfidentiality() {
		return confidentiality;
	}

	public void setConfidentiality(Boolean confidentiality) {
		this.confidentiality = confidentiality;
	}

	public Map getParamsWithoutValidation() {
		return paramsWithoutValidation;
	}

	public void setParamsWithoutValidation(Map paramsWithoutValidation) {
		this.paramsWithoutValidation = paramsWithoutValidation;
	}

	/**
	 * It creates a map from the list of start pages defined by the user.
	 * 
	 * @param userStartPages
	 *            list of start pages defined by the user
	 */
	public void setUserStartPages(List userStartPages) {

		for (int i = 0; i < userStartPages.size(); i++) {

			StartPage startPage;

			Object o = userStartPages.get(i);
			if (o instanceof String) {
				String currentPattern = (String) o;
				startPage = new StartPage(null, currentPattern);
			} else {
				startPage = (StartPage) o;
			}

			this.startPages.put(startPage.getPattern(), startPage);
		}
	}

	/**
	 * It creates a map from the list of init parameters defined by the user.
	 * 
	 * @param userStartParameters
	 *            list of init parameters defined by the user
	 */
	public void setUserStartParameters(List userStartParameters) {

		for (int i = 0; i < userStartParameters.size(); i++) {

			String currentPattern = (String) userStartParameters.get(i);
			this.startParameters.put(currentPattern, Pattern.compile(currentPattern));
		}
	}

	/**
	 * @param validations
	 *            The validations to set.
	 * @since HDIV 1.1
	 */
	public void setValidations(HDIVValidations validations) {
		this.validations = validations;
	}

	/**
	 * Checks if there are validations defined for editable fields
	 * 
	 * @return True if validations for editable fields have been defined. False otherwise.
	 * @since HDIV 1.1
	 */
	public boolean existValidations() {

		return ((this.validations != null) && (this.validations.getUrls() != null) && (this.validations.getUrls()
				.size() > 0));
	}

	/**
	 * <p>
	 * Checks if the values <code>values</code> are valid for the editable parameter <code>parameter</code>, using the
	 * validations defined in the hdiv-validations.xml configuration file of Spring.
	 * </p>
	 * <p>
	 * There are two types of validations:
	 * <li>accepted: the value is valid only if it passes the validation</li>
	 * <li>rejected: the value is rejected if doesn't pass the validation</li>
	 * </p>
	 * 
	 * @param url
	 *            target url
	 * @param parameter
	 *            parameter name
	 * @param values
	 *            parameter's values
	 * @param dataType
	 *            editable data type
	 * @return True if the values <code>values</code> are valid for the parameter <code>parameter</code>.
	 * @since HDIV 1.1
	 */
	public boolean areEditableParameterValuesValid(String url, String parameter, String[] values, String dataType) {

		Pattern p = null;
		Matcher m = null;

		Iterator userDefinedURLs = this.validations.getUrls().keySet().iterator();
		while (userDefinedURLs.hasNext()) {

			String regExp = (String) userDefinedURLs.next();

			p = Pattern.compile(regExp);
			m = p.matcher(url);

			if (m.matches()) {

				List userDefinedValidations = (List) this.validations.getUrls().get(regExp);
				for (int i = 0; i < userDefinedValidations.size(); i++) {

					IValidation currentValidation = (IValidation) userDefinedValidations.get(i);

					if (!currentValidation.validate(parameter, values, dataType)) {
						return false;
					}
				}
			}
		}

		return true;
	}

	/**
	 * @return Returns true if cookies' confidentiality is activated.
	 */
	public boolean isCookiesConfidentialityActivated() {
		return (this.avoidCookiesConfidentiality == false);
	}

	/**
	 * @param avoidCookiesConfidentiality
	 *            the avoidCookiesConfidentiality to set
	 */
	public void setAvoidCookiesConfidentiality(boolean avoidCookiesConfidentiality) {
		this.avoidCookiesConfidentiality = avoidCookiesConfidentiality;
	}

	/**
	 * @return Returns true if cookies' integrity is activated.
	 */
	public boolean isCookiesIntegrityActivated() {
		return (this.avoidCookiesIntegrity == false);
	}

	/**
	 * @param avoidCookiesIntegrity
	 *            the avoidCookiesIntegrity to set
	 */
	public void setAvoidCookiesIntegrity(boolean avoidCookiesIntegrity) {
		this.avoidCookiesIntegrity = avoidCookiesIntegrity;
	}

	/**
	 * @return Returns true if validation in urls without parameters is activated.
	 */
	public boolean isValidationInUrlsWithoutParamsActivated() {
		return (avoidValidationInUrlsWithoutParams == false);
	}

	/**
	 * @param avoidValidationInUrlsWithoutParams
	 *            The avoidValidationInUrlsWithoutParams to set.
	 */
	public void setAvoidValidationInUrlsWithoutParams(Boolean avoidValidationInUrlsWithoutParams) {
		this.avoidValidationInUrlsWithoutParams = avoidValidationInUrlsWithoutParams.booleanValue();
	}

	/**
	 * @param protectedExtensions
	 *            The protected extensions to set.
	 * @since HDIV 2.0
	 */
	public void setProtectedExtensions(List protectedExtensions) {

		this.protectedURLPatterns = new Hashtable();

		String currentProtectedExtension;
		for (int i = 0; i < protectedExtensions.size(); i++) {

			currentProtectedExtension = (String) protectedExtensions.get(i);
			this.protectedURLPatterns.put(currentProtectedExtension, Pattern.compile(currentProtectedExtension));
		}
	}

	public void setExcludedExtensions(List excludedExtensions) {
		this.excludedURLExtensions = new ArrayList();

		String currentExtension;
		for (int i = 0; i < excludedExtensions.size(); i++) {

			currentExtension = (String) excludedExtensions.get(i);
			this.excludedURLExtensions.add(currentExtension);
		}
	}

	/**
	 * @return Returns the protected extensions.
	 * @since HDIV 2.0
	 */
	public Hashtable getProtectedURLPatterns() {
		return protectedURLPatterns;
	}

	/**
	 * @return Returns the excluded extensions.
	 * @since HDIV 2.1.0
	 */
	public List getExcludedURLExtensions() {
		return excludedURLExtensions;
	}

	/**
	 * @return the randomName
	 */
	public boolean isRandomName() {
		return randomName;
	}

	/**
	 * @param randomName
	 *            the randomName to set
	 */
	public void setRandomName(boolean randomName) {
		this.randomName = randomName;
	}

	/**
	 * @return the strategy
	 */
	public String getStrategy() {
		return strategy;
	}

	/**
	 * @return the debugMode
	 */
	public boolean isDebugMode() {
		return debugMode;
	}

	/**
	 * @param debugMode
	 *            the debugMode to set
	 */
	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	/**
	 * @return the showErrorPageOnEditableValidation
	 */
	public boolean isShowErrorPageOnEditableValidation() {
		return showErrorPageOnEditableValidation;
	}

	/**
	 * @param showErrorPageOnEditableValidation
	 *            the showErrorPageOnEditableValidation to set
	 */
	public void setShowErrorPageOnEditableValidation(boolean showErrorPageOnEditableValidation) {
		this.showErrorPageOnEditableValidation = showErrorPageOnEditableValidation;
	}

	public String toString() {
		StringBuffer result = new StringBuffer().append("");
		result = result.append(" Confidentiality=").append(this.getConfidentiality().toString());
		result.append(" avoidCookiesIntegrity=").append(this.avoidCookiesIntegrity);
		result.append(" avoidCookiesConfidentiality=").append(this.avoidCookiesConfidentiality);
		result.append(" avoidValidationInUrlsWithoutParams=").append(this.avoidValidationInUrlsWithoutParams);
		result.append(" strategy=").append(this.getStrategy());
		result.append(" randomName=").append(this.isRandomName());
		result.append(" errorPage=").append(this.getErrorPage());
		result.append(" sessionExpiredLoginPage=").append(this.sessionExpiredLoginPage);
		result.append(" sessionExpiredHomePage=").append(this.sessionExpiredHomePage);
		result.append(" excludedExtensions=").append(this.excludedURLExtensions);
		result.append(" protectedExtensions=").append(this.getProtectedURLPatterns());
		result.append(" startPages=").append(this.startPages);
		result.append(" startParameters=").append(this.startParameters);
		result.append(" paramsWithoutValidation=").append(this.paramsWithoutValidation);
		result.append(" debugMode=").append(this.debugMode);
		result.append(" showErrorPageOnEditableValidation=").append(this.showErrorPageOnEditableValidation);

		return result.toString();
	}
}
