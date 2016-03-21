package com.b3.search;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Describes a node in a graph
 *
 * @author dxw405 nbg481
 */
public class Node implements Comparable<Node> {

	private final Point point;
	private final Map<Node, Float> edges;

	/**
	 * Creates a Node instance from the point
	 *
	 * @param point The related node's data
	 */
	public Node(Point point) {
		this.point = point;
		this.edges = new TreeMap<>();
	}

	@Override
	public String toString() {
		return "(" + point.getX() + "," + point.getY() + ")";
	}

	/**
	 * Give an alternative string representation of the node.
	 * Currently not a more desired representation and uses the <code>XN</code> format
	 * where <code>X</code> is the alphabetic character where A is 1, B is 2, etc of the X coordinate
	 * and <code>N</code> is the numeric value of the Y coordinate
	 *
	 * i.e. B3 is the coordinate (1,3)
	 *
	 * We prefer the standard coordinate representation in {@link #toString()}, so we do not currently use this.
	 *
	 * @return an alternative string representation of the node
	 */
	public String toAdaptedString() {
		return ((char) (getPoint().getX() + 65)) + Integer.toString(getPoint().getY());
	}

	/**
	 * @return The {@link Point} this Node is at.
	 */
	public Point getPoint() {
		return point;
	}

	/**
	 * @return A {@link Map} of the connected neighbors to the cost of the edge.
	 */
	public Map<Node, Float> getEdges() {
		return edges;
	}

	/**
	 * @return A {@link Set} of the connected neighbouring nodes.
	 */
	public Set<Node> getNeighbours() {
		return edges.keySet();
	}

	/**
	 * @param neighbour The connected neighbour to get the cost of the edge between this Node and it.
	 * @return The cost of the edge between the two Nodes.
	 * @throws IllegalArgumentException If the Node specified is not a connected neighbour.
	 */
	public float getEdgeCost(Node neighbour) {
		Float cost = edges.get(neighbour);
		if (cost == null)
			throw new IllegalArgumentException(String.format("Tried to get non-existent edge cost to neighbour %s for node %s", neighbour, this));
		return cost;
	}

	/**
	 * Sets the cost of the edge between this and the given neighbour,
	 * if it exists
	 *
	 * @param neighbour The neighbour who shares this edge
	 * @param cost      The new cost
	 * @return <code>true</code> if the neighbour is a valid neighbour and the
	 * operation was successful;
	 * <code>false</code> otherwise.
	 */
	public boolean setEdgeCost(Node neighbour, float cost) {
		if (!edges.containsKey(neighbour))
			return false;

		edges.put(neighbour, cost);
		neighbour.edges.put(this, cost);
		return true;
	}

	/**
	 * Creates an edge from this Node to another with a specified cost.
	 *
	 * @param key  The other Node to create an edge tp.
	 * @param cost The edge cost.
	 */
	public void addNeighbour(Node key, float cost) {
		edges.put(key, cost);
	}

	/**
	 * Checks if there is an edge from this Node to the one specified.
	 *
	 * @param node The neighbour to check if there is an edge to.
	 * @return <code>true</code> if there is an edge from this Node to another.
	 */
	public boolean hasNeighbour(Node node) {
		return edges.containsKey(node);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Node node = (Node) o;

		return point.equals(node.point);

	}

	@Override
	public int hashCode() {
		return point.hashCode();
	}

	/**
	 * Removes the edge from this Node to the one specified.
	 *
	 * @param node The Node to remove the edge to.
	 * @return <code>true</code> if the node was a neighbour;
	 * <code>false</code> otherwise.
	 */
	public boolean removeNeighbours(Node node) {
		return edges.remove(node) != null;
	}

	/**
	 * Removes all the edges between this Node and its neighbours.
	 * Will remove them bidirectionally.
	 */
	public void clearNeighbours() {
		for (Node neighbour : getNeighbours())
			neighbour.removeNeighbours(this);
		edges.clear();
	}

	@Override
	public int compareTo(Node n) {
		return point.compareTo(n.point);
	}
	
}

