package com.b3.searching;

import com.b3.searching.optional.*;
import com.b3.world.World;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Nishanth on 12/02/2016.
 */

public class WorldGraph<A> implements Serializable {

    private Map<A, Node<A>> nodes;
    private ArrayList<Buildings> buildings;
    private int xMax;
    private int yMax;

    private World world;

    /**
     * Constructs a new world graph with the following x and y dimensions.
     * Graph has all successors, no missing edges nor non-default edge costs
     * @param xMax maximum x value (IE Point goes to max (xMax-1, -)
     * @param yMax maximum y value (IE Point goes to max (-, yMax)
     */
    public WorldGraph(int xMax, int yMax) {
        this(xMax, yMax, null);
    }

    /**
     * Contructs a new world graph, then loads an existing WorldGraph from file.
     * @param fileName the name of the WorldGraph to loads from file.
     */
    public WorldGraph(String fileName) {
        this.nodes = new LinkedHashMap<>();
        this.buildings = new ArrayList<Buildings>();
        xMax = -1;
        yMax = -1;

        loadFromFile(fileName);
    }

    public WorldGraph(int xMax, int yMax, World world) {
        this.nodes = new LinkedHashMap<>();
        this.xMax = xMax;
        this.yMax = yMax;
        this.buildings = new ArrayList<Buildings>();
        this.world = world;

        generateEmptyGraph(xMax, yMax);
    }

    /**
     * Adds a new node to the table of nodes.
     * @param c The node's data/content, most likely a type Point.
     * @return The new node. If it exists then simply return it.
     */
    protected Node<A> addNode(A c, int cost) {
        Node<A> node;

        if (!nodes.containsKey(c)) {
            node = new Node<>(c);
            nodes.put(c, node);
        } else
            node = nodes.get(c);

        node.setExtraCost(cost);

        return node;
    }

    /**
     * Adds an edge/link between 2 nodes
     * @param c1 The content of the first node (source). Likely a Point.
     * @param c2 The content of the second node (destination). Likely a Point.
     * @param directed If true the edge is directed. If false it is undirected. Should be True for WorldGraph.
     */
    protected void addEdge(Point c1, Point c2, boolean directed, int cost1, int cost2) {
        // Add nodes (if not existing) and get nodes from adjacency list
        Node<A> node1 = addNode((A) c1, cost1);
        Node<A> node2 = addNode((A) c2, cost2);

        // Create edge
        node1.addSuccessor(node2);
        if (!directed)
            node2.addSuccessor(node1);

        // Update adjacency list
        nodes.put((A) c1, node1);
        if (!directed)
            nodes.put((A) c2, node2);
    }

