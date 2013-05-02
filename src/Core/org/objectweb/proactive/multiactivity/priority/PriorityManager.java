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

import java.util.TreeMap;

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

	public static final byte defaultPriorityLevel = 0;
	
	public static final byte maxPriorityLevel = 100;
	
	public static final byte minPriorityLevel = -100;

	// Priority groups that contain registered requests classified by priority
	// Searching is accelerated by the use of TreeMap
	private final TreeMap<Byte, PriorityGroup> priorityGroups;

	// The group manager
	private final CompatibilityMap compatibility;

	
	public PriorityManager(CompatibilityMap compatibility) {
		this.priorityGroups = new TreeMap<Byte, PriorityGroup>();
		this.compatibility = compatibility;

		// Indicates whether there is an annotation 
		// defined for the default priority group
		boolean defaultPriorityGroupOverriden = false;

		// Build one priority group per group. Note:
		// groups are statically registered in compatibility
		for (MethodGroup group : compatibility.getGroups()) {
			if (group.getPriorityLevel() == defaultPriorityLevel) {
				defaultPriorityGroupOverriden = true;
			}
			if (!this.priorityGroups.containsKey(group.getPriorityLevel())) {
				this.priorityGroups.put(
						group.getPriorityLevel(), 
						new PriorityGroup(group.getPriorityLevel()));
			}
		}

		// Add a priority group for methods without priority
		if (!defaultPriorityGroupOverriden) {
			this.priorityGroups.put(
					defaultPriorityLevel, 
					new PriorityGroup(defaultPriorityLevel));
		}
	}

	/**
	 * @return true if there is at least one registered request,
	 * all priority groups combined.
	 */
	public boolean hasSomeRequestsRegistered() {
		boolean result = false;
		for (PriorityGroup pg : this.priorityGroups.values()) {
			result |= pg.size() > 0;
		}
		return result;
	}

	/**
	 * Returns the current total number of registered 
	 * request, all priority groups combined.
	 * @return The number of registered requests
	 */
	public int getNbRequestsRegistered() {
		int sum = 0;
		for (PriorityGroup pg : this.priorityGroups.values()) {
			sum += pg.size();
		}
		return sum;
	}

	/**
	 * Return the list of all priority groups with 
	 * the current registered requests.
	 * @return The priority groups.
	 */
	public TreeMap<Byte, PriorityGroup> getPriorityGroups() {
		return this.priorityGroups;
	}

	/**
	 * Adds a {@link RunnableRequest} to the priority group it belongs to.
	 * The priority group of the request is determined from the group the 
	 * request belongs to. See {@link MethodGroup}.
	 * @param runnableRequest
	 */
	public void register(RunnableRequest runnableRequest) {
		MethodGroup group = this.compatibility.getGroupOf(
				runnableRequest.getRequest());
		if (group == null) {
			this.addToDefaultPriorityGroup(runnableRequest);
		}
		else {
			this.addToPriorityGroup(
					group.getPriorityLevel(), runnableRequest);
		}
	}

	/**
	 * Removes a registered request from the priority 
	 * group it had been assigned.
	 * @param runnableRequest
	 * @param priorityLevel
	 */
	public void unregister(
			RunnableRequest runnableRequest, byte priorityLevel) {
		this.priorityGroups.get(priorityLevel).remove(runnableRequest);
	}

	/**
	 * Return the {@link PriorityGroup} that have the highest 
	 * priority and that have at least one registered request.
	 * @return The priority group with the highest priority
	 */
	public PriorityGroup getHighestPriorityGroup() {
		for (PriorityGroup priorityGroup : this.priorityGroups.
				descendingMap().values()) {
			if (priorityGroup.size() > 0) {
				return priorityGroup;
			}
		}
		return this.priorityGroups.lastEntry().getValue();
	}
	
	private void addToDefaultPriorityGroup(RunnableRequest request) {
		this.addToPriorityGroup(defaultPriorityLevel, request);
	}

	private void addToPriorityGroup(
			byte groupLevel, RunnableRequest request) {
		this.priorityGroups.get(groupLevel).add(request);
	}

}
