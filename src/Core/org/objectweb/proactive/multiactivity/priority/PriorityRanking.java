package org.objectweb.proactive.multiactivity.priority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;

/**
 * Internal representation of priorities defined with the annotation:
 * {@link DefineRankBasedPriorities}. The structure used here to store groups 
 * and their priority is dictionary-based: for a given priority (that is an 
 * integer), we can access all the groups belonging to it and all the current 
 * registered requests for them. An inner class is defined to represent both 
 * the groups and the requests for a given priority: {@link PriorityRank}.
 * 
 * @author The ProActive Team
 */
public class PriorityRanking implements PriorityMap {

	/** The level of priority assigned to requests of default group */
	public static final int defaultPriorityLevel = 0;

	/** The dictionary containing the priorities and their associated groups
	 * and requests */
	private Set<PriorityRank> priorityRanks;

	/** List of all groups that have a priority */
	private ArrayList<MethodGroup> groupList;

	/** 
	 * Matrix that stores a boolean value that represents whether the first 
	 * entry can overtake the second. This speeds up the insertion process.
	 */
	private HashMap<String, HashMap<String, Boolean>> existPathMatrix;

	/** Specifies whether the matrix optimization should be used or not */
	private static boolean matrixEnabled = true;


	/**
	 * Initialize a new ranking and create the default rank with default 
	 * priority.s
	 */
	public PriorityRanking() {
		this.priorityRanks = new HashSet<PriorityRank>();
		this.priorityRanks.add(new PriorityRank(defaultPriorityLevel));
	}

	/**
	 * Inserts a new group in the priority structure.
	 * @param level The level of priority
	 * @param group The group to add
	 */
	public void insert(int level, MethodGroup group) {
		PriorityRank existingRank = null;
		for (PriorityRank rank : this.priorityRanks) {
			if (rank.rankNumber == level) {
				existingRank = rank;
				break;
			}
		}
		// The structure already has this priority level
		if (existingRank != null) {
			existingRank.methodGroups.add(group);
		}
		// This priority level is a new one
		else {
			PriorityRank priorityRank = new PriorityRank(level);
			priorityRank.methodGroups.add(group);
			this.priorityRanks.add(priorityRank);
		}
	}

	/** 
	 * {@inheritDoc}
	 */
	@Override
	public boolean canOvertake(MethodGroup group1, MethodGroup group2) {
		Boolean canOvertake = false;
		// group1 has a super priority, it can overtake group2 if group2 does 
		// not have a super priority
		if (group1.hasSuperPriority()) {
			return !group2.hasSuperPriority();
		}
		// We use the matrix optimization
		if (matrixEnabled) {
			// The group list does not exist yet, build it
			if (this.groupList == null) {
				this.groupList = new ArrayList<MethodGroup>();
				for (PriorityRank rank : this.priorityRanks) {			
					this.groupList.addAll(rank.methodGroups);
				}
			}
			//The matrix does not exist yet, build it
			if (this.existPathMatrix == null) {
				boolean subCanOvertake;
				PriorityRank rank1;
				PriorityRank rank2;
				int size = this.groupList.size();
				this.existPathMatrix = 
						new HashMap<String, HashMap<String, Boolean>>(size);
				for (MethodGroup gi : this.groupList) {
					HashMap<String, Boolean> map = new HashMap<>(size);
					for (MethodGroup gj : this.groupList) {		
						rank1 = null;
						rank2 = null;
						for (PriorityRank rank : this.priorityRanks) {
							if (rank.methodGroups.contains(gi)) {
								rank1 = rank;
							}
							if (rank.methodGroups.contains(gj)) {
								rank2 = rank;
							}
						}
						subCanOvertake = false;
						if (rank1 != null && rank2 != null) {
							if (rank1.rankNumber > rank2.rankNumber) {
								subCanOvertake = true;
							}
						}			
						map.put(gj.name, subCanOvertake);						
					}
					this.existPathMatrix.put(gi.name, map);
				}
			}
			// Return the matrix entry of group1->group2
			HashMap<String, Boolean> intermediateMatrix = 
					this.existPathMatrix.get(group1.name);
			if (intermediateMatrix != null) {
				canOvertake = intermediateMatrix.get(group2.name);
				if (canOvertake == null) {
					canOvertake = false;
				}
			}
		}
		// We do not use the matrix optimization: go through all ranks
		else {
			PriorityRank rank1 = null;
			PriorityRank rank2 = null;
			for (PriorityRank rank : this.priorityRanks) {
				if (rank.methodGroups.contains(group1)) {
					rank1 = rank;
				}
				if (rank.methodGroups.contains(group2)) {
					rank2 = rank;
				}
			}
			if (rank1 != null && rank2 != null) {
				if (rank1.rankNumber > rank2.rankNumber) {
					canOvertake = true;
				}
			}
		}
		return canOvertake;
	}


	/**
	 * Encapsulates a set of groups for a given priority and a set of ready 
	 * requests belonging to those groups.
	 * 
	 * @author The ProActive Team
	 */
	private class PriorityRank {

		/** Groups belonging to the same priority rank */
		public Set<MethodGroup> methodGroups;

		/** Level of priority of the rank. Note : the higher this number is, 
		 * the higher the priority is. */
		public final int rankNumber;


		public PriorityRank(int rankNumber) {
			this.methodGroups = new HashSet<MethodGroup>();
			this.rankNumber = rankNumber;
		}		

	}

}
