/**
 * Copyright 2005-2013 hdiv.org
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
package org.hdiv.config.xml;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.hdiv.config.HDIVConfig;
import org.hdiv.config.HDIVValidations;
import org.hdiv.logs.IUserData;
import org.hdiv.regex.DefaultPatternMatcher;
import org.hdiv.regex.PatternMatcher;
import org.hdiv.session.StateCache;
import org.hdiv.state.scope.StateScope;
import org.hdiv.state.scope.StateScopeManager;
import org.hdiv.validator.IValidation;
import org.hdiv.validator.Validation;
import org.hdiv.web.servlet.support.HdivRequestDataValueProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.web.servlet.support.csrf.CsrfRequestDataValueProcessor;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

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

		HDIVConfig hdivConfig = this.context.getBean(HDIVConfig.class);
		assertNotNull(hdivConfig);
		System.out.println(hdivConfig.toString());
		System.out.println("-----------------------");
		assertTrue(hdivConfig.isShowErrorPageOnEditableValidation());

		HDIVValidations validations = this.context.getBean(HDIVValidations.class);
		assertNotNull(validations);
		System.out.println(validations.toString());

	}

	public void testStartPages() {

		HDIVConfig hdivConfig = this.context.getBean(HDIVConfig.class);
		assertNotNull(hdivConfig);

		boolean result = hdivConfig.isStartPage("/onlyGet.html", "get");
		assertTrue(result);

		result = hdivConfig.isStartPage("/onlyGet.html", "post");
		assertFalse(result);
	}

	public void testExpiredSession() {

		HDIVConfig hdivConfig = this.context.getBean(HDIVConfig.class);
		assertNotNull(hdivConfig);

		String result = hdivConfig.getSessionExpiredLoginPage();
		assertEquals("/login.html", result);

	}

	public void testNames() {

		HDIVConfig hdivConfig = this.context.getBean(HDIVConfig.class);
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

	public void testUserData() {

		IUserData userData = (IUserData) this.context.getBean(ConfigBeanDefinitionParser.USER_DATA_NAME);
		assertNotNull(userData);
		assertTrue(userData instanceof TestUserData);

	}

	public void testEditableValidations() {

		HDIVValidations validations = this.context.getBean(HDIVValidations.class);
		assertNotNull(validations);

		Map<PatternMatcher, List<IValidation>> urls = validations.getUrls();
		assertEquals(2, urls.size());

		// First url
		List<IValidation> vals = urls.get(new DefaultPatternMatcher("a"));

		assertEquals(1, vals.size());
		// 1 custom rules
		Validation val = (Validation) vals.get(0);
		assertEquals("id1", val.getName());

		// Second url
		vals = urls.get(new DefaultPatternMatcher("b"));
		assertEquals(8, vals.size());
		// 2 custom rule + 6 default rules

		val = (Validation) vals.get(0);
		assertEquals("id2", val.getName());
		val = (Validation) vals.get(1);
		assertEquals("id3", val.getName());
		val = (Validation) vals.get(2);
		assertEquals("SQLInjection", val.getName());// first default rule
	}

	public void testReuseExistingPageInAjaxRequest() {

		HDIVConfig hdivConfig = this.context.getBean(HDIVConfig.class);
		assertNotNull(hdivConfig);

		assertEquals(true, hdivConfig.isReuseExistingPageInAjaxRequest());

	}

	public void testRequestDataValueProcessor() {

		HdivRequestDataValueProcessor processor = this.context.getBean(HdivRequestDataValueProcessor.class);
		assertNotNull(processor);

		// Spring security 'CsrfRequestDataValueProcessor' as inner processor.
		RequestDataValueProcessor inner = processor.getInnerRequestDataValueProcessor();
		assertNotNull(inner);

		assertEquals(CsrfRequestDataValueProcessor.class, inner.getClass());
	}

	public void testStateScopeManager() {

		StateScopeManager scopeManager = this.context.getBean(StateScopeManager.class);
		assertNotNull(scopeManager);

		StateScope appScope = scopeManager.getStateScopeByName("app");
		StateScope sessionScope = scopeManager.getStateScopeByName("user-session");

		assertNotNull(appScope);
		assertNotNull(sessionScope);
	}

	public void testIsLongLivingPages() {

		HDIVConfig hdivConfig = this.context.getBean(HDIVConfig.class);
		assertNotNull(hdivConfig);

		String result = hdivConfig.isLongLivingPages("/default.html");
		assertEquals("user-session", result);

		result = hdivConfig.isLongLivingPages("/user.html");
		assertEquals("user-session", result);

		result = hdivConfig.isLongLivingPages("/app.html");
		assertEquals("app", result);

		result = hdivConfig.isLongLivingPages("/other.html");
		assertNull(result);
	}

}
