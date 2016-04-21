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
package org.hdiv.upload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.upload.CommonsMultipartRequestHandler;
import org.apache.struts.upload.FormFile;
import org.apache.struts.upload.MultipartRequestWrapper;
import org.hdiv.config.multipart.IMultipartConfig;
import org.hdiv.config.multipart.exception.HdivMultipartException;
import org.hdiv.filter.RequestWrapper;
import org.hdiv.util.HDIVUtil;

public class HDIVMultipartRequestHandler extends CommonsMultipartRequestHandler {

	/**
	 * Commons Logging instance.
	 */
	protected static Log log = LogFactory.getLog(HDIVMultipartRequestHandler.class);

	/**
	 * The combined text and file request parameters.
	 */
	private Hashtable elementsAll;

	/**
	 * The file request parameters.
	 */
	private Hashtable elementsFile;

	/**
	 * The text request parameters.
	 */
	private Hashtable elementsText;

	/**
	 * Parses the input stream and partitions the parsed items into a set of form fields and a set of file items. In the
	 * process, the parsed items are translated from Commons FileUpload <code>FileItem</code> instances to Struts
	 * <code>FormFile</code> instances.
	 * 
	 * @param request The multipart request to be processed.
	 * @throws ServletException if an unrecoverable error occurs.
	 */
	public void handleRequest(HttpServletRequest request) throws ServletException {

		// Create the hash tables to be populated.
		elementsText = new Hashtable();
		elementsFile = new Hashtable();
		elementsAll = new Hashtable();

		if (request instanceof MultipartRequestWrapper) {

			MultipartRequestWrapper wrapper = (MultipartRequestWrapper) request;
			ServletRequest origRequest = wrapper.getRequest();
			if (origRequest == null)
				return;

			RequestWrapper requestWrapper = HDIVUtil.getNativeRequest(origRequest, RequestWrapper.class);
			if (requestWrapper == null)
				return;

			Boolean maxLengthExceeded = (Boolean) request.getAttribute(ATTRIBUTE_MAX_LENGTH_EXCEEDED);
			if ((maxLengthExceeded != null) && (maxLengthExceeded.booleanValue())) {
				return;
			}

			HdivMultipartException multipartException = (HdivMultipartException) request
					.getAttribute(IMultipartConfig.FILEUPLOAD_EXCEPTION);
			if (multipartException != null) {
				Exception orig = multipartException.getOriginal();
				log.error("Failed to parse multipart request", orig);
				if (orig instanceof ServletException) {
					throw (ServletException) orig;
				}
				else {
					throw new ServletException("Failed to parse multipart request", orig);
				}
			}

			// file items
			Map<String, Object> items = requestWrapper.getFileElements();
			for (Object fileItem : items.values()) {
				if (items != null) {
					addFileParameter((List) fileItem);
				}
			}

			// text items
			items = requestWrapper.getTextElements();

			for (String currentTextKey : items.keySet()) {

				String[] currentTextValue = (String[]) items.get(currentTextKey);
				this.addTextParameter(wrapper, currentTextKey, currentTextValue);
			}
		}
	}

	/**
	 * Adds a regular text parameter to the set of text parameters for this request and also to the list of all
	 * parameters. Handles the case of multiple values for the same parameter by using an array for the parameter value.
	 * 
	 * @param request The request in which the parameter was specified.
	 * @param name Parameter name.
	 * @param value Parameter value.
	 */
	protected void addTextParameter(HttpServletRequest request, String name, String[] value) {

		if (request instanceof MultipartRequestWrapper) {
			MultipartRequestWrapper wrapper = (MultipartRequestWrapper) request;

			for (int i = 0; i < value.length; i++) {
				wrapper.setParameter(name, value[i]);
			}
		}

		elementsText.put(name, value);
		elementsAll.put(name, value);
	}

	/**
	 * Adds a file parameter to the set of file parameters for this request and also to the list of all parameters.
	 * 
	 * @param items file items for the parameter to add
	 */
	protected void addFileParameter(List items) {

		FileItem currentItem;
		for (int i = 0; i < items.size(); i++) {

			currentItem = (FileItem) items.get(i);
			FormFile formFile = new CommonsFormFile(currentItem);

			elementsFile.put(currentItem.getFieldName(), formFile);
			elementsAll.put(currentItem.getFieldName(), formFile);
		}
	}

	/**
	 * Returns a hash table containing the text (that is, non-file) request parameters.
	 * 
	 * @return The text request parameters.
	 */
	public Hashtable getTextElements() {
		return this.elementsText;
	}

	/**
	 * Returns a hash table containing the file (that is, non-text) request parameters.
	 * 
	 * @return The file request parameters.
	 */
	public Hashtable getFileElements() {
		return this.elementsFile;
	}

	/**
	 * Returns a hash table containing both text and file request parameters.
	 * 
	 * @return The text and file request parameters.
	 */
	public Hashtable getAllElements() {
		return this.elementsAll;
	}

