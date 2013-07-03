package org.objectweb.proactive.multiactivity.priority;

import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;

/**
 * This interface is meant for all classes that want to define a particular 
 * structure for handling priorities. A PriorityStructure is used to reorder 
 * requests in a PriorityQueue. Here is also defined enums and constants that 
 * are related to priority structures or methods.
 * 
 * @author jrochas
 */
public interface PriorityStructure {
	
	/** The current concrete structure used for handling priorities. Calling 
	 * toString on this will give the concrete class name of the structure 
	 * currently used */
	public static final PriorityManagement currentStructure = 
			PriorityManagement.GRAPH_BASED; 
	
	/**
	 * This method aims to evaluate the priority relationship from a group1 to 
	 * a group2. More precisely, it says if group1 can overtake group2 or if 
	 * group1 cannot overtake group 2 or if they are not related at all.
	 * @param group1 The group that wants to overtake
	 * @param group2 The group that might be overtaken
	 * @return The priority relationship that links group1 with group2
	 */
	public abstract PriorityOvertakeState canOvertake(MethodGroup group1, 
			MethodGroup group2);
	
	/**
	 * This enum aims to specify which kind of structure is really used to 
	 * handle the priorities.
	 * 
	 * @author jrochas
	 */
	public enum PriorityManagement {
		
		RANK_BASED, GRAPH_BASED;
		
		/**
		 * {@inheritDoc}
		 * Note: the returned String is the class name of the concrete structure 
		 * that corresponds to the given enum.
		 */
		@Override
		public String toString() {
			String toReturn = null;
			if (this.name().equals(RANK_BASED)) {
				toReturn = "PriorityRanking";
			}
			if (this.name().equals(GRAPH_BASED)) {
				toReturn = "PriorityGraph";
			}
			return toReturn;
		}
		
	}
	
	/**
	 * This enum describes the priority relationship that links two groups. It 
	 * is kind of a boolean value with three states: true, false, and not 
	 * related. 
	 * 
	 * @author jrochas
	 */
	public enum PriorityOvertakeState {
		
		TRUE, FALSE, UNRELATED;
		
		/**
		 * This method defines kind of a AND operator for values of this enum.
		 * @param pr1 The first member of the AND operation
		 * @param pr2 The second member of the AND operation
		 * @return The enum that is the result of the AND operation
		 */
		public static PriorityOvertakeState and(PriorityOvertakeState pr1, PriorityOvertakeState pr2) {
			PriorityOvertakeState result = UNRELATED;
			switch (pr1) {
			case TRUE:
				if (pr2.equals(FALSE)) {
					result = FALSE;
				}
				else {
					result = TRUE;
				}
				break;
			case FALSE:
				result = FALSE;
				break;
			case UNRELATED:
				result = pr2;
				break;
			}
			return result;		
		}
		
	}
	
}