    /**
     * Generates a graph with x and y dimensions as specified in parameters
     * does not add any blocks or buildings, will add every single possible successor to each node
     * an integrity check is computed at the end to check if every node has at least one successor, does not mean graph is accurate or correct - visual inspection recommended.
     *
     * @param xMax max x value (coordinate's x value will go up to xMax-1) IE if you pass 5, it will go from 0 to 4
     * @param yMax max y value (coordinate's y value will go up to yMax-1) IE if you pass 5, it will go from 0 to 4
     */
    public void generateEmptyGraph(int xMax, int yMax) {
        for (int x = 0; x < xMax; x++) {
            for (int y = 0; y < yMax; y++) {
                Point tempCoord = new Point(x, y);
                //if in middle portion of graph (IE not edge)
                if (x != 0 & x != (xMax - 1) & y != 0 & y != (yMax - 1)) {
                    Point t1 = new Point(x + 1, y);
                    Point t2 = new Point(x - 1, y);
                    Point t3 = new Point(x, y + 1);
                    Point t4 = new Point(x, y - 1);
                    addEdge(tempCoord, t1, true, 0, 0);
                    addEdge(tempCoord, t2, true, 0, 0);
                    addEdge(tempCoord, t3, true, 0, 0);
                    addEdge(tempCoord, t4, true, 0, 0);

                } else
                    //if on left hand side
                    if (x == 0 & y != (yMax - 1) & y != 0) {
                        Point t1 = new Point(x + 1, y);
                        Point t2 = new Point(x, y + 1);
                        Point t3 = new Point(x, y - 1);
                        addEdge(tempCoord, t1, true, 0, 0);
                        addEdge(tempCoord, t2, true, 0, 0);
                        addEdge(tempCoord, t3, true, 0, 0);
                    } else
                        //if on right hand side
                        if (x == (xMax - 1) & y != (yMax - 1) & y != 0) {
                            Point t1 = new Point(x - 1, y);
                            Point t2 = new Point(x, y + 1);
                            Point t3 = new Point(x, y - 1);
                            addEdge(tempCoord, t1, true, 0, 0);
                            addEdge(tempCoord, t2, true, 0, 0);
                            addEdge(tempCoord, t3, true, 0, 0);
                        } else
                            //if on top
                            if (y == 0 & x != (xMax - 1) & x != 0) {
                                Point t1 = new Point(x + 1, y);
                                Point t2 = new Point(x - 1, y);
                                Point t3 = new Point(x, y + 1);
                                addEdge(tempCoord, t1, true, 0, 0);
                                addEdge(tempCoord, t2, true, 0, 0);
                                addEdge(tempCoord, t3, true, 0, 0);

                            } else
                                //if on bottom
                                if (y == (yMax - 1) & x != 0 & x != (xMax - 1)) {
                                    Point t1 = new Point(x - 1, y);
                                    Point t2 = new Point(x + 1, y);
                                    Point t3 = new Point(x, y - 1);
                                    addEdge(tempCoord, t1, true, 0, 0);
                                    addEdge(tempCoord, t2, true, 0, 0);
                                    addEdge(tempCoord, t3, true, 0, 0);
                                } else
                                    //deal with corner cases
                                    //top left
                                    if (x == 0 & y == 0) {
                                        Point t1 = new Point(x + 1, y);
                                        Point t2 = new Point(x, y + 1);
                                        addEdge(tempCoord, t1, true, 0, 0);
                                        addEdge(tempCoord, t2, true, 0, 0);
                                    } else
                                        //bottom left
                                        if (x == 0 & y == (yMax - 1)) {
                                            Point t1 = new Point(x + 1, y);
                                            Point t2 = new Point(x, y - 1);
                                            addEdge(tempCoord, t1, true, 0, 0);
                                            addEdge(tempCoord, t2, true, 0, 0);
                                        } else
                                            //top right
                                            if (x == (xMax - 1) & y == 0) {
                                                Point t1 = new Point(x - 1, y);
                                                Point t2 = new Point(x, y + 1);
                                                addEdge(tempCoord, t1, true, 0, 0);
                                                addEdge(tempCoord, t2, true, 0, 0);
                                            } else
                                                //bottom right
                                                if (x == (xMax - 1) & y == (yMax - 1)) {
                                                    Point t1 = new Point(x - 1, y);
                                                    Point t2 = new Point(x, y - 1);
                                                    addEdge(tempCoord, t1, true, 0, 0);
                                                    addEdge(tempCoord, t2, true, 0, 0);
                                                }
            }
        }
    }

    /**
     * Tells whether the graph has a specific edge
     * @param c1 The first Point
     * @param c2 The second Point
     * @return True if the graph has a c1 -- c2 edge, false otherwise
     */
    protected boolean hasEdge(Point c1, Point c2) {
        Node<A> n1 = nodes.get(c1);
        Node<A> n2 = nodes.get(c2);

        return n1 != null && n2 != null && (n1.getSuccessors().contains(n2) || n2.getSuccessors().contains(n1));
    }

    /**
     * Checks to see if two points have an edge between them
     * @param x1 x coordinate of 1st point
     * @param y1 y coordinate of 1st point
     * @param x2 x coordinate of 2nd point
     * @param y2 y coordinate of 2nd point
     * @return true if edge exists, false otherwise
     */
    private boolean checkExists(int x1, int y1, int x2, int y2) {
        Point a = new Point(x1, y1);
        Point b = new Point(x2, y2);

        return hasEdge(a, b);
    }

    /**
     * Check whether there is node c in the table of nodes
     * @param c The content of the node to search
     * @return True is the node exists, false otherwise
     */
    protected boolean hasNode(A c) {
        return nodes.containsKey(c);
    }

    /**
     * Removes an edge (undirected or directed) between two nodes if it exists
     * @param c1 The content of the first node
     * @param c2 The content of the second node
     * @return True if the edge exists and has been removed, false otherwise
     */
    protected boolean removeEdge(Point c1, Point c2) {
        // If there are the two nodes
        if (hasNode((A) c1) && hasNode((A) c2)) {
            Node<A> node1 = nodes.get(c1);
            Node<A> node2 = nodes.get(c2);

            // Attempt to remove the edge (if exists - either directed or undirected)
            boolean removed1 = node1.removeSuccessor(node2);
            boolean removed2 = node2.removeSuccessor(node1);

            if (removed1)
                nodes.put((A) c1, node1);
            else if (removed2)
                nodes.put((A) c2, node2);
            else return false;

            return true;
        }

        return false;
    }

    /**
     * Removes an edge (undirected or directed) between two nodes if it exists
     * @param x1 x coordinate of 1st point
     * @param y1 y coordinate of 1st point
     * @param x2 x coordinate of 2nd point
     * @param y2 y coordinate of 2nd point
     * @return True if the edge exists and has been removed, false otherwise
     */
    private boolean removeEdges(int x1, int y1, int x2, int y2) {
        Point a = new Point(x1, y1);
        Point b = new Point(x2, y2);

        return removeEdge(a, b);
    }

