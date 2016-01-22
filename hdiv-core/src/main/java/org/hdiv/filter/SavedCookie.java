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
package org.hdiv.filter;

import java.io.Serializable;

import javax.servlet.http.Cookie;

/**
 * Stores off the values of a cookie in a serializable holder.
 * 
 * @author Gorka Vicente
 * @since HDIV 1.1.1
 */
public class SavedCookie implements Serializable {

	/**
	 * Universal version identifier. Deserialization uses this number to ensure that a loaded class corresponds exactly
	 * to a serialized object.
	 */
	private static final long serialVersionUID = 4731047668982223493L;

	private String name;

	private String value;

	private String comment;

	private String domain;

	private int maxAge;

	private String path;

	private boolean secure;

	private int version;

	/**
	 * Constructs a new SavedCookie.
	 * 
	 * @param name Cookie name
	 * @param value Cookie value
	 * @param comment Cookie comment
	 * @param domain Cookie domain name
	 * @param maxAge Cookie maximum age
	 * @param path Cookie path
	 * @param secure Cookie secure boolean value
	 * @param version Cookie version number
	 */
	public SavedCookie(String name, String value, String comment, String domain, int maxAge, String path,
			boolean secure, int version) {

		this.name = name;
		this.value = value;
		this.comment = comment;
		this.domain = domain;
		this.maxAge = maxAge;
		this.path = path;
		this.secure = secure;
		this.version = version;
	}

	/**
	 * Constructs a new SavedCookie from <code>cookie</code> object.
	 * 
	 * @param cookie original cookie
	 */
	public SavedCookie(Cookie cookie) {

		this(cookie.getName(), cookie.getValue(), cookie.getComment(), cookie.getDomain(), cookie.getMaxAge(), cookie
				.getPath(), cookie.getSecure(), cookie.getVersion());
	}

	/**
	 * Constructs a new SavedCookie.
	 * 
	 * @param name Cookie name
	 * @param value Cookie value
	 */
	public SavedCookie(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Compares this Cookie to the specified object. The result is <code>true</code> if and only if the argument is not
	 * <code>null</code> and is a <code>Cookie</code> object that represents the same sequence of values as this object.
	 * 
	 * @param c the object to compare this <code>Cookie</code> against.
	 * @param cookiesConfidentialityActivated cookies' confidentiality indicator
	 * @return True if the <code>Cookie</code> are equal. False otherwise.
	 */
	public boolean isEqual(Cookie c, boolean cookiesConfidentialityActivated) {

		boolean result = (this.getName() == null ? c.getName() == null : this.getName().equals(c.getName()));
		if (result) {
			if (this.getValue() == null) {
				result = (c.getValue() == null);
			}
			else {
				if (cookiesConfidentialityActivated) {
					result = c.getValue().equals("0");
				}
				else {
					result = this.getValue().equals(c.getValue());
				}
			}
		}
		if (result) {
			result = (this.getDomain() == null ? c.getDomain() == null : this.getDomain().equals(c.getDomain()));
		}

		return result;
	}

	/**
	 * @return Returns the comment.
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @return Returns the domain.
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * @return Returns the maxAge.
	 */
	public int getMaxAge() {
		return maxAge;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Returns the value.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return Returns the path.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return Returns the secure.
	 */
	public boolean isSecure() {
		return secure;
	}

	/**
	 * @return Returns the version.
	 */
	public int getVersion() {
		return version;
	}

}
