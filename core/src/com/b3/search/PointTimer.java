package com.b3.search;

/**
 * Links a point to a timer, to allow for animation
 *  @author nbg481
 */
public class PointTimer {

	private final int x;
	private final int y;
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