    /**
     * Add a building to the graph
     * Should be safe - ie return false if placement of building will result in overlapping buildings.
     *
     * @param tempX the x coordinate of top left of building
     * @param tempY the y coordinate of top left of building
     * @param size  the size of the building (1 = NOT SAFE DONT USE; 2 = 1 edge removed + 1 below; 3 = 1 edge removed + 1 to right; 4 = edge @ xy, to right, below, and to right-below removed
     * @return true if successful, false if not
     */
    public Boolean addBuilding(int tempX, int tempY, int size) {

        Boolean revert = false;

        switch (size) {
            case 2:
                if (!checkExists(tempX, tempY, tempX - 1, tempY)
                        | !checkExists(tempX, tempY, tempX + 1, tempY)
                        | !checkExists(tempX, tempY, tempX, tempY - 1)
                        | !checkExists(tempX, tempY, tempX, tempY + 1)
                        | !checkExists(tempX, tempY + 1, tempX, tempY)
                        | !checkExists(tempX, tempY + 1, tempX + 1, tempY + 1)
                        | !checkExists(tempX, tempY + 1, tempX - 1, tempY + 1)
                        | !checkExists(tempX, tempY + 1, tempX, tempY + 2)
                        ) {
                    System.out.println("NOT ADDED");
                    return false;
                } else {
                    revert = !(
                            removeEdges(tempX, tempY, tempX - 1, tempY) //& removeEdges(tempX-1, tempY, tempX, tempY)
                                    & removeEdges(tempX, tempY, tempX + 1, tempY) //& removeEdges(tempX+1, tempY, tempX, tempY)
                                    & removeEdges(tempX, tempY, tempX, tempY - 1) //& removeEdges(tempX, tempY-1, tempX, tempY)
                                    & removeEdges(tempX, tempY, tempX, tempY + 1)// & removeEdges(tempX, tempY+1, tempX, tempY)
                                    //& removeEdges(tempX, tempY+1, tempX, tempY) & removeEdges(tempX, tempY, tempX, tempY+1)
                                    & removeEdges(tempX, tempY + 1, tempX + 1, tempY + 1)// & removeEdges(tempX+1, tempY+1, tempX, tempY+1)
                                    & removeEdges(tempX, tempY + 1, tempX - 1, tempY + 1)// & removeEdges(tempX-1, tempY+1, tempX, tempY+1)
                                    & removeEdges(tempX, tempY + 1, tempX, tempY + 2)// & removeEdges(tempX, tempY+2, tempX, tempY+1)
                    );

                    if (revert) {
                        System.out.println("SOMETHING WENT WRONG; delete this object and start again. Building's cleared, graph now corrupt");
                        return false;
                    } else {
                        buildings.add(new Buildings(new Point(tempX, tempY), 2));
                        System.out.println("ADDED");
                        return true;
                    }
                }

            case 3:
                if (
                        !checkExists(tempX, tempY, tempX + 1, tempY)
                                | !checkExists(tempX, tempY, tempX - 1, tempY)
                                | !checkExists(tempX, tempY, tempX, tempY + 1)
                                | !checkExists(tempX, tempY, tempX, tempY - 1)
                                | !checkExists(tempX + 1, tempY, tempX, tempY)
                                | !checkExists(tempX + 1, tempY, tempX + 2, tempY)
                                | !checkExists(tempX + 1, tempY, tempX + 1, tempY + 1)
                                | !checkExists(tempX + 1, tempY, tempX + 1, tempY - 1)
                        ) {
                    System.out.println("NOT ADDED");
                    return false;
                } else {
                    revert = !(removeEdges(tempX, tempY, tempX + 1, tempY)// & removeEdges(tempX+1, tempY, tempX, tempY)
                            & removeEdges(tempX, tempY, tempX - 1, tempY)/// & removeEdges(tempX-1, tempY, tempX, tempY)
                            & removeEdges(tempX, tempY, tempX, tempY + 1)// & removeEdges(tempX, tempY+1, tempX, tempY)
                            & removeEdges(tempX, tempY, tempX, tempY - 1)// & removeEdges(tempX, tempY-1, tempX, tempY)
                            //& removeEdges(tempX+1, tempY, tempX, tempY) & removeEdges(tempX, tempY, tempX+1, tempY)
                            & removeEdges(tempX + 1, tempY, tempX + 2, tempY)// & removeEdges(tempX+2, tempY, tempX+1, tempY)
                            & removeEdges(tempX + 1, tempY, tempX + 1, tempY + 1)// & removeEdges(tempX+1, tempY+1, tempX+1, tempY)
                            & removeEdges(tempX + 1, tempY, tempX + 1, tempY - 1)// & removeEdges(tempX+1, tempY-1, tempX+1, tempY)

                    );
                    if (revert) {
                        System.out.println("SOMETHING WENT WRONG; delete this object and start again. Building's cleared, graph now corrupt");
                        return false;
                    } else {
                        buildings.add(new Buildings(new Point(tempX, tempY), 3));
                        System.out.println("ADDED");
                        return true;
                    }
                }


            case 4:
                if (!checkExists(tempX, tempY, tempX - 1, tempY) |
                        !checkExists(tempX, tempY, tempX + 1, tempY) |
                        !checkExists(tempX, tempY, tempX, tempY + 1) |
                        !checkExists(tempX, tempY, tempX, tempY - 1) |

                        !checkExists(tempX + 1, tempY, tempX, tempY) |
                        !checkExists(tempX + 1, tempY, tempX + 2, tempY) |
                        !checkExists(tempX + 1, tempY, tempX + 1, tempY + 1) |
                        !checkExists(tempX + 1, tempY, tempX + 1, tempY - 1) |
                        !checkExists(tempX, tempY + 1, tempX + 1, tempY + 1) |

                        !checkExists(tempX, tempY + 1, tempX - 1, tempY + 1) |
                        !checkExists(tempX, tempY + 1, tempX, tempY + 2) |
                        !checkExists(tempX, tempY + 1, tempX, tempY) |
                        !checkExists(tempX, tempY + 1, tempX, tempY) |

                        !checkExists(tempX + 1, tempY + 1, tempX + 2, tempY + 1) |
                        !checkExists(tempX + 1, tempY + 1, tempX, tempY + 1) |
                        !checkExists(tempX + 1, tempY + 1, tempX + 1, tempY + 2) |
                        !checkExists(tempX + 1, tempY + 1, tempX + 1, tempY)
                        ) {
                    System.out.println("NOT ADDED");
                    return false;
                } else {
                    revert = !(removeEdges(tempX, tempY, tempX - 1, tempY) &// removeEdges(tempX-1, tempY, tempX, tempY) &
                            removeEdges(tempX, tempY, tempX + 1, tempY) & //removeEdges(tempX+1, tempY, tempX, tempY) &
                            removeEdges(tempX, tempY, tempX, tempY + 1) & //removeEdges(tempX, tempY+1, tempX, tempY) &
                            removeEdges(tempX, tempY, tempX, tempY - 1) & //removeEdges(tempX, tempY-1, tempX, tempY) &

                            //removeEdges(tempX+1, tempY, tempX, tempY) & removeEdges(tempX, tempY, tempX+1, tempY) &
                            removeEdges(tempX + 1, tempY, tempX + 2, tempY) & //removeEdges(tempX+2, tempY, tempX+1, tempY) &
                            removeEdges(tempX + 1, tempY, tempX + 1, tempY + 1) &// removeEdges(tempX+1, tempY+1, tempX+1, tempY) &
                            removeEdges(tempX + 1, tempY, tempX + 1, tempY - 1) & //removeEdges(tempX+1, tempY-1, tempX+1, tempY) &

                            removeEdges(tempX, tempY + 1, tempX + 1, tempY + 1) & //removeEdges(tempX+1, tempY+1, tempX, tempY+1) &
                            removeEdges(tempX, tempY + 1, tempX - 1, tempY + 1) &// removeEdges(tempX-1, tempY+1, tempX, tempY+1) &
                            removeEdges(tempX, tempY + 1, tempX, tempY + 2) &// removeEdges(tempX, tempY+2, tempX, tempY+1) &
                            //removeEdges(tempX, tempY+1, tempX, tempY) & removeEdges(tempX, tempY, tempX, tempY+1) &

                            removeEdges(tempX + 1, tempY + 1, tempX + 2, tempY + 1) & //removeEdges(tempX+2, tempY+1, tempX+1, tempY+1) &
                            //removeEdges(tempX+1, tempY+1, tempX, tempY+1) & removeEdges(tempX, tempY+1, tempX+1, tempY+1) &
                            removeEdges(tempX + 1, tempY + 1, tempX + 1, tempY + 2) //& removeEdges(tempX+1, tempY+2, tempX+1, tempY+1) //&
                            //removeEdges(tempX+1, tempY+1, tempX+1, tempY) & removeEdges(tempX+1, tempY, tempX+1, tempY+1)
                    );

                    if (revert) {
                        System.out.println("SOMETHING WENT WRONG; delete this object and start again. Building's cleared, graph now corrupt");
                        return false;
                    } else {
                        buildings.add(new Buildings(new Point(tempX, tempY), 4));
                        System.out.println("ADDED");
                        return true;
                    }
                }
        }
        return false;
    }

