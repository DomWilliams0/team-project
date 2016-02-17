package com.b3.search;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Describes a node in a graph
 */
public class Node implements Serializable {

	private Point point;
	private Map<Node, Float> edges;

	/**
	 * Creates a Node instance from the point
	 *
	 * @param point The related node's data
	 */
	public Node(Point point) {
		this.point = point;
		this.edges = new HashMap<>();
	}

	@Override
	public String toString() {
		return "(" + point.getX() + "," + point.getY() + ")";
	}


	public Point getPoint() {
		return point;
	}

	public Map<Node, Float> getEdges() {
		return edges;
	}

	public Set<Node> getNeighbours() {
		return edges.keySet();
	}

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
	 * @return True if the neighbour is a valid neighbour and the
	 * operation was successful, otherwise false
	 */
	public boolean setEdgeCost(Node neighbour, float cost) {
		if (!edges.containsKey(neighbour))
			return false;

		edges.put(neighbour, cost);
		neighbour.edges.put(this, cost);
		return true;
	}

	public void addNeighbour(Node key, float cost) {
		edges.put(key, cost);
	}

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

	public boolean removeNeighbours(Node node) {
		return edges.remove(node) != null;
	}

	public void clearNeighbours() {
		for (Node neighbour : getNeighbours())
			neighbour.removeNeighbours(this);
		edges.clear();
	}
}

