package com.b3.search.util;

import com.b3.search.Point;

/**
 * Links a point, or another object to a timer, to allow for animation
 *  @author nbg481
 */
public class PointTimer {

	private int x;
	private int y;
	private Object ob;
	private int timer;

	/**
	 * Creates a new PointTimer, with a x and y coorindate and time to be open
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param timer the time to count down from
     */
	public PointTimer(int x, int y, int timer) {
		this.x = x;
		this.y = y;
		this.timer = timer;
	}

	public PointTimer(Object ob, int timer) {
		this.ob = ob;
		this.timer = timer;
	}

	public Object getOb() {
		return ob;
	}

	public void setOb(Object ob) {
		this.ob = ob;
	}

	/**
	 * @return the point this {@link PointTimer} is holding
     */
	public Point getPoint() {
		return new Point(x, y);
	}

	/**
	 * @return the time left in this animation
     */
	public int getTimer() {
		return timer;
	}

	/**
	 * Reduces the timer by one
	 */
	public void decrementTimer() {
		timer--;
	}

	/**
	 * Reduces the timer by {@param change}
	 * @param change the amount the timer should be decremented by
     */
	public void decrementTimer(int change) {
		timer = timer - change;
	}

	/**
	 * @return true if the timer has been reduced to 0
     */
	public Boolean finishedTiming() {
		return (timer <= 0);
	}

}