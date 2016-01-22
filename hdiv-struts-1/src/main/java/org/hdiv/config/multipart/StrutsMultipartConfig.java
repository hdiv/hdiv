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
package org.hdiv.config.multipart;

import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.multipart.exception.HdivMultipartException;
import org.hdiv.filter.RequestWrapper;

/**
 * Class containing multipart request configuration and methods initialized from Spring Factory.
 * 
 * @author Gorka Vicente
 */
public class StrutsMultipartConfig extends AbstractMultipartConfig {

	/**
	 * Commons Logging instance.
	 */
	private static Log log = LogFactory.getLog(StrutsMultipartConfig.class);

	/**
	 * The size threshold which determines whether an uploaded file will be written to disk or cached in memory.
	 */
	protected String memFileSize;

	/**
	 * Parses the input stream and partitions the parsed items into a set of form fields and a set of file items.
	 * 
	 * @param request The multipart request wrapper.
	 * @param servletContext Our ServletContext object
	 * @return multipart processed request
	 * @throws HdivMultipartException if an unrecoverable error occurs.
	 */
	public HttpServletRequest handleMultipartRequest(RequestWrapper request, ServletContext servletContext)
			throws HdivMultipartException {

		DiskFileUpload upload = new DiskFileUpload();

		upload.setHeaderEncoding(request.getCharacterEncoding());
		// Set the maximum size before a FileUploadException will be thrown.
		upload.setSizeMax(getSizeMax());
		// Set the maximum size that will be stored in memory.
		upload.setSizeThreshold((int) getSizeThreshold());
		// Set the the location for saving data on disk.
		upload.setRepositoryPath(getRepositoryPath(servletContext));

		List<FileItem> items = null;
		try {
			items = upload.parseRequest(request);

		}
		catch (DiskFileUpload.SizeLimitExceededException e) {
			if (log.isErrorEnabled()) {
				log.error("Size limit exceeded exception");
			}
			// Special handling for uploads that are too big.
			throw new HdivMultipartException(e);

		}
		catch (FileUploadException e) {
			if (log.isErrorEnabled()) {
				log.error("Failed to parse multipart request", e);
			}
			throw new HdivMultipartException(e);
		}

		// Process the uploaded items
		Iterator<FileItem> iter = items.iterator();
		while (iter.hasNext()) {
			FileItem item = iter.next();

			if (item.isFormField()) {
				this.addTextParameter(request, item);
			}
			else {
				this.addFileParameter(request, item);
			}
		}
		return request;
	}

	/**
	 * Returns the size threshold which determines whether an uploaded file will be written to disk or cached in memory.
	 * 
	 * @return The size threshold, in bytes.
	 */
	protected long getSizeThreshold() {
		return convertSizeToBytes(this.memFileSize, AbstractMultipartConfig.DEFAULT_SIZE_THRESHOLD);
	}

	/**
	 * Adds a regular text parameter to the set of text parameters for this request. Handles the case of multiple values
	 * for the same parameter by using an array for the parameter value.
	 * 
	 * @param request The request in which the parameter was specified.
	 * @param item The file item for the parameter to add.
	 */
	public void addTextParameter(RequestWrapper request, FileItem item) {

		String name = item.getFieldName();
		String value = null;
		boolean haveValue = false;
		String encoding = request.getCharacterEncoding();

		if (encoding != null) {
			try {
				value = item.getString(encoding);
				haveValue = true;
			}
			catch (Exception e) {
				// Handled below, since haveValue is false.
			}
		}
		if (!haveValue) {
			try {
				value = item.getString("ISO-8859-1");
			}
			catch (java.io.UnsupportedEncodingException uee) {
				value = item.getString();
			}
			haveValue = true;
		}

		String[] oldArray = (String[]) request.getParameterValues(name);
		String[] newArray;

		if (oldArray != null) {
			newArray = new String[oldArray.length + 1];
			System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);
			newArray[oldArray.length] = value;
		}
		else {
			newArray = new String[] { value };
		}

		request.addParameter(name, newArray);
	}

	public void cleanupMultipart(HttpServletRequest request) {
	}

	/**
	 * @param memFileSize The memFileSize to set.
	 */
	public void setMemFileSize(String memFileSize) {
		this.memFileSize = memFileSize;
	}
}
