package org.hdiv.filter;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.exception.HDIVException;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;

public class ValidationContextImpl implements ValidationContext {

	private final HttpServletRequest request;

	private final StringBuilder sb = new StringBuilder(128);

	private String target;

	private String redirect;

	public ValidationContextImpl(final HttpServletRequest request, final StateRestorer restorer, final boolean obfuscation) {
		this.request = request;
		String target = getDecodedTarget(sb, request);
		if (obfuscation) {
			String hdivParameter = HDIVUtil.getHdivStateParameterName(request);
			if (hdivParameter != null) {

				// Restore state from request or memory
				ValidatorHelperResult result = restorer.restoreState(hdivParameter, request, target);
				if (result.isValid()) {
					this.target = result.getValue().getAction();
					redirect = this.target;
				}
			}
		}
		else {
			this.target = target;
		}
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public String getTarget() {
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
