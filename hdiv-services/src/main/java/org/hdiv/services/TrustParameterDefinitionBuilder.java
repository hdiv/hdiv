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

public interface TrustParameterDefinitionBuilder {
	TrustParameterDefinitionBuilder readOnly(boolean readOnly);

	TrustParameterDefinitionBuilder required(boolean required);

	TrustParameterDefinitionBuilder pattern(String pattern);

	TrustParameterDefinitionBuilder max(int max);

	TrustParameterDefinitionBuilder min(int min);

	TrustParameterDefinitionBuilder maxLength(int max);

	TrustParameterDefinitionBuilder minLength(int min);

}
