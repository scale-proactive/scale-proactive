package org.objectweb.proactive.multiactivity.priority;

import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;

import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;

public class ThreadManager {

	public static final int UNBOUNDED_MAX_THREADS = -1;
	private Map<MethodGroup, ThreadUsage> threadMonitor;

	public ThreadManager(Map<MethodGroup, Integer> threadLimits) {
		this.threadMonitor = new HashMap<MethodGroup, ThreadUsage>();
		for (Entry<MethodGroup, Integer> entry : threadLimits.entrySet()) {
			this.threadMonitor.put(entry.getKey(), new ThreadUsage(entry.getValue()));
		}
	}

	public void increaseUsage(MethodGroup group) {
		if (group != null) {
			ThreadUsage groupThreadUsage = this.threadMonitor.get(group);
			if (groupThreadUsage.inUse < groupThreadUsage.free) {
				groupThreadUsage.inUse++;
			}
		}
	}

	public void decreaseUsage(MethodGroup group) {
		if (group != null) {
			ThreadUsage groupThreadUsage = this.threadMonitor.get(group);
			if (groupThreadUsage.inUse > UNBOUNDED_MAX_THREADS) {
				groupThreadUsage.inUse--;
			}
		}
	}

	public boolean hasFreeThreads(MethodGroup group) {
		boolean res = true;
		if (group != null) {
			ThreadUsage groupThreadUsage = this.threadMonitor.get(group);
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
		int max = this.threadMonitor.get(group).free;
		if (max == UNBOUNDED_MAX_THREADS) {
			maxString = "UNLIMITED";
		}
		else {
			maxString = max + "";
		}
		return this.threadMonitor.get(group).inUse + "/" + maxString ;
	}

	private class ThreadUsage {

		private int inUse;
		private final int free;

		public ThreadUsage(int inUse, int free) {
			if (inUse <= free) {
				this.inUse = inUse;
				this.free = free;
			}
			else {
				this.free = UNBOUNDED_MAX_THREADS;
			}
		}

		public ThreadUsage(int free) {
			this(0, free);
		}
	}
}
