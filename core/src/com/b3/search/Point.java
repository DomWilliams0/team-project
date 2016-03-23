package com.b3.search;

import com.badlogic.gdx.math.Vector2;

/**
 * Represents a non-mutable point in 2 dimensions.
 *
 * @author dxw405 nbg481
 */
public class Point implements Comparable<Point> {

	public final int x;
	public final int y;

	/**
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 */
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Point point = (Point) o;

		return x == point.x && y == point.y;

	}

	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		return result;
	}

	@Override
	public String toString() {
		return "Point{" +
				"x=" + x +
				", y=" + y +
				'}';
	}

	/**
	 * @return The x coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return The y coordinate.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Generates a new {@link Vector2} with the coordinates of this Point.
	 *
	 * @return This point as a {@link Vector2}.
	 */
	public Vector2 toVector2() {
		return new Vector2(x, y);
	}

	@Override
	public int compareTo(Point point) {
		int xCompare = Integer.compare(x, point.x);
		if (xCompare != 0)
			return xCompare;

		return Integer.compare(y, point.y);
	}
}
