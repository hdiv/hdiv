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
package org.hdiv.dataComposer;

import org.hdiv.session.ISession;

/**
 * <p>
 * It processes the data contributed by the HDIV custom tags. The aim of this class
 * is to create an object of type IState for each possible request (form or link) in
 * every page processed by the HDIV custom tags. The IState object is used to
 * validate client's later requests.
 * </p>
 * <p>
 * The process of creating an IState object is as follows: Each time a link or a form
 * processing begins, HDIV custom tags set the request beginning by calling
 * beginRequest method. Once the beginning is set, an IState object is created and it
 * is fill in with all the data of the request(parameter values, non editable
 * values, parameter types) using the compose method. After processing all the request
 * data of the link or form, custom tags set the end of the processing by calling
 * endRequest method.
 * </p>
 * <p>
 * Depending on the strategy defined in HDIV configuration (memory, encoded or hash),
 * the IState object is stored in the user session or is sent to the client in the
 * html code by adding an extra parameter called _HDIV_STATE_.
 * </p>
 * <p>
 * In the memory strategy IState objects are stored in the user session (HttpSession)
 * while in the encoded and hash strategies these objects are stored in the client.
 * </p>
 * 
 * @author Roberto Velasco
 */
public abstract class AbstractDataComposer implements IDataComposer {

	/**
	 * Action to which the user request is directed to
	 */
	private String action;

	/**
	 * Identifier of the page sent back by the server.
	 */
	private String pageId;

	/**
	 * Http session wrapper
	 */
	private ISession session;

	/**
	 * It is called by each request or form of the html page sent back by the server.
	 */
	public void beginRequest() {
		this.initPageId();
	}

	/**
	 * It is called by each request or form of the html page returned by the server,
	 * as long as the destiny of the request is an action.
	 * 
	 * @param action target name
	 */
	public void beginRequest(String action) {
		this.setAction(action);
		this.beginRequest();
	}	
	
	/**
	 * Obtains the page identifier that contains the request or form in process
	 * 
	 * @return Returns the pageId.
	 */
	public String getPageId() {

		if (this.pageId != null) {
			return this.pageId;
		} else {
			this.pageId = this.session.getPageId();
			return this.pageId;
		}
	}

	/**
	 * Obtains a new unique identifier for the page
	 */
	public void initPageId() {
		this.pageId = this.session.getPageId();
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public ISession getSession() {
		return session;
	}

	public void setSession(ISession session) {
		this.session = session;
	}

}
