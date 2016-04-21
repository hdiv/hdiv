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
package org.hdiv.idGenerator;

import java.io.Serializable;

/**
 * Implementation of <code>UidGenerator</code> that generates constant tokens. <b>Only for testing</b>
 * 
 * @author Gotzon Illarramendi
 * @since HDIV 2.1.0
 */
public class ConstantUidGenerator implements UidGenerator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.util.UidGenerator#generateUid()
	 */
	public Serializable generateUid() {

		return "TOKEN";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.util.UidGenerator#parseUid(java.lang.String)
	 */
	public Serializable parseUid(String encodedUid) {

		return encodedUid;
	}

}
