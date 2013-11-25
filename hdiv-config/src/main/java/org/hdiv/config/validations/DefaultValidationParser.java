/**
 * Copyright 2005-2013 hdiv.org
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
	 * List with processed validations.
	 */
	private List<Map<String, String>> validations = new ArrayList<Map<String, String>>();

	/**
	 * Current Validation data.
	 */
	private Map<String, String> validation = null;

	/**
	 * Read xml file from the given path.
	 * @param filePath xml file path
	 */
	public void readDefaultValidations(String filePath) {

		try {
			ClassLoader classLoader = DefaultValidationParser.class.getClassLoader();
			InputStream is = classLoader.getResourceAsStream(filePath);
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			sp.parse(is, this);
		} catch (ParserConfigurationException e) {
			throw new HDIVException(e.getMessage());
		} catch (SAXException e) {
			throw new HDIVException(e.getMessage());
		} catch (IOException e) {
			throw new HDIVException(e.getMessage());
		}
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) {

		if (qName != null && qName.equals("validation")) {
			this.validation = new HashMap<String, String>();
			String id = attributes.getValue("id");
			this.validation.put("id", id);
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (qName != null && qName.equals("validation")) {
			if (this.validation != null) {
				this.validations.add(this.validation);
			}
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		String val = new String(ch, start, length).trim();
		if (val.length() > 0) {
			this.validation.put("regex", val);
		}
	}

	/**
	 * @return the validations
	 */
	public List<Map<String, String>> getValidations() {
		return validations;
	}

}