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
package org.hdiv.util;

import java.util.UUID;

import org.hdiv.exception.HDIVException;
import org.hdiv.idGenerator.FastUUID;

public final class HDIVStateUtils {

	private HDIVStateUtils() {
	}

	public static int numParts(final String hdivState) {
		return hdivState.split(Constants.STATE_ID_STR_SEPARATOR).length;
	}

	public static String encode(final UUID pageId, final int stateId, final String suffix) {
		// 11-0-C1EF82C48A86DE9BB907F37454998CC3
		StringBuilder sb = new StringBuilder(40);
		uuidToString(sb, pageId);
		return sb.append(Constants.STATE_ID_SEPARATOR).append(stateId).append(Constants.STATE_ID_SEPARATOR).append(suffix).toString();
	}

	public static UUID getPageId(final String stateId) {
		int firstSeparator = stateId.indexOf(Constants.STATE_ID_SEPARATOR);
		if (firstSeparator == -1) {
			throw new HDIVException(HDIVErrorCodes.INVALID_HDIV_PARAMETER_VALUE);
		}
		try {
			return new UUID(0, Integer.parseInt(stateId.substring(0, firstSeparator)));
		}
		catch (NumberFormatException ex) {
			throw new HDIVException(HDIVErrorCodes.INVALID_HDIV_PARAMETER_VALUE);
		}
	}

	public static String uuidToString(final UUID id) {
		if (id.getMostSignificantBits() == 0) {
			return Long.toString(id.getLeastSignificantBits());
		}
		else {
			return "U" + FastUUID.asHex(id.getMostSignificantBits(), id.getLeastSignificantBits());
		}
	}

	public static void uuidToString(final StringBuilder sb, final UUID id) {
		if (id.getMostSignificantBits() == 0) {
			sb.append(id.getLeastSignificantBits());
		}
		else {
			sb.append('U').append(FastUUID.asHexChars(id.getMostSignificantBits(), id.getLeastSignificantBits()));
		}
	}

	public static UUID parsePageId(final String pageId) {
		try {
			if (pageId.startsWith("U")) {
				return new UUID(parseUnsignedLong(pageId.substring(1, 17), 16), parseUnsignedLong(pageId.substring(17, 33), 16));
			}
			else {
				return new UUID(0, Long.parseLong(pageId));
			}
		}
		catch (Exception e) {
			throw new HDIVException(HDIVErrorCodes.INVALID_PAGE_ID, e);
		}
	}

	private static long parseUnsignedLong(final String s, final int radix) throws NumberFormatException {
		if (s == null) {
			throw new NumberFormatException("null");
		}

		int len = s.length();
		if (len > 0) {
			char firstChar = s.charAt(0);
			if (firstChar == '-') {
				throw new NumberFormatException(String.format("Illegal leading minus sign " + "on unsigned string %s.", s));
			}
			else {
				if (len <= 12 || // Long.MAX_VALUE in Character.MAX_RADIX is 13 digits
						radix == 10 && len <= 18) { // Long.MAX_VALUE in base 10 is 19 digits
					return Long.parseLong(s, radix);
				}

				// No need for range checks on len due to testing above.
				long first = Long.parseLong(s.substring(0, len - 1), radix);
				int second = Character.digit(s.charAt(len - 1), radix);
				if (second < 0) {
					throw new NumberFormatException("Bad digit at end of " + s);
				}
				long result = first * radix + second;
				if (compareUnsigned(result, first) < 0) {
					/*
					 * The maximum unsigned value, (2^64)-1, takes at most one more digit to represent than the maximum signed value,
					 * (2^63)-1. Therefore, parsing (len - 1) digits will be appropriately in-range of the signed parsing. In other words,
					 * if parsing (len -1) digits overflows signed parsing, parsing len digits will certainly overflow unsigned parsing.
					 *
					 * The compareUnsigned check above catches situations where an unsigned overflow occurs incorporating the contribution
					 * of the final digit.
					 */
					throw new NumberFormatException(String.format("String value %s exceeds " + "range of unsigned long.", s));
				}
				return result;
			}
		}
		else {
			throw new NumberFormatException(s);
		}
	}

	private static int compareUnsigned(final long x, final long y) {
		return compare(x + Long.MIN_VALUE, y + Long.MIN_VALUE);
	}

	private static int compare(final long x, final long y) {
		return x < y ? -1 : x == y ? 0 : 1;
	}

	public static int getStateId(final String stateId) {
		int start = stateId.indexOf(Constants.STATE_ID_SEPARATOR);
		try {
			return Integer.parseInt(stateId.substring(start + 1, stateId.indexOf(Constants.STATE_ID_SEPARATOR, start + 1)));
		}
		catch (NumberFormatException ex) {
			throw new HDIVException(HDIVErrorCodes.INVALID_HDIV_PARAMETER_VALUE);
		}
	}

	public static String getScopedState(final int stateId, final String token) {
		return Integer.toString(stateId) + Constants.STATE_ID_SEPARATOR + token;
	}

	public static int getStateFromScoped(final String scoped) {
		try {
			return Integer.parseInt(scoped.substring(0, scoped.indexOf(Constants.STATE_ID_SEPARATOR)));
		}
		catch (NumberFormatException ex) {
			throw new HDIVException(HDIVErrorCodes.INVALID_HDIV_PARAMETER_VALUE);
		}
	}

}
