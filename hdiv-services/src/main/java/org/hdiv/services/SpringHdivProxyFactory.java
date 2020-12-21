package org.hdiv.services;

import org.aopalliance.aop.Advice;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.target.EmptyTargetSource;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.Factory;
import org.springframework.objenesis.ObjenesisStd;

public class SpringHdivProxyFactory implements HdivProxyFactory {

	private static final ObjenesisStd OBJENESIS = new ObjenesisStd();

	private static SpringHdivProxyFactory instance = null;

	private SpringHdivProxyFactory() {

	}

	public static SpringHdivProxyFactory getInstance() {
		if (instance == null) {
			instance = new SpringHdivProxyFactory();
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	public <T> T getProxyWithInterceptor(final Class<T> type, final HdivMethodInterceptor interceptor, final ClassLoader classLoader,
			final boolean isfinal) {

		if (type.isInterface()) {

			ProxyFactory factory = new ProxyFactory(EmptyTargetSource.INSTANCE);
			factory.addInterface(type);
			if (interceptor instanceof Advice) {
				factory.addAdvice((Advice) interceptor);
			}
			else {
				// TODO: XAS. Log this scenario
			}

			return (T) factory.getProxy();
		}

		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(type);
		// enhancer.setInterfaces(new Class<?>[] { getInvocationMethodProvider().getInvokationAwareClass() });
		enhancer.setCallbackType(org.springframework.cglib.proxy.MethodInterceptor.class);
		enhancer.setClassLoader(classLoader);

		Factory factory = (Factory) OBJENESIS.newInstance(enhancer.createClass());
		if (interceptor instanceof Callback) {
			factory.setCallbacks(new Callback[] { (Callback) interceptor });
		}
		else {
			// TODO: XAS. Log this scenario
		}
		return (T) factory;
	}

	public <T> T getProxyWithInterceptor(final Class<T> type, final HdivMethodInterceptor interceptor, final ClassLoader classLoader) {
		return getProxyWithInterceptor(type, interceptor, classLoader, false);
	}
}
