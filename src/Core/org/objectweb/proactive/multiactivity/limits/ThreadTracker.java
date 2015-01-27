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
	private Map<MethodGroup, Integer> threadAccounting;

	/** Group manager (needed to guess the group of a request) */
	private CompatibilityManager compatibility;


	public ThreadTracker(CompatibilityManager compatibilityManager, 
			ThreadMap threadMap) {
		super(threadMap);
		this.compatibility = compatibilityManager;
		this.threadAccounting = new HashMap<MethodGroup, Integer>();
		Set<MethodGroup> groups = this.threadMap.getGroups();
		for (MethodGroup group : groups) {
			this.threadAccounting.put(group, 0);
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
			Integer i = this.threadAccounting.get(group);
			if (i != null) {
				this.threadAccounting.put(group, i + 1);
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
			Integer i = this.threadAccounting.get(group);
			if (i != null) {
				this.threadAccounting.put(group, i - 1);
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
		if (group != null && !group.hasSuperPriority()) {
			Integer usage = this.threadAccounting.get(group);
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
	public boolean isThreadReserved(RunnableRequest request, int freeThreads) {
		boolean isReserved = false;
		MethodGroup requestGroup = 
				this.compatibility.getGroupOf(request.getRequest());
		boolean superPriority = false;
		if (requestGroup != null) {
			superPriority = requestGroup.hasSuperPriority();
		}
		// If request is of utmost priority, we can skip this part since it 
		// has to be executed anyway.
		if (!superPriority){
			int reservedAndNotUsedThreads = 0;
			// Compute the number of threads that are reserved but not 
			// currently used, minus the ones reserved by the considered 
			// request group.
			for (MethodGroup group : this.threadMap.getGroups()) {
				if (!group.equals(requestGroup)) {
					ThreadPair groupPair = this.threadMap.get(group);
					int groupUsage = this.threadAccounting.get(group);
					if (groupPair.getMinThreads() != 0) {
						reservedAndNotUsedThreads += 
								groupPair.getMinThreads() - groupUsage > 0 ? 
										groupPair.getMinThreads() - groupUsage 
										: 0;
					}
				}
			}
			// If the number of free threads is smaller than the number of 
			// threads that are reserved by the group and that are not 
			// currently used, then we cannot use a thread to execute the 
			// request, because they are reserved for other groups.
			if (freeThreads <= reservedAndNotUsedThreads){
				isReserved = true;
			}
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
		StringBuilder sb = new StringBuilder("\n");
		int totalUsed = 0;
		for (Entry<MethodGroup, Integer> groupEntry : threadAccounting.entrySet()) {
			sb.append("group name=").append(
					groupEntry.getKey().name).append(" used=").
					append(groupEntry.getValue()).append(" reserved=").
					append(threadMap.get(groupEntry.getKey()).getMinThreads()).
					append(" maximum=").
					append(threadMap.get(groupEntry.getKey()).getMaxThreads()).
					append("\n");
			if (groupEntry.getValue() > 0) {
				totalUsed += groupEntry.getValue();
			}
		}
		sb.append("All groups - used threads = ").append(totalUsed);
		return sb.toString();
	}

}
