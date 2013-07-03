package org.objectweb.proactive.multiactivity.priority;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;

/**
 * This class contains the thread limits and the current number of threads 
 * that are used for all the groups. 
 * 
 * @author jrochas
 */
public class ThreadManager {

	/** Represents the unbounded limit value */
	public static final int UNBOUNDED_MAX_THREADS = -1;

	/** Associate a group with its thread limit and currently used threads */
	private Set<ThreadTracker> threadLimits;

	public ThreadManager() {
		this.threadLimits = new HashSet<ThreadTracker>();
	}

	/**
	 * Creates a new entry with a group and its thread limit.
	 * @param group The group to limit thread occupancy
	 * @param threadLimit The maximum number of threads that the group can use 
	 * at a time
	 */
	public void addThreadLimit(MethodGroup group, int threadLimit) {
		this.threadLimits.add(new ThreadTracker(group, threadLimit));
	}

	/**
	 * Increases the current number of threads used by the group by 1.
	 * @param group The group to increase occupancy
	 */
	public void increaseUsage(MethodGroup group) {
		if (group != null) {
			for (ThreadTracker tracker : this.threadLimits) {
				if (tracker.group.equals(group)) {
					if (tracker.inUse < tracker.free) {
						tracker.inUse++;
					}
					break;
				}
			}
		}
	}

	/**
	 * Decreases the current number of threads used by the group by 1.
	 * @param group The group to decrease occupancy
	 */
	public void decreaseUsage(MethodGroup group) {
		if (group != null) {
			for (ThreadTracker tracker : this.threadLimits) {
				if (tracker.group.equals(group)) {
					if (tracker.inUse > UNBOUNDED_MAX_THREADS) {
						tracker.inUse--;
					}
					break;
				}
			}
		}
	}

	/**
	 * @param group
	 * @return true if the group has a higher thread limit than the current 
	 * number of threads occupied by this group.
	 */
	public boolean hasFreeThreads(MethodGroup group) {
		boolean res = true;
		if (group != null) {
			for (ThreadTracker tracker : this.threadLimits) {
				if (tracker.group.equals(group)) {
					if (tracker == null 
							|| (tracker.inUse >= tracker.free 
							&& tracker.free != UNBOUNDED_MAX_THREADS)) {
						res = false;
					}
					break;
				}
			}
		}
		return res;
	}

	/**
	 * Prints the thread usage of a given group, i.e. how many threads it 
	 * currently occupies and the maximum number of threads that it can occupy.
	 * @param group
	 * @return
	 */
	public String printUsage(MethodGroup group) {
		ThreadTracker searched = null;
		String maxString = null;
		for (ThreadTracker tracker : this.threadLimits) {
			if (tracker.group.equals(group)) {
				searched = tracker;
				int max = tracker.free;
				if (max == UNBOUNDED_MAX_THREADS) {
					maxString = "UNLIMITED";
				}
				else {
					maxString = max + "";
				}
				break;
			}
		}
		return searched.inUse + "/" + maxString ;
	}

	/**
	 * Encapsulates the current number of threads used by a group and its 
	 * maximum occupancy limit.
	 * 
	 * @author jrochas
	 */
	private class ThreadTracker {

		/** The considered group */
		public final MethodGroup group;

		/** Number of threads that are currently used */
		public int inUse;

		/** Number of threads that can be used at maximum at the same time */
		public final int free;

		public ThreadTracker(MethodGroup group, int inUse, int free) {
			this.group = group;
			if (inUse <= free) {
				this.inUse = inUse;
				this.free = free;
			}
			else {
				this.free = UNBOUNDED_MAX_THREADS;
			}
		}

		public ThreadTracker(MethodGroup group, int free) {
			this(group, 0, free);
		}

	}

}
