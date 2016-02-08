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
        wg.generateGraph(9,10);
        //add some buildings at (x, y, size (2,3,4))
        if (wg.addBuilding(5, 5, 2)) System.out.println("added"); else System.out.println("not");
        if (wg.addBuilding(6, 6, 3)) System.out.println("added"); else System.out.println("not");
        if (wg.addBuilding(7, 7, 4)) System.out.println("added"); else System.out.println("not");
        //generate random graph with 3 2-sized buildings, 4 3-sized buildings and 2 4-sized buildings
        int[] arr = {2,2,2,3,3,3,3,4,4};
        wg.randomTheGraph(arr);
        //add costs to nodes - all other nodes default to 0
        wg.addCostNode(1,1,1);
        wg.addCostNode(2,3,10);
        wg.addCostNode(5,5,3);
        //save it to the file
        wg.saveToTextFile("default","");

        //to load file must clear it beforehand
        wg.clearObject();
        //load from file
        wg.loadFromFile("default");
        //can edit now from here if you want to -- remember to save though
        //get maxX and maxY values
        System.out.println(wg.getMaxXValue());
        System.out.println(wg.getMaxYValue());
        //get buildings
        ArrayList<Buildings> a = wg.getBuildings();
        //get costs
        int onetwo = wg.getNodeCost(1, 2);
        //get graph
        Graph<Point> b = wg.getGraphNicksStyle();

    }
}
