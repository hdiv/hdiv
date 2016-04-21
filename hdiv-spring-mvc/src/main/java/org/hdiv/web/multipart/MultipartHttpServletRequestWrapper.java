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
package org.hdiv.web.multipart;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * {@link MultipartHttpServletRequest} wrapper for Multipart requests.
 * <p>
 * Use inner {@link MultipartHttpServletRequest} to obtain Multipart processed files.
 * <p>
 * And original {@link HttpServletRequest} request for parameters and the rest of data.
 */
public class MultipartHttpServletRequestWrapper extends HttpServletRequestWrapper implements
		MultipartHttpServletRequest {

	private MultipartHttpServletRequest innerMultipartHttpServletRequest;

	public MultipartHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	public MultipartHttpServletRequestWrapper(HttpServletRequest request,
			MultipartHttpServletRequest innerMultipartHttpServletRequest) {
		super(request);
		Assert.notNull(request);
		Assert.notNull(innerMultipartHttpServletRequest);
		this.innerMultipartHttpServletRequest = innerMultipartHttpServletRequest;
	}

	public Iterator<String> getFileNames() {
		return this.innerMultipartHttpServletRequest.getFileNames();
	}

	public MultipartFile getFile(String name) {
		return this.innerMultipartHttpServletRequest.getFile(name);
	}

	public List<MultipartFile> getFiles(String name) {
		return this.innerMultipartHttpServletRequest.getFiles(name);
	}

	public Map<String, MultipartFile> getFileMap() {
		return this.innerMultipartHttpServletRequest.getFileMap();
	}

	public MultiValueMap<String, MultipartFile> getMultiFileMap() {
		return this.innerMultipartHttpServletRequest.getMultiFileMap();
	}

	public String getMultipartContentType(String paramOrFileName) {
		return this.innerMultipartHttpServletRequest.getMultipartContentType(paramOrFileName);
	}

	public HttpMethod getRequestMethod() {
		return this.innerMultipartHttpServletRequest.getRequestMethod();
	}

	public HttpHeaders getRequestHeaders() {
		return this.innerMultipartHttpServletRequest.getRequestHeaders();
	}

	public HttpHeaders getMultipartHeaders(String paramOrFileName) {
		return this.innerMultipartHttpServletRequest.getMultipartHeaders(paramOrFileName);
	}

}