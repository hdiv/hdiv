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
package org.hdiv.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hdiv.validator.Validation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * Validations for urls defined by the user in the hdiv-validations.xml file of
 * Spring.
 * 
 * @author Gorka Vicente
 * @since HDIV 1.1
 */
public class HDIVValidations implements BeanFactoryAware {

	/**
	 * Map containing the urls to which the user wants to apply validation for
	 * the editable parameters.
	 */
	protected Map urls;
	
	protected Map xmlData;
	
	private BeanFactory beanFactory;

	public Map getXmlData() {
		return xmlData;
	}

	public void setXmlData(Map xmlData) {
		this.xmlData = xmlData;
	}

	/**
	 * @return Returns the urls.
	 */
	public Map getUrls() {
		return urls;
	}

	/**
	 * @param urls
	 *            The urls to set.
	 */
	public void setUrls(Map urls) {
		this.urls = urls;
	}

	/**
	 * Using data read from HDIV custom schema and stored within xmlData
	 * attribute, initiliaze urls attribute.
	 * 
	 */
	public void init() {

		this.urls = new Hashtable();

		Iterator iterator = this.xmlData.keySet().iterator();

		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			List ids = this.convertToList((String) xmlData.get(key));
			this.urls.put(key, this.createValidationList(ids));
		}

	}

	private List createValidationList(List ids) {
		List newList = new ArrayList();

		for (int i = 0; i < ids.size(); i++) {
			String key = (String) ids.get(i);
			newList.add((Validation) this.beanFactory.getBean(key));
		}
		return newList;
	}

	public String toString() {
		return urls.toString();
	}

	private List convertToList(String data) {
		String[] result = data.split(",");
		List list = Arrays.asList(result);
		return list;

	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

}
