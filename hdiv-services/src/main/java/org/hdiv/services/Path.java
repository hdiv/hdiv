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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.target.EmptyTargetSource;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.Factory;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.core.ResolvableType;
import org.springframework.objenesis.ObjenesisStd;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public class Path {

	public static ThreadLocal<RecordingMethodInterceptor> interceptorThreadLocal = new ThreadLocal<Path.RecordingMethodInterceptor>();

	private static InvocationMethodProvider invocationMethodProvider = null;

	public static void registerInvocationMethodProvider(final InvocationMethodProvider invocationMethodProvider) {
		Path.invocationMethodProvider = invocationMethodProvider;
	}

	public static InvocationMethodProvider getInvocationMethodProvider() {
		if (invocationMethodProvider == null) {
			throw new RuntimeException("No InvocationMethodProvider has been registered. Register one to run.");
		}
		return invocationMethodProvider;
	}

	public static <T> T on(final Class<T> type) {
		return on(type, true);
	}

	public static <T> T on(final Class<T> type, final boolean init) {
		if (init) {
			interceptorThreadLocal.remove();
			interceptorThreadLocal.set(new RecordingMethodInterceptor(type));
		}
		return getProxyWithInterceptor(type, interceptorThreadLocal.get(), type.getClassLoader());
	}

	public static <T> T on(final Class<T> type, final RecordingMethodInterceptor rmi) {
		return getProxyWithInterceptor(type, rmi, type.getClassLoader());
	}

	@SuppressWarnings("unchecked")
	public static <T> T collection(final Collection<? extends T> collection) {
	
		ResolvableType resolvable = ResolvableType.forMethodReturnType(getInvocationMethodProvider().getLastInvocationMethod(collection));

		return on((Class<T>) resolvable.getGeneric(0).getRawClass(), false);
	}

	@SuppressWarnings("unchecked")
	public static <T> T collection(final Collection<? extends T> collection, final RecordingMethodInterceptor rmi) {
	
		ResolvableType resolvable = ResolvableType.forMethodReturnType(getInvocationMethodProvider().getLastInvocationMethod(collection));

		return on((Class<T>) resolvable.getGeneric(0).getRawClass(), rmi);
	}

	public static String path(final Object obj) {
		RecordingMethodInterceptor interceptor = interceptorThreadLocal.get();
		Assert.notNull(interceptor, "Path.on(Class) should be called first");
		interceptorThreadLocal.remove();
		return interceptor.getInvocation().toString();
	}

	public static interface InvocationAdvice {
		Object getInvocation();

		Iterator<Object> getObjectParameters();
	}

	public static class RecordingMethodInterceptor implements MethodInterceptor, InvocationAdvice, // LastInvocationAware,
			org.springframework.cglib.proxy.MethodInterceptor {

		private final Class<?> targetType;

		private final Object[] objectParameters;

		private Object invocation;

		public RecordingMethodInterceptor(final Class<?> targetType, final Object... objectParameters) {
			this.targetType = targetType;
			this.objectParameters = objectParameters;
		}

		public Iterator<Object> getObjectParameters() {
			return Arrays.asList(objectParameters).iterator();
		}

		public Object getInvocation() {
			return invocation;
		}

		public Object invoke(final org.aopalliance.intercept.MethodInvocation invocation) throws Throwable {
			return intercept(invocation.getThis(), invocation.getMethod(), invocation.getArguments(), null);
		}

		public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy arg3) throws Throwable {
			if (getInvocationMethodProvider().getLastInvocationDefMethod().equals(method)) {
				return getInvocation();
			}
			else if (getInvocationMethodProvider().getObjectParametersDefMethod().equals(method)) {
				return getObjectParameters();
			}
			else if (Object.class.equals(method.getDeclaringClass())) {
				return ReflectionUtils.invokeMethod(method, obj, args);
			}
			invocation = getInvocationMethodProvider().getMethodInvocationIntance(targetType, method, args, getInvocation());

			Class<?> returnType = method.getReturnType();
			if (Modifier.isFinal(returnType.getModifiers())) {
				return null;
			}
			else {
				return returnType.cast(getProxyWithInterceptor(returnType, this, obj.getClass().getClassLoader()));
			}
		}

	}

	private static ObjenesisStd OBJENESIS = new ObjenesisStd();

	private static <T> T getProxyWithInterceptor(final Class<?> type, final RecordingMethodInterceptor interceptor,
			final ClassLoader classLoader) {
		return getProxyWithInterceptor(type, interceptor, classLoader, false);
	}

	@SuppressWarnings("unchecked")
	private static <T> T getProxyWithInterceptor(final Class<?> type, final RecordingMethodInterceptor interceptor,
			final ClassLoader classLoader, final boolean isfinal) {

		if (type.isInterface()) {

			ProxyFactory factory = new ProxyFactory(EmptyTargetSource.INSTANCE);
			factory.addInterface(type);
			factory.addInterface(getInvocationMethodProvider().getInvokationAwareClass());
			factory.addAdvice(interceptor);

			return (T) factory.getProxy();
		}

		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(type);
		enhancer.setInterfaces(new Class<?>[] { getInvocationMethodProvider().getInvokationAwareClass() });
		enhancer.setCallbackType(org.springframework.cglib.proxy.MethodInterceptor.class);
		enhancer.setClassLoader(classLoader);

		Factory factory = (Factory) OBJENESIS.newInstance(enhancer.createClass());
		factory.setCallbacks(new Callback[] { interceptor });
		return (T) factory;
	}

	public static class PathBuilder {

		private RecordingMethodInterceptor rmi;

		public <T> T on(final Class<T> clazz) {
			rmi = new RecordingMethodInterceptor(clazz);
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