    /**
     * Creates a random graph. Will not regen graph: I.E. if you manually remove edges, then this data will be kept after calling this function.
     * Will not place buildings on top of each other
     * Will revert to original graph (will notify via System.out.println("") AND return a negative number) if cannot place all buildings
     * If an infinite loop occurs it will go to maxRevert (1000 loops), then revert the graph back to the beginning.
     *
     * @param arrBuildings an array of numbers 1..4. The length determines how many buildings to be placed. The number determines the size of building placed.
     * //TODO DONT USE     1 (small building) means randomly remove 1 edge (this is NOT SAFE HOWEVER - there are no checks)
     *                     2 (medium building) means randomly remove 1 edge and one edge vertically below it (if random no. generated means that will attempt to remove edge that doesn't exist, then will keep regenerating)
     *                     3 (medium building) means randomly remove 1 edge and one edge horizontally to the right of it (if random no. generated means that will remove edge that doesn't exist, then will keep regenerating)
     *                     4 (large building) means randomly remove 1 edge and one edge horizontally to the right of it and the same below it (IE REMOVE 4 coordinates in a square like formation) (if random no. generated means that will remove edge that doesn't exist, then will keep regenerating)
     *                     SOUT OUTPUT Explained:
     *                     "REMOVING x edge" -> size of building currently trying to place
     *                     "Randomly removing x;y" -> removing edge from / to / around x and y coordinates
     *                     "FAILED with (x,y)..." -> building already in place that random numbers are...
     *                     "Revertion anyway, skipping" -> failed to place a building, should not happen as checks should occur and should keep generating random x and y until it find a place to place the building
     *                     "Can't place all buildings..." -> too many buildings to place on small map without overlapping, will revert.
     * @return 1 = successfully randomised
     * 0  = invalid input
     * -1 = failed due to too many buildings being attempted to be placed onto grid
     */
    public int randomTheGraph(int[] arrBuildings) {

        if (arrBuildings.length == 0) return 0;

        int counterRevert = 0;
        int maxRevert = 1000;

        Random rnd = new Random();

        for (int i = 0; i < arrBuildings.length; i++) {
            int tempX = rnd.nextInt(xMax);
            int tempY = rnd.nextInt(yMax);
            int sizeB = arrBuildings[i];

            counterRevert = 0;

            if (!addBuilding(tempX, tempY, sizeB))
                while (addBuilding(tempX, tempY, sizeB) == false) {
                    System.out.println("Failed to place with (" + tempX + "," + tempY + "): Rechoosing another random number");
                    counterRevert++;
                    tempX = rnd.nextInt(xMax);
                    tempY = rnd.nextInt(yMax);
                    if (counterRevert > maxRevert) {
                        System.err.println("Can't place all buildings - they all won't fit on the graph x:" + tempX + "; y:" + tempY + "; size: " + sizeB + "|" + counterRevert + "|" + maxRevert);
                        break;
                    }
                }
        }

        System.out.println("Success");
        return 1;
    }

