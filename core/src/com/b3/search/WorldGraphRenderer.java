package com.b3.search;

import com.b3.search.util.PointTimer;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A renderer for the WorldGraph
 *
 * @author nbg481 dxw405
 */
public class WorldGraphRenderer {

	public static final Color FRONTIER_COLOUR = Color.LIME;
	public static final Color LAST_FRONTIER_COLOUR = Color.CYAN;
	public static final Color JUST_EXPANDED_COLOUR = Color.PINK;
	public static final Color VISITED_COLOUR = Color.LIGHT_GRAY;
	private static final Color EDGE_COLOUR = Color.BLACK;
	private static final Color NODE_COLOUR = Color.DARK_GRAY;
	private static final Color SEARCH_EDGE_COLOUR = Color.YELLOW;
	private static final Color CURRENT_NEIGHBOUR_COLOUR = Color.YELLOW;
	private static final Color CURRENT_NEIGHBOURS_COLOUR = Color.FIREBRICK;

	private static final float NODE_RADIUS = 0.10f;
	private static final Color BORDER_COLOUR = Color.BLACK;
	private static final float BORDER_THICKNESS = 1.3f; // relative to node radius
	private static final int NODE_EDGES = 4;

	private final WorldGraph worldGraph;
	private ShapeRenderer shapeRenderer;

	private final Color colPath;

	private int currentHighlightTimer;
	private Point currentHighlightPoint;
	private Color currentHighlightColor;

	private PointTimer redNode;
	private boolean finishedInitialAnimation;

