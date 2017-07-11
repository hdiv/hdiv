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
package org.hdiv.taglibs.standard.util;

import java.util.BitSet;

/**
 * Utilities for working with URLs.
 */
public class UrlUtil {
	/**
	 * <p>
	 * Valid characters in a scheme.
	 * </p>
	 * <p>
	 * RFC 1738 says the following:
	 * </p>
	 * <blockquote> Scheme names consist of a sequence of characters. The lower case letters "a"--"z", digits, and the characters plus
	 * ("+"), period ("."), and hyphen ("-") are allowed. For resiliency, programs interpreting URLs should treat upper case letters as
	 * equivalent to lower case in scheme names (e.g., allow "HTTP" as well as "http"). </blockquote>
	 * <p>
	 * We treat as absolute any URL that begins with such a scheme name, followed by a colon.
	 * </p>
	 */
	/*
	 * private static final String VALID_SCHEME_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789+.-";
	 */
	private static final BitSet VALID_SCHEME_CHARS;
	static {
		VALID_SCHEME_CHARS = new BitSet(128);
		VALID_SCHEME_CHARS.set('A', 'Z' + 1);
		VALID_SCHEME_CHARS.set('a', 'z' + 1);
		VALID_SCHEME_CHARS.set('0', '9' + 1);
		VALID_SCHEME_CHARS.set('+');
		VALID_SCHEME_CHARS.set('.');
		VALID_SCHEME_CHARS.set('-');
	}

	/**
	 * Determine if a URL is absolute by JSTL's definition.
	 */
	public static boolean isAbsoluteUrl(final String url) {
		// a null URL is not absolute, by our definition
		if (url == null) {
			return false;
		}

		// do a fast, simple check first
		int colonPos = url.indexOf(":");
		if (colonPos == -1) {
			return false;
		}

		// if we DO have a colon, make sure that every character
		// leading up to it is a valid scheme character
		for (int i = 0; i < colonPos; i++) {
			if (!VALID_SCHEME_CHARS.get(url.charAt(i))) {
				return false;
			}
		}

		// if so, we've got an absolute url
		return true;
	}

	public static String getScheme(final CharSequence url) {
		StringBuilder scheme = new StringBuilder();
		for (int i = 0; i < url.length(); i++) {
			char ch = url.charAt(i);
			if (ch == ':') {
				String result = scheme.toString();
				if (!"jar".equals(result)) {
					return result;
				}
			}
			scheme.append(ch);
		}
		throw new IllegalArgumentException("No scheme found: " + url);
	}
}
