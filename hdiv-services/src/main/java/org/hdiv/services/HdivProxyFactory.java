package org.hdiv.services;

public interface HdivProxyFactory {

	<T> T getProxyWithInterceptor(final Class<T> type, final HdivMethodInterceptor interceptor, final ClassLoader classLoader,
			final boolean isfinal);

	<T> T getProxyWithInterceptor(final Class<T> type, final HdivMethodInterceptor interceptor, final ClassLoader classLoader);
}
