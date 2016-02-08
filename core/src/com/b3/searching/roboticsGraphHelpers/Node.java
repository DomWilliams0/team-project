package com.b3.searching.roboticsGraphHelpers;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Describes a node in a graph
 */
public class Node<A> implements Comparable<Node<A>> {
	private A content;
	private float f; // estimated total cost
	private Set<Node<A>> successors;
	private int extraCost;

	/**
	 * Creates a Node instance from the content
	 * @param content The related node's data
	 */
	public Node(A content) {
		this.content = content;
		this.successors = new LinkedHashSet<Node<A>>();
		this.extraCost = 0;
	}



	/**
	 * Gets the content of the node
	 * @return The data
	 */
	public A getContent() {
		return content;
	}
	
	/**
	 * Sets a new content
	 * @param content
	 */
	public void setContent(A content) {
		this.content = content;
	}
	
	/**
	 * @return the f
	 */
	public float getF() {
		return f;
	}

	/**
	 * @param f the f to set
	 */
	public void setF(float f) {
		this.f = f;
	}
	
	/**
	 * Returns the set of successors
	 * @return The set of neighbours
	 */
	public Set<Node<A>> getSuccessors() {
		return successors;
	}
	
	/**
	 * Adds a new successor/neighbour to the current set of successors
	 * @param succ The new successor
	 */
	public void addSuccessor(Node<A> succ) {
		successors.add(succ);
	}
	
	/**
	 * Removes a specified successor
	 * @param succ The successor to remove
	 * @return True if the successor exists, false otherwise
	 */
	public boolean removeSuccessor(Node<A> succ) {
		return successors.remove(succ);
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj != null && content.equals(((Node<?>) obj).getContent());
	}
	
	@Override
	public String toString() {
		return content.toString();
	}

	@Override
	public int compareTo(Node<A> o) {
		return (int)(o.getF() - getF()); // Because we want the min when popping
	}

	public int getExtraCost() {
		return extraCost;
	}

	public void setExtraCost(int extraCost) {
		this.extraCost = extraCost;
	}
}