    /**
     * Add a cost to a node (IE four edges around one node).
     * 10 = default, 20 = double default etc.
     * @param x x coordinate to add cost to
     * @param y y coordinate to add cost to
     * @param cost cost of node (0.. = default
     */
    public void addCostNode(int x, int y, int cost) {
        nodes.get(new Point(x, y)).setExtraCost(cost);
    }

    /**
     * Remove a cost from x, y. If not costs exist nothing will change. No feedback - void function.
     * @param x self explanatory
     * @param y self explanatory
     */
    public void removeCostNode(int x, int y) {
        nodes.get(new Point(x, y)).setExtraCost(0);
    }

    /**
     * Get's cost from x, y. 0 / 10 is default.
     * @param x self explanatory
     * @param y self explanatory
     */
    public int getNodeCost(int x, int y) {
        if (x > xMax || y > yMax) {
            return -1;
        } else {
            return nodes.get(new Point(x, y)).getExtraCost();
        }
    }

    /**
     * Returns integer of how many nodes have a cost >0
     * @return int of how many nodes have a cost >0
     */
    public int getNoOfNodesWithCosts() {
        int counter = 0;
        for (Map.Entry<A, Node<A>> entry : nodes.entrySet()) {
            if (entry.getValue().getExtraCost() > 0) {
                counter = counter + 1;
            }
        }
        return counter;
    }

    /**
     * Saves the object as a serialisation, containing all information in this object.
     * After this is done, any changes to the original object are not reflected on saved file
     * Call this method again if you make changes to object and want to save them to file
     *
     * @param filename  file name (must NOT end in 'txt' / 'ser'). eg "text1.txt" would NOT be valid
     */
    public void saveToFile(String filename) {
        Serializer ser = new Serializer();
        ser.serializeAddress(filename + ".ser", this);
    }

