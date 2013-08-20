package org.hdiv.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Methods for request scope data and wrapper initialization. Used in {@link ValidatorFilter}.
 * 
 * @author Gotzon Illarramendi
 * @since 2.1.5
 */
public interface RequestInitializer {

	/**
	 * Initialize request scoped data
	 * 
	 * @param request
	 *            request object
	 */
	void initRequest(HttpServletRequest request);

	/**
	 * Destroy request scoped data
	 * 
	 * @param request
	 *            request object
	 */
	void endRequest(HttpServletRequest request);

	/**
	 * Create request wrapper.
	 * 
	 * @param request
	 *            HTTP request
	 * @return the request wrapper
	 */
	RequestWrapper createRequestWrapper(HttpServletRequest request);

	/**
	 * Create response wrapper.
	 * 
	 * @param response
	 *            HTTP response
	 * @return the response wrapper
	 */
	ResponseWrapper createResponseWrapper(HttpServletResponse response);
}
