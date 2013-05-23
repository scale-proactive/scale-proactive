package org.objectweb.proactive.multiactivity.priority;

import java.util.LinkedList;
import java.util.List;

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
			PriorityElement element = this.first;
			while (element.next != null) {
				element = element.next;
			}
			// Here element is the last
			while ((this.priorityStructure.canOvertake(group, element.belongingGroup) == PriorityOvertakeState.TRUE ||
					this.priorityStructure.canOvertake(group, element.belongingGroup) == PriorityOvertakeState.UNRELATED) &&
					element.previous != null) {
				element = element.previous;
			}
			toInsert.previous = element;
			toInsert.next = element.next;
			if (element.next != null) {
				element.next.previous = toInsert;
			}
			element.next = toInsert;

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
		int count = 0 ;
		StringBuilder sb = new StringBuilder();
		PriorityElement element = this.first;
		
		sb.append("\n");
		while (element != null) {
			for (int i = 0 ; i < count ; i++) {
				sb.append("\t");
			}
			sb.append(element.request.getRequest().getMethodName() + "\n");
			element = element.next;
			count++;
		}
		
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
