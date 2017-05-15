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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.hdiv.services.SecureIdContainer.VoidSecureIdContainer;

@Retention(RUNTIME)
@Target({ METHOD, PARAMETER, FIELD })
public @interface TrustAssertion {

	public static final String WILDCARD_LIST_MASK = "[*]";

	public static final String EMPTY = "EMPTY";

	/**
	 * Model class that defines the property if applies
	 * @return model class
	 */
	Class<? extends SecureIdContainer> idFor() default VoidSecureIdContainer.class;

	String plainIdFor() default EMPTY;

	/**
	 * If the annotated element is not an Object by default nid (Native Id) value is included
	 * @return include nid value
	 */
	TriState nid() default TriState.UNDEFINED;

	Type type() default Type.FROM_JAVA;

	int max() default Integer.MAX_VALUE;

	int min() default Integer.MIN_VALUE;

	int minLength() default Integer.MIN_VALUE;

	int maxLength() default Integer.MAX_VALUE;

	String pattern() default "";

	int step() default 0;

	TriState required() default TriState.UNDEFINED;

	TriState readOnly() default TriState.UNDEFINED;

	boolean ignored() default false;

	/**
	 * Allows to pass String arguments to the Options implementation. By default, a String array can be used to define possible values,
	 * since the default Options implementation is {@link StringOptions}
	 *
	 * @return arguments to the Options implementation. For the default {@link StringOptions}, an array of possible values.
	 */
	String[] values() default {};

	/**
	 * Specifies an implementation of the {@link Options} interface which provides possible values.
	 *
	 * @return implementation class of {@link Options}
	 */
	Class<? extends Options<?>> options() default StringOptions.class;

	/**
	 * When getting possible values using {@link Options#get}, pass the arguments having these names.
	 *
	 * @return names of the arguments whose value should be passed to {@link Options#get}
	 */
	String[] args() default {};

	/**
	 * Marks the type of select, in case of {@link SuggestType#EXTERNAL} the data may be outside the select, for example as a variable in
	 * HAL response rather than in HAL-FORMS document
	 * 
	 * @return the {@link SuggestType}
	 */
	SuggestType suggestType() default SuggestType.INTERNAL;

	boolean wildcardCollection() default false;

	boolean recursiveNavigation() default false;

	String originMask() default EMPTY;
}
