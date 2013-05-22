package org.objectweb.proactive.multiactivity.priority;

import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;

public interface PriorityStructure {

	public abstract boolean canOvertake(MethodGroup group1, MethodGroup group2);
}
