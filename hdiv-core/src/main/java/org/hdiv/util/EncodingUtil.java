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
package org.hdiv.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.cipher.ICipherHTTP;
import org.hdiv.cipher.Key;
import org.hdiv.exception.HDIVException;
import org.hdiv.session.ISession;

/**
 * Class containing utility methods for encoding.
 * 
 * @author Roberto Velasco
 * @author Gorka Vicente
 */
public class EncodingUtil {

	/**
	 * Commons Logging instance.
	 */
	private static final Log log = LogFactory.getLog(EncodingUtil.class);

	/**
	 * Name of a supported charset
	 */
	public static final String ZIP_CHARSET = "ISO-8859-1";

	/**
	 * Wrapper for http session
	 */
	private ISession session;

	/**
	 * Provides the funtionallity of a message digest algorithm.
	 */
	private MessageDigest messageDigest;

	/**
	 * Default message digest algorithm
	 */
	private String algorithmName = "MD5";

	/**
	 * Initialize the EncodingUtil with Http Session and message resource.
	 */
	public void init() {

		try {
			this.messageDigest = MessageDigest.getInstance(algorithmName);
			if (log.isDebugEnabled()) {
				log.debug("MessageDigest created: " + messageDigest);
			}
		} catch (NoSuchAlgorithmException e) {
			throw new HDIVException(e.getMessage());
		}
	}

