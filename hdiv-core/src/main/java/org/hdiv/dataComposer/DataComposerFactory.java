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
package org.hdiv.dataComposer;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.config.HDIVConfig;
import org.hdiv.config.Strategy;
import org.hdiv.context.RequestContext;
import org.hdiv.exception.HDIVException;
import org.hdiv.idGenerator.UidGenerator;
import org.hdiv.session.ISession;
import org.hdiv.state.IPage;
import org.hdiv.state.IState;
import org.hdiv.state.StateUtil;
import org.hdiv.state.scope.StateScopeManager;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;

/**
 * DataComposer object factory, more efficient than to use the Spring factory.
 * 
 * @author Gotzon Illarramendi
 * @since HDIV 2.1.0
 */
public class DataComposerFactory {

	private static final int DEFAULT_ALLOWED_LENGTH = 4000;

	/**
	 * HDIV configuration object.
	 */
	protected HDIVConfig config;

	/**
	 * Http session wrapper
	 */
	protected ISession session;

	/**
	 * Unique Id generator
	 */
	protected UidGenerator uidGenerator;

	/**
	 * Maximum size allowed to represent page state
	 */
	protected int allowedLength = DEFAULT_ALLOWED_LENGTH;

	/**
	 * State management utility
	 */
	protected StateUtil stateUtil;

	/**
	 * State scope manager.
	 */
	protected StateScopeManager stateScopeManager;

	/**
	 * HTTP headers to exclude page reuse in Ajax requests.
	 */
	protected List<String> excludePageReuseHeaders = Arrays.asList("X-PJAX", "X-HDIV-EXCLUDE-PAGE-REUSE");

	/**
	 * Creates a new instance of DataComposer based on the defined strategy.
	 * 
	 * @param request
	 *            {@link HttpServletRequest} instance
	 * 
	 * @return IDataComposer instance
	 */
	public IDataComposer newInstance(HttpServletRequest request) {

		IDataComposer dataComposer = null;

		RequestContext context = new RequestContext(request);

		if (this.config.getStrategy().equals(Strategy.MEMORY)) {
			DataComposerMemory composer = new DataComposerMemory(context);
			composer.setHdivConfig(this.config);
			composer.setSession(this.session);
			composer.setUidGenerator(this.uidGenerator);
			composer.setStateScopeManager(this.stateScopeManager);
			composer.init();
			dataComposer = composer;

		} else {
			String errorMessage = HDIVUtil.getMessage(request, "strategy.error", this.config.getStrategy().toString());
			throw new HDIVException(errorMessage);
		}

		this.initDataComposer(dataComposer, context);

		return dataComposer;
	}

	/**
	 * Initialize IDataComposer instance.
	 * 
	 * @param dataComposer
	 *            IDataComposer instance
	 * @param context
	 *            current request context
	 */
	protected void initDataComposer(IDataComposer dataComposer, RequestContext context) {

		HttpServletRequest request = context.getRequest();
		String hdivStateParamName = (String) request.getSession().getAttribute(Constants.HDIV_PARAMETER);
		String hdivState = request.getParameter(hdivStateParamName);

		String preState = this.getModifyStateParameterValue(dataComposer, request);

		if (preState != null && preState.length() > 0) {

			// We are modifying an existing state, preload dataComposer with it
			IState state = this.stateUtil.restoreState(context, preState);
			if (state.getPageId() > 0) {
				IPage page = this.session.getPage(context, state.getPageId());
				if (page != null) {
					dataComposer.startPage(page);
				}
			}
			if (state != null) {
				dataComposer.beginRequest(state);
			}

		} else if (this.reuseExistingPage(request)) {

			if (hdivState != null && hdivState.length() > 0) {
				IState state = this.stateUtil.restoreState(context, hdivState);
				if (state.getPageId() > 0) {
					IPage page = this.session.getPage(context, state.getPageId());
					dataComposer.startPage(page);
				} else {
					dataComposer.startPage(hdivState);
				}
			} else {
				dataComposer.startPage(hdivState);
			}
		} else {
			dataComposer.startPage(hdivState);
		}

		// Detect if request url is configured as a long living page
		String url = request.getRequestURI().substring(request.getContextPath().length());
		String scope = this.config.isLongLivingPages(url);
		if (scope != null) {
			dataComposer.startScope(scope);
		}
	}

	/**
	 * Get _MODIFY_HDIV_STATE_ parameter value.
	 * 
	 * @param dataComposer
	 *            IDataComposer instance
	 * @param request
	 *            current HttpServletRequest instance
	 * @return parameter value.
	 */
	protected String getModifyStateParameterValue(IDataComposer dataComposer, HttpServletRequest request) {

		String paramName = (String) request.getSession().getAttribute(Constants.MODIFY_STATE_HDIV_PARAMETER);
		String preState = paramName != null ? request.getParameter(paramName) : null;
		return preState;
	}

	/**
	 * Is it necessary to create a new Page or reuse existing Page adding the created states to it?
	 * 
	 * @param request
	 *            current HttpServletRequest instance
	 * @return reuse or not
	 */
	protected boolean reuseExistingPage(HttpServletRequest request) {

		if (isAjaxRequest(request)) {

			if (excludePageReuseInAjax(request)) {
				return false;
			}
			// Decides based on user settings
			return this.config.isReuseExistingPageInAjaxRequest();
		}
		return false;
	}

	/**
	 * Checks if request is an ajax request and store the result in a request's attribute
	 * 
	 * @param request
	 *            the HttpServletRequest
	 * 
	 * @return isAjaxRquest
	 */
	protected boolean isAjaxRequest(HttpServletRequest request) {

		String xRequestedWithValue = request.getHeader("x-requested-with");

		boolean isAjaxRequest = (xRequestedWithValue != null) ? "XMLHttpRequest".equalsIgnoreCase(xRequestedWithValue)
				: false;

		request.setAttribute(Constants.AJAX_REQUEST, isAjaxRequest);

		return isAjaxRequest;
	}

	protected boolean excludePageReuseInAjax(HttpServletRequest request) {

		for (String header : this.excludePageReuseHeaders) {
			String headerValue = request.getHeader(header);
			if (headerValue != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param config
	 *            the hdivConfig to set
	 */
	public void setConfig(HDIVConfig config) {
		this.config = config;
	}

	/**
	 * @param session
	 *            the session to set
	 */
	public void setSession(ISession session) {
		this.session = session;
	}

	/**
	 * @param uidGenerator
	 *            the uidGenerator to set
	 */
	public void setUidGenerator(UidGenerator uidGenerator) {
		this.uidGenerator = uidGenerator;
	}

	/**
	 * @param allowedLength
	 *            the allowedLength to set
	 */
	public void setAllowedLength(int allowedLength) {
		this.allowedLength = allowedLength;
	}

	/**
	 * @param stateUtil
	 *            the stateUtil to set
	 */
	public void setStateUtil(StateUtil stateUtil) {
		this.stateUtil = stateUtil;
	}

	/**
	 * @param stateScopeManager
	 *            the stateScopeManager to set
	 */
	public void setStateScopeManager(StateScopeManager stateScopeManager) {
		this.stateScopeManager = stateScopeManager;
	}

	/**
	 * @param excludePageReuseHeaders
	 *            the excludePageReuseHeaders to set
	 */
	public void setExcludePageReuseHeaders(List<String> excludePageReuseHeaders) {
		this.excludePageReuseHeaders = excludePageReuseHeaders;
	}

}