package com.b3.searching;

import java.io.Serializable;

/**
 * Created by Nishanth on 04/02/2016.
 */
public class Buildings implements Serializable{

    private Point coord;
    private int buildingSize;

    /**
     * Construct a building, with a position and a size.
     * @param coord coordinate of top left of building
     * @param buildingSize the size of the building (1 = NOT SAFE DONT USE; 2 = 1 edge removed + 1 below; 3 = 1 edge removed + 1 to right; 4 = edge @ xy, to right, below, and to right-below removed
     */
    public Buildings (Point coord, int buildingSize) {
        this.coord = coord;
        this.buildingSize = buildingSize;
    }

    /**
     * @return x value of top left of this building
     */
    public int getXValueofCoord () {
        return coord.getX();
    }

    /**
     * @return y value of top left of this building
     */
    public int getYValueofCoord () {
        return coord.getY();
    }

    /**
     * @return size of this building.
     */
    public int getBuildingSize () {
        return buildingSize;
    }


    @Override
    public String toString() {
        return "(" + coord.getX() + "," + coord.getY() + ")" + "-" + buildingSize;
    }
}
