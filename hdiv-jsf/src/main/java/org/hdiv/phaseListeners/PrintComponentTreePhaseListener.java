/**
 * Copyright 2005-2012 hdiv.org
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
package org.hdiv.phaseListeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

/**
 * <p>
 * Prints to the console the component tree for every phase of the life cycle.
 * </p>
 * <p>
 * CAUTION! Only for development.
 * </p>
 * 
 * @author Gotzon Illarramendi
 */
public class PrintComponentTreePhaseListener implements PhaseListener {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 */
	public PrintComponentTreePhaseListener() {
	}

	/**
	 * Indent value
	 */
	public int indent = 0;

	/**
	 * Indent size
	 */
	public static final int INDENTSIZE = 2;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.event.PhaseListener#beforePhase(javax.faces.event.PhaseEvent)
	 */
	public void beforePhase(PhaseEvent PhaseEvent) {
		System.out.println("");
		System.out.println("PHASE: " + PhaseEvent.getPhaseId());
		System.out.println("(Rendering Component Tree)");
		printComponentTree(FacesContext.getCurrentInstance().getViewRoot());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.event.PhaseListener#afterPhase(javax.faces.event.PhaseEvent)
	 */
	public void afterPhase(PhaseEvent PhaseEvent) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.event.PhaseListener#getPhaseId()
	 */
	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

	/**
	 * Print to console the component tree
	 * 
	 * @param comp
	 *            component tree root node
	 */
	private void printComponentTree(UIComponent comp) {
		if (comp == null) {
			return;
		}

		printComponentInfo(comp);

		Map<String, UIComponent> facets = comp.getFacets();
		List<UIComponent> facetList = new ArrayList<UIComponent>(facets.values());
		if (facetList.size() > 0)
			indent++;
		for (int i = 0; i < facetList.size(); i++) {
			Object obj = facetList.get(i);
			if (obj instanceof UIComponent) {
				UIComponent c = (UIComponent) obj;
				this.printComponentTree(c);
			}
			if (i + 1 == facetList.size())
				indent--;
		}

		List<UIComponent> complist = comp.getChildren();
		if (complist.size() > 0)
			indent++;
		for (int i = 0; i < complist.size(); i++) {
			UIComponent uicom = complist.get(i);
			printComponentTree(uicom);
			if (i + 1 == complist.size())
				indent--;
		}

	}

	/**
	 * Print to console component info
	 * 
	 * @param comp
	 *            component to print
	 */
	private void printComponentInfo(UIComponent comp) {

		if (comp.getId() == null) {
			System.out.println("UIViewRoot" + " (" + comp.getClass().getName() + ")");
		} else {
			printIndent();
			System.out.println("|");
			printIndent();
			System.out.println(comp.getId() + " (" + comp.getClass().getName() + ")");
		}
	}

	/**
	 * Print indent to console
	 */
	private void printIndent() {
		for (int i = 0; i < indent; i++)
			for (int j = 0; j < INDENTSIZE; j++)
				System.out.print(" ");
	}
}
