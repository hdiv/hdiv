package org.hdiv.web.multipart;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.hdiv.filter.RequestWrapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * <p>
 * {@link MultipartHttpServletRequest} wrapper for Multipart requests.
 * </p>
 * <p>
 * Use inner {@link RequestWrapper} in request chain to obtain request parameter real values with confidentiality
 * activated.
 * </p>
 * <p>
 * And inner {@link MultipartHttpServletRequest} to obtain Multipart processed Files.
 * </p>
 */
public class MultipartHttpServletRequestWrapper extends HttpServletRequestWrapper implements
		MultipartHttpServletRequest {

	private RequestWrapper innerRequestWrapper;

	private MultipartHttpServletRequest innerMultipartHttpServletRequest;

	public MultipartHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	public MultipartHttpServletRequestWrapper(HttpServletRequest request, RequestWrapper innerRequestWrapper,
			MultipartHttpServletRequest innerMultipartHttpServletRequest) {
		super(request);
		Assert.notNull(request);
		Assert.notNull(innerRequestWrapper);
		Assert.notNull(innerMultipartHttpServletRequest);
		this.innerRequestWrapper = innerRequestWrapper;
		this.innerMultipartHttpServletRequest = innerMultipartHttpServletRequest;
	}

	public String getParameter(String name) {
		String param = this.innerRequestWrapper.getParameter(name);
		if (param != null) {
			return param;
		}
		return this.getRequest().getParameter(name);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getParameterMap() {
		Map params = this.getRequest().getParameterMap();
		params.putAll(this.innerRequestWrapper.getParameterMap());

		return params;
	}

	@SuppressWarnings("rawtypes")
	public Enumeration getParameterNames() {

		return this.getRequest().getParameterNames();
	}

	public String[] getParameterValues(String name) {
		String[] params = this.innerRequestWrapper.getParameterValues(name);
		if (params != null && params.length > 0) {
			return params;
		}
		return this.getRequest().getParameterValues(name);
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