package org.hdiv.config;

import org.hdiv.AbstractHDIVTestCase;

public class EditableParameterValidationTest extends AbstractHDIVTestCase {

	protected void onSetUp() throws Exception {
	}

	public void testEditableParamValidator() {

		boolean exist = getConfig().existValidations();
		assertTrue(exist);

		String url = "/home";
		String parameter = "param";
		String[] values = { "<script>" };
		String dataType = "text";
		boolean result = getConfig().areEditableParameterValuesValid(url, parameter, values, dataType);

		assertFalse(result);

		dataType = "textarea";
		result = getConfig().areEditableParameterValuesValid(url, parameter, values, dataType);

		assertFalse(result);
	}

}
