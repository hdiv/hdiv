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
package org.hdiv.services;

import java.util.Collection;
import java.util.Map;

/**
 * Represents a descriptor for a http method execution. Created by Dietrich on 17.05.2015.
 */
public interface ActionDescriptor {

	/**
	 * Gets action name. Could be used as form name.
	 *
	 * @return name
	 */
	String getActionName();

	/**
	 * Gets method on uniform interface.
	 *
	 * @return method
	 */
	String getHttpMethod();

	/**
	 * Gets contentType consumed by the action
	 * 
	 * @return consumes
	 */
	String getConsumes();

	/**
	 * Gets contentType produced by the action
	 * 
	 * @return produces
	 */
	String getProduces();

	/**
	 * Gets names of path variables, if URL has variables.
	 *
	 * @return names or empty collection
	 */
	Collection<String> getPathVariableNames();

	/**
	 * Gets names of expected request headers, if any.
	 *
	 * @return names or empty collection
	 */
	Collection<String> getRequestHeaderNames();

	/**
	 * Gets names of expected request parameters, if any.
	 *
	 * @return names or empty collection
	 */
	Collection<String> getRequestParamNames();

	/**
	 * Gets action parameter by name.
	 *
	 * @param name parameter name
	 * @return parameter
	 */
	ActionInputParameter getActionInputParameter(String name);

	/**
	 * Request body descriptor, if the action expects a complex request body.
	 *
	 * @return request body parameter
	 */
	ActionInputParameter getRequestBody();

	/**
	 * Does the action expect a complex request body?
	 *
	 * @return true if applicable
	 */
	boolean hasRequestBody();

	/**
	 * Gets well-defined semantic action type, e.g. http://schema.org/Action subtype.
	 *
	 * @return semantic action type
	 */
	String getSemanticActionType();

	/**
	 * Gets required parameters.
	 *
	 * @return required parameters, may be empty
	 */
	Map<String, ActionInputParameter> getRequiredParameters();

	/**
	 * Hints if the action response is a single object or a collection.
	 *
	 * @return cardinality cardinality
	 */
	Cardinality getCardinality();

	/**
	 * Visits the body to find parameters
	 * 
	 * @param visitor visitor
	 */
	void accept(ActionInputParameterVisitor visitor);
}
