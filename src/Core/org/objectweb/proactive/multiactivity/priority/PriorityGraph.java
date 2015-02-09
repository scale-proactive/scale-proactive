package org.objectweb.proactive.multiactivity.priority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.objectweb.proactive.multiactivity.compatibility.MethodGroup;

/**
 * This class internally represents the priorities using a dependency graph. 
 * This graph is made of one (= connected graph) or several (= disconnected 
 * graph) roots that have successors. The roots have the highest priority 
 * level. This dependency graph is built when the annotations are processed.
 * 
 * @author The ProActive Team
 */
public class PriorityGraph implements PriorityMap {

	/** The roots of the graph, or the highest priority groups. They are the 
	 * only entry point of the graph. */
	private Set<PriorityNode> roots;

	/** Stores the nodes of the graph = the groups that have a priority */
	private Set<PriorityNode> nodesList;

	/** Matrix representation of the graph. This matrix is built by computing 
	 * the transitive closure of the initial graph. The values of the matrix 
	 * is true if the first entry has a higher priority than the second one, 
	 * it is false otherwise. This is an optimization to accelerate the access 
	 * to the priority information. It is much faster than exploring the graph 
	 * but building the matrix can be long. */
	private HashMap<String, HashMap<String, Boolean>> existPathMatrix;

	/** Specifies whether the matrix optimization should be used or not */
	private boolean matrixEnabled = true;


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
		// A new highest priority group is encountered
		if (!this.contains(group) && predecessorGroup == null) {
			this.addRoot(new PriorityNode(group));
		}
		else {
			// If the group is already in the graph and if predecessor is null,
			// there is nothing to do.
			if (predecessorGroup != null) {

				boolean newRoot = false;
				PriorityNode predecessorNode = this.findNode(predecessorGroup);

				// The predecessor group is not in the graph already, add it
				if (predecessorNode == null) {
					predecessorNode = new PriorityNode(predecessorGroup);
					this.addRoot(predecessorNode);
					newRoot = true;
				}
				// The group is already contained in the graph, just update its 
				// references
				if (this.contains(group)) {
					PriorityNode groupNode = this.findNode(group);
					predecessorNode.addSuccessor(groupNode);
				}

				// The group is not in the graph already
				else {
					predecessorNode.addSuccessor(new PriorityNode(group));
				}

				// If a predecessor is added to a root, then it is not a root 
				// any more
				if (this.isRoot(group) && newRoot) {
					this.removeRoot(group);
				}
			}
		}
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

