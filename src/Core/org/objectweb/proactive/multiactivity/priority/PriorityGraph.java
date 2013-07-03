package org.objectweb.proactive.multiactivity.priority;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;

/**
 * This class internally represents the priorities using a dependency graph. 
 * This graph is made of one or several roots that can have successors. The 
 * roots have the highest priority level. This dependency graph is built when 
 * the annotations are processed.
 * 
 * @author jrochas
 */
public class PriorityGraph implements PriorityStructure {

	/**
	 * The roots of the graph, or the highest priority groups. They are the 
	 * only entry point of the graph. 
	 */
	private Set<PriorityNode> roots;

	public PriorityGraph() {
		this.roots = new HashSet<PriorityNode>();
	}

	/**
	 * Inserts a new node in the graph according to its parent group. A parent 
	 * group is a group that has a higher priority.
	 * @param group The group to be inserted
	 * @param predecessorGroup The parent group of the group to be inserted
	 */
	public void insert(MethodGroup group, MethodGroup predecessorGroup) {
		if (!this.contains(group) && predecessorGroup == null) {
			this.addRoot(new PriorityNode(group));
		}
		else {
			if (predecessorGroup != null) {
				boolean newRoot = false;
				PriorityNode predecessorNode = this.findNode(predecessorGroup);
				if (predecessorNode == null) {
					predecessorNode = new PriorityNode(predecessorGroup);
					this.addRoot(predecessorNode);
					newRoot = true;
				}
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
				if (this.isRoot(group) && newRoot) {
					this.removeRoot(group);
					System.out.println("Root removed for group: " + group.name);
				}
			}
		}
	}

	/**
	 * Adds a root in the graph. A root is a group that have no parent group, 
	 * that is, has the highest priority.
	 * @param node
	 */
	private void addRoot(PriorityNode node) {
		this.roots.add(node);
	}
	
	/**
	 * Removes a root from the graph. Warning: removing the root does not 
	 * remove the node from the graph if it is referenced by another node 
	 * (= if it has a parent group)
	 */
	private void removeRoot(MethodGroup group) {
		PriorityNode nodeToRemove = this.findNode(group);
		if (nodeToRemove != null) {
			this.roots.remove(nodeToRemove);
		}
	}

	/**
	 * @param group
	 * @return true if the given group belongs to the roots of the graph 
	 * (according to the equals method).
	 */
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

	/**
	 * @param group
	 * @return true if the given group belongs to the graph (according to the 
	 * equals method).
	 */
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

	/**
	 * Utility method for the contains method.
	 * @param group
	 * @param currentNode
	 * @return
	 */
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

	/**
	 * Search for a particular node in the graph.
	 * @param group The searched group
	 * @return The node corresponding to the group in the graph or null if the 
	 * group does not exist in the graph
	 */
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

	/**
	 * Utility method for the findNode method.
	 * @param group
	 * @param currentNode
	 * @return
	 */
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

	/**
	 * Search for cycles in the graph.
	 * @return true if at least one cycle is found in the graph.
	 */
	public boolean containsCycle() {
		boolean contains = false;
		ArrayList<PriorityNode> visitedNodes = new ArrayList<PriorityNode>();
		for (PriorityNode root : this.roots) {
			contains = this.recursiveContainsCycle(visitedNodes, root);
			if (contains) {
				break;
			}
			visitedNodes.clear();
		}
		return contains;
	}

	/**
	 * Utility method for the containsCycle method.
	 * @param visitedNodes
	 * @param currentNode
	 * @return
	 */
	private boolean recursiveContainsCycle(ArrayList<PriorityNode> visitedNodes, PriorityNode currentNode) {
		boolean contains = false;
		if (visitedNodes.contains(currentNode)) {
			contains = true;
		}
		else {
			visitedNodes.add(currentNode);
			for (PriorityNode pn : currentNode.successors) {
				contains = recursiveContainsCycle(visitedNodes, pn);
				if (contains) {
					break;
				}
				visitedNodes.clear();
			}
		}
		return contains;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		int level = 0;
		String description = "";
		for (PriorityNode root : this.roots) {
			description += this.recursiveToString(root, level);
		}
		return description ;
	}

