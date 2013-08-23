package org.objectweb.proactive.multiactivity.limits;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;

/**
 * Data structure that enables to store a group as an entry and a pair as a 
 * value of minimum and maximum thread utilization limit for this group. This 
 * structure is filled when reading the annotations.
 * 
 * @author The ProActive Team
 */
public class ThreadMap {

	/** Dictionary group -> thread limits */
	private Map<MethodGroup, ThreadPair> threadLimits;
	
	
	public ThreadMap() {
		this.threadLimits = new HashMap<>();
	}

	/**
	 * Creates a new entry with a group and its thread limit.
	 * @param group The group to limit thread occupancy
	 * @param maxThreads The maximum number of threads that the group can use 
	 * at a time
	 */
	public void setThreadLimits(MethodGroup group, int maxThreads, int minThreads) {
		this.threadLimits.put(group, new ThreadPair(maxThreads, minThreads));
	}
	
	/**
	 * @param group
	 * @return The maximum and minimum limits for this group.
	 */
	public ThreadPair get(MethodGroup group) {
		return this.threadLimits.get(group);
	}
}
