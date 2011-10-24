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
package org.hdiv.filter;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.dataComposer.DataComposerFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.util.HDIVUtil;

/**
 * It validates client requests by comsuming an object of type IState and
 * validating all the entry data, besides replacing relative values by its real
 * values.
 * 
 * @author Roberto Velasco
 * @author Gorka Vicente
 * @since HDIV 2.0
 */
public class ValidatorHelperRequest extends AbstractValidatorHelper {

	private DataComposerFactory dataComposerFactory;

	/**
	 * It is called in the pre-processing stage of each user request.
	 * 
	 * @param request http request
	 */
	public void startPage(HttpServletRequest request) {
		
		IDataComposer dataComposer = this.dataComposerFactory.newInstance();
		dataComposer.startPage();
		HDIVUtil.setDataComposer(dataComposer, request);
		
	}

	/**
	 * Handle the storing of HDIV's state, which is done after action
	 * invocation.
	 * 
	 * @param request http request
	 */
	public void endPage(HttpServletRequest request) {

		IDataComposer dataComposer = (IDataComposer) HDIVUtil.getDataComposer(request);
		dataComposer.endPage();
	}

	public void setDataComposerFactory(DataComposerFactory dataComposerFactory) {
		this.dataComposerFactory = dataComposerFactory;
	}

}