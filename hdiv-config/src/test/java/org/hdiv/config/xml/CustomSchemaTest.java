package org.hdiv.config.xml;

import junit.framework.TestCase;

import org.hdiv.config.HDIVConfig;
import org.hdiv.config.HDIVValidations;
import org.hdiv.validator.Validation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CustomSchemaTest extends TestCase {

	public void testSchema() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"org/hdiv/config/xml/hdiv-config-test-schema.xml");

		Validation validation = (Validation) context.getBean("id1");
		assertNotNull(validation);
		System.out.println(validation.toString());
		System.out.println("-----------------------");

		HDIVConfig hdivConfig = (HDIVConfig) context.getBean("config");
		assertNotNull(hdivConfig);
		System.out.println(hdivConfig.toString());
		System.out.println("-----------------------");
		assertTrue(hdivConfig.isShowErrorPageOnEditableValidation());

		HDIVValidations validations = (HDIVValidations) context.getBean("editableParametersValidations");
		assertNotNull(validations);
		System.out.println(validations.toString());

	}

	public void testStartPages() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"org/hdiv/config/xml/hdiv-config-test-schema.xml");

		HDIVConfig hdivConfig = (HDIVConfig) context.getBean("config");
		assertNotNull(hdivConfig);

		boolean result = hdivConfig.isStartPage("/login.html", "get");
		assertTrue(result);

		result = hdivConfig.isStartPage("/login.html", "post");
		assertFalse(result);
	}

	public void testExpiredSession() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"org/hdiv/config/xml/hdiv-config-test-schema.xml");

		HDIVConfig hdivConfig = (HDIVConfig) context.getBean("config");
		assertNotNull(hdivConfig);

		String result = hdivConfig.getSessionExpiredLoginPage();
		assertEquals("/login.html", result);

	}

}
