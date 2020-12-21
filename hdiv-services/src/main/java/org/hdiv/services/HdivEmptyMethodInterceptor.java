package org.hdiv.services;

public class HdivEmptyMethodInterceptor implements HdivMethodInterceptor {

	public HdivEmptyMethodInterceptor() {
	}

	public Object getInvocation() {
		return "Invoke real";
	}

}
