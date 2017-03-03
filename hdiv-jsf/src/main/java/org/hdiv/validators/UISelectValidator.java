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
