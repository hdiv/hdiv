/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hdiv.dataValidator;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.state.IParameter;
import org.hdiv.state.IState;
import org.hdiv.state.Parameter;
import org.hdiv.state.State;

/**
 * Unit tests for the <code>org.hdiv.dataValidator.DataValidator</code> class.
 * 
 * @author Gorka Vicente
 */
public class DataValidatorTest extends AbstractHDIVTestCase {
	
	protected DataValidatorFactory dataValidatorFactory;

	protected IDataComposer composer;
	
	protected void onSetUp() throws Exception {
		
		this.dataValidatorFactory = (DataValidatorFactory) this.getApplicationContext().getBean("dataValidatorFactory");
	}

	/**
	 * Validation test with a noneditable parameter. It should not pass the
	 * validation as the received value is not an integer.
	 */
	public void testValidateDataIsNotInt() {
		
		IState state = new State();
		IDataValidator validator = this.dataValidatorFactory.newInstance(state);
		
		IParameter param1 = new Parameter();
		param1.addValue("value1");
		param1.setName("param1");
		param1.setEditable(false);		
		
		state.addParameter("param1", param1);
		
		validator.setState(state);
		
		IValidationResult result = validator.validate("dataIsNotInt", "simpleAction", "param1");
		assertFalse(result.getLegal());
	}
	
	/**
	 * Validation test with a noneditable parameter. It should not pass the validation
	 * as the received parameter doesn't exists.
	 */
	public void testValidateParameterDoesNotExist() {
		
		IState state = new State();
		IDataValidator validator = this.dataValidatorFactory.newInstance(state);
		
		IParameter param1 = new Parameter();
		param1.addValue("value1");
		param1.setName("param1");
		param1.setEditable(false);		
		
		state.addParameter("param1", param1);
		
		validator.setState(state);
		
		boolean confidentiality = this.getConfig().getConfidentiality().booleanValue();
		String value = (confidentiality) ? "0" : "value1";	
		try {
			IValidationResult result = validator.validate(value, "simpleAction",
																"parameterDoesNotExist");
		} catch (NullPointerException e) {		
			assertTrue(true);
			return;
		}
		assertFalse(true);		
	}	

	/**
	 * Validation test with a noneditable parameter. It should not pass the
	 * validation as the received parameter doesn't exists.
	 */
	public void testValidatePositionDoesNotExist() {
		
		IState state = new State();
		IDataValidator validator = this.dataValidatorFactory.newInstance(state);
		
		IParameter param1 = new Parameter();
		param1.addValue("value1");
		param1.setName("param1");
		param1.setEditable(false);		
		
		state.addParameter("param1", param1);
		
		validator.setState(state);
		
		IValidationResult result = validator.validate("1", "simpleAction", "param1");
		assertFalse(result.getLegal());
	}
	
	/**
	 * Validation test with a noneditable parameter. The validation is correct.
	 */
	public void testValidateCorrectData() {
		
		IState state = new State();
		IDataValidator validator = this.dataValidatorFactory.newInstance(state);
		
		IParameter param1 = new Parameter();
		param1.addValue("value1");
		param1.setName("param1");
		param1.setEditable(false);		
		
		state = new State();
		state.addParameter("param1", param1);
		
		validator.setState(state);
		
		boolean confidentiality = this.getConfig().getConfidentiality().booleanValue();
		String value = (confidentiality) ? "0" : "value1";		
		IValidationResult result = validator.validate(value, "simpleAction", "param1");
		
		assertEquals(((String) result.getResult()), "value1");				
		assertTrue(result.getLegal());
	}	
}
