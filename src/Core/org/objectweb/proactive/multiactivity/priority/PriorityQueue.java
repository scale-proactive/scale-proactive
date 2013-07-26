package org.objectweb.proactive.multiactivity.priority;

import java.util.LinkedList;
import java.util.List;

import org.objectweb.proactive.multiactivity.compatibility.CompatibilityManager;
import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;
import org.objectweb.proactive.multiactivity.execution.RunnableRequest;

/**
 * This class represents the structure that is used to reorder the requests 
 * among the ready to execute request (= compatible ones). It uses a 
 * PriorityStructure to know the relative priority of incoming requests.
 * 
 * @author jrochas
 */
public class PriorityQueue {

	/** Head of the priority queue (= oldest highest priority request) */
	private PriorityElement first;

	/** Priority representation used to decide where to insert a new request */
	private PriorityStructure priorityStructure;

	public PriorityQueue(PriorityStructure priorityStructure) {
		this.priorityStructure = priorityStructure;
	}

	/**
	 * Inserts a new request in the priority queue according to its priority 
	 * defined in the priority structure.
	 * @param runnableRequest
	 * @param group
	 */
	public void insert(RunnableRequest request, MethodGroup group) {
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
			while (!isOvertakable && currentElement != null) {
				isOvertakable = 
						this.priorityStructure.canOvertake(
								group, currentElement.belongingGroup);
				if (!isOvertakable) {
					previousElement = currentElement;
					currentElement = currentElement.next;
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
	 * @return The number of requests that are in the priority queue.
	 */
	public int nbRequests() {
		int size = 0;
		PriorityElement element = this.first;
		while (element != null) {
			size++;
			element = element.next;
		}
		return size;
	}

	/**
	 * @return true if there are some requests in the priority queue.
	 */
	public boolean hasRequests() {
		if (this.first == null) {
			return false;
		}
		else {
			return true;
		}
	}

	/**
	 * Removes a request in the queue (according to the equals method).
	 * @param runnableRequest The request to remove
	 */
	public void remove(RunnableRequest request) {
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
	 * Returns the list of the highest priority requests that are in the queue.
	 * @return High priority requests
	 */
	public List<RunnableRequest> getHighestPriorityRequests() {	
		List<RunnableRequest> requests = new LinkedList<RunnableRequest>();
		PriorityElement element = this.first;
		while (element != null) {
			// TODO Check here if there are enough thread to schedule the request
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
		StringBuilder sb = new StringBuilder();
		PriorityElement element = this.first;

		sb.append("\n\nPriority queue - from high priority...\n");
		while (element != null) {
			sb.append("\t" + element.request.getRequest().getMethodName() + "(");
			for (int i = 0 ; i < element.request.getRequest().getMethodCall().getNumberOfParameter() ; i++) {
				sb.append(element.request.getRequest().getParameter(i));
			}
			sb.append(")\n");
			element = element.next;
		}
		sb.append("Priority queue - to low priority\n");
		return sb.toString();
	}
	
	/**
	 * @param compatibility
	 * @param threadManager
	 * @return A string with queue content and thread utilization.
	 */
	public String toString(CompatibilityManager compatibility, ThreadManager threadManager) {
		StringBuilder sb = new StringBuilder();
		PriorityElement element = this.first;

		sb.append("\n\nPriority queue - from high priority...\n");
		while (element != null) {
			sb.append("\t" + element.request.getRequest().getMethodName() + "(");
			for (int i = 0 ; i < element.request.getRequest().getMethodCall().getNumberOfParameter() ; i++) {
				sb.append(element.request.getRequest().getParameter(i));
			}
			sb.append(")");
			MethodGroup group = compatibility.getGroupOf(element.request.getRequest());
			if (group != null) {
				sb.append((!threadManager.hasFreeThreads(group) ? " cannot be executed (thread limit: " + threadManager.printUsage(group) : "") + "\n");
			}
			element = element.next;
		}
		sb.append("Priority queue - to low priority\n");
		return sb.toString();
	}

	/**
	 * Represents an element in the PriorityQueue. Since it is a private class,
	 * the fields have been declared public for convenience.
	 * 
	 * @author jrochas
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
