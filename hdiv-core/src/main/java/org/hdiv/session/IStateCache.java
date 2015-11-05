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
package org.hdiv.session;

import java.io.Serializable;
import java.util.List;

/**
 * @author Roberto Velasco
 */
public interface IStateCache extends Serializable {

	/**
	 * Adds a new page identifier to the cache.
	 * 
	 * @param pageId
	 *            page identifier to add
	 * @param currentPageId
	 *            page identifier of the current request. It can be null if no state id is present.  
	 * @param isRefreshRequest
	 * 			  if the request is a refresh request
	 * @param isAjaxRequest
	 * 			  if the request is an ajax request
	 * 
	 * @return If the cache has reached its maximum size, less important identifier is returned in order to delete it
	 *         from session. Otherwise, null will be returned.
	 */
	public Integer addPage(int pageId, Integer currentPageId, boolean isRefreshRequest, boolean isAjaxRequest);

	/**
	 * @return the pageIds
	 */
	public List<Integer> getPageIds();
}
