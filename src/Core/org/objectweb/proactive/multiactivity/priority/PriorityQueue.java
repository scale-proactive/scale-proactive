package org.objectweb.proactive.multiactivity.priority;

import java.util.LinkedList;
import java.util.List;

import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;
import org.objectweb.proactive.multiactivity.execution.RunnableRequest;

public class PriorityQueue {

	private PriorityElement first;	
	private PriorityElement last;
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
			while (this.priorityStructure.canOvertake(group, element.belongingGroup) && element.previous != null) {
				element = element.previous;
			}

			if (element.previous == null) {
				PriorityElement secondElement = this.first;
				this.first = toInsert;
				toInsert.next = secondElement;
			}

			else {
				toInsert.next = element.next;
				element.next = toInsert;
			}
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
					System.out.println("element found");
					PriorityElement previous = element.previous;
					PriorityElement next = element.next;
					// The element to remove can be the first
					if (previous != null) {
						System.out.println("The request was not the first");
						previous.next = next;
					}
					else {
						this.first = next;
					}
					// The element to remove can be the last
					if (next != null) {
						System.out.println("The request was not the last");
						next.previous = previous;
					}
					break;
				}
				element = element.next;
			}
		}
		System.out.println("remove end");
	}

	public List<RunnableRequest> getHighestPriorityRequests() {	
		List<RunnableRequest> requests = new LinkedList<RunnableRequest>();
		PriorityElement element = this.first;
		while (element != null) {
			requests.add(element.request);
			element = element.next;
		}
		return requests;
	}

	public void printQueue() {
		PriorityElement element = this.first;
		while (element != null) {
			System.out.println(element.request.getRequest().getMethodName() + " ");
			element = element.next;
		}
	}

	private class PriorityElement {

		// We can afford public fields since it is a private class
		public final RunnableRequest request;
		public final MethodGroup belongingGroup;
		public PriorityElement previous;
		public PriorityElement next;

		public PriorityElement(RunnableRequest request, MethodGroup belongingGroup) {
			this.request = request;
			this.belongingGroup = belongingGroup;
		}	
	}

}
