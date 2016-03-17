package com.b3.search;

import com.b3.entity.Agent;
import com.b3.search.util.SearchAlgorithm;
import com.b3.world.World;
import com.b3.world.building.Building;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * The rendered graph, that holds all running searches
 *
 * @author oxe410 dxw405 nbg481
 */
public class WorldGraph {
	private final Graph graph;
	private final WorldGraphRenderer renderer;

	private SearchTicker latestSearchTicker;
	private Agent latestSearchAgent;
	private final Map<Agent, SearchTicker> searchTickers;

	//current wanted next destination (right click)
	private int wantedNextDestinationX = -5;
	private int wantedNextDestinationY = -5;

	//search algorithm wanted for learning mode only (Leave as null if compare mode)
	private SearchAlgorithm learningModeNext = null;

	/**
	 * Constructs a new world graph with the following x and y dimensions.
	 * Graph has all successors, no missing edges nor non-default edge costs
	 *
	 * @param width  the width of the graph
	 * @param height the height of the graph
	 */
	public WorldGraph(int width, int height) {
		this.graph = new Graph(width, height);
		this.renderer = new WorldGraphRenderer(this);
		this.searchTickers = new LinkedHashMap<>();
	}


	public WorldGraph(World world) {
		this((int) world.getTileSize().x, (int) world.getTileSize().y);
	}

	/**
	 * This allows tests to be run without initialising Gdx's graphics
	 * It must be called before any calls to render
	 */
	public void initRenderer() {
		renderer.initRenderer();
	}

	public WorldGraphRenderer getRenderer() {
		return renderer;
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

		graph.snipEdges(baseX, upToX, baseY, upToY);
	}

	/**
	 * @return all the nodes on the graph as a linkedHashSet / Map<Point, Node>
	 */
	public Map<Point, Node> getNodes() {
		return graph.getNodes();
	}

	/**
	 * @param point The key
	 * @return The node associated with the given key
	 */
	public Node getNode(Point point) {
		return graph.getNode(point);
	}

	/**
	 * @return size of graph in x dimension. -1 if not loaded / cleared object without new generation.
	 */
	public int getWidth() {
		return graph.getWidth();
	}

	/**
	 * @return size of graph in y dimension. -1 if not loaded / cleared object without new generation.
	 */
	public int getHeight() {
		return graph.getHeight();
	}

	/**
	 * Removes an edge (undirected or directed) between two nodes if it exists
	 *
	 * @param p1 The content of the first node
	 * @param p2 The content of the second node
	 * @return True if the edge existed and has been removed, false otherwise
	 */
	public boolean removeEdge(Point p1, Point p2) {
		return graph.removeEdge(p1, p2);
	}

	/**
	 * Tells whether the graph has a specific edge
	 *
	 * @param p1 The first Point
	 * @param p2 The second Point
	 * @return True if the graph has a p1 -- p2 edge, false otherwise
	 */
	public boolean hasEdge(Point p1, Point p2) {
		return graph.hasEdge(p1, p2);
	}

	/**
	 * Check whether there is node c in the table of nodes
	 *
	 * @param p The content of the node to search
	 * @return True is the node exists, false otherwise
	 */
	public boolean hasNode(Point p) {
		return graph.hasNode(p);
	}

	/**
	 * Removes the given node from the graph, snipping all connected edges
	 *
	 * @param node The node to remove
	 */
	public void removeNode(Node node) {
		graph.removeNode(node);
	}

	/**
	 * Removes the node at the given point, if it exists
	 *
	 * @param point The position of the node to remove
	 * @return True if the node existed and has been removed, otherwise false
	 * @see {@link Graph#removeNode(Node)}
	 */
	public boolean removeNode(Point point) {
		return graph.removeNode(point);
	}

	/**
	 * Adds a new node to the graph
	 *
	 * @param p The node's tile position
	 * @return The new node. If it exists then simply return it.
	 */
	public Node addNode(Point p) {
		return graph.addNode(p);
	}

	/**
	 * Adds an edge between 2 nodes
	 *
	 * @param p1   The content of the first node (source)
	 * @param p2   The content of the second node (destination)
	 * @param cost The edge cost
	 */
	public void addEdge(Point p1, Point p2, float cost) {
		graph.addEdge(p1, p2, cost);
	}

