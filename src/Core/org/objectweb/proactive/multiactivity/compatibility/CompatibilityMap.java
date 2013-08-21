package org.objectweb.proactive.multiactivity.compatibility;

import java.util.HashMap;
import java.util.Map;

public class CompatibilityMap {
	/** Group name -> group */
    private Map<String, MethodGroup> groups = new HashMap<String, MethodGroup>();
	
	/** Method name -> group */
    private Map<String, MethodGroup> membership = new HashMap<String, MethodGroup>();
    
    public CompatibilityMap() {
    	this.groups = new HashMap<>();
    	this.membership = new HashMap<>();
    }
    
    public void addGroup(String groupName, MethodGroup group) {
    	this.groups.put(groupName, group);
    }
    
    public void addMethod(String methodName, MethodGroup group) {
    	this.membership.put(methodName, group);
    }
    
    public Map<String, MethodGroup> getGroups() {
    	return this.groups;
    }
    
    public Map<String, MethodGroup> getMembership() {
    	return this.membership;
    }
}
