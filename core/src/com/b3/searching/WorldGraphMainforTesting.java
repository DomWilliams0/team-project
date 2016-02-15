package com.b3.searching;

/**
 * Created by Nishanth on 12/02/2016.
 */
public class WorldGraphMainforTesting {

    public static void main (String args[]) {
        WorldGraph wg = new WorldGraph(10,10);

        System.out.println(wg.addBuilding(5, 5, 2));
        System.out.println(wg.addBuilding(3, 3, 2));
        System.out.println(wg.addBuilding(1, 1, 2));

//        int[] arr = {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2};
//        wg.randomTheGraph(arr);

        wg.addCostNode(1,1,10);
        wg.addCostNode(2,2,10);
        wg.addCostNode(3,3,10);
        wg.addCostNode(4,4,10);

        wg.saveToFile("Hello");

        System.out.println(wg.toString());

        System.out.println(wg.getBuildings());

        System.out.println(wg.getNoOfNodesWithCosts());
        System.out.println(wg.getNodeCost(1,1));
        System.out.println(wg.getNodeCost(2,2));
        System.out.println(wg.getNodeCost(3,3));
        System.out.println(wg.getNodeCost(4,4));
        System.out.println(wg.getNodeCost(5,5));

        System.out.println(wg.getMaxYValue());
        System.out.println(wg.getMaxXValue());

        System.out.println(wg.findPathDFSwithCosts(new Point(9,9), new Point(9,0)));

        WorldGraph wg2 = new WorldGraph("Hello");

        System.out.println(wg2.toString().equals(wg.toString()));

        System.out.println(wg2.getBuildings().equals(wg.getBuildings())); //IS TRUE

        System.out.println(wg2.getNoOfNodesWithCosts() == wg.getNoOfNodesWithCosts());
        System.out.println(wg2.getNodeCost(1,1) == wg.getNodeCost(1,1));
        System.out.println(wg2.getNodeCost(2,2) == wg.getNodeCost(2,2));
        System.out.println(wg2.getNodeCost(3,3) == wg.getNodeCost(3,3));
        System.out.println(wg2.getNodeCost(4,4) == wg.getNodeCost(4,4));
        System.out.println(wg2.getNodeCost(5,5) == wg.getNodeCost(5,5));

        System.out.println(wg2.getMaxYValue() == wg.getMaxYValue());
        System.out.println(wg2.getMaxXValue() == wg.getMaxXValue());

        System.out.println(wg2.findPathDFSwithCosts(new Point(9,9), new Point(9,0)));
        System.out.println(wg.findPathDFSwithCosts(new Point(9,9), new Point(9,0)));

    }
}
