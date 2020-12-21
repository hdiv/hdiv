package org.hdiv.services;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JavaHdivProxyFactory implements HdivProxyFactory {

	private static JavaHdivProxyFactory instance = null;

	private JavaHdivProxyFactory() {

	}

	public static JavaHdivProxyFactory getInstance() {
		if (instance == null) {
			instance = new JavaHdivProxyFactory();
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	public <T> T getProxyWithInterceptor(final Class<T> type, final HdivMethodInterceptor interceptor, final ClassLoader classLoader,
			final boolean isfinal) {

		T proxyInstance = (T) Proxy.newProxyInstance(classLoader, new Class[] { type }, new InvocationHandler() {

			public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
				return method.invoke(proxy, args);
			}

		});

		return proxyInstance;

	}

	public <T> T getProxyWithInterceptor(final Class<T> type, final HdivMethodInterceptor interceptor, final ClassLoader classLoader) {
		return getProxyWithInterceptor(type, interceptor, classLoader, false);
	}
}