	/**
	 * Constructs a new renderer for the given graph
	 */
	public WorldGraphRenderer(WorldGraph graph) {
		worldGraph = graph;
		this.currentHighlightPoint = null;
		this.currentHighlightTimer = 0;
		this.shapeRenderer = null; // must be initialised with initRenderer()
		finishedInitialAnimation = false;

		colPath = SEARCH_EDGE_COLOUR;
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
	 * Renders the world graph with pretty node and edge colours, as well as all current searches
	 *
	 * @param camera     The {@link Camera} to render on.
	 * @param counter    The current step in the animation.
	 * @param zoomScalar How zoomed the {@code Camera} is.
	 */
	public void render(Camera camera, float counter, float zoomScalar) {
		if (zoomScalar < 1) zoomScalar = 1;

		shapeRenderer.setProjectionMatrix(camera.combined);

		renderEdges();
		renderNodes(counter, zoomScalar);

		//if scaled back so much that nodes collapse in on each other, then show white lines on top
		if (zoomScalar > 2) {
			renderZoomedOutGraph(zoomScalar);
			shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
			worldGraph.getAllSearches()
					.stream()
					.forEach(this::renderZoomedOutSearch);
			shapeRenderer.end();
		}

		final float finalZoomScalar = zoomScalar;
		worldGraph.getAllSearches()
				.stream()
				.forEach(s -> renderSearchTicker(finalZoomScalar, s));

	}

	/**
	 * Sets a node red for an fixed period of time. This is one tick of x left in the timer.
	 * @param zoomScalar the current zoom (scaled), so that red node is sized correctly
     */
	private void setRenderRed(float zoomScalar) {
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		float zoomScalarInside = (float) (zoomScalar * 2.5);
		shapeRenderer.setColor(Color.RED);
		renderSingleSearchNode(Color.RED, new Node(redNode.getPoint()), zoomScalarInside);
		shapeRenderer.end();

		redNode.decrementTimer();
	}

	/**
	 * @param zoomScalar How zoomed in the {@code Camera} is.
	 */
	private void renderSearchTicker(float zoomScalar, SearchTicker searchTicker) {
		boolean showPaths = Config.getBoolean(ConfigKey.SHOW_PATHS);

		//red node for wrong node clicked in practice mode
		if (redNode != null)
			if (!redNode.finishedTiming()) {
				setRenderRed(zoomScalar);
			}

		renderPath(showPaths, searchTicker);

		//if scaled back so much that nodes collapse in on each other, then show white lines on top
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		// render the current search
		if (showPaths && searchTicker != null && !searchTicker.isPathComplete()) {
			if (searchTicker.isRenderProgress()) {
				Set<Node> visited = searchTicker.getVisited();
				Collection<Node> frontier = searchTicker.getFrontier();
				Collection<Node> lastFront = searchTicker.getLastFrontier();
				Node justExpanded = searchTicker.getMostRecentlyExpanded();
				List<Node> currentNeighbours = searchTicker.getCurrentNeighbours();


				float zoomScalarInside = (zoomScalar / 5);
				if (zoomScalarInside < 1)
					zoomScalarInside = 1;

				// visited nodes
				renderSearchNodes(Color.BLACK, visited, zoomScalarInside);

				// visited nodes
				renderSearchNodes(VISITED_COLOUR, visited, zoomScalarInside);

				// frontier
				renderSearchNodes(FRONTIER_COLOUR, frontier, zoomScalarInside);

				// last frontier
				renderSearchNodes(LAST_FRONTIER_COLOUR, lastFront, zoomScalarInside);

				//just expanded
				if (justExpanded != null) {
					shapeRenderer.setColor(JUST_EXPANDED_COLOUR);
					renderSingleSearchNode(JUST_EXPANDED_COLOUR, justExpanded, zoomScalarInside);
				}

				// current neighbours
				if (searchTicker.isInspectingSearch() && currentNeighbours != null)
					renderSearchNodes(CURRENT_NEIGHBOURS_COLOUR, currentNeighbours, zoomScalarInside);

				// current neighbour (to be analysed)
				Node currentNeighbour = searchTicker.getCurrentNeighbour();
				if (searchTicker.isInspectingSearch() && currentNeighbour != null)
					renderSingleSearchNode(CURRENT_NEIGHBOUR_COLOUR, currentNeighbour, zoomScalarInside);
			}
		}

		// render start and end over the top of search
		if (showPaths && searchTicker != null) {
			Point start = searchTicker.getStart().getPoint();
			Point end = searchTicker.getEnd().getPoint();
			shapeRenderer.setColor(Color.BLUE);
			shapeRenderer.circle(start.x, start.y, (float) (NODE_RADIUS + 0.25), NODE_EDGES);
			shapeRenderer.circle(end.x, end.y, (float) (NODE_RADIUS + 0.25), NODE_EDGES);
		}

		shapeRenderer.end();

		if (currentHighlightTimer > 0)
			renderHighlightedNode();
	}

	/**
	 * Highlights a node a set colour for a fixed period of time, slowly getting smaller until it disappears
	 */
	private void renderHighlightedNode() {
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(currentHighlightColor);
		currentHighlightTimer = currentHighlightTimer - 5;
		shapeRenderer.ellipse((float) (currentHighlightPoint.x - ((currentHighlightTimer / 75.0) / 2.0)), (float) ((float) currentHighlightPoint.y - ((currentHighlightTimer / 75.0) / 2.0)), (float) (currentHighlightTimer / 75.0), (float) (currentHighlightTimer / 75.0));
		shapeRenderer.end();
	}

	/**
	 * Renders all the nodes of the graph
	 * @param colour the colour to render the node
	 * @param nodes the list of nodes to render on the screen
	 * @param zoomScalarInside the zoomScalar to decide how big the nodes should be, taking into account z position of {@code Camera}
     */
	private void renderSearchNodes(Color colour, Collection<Node> nodes, float zoomScalarInside) {
		shapeRenderer.setColor(colour);
		nodes.stream()
				.forEach(n -> renderSingleSearchNode(colour, n, zoomScalarInside));
	}

	/**
	 * Renders a single node with a black boarder around it
	 * @param color The colour of the interior colour
	 * @param node the node to render on the world
	 * @param zoomScalarInside the zoomScalar to decide how big the nodes should be, taking into account z position of {@code Camera}
     */
	private void renderSingleSearchNode(Color color, Node node, float zoomScalarInside) {
		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.circle(
				node.getPoint().getX(),
				node.getPoint().getY(),
				(float) ((NODE_RADIUS * zoomScalarInside) + 0.1),
				NODE_EDGES
		);

		shapeRenderer.setColor(color);
		shapeRenderer.circle(
				node.getPoint().getX(),
				node.getPoint().getY(),
				(float) ((NODE_RADIUS * zoomScalarInside) + 0.05),
				NODE_EDGES
		);
	}

	/**
	 * Renders the graph without any nodes, but a black background and lines only to represent edges and nodes of importance
	 * @param zoomScalar the zoomScalar to decide how big the nodes should be, taking into account z position of the {@code Camera}
     */
	private void renderZoomedOutGraph(float zoomScalar) {
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

		Collection<Node> nodes = worldGraph.getNodes().values();
		for (Node node1 : nodes) {
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

		shapeRenderer.end();
	}

	/**
	 * Renders the nodes in the frontier, visited and other ones of importance
	 * @param searchTicker an instance of {@link SearchTicker}, which contains recent information about all node's status
     */
	private void renderZoomedOutSearch(SearchTicker searchTicker) {
		shapeRenderer.setColor(SEARCH_EDGE_COLOUR);

		List<Node> path = searchTicker.getPath();
		for (int i = 0, pathSize = path.size(); i < pathSize - 1; i++) {
			Node pathNodeA = path.get(i);
			Node pathNodeB = path.get(i + 1);

			shapeRenderer.line(
					pathNodeA.getPoint().x, pathNodeA.getPoint().y,
					pathNodeB.getPoint().x, pathNodeB.getPoint().y
			);
		}
	}

	/**
	 * Renders all the nodes on the grid
	 * @param counter the radius of nodes
	 * @param zoomScalar the zoomScalar to decide how big the nodes should be, taking into account z position of the {@code Camera}
     */
	private void renderNodes(float counter, float zoomScalar) {
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		//if inital openeing animation finished then tell GUI to show intro popup
		finishedInitialAnimation = (counter<=1.5);

		Color nodeColour = zoomScalar > 2 ? Color.BLACK : NODE_COLOUR;
		Set<Point> points = worldGraph.getNodes().keySet();

		// border
		float tempFinalZoomScalar;
		if (zoomScalar > 3) {
			tempFinalZoomScalar = (float) (zoomScalar * 1.25);
		} else  {
			tempFinalZoomScalar = zoomScalar;
		}

		shapeRenderer.setColor(BORDER_COLOUR);
		points
				.stream()
				.forEach(point -> renderSingleSearchNodeWithScaling(point.getX(), point.getY(), NODE_RADIUS * counter * BORDER_THICKNESS * tempFinalZoomScalar, tempFinalZoomScalar, counter));
		shapeRenderer.setColor(nodeColour);

		// node body
		final float finalZoomScalar1 = zoomScalar;
		points
				.stream()
				.forEach(point -> renderSingleSearchNodeWithScaling(point.getX(), point.getY(), NODE_RADIUS * counter * finalZoomScalar1, tempFinalZoomScalar, counter));

		shapeRenderer.end();
	}

	/**
	 * Renders a single search node with four scaling corners which are bigger by default the scale to cover the rest of the world
	 * @param x x coordinate of node
	 * @param y y coordinate of node
	 * @param v radius of circle
	 * @param tempFinalZoomScalar the zoom scalar for animation
	 * @param counter the counter initial animation
	 */
	private void renderSingleSearchNodeWithScaling(int x, int y, float v, float tempFinalZoomScalar, float counter) {
		if (x == 0 && y == 0 || x == worldGraph.getWidth()-1 & y == worldGraph.getHeight()-1 || x == 0 && y == worldGraph.getHeight()-1 || x == worldGraph.getWidth()-1 & y ==0) {
			shapeRenderer.circle(x, y, v * tempFinalZoomScalar * counter * 2, WorldGraphRenderer.NODE_EDGES);
		} else {
			shapeRenderer.circle(x, y, v, WorldGraphRenderer.NODE_EDGES);
		}
	}

	/**
	 * Renders the path ontop of the search
	 * @param showPaths if true then render the path, otherwise not
	 * @param searchTicker an instance of {@link SearchTicker}, which contains the path to render
     */
	private void renderPath(boolean showPaths, SearchTicker searchTicker) {
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		// render the path
		if (showPaths && searchTicker != null) {// && searchTicker.isPathComplete()) {
			colPath.add((float) -0.015, (float) 0.025, (float) 0.010, 0);
			colPath.a = 1;

			shapeRenderer.setColor(colPath);

			List<Node> path = searchTicker.getPath();
			for (int i = 0, pathSize = path.size(); i < pathSize - 1; i++) {
				Node pathNodeA = path.get(i);
				Node pathNodeB = path.get(i + 1);

				float size = colPath.r / 7; //(1 - (colPath.r)) / 7;
				if (size < 0.05) size = (float) 0.05;

				shapeRenderer.rectLine(
						pathNodeA.getPoint().x, pathNodeA.getPoint().y,
						pathNodeB.getPoint().x, pathNodeB.getPoint().y,
						size
				);
			}
		}

		shapeRenderer.end();
	}

	/**
	 * Renders all of the edges onto the world, between all existing nodes
	 * Renders lower cost nodes black, and higher cost nodes white
	 */
	private void renderEdges() {
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		Collection<Node> nodes = worldGraph.getNodes().values();

		for (Node node : nodes) {
			Map<Node, Float> neighbours = node.getEdges();
			for (Map.Entry<Node, Float> neighbour : neighbours.entrySet()) {
				if (neighbour.getKey().hashCode() < node.hashCode())
					continue;

				Float colouringRedValue = neighbour.getValue() - 1;

				if (colouringRedValue <= 1) {
					shapeRenderer.setColor(EDGE_COLOUR);
				} else {
					Color col = new Color(((colouringRedValue + 1) * 25) / 100, 0, 0, 0);
					shapeRenderer.setColor(col);
				}
				shapeRenderer.line(
						node.getPoint().x, node.getPoint().y,
						neighbour.getKey().getPoint().x, neighbour.getKey().getPoint().y
				);
			}
		}

		shapeRenderer.end();
	}

	/**
	 * Resets the colour path to its pre-flicker base colour
	 */
	public void resetPathFlicker() {
		colPath.r = 255;
		colPath.g = 255;
		colPath.b = 0;
	}

	/**
	 * Sets the currently highlighted point to the given one
	 * @param highlightingPoint The point to highlight
	 * @param colour The colour to highlight it with
	 */
	public void highlightOver(Point highlightingPoint, Color colour) {
		currentHighlightTimer = 100;
		currentHighlightPoint = highlightingPoint;
		currentHighlightColor = colour;
	}

	/**
	 * Sets the current red-highlighted node to the given one
	 * @param x The node X coordinate
	 * @param y The node Y coordinate
	 * @param time The time to stay red for
	 */
	public void highlightNodeRed(int x, int y, int time) {
		redNode = new PointTimer(x, y, time);
	}

	public Boolean getAnimationFinished() {
		return finishedInitialAnimation;
	}
}
