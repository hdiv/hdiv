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
package org.hdiv.filter;

import java.util.ArrayList;
import java.util.List;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.util.HDIVErrorCodes;

/**
 * Unit tests for the <code>org.hdiv.filter.ValidatorHelperRequest</code> class.
 * 
 * @author Gorka Vicente
 */
public class ValidatorHelperRequestTest extends AbstractHDIVTestCase {

	private IValidationHelper helper;

	private String targetName = "/path/testAction.do";

	protected void onSetUp() throws Exception {

		this.helper = this.getApplicationContext().getBean(IValidationHelper.class);
	}

	/**
	 * This method check if hasNonConfidentialIncorrectValues method can receive repeated values in the last 2
	 * positions.
	 */
	public void testHasNonConfidentialIncorrectValues_RepeatedValuesInLastPosition() {

		String parameter = "param1";
		String[] values = new String[] { "0", "10", "20", "20" };
		List<String> tempStateValues = new ArrayList<String>();
		tempStateValues.add("0");
		tempStateValues.add("10");
		tempStateValues.add("20");

		ValidatorHelperResult actualResult = ((ValidatorHelperRequest) helper).hasNonConfidentialIncorrectValues(targetName, parameter,
				values, tempStateValues);
		assertFalse(actualResult.isValid());
		assertEquals(HDIVErrorCodes.REPEATED_VALUES, actualResult.getErrors().get(0).getType());
	}

	/**
	 * This method check if hasNonConfidentialIncorrectValues method can receive repeated values in the middle of
	 * received array values.
	 */
	public void testHasNonConfidentialIncorrectValues_RepeatedValuesInTheMiddle() {

		String parameter = "param1";
		String[] values = new String[] { "0", "20", "20", "10" };
		List<String> tempStateValues = new ArrayList<String>();
		tempStateValues.add("0");
		tempStateValues.add("10");
		tempStateValues.add("20");

		ValidatorHelperResult actualResult = ((ValidatorHelperRequest) helper).hasNonConfidentialIncorrectValues(targetName, parameter,
				values, tempStateValues);
		assertFalse(actualResult.isValid());
		assertEquals(HDIVErrorCodes.REPEATED_VALUES, actualResult.getErrors().get(0).getType());
	}

	/**
	 * This method check if hasNonConfidentialIncorrectValues method can receive repeated values at first positions of
	 * received array values.
	 */
	public void testHasNonConfidentialIncorrectValues_RepeatedValuesAtFirstPositions() {

		String parameter = "param1";
		String[] values = new String[] { "20", "20", "0", "10" };
		List<String> tempStateValues = new ArrayList<String>();
		tempStateValues.add("0");
		tempStateValues.add("10");
		tempStateValues.add("20");

		ValidatorHelperResult actualResult = ((ValidatorHelperRequest) helper).hasNonConfidentialIncorrectValues(targetName, parameter,
				values, tempStateValues);
		assertFalse(actualResult.isValid());
		assertEquals(HDIVErrorCodes.REPEATED_VALUES, actualResult.getErrors().get(0).getType());
	}

	/**
	 * This method check if hasNonConfidentialIncorrectValues method can receive repeated values of received array
	 * values.
	 */
	public void testHasNonConfidentialIncorrectValues_RepeatedValuesInAnyPosition_1() {

		String parameter = "param1";
		String[] values = new String[] { "20", "0", "10", "20" };
		List<String> tempStateValues = new ArrayList<String>();
		tempStateValues.add("0");
		tempStateValues.add("10");
		tempStateValues.add("20");

		ValidatorHelperResult actualResult = ((ValidatorHelperRequest) helper).hasNonConfidentialIncorrectValues(targetName, parameter,
				values, tempStateValues);
		assertFalse(actualResult.isValid());
		assertEquals(HDIVErrorCodes.REPEATED_VALUES, actualResult.getErrors().get(0).getType());
	}

