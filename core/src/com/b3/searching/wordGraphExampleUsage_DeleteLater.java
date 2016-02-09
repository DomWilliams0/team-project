package com.b3.searching;

import com.b3.searching.roboticsGraphHelpers.Graph;
import com.b3.searching.roboticsGraphHelpers.Point;
import com.b3.searching.utils.Buildings;

import java.util.ArrayList;

/**
 * Created by Nishanth on 07/02/2016.
 */
public class wordGraphExampleUsage_DeleteLater {

    public static void main (String args[]) {
        //create an empty worldGraph
        worldGraph wg = new worldGraph();
        //generate a 9 x 10 graph -- must do this first before anything OR load file NOTHING ELSE
        wg.generateGraph(100,100);
        //add some buildings at (x, y, size (2,3,4))
        if (wg.addBuilding(5, 5, 2)) System.out.println("added"); else System.out.println("not");
        if (wg.addBuilding(7, 7, 3)) System.out.println("added"); else System.out.println("not");
        if (wg.addBuilding(9, 9, 4)) System.out.println("added"); else System.out.println("not");
        //generate random graph with 3 2-sized buildings, 4 3-sized buildings and 2 4-sized buildings
        int[] arr = {2,2,2,3,3,3,3,4,4};
//        wg.randomTheGraph(arr);
        //add costs to nodes - all other nodes default to 0
        wg.addCostNode(2,1,10);
        wg.addCostNode(2,2,10);
        wg.addCostNode(3,2,10);
        //save it to the file
        wg.saveToFile("default");

        //to load file must clear it beforehand, otherwise will get error from loadfromfile
        wg.clearObject();
        //load from file
        wg.loadFromFile("default");
        //can edit now from here if you want to -- remember to save though
        //get maxX and maxY values
        wg.getMaxXValue();
        wg.getMaxYValue();
        //get buildings - note this is for visualisation ONLY
        ArrayList<Buildings> a = wg.getBuildings();
        //get costs -- note this is for visualisation ONLY
        //the costs are already contained within the Graph<Point> IE Resulting object from getGraphNicksStyle();
        int onetwo = wg.getNodeCost(2,3);
        //get graph
        Graph<Point> b = wg.getGraphNicksStyle();
        //DO NOT edit graph in Graph<Point> format.
        //Either go back to the original worldGraph object to edit, then call getGraphNicksStyle again.

        //ONLY CALL THESE FUNCTIONS FROM Graph CLASS - NO OTHERS
        System.out.println(b.findPathDFSwithCosts(new Point(1,1), new Point(25,25))); //returns nothing if building on top
        System.out.println(b.findPathBFS(new Point(1,1), new Point(25,25))); //returns nothing if building on top
        System.out.println(b.findPathFromASTAR(new Point(1,1), new Point(25,25)));//returns nothing if building on top

    }
}
