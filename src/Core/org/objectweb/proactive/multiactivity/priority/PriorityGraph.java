package org.objectweb.proactive.multiactivity.priority;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;

public class PriorityGraph implements PriorityStructure {

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
				if (this.contains(group)) {
					PriorityNode groupNode = this.findNode(group);
					predecessorNode.addSuccessor(groupNode);
				}
				else {
					predecessorNode.addSuccessor(group);
				}
				for (PriorityNode node : predecessorNode.successors) {
					System.out.println("Successor of " + predecessorGroup.name + ": " + node.group.name);
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
			if (group.equals(root.group)) {
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
			if (contains) {
				break;
			}
		}
		return contains;
	}

	private boolean recursiveContains(MethodGroup group, PriorityNode currentNode) {
		boolean contains = false;
		if (group.equals(currentNode.group)) {
			contains = true;
		}
		else {
			if (!currentNode.hasSuccessors()) {
				contains = false;
			}
			else {
				for (PriorityNode node : currentNode.successors) {
					contains = recursiveContains(group, node);
					if (contains) {
						break;
					}
				}
			}
		}
		return contains;
	}

	private PriorityNode findNode(MethodGroup group) {
		PriorityNode node = null;
		for (PriorityNode root : this.roots) {
			node = this.recursiveFindNode(group, root);
			if (node != null) {
				break;
			}
		}
		return node;
	}

	private PriorityNode recursiveFindNode(MethodGroup group, PriorityNode currentNode) {
		PriorityNode node = null;
		if (group.equals(currentNode.group)) {
			node = currentNode;
		}
		else {
			if (!currentNode.hasSuccessors()) {
				node = null;
			}
			else {
				for (PriorityNode pn : currentNode.successors) {
					node = recursiveFindNode(group, pn);
					if (node != null) {
						break;
					}
				}
			}
		}
		return node;
	}

	@Override
	public String toString() {
		int level = 0;
		String description = "";
		for (PriorityNode root : this.roots) {
			description += this.recursiveToString(root, level);
		}
		return description ;
	}

	private String recursiveToString(PriorityNode currentNode, int level) {
		String description = "";
		for (int i = 0 ; i < level ; i++) {
			description += "\t";
		}
		// TOREMOVE - USELESS
		if (!currentNode.hasSuccessors()) {
			description += currentNode.group.name + "(" +  ")" + "\n";
		}
		// TOREMOVE
		else {
			description += currentNode.group.name + "(" +  ")" + "\n";
			for (PriorityNode pn : currentNode.successors) {
				description += recursiveToString(pn, level + 1);
			}
		}
		return description;
	}

	@Override
	public PriorityOvertakeState canOvertake(MethodGroup group1,
			MethodGroup group2) {
		PriorityOvertakeState canOvertake = PriorityOvertakeState.UNRELATED;
		for (PriorityNode root : this.roots) {
			canOvertake = PriorityOvertakeState.and(canOvertake, this.recursiveCanOvertake(group1, group2, root, false, false));
		}
		return canOvertake;
	}

	private PriorityOvertakeState recursiveCanOvertake(MethodGroup group1,
			MethodGroup group2, PriorityNode currentNode, boolean g1Found, boolean g2Found) {
		PriorityOvertakeState canOvertake = PriorityOvertakeState.UNRELATED;
		if (!group1.equals(group2)) {
			if (group1.equals(currentNode.group)) {
				if (g2Found) {
					canOvertake = PriorityOvertakeState.FALSE;
				}
				g1Found = true;
			}
			if (group2.equals(currentNode.group)) {
				if (g1Found) {
					canOvertake = PriorityOvertakeState.TRUE;
				}
				g2Found = true;
			}
			for (PriorityNode pn : currentNode.successors) {
				canOvertake = PriorityOvertakeState.and(canOvertake, recursiveCanOvertake(group1, group2, pn, g1Found, g2Found));
			}
		}
		return canOvertake;
	}

	/**
	 * Represents a node in the graph of group considering the methods in it.
	 * @author jrochas
	 *
	 */
	private class PriorityNode {

		public final MethodGroup group;
		public Set<PriorityNode> successors;

		public PriorityNode(MethodGroup group) {
			this.successors = new HashSet<PriorityNode>();
			this.group = group;
		}
		
		public void addSuccessor(PriorityNode groupNode) {
			this.successors.add(groupNode);
		}

		public void addSuccessor(MethodGroup group) {
			this.successors.add(new PriorityNode(group));
		}

		public boolean hasSuccessors() {
			if (successors.isEmpty()) {
				return false;
			}
			else {
				return true;
			}
		}
	}

	public static void main(String[] args) {
		PriorityGraph graph = new PriorityGraph();
		MethodGroup g1 = new MethodGroup("m1", true);
		MethodGroup g2 = new MethodGroup("m2", true);
		MethodGroup g3 = new MethodGroup("m3", true);
		MethodGroup g4 = new MethodGroup("m4", true);
		MethodGroup g5 = new MethodGroup("m5", true);

		graph.insert(g1, null);
		System.out.println("m1 inserted");
		System.out.println(graph);

		graph.insert(g2, g1);
		System.out.println("m2 inserted");
		System.out.println(graph);

		graph.insert(g3, g2);
		System.out.println("m3 inserted");
		System.out.println(graph);

		graph.insert(g4, g3);
		System.out.println("m4 inserted");
		System.out.println(graph);

		graph.insert(g2, null);
		System.out.println("m2 inserted");
		System.out.println(graph);

		graph.insert(g5, g2);
		System.out.println("m5 inserted");
		System.out.println(graph);

		graph.insert(g4, g5);
		System.out.println("m4 inserted");
		System.out.println(graph);

		System.out.println("Can g4 overtake g3 (no)? " + graph.canOvertake(g4, g3));
		System.out.println("Can g3 overtake g4 (yes)? " + graph.canOvertake(g3, g4));
		System.out.println("Can g2 overtake g1 (no)? " + graph.canOvertake(g2, g1));
		System.out.println("Can g1 overtake g2 (yes)? " + graph.canOvertake(g1, g2));
		System.out.println("Can g3 overtake m5 (unrelated)? " + graph.canOvertake(g3, g5));
	}

}
