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

package org.hdiv.services;

import java.beans.Introspector;
import java.lang.reflect.Method;

import org.springframework.hateoas.core.DummyInvocationUtils.LastInvocationAware;
import org.springframework.hateoas.core.DummyInvocationUtils.MethodInvocation;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public class InvocationMethodProviderForTesting implements InvocationMethodProvider {

	private static final Method lastInvocationDefMethod = ReflectionUtils.findMethod(LastInvocationAware.class, "getLastInvocation");

	private static final Method objectParametersDefMethod = ReflectionUtils.findMethod(LastInvocationAware.class, "getObjectParameters");

	@Override
	public Method getObjectParametersDefMethod() {
		return objectParametersDefMethod;
	}

	@Override
	public Method getLastInvocationMethod(final Object object) {
		Assert.isInstanceOf(LastInvocationAware.class, object);
		return ((LastInvocationAware) object).getLastInvocation().getMethod();
	}

	@Override
	public Method getLastInvocationDefMethod() {
		return lastInvocationDefMethod;
	}

	@Override
	public Class<?> getInvokationAwareClass() {
		return LastInvocationAware.class;
	}

	@Override
	public Object getMethodInvocationIntance(final Class<?> targetType, final Method method, final Object[] arguments,
			final Object invocation) {
		if (invocation != null) {
			Assert.isInstanceOf(MethodInvocation.class, invocation);
		}
		return new SimpleMethodInvocation(targetType, method, arguments, (MethodInvocation) invocation);
	}

	static class SimpleMethodInvocation implements MethodInvocation {

		private final Class<?> targetType;

		private final Method method;

		private final Object[] arguments;

		private final MethodInvocation invocation;

		/**
		 * Creates a new {@link SimpleMethodInvocation} for the given {@link Method} and arguments.
		 * 
		 * @param method
		 * @param arguments
		 */
		private SimpleMethodInvocation(final Class<?> targetType, final Method method, final Object[] arguments,
				final MethodInvocation invocation) {

			this.targetType = targetType;
			this.arguments = arguments;
			this.method = method;
			this.invocation = invocation;
		}

		@Override
		public Class<?> getTargetType() {
			return targetType;
		}

		@Override
		public Object[] getArguments() {
			return arguments;
		}

		@Override
		public Method getMethod() {
			return method;
		}

		@Override
		public String toString() {
			return (invocation != null ? invocation.toString() + "." : "") + getPropertyFromMethod(method);
		}

		private String getPropertyFromMethod(final Method method) {
			String name = method.getName();
			return Introspector.decapitalize(name.substring(name.startsWith("is") ? 2 : 3));
		}
	}
}
