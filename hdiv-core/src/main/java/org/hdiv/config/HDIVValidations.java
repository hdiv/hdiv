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
package org.hdiv.config;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hdiv.regex.PatternMatcher;
import org.hdiv.validator.IValidation;

/**
 * Editable field validations.
 * 
 * @author Gorka Vicente
 * @since HDIV 1.1
 */
public class HDIVValidations implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Map containing the urls to which the user wants to apply validation for the editable parameters.
	 */
	protected Map<PatternMatcher, List<IValidation>> urls;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return urls.toString();
	}

	/**
	 * @return Returns the urls.
	 */
	public Map<PatternMatcher, List<IValidation>> getUrls() {
		return urls;
	}

	/**
	 * @param urls
	 *            The urls to set.
	 */
	public void setUrls(Map<PatternMatcher, List<IValidation>> urls) {
		this.urls = urls;
	}

}
