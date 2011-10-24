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
package org.hdiv.session;

import java.io.Serializable;
import java.util.List;

/**
 * @author Roberto Velasco
 */
public interface IStateCache extends Serializable {

	/**
	 * Adds a new page identifier to the map <code>pageIds</code>.
	 * 
	 * @return If the map <code>pageIds</code> has reached its maximun size
	 *         <code>maxSize</code>, the oldest page identifier is deleted.
	 *         Otherwise, null will be returned.
	 */
	public String addPage(String pageId);

	/**
	 * @return the pageIds
	 */
	public List getPageIds();
}
