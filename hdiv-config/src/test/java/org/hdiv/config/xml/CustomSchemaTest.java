package org.hdiv.config.xml;

import junit.framework.TestCase;

import org.hdiv.config.HDIVConfig;
import org.hdiv.config.HDIVValidations;
import org.hdiv.session.StateCache;
import org.hdiv.validator.Validation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CustomSchemaTest extends TestCase {

	private ApplicationContext context;

	@Override
	protected void setUp() throws Exception {

		this.context = new ClassPathXmlApplicationContext("org/hdiv/config/xml/hdiv-config-test-schema.xml");

	}

	public void testSchema() {

		Validation validation = (Validation) this.context.getBean("id1");
		assertNotNull(validation);
		System.out.println(validation.toString());
		System.out.println("-----------------------");

		HDIVConfig hdivConfig = (HDIVConfig) this.context.getBean(HDIVConfig.class);
		assertNotNull(hdivConfig);
		System.out.println(hdivConfig.toString());
		System.out.println("-----------------------");
		assertTrue(hdivConfig.isShowErrorPageOnEditableValidation());

		HDIVValidations validations = (HDIVValidations) this.context.getBean(HDIVValidations.class);
		assertNotNull(validations);
		System.out.println(validations.toString());

	}

	public void testStartPages() {

		HDIVConfig hdivConfig = (HDIVConfig) this.context.getBean(HDIVConfig.class);
		assertNotNull(hdivConfig);

		boolean result = hdivConfig.isStartPage("/onlyGet.html", "get");
		assertTrue(result);

		result = hdivConfig.isStartPage("/onlyGet.html", "post");
		assertFalse(result);
	}

	public void testExpiredSession() {

		HDIVConfig hdivConfig = (HDIVConfig) this.context.getBean(HDIVConfig.class);
		assertNotNull(hdivConfig);

		String result = hdivConfig.getSessionExpiredLoginPage();
		assertEquals("/login.html", result);

	}

	public void testNames() {

		HDIVConfig hdivConfig = (HDIVConfig) this.context.getBean(HDIVConfig.class);
		assertNotNull(hdivConfig);

		String[] names = this.context.getBeanDefinitionNames();
		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			System.out.println(name);
		}

	}

	public void testStateCache() {

		StateCache stateCache = this.context.getBean(StateCache.class);
		assertNotNull(stateCache);

	}

}
