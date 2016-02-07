package com.b3.searching.utils;

import java.util.ArrayList;

/**
 * Created by Nishanth on 02/02/2016.
 */
public class Coordinates {

    private final int x;
    private final int y;
    private final ArrayList<Coordinates> arr;

    /**
     * Create a new coordinate. Will set up with an empty list of successors
     * @param x the x coordinate of this coordinate
     * @param y the y coordinate of this coordinate
     */
    public Coordinates (int x, int y) {
        this.x = x;
        this.y = y;
        this.arr = new ArrayList<Coordinates>();
    }

    /**
     * @return the x value of this particular coordinate
     */
    public int getX () {
        return x;
    }

    /**
     * @return the y value of this particular coordinte
     */
    public int getY () {
        return y;
    }

    /**
     * Add a successor to this coordinate
     * @param x the x value of the successor
     * @param y the y value of the successor
     */
    public void addSuccessor (int x, int y) {
        arr.add(new Coordinates(x, y));
    }

    /**
     * removes a successor from this coordinate.
     * if doesn't exist will not remove anything. No feedback on if it has been removed or not.
     * @param x the x coordinate of the successor to remove.
     * @param y the y coordinate of the successor to remove.
     */
    public void removeSuccessor (int x, int y) {
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i).getX() == x && arr.get(i).getY() == y) {
                arr.remove(i);
            }
        }
    }

    /**
     * @return true if this coordinate has any successors
     */
    public Boolean hasSuccessors () {
        return arr.size() != 0;
    }

    /**
     * @param x x value to check if this coordinate has a successor with this x value
     * @param y y value to check if this coordinate has a successor with this y value
     * @return true if has a successor with same x and y coordinate as passed
     */
    public Boolean hasSuccessors (int x, int y) {
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i).getX() == x & arr.get(i).getY() == y) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return this coordinate as a string, in this format: (x,y)
     */
    public String getOnlyCoord() {
        return "("+x+","+y+")";
    }

    /**
     * @return this coordinate's successors as a string in this format: (x,y) (x,y) (x,y). If no successors then no output ("").
     */
    public String getSuccessor() {
        String temp = "";
        for (int i = 0; i < arr.size(); i++) {
            temp = temp + arr.get(i).getOnlyCoord() + " ";
        }
        return temp;
    }

    /**
     * Depreciated toString method
     * @return string output for debugging purposes.
     */
    public String toString() {
        String temp = "";
        for (int i = 0; i < arr.size(); i++) {
            temp = temp + arr.get(i);
        }
        return " ("+x+","+y+") " + temp;
    }

}
