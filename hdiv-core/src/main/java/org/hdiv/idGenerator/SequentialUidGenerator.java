/**
 * Copyright 2005-2011 hdiv.org
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
 * This implementation uses a sequence number to generate unique ids.<br/>
 * Only for testing.
 * 
 * @author Gotzon Illarramendi
 * @since HDIV 2.1.0
 */
public class SequentialUidGenerator implements UidGenerator {

	private long seq = 0;

	public Serializable generateUid() {

		Long id = null;

		synchronized (this) {
			id = new Long(this.seq);
			this.seq = this.seq + 1;
		}
		return id;
	}

	public Serializable parseUid(String encodedUid) {

		return encodedUid;
	}

}
