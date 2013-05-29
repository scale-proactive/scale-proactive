package org.objectweb.proactive.multiactivity.priority;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;

public class ThreadManager {

	public static final int UNBOUNDED_MAX_THREADS = -1;
	private Map<MethodGroup, ThreadTracker> threadLimits;
	
	public ThreadManager() {
		this.threadLimits = new HashMap<MethodGroup, ThreadTracker>();
	}
	
	public void addThreadLimit(MethodGroup group, int threadLimit) {
		this.threadLimits.put(group,  new ThreadTracker(threadLimit));
	}

	public void increaseUsage(MethodGroup group) {
		if (group != null) {
			ThreadTracker groupThreadUsage = this.threadLimits.get(group);
			if (groupThreadUsage.inUse < groupThreadUsage.free) {
				groupThreadUsage.inUse++;
			}
		}
	}

	public void decreaseUsage(MethodGroup group) {
		if (group != null) {
			ThreadTracker groupThreadUsage = this.threadLimits.get(group);
			if (groupThreadUsage.inUse > UNBOUNDED_MAX_THREADS) {
				groupThreadUsage.inUse--;
			}
		}
	}

	public boolean hasFreeThreads(MethodGroup group) {
		boolean res = true;
		if (group != null) {
			ThreadTracker groupThreadUsage = this.threadLimits.get(group);
			if (groupThreadUsage == null 
					|| (groupThreadUsage.inUse >= groupThreadUsage.free 
					&& groupThreadUsage.free != UNBOUNDED_MAX_THREADS)) {
				res = false;
			}
		}
		return res;
	}
	
	public String printUsage(MethodGroup group) {
		String maxString;
		int max = this.threadLimits.get(group).free;
		if (max == UNBOUNDED_MAX_THREADS) {
			maxString = "UNLIMITED";
		}
		else {
			maxString = max + "";
		}
		return this.threadLimits.get(group).inUse + "/" + maxString ;
	}

	private class ThreadTracker {

		private int inUse;
		private final int free;

		public ThreadTracker(int inUse, int free) {
			if (inUse <= free) {
				this.inUse = inUse;
				this.free = free;
			}
			else {
				this.free = UNBOUNDED_MAX_THREADS;
			}
		}

		public ThreadTracker(int free) {
			this(0, free);
		}
	}
}
