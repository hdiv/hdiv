/**
 * Copyright 2005-2010 hdiv.org
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

package org.hdiv.webflow.mvc.servlet;

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.web.servlet.View;
import org.springframework.webflow.engine.builder.BinderConfiguration;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.mvc.view.AbstractMvcView;
import org.springframework.webflow.mvc.view.AbstractMvcViewFactory;
import org.springframework.webflow.mvc.view.FlowViewResolver;

public class ServletMvcViewFactoryHDIV extends AbstractMvcViewFactory {

    /**
     * Creates a new servlet-based MVC view factory.
     * @param viewId the id of the view as an expression
     * @param viewResolver the resolver to resolve the View implementation
     * @param expressionParser the expression parser
     * @param conversionService the conversion service
     * @param binderConfiguration the model binding configuration
     */
	public ServletMvcViewFactoryHDIV(Expression viewId, FlowViewResolver viewResolver, ExpressionParser expressionParser,
			ConversionService conversionService, BinderConfiguration binderConfiguration,
			MessageCodesResolver messageCodesResolver) {
		super(viewId, viewResolver, expressionParser, conversionService, binderConfiguration, messageCodesResolver);
	}

    @Override
	protected AbstractMvcView createMvcView(View view, RequestContext context) {
    	return new ServletMvcViewHDIV(view, context);
    }

}
