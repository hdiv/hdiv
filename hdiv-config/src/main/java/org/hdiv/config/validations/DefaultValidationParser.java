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
package org.hdiv.config.validations;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.hdiv.exception.HDIVException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX parser to read default editable validations xml file.
 *
 * @author Gotzon Illarramendi
 */
public class DefaultValidationParser extends DefaultHandler {

	/**
	 * Location of the xml file with default editable validations.
	 */
	private static final String DEFAULT_VALIDATION_PATH = "org/hdiv/config/validations/defaultEditableValidations.xml";

	/**
	 * List with processed validations.
	 */
	private final List<Map<ValidationParam, String>> validations = new ArrayList<Map<ValidationParam, String>>();

	/**
	 * Current Validation data.
	 */
	private Map<ValidationParam, String> validation = null;

	/**
	 * Read xml file from the default path.
	 */
	public void readDefaultValidations() {
		this.readDefaultValidations(DEFAULT_VALIDATION_PATH);
	}

	/**
	 * Read xml file from the given path.
	 *
	 * @param filePath xml file path
	 */
	public void readDefaultValidations(final String filePath) {

		try {
			ClassLoader classLoader = DefaultValidationParser.class.getClassLoader();
			InputStream is = classLoader.getResourceAsStream(filePath);
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			sp.parse(is, this);
		}
		catch (ParserConfigurationException e) {
			throw new HDIVException(e.getMessage(), e);
		}
		catch (SAXException e) {
			throw new HDIVException(e.getMessage(), e);
		}
		catch (IOException e) {
			throw new HDIVException(e.getMessage(), e);
		}
	}

	@Override
	public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) {

		if ("validation".equals(qName)) {
			this.validation = new HashMap<ValidationParam, String>();
			String id = attributes.getValue("id");
			this.validation.put(ValidationParam.ID, id);
			this.validations.add(this.validation);
		}
	}

	@Override
	public void characters(final char[] ch, final int start, final int length) throws SAXException {
		String val = new String(ch, start, length).trim();
		if (val.length() > 0) {
			this.validation.put(ValidationParam.REGEX, val);
		}
	}

	/**
	 * @return the validations
	 */
	public List<Map<ValidationParam, String>> getValidations() {
		return validations;
	}

	public enum ValidationParam {
		ID, REGEX
	}

}