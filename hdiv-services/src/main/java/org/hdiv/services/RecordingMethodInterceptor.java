package org.hdiv.services;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.hdiv.services.Path.InvocationAdvice;
import org.springframework.cglib.proxy.MethodProxy;

public class RecordingMethodInterceptor extends HdivDefaultMethodInterceptor
		implements MethodInterceptor, InvocationAdvice, org.springframework.cglib.proxy.MethodInterceptor {

	public RecordingMethodInterceptor(final InvocationMethodProvider invocationMethodProvider, final HdivProxyFactory hdivProxyFactory,
			final Class<?> targetType, final Object... objectParameters) {
		super(invocationMethodProvider, hdivProxyFactory, targetType, objectParameters);
	}

	@Override
	public Object invoke(final org.aopalliance.intercept.MethodInvocation invocation) throws Throwable {
		return intercept(invocation.getThis(), invocation.getMethod(), invocation.getArguments(), null);
	}

	public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy arg3) throws Throwable {
		return intercept(obj, method, args);
	}

}
