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
package org.hdiv.session;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * It is composed by a data structure limited by a maximum size (maxSize). Map data structure is composed by elements of
 * type IPage (all the possible requests generated in the request processing).
 * 
 * @author Roberto Velasco
 */
public class StateCache implements IStateCache {

	/**
	 * Commons Logging instance.
	 */
	private static final Log log = LogFactory.getLog(StateCache.class);

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that a loaded class corresponds exactly
	 * to a serialized object.
	 */
	private static final long serialVersionUID = -386843742684433849L;

	/**
	 * Buffer size
	 */
	private int maxSize;

	/**
	 * page's ids map
	 */
	private List<String> pageIds = new ArrayList<String>();

	/**
	 * Adds a new page identifier to the map <code>pageIds</code>.
	 * 
	 * @return If the map <code>pageIds</code> has reached its maximum size <code>maxSize</code>, the oldest page
	 *         identifier is deleted. Otherwise, null will be returned.
	 */
	public synchronized String addPage(String key) {

		if (this.pageIds.contains(key)) {
			// Page id already exist in session
			return null;

		} else {
			String removedKey = this.cleanBuffer();
			this.pageIds.add(key);

			if (log.isDebugEnabled()) {
				log.debug("Page with [" + key + "] added to the cache.");
			}

			return removedKey;
		}
	}

	/**
	 * If the map <code>pageIds</code> has reached its maximum size <code>maxSize</code>, the oldest page identifier in
	 * the map is deleted.
	 * 
	 * @return Oldest page identifier in the map <code>pageIds</code>. Null in otherwise.
	 */
	public String cleanBuffer() {

		if (this.pageIds.size() >= this.maxSize) {

			// delete first element
			String key = this.pageIds.remove(0);

			if (log.isDebugEnabled()) {
				log.debug("Full Cache, deleted page with id [" + key + "].");
			}

			return key;
		}
		return null;
	}

	public String toString() {

		StringBuffer result = new StringBuffer();
		result.append("[");
		for (String pageId : pageIds) {
			result.append(" " + pageId);
		}
		result.append("]");
		return result.toString();
	}

	/**
	 * @return Returns the maxSize.
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * @param maxSize
	 *            The maxSize to set.
	 */
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	/**
	 * @return the pageIds
	 */
	public List<String> getPageIds() {
		return pageIds;
	}

}