package com.b3.searching.utils;

import java.io.Serializable;

/**
 * Created by Nishanth on 04/02/2016.
 */
public class Buildings implements Serializable{

    private Coordinates coord;
    private int buildingSize;

    public Buildings (Coordinates coord, int buildingSize) {
        this.coord = coord;
        this.buildingSize = buildingSize;
    }

    public int getXValueofCoord () {
        return coord.getX();
    }

    public int getYValueofCoord () {
        return coord.getY();
    }

    public int getBuildingSize () {
        return buildingSize;
    }


    @Override
    public String toString() {
        return coord.getOnlyCoord() + "-" + buildingSize;
    }
}
