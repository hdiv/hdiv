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

import java.util.Collection;
import java.util.Iterator;

import org.springframework.core.ResolvableType;

public class Path {

	private static boolean USING_SPRING;
	static {
		try {
			Class.forName("org.springframework.aop.framework.ProxyFactory");
			Class.forName("org.springframework.aop.target.EmptyTargetSource");
			Class.forName("org.springframework.cglib.proxy.Callback");
			Class.forName("org.springframework.cglib.proxy.Enhancer");
			Class.forName("org.springframework.cglib.proxy.Factory");
			Class.forName("org.springframework.objenesis.ObjenesisStd");
			USING_SPRING = true;
		}
		catch (Exception e) {
			USING_SPRING = false;
		}
	}

	public static ThreadLocal<HdivMethodInterceptor> interceptorThreadLocal = new ThreadLocal<HdivMethodInterceptor>();

	private static InvocationMethodProvider invocationMethodProvider = null;

	public static void registerInvocationMethodProvider(final InvocationMethodProvider invocationMethodProvider) {
		Path.invocationMethodProvider = invocationMethodProvider;
	}

	public static InvocationMethodProvider getInvocationMethodProvider() {
		if (invocationMethodProvider == null) {
			// TODO: XAS.do not throw an exception. Use a default invocation method provider
			// throw new RuntimeException("No InvocationMethodProvider has been registered. Register one to run.");
		}
		return invocationMethodProvider;
	}

	public static <T> T on(final Class<T> type) {
		return on(type, true);
	}

	public static <T> T on(final Class<T> type, final boolean init) {
		if (init) {
			interceptorThreadLocal.remove();
			InvocationMethodProvider invocationMethodProvider = getInvocationMethodProvider();
			// XAS. Not sure about that. Use default dummy proxy and interceptor if no invocationMethodProvider is set
			if (invocationMethodProvider == null) {
				interceptorThreadLocal.set(new HdivEmptyMethodInterceptor());
			}
			else {
				interceptorThreadLocal.set(getInterceptor(getInvocationMethodProvider(), getHdivProxyFactory(), type));
			}
		}
		return getHdivProxyFactory().getProxyWithInterceptor(type, interceptorThreadLocal.get(), type.getClassLoader());
	}

	public static <T> T on(final Class<T> type, final HdivMethodInterceptor rmi) {
		return getHdivProxyFactory().getProxyWithInterceptor(type, rmi, type.getClassLoader());
	}

	@SuppressWarnings("unchecked")
	public static <T> T collection(final Collection<? extends T> collection) {
		ResolvableType resolvable = ResolvableType.forMethodReturnType(getInvocationMethodProvider().getLastInvocationMethod(collection));

		return on((Class<T>) resolvable.getGeneric(0).getRawClass(), false);
	}

	@SuppressWarnings("unchecked")
	public static <T> T collection(final Collection<? extends T> collection, final HdivMethodInterceptor rmi) {

		ResolvableType resolvable = ResolvableType.forMethodReturnType(getInvocationMethodProvider().getLastInvocationMethod(collection));

		return on((Class<T>) resolvable.getGeneric(0).getRawClass(), rmi);
	}

	public static String path(final Object obj) {
		HdivMethodInterceptor interceptor = interceptorThreadLocal.get();
		if (interceptor == null) {

			throw new IllegalArgumentException("Path.on(Class) should be called first");
		}
		interceptorThreadLocal.remove();
		return interceptor.getInvocation().toString();
	}

	public static interface InvocationAdvice {
		Object getInvocation();

		Iterator<Object> getObjectParameters();
	}

	public static <T> HdivProxyFactory getHdivProxyFactory() {
		if (USING_SPRING) {
			return SpringHdivProxyFactory.getInstance();
		}
		else {
			return JavaHdivProxyFactory.getInstance();
		}

	}

	public static HdivMethodInterceptor getInterceptor(final InvocationMethodProvider invocationMethodProvider,
			final HdivProxyFactory hdivProxyFactory, final Class<?> targetType) {
		if (USING_SPRING) {
			return new RecordingMethodInterceptor(invocationMethodProvider, hdivProxyFactory, targetType);
		}
		else {
			return new HdivDefaultMethodInterceptor(invocationMethodProvider, hdivProxyFactory, targetType);
		}
	}

	public static class PathBuilder {

		private HdivMethodInterceptor rmi;

		public <T> T on(final Class<T> clazz) {
			rmi = getInterceptor(getInvocationMethodProvider(), getHdivProxyFactory(), clazz);
			T on = Path.on(clazz, rmi);
			return on;
		}

		public String build(final Object object) {
			return build();
		}

		public String build() {
			return rmi.getInvocation().toString();
		}

		public <E> E collection(final Collection<? extends E> collection) {
			return Path.collection(collection, rmi);
		}

	}
}
