package org.objectweb.proactive.multiactivity.limits;

import org.objectweb.proactive.multiactivity.execution.RunnableRequest;

/**
 * This class aims at managing the threads according to the thread limits that 
 * were set in the annotations. In particular, the {@link RequestExecutor} 
 * must ask this class if a ready-to-execute request has enough threads left 
 * to actually execute.
 * 
 * @author The ProActive Team
 */
public abstract class ThreadManager {
	
	/** Default thread pool size */
	public static final int THREAD_POOL_DEFAULT = Integer.MAX_VALUE;
	
	/** Number of threads added to the thread pool in case the size of the 
	 * thread pool = the sum of reserved threads (this variable should not 
	 * be 0 to prevent deadlocks) */
	public static final int THREAD_POOL_MARGIN = 1;

	/** Default maximum number of threads that can be occupied at the same time 
	 * by one group */
	public static final int MAX_THREADS_DEFAULT = Integer.MAX_VALUE;
	
	/** Default number of reserved threads for a given group */
	public static final int MIN_THREADS_DEFAULT = 0;
	
	/** Default value of boolean that says if we limit the number of active 
	 * threads or the number of total threads. */
	public static final boolean HARD_LIMIT_DEFAULT = false;
	
	/** Default value of boolean that says whether reentrant calls are hosted 
	 * on the same thread. */
	public static final boolean HOST_REENTRANT_DEFAULT = false;

	/** The structure that stores all the limits set for the groups */
	protected ThreadMap threadMap;
	
	
	protected ThreadManager(ThreadMap threadMap) {
		this.threadMap = threadMap;
	}
	
	public int getThreadPoolSize() {
		return this.threadMap.getThreadPoolSize();
	}
	
	public boolean getHardLimit() {
		return this.threadMap.getHardLimit();
	}
	
	public boolean getHostReentrant() {
		return this.threadMap.getHostReentrant();
	}
	
	/**
	 * @param group
	 * @return true if the group has a higher thread limit than the current 
	 * number of threads occupied by this group.
	 */
	public abstract boolean hasFreeThreads(RunnableRequest request);
	
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

	/**
	 * @param size
	 * @param tHREAD_LIMIT
	 * @return true if all the free current threads belong to reserved threads.
	 */
	public abstract boolean isThreadReserved(RunnableRequest request, 
			int freeThreads);

}
