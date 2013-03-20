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

import java.util.Stack;

import org.hdiv.idGenerator.UidGenerator;
import org.hdiv.session.ISession;
import org.hdiv.state.IPage;
import org.hdiv.state.Page;

/**
 * <p>
 * It processes the data contributed by the HDIV custom tags. The aim of this class is to create an object of type
 * IState for each possible request (form or link) in every page processed by the HDIV custom tags. The IState object is
 * used to validate client's later requests.
 * </p>
 * <p>
 * The process of creating an IState object is as follows: Each time a link or a form processing begins, HDIV custom
 * tags set the request beginning by calling beginRequest method. Once the beginning is set, an IState object is created
 * and it is fill in with all the data of the request(parameter values, non editable values, parameter types) using the
 * compose method. After processing all the request data of the link or form, custom tags set the end of the processing
 * by calling endRequest method.
 * </p>
 * <p>
 * Depending on the strategy defined in HDIV configuration (memory, encoded or hash), the IState object is stored in the
 * user session or is sent to the client in the html code by adding an extra parameter called _HDIV_STATE_.
 * </p>
 * <p>
 * In the memory strategy IState objects are stored in the user session (HttpSession) while in the encoded and hash
 * strategies these objects are stored in the client.
 * </p>
 * 
 * @author Roberto Velasco
 */
public abstract class AbstractDataComposer implements IDataComposer {

	/**
	 * Dash character
	 */
	public static final String DASH = "-";

	/**
	 * Http session wrapper
	 */
	private ISession session;

	/**
	 * Unique id generator
	 */
	private UidGenerator uidGenerator;

	/**
	 * Page with the possible requests or states
	 */
	private IPage page;

	/**
	 * States stack to store all states of the page <code>page</code>
	 */
	private Stack statesStack;

	/**
	 * DataComposer initialization with new stack to store all states of the page <code>page</code>.
	 */
	public void init() {
		this.setPage(new Page());
		this.statesStack = new Stack();
	}

	/**
	 * Obtains a new unique identifier for the page.
	 */
	public void initPage() {
		this.page = new Page();
		String pageId = this.session.getPageId();
		this.page.setName(pageId);
		String tokenGenerated = this.uidGenerator.generateUid().toString();
		// TODO the in-memory strategy generates a hex token which is matched by StateUtil.isMemoryStragtegy
		// however this uidGenerator is pluggable, so the token should be set to hex here
		this.page.setRandomToken(tokenGenerated);
	}

	/**
	 * True if beginRequest has been executed and endRequest not.
	 * 
	 * @return boolean
	 */
	public boolean isRequestStarted() {
		return this.statesStack.size() > 0;
	}

	/**
	 * @return the session
	 */
	public ISession getSession() {
		return session;
	}

	/**
	 * @param session
	 *            the session to set
	 */
	public void setSession(ISession session) {
		this.session = session;
	}

	/**
	 * @return the page
	 */
	public IPage getPage() {
		return page;
	}

	/**
	 * @param page
	 *            the page to set
	 */
	public void setPage(IPage page) {
		this.page = page;
	}

	/**
	 * @param uidGenerator
	 *            the uidGenerator to set
	 */
	public void setUidGenerator(UidGenerator uidGenerator) {
		this.uidGenerator = uidGenerator;
	}

	/**
	 * @return the statesStack
	 */
	public Stack getStatesStack() {
		return statesStack;
	}

}
