package org.hdiv.services;

import java.io.Serializable;

import org.hdiv.context.RequestContextHolder;

public interface TrustValidationDefinition<T extends SecureIdentifiable<? extends Serializable>> {

	public void define(final RequestContextHolder request, final T secureIdentifiable, final TrustValidationBuilder builder);
}
