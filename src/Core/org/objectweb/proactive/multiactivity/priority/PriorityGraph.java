package org.objectweb.proactive.multiactivity.priority;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;

public class PriorityGraph {
	
	private Set<PriorityNode> roots;
	
	public PriorityGraph() {
		this.roots = new HashSet<PriorityNode>();
	}
	
	public void insert(MethodGroup group, MethodGroup predecessorGroup) {
		if (!this.contains(group) && predecessorGroup == null) {
			this.addRoot(group);
		}
		else {
			if (predecessorGroup != null) {
				PriorityNode predecessorNode = this.findNode(predecessorGroup);
				predecessorNode.addSuccessor(group);
				for (PriorityNode node : predecessorNode.getSuccessors()) {
					System.out.println("Successor of " + predecessorGroup.name + ": " + node.getGroup().name);
				}
				if (this.isRoot(group)) {
					this.removeRoot(group);
					System.out.println("Root removed for group: " + group.name);
				}
			}
		}
	}
	
	private void addRoot(MethodGroup group) {
		this.roots.add(new PriorityNode(group));
	}
	
	private void removeRoot(MethodGroup group) {
		this.roots.remove(group);
	}
	
	private boolean isRoot(MethodGroup group) {
		boolean isRoot = false;
		for (PriorityNode root : this.roots) {
			if (group.equals(root.getGroup())) {
				isRoot = true;
				break;
			}
		}
		return isRoot;
	}	
	
	private boolean contains(MethodGroup group) {
		boolean contains = false;
		for (PriorityNode root : this.roots) {
			contains = this.recursiveContains(group, root);
		}
		return contains;
	}
	
	private boolean recursiveContains(MethodGroup group, PriorityNode currentNode) {
		boolean contains = false;
		if (group.equals(currentNode.getGroup())) {
			contains = true;
		}
		else {
			if (!currentNode.hasSuccessors()) {
				contains = false;
			}
			else {
				for (PriorityNode node : currentNode.getSuccessors()) {
					contains = contains || recursiveContains(group, node);
				}
			}
		}
		return contains;
	}
	
	private PriorityNode findNode(MethodGroup group) {
		PriorityNode node = null;
		for (PriorityNode root : this.roots) {
			node = this.recursiveFindNode(group, root);
		}
		return node;
	}
	
	private PriorityNode recursiveFindNode(MethodGroup group, PriorityNode currentNode) {
		PriorityNode node = null;
		if (group.equals(currentNode.getGroup())) {
			node = currentNode;
		}
		else {
			if (!currentNode.hasSuccessors()) {
				node = null;
			}
			else {
				for (PriorityNode pn : currentNode.getSuccessors()) {
					node = recursiveFindNode(group, pn);
				}
			}
		}
		return node;
	}
	
	/**
	 * Represents a node in the graph of group considering the methods in it.
	 * @author jrochas
	 *
	 */
	private class PriorityNode {
		 
		private MethodGroup group;
		private Set<PriorityNode> successors;
		
		public PriorityNode(MethodGroup group) {
			successors = new HashSet<PriorityNode>();
			this.group = group;
		}
		
		public MethodGroup getGroup() {
			return this.group;
		}
		
		public Set<PriorityNode> getSuccessors() {
			return this.successors;
		}
		
		public boolean hasSuccessors() {
			return !this.successors.isEmpty();
		}
		
		public void addSuccessor(MethodGroup group) {
			this.successors.add(new PriorityNode(group));
		}
	}
	
	public static void main(String[] args) {
		PriorityGraph graph = new PriorityGraph();
		MethodGroup m1 = new MethodGroup("m1", true);
		MethodGroup m2 = new MethodGroup("m2", true);
		MethodGroup m3 = new MethodGroup("m3", true);
		MethodGroup m4 = new MethodGroup("m4", true);
		MethodGroup m5 = new MethodGroup("m5", true);
		graph.insert(m1, null);
		System.out.println("m1 inserted");
		graph.insert(m2, m1);
		System.out.println("m2 inserted");
		graph.insert(m3, m2);
		System.out.println("m3 inserted");
		graph.insert(m4, m3);
		System.out.println("m4 inserted");
		graph.insert(m2, null);
		System.out.println("m2 inserted");
		graph.insert(m5, m2);
		System.out.println("m5 inserted");
		graph.insert(m4, m5);
		System.out.println("m4 inserted");
	}

}
