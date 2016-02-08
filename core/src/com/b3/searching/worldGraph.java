package com.b3.searching;

import com.b3.searching.roboticsGraphHelpers.Graph;
import com.b3.searching.roboticsGraphHelpers.Point;
import com.b3.searching.utils.Buildings;
import com.b3.searching.utils.Coordinates;
import com.b3.searching.utils.ReadFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Nishanth on 07/02/2016.
 */
public class worldGraph implements Serializable {

    private ArrayList<Coordinates> tempGraph;
    private ArrayList<Buildings> tempBuildings;
    private ArrayList<Coordinates> coords;
    private ArrayList<Integer> costs;

    private int xMax;
    private int yMax;
    private ArrayList<Coordinates> graph;


    /**
     * Constructor
     */
    public worldGraph () {
        this.tempGraph = new ArrayList<Coordinates>();
        this.tempBuildings = new ArrayList<Buildings>();
        this.coords = new ArrayList<Coordinates>();
        this.costs = new ArrayList<Integer>();
    }

    /**
     * Generates a graph with x and y dimentions as specified in parameters
     * does not add any blocks or buildings, will add every single possible successor to each node
     * an integrity check is computed at the end to check if every node has at least one successor, does not mean graph is accurate or correct - visual inspection recommended.
     *
     * @param xMax max x value (coordinate's x value will go up to xMax-1) IE if you pass 5, it will go from 0 to 4
     * @param yMax max y value (coordinate's y valuewill go up to yMax-1) IE if you pass 5, it will go from 0 to 4
     * @return true iff integrity check passes. If not, then there are some nodes with no successors (likely a bug)
     */
    public Boolean generateGraph(int xMax, int yMax) {
        this.xMax = xMax;
        this.yMax = yMax;
        for (int x = 0; x < xMax; x++) {
            for (int y = 0; y < yMax; y++) {
                Coordinates tempCoord = new Coordinates(x, y);
                //if in middle portion of graph (IE not edge)
                if (x != 0 & x != (xMax - 1) & y != 0 & y != (yMax - 1)) {
                    tempCoord.addSuccessor(x + 1, y);
                    tempCoord.addSuccessor(x - 1, y);
                    tempCoord.addSuccessor(x, y + 1);
                    tempCoord.addSuccessor(x, y - 1);
                } else
                    //if on left hand side
                    if (x == 0 & y != (yMax - 1) & y != 0) {
                        tempCoord.addSuccessor(x + 1, y);
                        tempCoord.addSuccessor(x, y + 1);
                        tempCoord.addSuccessor(x, y - 1);
                    } else
                        //if on right hand side
                        if (x == (xMax - 1) & y != (yMax - 1) & y != 0) {
                            tempCoord.addSuccessor(x - 1, y);
                            tempCoord.addSuccessor(x, y + 1);
                            tempCoord.addSuccessor(x, y - 1);
                        } else
                            //if on top
                            if (y == 0 & x != (xMax - 1) & x != 0) {
                                tempCoord.addSuccessor(x + 1, y);
                                tempCoord.addSuccessor(x - 1, y);
                                tempCoord.addSuccessor(x, y + 1);
                            } else
                                //if on bottom
                                if (y == (yMax - 1) & x != 0 & x != (xMax - 1)) {
                                    tempCoord.addSuccessor(x - 1, y);
                                    tempCoord.addSuccessor(x + 1, y);
                                    tempCoord.addSuccessor(x, y - 1);
                                } else
                                    //deal with corner cases
                                    //top left
                                    if (x == 0 & y == 0) {
                                        tempCoord.addSuccessor(x + 1, y);
                                        tempCoord.addSuccessor(x, y + 1);
                                    } else
                                        //bottom left
                                        if (x == 0 & y == (yMax - 1)) {
                                            tempCoord.addSuccessor(x + 1, y);
                                            tempCoord.addSuccessor(x, y - 1);
                                        } else
                                            //top right
                                            if (x == (xMax - 1) & y == 0) {
                                                tempCoord.addSuccessor(x - 1, y);
                                                tempCoord.addSuccessor(x, y + 1);
                                            } else
                                                //bottom right
                                                if (x == (xMax - 1) & y == (yMax - 1)) {
                                                    tempCoord.addSuccessor(x - 1, y);
                                                    tempCoord.addSuccessor(x, y - 1);
                                                }

                tempGraph.add(tempCoord);
            }
        }

        if (!checkIntegrity(tempGraph)) {
            System.err.println("Integrity check failed - programming error in generateGraph() in worldGraph");
            return false;
        } else return true;
    }

