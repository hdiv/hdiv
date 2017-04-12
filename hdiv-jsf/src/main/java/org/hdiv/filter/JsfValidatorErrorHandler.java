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

import java.io.IOException;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;
import javax.faces.context.PartialViewContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.context.RequestContextHolder;

public class JsfValidatorErrorHandler extends DefaultValidatorErrorHandler {

	private static final Log log = LogFactory.getLog(JsfValidatorErrorHandler.class);

	@Override
	public void handleValidatorError(final RequestContextHolder ctx, final List<ValidatorError> errors) {

		FacesContext context = FacesContext.getCurrentInstance();
		if (context == null) {

			super.handleValidatorError(ctx, errors);
			return;
		}

		PartialViewContext partialViewContext = context.getPartialViewContext();
		if (partialViewContext.isAjaxRequest() && partialViewContext.isPartialRequest()) {

			try {

				ExternalContext extContext = context.getExternalContext();
				extContext.setResponseContentType("text/xml");
				extContext.addResponseHeader("Cache-Control", "no-cache");
				PartialResponseWriter writer = context.getPartialViewContext().getPartialResponseWriter();

				writer.startDocument();
				writer.startError("Invalid request");
				for (ValidatorError error : errors) {
					writer.write(" Type:" + error.getType());
					writer.write(", Param:" + error.getParameterName());
					writer.write(", Value:" + error.getParameterValue());
				}
				writer.endError();
				writer.endDocument();
				context.responseComplete();
			}
			catch (IOException ioe) {
				if (log.isErrorEnabled()) {
					log.error("Error rendering response.", ioe);
				}
			}
		}
		else {

			super.handleValidatorError(ctx, errors);
			context.responseComplete();
		}
	}
}