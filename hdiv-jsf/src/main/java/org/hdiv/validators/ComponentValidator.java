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
package org.hdiv.validators;

import jakarta.faces.component.UIComponent;

import org.hdiv.validation.ValidationContext;

/**
 * The class that implements this interface will handle the validation of a component type. Implementation must be stateless as it is
 * singleton.
 * 
 * @author Gotzon Illarramendi
 */
public interface ComponentValidator {

	/**
	 * @param component {@link UIComponent} to validate.
	 * @return true if this {@link ComponentValidator} can validate the component.
	 */
	boolean supports(UIComponent component);

	/**
	 * Validates a component
	 * 
	 * @param context Validation context
	 * @param component Component to validate
	 */
	void validate(ValidationContext context, UIComponent component);
}
