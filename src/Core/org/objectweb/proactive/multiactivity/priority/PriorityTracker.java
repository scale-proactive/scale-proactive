/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2012 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.multiactivity.priority;

import java.util.LinkedList;
import java.util.List;

import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.multiactivity.compatibility.CompatibilityManager;
import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;
import org.objectweb.proactive.multiactivity.execution.RunnableRequest;


/**
 * This class aims at reordering the requests in the queue according to the 
 * priorities that were defined with the priority annotations. For that it 
 * uses an internal queue from where we can insert new requests and poll 
 * highest priority requests, as defined in the {@link PriorityManager} super 
 * class.
 * 
 * @author The ProActive Team
 */
public class PriorityTracker extends PriorityManager {

	/** Head of the priority queue (= oldest highest priority request) */
	private PriorityElement first;

	/** Group manager (needed to find the group of a request) */
	private final CompatibilityManager compatibility;


	public PriorityTracker(CompatibilityManager compatibility, 
			PriorityMap priorityMap) {
		super(priorityMap);
		this.compatibility = compatibility;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void register(RunnableRequest request) {

		MethodGroup group = this.compatibility.getGroupOf(request.getRequest());
		PriorityElement toInsert = new PriorityElement(request, group);

		// The request to insert is the only one in the priority queue
		if (this.first == null) {
			this.first = toInsert;
		}

		else {

			PriorityElement currentElement = this.first;
			PriorityElement previousElement = null;
			boolean isOvertakable = false;

			// Search for the first request that has a lower priority
			if (group != null) {
				while (!isOvertakable && currentElement != null) {
					isOvertakable = 
							this.priorityMap.canOvertake(
									group, currentElement.belongingGroup);
					if (!isOvertakable) {
						previousElement = currentElement;
						currentElement = currentElement.next;
					}
				}
			}

			// The request must be placed just before the currentElement
			if (currentElement != null) {
				toInsert.next = currentElement;
				toInsert.previous = currentElement.previous;

				if (currentElement.previous != null) {
					currentElement.previous.next = toInsert;
					currentElement.previous = toInsert;
				}

				// The element to insert must be the first in the queue
				else {
					toInsert.next = this.first;
					this.first.previous = toInsert;
					this.first = toInsert;
				}
			}

			// Means that the element must be inserted at the end
			else {
				toInsert.previous = previousElement;
				previousElement.next = toInsert;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unregister(RunnableRequest request) {

		PriorityElement element = this.first;

		// There is only the element to remove in the PriorityQueue
		if (this.first.request.equals(request) && this.first.next == null) {
			this.first = null;
		}

		else {

			while (element != null) {

				if (element.request.equals(request)) {

					PriorityElement previous = element.previous;
					PriorityElement next = element.next; 

					// The element to remove can be the first
					if (previous != null) {
						previous.next = next;
					}
					else {
						this.first = next;
					}

					// The element to remove can be the last
					if (next != null) {
						next.previous = previous;
					}
					break;
				}

				element = element.next;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNbRequestsRegistered() {
		int size = 0;
		PriorityElement element = this.first;
		while (element != null) {
			size++;
			element = element.next;
		}
		return size;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<RunnableRequest> getHighestPriorityRequests() {	
		List<RunnableRequest> requests = new LinkedList<RunnableRequest>();
		PriorityElement element = this.first;
		while (element != null) {
			requests.add(element.request);
			element = element.next;
		}
		return requests;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {

		Request request;
		int nbParameters;
		StringBuilder sb = new StringBuilder();
		PriorityElement element = this.first;

		sb.append("\n\nPriority queue - from high priority...\n");
		while (element != null) {
			request = element.request.getRequest();
			sb.append("\t" + request.getMethodName() + "(");
			nbParameters = request.getMethodCall().getNumberOfParameter();
			for (int i = 0 ; i < nbParameters ; i++) {
				sb.append(request.getParameter(i));
				if (i != nbParameters) {
					sb.append(", ");
				}
			}
			sb.append(")\n");
			element = element.next;
		}

		sb.append("Priority queue - to low priority\n");
		return sb.toString();
	}

	/**
	 * Represents an element in the PriorityQueue. Since it is a private class,
	 * the fields have been declared public for convenience.
	 * 
	 * @author The ProActive Team
	 */
	private class PriorityElement {

		/** The request to execute */
		public final RunnableRequest request;

		/** The group where the request belongs. Warning: it is up to the 
		 * programmer to keep this variable consistent with the request. No 
		 * further verification are done to ensure that it really is the group 
		 * of the request. */
		public final MethodGroup belongingGroup;

		/** The previous element in the PriorityQueue (with higher priority)*/
		public PriorityElement previous;

		/** The next element in the PriorityQueue (with lower priority)*/
		public PriorityElement next;

		public PriorityElement(RunnableRequest request, 
				MethodGroup belongingGroup) {
			this.request = request;
			this.belongingGroup = belongingGroup;
		}

	}

}