	/**
	 * Utility method for the toString method.
	 * @param currentNode
	 * @param level
	 * @return
	 */
	private String recursiveToString(PriorityNode currentNode, int level) {
		String description = "";
		for (int i = 0 ; i < level ; i++) {
			description += "\t";
		}
		description += currentNode.group.name + "(" +  ")" + "\n";
		for (PriorityNode pn : currentNode.successors) {
			description += recursiveToString(pn, level + 1);
		}
		return description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PriorityOvertakeState canOvertake(MethodGroup group1,
			MethodGroup group2) {
		PriorityOvertakeState canOvertake = PriorityOvertakeState.UNRELATED;
		for (PriorityNode root : this.roots) {
			canOvertake = PriorityOvertakeState.and(canOvertake, this.recursiveCanOvertake(group1, group2, root, false, false));
		}
		return canOvertake;
	}

	/**
	 * Utility method for the canOvertake method.
	 * @param group1
	 * @param group2
	 * @param currentNode
	 * @param g1Found
	 * @param g2Found
	 * @return
	 */
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
				canOvertake = PriorityOvertakeState.and(canOvertake, 
						recursiveCanOvertake(group1, group2, pn, g1Found, 
								g2Found));
			}
		}
		return canOvertake;
	}

	/**
	 * Represents a node in the PriorityGraph.
	 * A node is defined by the group that it contains and by a list of 
	 * successors nodes.
	 * @author jrochas
	 */
	private class PriorityNode {

		/** The group contained in the node */
		public final MethodGroup group;

		/** The successors of this node (successors have lower priority than 
		 * the considered node) */
		public Set<PriorityNode> successors;

		public PriorityNode(MethodGroup group) {
			this.successors = new HashSet<PriorityNode>();
			this.group = group;
		}

		/**
		 * Add a successor node to the considered node. The successor node has 
		 * then a lower priority than the considered node.
		 * @param groupNode The node to add as successor
		 */
		public void addSuccessor(PriorityNode groupNode) {
			this.successors.add(groupNode);
		}

		/**
		 * Create a new PriorityNode from a MethodGroup and then add it to the 
		 * successors of the considered node. The successor node has then a 
		 * lower priority than the considered node.
		 * @param group The group to add as a successor
		 */
		public void addSuccessor(MethodGroup group) {
			this.successors.add(new PriorityNode(group));
		}

		/**
		 * If false, it means that no other node has a lower priority than the 
		 * considered node, but other nodes can be unrelated to it.
		 * @return true if the considered node has successor nodes, false 
		 * otherwise
		 */
		public boolean hasSuccessors() {
			if (successors.isEmpty()) {
				return false;
			}
			else {
				return true;
			}
		}

	}

	/**
	 * Quick test to debug the graph operations.
	 * Creates a graph like:
	 *               			    m3 m4
	 *  highest priority - m0 m1 m2       - lowest priority
	 *			 				    m5
	 * Checks the absence of cycles at each insertion, and then checks the 
	 * possibilities to overtake.
	 * @param args Unused
	 */
	public static void main(String[] args) {

		PriorityGraph graph = new PriorityGraph();

		MethodGroup g0 = new MethodGroup("G0", true);
		MethodGroup g1 = new MethodGroup("G1", true);
		MethodGroup g2 = new MethodGroup("G2", true);
		MethodGroup g3 = new MethodGroup("G3", true);
		MethodGroup g4 = new MethodGroup("G4", true);
		MethodGroup g5 = new MethodGroup("G5", true);

		// G1
		graph.insert(g1, null);
		System.out.println("- " + g1.name + " inserted");
		System.out.println("- Contains cycle? " +
						"" + graph.containsCycle());
		System.out.println("- Graph:\n" + graph);

		graph.insert(g2, g1);
		System.out.println("- " + g2.name + " inserted");
		System.out.println("- Contains cycle? " +
						"" + graph.containsCycle());
		System.out.println("- Graph:\n" + graph);

		graph.insert(g3, g2);
		System.out.println("- " + g3.name + " inserted");
		System.out.println("Contains cycle? " +
						"" + graph.containsCycle());
		System.out.println("- Graph:\n" + graph);

		graph.insert(g4, g3);
		System.out.println(" " + g4.name + " inserted");
		System.out.println("- Contains cycle? " +
						"" + graph.containsCycle());
		System.out.println("- Graph:\n" + graph);

		graph.insert(g2, null);
		System.out.println("- " + g2.name + "  inserted");
		System.out.println("- Contains cycle? " +
						"" + graph.containsCycle());
		System.out.println("- Graph:\n" + graph);

		graph.insert(g5, g2);
		System.out.println("- " + g5.name + " inserted");
		System.out.println("Contains cycle? " +
						"" + graph.containsCycle());
		System.out.println("- Graph:\n" + graph);

		graph.insert(g4, g5);
		System.out.println("- " + g4.name + " inserted");
		System.out.println("- Contains cycle? " +
						"" + graph.containsCycle());
		System.out.println("- Graph:\n" + graph);
		
		// Creation of predecessor test
		graph.insert(g1, g0);
		System.out.println("- " + g1.name + " inserted");
		System.out.println("- Contains cycle? " +
						"" + graph.containsCycle());
		System.out.println("- Graph:\n" + graph);
		
		// This should introduce a cycle in the graph
		//graph.insert(g1, g4);
		//System.out.println("m4 inserted");
		//System.out.println("Contains cycle? " +
		//				"" + graph.containsCycle());
		//System.out.println("- Graph:\n" + graph);

		// Overtake tests
		System.out.println("- Can g4 overtake g3 (no)? " +
				"" + graph.canOvertake(g4, g3));
		System.out.println("- Can g3 overtake g4 (yes)? " +
				"" + graph.canOvertake(g3, g4));
		System.out.println("- Can g2 overtake g1 (no)? " +
				"" + graph.canOvertake(g2, g1));
		System.out.println("- Can g1 overtake g2 (yes)? " +
				"" + graph.canOvertake(g1, g2));
		System.out.println("- Can g3 overtake m5 (not related)? " +
				"" + graph.canOvertake(g3, g5));
	}

}