    /**
     * Load data from file.
     * WILL DESTROY ALL CURRENT DATA IN OBJECT
     * @param fileName file name (must NOT end in 'txt' / 'ser'). eg "text1.txt" would NOT be valid
     */
    public void loadFromFile(String fileName) {
        Serializer ser = new Serializer();
        WorldGraph temp = ser.deserialzeAddress(fileName + ".ser");

        nodes = temp.getNodes();

        xMax = temp.getMaxXValue();
        yMax = temp.getMaxYValue();

        buildings = temp.getBuildings();
    }

    public void render(Camera camera, SearchTicker searchTicker, float counter) {
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Render lines
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);

        for (Map.Entry<A, Node<A>> entry : nodes.entrySet()) {
            Set<Node<A>> a = entry.getValue().getSuccessors();
            Object[] aObject = a.toArray();

            int a_xValue = ((Point) entry.getValue().getContent()).getX();
            int a_yValue = ((Point) entry.getValue().getContent()).getY();


            for (int i = 0; i < aObject.length; i++) {
                Node<Point> aNodePoint = (Node<Point>) aObject[i];

                //one above
                if (aNodePoint.getContent().equals(new Point(a_xValue, a_yValue-1))) {
                    if (aNodePoint.getExtraCost() > 10 || entry.getValue().getExtraCost() > 10) {
                        int percentageColour = (int) (((float) Math.max(getNodeCost(a_xValue, a_yValue), getNodeCost(a_xValue, a_yValue-1)) + 10) * 2.55);
                        shapeRenderer.setColor(new Color((float) (percentageColour / 100.0), 0, 0, 0));
                    }
                    else
                        shapeRenderer.setColor(Color.BLACK);
                    shapeRenderer.line(a_xValue, a_yValue, a_xValue, a_yValue-1);
                }

                //one below
                if (aNodePoint.getContent().equals(new Point(a_xValue, a_yValue+1))) {
                    if (aNodePoint.getExtraCost() > 10 || entry.getValue().getExtraCost() > 10) {
                        int percentageColour = (int) (((float) Math.max(getNodeCost(a_xValue, a_yValue), getNodeCost(a_xValue, a_yValue+1)) + 10) * 2.55);
                        shapeRenderer.setColor(new Color((float) (percentageColour / 100.0), 0, 0, 0));
                    }  else
                        shapeRenderer.setColor(Color.BLACK);
                    shapeRenderer.line(a_xValue, a_yValue, a_xValue, a_yValue+1);
                }

                //one left
                if (aNodePoint.getContent().equals(new Point(a_xValue+1, a_yValue))) {
                    if (aNodePoint.getExtraCost() > 10 || entry.getValue().getExtraCost() > 10) {
                        int percentageColour = (int) (((float) Math.max(getNodeCost(a_xValue, a_yValue), getNodeCost(a_xValue+1, a_yValue)) + 10) * 2.55);
                        shapeRenderer.setColor(new Color((float) (percentageColour / 100.0), 0, 0, 0));
                    } else
                        shapeRenderer.setColor(Color.BLACK);
                    shapeRenderer.line(a_xValue, a_yValue, a_xValue+1, a_yValue);
                }

                //one right
                if (aNodePoint.getContent().equals(new Point(a_xValue-1, a_yValue))) {
                    if (aNodePoint.getExtraCost() > 10 || entry.getValue().getExtraCost() > 10) {
                        int percentageColour = (int) (((float) Math.max(getNodeCost(a_xValue, a_yValue), getNodeCost(a_xValue-1, a_yValue)) + 10) * 2.55);
                        shapeRenderer.setColor(new Color((float) (percentageColour / 100.0), 0, 0, 0));
                    } else
                        shapeRenderer.setColor(Color.BLACK);
                    shapeRenderer.line(a_xValue, a_yValue, a_xValue-1, a_yValue);
                }
            }
        }

        shapeRenderer.end();

        // Render nodes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);

        for (Map.Entry<A, Node<A>> entry : nodes.entrySet()) {
            Point p = (Point) entry.getValue().getContent();
            shapeRenderer.circle(p.getX(), p.getY(), 0.15f * counter, 20);
        }

