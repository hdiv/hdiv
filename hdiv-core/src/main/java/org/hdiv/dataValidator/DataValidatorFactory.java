/**
 * Copyright 2005-2011 hdiv.org
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

import org.hdiv.config.HDIVConfig;
import org.hdiv.state.IState;

/**
 * DataValidator object factory, more efficient than to use the Spring factory.
 * 
 * @author Gotzon Illarramendi
 * @since HDIV 2.1.0
 */
public class DataValidatorFactory {

	/**
	 * HDIV configuration object.
	 */
	private HDIVConfig hdivConfig;
	
	/**
	 * Creates a new instance of IDataValidator that validates the request over the state.
	 * 
	 * @param state IState object
	 * @return IDataValidator instance
	 */
	public IDataValidator newInstance(IState state){
		
		IValidationResult result = new ValidationResult();
		DataValidator dataValidator = new DataValidator();
		dataValidator.setValidationResult(result);
		dataValidator.setConfidentiality(this.hdivConfig.getConfidentiality());
		dataValidator.setState(state);
		return dataValidator;
	}

	public void setHdivConfig(HDIVConfig hdivConfig) {
		this.hdivConfig = hdivConfig;
	}
	
}
