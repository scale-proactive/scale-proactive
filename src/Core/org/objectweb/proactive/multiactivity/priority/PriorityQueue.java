package org.objectweb.proactive.multiactivity.priority;

import java.util.LinkedList;
import java.util.List;

import org.objectweb.proactive.multiactivity.compatibility.CompatibilityManager;
import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;
import org.objectweb.proactive.multiactivity.execution.RunnableRequest;
import org.objectweb.proactive.multiactivity.priority.PriorityStructure.PriorityOvertakeState;

public class PriorityQueue {

	private PriorityElement first;	
	private PriorityStructure priorityStructure;

	public PriorityQueue(PriorityStructure priorityStructure) {
		this.priorityStructure = priorityStructure;
	}

	/**
	 * Pour le moment, ajoute en queue
	 * @param runnableRequest
	 * @param group
	 */
	public void insert(RunnableRequest request, MethodGroup group) {
		PriorityElement toInsert = new PriorityElement(request, group);

		if (this.first == null) {
			this.first = toInsert;
		}
		else {
			PriorityElement currentElement = this.first;
			boolean falseEncountered = false;
			while (currentElement.next != null) {
				currentElement = currentElement.next;
			}
			// Here element is the last
			PriorityElement lastViableElement = currentElement;

			// We continue to overtake if no unovertakable element has been encountered and if there still are previous element in the queue 
			while (!falseEncountered && currentElement != null) {
				PriorityOvertakeState overtakable = this.priorityStructure.canOvertake(group, currentElement.belongingGroup);
				if (overtakable.equals(PriorityOvertakeState.TRUE)) {
					// This position is viable for sure, save it.
					lastViableElement = currentElement.previous;
				}
				else {
					if (overtakable.equals(PriorityOvertakeState.FALSE)) {
						// An unovertakable element has been found, stop searching better position
						falseEncountered = true;
					}
				}
				currentElement = currentElement.previous;
			}

			// the loop stopped, insert to asved element
			if (lastViableElement != null) {
				toInsert.previous = lastViableElement;
				toInsert.next = lastViableElement.next;
				if (lastViableElement.next != null) {
					lastViableElement.next.previous = toInsert;
				}
				lastViableElement.next = toInsert;
			}
			// Mean that the element to insert must be the first
			else {
				toInsert.next = this.first;
				this.first.previous = toInsert;
				this.first = toInsert;
			}

			///////////////////////
			/*while ((this.priorityStructure.canOvertake(group, currentElement.belongingGroup) == PriorityOvertakeState.TRUE
					&& currentElement.previous != null)) {
				currentElement = currentElement.previous;
			}

			toInsert.previous = currentElement;
			toInsert.next = currentElement.next;
			if (currentElement.next != null) {
				currentElement.next.previous = toInsert;
			}
			currentElement.next = toInsert;*/
			//////////////

		}

		/*if (this.head == null) {
			this.head = toInsert;
		}
		else {
			PriorityElement previousElement = null;
			PriorityElement element = this.head;
			while (element != null) {
				previousElement = element;
				element = element.next;
			}	
			previousElement.next = toInsert;
			toInsert.previous = previousElement;
		}*/

	}

	/**
	 * Linear
	 */
	public int nbRequests() {
		int size = 0;
		PriorityElement element = this.first;
		while (element != null) {
			size++;
			element = element.next;
		}
		System.out.println("nbRequests registered: " + size);
		return size;
	}

	/**
	 * Constant
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
	 * Linear
	 * @param runnableRequest
	 */
	public void remove(RunnableRequest request) {
		System.out.println("Calling remove");
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
	 * @return 
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
	 * 
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

		/** The request to schedule */
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
