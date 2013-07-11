package org.hdiv.web.hateoas.error;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.filter.ValidatorErrorHandler;

/**
 * Implementation of {@link ValidatorErrorHandler} to handle the errors of hdiv's validation.
 * 
 * @author inigo
 */
public class HateoasValidationErrorHander implements ValidatorErrorHandler {

	/** Logger to log the received errors */
	private static final Log logger = LogFactory.getLog(HateoasValidationErrorHander.class);

	/** Response's error code to be sent. Default value is {@link HttpServletResponse.SC_FORBIDDEN} */
	private int responseErrorCode = HttpServletResponse.SC_FORBIDDEN;

	/** Response's error message to be sent. Default value is "HDIV. Unauthorized access." */
	private String responseErrorMessage = "HDIV. Unauthorized access.";

	public void setResponseErrorCode(int responseErrorCode) {
		this.responseErrorCode = responseErrorCode;
	}

	public void setResponseErrorMessage(String responseErrorMessage) {
		this.responseErrorMessage = responseErrorMessage;
	}

	/**
	 * Sends an error code with a message as response
	 */
	public void handleValidatorError(HttpServletRequest request, HttpServletResponse response, String errorCode) {
		try {
			if (logger.isInfoEnabled()) {
				logger.info("Sending error response due to " + errorCode + " hdiv error");
			}

			response.sendError(responseErrorCode, responseErrorMessage);
		} catch (IOException e) {
			throw new IllegalStateException("Fail sending error code");
		}
	}

}
