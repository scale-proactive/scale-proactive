package org.objectweb.proactive.multiactivity.limits;

import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;
import org.objectweb.proactive.multiactivity.execution.RunnableRequest;

public abstract class ThreadManager {
	
	public static final int THREAD_POOL_DEFAULT = Integer.MAX_VALUE;

	public static final int MAX_THREADS_DEFAULT = Integer.MAX_VALUE;
	
	public static final int MIN_THREADS_DEFAULT = 0;
	
	/** 
	 * The minimum size of the thread pool (at least the sum of minThread 
	 * limit of all the groups 
	 */
	private int threadPoolMinSize;

	protected ThreadMap threadMap;
	
	protected ThreadManager(ThreadMap threadMap) {
		this.threadMap = threadMap;
	}
	
	/**
	 * @param group
	 * @return true if the group has a higher thread limit than the current 
	 * number of threads occupied by this group.
	 */
	public abstract boolean hasFreeThreads(MethodGroup group);
	
	/**
	 * Increases the current number of threads used by the group by 1.
	 * @param group The group to increase occupancy
	 */
	public abstract void increaseUsage(RunnableRequest request);
	
	/**
	 * Decreases the current number of threads used by the group by 1.
	 * @param group The group to decrease occupancy
	 */
	public abstract void decreaseUsage(RunnableRequest request);
}
