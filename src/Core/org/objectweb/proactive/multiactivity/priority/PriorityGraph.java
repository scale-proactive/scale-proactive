package org.objectweb.proactive.multiactivity.priority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
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
public class PriorityGraph implements PriorityMap {

	/**
	 * The roots of the graph, or the highest priority groups. They are the 
	 * only entry point of the graph. 
	 */
	private Set<PriorityNode> roots;

	private HashMap<String, HashMap<String, Boolean>> existPathMatrix;
	private boolean matrixEnabled = false;

	private ArrayList<PriorityNode> nodesList;

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
					predecessorNode.addSuccessor(new PriorityNode(group));
				}
				if (this.isRoot(group) && newRoot) {
					this.removeRoot(group);
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
	 * Search for a particular node in the graph.
	 * @param group The searched group
	 * @return The node corresponding to the group in the graph or null if the 
	 * group does not exist in the graph
	 */
	public MethodGroup findGroup(PriorityNode node) {
		MethodGroup group = null;
		for (PriorityNode root : this.roots) {
			group = this.recursiveFindGroup(node, root);
			if (group != null) {
				break;
			}
		}
		return group;
	}

	/**
	 * Utility method for the findNode method.
	 * @param group
	 * @param currentNode
	 * @return
	 */
	private MethodGroup recursiveFindGroup(PriorityNode node, PriorityNode currentNode) {
		MethodGroup group = null;
		if (node.equals(currentNode)) {
			group = currentNode.group;
		}
		else {
			if (!currentNode.hasSuccessors()) {
				group = null;
			}
			else {
				for (PriorityNode pn : currentNode.successors) {
					group = recursiveFindGroup(node, pn);
					if (group != null) {
						break;
					}
				}
			}
		}
		return group;
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

	private ArrayList<PriorityNode> listNodes() {
		ArrayList<PriorityNode> list = new ArrayList<PriorityNode>();
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
	private void recursiveListNodes(ArrayList<PriorityNode> visitedNodes, PriorityNode currentNode) {
		if (!visitedNodes.contains(currentNode)) {
			visitedNodes.add(currentNode);
		}
		for (PriorityNode pn : currentNode.successors) {
			recursiveListNodes(visitedNodes, pn);
		}
	}

	private boolean existPath(MethodGroup group1,
			MethodGroup group2) {
		boolean exist = false;
		PriorityNode node = null;
		for (PriorityNode root : this.roots) {
			node = this.recursiveFindNode(group1, root);
			if (node != null) {
				break;
			}
		}
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
	public boolean canOvertake(MethodGroup group1,
			MethodGroup group2) {
		if (this.matrixEnabled) {
			Boolean returnValue = false;
			if (this.nodesList == null) {
				this.nodesList = this.listNodes();
				for (PriorityNode node : this.nodesList) {
					PriorityUtils.logMessage(node.group.name + " ");
				}
			}
			if (this.existPathMatrix == null) {
				int size = this.nodesList.size();
				this.existPathMatrix = new HashMap<String, HashMap<String, Boolean>>(size);
				for (int i = 0 ; i < size; i++) {
					PriorityNode pni = this.nodesList.get(i);
					HashMap<String, Boolean> map = new HashMap<String, Boolean>(size);
					for (int j = 0 ; j < size; j++) {
						PriorityNode pnj = this.nodesList.get(j);	
						map.put(this.nodesList.get(j).group.name, this.existPath(pni.group, pnj.group));						
					}
					this.existPathMatrix.put(this.nodesList.get(i).group.name, map);
				}
				StringBuilder sb = new StringBuilder();
				for (Entry<String, HashMap<String,Boolean>> entry :this.existPathMatrix.entrySet()) {
					sb.append("** " + entry.getKey() + ": ");
					for (Entry<String, Boolean> entry2 : entry.getValue().entrySet()) {
						sb.append(entry2.getKey() + ":" + entry2.getValue() + " ");
					}
					sb.append("\n");
				}
				PriorityUtils.logMessage(sb.toString());
			}
			HashMap<String, Boolean> intermediateMatrix = this.existPathMatrix.get(group1.name);
			if (intermediateMatrix != null) {
				returnValue = intermediateMatrix.get(group2.name);
				if (returnValue != null) {
					return returnValue;
				}
				else { 
					return false;
				}
			}
			else {
				return false;
			}
		}
		else {
			return this.existPath(group1, group2);
		}
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

		// Checking node listing functionality
		ArrayList<PriorityNode> list = graph.listNodes();
		for (PriorityNode node : list) {
			System.out.println(node.group.name);
		}
	}

}
