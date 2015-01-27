package org.objectweb.proactive.multiactivity.priority;

import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;

/**
 * This interface is meant for all classes that want to define a particular 
 * structure for handling priorities. Such classes must define the canOvertake 
 * method. A PriorityMap is used to reorder requests in a queue. Here is also 
 * defined an enum that aims at fixing a particular structure used to handle 
 * priorities. It also defines constants that are related to priority 
 * structures or methods.
 * 
 * @author The ProActive Team
 */
public interface PriorityMap {
	
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
	public abstract boolean canOvertake(MethodGroup group1, 
			MethodGroup group2);
	
	
	/**
	 * This enum aims to specify which kind of structure is actually used to 
	 * handle the priorities.
	 * 
	 * @author The ProActive Team
	 */
	public enum PriorityManagement {
		
		RANK_BASED, GRAPH_BASED;
		
		/**
		 * {@inheritDoc}
		 * Note: the returned String is the class name of the concrete 
		 * structure that corresponds to the given enum.
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
	
}
