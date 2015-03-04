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
package org.hdiv.dataComposer;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.exception.HDIVException;
import org.hdiv.state.IPage;
import org.hdiv.state.IState;
import org.hdiv.state.State;
import org.hdiv.state.scope.StateScope;
import org.hdiv.state.scope.StateScopeManager;
import org.hdiv.util.Constants;

/**
 * <p>
 * It generates the states of each page by storing them in the user session. To be able to associate the request state
 * with the state stored in session, an extra parameter is added to each request, containing the state identifier which
 * makes possible to get the state of the user session.
 * </p>
 * <p>
 * Non editable values are hidden to the client, guaranteeing <b>confidentiality</b>
 * </p>
 * 
 * @see org.hdiv.dataComposer.AbstractDataComposer
 * @see org.hdiv.dataComposer.IDataComposer
 * @author Roberto Velasco
 */
public class DataComposerMemory extends AbstractDataComposer {

	/**
	 * Commons Logging instance.
	 */
	private static Log log = LogFactory.getLog(DataComposerMemory.class);

	/**
	 * Represents the identifier of each possible state stored in the page <code>page</code>.
	 */
	protected int requestCounter = 0;

	/**
	 * State scope manager.
	 */
	protected StateScopeManager stateScopeManager;

	/**
	 * Stack to store existing scopes, active and inactive
	 */
	protected Stack<String> scopeStack;

	/**
	 * DataComposer initialization with new stack to store all states of the page <code>page</code>.
	 */
	public void init() {
		super.init();
		this.scopeStack = new Stack<String>();
		// Add default scope
		this.scopeStack.push("page");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.dataComposer.IDataComposer#startScope(java.lang.String)
	 */
	public void startScope(String scope) {

		this.scopeStack.push(scope);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hdiv.dataComposer.IDataComposer#endScope()
	 */
	public void endScope() {

		this.scopeStack.pop();
	}

	/**
	 * Return current active Scope
	 * 
	 * @return Scope name
	 */
	protected String getCurrentScope() {

		return this.scopeStack.peek();
	}

	/**
	 * It is called by each request or form existing in the page returned by the server. It creates a new state to store
	 * all the parameters and values of the request or form.
	 * 
	 * @return state id for this request
	 */
	public String beginRequest() {

		return this.beginRequest(null, "");
	}

	/**
	 * It is called in the pre-processing stage of each request or form existing in the page returned by the server, as
	 * long as the destiny of the request is an action. It creates a new state to store all the parameters and values of
	 * the request or form.
	 * 
	 * @param method
	 *            HTTP method of the request.
	 * @param action
	 *            action name
	 * @return state id for this request
	 * 
	 * @see org.hdiv.dataComposer.DataComposerMemory#beginRequest()
	 */
	public String beginRequest(String method, String action) {

		try {
			action = URLDecoder.decode(action, Constants.ENCODING_UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new HDIVException(Constants.ENCODING_UTF_8 + " enconding not supported.", e);
		} catch (IllegalArgumentException e) {
		}

		// Create new IState
		IState state = new State(this.requestCounter);
		state.setAction(action);
		state.setMethod(method);

		return this.beginRequest(state);
	}

	public String beginRequest(IState state) {

		this.getStatesStack().push(state);

		// Add to scope
		String currentScope = this.getCurrentScope();
		StateScope stateScope = this.stateScopeManager.getStateScopeByName(currentScope);
		if (stateScope != null) {
			// Its custom scope Scope
			// TODO can't return the state id
			// We can't know the id before compose all parameters
			return null;
		}

		// It is Page scope or none
		this.requestCounter = state.getId() + 1;

		String id = this.getPage().getName() + DASH + state.getId() + DASH + this.getStateSuffix(state.getMethod());
		return id;
	}

	/**
	 * It is called in the pre-processing stage of each request or form existing in the page returned by the server. It
	 * adds the state of the treated request or form to the page <code>page</code> and returns and identifier composed
	 * by the page identifier and the state identifier.
	 * 
	 * @return Identifier composed by the page identifier and the state identifier.
	 */
	public String endRequest() {

		IState state = this.getStatesStack().pop();

		// Add to scope
		String currentScope = this.getCurrentScope();
		StateScope stateScope = this.stateScopeManager.getStateScopeByName(currentScope);
		if (stateScope != null) {
			// Its custom Scope
			String stateId = stateScope.addState(state, this.getStateSuffix(state.getMethod()));
			return stateId;
		}

		// Add to page scope
		IPage page = this.getPage();
		state.setPageId(page.getId());
		page.addState(state);

		// Save Page in session if this is the first state to add
		boolean firstState = page.getStatesCount() == 1;
		if (firstState) {

			super.session.addPage(page.getId(), page);
		}

		String id = this.getPage().getId() + DASH + state.getId() + DASH + this.getStateSuffix(state.getMethod());
		return id;
	}

	/**
	 * It is called in the pre-processing stage of each user request assigning a new page identifier to the page.
	 */
	public void startPage() {

		this.initPage();
	}

	/**
	 * It is called in the pre-processing stage of each user request. Create a new {@link IPage} based on an existing
	 * page.
	 * 
	 * @param existingPage
	 *            other IPage
	 */
	public void startPage(IPage existingPage) {
		this.requestCounter = existingPage.getStatesCount();
		this.setPage(existingPage);
	}

	/**
	 * This method is called in the pre-processing stage of each user request to add an IPage object, which represents
	 * the page to show by the server, with all its states to the user session.
	 */
	public void endPage() {

		if (this.isRequestStarted()) {
			// A request is started but not ended
			this.endRequest();
		}

		IPage page = this.getPage();
		if (page.getStatesCount() > 0) {
			// The page has states, update them in session
			super.session.addPage(page.getId(), page);
		} else {
			if (log.isDebugEnabled()) {
				log.debug("The page [" + page.getId() + "] has no states, is not stored in session");
			}
		}

	}

	/**
	 * @param stateScopeManager
	 *            the stateScopeManager to set
	 */
	public void setStateScopeManager(StateScopeManager stateScopeManager) {
		this.stateScopeManager = stateScopeManager;
	}

}