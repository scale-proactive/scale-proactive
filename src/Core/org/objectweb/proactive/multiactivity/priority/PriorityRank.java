package org.objectweb.proactive.multiactivity.priority;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;

public class PriorityRank {

	private Map<Integer, Set<MethodGroup>> priorityLevels;
	
	public PriorityRank() {
		this.priorityLevels = new TreeMap<Integer, Set<MethodGroup>>();
	}
	
	public void insert(MethodGroup group, int level) {
		if (this.priorityLevels.containsKey(level)) {
			this.priorityLevels.get(level).add(group);
		}
		else {
			HashSet<MethodGroup> methodGroups = new HashSet<MethodGroup>();
			methodGroups.add(group);
			this.priorityLevels.put(level, methodGroups);
		}
	}
}