        // Search ticker
        if (!searchTicker.isPathComplete()) {
            searchTicker.tick();

            if (searchTicker.isRenderProgress()) {
                Set<Node<Point>> visited = searchTicker.getVisited();
                Collection<Node<Point>> frontier = searchTicker.getFrontier();
                // Last frontier

                // Draw visited nodes
                shapeRenderer.setColor(Color.LIGHT_GRAY);
                for (Node<Point> visitedNode : visited) {
                    shapeRenderer.circle(
                            visitedNode.getContent().getX(),
                            visitedNode.getContent().getY(),
                            0.15f * counter,
                            20
                    );
                }

                // last frame's frontier
                /*for (Node<Point> frontierNode : lastFrontier) {
                    actorLookup.get(frontierNode.getContent()).setNodeColour(Color.FOREST);
                }*/

                // Draw frontier
                shapeRenderer.setColor(Color.LIME);
                for (Node<Point> frontierNode : frontier) {
                    shapeRenderer.circle(
                            frontierNode.getContent().getX(),
                            frontierNode.getContent().getY(),
                            0.15f * counter,
                            20
                    );
                }
            }

            if (searchTicker.isPathComplete()) {
                // Should send a message to World! (for now here)

                List<Node<Point>> path = searchTicker.getPath();

                System.out.println(path);

                List<Vector2> points = path
                        .stream()
                        .map(pointNode -> new Vector2(pointNode.getContent().getX(), pointNode.getContent().getY()))
                        .collect(Collectors.toList());

                world.spawnAgentWithPath(points.get(0), points);
            }
        }

        // start and end are always red
        /*if (start != null)
            actorLookup.get(start.getContent()).setNodeColour(Color.RED);
        if (end != null)
            actorLookup.get(end.getContent()).setNodeColour(Color.RED);*/

