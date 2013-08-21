package org.objectweb.proactive.multiactivity.priority;

import java.util.List;

import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;
import org.objectweb.proactive.multiactivity.execution.RunnableRequest;

public abstract class PriorityManager {
	
	/** Priority representation used to decide where to insert a new request */
	protected PriorityMap priorityMap;
	
	protected PriorityManager(PriorityMap priorityMap) {
		this.priorityMap = priorityMap;
	}

	/**
	 * Adds a {@link RunnableRequest} to the priority group it belongs to.
	 * The priority group of the request is determined from the group the 
	 * request belongs to. See {@link MethodGroup}.
	 * @param request
	 */
	public abstract void register(RunnableRequest request);
	
	/**
	 * Removes a registered request from the priority 
	 * group it had been assigned.
	 * @param runnableRequest
	 * @param priorityLevel
	 */
	public abstract void unregister(RunnableRequest request);
	
	/**
	 * Returns the list of the highest priority requests that are in the queue.
	 * @return High priority requests
	 */
	public abstract List<RunnableRequest> getHighestPriorityRequests();
	
	/**
	 * @return The number of requests that are in the priority queue.
	 */
	public abstract int getNbRequestsRegistered();
}
