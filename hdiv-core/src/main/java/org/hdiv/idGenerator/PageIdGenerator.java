/**
 * Copyright 2005-2015 hdiv.org
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
 * Component responsible for generating the unique page ids, which are part of state.
 * 
 * @author Gotzon Illarramendi
 * @since HDIV 2.1.0
 */
public interface PageIdGenerator extends Serializable {

	/**
	 * <p>
	 * Create a new page id.
	 * </p>
	 * <p>
	 * The identifier must be an integer greater than 0.
	 * </p>
	 * 
	 * @return new id
	 */
	int getNextPageId();

}
