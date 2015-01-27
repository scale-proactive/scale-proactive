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

import java.util.List;

import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;
import org.objectweb.proactive.multiactivity.execution.RunnableRequest;

/**
 * This class is meant to manage the requests according their priority. It 
 * provides an interface to store requests and to retrieve the highest 
 * priority requests. To order requests according to their priority, it uses 
 * the PriorityMap built when annotations are processed.
 * 
 * @author The ProActive Team
 *
 */

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
