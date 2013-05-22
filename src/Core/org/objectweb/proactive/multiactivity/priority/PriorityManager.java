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

import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.multiactivity.compatibility.CompatibilityMap;
import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;
import org.objectweb.proactive.multiactivity.execution.RunnableRequest;

/**
 * Maintains the list {@link PriorityGroup} that have 
 * current requests. Contains the priority strategy to
 * schedule ready requests.
 * 
 * @author The ProActive Team
 */
public class PriorityManager {
	
	public static final PriorityManagement management = PriorityManagement.RANK_BASED; 

	// The group manager
	private final CompatibilityMap compatibility;
	
	private PriorityQueue priorityQueue;

	
	public PriorityManager(PriorityStructure priority, CompatibilityMap compatibility) {
		this.compatibility = compatibility;
		this.priorityQueue = new PriorityQueue(priority);
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
		this.priorityQueue.insert(request, group);
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
	 * Return the {@link PriorityGroup} that have the highest 
	 * priority and that have at least one registered request.
	 * @return The priority group with the highest priority
	 */
	public List<RunnableRequest> getHighestPriorityRequests() {
		return this.priorityQueue.getHighestPriorityRequests();
	}
	
	public void printRequests() {
		this.priorityQueue.printQueue();
	}
	
	public enum PriorityManagement {
		RANK_BASED, GRAPH_BASED
	}

}
