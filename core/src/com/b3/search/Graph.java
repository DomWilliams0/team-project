package com.b3.search;

import com.b3.util.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A graph with nodes and edges with costs
 *
 * @author dxw405 bxd428
 */
public class Graph {

	private final Map<Point, Node> nodes;
	private final int width;
	private final int height;

	/**
	 * Constructs a new graph with the given x and y dimensions.
	 * The new graph has all successors, no missing edges nor non-default edge costs
	 *
	 * @param width  The width of the graph
	 * @param height The height of the graph
	 */
	public Graph(int width, int height) {
		this.nodes = new LinkedHashMap<>();
		this.width = width;
		this.height = height;

		generateEmptyGraph(width, height);
	}


	/**
	 * Adds a new node to the graph
	 *
	 * @param p The node's tile position
	 * @return The new node. If it exists then simply return it.
	 */
	public Node addNode(Point p) {
		Node node;

		if (!nodes.containsKey(p)) {
			node = new Node(p);
			nodes.put(p, node);
		} else
			node = nodes.get(p);

		return node;
	}

	/**
	 * Adds an edge between 2 nodes
	 *
	 * @param p1   The content of the first node (source)
	 * @param p2   The content of the second node (destination)
	 * @param cost The edge cost
	 */
	public void addEdge(Point p1, Point p2, float cost) {
		// Add nodes (if not existing) and get nodes from adjacency list
		Node node1 = addNode(p1);
		Node node2 = addNode(p2);

		// create edge
		node1.addNeighbour(node2, cost);
		node2.addNeighbour(node1, cost);

		// Update adjacency list
		nodes.put(p1, node1);
		nodes.put(p2, node2);
	}

	/**
	 * Generates a graph with x and y dimensions as
	 * specified in parameters.
	 * <p>
	 * Adds every possible successor to the edges.
	 *
	 * @param xMax max x value (coordinate's x value will go up to xMax-1) IE if you pass 5, it will go from 0 to 4
	 * @param yMax max y value (coordinate's y value will go up to yMax-1) IE if you pass 5, it will go from 0 to 4
	 */
	public void generateEmptyGraph(int xMax, int yMax) {
		ArrayList<Point> points = new ArrayList<>();
		for (int x = 0; x < xMax; x++) {
			for (int y = 0; y < yMax; y++) {
				points.add(new Point(x, y));
			}
		}
		for (Point p1 : points) {
			for (Point p2 : points) {
				int xDiff = Math.abs(p1.getX() - p2.getX());
				int yDiff = Math.abs(p1.getY() - p2.getY());
				// Check only one of the coords is off by one.
				if ((xDiff == 1 && yDiff == 0) || (xDiff == 0 && yDiff == 1)) {
					addEdge(p1, p2, 1);
				}
			}
		}
	}

	/**
	 * Tells whether the graph has a specific edge
	 *
	 * @param p1 The first Point
	 * @param p2 The second Point
	 * @return True if the graph has a p1 -- p2 edge, false otherwise
	 */
	protected boolean hasEdge(Point p1, Point p2) {
		Node n1 = nodes.get(p1);
		Node n2 = nodes.get(p2);

		return !(n1 == null || n2 == null) && n1.hasNeighbour(n2);
	}

	/**
	 * Check whether there is node c in the table of nodes
	 *
	 * @param p The content of the node to search
	 * @return True is the node exists, false otherwise
	 */
	public boolean hasNode(Point p) {
		return nodes.containsKey(p);
	}

	/**
	 * Removes an edge (undirected or directed) between two nodes if it exists
	 *
	 * @param p1 The content of the first node
	 * @param p2 The content of the second node
	 * @return True if the edge existed and has been removed, false otherwise
	 */
	protected boolean removeEdge(Point p1, Point p2) {
		if (!hasNode(p1) || !hasNode(p2))
			return false;

		Node node1 = nodes.get(p1);
		Node node2 = nodes.get(p2);

		boolean removed1 = node1.removeNeighbours(node2);
		boolean removed2 = node2.removeNeighbours(node1);

		return removed1 || removed2;
	}

	/**
	 * Removes the given node from the graph, snipping all connected edges
	 *
	 * @param node The node to remove
	 */
	public void removeNode(Node node) {
		node.clearNeighbours();
		nodes.remove(node.getPoint());
	}

	/**
	 * Removes the node at the given point, if it exists
	 *
	 * @param point The position of the node to remove
	 * @return True if the node existed and has been removed, otherwise false
	 * @see {@link Graph#removeNode(Node)}
	 */
	public boolean removeNode(Point point) {
		Node node = getNode(point);
		if (node == null)
			return false;

		removeNode(node);
		return true;
	}

	/**
	 * @return all the nodes on the graph as a linkedHashSet / Map<Point, Node>
	 */
	public Map<Point, Node> getNodes() {
		return nodes;
	}

	/**
	 * @param point The key
	 * @return The node associated with the given key
	 */
	public Node getNode(Point point) {
		return nodes.get(point);
	}

	/**
	 * @return The graph's width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return The graph's height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Snips all the edges in the given range
	 *
	 * @param baseX lower X bound
	 * @param upToX upper X bound
	 * @param baseY lower Y bound
	 * @param upToY upper Y bound
	 */
	public void snipEdges(int baseX, int upToX, int baseY, int upToY) {
		for (int x = baseX; x < baseX + upToX; x++) {
			for (int y = baseY; y < baseY + upToY; y++) {
				Node node = nodes.get(new Point(x, y));
				if (node != null)
					removeNode(node);
			}
		}
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (Map.Entry<Point, Node> entry : nodes.entrySet()) {
			sb.append(entry.getKey())
					.append(" -- ")
					.append(entry.getValue().getEdges().keySet())
					.append("\n");
		}

		return sb.toString();
	}

	/**
	 * Adds nodes in all spaces in the given range
	 *
	 * @param x    The start X coordinate
	 * @param y    The start Y coordinate
	 * @param size The width and height of the area to fill with nodes
	 */
	public void addNodesInSquare(int x, int y, int size) {

		// add nodes
		for (int i = x; i < x + size; i++)
			for (int j = y; j < y + size; j++)
				addNode(new Point(i, j));

		// add edges
		for (int i = x; i < x + size; i++) {
			for (int j = y; j < y + size; j++) {
				Point currentPoint = new Point(i, j);
				if (hasNode(new Point(i + 1, j)))
					addEdge(currentPoint, new Point(i + 1, j), 1);
				if (hasNode(new Point(i - 1, j)))
					addEdge(currentPoint, new Point(i - 1, j), 1);
				if (hasNode(new Point(i, j + 1)))
					addEdge(currentPoint, new Point(i, j + 1), 1);
				if (hasNode(new Point(i, j - 1)))
					addEdge(currentPoint, new Point(i, j - 1), 1);
			}
		}
	}

	/**
	 * @return A random {@link Node} from the graph
	 */
	public Node getRandomNode() {
		Collection<Node> nodes = getNodes().values();
		int index = Utils.RANDOM.nextInt(nodes.size());
		for (Node node : nodes)
			if (index-- == 0)
				return node;

		return null;
	}

	/**
	 * @param except The node to not generate
	 * @return A random {@link Node} from the graph that isn't <code>except</code>
	 */
	public Node getRandomNode(Node except) {
		Node node;
		do {
			node = getRandomNode();
		} while (node.equals(except));

		return node;
	}

}