	public void suppress(MethodGroup group, MethodGroup predecessorGroup) {
		PriorityNode groupNode = this.findNode(group);
		PriorityNode predecessorNode = this.findNode(predecessorGroup);
		if (groupNode != null && predecessorNode != null) {
			predecessorNode.removeSuccessor(groupNode);
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
	private boolean recursiveContains(MethodGroup group, 
			PriorityNode currentNode) {
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
	private PriorityNode recursiveFindNode(MethodGroup group, 
			PriorityNode currentNode) {
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
	 * Utility method for the containsCycle method.
	 * @param visitedNodes
	 * @param currentNode
	 * @return
	 */
	private boolean recursiveContainsCycle(ArrayList<PriorityNode> visitedNodes,
			PriorityNode currentNode) {
		boolean contains = false;
		@SuppressWarnings("unchecked")
		ArrayList<PriorityNode> visitedNodesCopy = (
				ArrayList<PriorityNode>) visitedNodes.clone();
		if (visitedNodesCopy.contains(currentNode)) {
			contains = true;
		}
		else {
			visitedNodesCopy.add(currentNode);
			for (PriorityNode pn : currentNode.successors) {
				contains = recursiveContainsCycle(visitedNodesCopy, pn);
				if (contains) {
					break;
				}
			}
		}
		return contains;
	}

	private Set<PriorityNode> listNodes() {
		Set<PriorityNode> list = new HashSet<PriorityNode>();
		for (PriorityNode root : this.roots) {
			this.recursiveListNodes(list, root);
		}
		return list;
	}

	/**
	 * Utility method for the containsCycle method.
	 * @param visitedNodes
	 * @param currentNode
	 * @return
	 */
	private void recursiveListNodes(Set<PriorityNode> visitedNodes, 
			PriorityNode currentNode) {
		if (!visitedNodes.contains(currentNode)) {
			visitedNodes.add(currentNode);
		}
		for (PriorityNode pn : currentNode.successors) {
			recursiveListNodes(visitedNodes, pn);
		}
	}

	/**
	 * @param group1
	 * @param group2
	 * @return true if there is a path from group1 to group2 in the graph, 
	 * according to the successors references.
	 */
	private boolean existPath(MethodGroup group1, MethodGroup group2) {

		boolean exist = false;
		PriorityNode node = null;

		// Find the first group
		for (PriorityNode root : this.roots) {
			node = this.recursiveFindNode(group1, root);
			if (node != null) {
				break;
			}
		}

		// See if the second group is accessible from the first one
		if (node != null) {
			for (PriorityNode succ : node.successors) {
				exist = this.recursiveContains(group2, succ);
				if (exist) {
					break;
				}
			}
		}
		return exist;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canOvertake(MethodGroup group1, MethodGroup group2) {	
		// group1 has a super priority, it can overtake group2 if group2 does 
		// not have a super priority
		if (group1.hasSuperPriority()) {
			return !group2.hasSuperPriority();
		}
		// We use the matrix optimization to know about priorities of groups
		if (this.matrixEnabled) {			
			Boolean returnValue = false;

			// If the node list is not yet built, build it
			if (this.nodesList == null) {
				this.nodesList = this.listNodes();
			}

			// If the matrix is not yet built, build it
			if (this.existPathMatrix == null) {

				int size = this.nodesList.size();
				this.existPathMatrix = 
						new HashMap<String, HashMap<String, Boolean>>(size);

				for (PriorityNode pni : this.nodesList) {
					HashMap<String, Boolean> map = 
							new HashMap<String, Boolean>(size);
					for (PriorityNode pnj : this.nodesList) {
						map.put(pnj.group.name, 
								this.existPath(pni.group, pnj.group));					
					}
					this.existPathMatrix.put(pni.group.name, map);
				}
			}

			// The matrix exists, now look for the entry we are interested in
			HashMap<String, Boolean> intermediateMatrix = 
					this.existPathMatrix.get(group1.name);

			if (intermediateMatrix != null) {
				returnValue = intermediateMatrix.get(group2.name);
				if (returnValue == null) {
					returnValue = false;
				}
			}
			return returnValue;

		}

		// We do not use the matrix optimization; look through the graph
		else {
			return this.existPath(group1, group2);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		int level = 0;
		String description = "\n";
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
	 * Represents a node in the PriorityGraph. A node is defined by the group 
	 * that it contains and by a list of successors nodes.
	 * 
	 * @author The ProActive Team
	 */
	private class PriorityNode {

		/** The group contained in the node */
		public final MethodGroup group;

		/** The successors of this node (successors have lower priority than 
		 * the considered node) */
		private Set<PriorityNode> successors;


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
		 * Remove a successor node to the considered node.
		 * @param groupNode
		 */
		public void removeSuccessor(PriorityNode groupNode) {
			this.successors.remove(groupNode);
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

		MethodGroup g0 = new MethodGroup("G0", true, false);
		MethodGroup g1 = new MethodGroup("G1", true, false);
		MethodGroup g2 = new MethodGroup("G2", true, false);
		MethodGroup g3 = new MethodGroup("G3", true, false);
		MethodGroup g4 = new MethodGroup("G4", true, false);
		MethodGroup g5 = new MethodGroup("G5", true, false);

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
		graph.insert(g1, g4);
		System.out.println("m4 inserted");
		System.out.println("- Contains cycle? " +
				"" + graph.containsCycle());
		if (graph.containsCycle()) {
			System.out.println("- Cycle detected; now removing dependency");
			graph.suppress(g1, g4);
		}
		System.out.println("- Graph:\n" + graph);

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

		// Checking node listing functionality
		Set<PriorityNode> list = graph.listNodes();
		for (PriorityNode node : list) {
			System.out.println(node.group.name);
		}
	}

}
