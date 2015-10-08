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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.state.IState;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.util.UtilsJsf;

/**
 * <p>
 * JSF's ValidatorHelper.
 * </p>
 * <p>
 * Differences with core's validation helper:
 * </p>
 * <ul>
 * <li>
 * If it is a JSF request (with JSF state) doesn't do any validation. If it is a HDIV request (with HDIV state)
 * delegates validation to core's validation helper.</li>
 * <li>
 * isTheSameAction() method is overwritten because action checking changes.</li>
 * <li>
 * addParameterToRequest(..) method is overwritten because in the JSF version confidentiality is disabled and
 * RequestWrapper is not used. Consequently, no parameter is added to the request.</li>
 * </ul>
 * 
 * @author Gotzon Illarramendi
 */
public class JsfValidatorHelper extends ValidatorHelperRequest {

	/**
	 * Commons Logging instance.
	 */
	private static Log log = LogFactory.getLog(JsfValidatorHelper.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.filter.ValidatorHelperRequest#preValidate(javax.servlet.http.HttpServletRequest, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	protected ValidatorHelperResult preValidate(HttpServletRequest request, String target) {

		if (log.isDebugEnabled()) {
			log.debug("URI: " + request.getRequestURI());
		}

		Map<String, Object> requestMap = request.getParameterMap();

		boolean isViewState = UtilsJsf.hasFacesViewParamName(requestMap.keySet());
		if (isViewState) {
			// Contains parameter with JSF state, it is a JSF request.
			if (log.isDebugEnabled()) {
				log.debug("Request contains view state");
			}

			if (this.hdivConfig.isStartPage(target, request.getMethod())) {
				// It is an init page
				if (log.isDebugEnabled()) {
					log.debug("Request is start page");
				}
				return ValidatorHelperResult.VALIDATION_NOT_REQUIRED;
			}

			return ValidatorHelperResult.VALID;
		}

		// Delegate to ValidatorHelperRequest
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.filter.ValidatorHelperRequest#isTheSameAction(javax.servlet.http.HttpServletRequest,
	 * java.lang.String, org.hdiv.state.IState)
	 */
	@Override
	public ValidatorHelperResult isTheSameAction(HttpServletRequest request, String target, IState state) {

		// First check if target and action are the same
		// When outputlink is used target matches action
		if (state.getAction().equalsIgnoreCase(target)) {
			return ValidatorHelperResult.VALID;
		}

		if (target.endsWith("/")) {
			String actionSlash = state.getAction() + "/";
			if (actionSlash.equalsIgnoreCase(target)) {
				return ValidatorHelperResult.VALID;
			}
		}

		// In the case of <h:link> component, action may not have context-path and servlet mapping.
		// e.g. action=/view/viewAccount, target=/hdiv-jsf/view/viewAccount.faces and
		// targetWithoutContextPath = /view/viewAccount.faces

		String targetWithoutServletAndContextPath = target.substring(0, target.indexOf("."));
		boolean isActionState = state.getAction().equalsIgnoreCase(targetWithoutServletAndContextPath);
		if (isActionState) {
			return ValidatorHelperResult.VALID;
		}

		// In other case, <h:link> component may have context path but not servlet mapping
		String targetWithoutServlet = target.substring(0, target.indexOf("."));
		isActionState = state.getAction().equalsIgnoreCase(targetWithoutServlet);
		if (isActionState) {
			return ValidatorHelperResult.VALID;
		}

		if (log.isDebugEnabled()) {
			log.debug("isTheSameAction=false");
			log.debug(" target:" + target);
			log.debug(" state action:" + state.getAction());
		}

		ValidatorError error = new ValidatorError(HDIVErrorCodes.ACTION_ERROR, target);
		return new ValidatorHelperResult(error);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.filter.ValidatorHelperRequest#addParameterToRequest(javax.servlet.http.HttpServletRequest,
	 * java.lang.String, java.lang.Object)
	 */
	protected void addParameterToRequest(HttpServletRequest request, String name, Object value) {
		throw new IllegalStateException("Confidentiality is not implemented in JSF.");
	}

}