/**
 * Copyright 2005-2015 hdiv.org
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
package org.hdiv.config;

/**
 * Types of strategy to create and store the states created by HDIV.
 * 
 * @since 2.1.7
 */
public enum Strategy {

	/**
	 * All the states of the page are stored in the user's HttpSession.
	 */
	MEMORY,

	/**
	 * For each possible request of each page (link or form) an extra parameter (_HDIV_STATE_ by default) is added which
	 * represents the state of the request. To guarantee the integrity of the state itself, which is the base of the
	 * validation, it is ciphered using a symmetrical algorithm. No HttpSession is used in this strategy.
	 */
	CIPHER,

	/**
	 * This strategy is very similar to the Cipher strategy but in this case the state sent to the client is coded in
	 * Base64. To be able to check this parameter integrity, a hash of the state is generated before being sent to the
	 * client and it is stored in the user session. This strategy does not guarantee confidentiality because the state
	 * can be decoded if we have a high technical knowledge.
	 */
	HASH;

}
