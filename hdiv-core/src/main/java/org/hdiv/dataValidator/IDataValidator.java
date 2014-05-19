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
package org.hdiv.dataValidator;


import org.hdiv.state.IState;

/**
 * Interface to validate the data sent by the user.
 *  
 * @author Roberto Velasco
 * @author Oscar Ocariz
 */
public interface IDataValidator {
	
	/**
	 * <p>
	 * Checks if the value <code>data</code> sent by the user to the server in the
	 * parameter <code>parameter</code> is correct or not. The received value is
	 * checked with the one stored in the state to decide if it is correct.
	 * </p>
	 * <p>
	 * In the encoded and hash strategies, the state is obtained from the user
	 * request. However, in the memory strategy the state is obtained from the user
	 * session, using the state identifier receiced within the request.
	 * </p>
	 * 
	 * @param value value sent by the client
	 * @param target target action name
	 * @param parameter parameter name
	 * @return object that represents the result of the validation process for the
	 *         parameter <code>parameter</code> and the value <code>data</code>.
	 */
	public IValidationResult validate(String value, String target, String parameter);
	
	/**
	 * @param state The validation process state to set.
	 */
	public void setState(IState state);
	
}
