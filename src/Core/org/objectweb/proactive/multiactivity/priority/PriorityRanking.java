package org.objectweb.proactive.multiactivity.priority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;

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
	
	private static boolean matrixEnabled = true;
	private ArrayList<MethodGroup> nodesList;
	private HashMap<String, HashMap<String, Boolean>> existPathMatrix;

	/** The level of priority assigned to requests of default group */
	public static final int defaultPriorityLevel = 0;

	/** The dictionary containing the priorities and their associated groups
	 * and requests */
	private Set<PriorityRank> priorityRanks;

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
	 * Cannot return unrelated because there exist an order inevitably
	 */
	@Override
	public boolean canOvertake(MethodGroup group1,
			MethodGroup group2) {
		boolean overtake = false;
		if (this.matrixEnabled) {
			Boolean returnValue = false;
			if (this.nodesList == null) {
				this.nodesList = new ArrayList<MethodGroup>();
				for (PriorityRank rank : this.priorityRanks) {			
					this.nodesList.addAll(rank.methodGroups);
				}
			}
			if (this.existPathMatrix == null) {
				int size = this.nodesList.size();
				this.existPathMatrix = new HashMap<String, HashMap<String, Boolean>>(size);
				for (int i = 0 ; i < size; i++) {
					MethodGroup pni = this.nodesList.get(i);
					HashMap<String, Boolean> map = new HashMap<String, Boolean>(size);
					for (int j = 0 ; j < size; j++) {
						MethodGroup pnj = this.nodesList.get(j);	
						
						PriorityRank rank1 = null;
						PriorityRank rank2 = null;
						for (PriorityRank rank : this.priorityRanks) {
							if (rank.methodGroups.contains(pni)) {
								rank1 = rank;
							}
							if (rank.methodGroups.contains(pnj)) {
								rank2 = rank;
							}
						}
						boolean canOvert = false;
						if (rank1 != null && rank2 != null) {
							if (rank1.rankNumber > rank2.rankNumber) {
								canOvert = true;
							}
						}
						
						map.put(this.nodesList.get(j).name, canOvert);						
					}
					this.existPathMatrix.put(this.nodesList.get(i).name, map);
				}
				StringBuilder sb = new StringBuilder();
				for (Entry<String, HashMap<String,Boolean>> entry :this.existPathMatrix.entrySet()) {
					sb.append("** " + entry.getKey() + ": ");
					for (Entry<String, Boolean> entry2 : entry.getValue().entrySet()) {
						sb.append(entry2.getKey() + ":" + entry2.getValue() + " ");
					}
					sb.append("\n");
				}
				PriorityUtils.logMessage(sb.toString());
			}
			HashMap<String, Boolean> intermediateMatrix = this.existPathMatrix.get(group1.name);
			if (intermediateMatrix != null) {
				returnValue = intermediateMatrix.get(group2.name);
				if (returnValue != null) {
					return returnValue;
				}
				else { 
					return false;
				}
			}
			else {
				return false;
			}
		}
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
					overtake = true;
				}
			}
		}
		return overtake;
	}

	/**
	 * Encapsulates a set of groups for a given priority and 
	 * a set of ready requests belonging to those groups.
	 * 
	 * @author jrochas
	 */
	private class PriorityRank {

		/** Groups of the same priority rank */
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
