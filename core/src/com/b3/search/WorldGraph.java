package com.b3.search;

import com.b3.entity.Agent;
import com.b3.search.util.SearchAlgorithm;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.world.World;
import com.b3.world.building.Building;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.StringBuilder;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The internal graph at a low level.
 */
public class WorldGraph implements Serializable {

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

    private Map<Point, Node> nodes;
    private int width;
    private int height;
    private World world;
    private ShapeRenderer shapeRenderer;

    private SearchTicker latestSearchTicker;
    private Agent latestSearchAgent;
    private Map<Agent, SearchTicker> searchTickers;

    //current wanted next destination (right click)
    private int wantedNextDestinationX = -5;
    private int wantedNextDestinationY = -5;

    //search algorithm wanted for learning mode only (Leave as null if compare mode)
    private SearchAlgorithm learningModeNext = null;

    private Color colPath;

    private int currentHighlightTimer;
    private Point currentHighlightPoint;
    private Color currentHighlightColor;

    private PointTimer setRedNode;

    /**
     * Constructs a new world graph with the following x and y dimensions.
     * Graph has all successors, no missing edges nor non-default edge costs
     *
     * @param width  maximum x value (IE Point goes to max (width-1, -)
     * @param height maximum y value (IE Point goes to max (-, height)
     */
    public WorldGraph(int width, int height) {
        this.currentHighlightPoint = null;
        this.currentHighlightTimer = 0;
        this.nodes = new LinkedHashMap<>();
        this.width = width;
        this.height = height;
        this.world = null;
        this.shapeRenderer = null; // must be initialised with initRenderer()
        this.searchTickers = new LinkedHashMap<>();

        colPath = SEARCH_EDGE_COLOUR;

        generateEmptyGraph(width, height);
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
     * Snips the edges to accommodate for the given building
     */
    public void addBuilding(Building building) {
        Vector2 tPos = building.getTilePosition();
        Vector3 dPos = building.getDimensions();
        int baseX = Math.round(tPos.x);
        int baseY = Math.round(tPos.y);
        int upToX = Math.round(dPos.x);
        int upToY = Math.round(dPos.y);

//		Point entryPoint = building.getEntryPoint() == null ? null : Utils.vector2ToPoint(building.getEntryPoint());

        for (int x = baseX; x < baseX + upToX; x++) {
            for (int y = baseY; y < baseY + upToY; y++) {
//				removeNode(new Node(new Point(x,y)));
//				Point point = new Point(x, y);
//				 Entry point check.
//				if (point.equals(entryPoint))
//					continue;
                Node node = nodes.get(new Point(x, y));
                if (node != null)
                    removeNode(node);
                //node.clearNeighbours();
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

    /**
     * Renders the world graph with pretty node and edge colours, as well as all current searches
     *
     * @param camera     The {@link Camera} to render on.
     * @param counter    The current step in the animation.
     * @param zoomScalar How zoomed the {@code Camera} is.
     */
    public void render(Camera camera, float counter, float zoomScalar) {
        boolean showPaths = Config.getBoolean(ConfigKey.SHOW_PATHS);

        if (zoomScalar < 1) zoomScalar = 1;

        shapeRenderer.setProjectionMatrix(camera.combined);

        renderEdges();
        renderNodes(counter, zoomScalar);

        //if scaled back so much that nodes collapse in on each other, then show white lines on top
        if (zoomScalar > 2) {
            renderZoomedOutGraph(zoomScalar, showPaths);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            searchTickers
                    .values()
                    .stream()
                    .forEach(this::renderZoomedOutSearch);
            shapeRenderer.end();
        }
        final float finalZoomScalar = zoomScalar;
        searchTickers
                .values()
                .stream()
                .forEach(s -> renderSearchTicker(camera, counter, finalZoomScalar, s));

    }

    private void setRenderRed(float zoomScalar) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        float zoomScalarInside = (float) (zoomScalar * 2.5);
        shapeRenderer.setColor(Color.RED);
        renderSingleSearchNode(Color.RED, new Node(setRedNode.getPoint()), zoomScalarInside);
        shapeRenderer.end();

        setRedNode.decrementTimer();
    }

    /**
     * @param camera     The {@link Camera} to render on.
     * @param counter    The current step in the animation.
     * @param zoomScalar How zoomed the {@code Camera} is.
     */
    private void renderSearchTicker(Camera camera, float counter, float zoomScalar, SearchTicker searchTicker) {
        boolean showPaths = Config.getBoolean(ConfigKey.SHOW_PATHS);

        //red node for wrong node clicked in practice mode
        if (setRedNode != null)
            if (!setRedNode.finishedTiming()) {
                setRenderRed(zoomScalar);
            }

        renderPath(showPaths, searchTicker, zoomScalar);

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


            // TODO - Should send a message to World! (for now here)
            // TODO - Better failing system for SearchTicker.
            // just completed this tick: send the path to an agent
            if (searchTicker.isPathComplete())
                spawnAgentWithPath(searchTicker);
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

    private void renderHighlightedNode() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(currentHighlightColor);
        currentHighlightTimer = currentHighlightTimer - 5;
        shapeRenderer.ellipse((float) (currentHighlightPoint.x - ((currentHighlightTimer / 75.0) / 2.0)), (float) ((float) currentHighlightPoint.y - ((currentHighlightTimer / 75.0) / 2.0)), (float) (currentHighlightTimer / 75.0), (float) (currentHighlightTimer / 75.0));
        shapeRenderer.end();
    }

    private void spawnAgentWithPath(SearchTicker searchTicker) {
        List<Node> path = searchTicker.getPath();
        if (path.isEmpty())
            return;

        List<Vector2> points = path
                .stream()
                .map(pointNode -> new Vector2(pointNode.getPoint().x, pointNode.getPoint().y))
                .collect(Collectors.toList());

        world.spawnAgentWithPath(points.get(0), points);
    }

    private void renderSearchNodes(Color colour, Collection<Node> nodes, float zoomScalarInside) {
        shapeRenderer.setColor(colour);
        nodes.stream()
                .forEach(n -> renderSingleSearchNode(colour, n, zoomScalarInside));
    }

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

    private void renderZoomedOutGraph(float zoomScalar, boolean showPaths) {
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

        shapeRenderer.end();
    }

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

    private void renderNodes(float counter, float zoomScalar) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Color nodeColour = zoomScalar > 2 ? Color.BLACK : NODE_COLOUR;

        // border
        shapeRenderer.setColor(BORDER_COLOUR);
        final float finalZoomScalar = zoomScalar;
        nodes.keySet()
                .stream()
                .forEach(p -> shapeRenderer.circle(p.x, p.y, NODE_RADIUS * counter * BORDER_THICKNESS * finalZoomScalar, NODE_EDGES));
        shapeRenderer.setColor(nodeColour);

        // node body
        final float finalZoomScalar1 = zoomScalar;
        nodes.keySet()
                .stream()
                .forEach(p -> shapeRenderer.circle(p.x, p.y, NODE_RADIUS * counter * finalZoomScalar1, NODE_EDGES));

        shapeRenderer.end();
    }

    private void renderPath(boolean showPaths, SearchTicker searchTicker, float zoomScalar) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

//		// render the path
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

    private void renderEdges() {
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
    
    public Set<Agent> getAllSearchAgents() {
        return searchTickers.keySet();
    }

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
        searchTickers.forEach((a,s) -> {
            s.reset(true);
        });
        
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

    public void setColFlicker() {
        colPath.r = 255;
        colPath.g = 255;
        colPath.b = 0;
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
                addNode(new Point(i, j));
            }
        }
        //add edges back into
        for (int i = (int) positionDeletion.x; i < positionDeletion.x + 4; i++) {
            for (int j = (int) positionDeletion.y; j < positionDeletion.y + 4; j++) {
                Point currentPoint = new Point(i, j);
                if (nodes.containsKey(new Point(i + 1, j)))
                    addEdge(currentPoint, new Point(i + 1, j), 1);
                if (nodes.containsKey(new Point(i - 1, j)))
                    addEdge(currentPoint, new Point(i - 1, j), 1);
                if (nodes.containsKey(new Point(i, j + 1)))
                    addEdge(currentPoint, new Point(i, j + 1), 1);
                if (nodes.containsKey(new Point(i, j - 1)))
                    addEdge(currentPoint, new Point(i, j - 1), 1);
            }
        }

    }

    public void highlightOver(Point highlightingPoint, Color colors) {
        currentHighlightTimer = 100;
        currentHighlightPoint = highlightingPoint;
        currentHighlightColor = colors;
    }

    public void setRed(int x, int y, int time) {
        setRedNode = new PointTimer(x, y, time);
    }

//	public boolean checkEveryEdge() {
//		for (Map.Entry<Point, Node> entry: nodes.entrySet()) {
//			Point point = entry.getKey();
//			Node connected = entry.getValue();
//
//			if (connected.getEdges().size() == 0)
//				return false;
//		}
//		return true;
//	}
}
