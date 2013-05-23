package org.objectweb.proactive.multiactivity.priority;

import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;

public interface PriorityStructure {

	public abstract PriorityOvertakeState canOvertake(MethodGroup group1, MethodGroup group2);
	
	public enum PriorityOvertakeState {
		TRUE, FALSE, UNRELATED;
		
		public static PriorityOvertakeState and(PriorityOvertakeState pr1, PriorityOvertakeState pr2) {
			PriorityOvertakeState result = UNRELATED;
			switch (pr1) {
			case TRUE:
				if (pr2.equals(FALSE)) {
					result = FALSE;
				}
				else {
					result = TRUE;
				}
				break;
			case FALSE:
				result = FALSE;
				break;
			case UNRELATED:
				result = pr2;
				break;
			}
			return result;		
		}
	}
	
}
