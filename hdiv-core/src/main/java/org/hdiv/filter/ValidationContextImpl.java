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
package org.hdiv.filter;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.exception.HDIVException;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;

public class ValidationContextImpl implements ValidationContext {

	private final HttpServletRequest request;

	private final boolean obfuscation;

	private final StateRestorer restorer;

	private final StringBuilder sb = new StringBuilder(128);

	private String target;

	private String redirect;

	public ValidationContextImpl(final HttpServletRequest request, final StateRestorer restorer, final boolean obfuscation) {
		this.request = request;
		this.obfuscation = obfuscation;
		this.restorer = restorer;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public String getTarget() {
		if (target == null) {
			String target = getDecodedTarget(sb, request);
			if (obfuscation && HDIVUtil.isObfuscatedTarget(target)) {
				String hdivParameter = HDIVUtil.getHdivStateParameterName(request);
				if (hdivParameter != null) {

					// Restore state from request or memory
					ValidatorHelperResult result = restorer.restoreState(hdivParameter, request, target);
					if (result.isValid()) {
						this.target = result.getValue().getAction();
						redirect = this.target;
						HDIVUtil.setHdivObfRedirectAction(request, redirect);
					}
				}
			}
			if (this.target == null) {
				this.target = target;
			}
		}
		return target;
	}

	public StringBuilder getBuffer() {
		return sb;
	}

	public String getRedirect() {
		return redirect;
	}

	private final String getDecodedTarget(final StringBuilder sb, final HttpServletRequest request) {
		/**
		 * Remove contest path and session info first
		 */
		String target = HDIVUtil.stripSession(request.getRequestURI().substring(request.getContextPath().length()));
		return decodeUrl(sb, target);
	}

	/**
	 * It decodes the url to replace the character represented by percentage with its equivalent.
	 *
	 * @param url url to decode
	 * @return decoder url
	 */
	private String decodeUrl(final StringBuilder sb, final String url) {
		try {
			return HDIVUtil.decodeValue(sb, url, Constants.ENCODING_UTF_8);
		}
		catch (final UnsupportedEncodingException e) {
			throw new HDIVException("Error decoding url", e);
		}
		catch (final IllegalArgumentException e) {
			throw new HDIVException("Error decoding url", e);
		}
	}

}
