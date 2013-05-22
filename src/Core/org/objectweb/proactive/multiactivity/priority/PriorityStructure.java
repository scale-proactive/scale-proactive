package org.objectweb.proactive.multiactivity.priority;

import org.objectweb.proactive.multiactivity.execution.RunnableRequest;

public interface PriorityStructure {

	public abstract boolean canOvertake(RunnableRequest request1, RunnableRequest request2);
}
