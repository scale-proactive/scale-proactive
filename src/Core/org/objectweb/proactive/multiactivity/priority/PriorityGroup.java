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

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.objectweb.proactive.multiactivity.execution.RunnableRequest;

/**
 * Groups requests that have the same priority level.
 * This class enables to access all the ready requests with the same
 * priority, without having to check their group at scheduling time.
 * 
 * @author The ProActive Team
 */
public class PriorityGroup implements Comparator<PriorityGroup>, 
Iterable<RunnableRequest> {

	// The priority level is "cached" in the
	// PriorityGroup class to enable fast lookup
	private final byte priorityLevel;

	// The list of ready requests. 
	// The real type should preserve FIFO 
	// ordering to dequeue the oldest request.
	private final Set<RunnableRequest> requests;

	
	public PriorityGroup(byte priorityLevel) {
		this.priorityLevel = priorityLevel;
		this.requests = new LinkedHashSet<RunnableRequest>();
	}
	
	/**
	 * Add a {@link RunnableRequest} in the ready list of the priority group.
	 * @param request
	 */
	public void add(RunnableRequest request) {
		this.requests.add(request);
	}

	/**
	 * @param request
	 * @return true if the priority group contains the specified request.
	 */
	public boolean contains(RunnableRequest request) {
		return this.requests.contains(request);
	}

	/**
	 * Empties all registered requests from the group.
	 */
	public void clear() {
		this.requests.clear();
	}

	/**
	 * Returns the priority level of all requests registered in
	 * this priority group. Note: it means that either the requests
	 * are from the same group or their respective group has the
	 * same priority level, since priorities are given per group.
	 * @return The priority level of the group.
	 */
	public byte getPriorityLevel() {
		return this.priorityLevel;
	}

	/**
	 * Removes a registered request from the priority group.
	 * @param request
	 * @return
	 */
	public boolean remove(RunnableRequest request) {
		return this.requests.remove(request);
	}

	/**
	 * @return The number of registered ready requests.
	 */
	public int size() {
		return this.requests.size();
	}

	/**
	 * {@inheritDoc}
	 */
	 @Override
	 public int compare(PriorityGroup pg1, PriorityGroup pg2) {
		 return pg1.priorityLevel - pg2.priorityLevel;
	 }

	 /**
	  * {@inheritDoc}
	  */
	 @Override
	 public int hashCode() {
		 final int prime = 31;
		 int result = 1;
		 result = prime * result + this.priorityLevel;
		 result = prime * result + ((this.requests == null)
				 ? 0 : this.requests.hashCode());
		 return result;
	 }

	 /**
	  * {@inheritDoc}
	  */
	 @Override
	 public boolean equals(Object obj) {
		 if (this == obj) {
			 return true;
		 }
		 if (obj == null) {
			 return false;
		 }
		 if (getClass() != obj.getClass()) {
			 return false;
		 }
		 PriorityGroup other = (PriorityGroup) obj;
		 if (this.priorityLevel != other.priorityLevel) {
			 return false;
		 }
		 if (this.requests == null) {
			 if (other.requests != null) {
				 return false;
			 }
		 } else if (!this.requests.equals(other.requests)) {
			 return false;
		 }
		 return true;
	 }

	 /**
	  * {@inheritDoc}
	  */
	 @Override
	 public Iterator<RunnableRequest> iterator() {
		 return requests.iterator();
	 }

}
