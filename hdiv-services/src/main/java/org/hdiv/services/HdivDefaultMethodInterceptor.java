package org.hdiv.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.Iterator;

import org.aopalliance.intercept.MethodInvocation;

public class HdivDefaultMethodInterceptor implements HdivMethodInterceptor {

	private final Class<?> targetType;

	private final Object[] objectParameters;

	protected final InvocationMethodProvider invocationMethodProvider;

	private final HdivProxyFactory hdivProxyFactory;

	private Object invocation;

	public HdivDefaultMethodInterceptor(final InvocationMethodProvider invocationMethodProvider, final HdivProxyFactory hdivProxyFactory,
			final Class<?> targetType, final Object... objectParameters) {
		this.invocationMethodProvider = invocationMethodProvider;
		this.hdivProxyFactory = hdivProxyFactory;
		this.targetType = targetType;
		this.objectParameters = objectParameters;
	}

	public Iterator<Object> getObjectParameters() {
		return Arrays.asList(objectParameters).iterator();
	}

	public Object getInvocation() {
		return invocation;
	}

	public Object invoke(final MethodInvocation invocation) throws Throwable {
		return intercept(invocation.getThis(), invocation.getMethod(), invocation.getArguments());
	}

	public Object intercept(final Object obj, final Method method, final Object[] args) throws Throwable {
		if (invocationMethodProvider.getLastInvocationDefMethod().equals(method)) {
			return getInvocation();
		}
		else if (invocationMethodProvider.getObjectParametersDefMethod().equals(method)) {
			return getObjectParameters();
		}
		else if (Object.class.equals(method.getDeclaringClass())) {
			return invokeMethod(method, obj, args);
		}
		invocation = invocationMethodProvider.getMethodInvocationIntance(targetType, method, args, getInvocation());

		Class<?> returnType = method.getReturnType();
		if (Modifier.isFinal(returnType.getModifiers())) {
			return null;
		}
		else {
			return returnType.cast(hdivProxyFactory.getProxyWithInterceptor(returnType, this, obj.getClass().getClassLoader()));
		}
	}

	private Object invokeMethod(final Method method, final Object target, final Object... args) {
		try {
			return method.invoke(target, args);
		}
		catch (Exception ex) {
			handleReflectionException(ex);
		}
		throw new IllegalStateException("Should never get here");
	}

	private void handleReflectionException(final Exception ex) {
		if (ex instanceof NoSuchMethodException) {
			throw new IllegalStateException("Method not found: " + ex.getMessage());
		}
		if (ex instanceof IllegalAccessException) {
			throw new IllegalStateException("Could not access method: " + ex.getMessage());
		}
		if (ex instanceof InvocationTargetException) {
			rethrowRuntimeException(((InvocationTargetException) ex).getTargetException());
		}
		if (ex instanceof RuntimeException) {
			throw (RuntimeException) ex;
		}
		throw new UndeclaredThrowableException(ex);
	}

	private void rethrowRuntimeException(final Throwable ex) {
		if (ex instanceof RuntimeException) {
			throw (RuntimeException) ex;
		}
		if (ex instanceof Error) {
			throw (Error) ex;
		}
		throw new UndeclaredThrowableException(ex);
	}

}
