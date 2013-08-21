package org.objectweb.proactive.multiactivity.limits;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.proactive.multiactivity.compatibility.CompatibilityManager;
import org.objectweb.proactive.multiactivity.compatibility.CompatibilityTracker;
import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;
import org.objectweb.proactive.multiactivity.execution.RunnableRequest;

/**
 * This class contains the thread limits and the current number of threads 
 * that are used for all the groups. 
 * 
 * @author jrochas
 */
public class ThreadTracker extends ThreadManager {

	/** Associate a group with its thread limit and currently used threads */
	private Map<MethodGroup, Integer> threadUsage;
	
	/** Group manager (needed to guess the group of a request) */
	private CompatibilityManager compatibility;

	
	public ThreadTracker(CompatibilityTracker compatibilityManager, ThreadMap threadMap) {
		super(threadMap);
		this.compatibility = compatibilityManager;
		this.threadUsage = new HashMap<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void increaseUsage(RunnableRequest request) {
		MethodGroup group = this.compatibility.getGroupOf(request.getRequest());
		if (group != null) {
			Integer i = this.threadUsage.get(group);
			if (i != null) {
				this.threadUsage.put(group, i + 1);
			}
		}
	}

	/**
	 * Decreases the current number of threads used by the group by 1.
	 * @param group The group to decrease occupancy
	 */
	@Override
	public void decreaseUsage(RunnableRequest request) {
		MethodGroup group = this.compatibility.getGroupOf(request.getRequest());
		if (group != null) {
			Integer i = this.threadUsage.get(group);
			if (i != null) {
				this.threadUsage.put(group, i - 1);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasFreeThreads(MethodGroup group) {
		boolean hasFreeThreads = false;
		if (group != null) {
			Integer usage = this.threadUsage.get(group);
			if (usage != null) {
				ThreadPair groupPair = threadMap.get(group);
				if (groupPair != null) {
					int maxThreads = groupPair.getMaxThreads();
					if (usage < maxThreads) {
						hasFreeThreads = true;
					}
				}
			}
		}
		return hasFreeThreads;
	}

	/**
	 * Prints the thread usage of a given group, i.e. how many threads it 
	 * currently occupies and the maximum number of threads that it can occupy.
	 * @param group
	 * @return
	 */
	public String printUsage(MethodGroup group) {
		StringBuilder sb = new StringBuilder();
		if (group != null) {
			Integer usage = this.threadUsage.get(group);
			if (usage != null) {
				ThreadPair groupPair = threadMap.get(group);
				if (groupPair != null) {
					int maxThreads = groupPair.getMaxThreads();
					int minThreads = groupPair.getMaxThreads();
					sb.append("Group: ").append(group.name).
					append("; minThreads: ").append(minThreads).
					append("; maxThreads: ").append(maxThreads).
					append("; utilization: ").append(usage);
				}
			}
		}
		return sb.toString();
	}
}