        shapeRenderer.end();
    }

    @Override
    public String toString() {
        String str = "";

        for (Map.Entry<A, Node<A>> entry : nodes.entrySet()) {
            str += entry.getKey() + " -- " + entry.getValue().getSuccessors() + "\n";
        }

        return str;
    }

    /**
     * @return ArrayList of buildings on the map, a building is a Point and a size.
     */
    public ArrayList<Buildings> getBuildings() {
        return buildings;
    }

    /**
     * @return all the nodes on the graph as a linkedHashSet / Map<Point, Node<Point>>
     */
    public Map<A, Node<A>> getNodes() {
        return nodes;
    }

    /**
     * @param content The key
     * @return The node associated with the given key
     */
    public Node<A> getNode(A content) {
        return nodes.get(content);
    }

    /**
     * @return size of graph in x dimension. -1 if not loaded / cleared object without new generation.
     */
    public int getMaxXValue() {
        return xMax;
    }

    /**
     * @return size of graph in y dimension. -1 if not loaded / cleared object without new generation.
     */
    public int getMaxYValue() {
        return yMax;
    }

    /**
     * Clears object and deletes all data stored in this object.
     * Does not delete saved data.
     */
    public void clearWorld() {
        xMax = -1;
        yMax = -1;
        nodes.clear();
        buildings.clear();
    }

    /**
     * Initialise a frontier according to a specified search algorithm
     * @param alg The search algorithm
     * @return An empty collection
     */
    private Takeable<Node<A>> initFrontier(SearchAlgorithm alg) {
        Takeable<Node<A>> frontier;

        switch (alg) {
            case A_STAR:
                frontier = new PriorityQueueT<>();
                break;

            case DEPTH_FIRST:
                frontier = new StackT<>();
                break;

            case BREADTH_FIRST:
            default:
                frontier = new LinkedListT<>();
                break;
        }

        return frontier;
    }

    /**
     * Constructs a path from a tracking map, a start and a goal node
     * @param map The tracking map (child - parent entries)
     * @param start Start node
     * @param end Goal node
     * @return List of nodes that defines the path
     */
    private ArrayList<Node<A>> constructPath(Map<Node<A>, Node<A>> map, Node<A> start, Node<A> end) {
        // Helper stack for inserting elements
        Stack<Node<A>> path = new Stack<>();
        // Array list that will contain the ordered path to return
        ArrayList<Node<A>> retPath = new ArrayList<>();
        // Add end node to the stack
        path.add(end);

        // Insert nodes into stack
        Node<A> n;
        while (!(n = path.peek()).equals(start)) {
            path.add(map.get(n));
        }

        // Pop elements to get correct order (from start to end)
        while (!path.isEmpty()) {
            retPath.add(path.pop());
        }

        return retPath;
    }

    /**
     * Find a path from the origin to the destination using AStar with Euclidian distance
     * @param origin The content of the starting node
     * @param destination The content of the destination node
     * @return Optional a path
     */
    public Optional<List<Node<A>>> findPathFromASTAR(A origin, A destination) {

        SearchParameters<A> params = (SearchParameters<A>) new SearchParameters<>().AStarParams();

        // Get origin node from nodes map and create destination node with the specified content
        Node<A> startNode = nodes.get(origin);
        Node<A> destinationNode = new Node<>(destination);

        if (startNode == null)
            return Optional.empty();

        // Unpack parameters: search algorithm, heuristic and distance functions
        Function2<Node<A>, Node<A>, Float> h = params.getHeuristic();
        Function2<Node<A>, Node<A>, Float> d = params.getDistanceFn(); // Edge weighting
        SearchAlgorithm alg = params.getAlgorithm();

        // Declare explored, pending, predecessors and cost tracking collections (cost from the origin along best known path)
        Set<Node<A>> explored = new HashSet<>();
        Takeable<Node<A>> pending = initFrontier(alg);
        Map<Node<A>, Node<A>> pred = new LinkedHashMap<>();
        Map<Node<A>, Float> D = new LinkedHashMap<>();

        // Initialise collections
        pending.add(startNode);
        D.put(startNode, 0f);
        startNode.setF(h.apply(startNode, destinationNode));

        // Start search
        while (!pending.isEmpty()) {
            // Get node according to the data structure
            Node<A> n = pending.take();

            // If it is the destination node then
            if (n.equals(destinationNode))
                // construct path from the map tracking and the start node
                return Optional.of(constructPath(pred, startNode, n));

            // Add n to the explored set
            explored.add(n);


            // Loop through all successors of n that haven't been visited yet
            for (Node<A> s : n.getSuccessors()) {
                // If successor has not been visited yet
                if (!explored.contains(s)) {
                    float cost = D.get(n) + d.apply(n, s);
                    boolean inPending = pending.contains(s); // Without computing it 2 times

                    // If s is not in the frontier or found a better path
                    if (!inPending || cost < D.get(s)) {
                        // Update predecessor, actual and estimated cost
                        pred.put(s, n);
                        D.put(s, cost);
                        if (s.getExtraCost() > 0) System.out.println("working");
                        s.setF(D.get(s) + h.apply(s, destinationNode) - s.getExtraCost());

                        if (!inPending)
                            pending.add(s);
                    }
                }
            }
        }

        // No path found
        return Optional.empty();
    }

    /**
     * Find a path from the origin to the destination using BFS
     * @param origin The content of the starting node
     * @param destination The content of the destination node
     * @return Optional a path
     */
    public Optional<List<Node<A>>> findPathBFS (A origin, A destination) {
        Node<A> startNode = nodes.get(origin);
        Node<A> destinationNode = new Node<>(destination);

        if (startNode == null)
            return Optional.empty();

        Set<Node<A>> explored = new HashSet<>();
        Takeable<Node<A>> pending = new LinkedListT<>();
        Map<Node<A>, Node<A>> pred = new LinkedHashMap<>();

        pending.add(startNode);

        while (!pending.isEmpty()) {
            Node<A> n = pending.take();

            if (n.equals(destinationNode))
                return Optional.of(constructPath(pred, startNode, n));

            explored.add(n);

            Object[] arr = n.getSuccessors().toArray();
            arr = findLowestCost(arr);
            for (int i=0; i<arr.length; i++) {
                Node<A> s = (Node<A>) arr[i];
                if (!explored.contains(s)) {
                    boolean inPending = pending.contains(s);

                    if (!inPending) {
                        pred.put(s, n);
                        pending.add(s);
                    }
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Find a path from the origin to the destination using DFS with costs
     * @param origin The content of the starting node
     * @param destination The content of the destination node
     * @return Optional a path
     */
    public Optional<List<Node<A>>> findPathDFSwithCosts (A origin, A destination) {
        Node<A> startNode = nodes.get(origin);
        Node<A> destinationNode = new Node<>(destination);

        if (startNode == null)
            return Optional.empty();

        Set<Node<A>> explored = new HashSet<>();
        Takeable<Node<A>> pending = new StackT<>();
        Map<Node<A>, Node<A>> pred = new LinkedHashMap<>();

        pending.add(startNode);

        while (!pending.isEmpty()) {
            Node<A> n = pending.take();

            if (n.equals(destinationNode))
                return Optional.of(constructPath(pred, startNode, n));

            explored.add(n);

            Object[] arr = n.getSuccessors().toArray();
            arr = findLowestCost(arr);
            for (int i=0; i<arr.length; i++) {
                Node<A> s = (Node<A>) arr[i];
                if (!explored.contains(s)) {
                    boolean inPending = pending.contains(s);

                    if (!inPending) {
                        pred.put(s, n);
                        pending.add(s);
                    }
                }
            }
        }

        return Optional.empty();
    }

    /**
     * finds the lowest cost in the list of nodes given
     * @param arrTemp array to parse and sort
     * @return sorted version of arrtemp using extraCosts as comparsion
     */
    private Object[] findLowestCost(Object[] arrTemp) {
        int n = arrTemp.length;

        boolean swapped = true;

        while (swapped) {
            swapped = false;
            for (int i = 1; i < n; i++) {
                Node<A> a = (Node<A>) arrTemp[i];
                Node<A> b = (Node<A>) arrTemp[i - 1];
                if (b.getExtraCost() < a.getExtraCost()) {
                    arrTemp[i] = b;
                    arrTemp[i-1] = a;
                    swapped = true;
                }
            }
        }

        return arrTemp;
    }
}
