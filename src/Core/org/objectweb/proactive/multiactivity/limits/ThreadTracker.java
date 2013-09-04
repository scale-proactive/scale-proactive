package org.objectweb.proactive.multiactivity.limits;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.objectweb.proactive.multiactivity.compatibility.CompatibilityManager;
import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;
import org.objectweb.proactive.multiactivity.execution.RunnableRequest;

/**
 * This class records the number of threads currently occupied by one group, 
 * and uses the {@link ThreadMap} to see if this current number is indeed in 
 * between the thread limits set in the annotations.
 * 
 * @author The ProActive Team
 */
public class ThreadTracker extends ThreadManager {

	/** Associate a group with its thread limit and currently used threads */
	private Map<MethodGroup, Integer> threadUsage;

	/** Group manager (needed to guess the group of a request) */
	private CompatibilityManager compatibility;


	public ThreadTracker(CompatibilityManager compatibilityManager, 
			ThreadMap threadMap) {
		super(threadMap);
		this.compatibility = compatibilityManager;
		this.threadUsage = new HashMap<MethodGroup, Integer>();
		Set<MethodGroup> groups = this.threadMap.getGroups();
		for (MethodGroup group : groups) {
			this.threadUsage.put(group, 0);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void increaseUsage(RunnableRequest request) {
		MethodGroup group = 
				this.compatibility.getGroupOf(request.getRequest());
		if (group != null) {
			Integer i = this.threadUsage.get(group);
			if (i != null) {
				this.threadUsage.put(group, i + 1);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void decreaseUsage(RunnableRequest request) {
		MethodGroup group = 
				this.compatibility.getGroupOf(request.getRequest());
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
	public boolean hasFreeThreads(RunnableRequest request) {
		boolean hasFreeThreads = true;
		MethodGroup group = 
				this.compatibility.getGroupOf(request.getRequest());
		// A request belonging to no group has no limit
		if (group != null) {
			Integer usage = this.threadUsage.get(group);
			if (usage != null) {
				ThreadPair groupPair = threadMap.get(group);
				if (groupPair != null) {
					int maxThreads = groupPair.getMaxThreads();
					if (usage >= maxThreads) {
						hasFreeThreads = false;
					}
				}
			}
		}
		return hasFreeThreads;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isThreadReserved(RunnableRequest request, int globalThreadUsage, 
			int globalThreadLimit) {
		boolean isReserved = false;
		MethodGroup requestGroup = 
				this.compatibility.getGroupOf(request.getRequest());
		int freeThreads = globalThreadLimit - globalThreadUsage;
		int reservedAndNotUsedThreads = 0;
		// Compute the number of threads that are reserved but not currently 
		// used, minus the ones reserved by the considered request group.
		for (MethodGroup group : this.threadMap.getGroups()) {
			if (!group.equals(requestGroup)) {
				ThreadPair groupPair = this.threadMap.get(group);
				int groupUsage = this.threadUsage.get(group);
				if (groupPair.getMinThreads() != 0) {
					reservedAndNotUsedThreads += 
							groupPair.getMinThreads() - groupUsage;
				}
			}
		}
		// If the number of free threads is smaller than the number of threads 
		// that are reserved by the group and that are not currently used, 
		// then we cannot use a thread to execute the request, because they 
		// are reserved.
		if (freeThreads <= reservedAndNotUsedThreads){
			isReserved = true;
		}
		return isReserved;
	}

	/**
	 * Prints the thread usage of a given group, i.e. how many threads it 
	 * currently occupies and the maximum number of threads that it can occupy.
	 * @param group
	 * @return
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int totalUsed = 0;
		for (Entry<MethodGroup, Integer> groupEntry : this.threadUsage.entrySet()) {
			sb.append(groupEntry.getKey().name).append(" usage=").append(
					groupEntry.getValue()).append(" reserved=").append(this.threadMap.get(groupEntry.getKey()).getMinThreads()).
					append(" maximum=").append(this.threadMap.get(groupEntry.getKey()).getMaxThreads()).append("\n");
			if (groupEntry.getValue() > 0) {
				totalUsed += groupEntry.getValue();
			}
		}
		sb.append("Used threads = ").append(totalUsed);
		return sb.toString();
	}

}
