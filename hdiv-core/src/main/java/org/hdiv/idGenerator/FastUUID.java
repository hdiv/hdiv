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

import java.security.SecureRandom;
import java.util.Random;

public class FastUUID {
	// @formatter:off
	 private static final char[] BYTE2HEX=(
			    "000102030405060708090A0B0C0D0E0F"+
			    "101112131415161718191A1B1C1D1E1F"+
			    "202122232425262728292A2B2C2D2E2F"+
			    "303132333435363738393A3B3C3D3E3F"+
			    "404142434445464748494A4B4C4D4E4F"+
			    "505152535455565758595A5B5C5D5E5F"+
			    "606162636465666768696A6B6C6D6E6F"+
			    "707172737475767778797A7B7C7D7E7F"+
			    "808182838485868788898A8B8C8D8E8F"+
			    "909192939495969798999A9B9C9D9E9F"+
			    "A0A1A2A3A4A5A6A7A8A9AAABACADAEAF"+
			    "B0B1B2B3B4B5B6B7B8B9BABBBCBDBEBF"+
			    "C0C1C2C3C4C5C6C7C8C9CACBCCCDCECF"+
			    "D0D1D2D3D4D5D6D7D8D9DADBDCDDDEDF"+
			    "E0E1E2E3E4E5E6E7E8E9EAEBECEDEEEF"+
			    "F0F1F2F3F4F5F6F7F8F9FAFBFCFDFEFF").toCharArray();
			   ; 
	// @formatter:on
	private static final Random random = new Random(new SecureRandom().nextLong());

	private static String asHex(final long mostSignificant, final long lestSignificant) {
		char[] hexChars = new char[32];
		long l = mostSignificant;
		for (int i = 0; i < 8; i++) {
			int v = ((byte) (l & 0xFF) & 0xFF) << 1;

			hexChars[14 - 2 * i] = BYTE2HEX[v];
			hexChars[15 - 2 * i] = BYTE2HEX[v + 1];
			l >>= 8;
		}
		l = lestSignificant;
		for (int i = 0; i < 8; i++) {
			int v = ((byte) (l & 0xFF) & 0xFF) << 1;
			hexChars[30 - 2 * i] = BYTE2HEX[v];
			hexChars[31 - 2 * i] = BYTE2HEX[v + 1];
			l >>= 8;
		}
		return new String(hexChars);
	}

	/**
	 * Method to generate the random GUID. Setting secure true enables each random number generated to be cryptographically strong. Secure
	 * false defaults to the standard Random function seeded with a single cryptographically strong random number.
	 */
	public static String get() {
		return asHex(random.nextLong(), random.nextLong());
	}

}
