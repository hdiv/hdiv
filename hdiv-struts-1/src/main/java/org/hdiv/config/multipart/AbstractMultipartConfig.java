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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.filter.RequestWrapper;

/**
 * Class containing multipart request configuration and methods initialized from Spring Factory.
 * 
 * @author Gorka Vicente
 */
public abstract class AbstractMultipartConfig implements IMultipartConfig {

	/**
	 * Commons Logging instance.
	 */
	private static Log log = LogFactory.getLog(AbstractMultipartConfig.class);

	/**
	 * The default value for the maximum allowable size, in bytes, of an uploaded file. The value is equivalent to 2MB.
	 */
	public static final long DEFAULT_SIZE_MAX = 2 * 1024 * 1024;

	/**
	 * The default value for the threshold which determines whether an uploaded file will be written to disk or cached
	 * in memory. The value is equivalent to 250KB.
	 */
	public static final int DEFAULT_SIZE_THRESHOLD = 256 * 1024;

	/**
	 * The maximum allowable size, in bytes, of an uploaded file.
	 */
	protected String maxFileSize;

	/**
	 * The temporary working directory to use for file uploads.
	 */
	protected String tempDir = null;

	/**
	 * Converts a size value from a string representation to its numeric value. The string must be of the form nnnm,
	 * where nnn is an arbitrary decimal value, and m is a multiplier. The multiplier must be one of 'K', 'M' and 'G',
	 * representing kilobytes, megabytes and gigabytes respectively. If the size value cannot be converted, for example
	 * due to invalid syntax, the supplied default is returned instead.
	 * 
	 * @param sizeString
	 *            The string representation of the size to be converted.
	 * @param defaultSize
	 *            The value to be returned if the string is invalid.
	 * @return The actual size in bytes.
	 */
	public long convertSizeToBytes(String sizeString, long defaultSize) {

		if(sizeString == null) {
			return defaultSize;
		}
		int multiplier = 1;

		if (sizeString.endsWith("K")) {
			multiplier = 1024;
		} else if (sizeString.endsWith("M")) {
			multiplier = 1024 * 1024;
		} else if (sizeString.endsWith("G")) {
			multiplier = 1024 * 1024 * 1024;
		}
		if (multiplier != 1) {
			sizeString = sizeString.substring(0, sizeString.length() - 1);
		}

		long size = 0;
		try {
			size = Long.parseLong(sizeString);
		} catch (NumberFormatException nfe) {
			log.warn("Invalid format for file size ('" + sizeString + "'). Using default.");
			size = defaultSize;
			multiplier = 1;
		}

		return (size * multiplier);
	}

	/**
	 * Returns the path to the temporary directory to be used for uploaded files which are written to disk. The
	 * directory used is determined from the first of the following to be non-empty.
	 * <ol>
	 * <li>A temp dir explicitly defined using the <code>saveDir</code> attribute of the &lt;multipartConfig&gt; element
	 * in the Spring config file.</li>
	 * <li>The temp dir specified by the <code>javax.servlet.context.tempdir</code> attribute.</li>
	 * </ol>
	 * 
	 * @param servletContext
	 *            servlet context
	 * @return The path to the directory to be used to store uploaded files.
	 */
	public String getRepositoryPath(ServletContext servletContext) {

		// First, look for an explicitly defined temp dir.
		String saveDir = this.tempDir;

		if ((saveDir == null) || saveDir.equals("")) {
			File tempdir = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
			log.info("Unable to find 'saveDir' property setting. Defaulting to javax.servlet.context.tempdir");

			if (tempdir != null) {
				saveDir = tempdir.toString();
			}

			// If none, look for a container specified temp dir.
			if (saveDir == null || saveDir.length() == 0) {
				saveDir = System.getProperty("java.io.tmpdir");
			}

		} else {
			File multipartSaveDir = new File(saveDir);

			if (!multipartSaveDir.exists()) {
				multipartSaveDir.mkdir();
			}
		}

		if (log.isTraceEnabled()) {
			log.trace("File upload temp dir: " + saveDir);
		}

		return saveDir;
	}

	/**
	 * Adds a file parameter to the set of file parameters for this request and also to the list of all parameters.
	 * 
	 * @param request
	 *            The request in which the parameter was specified.
	 * @param item
	 *            The file item for the parameter to add.
	 */
	public void addFileParameter(RequestWrapper request, FileItem item) {

		List values;
		if (request.getFileElements().get(item.getFieldName()) != null) {
			values = (List) request.getFileElements().get(item.getFieldName());
		} else {
			values = new ArrayList();
		}

		values.add(item);
		request.addFileItem(item.getFieldName(), values);
	}

	/**
	 * @param maxFileSize
	 *            The maximum size to set.
	 */
	public void setMaxFileSize(String maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	/**
	 * @param tempDir
	 *            The tempDir to set.
	 */
	public void setTempDir(String tempDir) {
		this.tempDir = tempDir;
	}

	/**
	 * Returns the maximum allowable size, in bytes, of an uploaded file. The value is obtained from the Spring
	 * configuration.
	 * 
	 * @return The maximum allowable file size, in bytes.
	 */
	protected long getSizeMax() {
		return convertSizeToBytes(this.maxFileSize, AbstractMultipartConfig.DEFAULT_SIZE_MAX);
	}
}
