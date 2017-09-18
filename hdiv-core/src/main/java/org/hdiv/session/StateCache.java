/**
 * Copyright 2005-2016 hdiv.org
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
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It is composed by a data structure limited by a maximum size <code>maxSize</code>. <code>pageIds</code> structure is composed by elements
 * of type IPage (all the possible requests generated in the request processing).
 * 
 * @author Roberto Velasco
 */
public class StateCache implements IStateCache {

	private static final int DEFAULT_MAX_SIZE = 5;

	/**
	 * Commons Logging instance.
	 */
	private static final Logger log = LoggerFactory.getLogger(StateCache.class);

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that a loaded class corresponds exactly to a serialized
	 * object.
	 */
	private static final long serialVersionUID = -386843742684433849L;

	/**
	 * Buffer size
	 */
	private int maxSize = DEFAULT_MAX_SIZE;

	/**
	 * Page identifiers buffer
	 */
	private final List<UUID> pageIds = new ArrayList<UUID>();

	/**
	 * Adds a new page identifier to the cache.
	 * 
	 * @param pageId page identifier to add
	 * @param currentPageId page identifier of the current request. It can be null if no state id is present.
	 * 
	 * @param isRefreshRequest if request is a refresh request
	 * 
	 * @param isAjaxRequest if request is an ajax request
	 * 
	 * @return If the cache has reached its maximum size, less important identifier is returned in order to delete it from session.
	 * Otherwise, null will be returned.
	 */
	public synchronized UUID addPage(final UUID pageId, final UUID currentPageId, final boolean isRefreshRequest,
			final boolean isAjaxRequest) {

		if (pageIds.contains(pageId)) {
			// Page id already exist in session
			return null;

		}
		else {
			UUID removedKey = cleanBuffer(currentPageId, isRefreshRequest, isAjaxRequest);
			pageIds.add(pageId);

			if (log.isDebugEnabled()) {
				log.debug("Page with [" + pageId + "] added to the cache. Cache contains [" + pageIds + "]");
			}

			return removedKey;
		}
	}

	/**
	 * If the buffer <code>pageIds</code> has reached its maximum size <code>maxSize</code>, one page is deleted. If current page is the
	 * last one, the oldest key is removed, otherwise any newer page is removed
	 * 
	 * @param currentPageId page identifier of the current request. It can be null if no state id is present.
	 * 
	 * @param isRefreshRequest if request is a refresh request
	 *
	 * @param isAjaxRequest if request is an ajax request
	 * 
	 * @return Oldest page identifier in the map <code>pageIds</code>. Null in otherwise.
	 */
	private UUID cleanBuffer(final UUID currentPageId, final boolean isRefreshRequest, final boolean isAjaxRequest) {

		UUID removed = null;

		int totalPages = pageIds.size();

		// Remove last page when we know that browser's forward history is empty (See issue #67)
		if (currentPageId != null && totalPages > 1 && currentPageId.equals(pageIds.get(totalPages - 2)) && isRefreshRequest
				&& !isAjaxRequest) {
			removed = pageIds.remove(totalPages - 1);
		}

		if (pageIds.size() >= maxSize) {
			removed = pageIds.remove(0);
		}

		if (log.isDebugEnabled() && removed != null) {
			log.debug("Deleted pages with id [" + removed + "].");
		}
		return removed;
	}

	/**
	 * Return last page id in the cache.
	 * 
	 * @return page id
	 * @since 2.1.14
	 */
	public UUID getLastPageId() {

		return !pageIds.isEmpty() ? pageIds.get(pageIds.size() - 1) : null;
	}

	@Override
	public String toString() {

		StringBuilder result = new StringBuilder();
		result.append("[");
		for (UUID pageId : pageIds) {
			result.append(" " + pageId.getLeastSignificantBits());
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
	 * @param maxSize The maxSize to set.
	 */
	public void setMaxSize(final int maxSize) {
		this.maxSize = maxSize;
	}

	/**
	 * @return the pageIds
	 */
	public List<UUID> getPageIds() {
		return pageIds;
	}

	public static void main(final String[] args) {
		int first = 129;
		Integer second = 129;
		System.out.println(first == second);
	}

}