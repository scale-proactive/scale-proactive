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
 * Maintain {@link PriorityConstraint}s and registered requests classified by
 * priorities.
 * 
 * @author The ProActive Team
 */
public class PriorityManager {

	public static final int defaultPriorityLevel = -1;

	// priority groups that contain registered requests classified by priority
	private final TreeMap<Integer, PriorityGroup> priorityGroups;

	private final CompatibilityMap compatibility;

	public PriorityManager(CompatibilityMap compatibility) {

		this.priorityGroups = new TreeMap<Integer, PriorityGroup>();
		this.compatibility = compatibility;

		// indicates whether there is an annotation defined for priority group 0
		boolean defaultPriorityGroupOverriden = false;

		for (MethodGroup group : compatibility.getGroups()) {
			if (group.getPriorityLevel() == 0) {
				defaultPriorityGroupOverriden = true;
			}

			if (!this.priorityGroups.containsKey(group.getPriorityLevel())) {
				this.priorityGroups.put(
						group.getPriorityLevel(), new PriorityGroup(
								group.getPriorityLevel()));
			}
		}

		if (!defaultPriorityGroupOverriden) {
			// adds priority group for methods without priority
			this.priorityGroups.put(defaultPriorityLevel, new PriorityGroup(defaultPriorityLevel));
		}
	}

	private void addToDefaultPriorityGroup(RunnableRequest request) {
		this.addToPriorityGroup(defaultPriorityLevel, request);
	}

	private void addToPriorityGroup(int groupLevel, RunnableRequest request) {
		this.priorityGroups.get(groupLevel).add(request);
	}

	public boolean hasSomeRequestsRegistered() {
		boolean result = false;

		for (PriorityGroup pg : this.priorityGroups.values()) {
			result |= pg.size() > 0;
		}

		return result;
	}

	public int getNbRequestsRegistered() {
		int sum = 0;

		for (PriorityGroup pg : this.priorityGroups.values()) {
			sum += pg.size();
		}

		return sum;
	}

	public TreeMap<Integer, PriorityGroup> getPriorityGroups() {
		return this.priorityGroups;
	}

	public void register(RunnableRequest runnableRequest) {
		MethodGroup group = this.compatibility.getGroupOf(runnableRequest.getRequest());
		if (group == null) {
			this.addToDefaultPriorityGroup(runnableRequest);
		}
		else {
			this.addToPriorityGroup(
					group.getPriorityLevel(), runnableRequest);
		}
	}

	public void unregister(RunnableRequest runnableRequest, int priorityLevel) {
		this.priorityGroups.get(priorityLevel).remove(runnableRequest);
	}

	public PriorityGroup getHighestPriorityGroup() {
		for (PriorityGroup priorityGroup : this.priorityGroups.descendingMap()
				.values()) {
			if (priorityGroup.size() > 0) {
				return priorityGroup;
			}
		}

		return this.priorityGroups.lastEntry().getValue();
	}

}
