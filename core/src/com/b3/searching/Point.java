package com.b3.searching;

import java.io.Serializable;

public class Point implements Serializable{
	private int x;
	private int y;
	
	/**
	 * Creates a new point
	 * @param _x The x coordinate
	 * @param _y The y coordinate
	 */
	public Point(int _x, int _y) {
		setX(_x);
		setY(_y);
	}

	/**
	 * Returns the x coordinate
	 * @return The x coordinate
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets the x coordinate
	 * @param x The x coordinate to set
	 */
	public void setX(int x) {
		this.x = x;
	}
	
	/**
	 * Returns the y coordinate
	 * @return The y coordinate
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets the y coordinate
	 * @param y The y coordinate to set
	 */
	public void setY(int y) {
		this.y = y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Point)) return false;
		Point other = (Point) obj;
		return x == other.x && y == other.y;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
