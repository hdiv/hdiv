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
package org.hdiv.idGenerator;

import java.io.Serializable;

/**
 * A strategy for generating random tokens.
 * 
 * @author Gorka Vicente
 * @since HDIV 2.0.4
 */
public interface UidGenerator {

	/**
	 * Generate a new unique id.
	 * 
	 * @return a serializable id, guaranteed to be unique in some context
	 */
	public Serializable generateUid();

	/**
	 * Convert the string-encoded uid into its original object form.
	 * 
	 * @param encodedUid the string encoded uid
	 * @return the converted uid
	 */
	public Serializable parseUid(String encodedUid);
}
