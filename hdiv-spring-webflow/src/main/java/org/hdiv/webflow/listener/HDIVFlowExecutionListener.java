/**
 * Copyright 2005-2010 hdiv.org
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

package org.hdiv.webflow.listener;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.util.HDIVUtil;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.FlowExecutionListenerAdapter;
import org.springframework.webflow.execution.FlowSession;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.repository.support.CompositeFlowExecutionKey;

/**
 * HDIV listener for webflow to delete HDIV pages from session in order to
 * optimize memory consumption.
 * 
 * @author Gorka Vicente
 * @author Gotzon Illarramendi
 * @since 2.0.3
 */
public class HDIVFlowExecutionListener extends FlowExecutionListenerAdapter {

	private static Log log = LogFactory.getLog(HDIVFlowExecutionListener.class);

	/**
	 * Called when a client request has completed processing. Adds the
	 * conversation id to the current IDataComposer.
	 * 
	 * @param context
	 *            the source of the event
	 */
	public void requestProcessed(RequestContext context) {

		FlowExecutionKey flowExecutionKey = context.getFlowExecutionContext().getKey();
		if (flowExecutionKey != null) {

			if (flowExecutionKey instanceof CompositeFlowExecutionKey) {

				CompositeFlowExecutionKey compositeFlowExecutionKey = (CompositeFlowExecutionKey) flowExecutionKey;
				// Get current conversation/execution id
				String executionKey = compositeFlowExecutionKey.getExecutionId().toString();

				HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getNativeRequest();
				IDataComposer dataComposer = HDIVUtil.getDataComposer(request);

				// Add the id to the DataComposer
				dataComposer.addFlowId(executionKey);
			}
		}

	}

	/**
	 * Called when a flow execution session ends. If the ended session was the
	 * root session of the flow execution, the entire flow execution also ends.
	 * 
	 * @param context
	 *            the source of the event
	 * @param session
	 *            ending flow session
	 * @param outcome
	 *            the outcome reached by the ended session, generally the id of
	 *            the terminating end-state
	 * @param output
	 *            the flow output returned by the ending session
	 */
	public void sessionEnded(RequestContext context, FlowSession session, String outcome, AttributeMap output) {

		if (!context.getFlowExecutionContext().isActive()) {
			FlowExecutionKey flowExecutionKey = context.getFlowExecutionContext().getKey();

			if (flowExecutionKey != null) {

				if (flowExecutionKey instanceof CompositeFlowExecutionKey) {

					CompositeFlowExecutionKey compositeFlowExecutionKey = (CompositeFlowExecutionKey) flowExecutionKey;
					String executionKey = compositeFlowExecutionKey.getExecutionId().toString();

					// Remove the pages of this conversation from session
					HDIVUtil.getISession().removeEndedPages(executionKey);

					if (log.isDebugEnabled()) {
						log.debug("Flow ended:" + executionKey);
					}
				}
			}
		}
	}

}
