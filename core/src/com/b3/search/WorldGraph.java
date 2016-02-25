package com.b3.search;

import com.b3.entity.Agent;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.util.Utils;
import com.b3.world.Building;
import com.b3.world.World;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.StringBuilder;
import com.sun.org.apache.bcel.internal.generic.NEW;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class WorldGraph implements Serializable {

	private static final Color EDGE_COLOUR = Color.BLACK;
	private static final Color NODE_COLOUR = Color.BLACK;
	private static final Color FRONTIER_COLOUR = Color.LIME;
	private static final Color NEW_FRONTIER_COLOUR = Color.CYAN;
	private static final Color JUST_EXPANDED_COLOUR = Color.PINK;
	private static final Color VISITED_COLOUR = Color.LIGHT_GRAY;
	private static final Color SEARCH_EDGE_COLOUR = Color.YELLOW;
	private static final float NODE_RADIUS = 0.10f;
	private static final Color BORDER_COLOUR = Color.BLACK;
	private static final float BORDER_THICKNESS = 1.3f; // relative to node radius
	private static final int NODE_EDGES = 4;

	private Map<Point, Node> nodes;
	private int width;
	private int height;
	private World world;
	private ShapeRenderer shapeRenderer;

	private SearchTicker currentSearch;
	private Agent currentSearchAgent;

	/**
	 * Constructs a new world graph with the following x and y dimensions.
	 * Graph has all successors, no missing edges nor non-default edge costs
	 *
	 * @param width  maximum x value (IE Point goes to max (width-1, -)
	 * @param height maximum y value (IE Point goes to max (-, height)
	 */
	public WorldGraph(int width, int height) {
		this.nodes = new LinkedHashMap<>();
		this.width = width;
		this.height = height;
		this.world = null;
		this.shapeRenderer = null; // must be initialised with initRenderer()
		this.currentSearch = null;
		this.currentSearchAgent = null;

		generateEmptyGraph(width, height);
	}

	/**
	 * Contructs a new world graph, then loads an existing WorldGraph from file.
	 *
	 * @param fileName the name of the WorldGraph to loads from file.
	 */
	public WorldGraph(String fileName) {
		this.nodes = new LinkedHashMap<>();
		width = -1;
		height = -1;

		loadFromFile(fileName);
	}

	public WorldGraph(World world) {
		this((int) world.getTileSize().x, (int) world.getTileSize().y);
		this.world = world;
	}

	/**
	 * @param fileName file name (must NOT end in 'txt' / 'ser'). eg "text1.txt" would NOT be valid
	 */
	public static WorldGraph loadFromFile(String fileName) {
		try (FileInputStream fin = new FileInputStream(fileName + ".ser");
		     ObjectInputStream ois = new ObjectInputStream(fin)) {
			return (WorldGraph) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * This allows tests to be run without initialising Gdx's graphics
	 * It must be called before any calls to render
	 */
	public void initRenderer() {
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.translate(0.5f, 0.5f, 0f);
	}

	/**
	 * Adds a new node to the table of nodes.
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
	protected boolean hasNode(Point p) {
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
	 * Snips the edges to accommodate for the given building
	 */
	public void addBuilding(Building building) {
		Vector2 tPos = building.getTilePosition();
		Vector3 dPos = building.getDimensions();
		int baseX = Math.round(tPos.x);
		int baseY = Math.round(tPos.y);
		int upToX = Math.round(dPos.x);
		int upToY = Math.round(dPos.y);

		Point entryPoint = building.getEntryPoint() == null ? null : Utils.vector2ToPoint(building.getEntryPoint());

		for (int x = baseX; x < baseX + upToX; x++) {
			for (int y = baseY; y < baseY + upToY; y++) {
				Point point = new Point(x, y);
				// Entry point check.
				if (point.equals(entryPoint))
					continue;
				Node node = nodes.get(point);
				if (node != null)
					node.clearNeighbours();
			}
		}
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
	 * @see {@link WorldGraph#removeNode(Node)}
	 */
	public boolean removeNode(Point point) {
		Node node = getNode(point);
		if (node == null)
			return false;

		removeNode(node);
		return true;
	}

	/**
	 * Saves the object as a serialisation, containing all information in this object.
	 *
	 * @param filename file name (must NOT end in 'txt' / 'ser'). eg "text1.txt" would NOT be valid
	 */
	public void saveToFile(String filename) {

		try (FileOutputStream fout = new FileOutputStream(filename);
		     ObjectOutputStream oos = new ObjectOutputStream(fout)) {
			oos.writeObject(this);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void render(Camera camera, float counter, float zoomScalar) {
		boolean showPaths = Config.getBoolean(ConfigKey.SHOW_PATHS);

		shapeRenderer.setProjectionMatrix(camera.combined);
		// render lines
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

		for (Node node1 : nodes.values()) {
			Map<Node, Float> neighbours = node1.getEdges();
			for (Map.Entry<Node, Float> neighbour : neighbours.entrySet()) {
				if (neighbour.getKey().hashCode() < node1.hashCode())
					continue;

				Float colouringRedValue = neighbour.getValue() - 1;

				if (colouringRedValue <= 1) {
					shapeRenderer.setColor(Color.BLACK);
				} else {
					Color col = new Color(((colouringRedValue + 1) * 25) / 100, 0, 0, 0);
					shapeRenderer.setColor(col);
				}
				shapeRenderer.line(
						node1.getPoint().x, node1.getPoint().y,
						neighbour.getKey().getPoint().x, neighbour.getKey().getPoint().y
				);
			}
		}

		// render the path
		if (showPaths && currentSearch != null) {// && currentSearch.isPathComplete()) {
			shapeRenderer.setColor(SEARCH_EDGE_COLOUR);

			List<Node> path = currentSearch.getPath();
			for (int i = 0, pathSize = path.size(); i < pathSize - 1; i++) {
				Node pathNodeA = path.get(i);
				Node pathNodeB = path.get(i + 1);

				shapeRenderer.line(
						pathNodeA.getPoint().x, pathNodeA.getPoint().y,
						pathNodeB.getPoint().x, pathNodeB.getPoint().y
				);
			}

		}

		shapeRenderer.end();

		// render nodes
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		Color tempCol;
		if (counter < 1)
			tempCol = NODE_COLOUR;
		else
			tempCol = new Color(counter / 10, counter / 10, counter / 10, counter / 10);

		// border
		shapeRenderer.setColor(BORDER_COLOUR);
		nodes.keySet()
				.stream()
				.forEach(p -> shapeRenderer.circle(p.x, p.y, NODE_RADIUS * counter * BORDER_THICKNESS * zoomScalar, NODE_EDGES));
		shapeRenderer.setColor(tempCol);

		// node body
		nodes.keySet()
				.stream()
				.forEach(p -> shapeRenderer.circle(p.x, p.y, NODE_RADIUS * counter * zoomScalar, NODE_EDGES));

		shapeRenderer.end();

		//if scaled back so much that nodes collapse in on each other, then show white lines on top
		if (zoomScalar > 2) {
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

			for (Node node1 : nodes.values()) {
				Map<Node, Float> neighbours = node1.getEdges();
				for (Map.Entry<Node, Float> neighbour : neighbours.entrySet()) {
					if (neighbour.getKey().hashCode() < node1.hashCode())
						continue;

					Float colouringRedValue = neighbour.getValue() - 1;

					if (colouringRedValue == 0) {
						float tempColRGBVal = (float) ((zoomScalar - 8) * 7.5);
						tempColRGBVal = tempColRGBVal / 100;
						shapeRenderer.setColor(tempColRGBVal, tempColRGBVal, tempColRGBVal, tempColRGBVal);
						if (zoomScalar > 8)
							shapeRenderer.line(
									node1.getPoint().x, node1.getPoint().y,
									neighbour.getKey().getPoint().x, neighbour.getKey().getPoint().y
							);
					} else {
						Color col = new Color(((colouringRedValue + 1) * 25) / 100, 0, 0, 0);
						shapeRenderer.setColor(col);
						shapeRenderer.line(
								node1.getPoint().x, node1.getPoint().y,
								neighbour.getKey().getPoint().x, neighbour.getKey().getPoint().y
						);
					}
				}
			}

			// render the path
			if (showPaths && currentSearch != null) {// && currentSearch.isPathComplete()) {
				shapeRenderer.setColor(SEARCH_EDGE_COLOUR);

				List<Node> path = currentSearch.getPath();
				for (int i = 0, pathSize = path.size(); i < pathSize - 1; i++) {
					Node pathNodeA = path.get(i);
					Node pathNodeB = path.get(i + 1);

					shapeRenderer.line(
							pathNodeA.getPoint().x, pathNodeA.getPoint().y,
							pathNodeB.getPoint().x, pathNodeB.getPoint().y
					);
				}

			}

			shapeRenderer.end();
		}

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		// render the current search
		if (showPaths && currentSearch != null && !currentSearch.isPathComplete()) {
			if (currentSearch.isRenderProgress()) {
				Set<Node> visited = currentSearch.getVisited();
				Collection<Node> frontier = currentSearch.getFrontier();
				Collection<Node> lastFront = currentSearch.getLastFrontier();
				Node justExpanded = currentSearch.getMostRecentlyExpanded();
				// todo last frontier


				float zoomScalarInside = zoomScalar / 5;
				if (zoomScalarInside < 1)
					zoomScalarInside = 1;

				// draw visited nodes
				shapeRenderer.setColor(Color.BLACK);
				for (Node visitedNode : visited) {
					shapeRenderer.circle(
							visitedNode.getPoint().getX(),
							visitedNode.getPoint().getY(),
							(float) ((NODE_RADIUS * zoomScalarInside) + 0.1),
							NODE_EDGES
					);
				}


				// draw visited nodes
				shapeRenderer.setColor(VISITED_COLOUR);
				for (Node visitedNode : visited) {
					shapeRenderer.circle(
							visitedNode.getPoint().getX(),
							visitedNode.getPoint().getY(),
							(float) ((NODE_RADIUS * zoomScalarInside) + 0.05),
							NODE_EDGES
					);
				}

				// draw frontier
				shapeRenderer.setColor(Color.BLACK);
				for (Node frontierNode : frontier) {
					shapeRenderer.circle(
							frontierNode.getPoint().getX(),
							frontierNode.getPoint().getY(),
							(float) ((NODE_RADIUS * zoomScalarInside) + 0.1),
							NODE_EDGES
					);
				}

				// draw frontier
				shapeRenderer.setColor(FRONTIER_COLOUR);
				for (Node frontierNode : frontier) {
					shapeRenderer.circle(
							frontierNode.getPoint().getX(),
							frontierNode.getPoint().getY(),
							(float) ((NODE_RADIUS * zoomScalarInside) + 0.05),
							NODE_EDGES
					);
				}

				// draw last frontier
				shapeRenderer.setColor(NEW_FRONTIER_COLOUR);
				for (Node newFront : lastFront) {
					shapeRenderer.circle(
							newFront.getPoint().getX(),
							newFront.getPoint().getY(),
							(float) ((NODE_RADIUS * zoomScalarInside) + 0.05),
							NODE_EDGES
					);
				}

				//draw just expanded
				if(justExpanded != null) {
					shapeRenderer.setColor(JUST_EXPANDED_COLOUR);
					shapeRenderer.circle(
							justExpanded.getPoint().getX(),
							justExpanded.getPoint().getY(),
							(float) ((NODE_RADIUS * zoomScalarInside) + 0.05),
							NODE_EDGES
					);
				}

				// last frame's frontier
//				for (Node frontierNode : lastFrontier) {
//					actorLookup.get(frontierNode.getPoint()).setNodeColour(Color.FOREST);
//                }


			}


			// now complete this tick: send the path to an agent
			if (currentSearch.isPathComplete()) {
				// TODO - Should send a message to World! (for now here)
				// TODO - Better failing system for SearchTicker.

				List<Node> path = currentSearch.getPath();
				if (path.isEmpty())
					return;

				List<Vector2> points = path
						.stream()
						.map(pointNode -> new Vector2(pointNode.getPoint().x, pointNode.getPoint().y))
						.collect(Collectors.toList());

				world.spawnAgentWithPath(points.get(0), points);
			}

		}

		// render start and end over the top of search
		if (showPaths && currentSearch != null) {
			Point start = currentSearch.getStart().getPoint();
			Point end = currentSearch.getEnd().getPoint();
			shapeRenderer.setColor(Color.BLUE);
			shapeRenderer.circle(start.x, start.y, (float) (NODE_RADIUS + 0.25), NODE_EDGES);
			shapeRenderer.circle(end.x, end.y, (float) (NODE_RADIUS + 0.25), NODE_EDGES);
		}

		// start and end are always red
//		if (start != null)
//			actorLookup.get(start.getPoint()).setNodeColour(Color.RED);
//        if (end != null)
//            actorLookup.get(end.getPoint()).setNodeColour(Color.RED);

		shapeRenderer.end();



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
	 * @return size of graph in x dimension. -1 if not loaded / cleared object without new generation.
	 */
	public int getMaxXValue() {
		return width;
	}

	/**
	 * @return size of graph in y dimension. -1 if not loaded / cleared object without new generation.
	 */
	public int getMaxYValue() {
		return height;
	}

	/**
	 * Clears object and deletes all data stored in this object.
	 * Does not delete saved data.
	 */
	public void clearWorld() {
		width = -1;
		height = -1;
		nodes.clear();
	}

	public void setCurrentSearch(Agent agent, SearchTicker currentSearch) {
		this.currentSearch = currentSearch;
		this.currentSearchAgent = agent;
	}

	public SearchTicker getCurrentSearch() {
		return currentSearch;
	}

	public Agent getCurrentSearchAgent() {
		return currentSearchAgent;
	}

	public void clearCurrentSearch() {
		// todo just for prototype
		currentSearch.reset(true);
//		currentSearch = null;
//		currentSearchAgent = null;
	}

	public boolean hasSearchInProgress() {
		return currentSearch != null;
	}
}
