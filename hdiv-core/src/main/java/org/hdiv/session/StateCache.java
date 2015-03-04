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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * It is composed by a data structure limited by a maximum size <code>maxSize</code>. <code>pageIds</code> structure is
 * composed by elements of type IPage (all the possible requests generated in the request processing).
 * 
 * @author Roberto Velasco
 */
public class StateCache implements IStateCache {

	private static final int DEFAULT_MAX_SIZE = 5;

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
	private int maxSize = DEFAULT_MAX_SIZE;

	/**
	 * Page identifiers buffer
	 */
	private List<Integer> pageIds = new ArrayList<Integer>();

	/**
	 * Adds a new page identifier to the cache.
	 * 
	 * @param pageId
	 *            page identifier to add
	 * @param currentPageId
	 *            page identifier of the current request. It can be null if no state id is present.
	 * @return If the cache has reached its maximum size, less important identifier is returned in order to delete it
	 *         from session. Otherwise, null will be returned.
	 */
	public synchronized Integer addPage(int key, Integer currentPageId) {

		if (this.pageIds.contains(key)) {
			// Page id already exist in session
			return null;

		} else {
			Integer removedKey = this.cleanBuffer(currentPageId);
			this.pageIds.add(key);

			if (log.isDebugEnabled()) {
				log.debug("Page with [" + key + "] added to the cache. Cache contains [" + pageIds + "]");
			}

			return removedKey;
		}
	}

	/**
	 * If the buffer <code>pageIds</code> has reached its maximum size <code>maxSize</code>, one page is deleted. If
	 * current page is the last one, the oldest key is removed, otherwise any newer page is removed
	 * 
	 * @param currentPageId
	 *            page identifier of the current request. It can be null if no state id is present.
	 * @return Oldest page identifier in the map <code>pageIds</code>. Null in otherwise.
	 */
	public Integer cleanBuffer(Integer currentPageId) {

		Integer removed = null;

		int totalPages = this.pageIds.size();

		// Remove last page when we know that browser's forward history is empty (See issue #67)
		if (currentPageId != null && totalPages > 1 && currentPageId == pageIds.get(totalPages - 2)) {
			removed = this.pageIds.remove(totalPages - 1);
		}

		if (this.pageIds.size() >= this.maxSize) {
			removed = this.pageIds.remove(0);
		}

		if (log.isDebugEnabled() && removed != null) {
			log.debug("Deleted pages with id [" + removed + "].");
		}
		return removed;
	}

	public String toString() {

		StringBuffer result = new StringBuffer();
		result.append("[");
		for (Integer pageId : pageIds) {
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
	public List<Integer> getPageIds() {
		return pageIds;
	}

}