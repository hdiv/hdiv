package org.hdiv.filter;

import javax.servlet.http.HttpServletRequest;

public interface ValidationContext {

	public HttpServletRequest getRequest();

	public String getTarget();

	public String getRedirect();

	public StringBuilder getBuffer();

}
