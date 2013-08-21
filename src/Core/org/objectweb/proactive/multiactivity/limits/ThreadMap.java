package org.objectweb.proactive.multiactivity.limits;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;

public class ThreadMap {

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
	
	public ThreadPair get(MethodGroup group) {
		return this.threadLimits.get(group);
	}
}
