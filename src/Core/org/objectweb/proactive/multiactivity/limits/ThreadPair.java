package org.objectweb.proactive.multiactivity.limits;

/**
 * Stores a pair of maximum and minimum limits that represents the thread 
 * utilization limits of a group.
 * 
 * @author The ProActive Team
 */
public class ThreadPair {

	/** Minimum number of threads reserved */
	private final int minThreads;
	
	/** Maximum number of threads occupied at the same time */
	private final int maxThreads;
	
	
	public ThreadPair(int minThreads, int maxThreads) {		
		this.minThreads = minThreads;
		this.maxThreads = maxThreads;
	}
	
	/**
	 * @return The minimum limit.
	 */
	public int getMinThreads() {
		return minThreads;
	}
	
	/**
	 * @return The maximum limit.
	 */
	public int getMaxThreads() { 
		return maxThreads;
	}
	
}