    /**
     * Confirms that every node has at least one successor.
     * Do not use this method after you have removed some edges.
     *
     * @param checker graph to check
     * @return true iff every node has at least one successor.
     */
    private boolean checkIntegrity(ArrayList<Coordinates> checker) {
        for (int i = 0; i < checker.size(); i++) {
            if (!checker.get(i).hasSuccessors()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Use new function: addBuilding
     * Removes one edge from (x1,y1) to (x2, y2)
     *
     * @param x1 x part of from coordinate
     * @param y1 y part of from coordinate
     * @param x2 x part of to coordinate
     * @param y2 y part of to coordinate
     * @return true iff removed; false if cannot be removed (either from coordinate does not exist OR to coordinate is not successof or from coordinate)
     */
    private Boolean removeEdges(int x1, int y1, int x2, int y2) {
        Coordinates start = new Coordinates(x1, y1);
        Coordinates end = new Coordinates(x2, y2);

        for (int i = 0; i < tempGraph.size(); i++) {
            if (tempGraph.get(i).getX() == x1 & tempGraph.get(i).getY() == y1) {
                if (tempGraph.get(i).hasSuccessors(x2, y2)) {
                    System.out.println("Removed");
                    tempGraph.get(i).removeSuccessor(x2, y2);
                    return true;
                } else {
                    System.out.println("NOT REMOVED");
                    return false;
                }
            }
        }
        return false;

    }

    /**
     * checks that an edge exists from (x1, y1) -> (x2, y2)
     *
     * @param x1 x cood of from coordinate
     * @param y1 y cood of from coordinate
     * @param x2 x cood of to coordinate
     * @param y2 y cood of tocoordinate
     * @return true iff edge exists; false if not
     */
    public Boolean checkExists(int x1, int y1, int x2, int y2) {
        for (int i = 0; i < tempGraph.size(); i++) {
            if (tempGraph.get(i).getX() == x1 & tempGraph.get(i).getY() == y1) {
                if (tempGraph.get(i).hasSuccessors(x2, y2)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Add a building to the graph
     * Should be safe - ie return false if placment of building will result in overlapping buildings.
     *
     * @param tempX the xcoordinate of top left of building
     * @param tempY the ycoordinate of top left of building
     * @param size  the size of the building (1 = NOT SAFE DONT USE; 2 = 1 edge removed + 1 below; 3 = 1 edge removed + 1 to right; 4 = edge @ xy, to right, below, and to right-below removed
     * @return true if successful
     */
    public Boolean addBuilding(int tempX, int tempY, int size) {

        Boolean revert;

        switch (size) {
            case 2:
                System.out.println("REMOVING 2 edges, vertical");
                if (tempY + 2 == yMax | tempY + 1 == yMax | tempY == yMax | !checkExists(tempX, tempY, tempX, tempY + 1) | !checkExists(tempX, tempY + 1, tempX, tempY + 2)) {
                    System.out.println("NOT ADDED");
                    return false;
                } else {
                    revert = !(removeEdges(tempX, tempY, tempX, tempY + 1) &
                            removeEdges(tempX, tempY + 1, tempX, tempY + 2) &
                            removeEdges(tempX, tempY + 1, tempX, tempY) &
                            removeEdges(tempX, tempY + 2, tempX, tempY + 1));

                    if (revert) System.out.println("SOMETHING WENT WRONG; delete this object and start again");

                    System.out.println("ADDED");
                    tempBuildings.add(new Buildings(new Coordinates(tempX, tempY), 2));
                    return true;
                }

            case 3:
                System.out.println("REMOVING 2 edges, horizonal");
                if (tempX + 2 == xMax | tempX + 1 == xMax | tempX == xMax | !checkExists(tempX, tempY, tempX + 1, tempY) | !checkExists(tempX + 1, tempY, tempX + 2, tempY)) {
                    System.out.println("NOT ADDED");
                    return false;
                } else {
                    revert = !(removeEdges(tempX, tempY, tempX + 1, tempY) &
                            removeEdges(tempX + 1, tempY, tempX + 2, tempY) &
                            removeEdges(tempX + 1, tempY, tempX, tempY) &
                            removeEdges(tempX + 2, tempY, tempX + 1, tempY));

                    if (revert) System.out.println("SOMETHING WENT WRONG; delete this object and start again");

                    System.out.println("ADDED");
                    tempBuildings.add(new Buildings(new Coordinates(tempX, tempY), 3));
                    return true;
                }


            case 4:
                System.out.println("REMOVING 4 edges");

                if (tempX + 2 == xMax | tempX + 1 == xMax | tempX == xMax | tempY + 2 == yMax | tempY + 1 == yMax | tempY == yMax
                        | !checkExists(tempX, tempY, tempX + 1, tempY) | !checkExists(tempX + 1, tempY, tempX + 2, tempY)
                        | !checkExists(tempX, tempY + 1, tempX + 1, tempY + 1) | !checkExists(tempX + 1, tempY + 1, tempX + 2, tempY + 1)) {
                    System.out.println("NOT ADDED");
                    return false;
                } else {
                    revert = !(removeEdges(tempX, tempY, tempX + 1, tempY) & removeEdges(tempX + 1, tempY, tempX, tempY) &
                            removeEdges(tempX + 1, tempY, tempX + 2, tempY) & removeEdges(tempX + 2, tempY, tempX + 1, tempY) &
                            removeEdges(tempX, tempY + 1, tempX + 1, tempY + 1) & removeEdges(tempX + 1, tempY + 1, tempX, tempY + 1) &
                            removeEdges(tempX + 1, tempY + 1, tempX + 2, tempY + 1) & removeEdges(tempX + 2, tempY + 1, tempX + 1, tempY + 1));

                    if (revert) System.out.println("SOMETHING WENT WRONG; delete this object and start again");

                    System.out.println("ADDED");
                    tempBuildings.add(new Buildings(new Coordinates(tempX, tempY), 4));
                    return true;
                }
        }
        return false;
    }

    /**
     * STEP 3-b
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
     *                     FIXED *NOTE* Checking for available nodes does not work correctly - sometimes it will pick impossible nodes
     *                     FIXED *NOTE* if this happens the error checker will pick this up and then revert the graph to the original graph.
     *                     FIXED *NOTE* (it should be caught in the while loop and another random number generated)
     *                     SOUT OUTPUT Explained:
     *                     "REMOVING x edge" -> size of building currently trying to place
     *                     "Randomly removing x;y" -> removing edge from / to / around x and y coordinates
     *                     "FAILED with (x,y)..." -> building already in place that random numbers are...
     *                     "Revertion anyway, skipping" -> failed to place a building, should not happen as checks should occur and should keep generating random x and y until it find a place to place the building
     *                     "Can't place all buildings..." -> too many buildings to place on small map without overlapping, will revert.
     * @return 1  = successfully randomised
     * //TODO  2  = successfully randomised but more than 75% of edges have been deleted.
     * 0  = invalid input
     * -1 = failed due to too many buildings being attempted to be placed onto grid
     * -2 = failed due to above bug in program, recommend retry a few times until works. If still doesn't reduce number of 'large' / 4 -sized buildings in input
     */
    public int randomTheGraph(int[] arrBuildings) {

        if (arrBuildings.length == 0) return 0;

        ArrayList<Coordinates> backupGraph = tempGraph;
        Boolean revert = false;
        int counterRevert = 0;
        int maxRevert = 1000;

        for (int i = 0; i < arrBuildings.length; i++) {
            int typeOfRemoval = arrBuildings[i];

            Random rdm = new Random();
            int tempX = rdm.nextInt(xMax);
            int tempY = rdm.nextInt(yMax);

            switch (typeOfRemoval) {
                case 1:
                    System.out.println("REMOVING 1 edge");
                    System.out.println("Randomly removing " + tempX + "; " + tempY);
                    if (!removeEdges(tempX, tempY, tempX + 1, tempY))
                        if (!removeEdges(tempX, tempY, tempX - 1, tempY))
                            if (!removeEdges(tempX, tempY, tempX, tempY + 1))
                                if (!removeEdges(tempX, tempY, tempX, tempY - 1))
                                    System.err.printf("FAILED TO ADD ONE");
                    break;
                case 2:
                    System.out.println("REMOVING 2 edges, vertical");
                    counterRevert = 0;
                    if (tempY + 2 == yMax | tempY + 1 == yMax | tempY == yMax | !checkExists(tempX, tempY, tempX, tempY + 1) | !checkExists(tempX, tempY + 1, tempX, tempY + 2)) {
                        while (tempY + 2 == yMax | tempY + 1 == yMax | tempY == yMax | !checkExists(tempX, tempY, tempX, tempY + 1) | !checkExists(tempX, tempY + 1, tempX, tempY + 2)) {
                            System.out.println("FAILED with (" + tempX + "," + tempY + "): Rechoosing another random number");
                            tempY = rdm.nextInt(yMax);
                            tempX = rdm.nextInt(xMax);
                            counterRevert++;
                            if (counterRevert > maxRevert) {
                                revert = true;
                                System.err.println("Can't place all buildings - they all won't fit on the graph");
                                break;
                            }
                        }
                    }

                    System.out.println("Randomly removing " + tempX + "; " + tempY + " by two");

                    revert = !(removeEdges(tempX, tempY, tempX, tempY + 1) &
                            removeEdges(tempX, tempY + 1, tempX, tempY + 2) &
                            removeEdges(tempX, tempY + 1, tempX, tempY) &
                            removeEdges(tempX, tempY + 2, tempX, tempY + 1));

                    if (revert) {
                        System.out.println("Revertion anyway, skipping");
                        break;
                    } else tempBuildings.add(new Buildings(new Coordinates(tempX, tempY), 2));

                    break;


                case 3:
                    System.out.println("REMOVING 2 edges, horizonal");
                    counterRevert = 0;
                    if (tempX + 2 == xMax | tempX + 1 == xMax | tempX == xMax | !checkExists(tempX, tempY, tempX + 1, tempY) | !checkExists(tempX + 1, tempY, tempX + 2, tempY)) {
                        while (tempX + 2 == xMax | tempX + 1 == xMax | tempX == xMax | !checkExists(tempX, tempY, tempX + 1, tempY) | !checkExists(tempX + 1, tempY, tempX + 2, tempY)) {
                            System.out.println("FAILED with (" + tempX + "," + tempY + "): Rechoosing another random number");
                            tempX = rdm.nextInt(xMax);
                            tempY = rdm.nextInt(yMax);
                            counterRevert++;
                            if (counterRevert > maxRevert) {
                                revert = true;
                                System.err.println("Can't place all buildings - they all won't fit on the graph");
                                break;
                            }
                        }
                    }

                    System.out.println("Randomly removing " + tempX + "; " + tempY + " by two to right");

                    revert = !(removeEdges(tempX, tempY, tempX + 1, tempY) &
                            removeEdges(tempX + 1, tempY, tempX + 2, tempY) &
                            removeEdges(tempX + 1, tempY, tempX, tempY) &
                            removeEdges(tempX + 2, tempY, tempX + 1, tempY));

                    if (revert) {
                        System.out.println("Revertion anyway, skipping");
                        break;
                    } else tempBuildings.add(new Buildings(new Coordinates(tempX, tempY), 3));

                    break;

                case 4:
                    System.out.println("REMOVING 4 edges");
                    counterRevert = 0;

                    while (tempX + 2 == xMax | tempX + 1 == xMax | tempX == xMax | tempY + 2 == yMax | tempY + 1 == yMax | tempY == yMax
                            | !checkExists(tempX, tempY, tempX + 1, tempY)
                            | !checkExists(tempX + 1, tempY, tempX + 2, tempY)
                            | !checkExists(tempX, tempY + 1, tempX + 1, tempY + 1)
                            | !checkExists(tempX + 1, tempY + 1, tempX + 2, tempY + 1)) {
                        System.out.println("FAILED with (" + tempX + "," + tempY + "): Rechoosing another random number");
                        tempX = rdm.nextInt(xMax);
                        tempY = rdm.nextInt(yMax);
                        counterRevert++;
                        if (counterRevert > maxRevert) {
                            revert = true;
                            System.err.println("Can't place all buildings - they all won't fit on the graph");
                            break;
                        }
                    }

                    System.out.println("Randomly removing " + tempX + "; " + tempY + " by four");
                    revert = !(removeEdges(tempX, tempY, tempX + 1, tempY)
                            & removeEdges(tempX + 1, tempY, tempX, tempY)
                            & removeEdges(tempX + 1, tempY, tempX + 2, tempY)
                            & removeEdges(tempX + 2, tempY, tempX + 1, tempY)
                            & removeEdges(tempX, tempY + 1, tempX + 1, tempY + 1)
                            & removeEdges(tempX + 1, tempY + 1, tempX, tempY + 1)
                            & removeEdges(tempX + 1, tempY + 1, tempX + 2, tempY + 1)
                            & removeEdges(tempX + 2, tempY + 1, tempX + 1, tempY + 1));

                    if (revert) {
                        System.out.println("Revertion anyway, skipping");
                        break;
                    } else tempBuildings.add(new Buildings(new Coordinates(tempX, tempY), 4));
                    break;
            }

            if (revert) {
                System.err.println("Revertion was enabled for some reason (see above). Reverting, cancelling and stopping algorithm loop.");
                break;
            }
        }
        if (revert) {
            System.err.println("FAILED TO ADD ALL HOUSES USING RANDOM FUNCTION");
            System.err.println("<<<<<<<<<<<<<<<<<<<<<<REVERTING>>>>>>>>>>>>>>>>>>>>>");
            tempGraph = backupGraph;
            if (counterRevert != 0) {
                return -1;
            } else {
                return -2;
            }
        } else {
            System.out.println("Successfully randomised");
            return 1;
        }
    }

    /**
     * Assign a cost to a node
     *
     * @param x self explanatory. no bounds checking
     * @param y self explanatory. no bounds checking
     * @param cost cost of node
     */
    public void addCostNode(int x, int y, int cost) {
        removeCostNode(x, y);
        Coordinates c = new Coordinates(x, y);
        coords.add(c);
        costs.add(cost);
    }

    /**
     * Remove a cost from x, y. If not costs exist nothing will change. No feedback - void function.
     * @param x self explanatory
     * @param y self explanatory
     */
    public void removeCostNode(int x, int y) {
        for (int i = 0; i < coords.size(); i++) {
            if (coords.get(i).getX() == x & coords.get(i).getY() == y) {
                coords.remove(i);
                costs.remove(i);
                break;
            }
        }
    }

    /**
     * Clears the object of all data.
     * Call before loading from file.
     */
    public void clearObject() {
        System.out.println(tempGraph);
        tempBuildings.clear();
        tempGraph.clear();
        costs.clear();
        coords.clear();
        xMax = -1;
        yMax = -1;
    }

    /**
     * DO NOT USE
     * converts the internal strucuture to a file that can be read by Nick's graph converter / loader in Graph.java (or something)
     *
     * @return
     */
    private String finaliseForGraphFromFile() {
        String temp = "";

        for (int i = 0; i < tempGraph.size(); i++) {
            temp = temp + tempGraph.get(i).getOnlyCoord() + ": " + tempGraph.get(i).getSuccessor() + '\n';
        }
        return temp;
    }

    private String finaliseForBuildingsFromFile() {
        String returnString = "";

        for (int i = 0; i < tempBuildings.size(); i++) {
            returnString = returnString + tempBuildings.get(i).toString() + '\n';
        }

        return returnString;
    }

    private String finaliseCostsForFile() {
        String returnString = "";

        for (int i = 0; i < costs.size(); i++) {
            returnString = returnString + coords.get(i).getOnlyCoord() + "-" + costs.get(i).toString() + '\n';
        }

        return returnString;
    }

    /**
     * Saves the object as a serialisation, containing all information in this object.
     * Converts the internal structure to a file that can be read by Nick's graph converter / loader in Graph.java (or something)
     * After this is done, it can be read and used by Graph.java
     * After this is done, any changes to the original object are not reflected on saved file
     * Call this method again if you make changes to object and want to save them to file
     *
     * @param filename  file name (must NOT end in 'txt'). eg "text1.txt" would NOT be valid
     */
    public void saveToFile (String filename) {
        Serializer ser = new Serializer();
        ser.serializeAddress(filename + ".ser", this);

    }

    /**
     * @param fileName
     */
    public void loadFromFile(String fileName) {
        Serializer ser = new Serializer();
        worldGraph temp = ser.deserialzeAddress(fileName + ".ser");

        tempBuildings = temp.getBuildings();
        tempGraph = temp.getGraph();
        costs = temp.getCosts();
        coords = temp.getCoords();
    }

    /**
     * get the cost of a node at an x and y position.
     * @param x
     * @param y
     * @return 0 if cannot find node cost.
     */
    public int getNodeCost(int x, int y) {
        for (int i = 0; i < coords.size(); i++) {
            if (coords.get(i).getX() == x & coords.get(i).getY() == y)
                return costs.get(i);
        }
        return 0;
    }

    /**
     * gets buildings on this graph
     * @return a list of buildings on this graph. A building is a coordinate and a size (2 = 1 edge removed + 1 below; 3 = 1 edge removed + 1 to right; 4 = edge @ xy, to right, below, and to right-below removed)
     */
    public ArrayList<Buildings> getBuildings() {
        return tempBuildings;
    }

    /**
     * get's max x value of graph
     * @return max x value of graph
     */
    public int getMaxXValue() {
        int maxX = 0;
        for (int i = 0; i < tempGraph.size(); i++) {
            if (tempGraph.get(i).getX() > maxX)
                maxX = tempGraph.get(i).getX();
        }
        return maxX+1;
    }

    /**
     * get's max y value of graph
     * @return max y value of graph
     */
    public int getMaxYValue() {
        int maxY = 0;
        for (int i = 0; i < tempGraph.size(); i++) {
            if (tempGraph.get(i).getY() > maxY)
                maxY = tempGraph.get(i).getY();
        }
        return maxY+1;
    }

    /**
     * USE THIS TO APPLY GRAPH TO Graph<Point> that nicksgraph requires.
     * @return Graph<Point> -- returns graph in format that nicks search requires
     */
    public Graph<Point> getGraphNicksStyle() {

        String lineString = finaliseForGraphFromFile();
        String[] lines = lineString.split("\\r?\\n");

        // Create graph
        Graph<Point> g = new Graph<>();

        //Loop through file lines
        for (String line : lines) {
            String[] parts = line.split(":");
            String nodeStr = parts[0];
            String neighboursStr = parts[1].substring(1); // Without initial space

            String[] coords = nodeStr.replaceAll("[()]", "").split(",");
            Point p = new Point(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));

            // Add neighbours
            if (!neighboursStr.isEmpty()) {
                String[] neighbours = neighboursStr.split(" ");

                for (String neighbour : neighbours) {
                    String[] coordsNeighbour = neighbour.replaceAll("[()]", "").split(",");
                    Point pn = new Point(Integer.parseInt(coordsNeighbour[0]), Integer.parseInt(coordsNeighbour[1]));

                    // Add edge between p and pn
                    int pCost = getNodeCost(p.getX(), p.getY());
                    int p2Cost = getNodeCost(pn.getX(), pn.getY());

                    g.addEdge(p, pn, true, getNodeCost(p.getX(), p.getY()), getNodeCost(pn.getX(), pn.getY()));
                }
            } else
                g.addNode(p, getNodeCost(p.getX(), p.getY()));

        }

        return g;
    }

    public ArrayList<Coordinates> getGraph() {
        return tempGraph;
    }

    public ArrayList<Integer> getCosts() {
        return costs;
    }

    public ArrayList<Coordinates> getCoords() {
        return coords;
    }
}
