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
package org.hdiv.web.multipart;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.config.multipart.IMultipartConfig;
import org.hdiv.config.multipart.exception.HdivMultipartException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.util.WebUtils;

/**
 * {@link MultipartResolver} to use instead of {@link StandardServletMultipartResolver}.
 * 
 * @author Gotzon Illarramendi
 */
public class HdivStandardServletMultipartResolver extends StandardServletMultipartResolver {

	@Override
	public boolean isMultipart(HttpServletRequest request) {

		HdivMultipartException multipartException = (HdivMultipartException) request
				.getAttribute(IMultipartConfig.FILEUPLOAD_EXCEPTION);
		if (multipartException != null) {
			Exception orig = multipartException.getOriginal();
			if (orig instanceof MultipartException) {
				throw (MultipartException) orig;
			}
			else {
				throw new MultipartException("Could not parse multipart servlet request", orig);
			}
		}

		return super.isMultipart(request);
	}

	@Override
	public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException {

		MultipartHttpServletRequest multipartHttpServletRequest = WebUtils.getNativeRequest(request,
				MultipartHttpServletRequest.class);
		if (multipartHttpServletRequest != null) {
			// Use MultipartHttpServletRequestWrapper to maintain MultipartHttpServletRequest in first place
			// and obtains parameter values from RequestWrapper, with real values with confidentiality activated
			return new MultipartHttpServletRequestWrapper(request, multipartHttpServletRequest);
		}

		// If MultipartHttpServletRequest instance is not present, parse multipart request
		return super.resolveMultipart(request);
	}

}
