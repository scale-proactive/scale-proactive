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

import java.util.Iterator;
import java.util.List;

import org.objectweb.proactive.multiactivity.compatibility.CompatibilityManager;
import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;
import org.objectweb.proactive.multiactivity.execution.RunnableRequest;


/**
 * This class aims at reordering the requests in the queue according to the 
 * priorities that were defined with the priority annotations. For that it 
 * uses an internal queue from where we can insert new requests and poll 
 * highest priority requests.
 * 
 * @author The ProActive Team
 */
public class PriorityManager {

	/** Group manager (needed to guess the group of a request) */
	private final CompatibilityManager compatibility;
	
	/** Reordered queue of ready requests */
	private PriorityQueue priorityQueue;
	
	/** Manager of the thread limits per group */
	private ThreadManager threadManager;
	
	public PriorityManager(CompatibilityManager compatibility, PriorityStructure priority, ThreadManager threadManager) {
		this.compatibility = compatibility;
		this.priorityQueue = new PriorityQueue(priority);
		this.threadManager = threadManager;
	}

	/**
	 * @return true if there is at least one registered request,
	 * all priority groups combined.
	 */
	public boolean hasSomeRequestsRegistered() {
		return this.priorityQueue.hasRequests();
	}

	/**
	 * Returns the current total number of registered 
	 * request, all priority groups combined.
	 * @return The number of registered requests
	 */
	public int getNbRequestsRegistered() {
		return this.priorityQueue.nbRequests();
	}

	/**
	 * Adds a {@link RunnableRequest} to the priority group it belongs to.
	 * The priority group of the request is determined from the group the 
	 * request belongs to. See {@link MethodGroup}.
	 * @param request
	 */
	public void register(RunnableRequest request) {
		MethodGroup group = this.compatibility.getGroupOf(request.getRequest());
		long before = System.nanoTime();
		this.priorityQueue.insert(request, group);
		long after = System.nanoTime();
		PriorityUtils.logMessage(request.getRequest().
				getMethodName() + " insertTime: " 
				+ (after - before));
	}

	/**
	 * Removes a registered request from the priority 
	 * group it had been assigned.
	 * @param runnableRequest
	 * @param priorityLevel
	 */
	public void unregister(
			RunnableRequest request) {
		this.priorityQueue.remove(request);
	}

	/**
	 * Returns the {@link PriorityGroup} that have the highest 
	 * priority and that have at least one registered request.
	 * @return The priority group with the highest priority
	 */
	public List<RunnableRequest> getHighestPriorityRequests() {
		List<RunnableRequest> requests = this.priorityQueue.getHighestPriorityRequests();
		Iterator<RunnableRequest> it = requests.iterator();
		while (it.hasNext()) {
			MethodGroup group = this.compatibility.getGroupOf(it.next().getRequest());
			if (group != null) {
				if (!this.threadManager.hasFreeThreads(group)) {
					it.remove();
				}
			}
		}
		return requests;
	}
	
	/**
	 * Notify the thread manager that a request is executed, to update the 
	 * thread usage of the group of this request.
	 * @param request The executed request
	 */
	public void notifyRunning(RunnableRequest request) {
		MethodGroup group = this.compatibility.getGroupOf(request.getRequest());
		if (group != null) {
			this.threadManager.increaseUsage(group);
		}
	}
	
	/**
	 * Notify the thread manager that a request is finished, so that it can 
	 * update the thread usage for the corresponding group.
	 * @param request The finished request
	 */
	public void notifyFinished(RunnableRequest request) {
		MethodGroup group = this.compatibility.getGroupOf(request.getRequest());
		if (group != null) {
			this.threadManager.decreaseUsage(group);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString(int globalUsage, int globalLimit) {
		String global = "\n\nGlobal usage: " + globalUsage + "/" + globalLimit;
		return global + this.priorityQueue.toString(this.compatibility, this.threadManager);
	}

}
