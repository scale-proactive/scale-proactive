package org.objectweb.proactive.multiactivity;

import java.util.List;

import org.objectweb.proactive.core.body.request.Request;

public interface ServingPolicy {	
	
	/**
	 * This method will decide which methods get to run given the current state of the scheduler
	 * and the relation between methods.
	 * @param state
	 * @param graph
	 * @return a sublist of the methods returned by the scheduler state as being queued
	 */
	public List<Request> runPolicy(SchedulerState state);
	
}
