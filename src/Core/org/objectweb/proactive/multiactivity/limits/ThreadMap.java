package org.objectweb.proactive.multiactivity.limits;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
	
	/** The size of the thread pool */
	private int threadPoolSize;
	
	/** Hard or soft limit (whether to limit the total number of threads, or 
	 * only the ones that are active) */
	private boolean hardLimit;
	
	/** Whether to serve re-entrant calls on the same thread as their source 
	 */
	private boolean hostReentrant;
	
	/** Whether the thread config is specified through annotations */
	private boolean isConfiguredThroughAnnot;
	
	
	public ThreadMap() {
		this.threadLimits = new HashMap<>();
		this.threadPoolSize = ThreadManager.THREAD_POOL_DEFAULT;
		this.hardLimit = ThreadManager.HARD_LIMIT_DEFAULT;
		this.hostReentrant = ThreadManager.HOST_REENTRANT_DEFAULT;
		this.isConfiguredThroughAnnot = false;
	}

	/**
	 * Creates a new entry with a group and its thread limit.
	 * @param group The group to limit thread occupancy
	 * @param maxThreads The maximum number of threads that the group can use 
	 * at a time
	 */
	public void setThreadLimits(MethodGroup group, int minThreads, 
			int maxThreads) {
		this.threadLimits.put(group, new ThreadPair(minThreads, maxThreads));
	}
	
	/**
	 * Configure the thread manager for future executions.
	 * @param threadPoolSize Size of thread pool
	 * @param hardLimit Whether the limit applies on active threads or not 
	 * @param hostReentrant Whether to use the same thread for reentrant calls
	 */
	public void configure(int threadPoolSize, boolean hardLimit, 
			boolean hostReentrant) {
		this.threadPoolSize = threadPoolSize;
		this.hardLimit = hardLimit;
		this.hostReentrant = hostReentrant;
	}
	
	/**
	 * @param group
	 * @return The maximum and minimum limits for this group.
	 */
	public ThreadPair get(MethodGroup group) {
		return this.threadLimits.get(group);
	}
	
	/**
	 * @return The set of groups that have thread limits.
	 */
	public Set<MethodGroup> getGroups() {
		return this.threadLimits.keySet();
	}
	
	/**
	 * @return The current thread pool size.
	 */
	public int getThreadPoolSize() {
		return this.threadPoolSize;
	}
	
	/**
	 * @return Whether the thread limit applies on active threads or not.
	 */
	public boolean getHardLimit() {
		return this.hardLimit;
	}
	
	/**
	 * @return Whether reentrant calls are hosted on the same threads.
	 */
	public boolean getHostReentrant() {
		return this.hostReentrant;
	}
	
	/**
	 * Sets the value of configuredThroughAnnotation to true.
	 */
	public void setConfiguredThroughAnnot() {
		this.isConfiguredThroughAnnot = true;
	}
	
	/**
	 * @return Whether the thread config is configured using an annotation.
	 */
	public boolean isConfiguredThroughAnnot() {
		return this.isConfiguredThroughAnnot;
	}
	
}