	/**
	 * This method check if hasNonConfidentialIncorrectValues method can receive repeated values of received array
	 * values.
	 */
	public void testHasNonConfidentialIncorrectValues_RepeatedValuesInAnyPosition_2() {

		String parameter = "param1";
		String[] values = new String[] { "20", "0", "20", "10" };
		List<String> tempStateValues = new ArrayList<String>();
		tempStateValues.add("0");
		tempStateValues.add("10");
		tempStateValues.add("20");

		ValidatorHelperResult actualResult = ((ValidatorHelperRequest) helper).hasNonConfidentialIncorrectValues(targetName, parameter,
				values, tempStateValues);
		assertFalse(actualResult.isValid());
		assertEquals(HDIVErrorCodes.REPEATED_VALUES, actualResult.getErrors().get(0).getType());
	}

	/**
	 * This method check if hasNonConfidentialIncorrectValues method can receive repeated values of received array
	 * values.
	 */
	public void testHasNonConfidentialIncorrectValues_RepeatedValuesInAnyPosition_3() {

		String parameter = "param1";
		String[] values = new String[] { "0", "20", "10", "20" };
		List<String> tempStateValues = new ArrayList<String>();
		tempStateValues.add("0");
		tempStateValues.add("10");
		tempStateValues.add("20");

		ValidatorHelperResult actualResult = ((ValidatorHelperRequest) helper).hasNonConfidentialIncorrectValues(targetName, parameter,
				values, tempStateValues);
		assertFalse(actualResult.isValid());
		assertEquals(HDIVErrorCodes.REPEATED_VALUES, actualResult.getErrors().get(0).getType());
	}

	/**
	 * This method check that hasNonConfidentialIncorrectValues method returns invalid result when unexpected value is
	 * received for specific parameter.
	 */
	public void testHasNonConfidentialIncorrectValues_IncorrectParameterValueReceivedInLastPosition() {

		String parameter = "param1";
		String[] values = new String[] { "0", "10", "99999" };
		List<String> tempStateValues = new ArrayList<String>();
		tempStateValues.add("0");
		tempStateValues.add("10");
		tempStateValues.add("20");

		ValidatorHelperResult actualResult = ((ValidatorHelperRequest) helper).hasNonConfidentialIncorrectValues(targetName, parameter,
				values, tempStateValues);
		assertFalse(actualResult.isValid());
		assertEquals(HDIVErrorCodes.PARAMETER_VALUE_INCORRECT, actualResult.getErrors().get(0).getType());
	}

	/**
	 * This method check that hasNonConfidentialIncorrectValues method returns invalid result when unexpected value is
	 * received for specific parameter.
	 */
	public void testHasNonConfidentialIncorrectValues_IncorrectParameterValueReceivedInTheMiddle() {

		String parameter = "param1";
		String[] values = new String[] { "0", "99999", "10" };
		List<String> tempStateValues = new ArrayList<String>();
		tempStateValues.add("0");
		tempStateValues.add("10");
		tempStateValues.add("20");

		ValidatorHelperResult actualResult = ((ValidatorHelperRequest) helper).hasNonConfidentialIncorrectValues(targetName, parameter,
				values, tempStateValues);
		assertFalse(actualResult.isValid());
		assertEquals(HDIVErrorCodes.PARAMETER_VALUE_INCORRECT, actualResult.getErrors().get(0).getType());
	}

	/**
	 * This method check that hasNonConfidentialIncorrectValues method returns invalid result when unexpected value is
	 * received for specific parameter.
	 */
	public void testHasNonConfidentialIncorrectValues_IncorrectParameterValueReceivedAtFirstPosition() {

		String parameter = "param1";
		String[] values = new String[] { "99999", "0", "10" };
		List<String> tempStateValues = new ArrayList<String>();
		tempStateValues.add("0");
		tempStateValues.add("10");
		tempStateValues.add("20");

		ValidatorHelperResult actualResult = ((ValidatorHelperRequest) helper).hasNonConfidentialIncorrectValues(targetName, parameter,
				values, tempStateValues);
		assertFalse(actualResult.isValid());
		assertEquals(HDIVErrorCodes.PARAMETER_VALUE_INCORRECT, actualResult.getErrors().get(0).getType());
	}
}