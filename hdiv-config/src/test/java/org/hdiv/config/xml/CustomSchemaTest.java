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
package org.hdiv.config.xml;

import java.util.List;
import java.util.Map;

import org.hdiv.config.HDIVConfig;
import org.hdiv.logs.IUserData;
import org.hdiv.regex.DefaultPatternMatcher;
import org.hdiv.regex.PatternMatcher;
import org.hdiv.session.StateCache;
import org.hdiv.state.scope.StateScope;
import org.hdiv.state.scope.StateScopeManager;
import org.hdiv.state.scope.StateScopeType;
import org.hdiv.util.Method;
import org.hdiv.validator.DefaultValidationRepository;
import org.hdiv.validator.EditableDataValidationProvider;
import org.hdiv.validator.EditableDataValidationResult;
import org.hdiv.validator.IValidation;
import org.hdiv.validator.Validation;
import org.hdiv.validator.ValidationTarget;
import org.hdiv.web.servlet.support.HdivRequestDataValueProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.web.servlet.support.csrf.CsrfRequestDataValueProcessor;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

import junit.framework.TestCase;

public class CustomSchemaTest extends TestCase {

	private ApplicationContext context;

	@Override
	protected void setUp() throws Exception {

		context = new ClassPathXmlApplicationContext("org/hdiv/config/xml/hdiv-config-test-schema.xml");
	}

	public void testSchema() {

		Validation validation = (Validation) context.getBean("id1");
		assertNotNull(validation);
		System.out.println(validation.toString());
		System.out.println("-----------------------");

		HDIVConfig hdivConfig = context.getBean(HDIVConfig.class);
		assertNotNull(hdivConfig);
		System.out.println(hdivConfig.toString());
		System.out.println("-----------------------");
		assertTrue(hdivConfig.isShowErrorPageOnEditableValidation());
		assertFalse(hdivConfig.isMultipartIntegration());

		EditableDataValidationProvider validationProvider = context.getBean(EditableDataValidationProvider.class);
		assertNotNull(validationProvider);
		System.out.println(validationProvider.toString());

	}

	public void testStartPages() {

		HDIVConfig hdivConfig = context.getBean(HDIVConfig.class);
		assertNotNull(hdivConfig);

		boolean result = hdivConfig.isStartPage("/onlyGet.html", Method.GET);
		assertTrue(result);

		result = hdivConfig.isStartPage("/onlyGet.html", Method.POST);
		assertFalse(result);
	}

	public void testParamsWithoutValidation() {

		HDIVConfig hdivConfig = context.getBean(HDIVConfig.class);
		assertNotNull(hdivConfig);

		assertTrue(hdivConfig.isParameterWithoutValidation("/page1", "param1"));
		assertTrue(hdivConfig.isParameterWithoutValidation("/page1", "param2"));
		assertTrue(hdivConfig.isParameterWithoutValidation("/page1", "param3"));
		assertTrue(hdivConfig.isParameterWithoutValidation("/page2", "param3"));
		assertTrue(hdivConfig.isParameterWithoutValidation("/page2", "param4"));

		assertFalse(hdivConfig.isParameterWithoutValidation("/other", "param1"));
	}

	public void testExpiredSession() {

		HDIVConfig hdivConfig = context.getBean(HDIVConfig.class);
		assertNotNull(hdivConfig);

		String result = hdivConfig.getSessionExpiredLoginPage();
		assertEquals("/login.html", result);

	}

