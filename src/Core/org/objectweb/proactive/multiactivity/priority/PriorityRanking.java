package org.objectweb.proactive.multiactivity.priority;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;
import org.objectweb.proactive.multiactivity.execution.RunnableRequest;

/**
 * Internal representation of priorities defined with 
 * {@link DefineRankBasedPriorities}. The structure used here to store groups 
 * and their priority is dictionary-based: for a given priority (that is an 
 * integer), we can access all the groups belonging to it and all the current 
 * registered requests for them. An inner class is defined to represent both 
 * the groups and the requests for a given priority: {@link PriorityRank}.
 * 
 * @author jrochas
 */
public class PriorityRanking implements PriorityStructure {
	
	/** The level of priority assigned to requests of default group */
	public static final int defaultPriorityLevel = 0;

	/** The dictionary containing the priorities and their associated groups
	 * and requests */
	private TreeMap<Integer, PriorityRank> priorityRanks;

	/**
	 * Initialize a new ranking and create the default rank with default 
	 * priority.
	 */
	public PriorityRanking() {
		this.priorityRanks = new TreeMap<Integer, PriorityRank>();
		this.priorityRanks.put(defaultPriorityLevel, new PriorityRank());
	}

	/**
	 * Inserts a new group in the priority structure.
	 * @param level The level of priority
	 * @param group The group to add
	 */
	public void insert(int level, MethodGroup group) {
		// The structure already has this priority level
		if (this.priorityRanks.containsKey(level)) {
			this.priorityRanks.get(level).addGroup(group);
		}
		// This priority level is a new one
		else {
			PriorityRank priorityRank = new PriorityRank();
			priorityRank.addGroup(group);
			this.priorityRanks.put(level, priorityRank);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void register(RunnableRequest request) {
		//int priorityLevel = findPriority(request);
		//this.priorityRanks.get(priorityLevel).addRequest(request);
	}

	/**
	 * {@inheritDoc}
	 */
	public void unregister(RunnableRequest request) {
		//int priorityLevel = findPriority(request);
		//this.priorityRanks.get(priorityLevel).removeRequest(request);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasRegisteredRequests() {
		boolean hasRegisteredRequests = false;
		// Look if some requests are registered for each rank
		for (Entry<Integer, PriorityRank> entry : this.priorityRanks.
				entrySet()) {
			if (entry.getValue().hasRegisteredRequests()) {
				hasRegisteredRequests = true;
				break;
			}
		}
		return hasRegisteredRequests;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getNbRegisteredRequests() {
		int nbRegisteredRequests = 0;
		// Add all the registered requests of all the ranks
		for (Entry<Integer, PriorityRank> entry : this.priorityRanks.
				entrySet()) {
			nbRegisteredRequests += entry.getValue().getNbRegisteredRequests();
		}
		return nbRegisteredRequests;
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<RunnableRequest> getRequestsToExecute() {
		Iterator<RunnableRequest> iterator = null;
		// First order the map with highest priority as head
		Map<Integer, PriorityRank> orderedMap = 
				this.priorityRanks.descendingMap();
		// Return the requests of the first rank that has a non empty request 
		// list
		for (Entry<Integer, PriorityRank> entry : orderedMap.entrySet()) { 
			iterator = entry.getValue().requestIterator();
			if (entry.getValue().hasRegisteredRequests()) {
				break;
			}
		}
		return iterator;
	}

	/**
	 * {@inheritDoc}
	 */
	/*public String snapshot(Iterator<RunnableRequest> iterator) {
		// TODO Use iterator to find out groups of selected requests
		String groupName;
		MethodGroup group;
		Iterator<MethodGroup> groupIterator;
		Iterator<RunnableRequest> requestIterator;
		
		StringBuilder sb = new StringBuilder();
		String enter = System.getProperty("line.separator");
		Map<String, Integer> requestMap = new HashMap<String, Integer>();
		
		// The goal is to build a map<group name, number of requests> for each 
		// rank. Note: this map could not be used to maintain the priority 
		// structure because we would lose FIFO ordering for requests from 
		// different groups but with the same priority rank.
		for (Entry<Integer, PriorityRank> entry : this.priorityRanks.
				entrySet()) {
			
			sb.append("\t\t\tPriority rank " + entry.getKey() + ": " 
				+ entry.getValue().getNbRegisteredRequests() 
				+ " requests. Details:" + enter);
			
			groupIterator = entry.getValue().groupIterator();
			requestIterator = entry.getValue().requestIterator();
			
			// Initialize the keys of the map
			while (groupIterator.hasNext()) {
				requestMap.put(groupIterator.next().name, 0);
			}
			
			// Update the values of the map
			while (requestIterator.hasNext()) {
				group = this.compatibility.getGroupOf(
						requestIterator.next().getRequest());
				if (group != null) {
					groupName = group.name;
				}
				else {
					groupName = MethodGroup.defaultGroupName;
				}
				requestMap.put(groupName, requestMap.get(groupName) == null ? 
						1 : requestMap.get(groupName) + 1);
			}
			
			for (Entry<String, Integer> e : requestMap.entrySet()) {
				sb.append("\t\t\t\t" + e.getKey() + "\t\t" + e.getValue() 
						+ " requests" + enter);
			}
			
			requestMap.clear();
		}
		
		return sb.toString();
	}*/
	
	/**
	 * To find the level of priority of the request, this method first looks 
	 * for the group associated to the request and then the priority level
	 * of the group. 
	 * @param request
	 * @return The level of priority of the request
	 */
	/*private int findPriority(RunnableRequest request) {
		int priorityLevel = 0;
		MethodGroup group = 
				this.compatibility.getGroupOf(request.getRequest());
		if (group != null) {
			priorityLevel = group.getPriorityLevel();
		}
		return priorityLevel;
	}*/

	/**
	 * Encapsulates a set of groups for a given priority and 
	 * a set of ready requests belonging to those groups.
	 * 
	 * @author jrochas
	 */
	private class PriorityRank {

		private Set<MethodGroup> methodGroups;
		private Set<RunnableRequest> registeredRequests;

		public PriorityRank() {
			this.methodGroups = new HashSet<MethodGroup>();
			this.registeredRequests = new LinkedHashSet<RunnableRequest>();
		}

		public void addGroup(MethodGroup group) {
			this.methodGroups.add(group);
		}

		public void addRequest(RunnableRequest request) {
			this.registeredRequests.add(request);
		}

		public void removeRequest(RunnableRequest request) {
			this.registeredRequests.remove(request);
		}

		public boolean hasRegisteredRequests() {
			return !this.registeredRequests.isEmpty() ;
		}

		public int getNbRegisteredRequests() {
			return this.registeredRequests.size();
		}

		public Iterator<MethodGroup> groupIterator() {
			return this.methodGroups.iterator();
		}

		public Iterator<RunnableRequest> requestIterator() {
			return this.registeredRequests.iterator();
		}

	}

	@Override
	public boolean canOvertake(RunnableRequest request1,
			RunnableRequest request2) {
		return false;
	}

}
