package org.objectweb.proactive.multiactivity.priority;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.proactive.multiactivity.compatibility.CompatibilityMap;
import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;
import org.objectweb.proactive.multiactivity.execution.RunnableRequest;

public class PriorityTracker {

	private MethodGroup defaultGroup;
	private CompatibilityMap compatibility;
	private Map<MethodGroup, List<RunnableRequest>> registeredRequests;

	public PriorityTracker(CompatibilityMap compatibility) {
		this.compatibility = compatibility;
		this.defaultGroup = new MethodGroup("default", false);
		this.registeredRequests = new HashMap<MethodGroup, List<RunnableRequest>>();
		this.registeredRequests.put(defaultGroup, new ArrayList<RunnableRequest>());
	}

	public void register(RunnableRequest runnableRequest) {
		MethodGroup group = 
				this.compatibility.getGroupOf(runnableRequest.getRequest());
		// If group is null it means that the request does not belong 
		// to any group so we add it in a default MethodGroup
		if (group == null) {
			this.registeredRequests.get(this.defaultGroup).add(runnableRequest);
		}
		else {
			if (this.registeredRequests.containsKey(group)) {
				this.registeredRequests.get(group).add(runnableRequest);
			}
			else {
				ArrayList<RunnableRequest> requestList = new ArrayList<RunnableRequest>();
				requestList.add(runnableRequest);
				this.registeredRequests.put(group, requestList);
			}
		}
	}

	public void unregister(RunnableRequest runnableRequest) {
		MethodGroup group = 
				this.compatibility.getGroupOf(runnableRequest.getRequest());
		// If group is null it means that the request belong to the default group
		if (group == null) {
			this.registeredRequests.get(this.defaultGroup).remove(runnableRequest);
		}
		else {
			if (this.registeredRequests.containsKey(group)) {
				this.registeredRequests.get(group).remove(runnableRequest);
			}
		}
	}

	public boolean hasFreeBoostThreads() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasSomeRequestsRegistered() {
		boolean contains = false;
		for (Entry<MethodGroup, List<RunnableRequest>> entry : this.registeredRequests.entrySet()) {
			if (entry.getValue().size() > 0) {
				contains = true;
			}
		}
		return contains;
	}

	@SuppressWarnings("unused")
	public int getNbRequestsRegistered() {
		int sum = 0;
		for (Entry<MethodGroup, List<RunnableRequest>> entry : this.registeredRequests.entrySet()) {
			for (RunnableRequest request : entry.getValue()) {
				sum++;
			}
		}
		return sum;
	}

	public void decrementActiveBoostThreads() {
		// TODO Auto-generated method stub

	}

	public Entry<MethodGroup, List<RunnableRequest>> getRequestsOfHighestPriorityGroup() {
		int maxPriority = -1;
		Entry<MethodGroup, List<RunnableRequest>> maxPriorityGroup = null;
		for (Entry<MethodGroup, List<RunnableRequest>> entry : this.registeredRequests.entrySet()) {
			if (entry.getValue().size() > 0) {
				/*if (entry.getKey().getPriorityLevel() > maxPriority) {
					maxPriority = entry.getKey().getPriorityLevel();
					maxPriorityGroup = entry;
				}*/
			}
		}
		return maxPriorityGroup;
	}



	public String snapshotRequestsWithPriorities(MethodGroup chosenGroup) {	
		StringBuilder buf = new StringBuilder();
		buf.append("Priority groups snapshot\n");

		for (Entry<MethodGroup, List<RunnableRequest>> entry : this.registeredRequests.entrySet()) {
			/*buf.append("  groupLevel=" + entry.getKey().getPriorityLevel()
					+ "\t nbRequests=" + entry.getValue().size() + ((entry.getKey() == chosenGroup)
							? "\t [selected]" : "") + " ");*/
			for (RunnableRequest request : entry.getValue()) {
				buf.append(request.getRequest().getMethodName() + " ");
			}
			buf.append(File.pathSeparatorChar);
		}

		buf.append("  default threads usage ");
		//buf.append(this.countActive());
		buf.append("/");
		//buf.append(this.THREAD_LIMIT);
		buf.append("\n");
		return buf.toString();
	}
}