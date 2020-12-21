package org.hdiv.services;

import java.beans.Introspector;
import java.lang.reflect.Method;

import org.springframework.hateoas.core.DummyInvocationUtils.LastInvocationAware;
import org.springframework.hateoas.core.DummyInvocationUtils.MethodInvocation;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public class DefaultInvocationMethodProvider implements InvocationMethodProvider {

	private static final Method lastInvocationDefMethod = ReflectionUtils.findMethod(LastInvocationAware.class, "getLastInvocation");

	private static final Method objectParametersDefMethod = ReflectionUtils.findMethod(LastInvocationAware.class, "getObjectParameters");

	public Method getObjectParametersDefMethod() {
		return objectParametersDefMethod;
	}

	public Method getLastInvocationMethod(final Object object) {
		Assert.isInstanceOf(LastInvocationAware.class, object);
		return ((LastInvocationAware) object).getLastInvocation().getMethod();
	}

	public Method getLastInvocationDefMethod() {
		return lastInvocationDefMethod;
	}

	public Class<?> getInvokationAwareClass() {
		return LastInvocationAware.class;
	}

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

		public Class<?> getTargetType() {
			return targetType;
		}

		public Object[] getArguments() {
			return arguments;
		}

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