	public void testNames() {

		HDIVConfig hdivConfig = context.getBean(HDIVConfig.class);
		assertNotNull(hdivConfig);

		String[] names = context.getBeanDefinitionNames();
		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			System.out.println(name);
		}

	}

	public void testStateCache() {

		StateCache stateCache = context.getBean(StateCache.class);
		assertNotNull(stateCache);

	}

	public void testUserData() {

		IUserData userData = (IUserData) context.getBean(ConfigBeanDefinitionParser.USER_DATA_NAME);
		assertNotNull(userData);
		assertTrue(userData instanceof TestUserData);

	}

	public void testEditableValidations() {

		DefaultValidationRepository validationRepository = context.getBean(DefaultValidationRepository.class);
		assertNotNull(validationRepository);

		Map<ValidationTarget, List<IValidation>> validations = validationRepository.getValidations();
		assertEquals(4, validations.size());

		// First url
		List<IValidation> vals = getValidations(validations, "a");
		ValidationTarget target = getTarget(validations, "a");
		assertEquals(0, vals.size());
		assertNull(target.getParams());

		// Second url
		vals = getValidations(validations, "b");
		target = getTarget(validations, "b");
		List<PatternMatcher> params = target.getParams();
		assertEquals(3, params.size());
		assertEquals(new DefaultPatternMatcher("param1"), params.get(0));
		assertEquals(new DefaultPatternMatcher("param2"), params.get(1));
		assertEquals(new DefaultPatternMatcher("param3"), params.get(2));

		assertEquals(1, vals.size());
		// 1 custom rules
		Validation val = (Validation) vals.get(0);
		assertEquals("id1", val.getName());
		assertFalse(val.isDefaultValidation());

		// Third url
		vals = getValidations(validations, "c");
		target = getTarget(validations, "c");
		assertNull(target.getParams());
		assertEquals(2, vals.size());
		// 2 custom rule

		val = (Validation) vals.get(0);
		assertEquals("id2", val.getName());
		val = (Validation) vals.get(1);
		assertEquals("id3", val.getName());

		// Fourth url
		vals = getValidations(validations, null);
		target = getTarget(validations, null);
		params = target.getParams();
		assertEquals(2, params.size());
		assertEquals(new DefaultPatternMatcher("param4"), params.get(0));
		assertEquals(new DefaultPatternMatcher("param5"), params.get(1));
		assertEquals(1, vals.size());
		// 1 custom rule

		val = (Validation) vals.get(0);
		assertEquals("id3", val.getName());
		assertFalse(val.isDefaultValidation());
	}

	public void testEditableValidationsOrder() {

		DefaultValidationRepository validationRepository = context.getBean(DefaultValidationRepository.class);
		assertNotNull(validationRepository);

		Map<ValidationTarget, List<IValidation>> validations = validationRepository.getValidations();
		assertEquals(4, validations.size());

		Object[] ptrs = validations.keySet().toArray();

		ValidationTarget vt0 = (ValidationTarget) ptrs[0];
		ValidationTarget vt1 = (ValidationTarget) ptrs[1];
		ValidationTarget vt2 = (ValidationTarget) ptrs[2];
		ValidationTarget vt3 = (ValidationTarget) ptrs[3];

		assertEquals(new DefaultPatternMatcher("a"), vt0.getUrl());
		assertEquals(new DefaultPatternMatcher("b"), vt1.getUrl());
		assertEquals(new DefaultPatternMatcher("c"), vt2.getUrl());
		assertEquals(null, vt3.getUrl());
	}

	public void testEditableValidationsParams() {

		HDIVConfig config = context.getBean(HDIVConfig.class);

		// param1
		String url = "b";
		String parameter = "param1";
		String[] values = { "<script>" };
		String dataType = "text";

		EditableDataValidationProvider provider = config.getEditableDataValidationProvider();
		EditableDataValidationResult result = provider.validate(url, parameter, values, dataType);

		assertFalse(result.isValid());

		// param2
		parameter = "param2";
		result = provider.validate(url, parameter, values, dataType);

		assertFalse(result.isValid());

		// otherParam
		parameter = "otherParam";
		result = provider.validate(url, parameter, values, dataType);

		assertTrue(result.isValid());
	}

	public void testReuseExistingPageInAjaxRequest() {

		HDIVConfig hdivConfig = context.getBean(HDIVConfig.class);
		assertNotNull(hdivConfig);

		assertEquals(true, hdivConfig.isReuseExistingPageInAjaxRequest());

	}

	public void testRequestDataValueProcessor() {

		HdivRequestDataValueProcessor processor = context.getBean(HdivRequestDataValueProcessor.class);
		assertNotNull(processor);

		// Spring security 'CsrfRequestDataValueProcessor' as inner processor.
		RequestDataValueProcessor inner = processor.getInnerRequestDataValueProcessor();
		assertNotNull(inner);

		assertEquals(CsrfRequestDataValueProcessor.class, inner.getClass());
	}

	public void testStateScopeManager() {

		StateScopeManager scopeManager = context.getBean(StateScopeManager.class);
		assertNotNull(scopeManager);

		StateScope appScope = scopeManager.getStateScope(StateScopeType.APP);
		StateScope sessionScope = scopeManager.getStateScope(StateScopeType.USER_SESSION);

		assertNotNull(appScope);
		assertNotNull(sessionScope);
	}

	public void testIsLongLivingPages() {

		HDIVConfig hdivConfig = context.getBean(HDIVConfig.class);
		assertNotNull(hdivConfig);

		StateScopeType result = hdivConfig.isLongLivingPages("/default.html");
		assertEquals(StateScopeType.USER_SESSION, result);

		result = hdivConfig.isLongLivingPages("/user.html");
		assertEquals(StateScopeType.USER_SESSION, result);

		result = hdivConfig.isLongLivingPages("/app.html");
		assertEquals(StateScopeType.APP, result);

		result = hdivConfig.isLongLivingPages("/other.html");
		assertNull(result);
	}

	protected List<IValidation> getValidations(final Map<ValidationTarget, List<IValidation>> validations, final String pattern) {

		for (ValidationTarget target : validations.keySet()) {
			PatternMatcher urlPattern = target.getUrl();
			if (pattern == null) {
				if (urlPattern == null) {
					return validations.get(target);
				}
			}
			else if (urlPattern.matches(pattern)) {
				return validations.get(target);
			}
		}
		return null;
	}

	protected ValidationTarget getTarget(final Map<ValidationTarget, List<IValidation>> validations, final String pattern) {

		for (ValidationTarget target : validations.keySet()) {
			PatternMatcher urlPattern = target.getUrl();
			if (pattern == null) {
				if (urlPattern == null) {
					return target;
				}
			}
			else if (target.getUrl().matches(pattern)) {
				return target;
			}
		}
		return null;
	}

}
