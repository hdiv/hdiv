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

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectBoolean;
import javax.faces.component.UISelectMany;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;

import org.hdiv.validation.ValidationContext;

public class UISelectValidator implements ComponentValidator {

	public boolean supports(final UIComponent component) {

		boolean typeMatch = UISelectOne.class.isAssignableFrom(component.getClass()) || //
				UISelectBoolean.class.isAssignableFrom(component.getClass()) || //
				UISelectMany.class.isAssignableFrom(component.getClass());
		return typeMatch && component.getFamily().startsWith("javax.faces.");
	}

	public void validate(final ValidationContext validationContext, final UIComponent component) {

		// UISelect component values integrity is validated in the validation phase by the component itself
		// javax.faces.component.UISelectOne.validateValue(FacesContext, Object)

		FacesContext context = validationContext.getFacesContext();
		String clientId = component.getClientId(context);
		Object value = context.getExternalContext().getRequestParameterMap().get(clientId);

		validationContext.acceptParameter(clientId, value);
	}

}
