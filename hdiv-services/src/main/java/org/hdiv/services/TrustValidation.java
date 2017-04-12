package org.hdiv.services;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.hdiv.services.SecureIdentifiable;

@Retention(RUNTIME)
@Target({ TYPE })
public @interface TrustValidation {
	Class<? extends TrustValidationDefinition<? extends SecureIdentifiable<? extends Serializable>>> value();
}
