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
package org.hdiv.components.support;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutcomeTarget;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * This class has been mostly copied from: com.sun.faces.renderkit.html_basic.OutcomeTargetRenderer
 * </p>
 * <p>
 * Copied method are responsible for generating the url of the link/button from the outcome of the component.
 * </p>
 * <p>
 * Only for JSF 2.0+
 * </p>
 * @author Gotzon Illarramendi
 */
public class OutcomeTargetComponentHelper {

	private static Log log = LogFactory.getLog(OutcomeTargetComponentHelper.class);

	protected static final Param[] EMPTY_PARAMS = new Param[0];

	/**
	 * Returns the url that the component would generate if it was a UIOutcomeTarget
	 * @param context {@link FacesContext} instance
	 * @param component {@link UIOutcomeTarget} instance
	 * @return the url
	 */
	public String getUrl(FacesContext context, UIOutcomeTarget component) {

		NavigationCase navCase = getNavigationCase(context, component);
		String url = getEncodedTargetURL(context, component, navCase);

		return url;
	}

	/**
	 * Invoke the {@link NavigationHandler} preemptively to resolve a {@link NavigationCase} for the outcome declared on
	 * the {@link UIOutcomeTarget} component. The current view id is used as the from-view-id when matching navigation
	 * cases and the from-action is assumed to be null.
	 *
	 * @param context the {@link FacesContext} for the current request
	 * @param component the target {@link UIComponent}
	 *
	 * @return the NavigationCase represeting the outcome target
	 */
	protected NavigationCase getNavigationCase(FacesContext context, UIComponent component) {
		NavigationHandler navHandler = context.getApplication().getNavigationHandler();
		if (!(navHandler instanceof ConfigurableNavigationHandler)) {
			// if (logger.isLoggable(Level.WARNING)) {
			// logger.log(Level.WARNING,
			// "jsf.outcome.target.invalid.navigationhandler.type",
			// component.getId());
			// }
			log.warn("jsf.outcome.target.invalid.navigationhandler.type Componente:" + component.getId());
			return null;
		}

		String outcome = ((UIOutcomeTarget) component).getOutcome();
		if (outcome == null) {
			outcome = context.getViewRoot().getViewId();
			// QUESTION should we avoid the call to getNavigationCase() and instead instantiate one explicitly?
			// String viewId = context.getViewRoot().getViewId();
			// return new NavigationCase(viewId, null, null, null, viewId, false, false);
		}
		NavigationCase navCase = ((ConfigurableNavigationHandler) navHandler).getNavigationCase(context, null, outcome);
		if (navCase == null) {
			// if (logger.isLoggable(Level.WARNING)) {
			// logger.log(Level.WARNING,
			// "jsf.outcometarget.navigation.case.not.resolved",
			// component.getId());
			// }
			log.warn("jsf.outcometarget.navigation.case.not.resolved Componente:" + component.getId());
		}
		return navCase;
	}

	/**
	 * <p>
	 * Resolve the target view id and then delegate to
	 * {@link ViewHandler#getBookmarkableURL(javax.faces.context.FacesContext, String, java.util.Map, boolean)} to
	 * produce a redirect URL, which will add the page parameters if necessary and properly prioritizing the parameter
	 * overrides.
	 * </p>
	 *
	 * @param context the {@link FacesContext} for the current request
	 * @param component the target {@link UIComponent}
	 * @param navCase the target navigation case
	 *
	 * @return an encoded URL for the provided navigation case
	 */
	protected String getEncodedTargetURL(FacesContext context, UIComponent component, NavigationCase navCase) {
		// FIXME getNavigationCase doesn't resolve the target viewId (it is part of CaseStruct)
		String toViewId = navCase.getToViewId(context);
		Map<String, List<String>> params = getParamOverrides(component);
		addNavigationParams(navCase, params);
		return context.getApplication().getViewHandler()
				.getBookmarkableURL(context, toViewId, params, isIncludeViewParams(component, navCase));
	}

	protected boolean isIncludeViewParams(UIComponent component, NavigationCase navcase) {

		return (((UIOutcomeTarget) component).isIncludeViewParams() || navcase.isIncludeViewParams());

	}

	protected Map<String, List<String>> getParamOverrides(UIComponent component) {
		Map<String, List<String>> params = new LinkedHashMap<String, List<String>>();
		Param[] declaredParams = getParamList(component);
		for (Param candidate : declaredParams) {
			// QUESTION shouldn't the trimming of name should be done elsewhere?
			// null value is allowed as a way to suppress page parameter
			if (candidate.name != null && candidate.name.trim().length() > 0) {
				candidate.name = candidate.name.trim();
				List<String> values = params.get(candidate.name);
				if (values == null) {
					values = new ArrayList<String>();
					params.put(candidate.name, values);
				}
				values.add(candidate.value);
			}
		}

		return params;
	}

	/**
	 * @param command the command which may have parameters
	 *
	 * @return an array of parameters
	 */
	protected Param[] getParamList(UIComponent command) {

		if (command.getChildCount() > 0) {
			ArrayList<Param> parameterList = new ArrayList<Param>();

			for (UIComponent kid : command.getChildren()) {
				if (kid instanceof UIParameter) {
					UIParameter uiParam = (UIParameter) kid;
					if (!uiParam.isDisable()) {
						Object value = uiParam.getValue();
						Param param = new Param(uiParam.getName(), (value == null ? null : value.toString()));
						parameterList.add(param);
					}
				}
			}
			return parameterList.toArray(new Param[parameterList.size()]);
		}
		else {
			return EMPTY_PARAMS;
		}

	}

	protected void addNavigationParams(NavigationCase navCase, Map<String, List<String>> existingParams) {

		Map<String, List<String>> navParams = navCase.getParameters();
		if (navParams == null || navParams.isEmpty()) {
			return;
		}
		for (Map.Entry<String, List<String>> entry : navParams.entrySet()) {
			String navParamName = entry.getKey();
			// only add the navigation params to the existing params collection
			// if the parameter name isn't already present within the existing
			// collection
			if (!existingParams.containsKey(navParamName)) {
				existingParams.put(navParamName, entry.getValue());
			}
		}

	}

	/**
	 * <p>
	 * Simple class to encapsulate the name and value of a <code>UIParameter</code>.
	 */
	public static class Param {

		public String name;

		public String value;

		// -------------------------------------------------------- Constructors

		public Param(String name, String value) {

			this.name = name;
			this.value = value;

		}

	}

}