	/**
	 * Cleans up when a problem occurs during request processing.
	 */
	public void rollback() {

		Iterator iter = elementsFile.values().iterator();

		while (iter.hasNext()) {
			FormFile formFile = (FormFile) iter.next();

			formFile.destroy();
		}
	}

	// ---------------- Inner Class --------------------

	/**
	 * This class implements the Struts <code>FormFile</code> interface by wrapping the Commons FileUpload
	 * <code>FileItem</code> interface. This implementation is <i>read-only</i>; any attempt to modify an instance of
	 * this class will result in an <code>UnsupportedOperationException</code>.
	 */
	static class CommonsFormFile implements FormFile, Serializable {

		/**
		 * The <code>FileItem</code> instance wrapped by this object.
		 */
		FileItem fileItem;

		/**
		 * Constructs an instance of this class which wraps the supplied file item.
		 * 
		 * @param fileItem The Commons file item to be wrapped.
		 */
		public CommonsFormFile(FileItem fileItem) {
			this.fileItem = fileItem;
		}

		/**
		 * Returns the content type for this file.
		 * 
		 * @return A String representing content type.
		 */
		public String getContentType() {
			return fileItem.getContentType();
		}

		/**
		 * Sets the content type for this file.
		 * <p>
		 * NOTE: This method is not supported in this implementation.
		 * 
		 * @param contentType A string representing the content type.
		 */
		public void setContentType(String contentType) {
			throw new UnsupportedOperationException("The setContentType() method is not supported.");
		}

		/**
		 * Returns the size, in bytes, of this file.
		 * 
		 * @return The size of the file, in bytes.
		 */
		public int getFileSize() {
			return (int) fileItem.getSize();
		}

		/**
		 * Sets the size, in bytes, for this file.
		 * <p>
		 * NOTE: This method is not supported in this implementation.
		 * 
		 * @param filesize The size of the file, in bytes.
		 */
		public void setFileSize(int filesize) {
			throw new UnsupportedOperationException("The setFileSize() method is not supported.");
		}

		/**
		 * Returns the (client-side) file name for this file.
		 * 
		 * @return The client-size file name.
		 */
		public String getFileName() {
			return getBaseFileName(fileItem.getName());
		}

		/**
		 * Sets the (client-side) file name for this file.
		 * <p>
		 * NOTE: This method is not supported in this implementation.
		 * 
		 * @param fileName The client-side name for the file.
		 */
		public void setFileName(String fileName) {
			throw new UnsupportedOperationException("The setFileName() method is not supported.");
		}

		/**
		 * Returns the data for this file as a byte array. Note that this may result in excessive memory usage for large
		 * uploads. The use of the {@link #getInputStream() getInputStream} method is encouraged as an alternative.
		 * 
		 * @return An array of bytes representing the data contained in this form file.
		 * 
		 * @exception FileNotFoundException If some sort of file representation cannot be found for the FormFile
		 * @exception IOException If there is some sort of IOException
		 */
		public byte[] getFileData() throws FileNotFoundException, IOException {
			return fileItem.get();
		}

		/**
		 * Get an InputStream that represents this file. This is the preferred method of getting file data.
		 * 
		 * @exception FileNotFoundException If some sort of file representation cannot be found for the FormFile
		 * @exception IOException If there is some sort of IOException
		 */
		public InputStream getInputStream() throws FileNotFoundException, IOException {
			return fileItem.getInputStream();
		}

		/**
		 * Destroy all content for this form file. Implementations should remove any temporary files or any temporary
		 * file data stored somewhere
		 */
		public void destroy() {
			fileItem.delete();
		}

		/**
		 * Returns the base file name from the supplied file path. On the surface, this would appear to be a trivial
		 * task. Apparently, however, some Linux JDKs do not implement <code>File.getName()</code> correctly for Windows
		 * paths, so we attempt to take care of that here.
		 * 
		 * @param filePath The full path to the file.
		 * 
		 * @return The base file name, from the end of the path.
		 */
		protected String getBaseFileName(String filePath) {

			// First, ask the JDK for the base file name.
			String fileName = new File(filePath).getName();

			// Now check for a Windows file name parsed incorrectly.
			int colonIndex = fileName.indexOf(":");
			if (colonIndex == -1) {
				// Check for a Windows SMB file path.
				colonIndex = fileName.indexOf("\\\\");
			}
			int backslashIndex = fileName.lastIndexOf("\\");

			if (colonIndex > -1 && backslashIndex > -1) {
				// Consider this filename to be a full Windows path, and parse it
				// accordingly to retrieve just the base file name.
				fileName = fileName.substring(backslashIndex + 1);
			}

			return fileName;
		}

		/**
		 * Returns the (client-side) file name for this file.
		 * 
		 * @return The client-size file name.
		 */
		public String toString() {
			return getFileName();
		}
	}

}
