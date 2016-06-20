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
package org.hdiv.dataComposer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayDeque;
import java.util.Deque;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.context.RequestContext;
import org.hdiv.exception.HDIVException;
import org.hdiv.state.IPage;
import org.hdiv.state.IState;
import org.hdiv.state.State;
import org.hdiv.state.scope.StateScope;
import org.hdiv.state.scope.StateScopeManager;
import org.hdiv.state.scope.StateScopeType;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVStateUtils;
import org.hdiv.util.HDIVUtil;
import org.hdiv.util.Method;

/**
 * <p>
 * It generates the states of each page by storing them in the user session. To be able to associate the request state with the state stored
 * in session, an extra parameter is added to each request, containing the state identifier which makes possible to get the state of the
 * user session.
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
	private static final Log log = LogFactory.getLog(DataComposerMemory.class);

	/**
	 * State scope manager.
	 */
	protected StateScopeManager stateScopeManager;

	/**
	 * Stack to store existing scopes, active and inactive
	 */
	protected Deque<StateScopeType> scopeStack;

	protected StateScope stateScope;

	public DataComposerMemory(final RequestContext requestContext) {
		super(requestContext);
	}

	/**
	 * DataComposer initialization with new stack to store all states of the page <code>page</code>.
	 */
	@Override
	public void init() {
		super.init();
		scopeStack = new ArrayDeque<StateScopeType>();
		// Add default scope
		startScope(StateScopeType.PAGE);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.hdiv.dataComposer.IDataComposer#startScope(java.lang.String)
	 */
	public void startScope(final StateScopeType scope) {
		scopeStack.push(scope);
		stateScope = getStateScope();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.hdiv.dataComposer.IDataComposer#endScope()
	 */
	public void endScope() {
		scopeStack.pop();
		stateScope = getStateScope();
	}

	/**
	 * It is called by each request or form existing in the page returned by the server. It creates a new state to store all the parameters
	 * and values of the request or form.
	 *
	 * @return state id for this request
	 */
	public final String beginRequest() {
		return beginRequest(null, "");
	}

	/**
	 * It is called in the pre-processing stage of each request or form existing in the page returned by the server, as long as the destiny
	 * of the request is an action. It creates a new state to store all the parameters and values of the request or form.
	 *
	 * @param method HTTP method of the request.
	 * @param action action name
	 * @return state id for this request
	 *
	 * @see org.hdiv.dataComposer.DataComposerMemory#beginRequest()
	 */
	public String beginRequest(final Method method, String action) {
		try {
			action = HDIVUtil.decodeValue(sb, action, Constants.ENCODING_UTF_8);
		}
		catch (UnsupportedEncodingException e) {
			throw new HDIVException(Constants.ENCODING_UTF_8 + " enconding not supported.", e);
		}
		catch (IllegalArgumentException e) {
			// Some decoding errors throw IllegalArgumentException
		}
		return beginRequest(createNewState(page.getNextStateId(), method, action));
	}

	/**
	 * Create new {@link IState} instance.
	 *
	 * @param stateId Identifier for the new {@link IState}
	 * @param method HTTP method of the request.
	 * @param action action name
	 * @return new {@link IState} instance.
	 */
	protected IState createNewState(final int stateId, final Method method, final String action) {
		IState state = new State(stateId);
		state.setAction(action);
		state.setMethod(method);
		return state;
	}

	private final StateScope getStateScope() {
		StateScopeType type = scopeStack.peek();
		if (type != StateScopeType.PAGE) {
			return stateScopeManager.getStateScope(type);
		}
		return null;
	}

	public String beginRequest(final IState state) {
		states.push(state);

		// Add to scope
		if (stateScope != null) {
			// Its custom scope Scope
			// TODO can't return the state id
			// We can't know the id before compose all parameters
			return null;
		}
		return toId(state);
	}

	protected String toId(final IState state) {
		return HDIVStateUtils.encode(page.getId(), state.getId(), getStateSuffix(state.getTokenType()));
	}

	/**
	 * It is called in the pre-processing stage of each request or form existing in the page returned by the server. It adds the state of
	 * the treated request or form to the page <code>page</code> and returns and identifier composed by the page identifier and the state
	 * identifier.
	 *
	 * @return Identifier composed by the page identifier and the state identifier.
	 */
	public String endRequest() {

		IState state = states.pop();

		// Add to scope
		if (stateScope != null) {
			// Its custom Scope
			return stateScope.addState(context, state, getStateSuffix(state.getTokenType()));
		}

		// Add to page scope
		page.addState(state);

		// Save Page in session if this is the first state to add
		if (page.getStatesCount() == 1) {
			session.addPartialPage(context, page);
		}

		return toId(state);
	}

	/**
	 * It is called in the pre-processing stage of each user request assigning a new page identifier to the page.
	 */
	public void startPage() {
		initPage();
	}

	/**
	 * It is called in the pre-processing stage of each user request assigning a new page identifier to the page with its parent state id.
	 */
	public void startPage(final String parentStateId) {
		initPage(parentStateId);
	}

	/**
	 * It is called in the pre-processing stage of each user request. Create a new {@link IPage} based on an existing page.
	 *
	 * @param existingPage other IPage
	 */
	public void startPage(final IPage existingPage) {
		existingPage.markAsReused();
		setPage(existingPage);
	}

	/**
	 * This method is called in the pre-processing stage of each user request to add an IPage object, which represents the page to show by
	 * the server, with all its states to the user session.
	 */
	public void endPage() {

		if (isRequestStarted()) {
			// A request is started but not ended
			endRequest();
		}

		if (page.getStatesCount() > 0) {
			// The page has states, update them in session
			session.addPage(context, page);
		}
		else {
			if (log.isDebugEnabled()) {
				log.debug("The page [" + page.getId() + "] has no states, is not stored in session");
			}
		}

	}

	/**
	 * @param stateScopeManager the stateScopeManager to set
	 */
	public void setStateScopeManager(final StateScopeManager stateScopeManager) {
		this.stateScopeManager = stateScopeManager;
	}

}