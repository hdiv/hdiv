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
package org.hdiv.urlProcessor;

import org.hdiv.config.HDIVConfig;
import org.hdiv.util.Method;

public class CachedUrlDataImpl extends UrlDataImpl {

	private HDIVStatus status;

	private int cached;

	private String cacheProcessed;

	public CachedUrlDataImpl(final String url, final Method method) {
		super(url, method);
	}

	/**
	 * Generate a url with all parameters.
	 *
	 * @param urlData url data object
	 * @return complete url
	 */
	@Override
	void getParamProcessedUrl(final StringBuilder sb) {
		sb.setLength(0);
		if (cacheProcessed != null) {
			sb.append(cacheProcessed);
			return;
		}
		if (server != null) {
			sb.append(server);
		}
		sb.append(contextPathRelativeUrl);

		// Add jSessionId
		if (jSessionId != null) {
			sb.append(';').append(jSessionId);
		}

		if (composedParams != null) {
			sb.append('?').append(composedParams);
		}
		if (cached > 0) {
			cacheProcessed = sb.toString();
		}
	}

	@Override
	public String getProcessedUrlWithHdivState(final StringBuilder sb, final String hdivParameter, final String stateParam) {

		if (stateParam == null || stateParam.length() <= 0) {
			if (cacheProcessed != null) {
				return cacheProcessed;
			}
			else {
				getParamProcessedUrl(sb);
				return sb.toString();
			}
		}
		getParamProcessedUrl(sb);
		char separator = containsParams() ? '&' : '?';

		sb.append(separator).append(hdivParameter).append('=').append(stateParam);
		if (uriTemplate != null) {
			sb.append(uriTemplate.replace('?', '&'));
		}
		if (anchor != null) {
			// it could be ""
			sb.append('#').append(anchor);
		}
		return sb.toString();

	}

	@Override
	public boolean isHdivStateNecessary(final HDIVConfig config) {
		if (status == null) {
			boolean needed = super.isHdivStateNecessary(config);
			this.status = needed ? HDIVStatus.ACTIVE : HDIVStatus.INACTIVE;
			return needed;
		}
		else {
			return status == HDIVStatus.ACTIVE;
		}
	}

	private enum HDIVStatus {
		ACTIVE, INACTIVE
	}

	public void cached() {
		if (cached < Integer.MAX_VALUE) {
			cached++;
		}
	}

	public int getCached() {
		return cached;
	}
}
