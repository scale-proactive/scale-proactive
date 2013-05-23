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
	 * Cannot return unrelated because there exist an order inevitably
	 */
	@Override
	public PriorityOvertakeState canOvertake(MethodGroup group1,
			MethodGroup group2) {
		PriorityOvertakeState pr = PriorityOvertakeState.UNRELATED;
		Entry<Integer, PriorityRank> group1Entry = null;
		Entry<Integer, PriorityRank> group2Entry = null;
		for (Entry<Integer, PriorityRank> entry : this.priorityRanks.entrySet()) {
			if (entry.getValue().contains(group1)) {
				group1Entry = entry;
			}
			if (entry.getValue().contains(group2)) {
				group2Entry = entry;
			}
		}
		if (group1Entry != null && group2Entry != null) {
			if (group1Entry.getKey() > group2Entry.getKey()) {
				pr = PriorityOvertakeState.TRUE;
			}
			if (group1Entry.getKey() == group2Entry.getKey()) {
				pr = PriorityOvertakeState.UNRELATED;
			}
			if (group1Entry.getKey() < group2Entry.getKey()) {
				pr = PriorityOvertakeState.FALSE;
			}
		}
		return pr;
	}

	/**
	 * Encapsulates a set of groups for a given priority and 
	 * a set of ready requests belonging to those groups.
	 * 
	 * @author jrochas
	 */
	private class PriorityRank {

		private Set<MethodGroup> methodGroups;

		public PriorityRank() {
			this.methodGroups = new HashSet<MethodGroup>();
		}

		public void addGroup(MethodGroup group) {
			this.methodGroups.add(group);
		}
		
		public boolean contains(MethodGroup group) {
			return this.methodGroups.contains(group);
		}

	}

}