	/**
	 * The object <code>obj</code> is compressed, encrypted and coded in Base64.
	 * 
	 * @param obj
	 *            Object to encrypt
	 * @return Objet <code>obj</code> compressed, encrypted and coded in Base64
	 * @throws HDIVException
	 *             if there is an error encoding object <code>data</code>
	 * @see java.util.zip.GZIPOutputStream#GZIPOutputStream(java.io.OutputStream)
	 * @see org.apache.commons.codec.net.URLCodec#encodeUrl(java.util.BitSet, byte[])
	 * @see org.apache.commons.codec.binary.Base64#encode(byte[])
	 */
	public String encode64Cipher(Object obj) {

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			OutputStream zos = new GZIPOutputStream(baos);
			ObjectOutputStream oos = new ObjectOutputStream(zos);
			oos.writeObject(obj);
			oos.close();
			zos.close();
			baos.close();

			ICipherHTTP cipher = this.session.getEncryptCipher();
			// Get Key from session
			Key key = this.session.getCipherKey();
			byte[] cipherData;
			synchronized (cipher) {
				cipher.initEncryptMode(key);
				cipherData = cipher.encrypt(baos.toByteArray());
			}

			// Encodes an array of bytes into an array of URL safe 7-bit characters.
			// Unsafe characters are escaped.
			byte[] encodedData = URLCodec.encodeUrl(null, cipherData);

			// Encodes a byte[] containing binary data, into a byte[] containing
			// characters in the Base64 alphabet.
			Base64 base64Codec = new Base64();
			return new String(base64Codec.encode(encodedData), ZIP_CHARSET);

		} catch (Exception e) {
			String errorMessage = HDIVUtil.getMessage("encode.message");
			throw new HDIVException(errorMessage, e);
		}
	}

	/**
	 * Decodes Base64 alphabet characters of the string <code>s</code>, decrypts this string and finally decompresses
	 * it.
	 * 
	 * @param s
	 *            data to decrypt
	 * @return decoded data
	 * @throws HDIVException
	 *             if there is an error decoding object <code>data</code>
	 * @see org.apache.commons.codec.binary.Base64#decode(byte[])
	 * @see org.apache.commons.codec.net.URLCodec#decode(byte[])
	 * @see java.util.zip.GZIPInputStream#GZIPInputStream(java.io.InputStream)
	 */
	public Object decode64Cipher(String s) {

		try {
			Base64 base64Codec = new Base64();
			// Decodes string s containing characters in the Base64 alphabet.
			byte[] encryptedData = base64Codec.decode(s.getBytes(ZIP_CHARSET));

			// Decodes an array of URL safe 7-bit characters into an array of
			// original bytes. Escaped characters are converted back to their
			// original representation.
			byte[] encodedData = URLCodec.decodeUrl(encryptedData);

			ICipherHTTP cipher = this.session.getDecryptCipher();
			// Get Key from session
			Key key = this.session.getCipherKey();
			byte[] data;
			synchronized (cipher) {
				cipher.initDecryptMode(key);
				data = cipher.decrypt(encodedData);
			}

			ByteArrayInputStream decodedStream = new ByteArrayInputStream(data);
			InputStream unzippedStream = new GZIPInputStream(decodedStream);
			ObjectInputStream ois = new ObjectInputStream(unzippedStream);
			Object obj = ois.readObject();

			ois.close();
			unzippedStream.close();
			decodedStream.close();
			return obj;

		} catch (Exception e) {
			throw new HDIVException(HDIVErrorCodes.HDIV_PARAMETER_INCORRECT_VALUE);
		}
	}

	/**
	 * The object <code>obj</code> is compressed and coded in Base64.
	 * 
	 * @param obj
	 *            Object to encode
	 * @return Object <code>obj</code> compressed y encoded in Base64
	 * @throws HDIVException
	 *             if there is an error encoding object <code>data</code>
	 * @see java.util.zip.GZIPOutputStream#GZIPOutputStream(java.io.OutputStream)
	 * @see org.apache.commons.codec.net.URLCodec#encodeUrl(java.util.BitSet, byte[])
	 * @see org.apache.commons.codec.binary.Base64#encode(byte[])
	 */
	public String encode64(Object obj) {

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			OutputStream zos = new GZIPOutputStream(baos);
			ObjectOutputStream oos = new ObjectOutputStream(zos);
			oos.writeObject(obj);
			oos.close();
			zos.close();
			baos.close();

			// Encodes an array of bytes into an array of URL safe 7-bit characters.
			// Unsafe characters are escaped.
			byte[] encodedData = URLCodec.encodeUrl(null, baos.toByteArray());

			// Encodes a byte[] containing binary data, into a byte[] containing
			// characters in the Base64 alphabet.
			Base64 base64Codec = new Base64();
			return new String(base64Codec.encode(encodedData), ZIP_CHARSET);

		} catch (Exception e) {
			String errorMessage = HDIVUtil.getMessage("encode.message");
			throw new HDIVException(errorMessage, e);
		}
	}

	/**
	 * Decodes Base64 alphabet characters of the string <code>s</code> and decompresses it.
	 * 
	 * @param s
	 *            data to decode
	 * @return decoded <code>s</code> string
	 * @throws HDIVException
	 *             if there is an error decoding object <code>data</code>
	 * @see org.apache.commons.codec.binary.Base64#decode(byte[])
	 * @see org.apache.commons.codec.net.URLCodec#decode(byte[])
	 * @see java.util.zip.GZIPInputStream#GZIPInputStream(java.io.InputStream)
	 */
	public Object decode64(String s) {

		try {
			Base64 base64Codec = new Base64();
			// Decodes string s containing characters in the Base64 alphabet.
			byte[] encryptedData = base64Codec.decode(s.getBytes(ZIP_CHARSET));

			// Decodes an array of URL safe 7-bit characters into an array of
			// original bytes. Escaped characters are converted back to their
			// original representation.
			byte[] encodedData = URLCodec.decodeUrl(encryptedData);

			ByteArrayInputStream decodedStream = new ByteArrayInputStream(encodedData);
			InputStream unzippedStream = new GZIPInputStream(decodedStream);
			ObjectInputStream ois = new ObjectInputStream(unzippedStream);
			Object obj = ois.readObject();

			ois.close();
			unzippedStream.close();
			decodedStream.close();
			return obj;

		} catch (Exception e) {
			throw new HDIVException(HDIVErrorCodes.HDIV_PARAMETER_INCORRECT_VALUE);
		}
	}

	/**
	 * Calculate <code>data</code> hash value.
	 * 
	 * @param data
	 *            data
	 * @return <code>data</code> hash value
	 */
	public String calculateStateHash(String data) {

		try {
			this.messageDigest.update(data.getBytes());

			// The digest is reset after this call is made
			byte[] raw = this.messageDigest.digest();

			return new String(raw);

		} catch (Exception e) {
			String errorMessage = HDIVUtil.getMessage("hash.digest");
			throw new HDIVException(errorMessage, e);
		}
	}

	/**
	 * @return Returns the session.
	 */
	public ISession getSession() {
		return session;
	}

	/**
	 * @param session
	 *            The session to set.
	 */
	public void setSession(ISession session) {
		this.session = session;
	}

	/**
	 * @param md
	 *            The message digest to set.
	 */
	public void setMessageDigest(MessageDigest md) {
		this.messageDigest = md;
	}

	/**
	 * @param algorithmName
	 *            The algorithm to set for the message digest.
	 */
	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}

}
