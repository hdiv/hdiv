package org.hdiv.filter;

import javax.servlet.http.HttpServletRequest;

interface StateRestorer {
	ValidatorHelperResult restoreState(final String hdivParameter, final HttpServletRequest request, final String target);
}