	/**
	 * Sets the current search to display and the agent responsible.
	 *
	 * @param agent         The agent who will use the path generated.
	 * @param currentSearch The {@link SearchTicker} doing the path generation.
	 */
	public void setCurrentSearch(Agent agent, SearchTicker currentSearch) {
		latestSearchAgent = agent;
		latestSearchTicker = currentSearch;
		searchTickers.put(agent, currentSearch);
	}

	/**
	 * @return The {@link SearchTicker} that is currently being displayed.
	 */
	public SearchTicker getCurrentSearch() {
		return latestSearchTicker;
	}

	/**
	 * @return A set of all agents who have a corresponding SearchTicker
	 * Note that they are not all necessarily in progress
	 */
	public Set<Agent> getAllSearchAgents() {
		return searchTickers.keySet();
	}


	/**
	 * @return A collection of all current search tickers
	 * Note that they are not all necessarily in progress
	 */
	public Collection<SearchTicker> getAllSearches() {
		return searchTickers.values();
	}

	/**
	 * @return The agent that will follow {@link #getCurrentSearch()}.
	 */
	public Agent getCurrentSearchAgent() {
		return latestSearchAgent;
	}

	public boolean isAgentSearching(Entity entity) {
		return entity instanceof Agent && searchTickers.containsKey(entity);
	}

	public void clearAllSearches() {
		searchTickers.forEach((a, s) -> s.reset(true));
		searchTickers.clear();
	}

	public void clearSearch(Agent searchingAgent) {
		SearchTicker searchTicker = searchTickers.remove(searchingAgent);
		if (searchTicker != null)
			searchTicker.reset(true);
	}

	/**
	 * Whether there is a search currently being displayed.
	 *
	 * @return <code>true</code> if there is a search being displayed;
	 * <code>false</code> otherwise.
	 */
	public boolean hasSearchInProgress() {
		return !searchTickers.isEmpty();
	}

	/**
	 * Sets the next destination for the {@link SearchTicker}.
	 *
	 * @param x The x coordinate to request.
	 * @param y The y coordinate to request.
	 */
	public void setNextDestination(int x, int y) {
		wantedNextDestinationX = x;
		wantedNextDestinationY = y;
	}

	/**
	 * @return The next destination the {@link SearchTicker} will try reach.
	 */
	public Point getNextDestination() {
		return new Point(wantedNextDestinationX, wantedNextDestinationY);
	}

	public SearchAlgorithm getLearningModeNext() {
		return learningModeNext;
	}

	public void setLearningModeNext(SearchAlgorithm learningModeNext) {
		this.learningModeNext = learningModeNext;
	}

	/**
	 * Removed a building from the WorldGraph,
	 * the {@link Node Nodes} and edges that were covered will be restored.
	 *
	 * @param positionDeletion The bottom left hand corner of the building to delete.
	 */
	public void removeBuilding(Vector2 positionDeletion) {
		//add nodes back into
		for (int i = (int) positionDeletion.x; i < positionDeletion.x + 4; i++) {
			for (int j = (int) positionDeletion.y; j < positionDeletion.y + 4; j++) {
				graph.addNode(new Point(i, j));
			}
		}
		//add edges back into
		for (int i = (int) positionDeletion.x; i < positionDeletion.x + 4; i++) {
			for (int j = (int) positionDeletion.y; j < positionDeletion.y + 4; j++) {
				Point currentPoint = new Point(i, j);
				if (graph.hasNode(new Point(i + 1, j)))
					graph.addEdge(currentPoint, new Point(i + 1, j), 1);
				if (graph.hasNode(new Point(i - 1, j)))
					graph.addEdge(currentPoint, new Point(i - 1, j), 1);
				if (graph.hasNode(new Point(i, j + 1)))
					graph.addEdge(currentPoint, new Point(i, j + 1), 1);
				if (graph.hasNode(new Point(i, j - 1)))
					graph.addEdge(currentPoint, new Point(i, j - 1), 1);
			}
		}

	}
}
