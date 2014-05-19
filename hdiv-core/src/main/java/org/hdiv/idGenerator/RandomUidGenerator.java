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
package org.hdiv.idGenerator;

import java.io.Serializable;
import java.util.Random;

import javax.servlet.ServletContext;

import org.springframework.web.context.ServletContextAware;

/**
 * Unique id generator based on the java.util.Random class.
 * 
 */
public class RandomUidGenerator implements UidGenerator, ServletContextAware{

	/**
     * Used to generate unique server state IDs.
     */
    protected Random random;
    
    /**
     * ServletContext of the application
     */
    private ServletContext servletContext;
	
    /**
     * Initializaiton method
     */
    public void init() {
    	this.random = new Random(System.currentTimeMillis() + this.servletContext.hashCode());
	}
    
    /*
     * (non-Javadoc)
     * @see org.hdiv.util.UidGenerator#generateUid()
     */
	public Serializable generateUid() {
		
		long uid = this.random.nextLong();
		if(uid < 0){
			uid = uid * -1;//Hacerlo positivo
		}
		return new Long(uid).toString();
	}

	/*
	 * (non-Javadoc)
	 * @see org.hdiv.util.UidGenerator#parseUid(java.lang.String)
	 */
	public Serializable parseUid(String encodedUid) {
		
		return encodedUid;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.web.context.ServletContextAware#setServletContext(javax.servlet.ServletContext)
	 */
	public void setServletContext(ServletContext servletContext) {
		
		this.servletContext = servletContext;
	}

}
