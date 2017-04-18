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

@Retention(RUNTIME)
@Target({ METHOD, PARAMETER, FIELD })
public @interface TrustAssertion {

	/**
	 * Model class that defines the property if applies
	 * @return
	 */
	// TODO: review Void as default value
	Class<?> idFor() default Void.class;

	/**
	 * If the annotated element is not an Object by default nid (Native Id) value is included
	 * @return
	 */
	boolean nid() default true;

	Type type() default Type.FROM_JAVA;

	int max() default Integer.MAX_VALUE;

	int min() default Integer.MIN_VALUE;

	int minLength() default Integer.MIN_VALUE;

	int maxLength() default Integer.MAX_VALUE;

	String pattern() default "";

	int step() default 0;

	boolean required() default false;

	boolean readOnly() default false;
}
