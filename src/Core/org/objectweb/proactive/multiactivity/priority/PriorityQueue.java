package org.objectweb.proactive.multiactivity.priority;

import java.util.LinkedList;
import java.util.List;

import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;
import org.objectweb.proactive.multiactivity.execution.RunnableRequest;

public class PriorityQueue {

	private PriorityElement head;	
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
		if (this.head == null) {
			this.head = toInsert;
		}
		else {
			PriorityElement previousElement = null;
			PriorityElement element = this.head;
			while (element != null) {
				previousElement = element;
				element = element.next;
			}
			if (previousElement != null) {
				previousElement.next = toInsert;
			}
			toInsert.previous = previousElement;
		}
	}

	/**
	 * Linear
	 */
	public int nbRequests() {
		int size = 0;
		PriorityElement element = this.head;
		while (element != null) {
			size++;
			element = element.next;
		}
		return size;
	}

	/**
	 * Constant
	 */
	public boolean hasRequests() {
		if (this.head == null) {
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
		PriorityElement element = this.head;
		if (element.next == null) {
			this.head = null;
		}
		else {
			while (element != null) {
				System.out.println("searching element");
				if (element.request.equals(request)) {
					System.out.println("element found");
					PriorityElement previous = element.previous;
					PriorityElement next = element.next;
					if (previous != null) {
						previous.next = next;
					}
					next.previous = previous;
					break;
				}
				element = element.next;
			}
		}
	}

	public List<RunnableRequest> getHighestPriorityRequests() {	
		List<RunnableRequest> requests = new LinkedList<RunnableRequest>();
		PriorityElement element = this.head;
		while (element != null) {
			requests.add(element.request);
			element = element.next;
		}
		return requests;
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
